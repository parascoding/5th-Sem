#include<bits/stdc++.h>
#include <iostream>
#include <string>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <netdb.h>
#include <sys/uio.h>
#include <sys/time.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <fstream>
#include<thread>
using namespace std;
sockaddr_in servAddr;
char msg[1500];
int port, serverSd, bindStatus;

vector<string> splitString(string &s){
    stringstream ss(s);
    string temp;
    vector<string> v;
    while(getline(ss, temp, ' '))
        v.push_back(temp);
    return v;
}

void sendToClient(int Sd, string s){
    try{
        char tempMsg[s.length()+1];
        memset(&tempMsg, 0, sizeof(tempMsg));
        int ind = 0;
        for(int i=0;i<s.length();i++){
            tempMsg[ind]=s[i];
            if(tempMsg[ind]=='\0')
                ind--;
            ind++;
        }

        send(Sd, (char*)&tempMsg, strlen(tempMsg), 0);
    } catch(int x){

    }
}

string receiveFromClient(int Sd){
    try{
        char tempMsg[100];
        memset(&tempMsg, 0, sizeof(tempMsg));
        recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
        string s(tempMsg);    
        
        return s;
    } catch(int x){

    }
    return "";
}
string findRestaurantList(){
    fstream file;
    string s;
    file.open("Restaurants.txt");

    while(file){
        string temp;
        getline(file, temp);
        s+=temp;
        s+='\n';
    }
    return s;

}
string findNameFromRollNo(string rollNo){
    fstream file;
    string s;
    file.open("students.txt");
    
    while(file){
        string temp;
        getline(file, temp);
        if(temp.length() == 0)
            break;
        vector<string> vec = splitString(temp);
        if(vec[0]==rollNo){
            return vec[1];
        }
    }
    return "NO SUCH STUDENT EXISTS";
}

string findAllDetailsFromRollNo(string rollNo){
    fstream file;
    string s;
    file.open("students.txt");
    int max = 0;
    while(file){
        string temp;
        getline(file, temp);
        if(temp.length() == 0)
            break;
        vector<string> vec = splitString(temp);
        if(vec[0]==rollNo){
            s = "Roll No: " + rollNo;
            s += "\nName: " + vec[1];
            s += "\nMarks: " + vec[2];
            s += "\nAddress: " +vec[3];
            break;
        }
    }
    return s;
}
string findAddressUsingRollNO(string rollNo){
    fstream file;
    string s;
    file.open("students.txt");
    int min = 101;
    while(file){
        string temp;
        getline(file, temp);
        if(temp.length() == 0)
            break;
        vector<string> vec = splitString(temp);
        if(vec[0]==rollNo){
            s = vec[3];
            break;
        }
    }
    return s;
}
int countt = 0;
void fun(){
    try{
        std::ios::sync_with_stdio(false);
        // cout << "START: " <<(countt++) << endl <<flush;
        sockaddr_in newSockAddr;
        socklen_t newSockAddrSize = sizeof(newSockAddr);

        //accept, create a new socket descriptor to 
        //handle the new connection with client

        int Sd = accept(serverSd, (sockaddr *)&newSockAddr, &newSockAddrSize);
        if(Sd < 0){
            cerr << "Error accepting request from client!" << endl;
        }

        cout << "Connected with client!" << endl;
        
        while(1)
        {
            sleep(0.5);
            string s = "\n\n\t\t!!! Welcome !!!\t\t\n";
            s+="Enter your choice -\n";
            s+="1: Find name of student from roll no\n";
            s+="2: Find all details using roll no\n";
            s+="3: Address using roll no\n";
            
            sendToClient(Sd, s);
            sleep(0.5);
            string choice = receiveFromClient(Sd);
            sleep(0.5);
            string rollNo = receiveFromClient(Sd);
            if(choice=="1"){
                string res = "Name :" ;
                res += findNameFromRollNo(rollNo);
                
                sendToClient(Sd, res);
            } else if(choice=="2"){
                string res = "Details: ";
                res += findAllDetailsFromRollNo(rollNo);
                sendToClient(Sd, res);
            } else if(choice=="3"){
                string res = "Address: ";
                res += findAddressUsingRollNO(rollNo);
                sendToClient(Sd, res);
            } else{
                string res = "Wrong Choice\n Closing connetion";
                // close(Sd);
                break;
            }
            
        }
        
        close(Sd);
        
    } catch(int x){

    }
    // cout << "END: "<<(countt++) << endl <<flush;
}
int main(int argc, char *argv[])
{
    cin.tie(nullptr);
    // ios::sync_with_stdio(true);
    //for te server, we only need to specify a port number
    if(argc != 2)
    {
        cerr << "Usage: port" << endl;
        exit(0);
    }
    
    port = atoi(argv[1]);
     
    bzero((char*)&servAddr, sizeof(servAddr));
    servAddr.sin_family = AF_INET;
    servAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servAddr.sin_port = htons(port);
 
    //open stream oriented socket with internet address
    //also keep track of the socket descriptor
    serverSd = socket(AF_INET, SOCK_STREAM, 0);
    if(serverSd < 0){
        cerr << "Error establishing the server socket" << endl;
        exit(0);
    }
    
    bindStatus = bind(serverSd, (struct sockaddr*) &servAddr, 
        sizeof(servAddr));

    if(bindStatus < 0){
        cerr << "Error binding socket to local address" << endl;
        exit(0);
    }
    
    cout << "Waiting for a client to connect..." << endl;
    //listen for up to 5 requests at a time
    listen(serverSd, 100);
    
    // ----------------------------------------------------------------
    //                                    Multi 
    server:
        thread th1[100];
        int ind = 0;
        while(ind<100){
            th1[ind++]=thread(&fun);
        }
        ind =0;
        while(ind<100){
            th1[ind++].join();
        }
    goto server;

    // ----------------------------------------------------------------

    cout << "CLOSING" << flush;
    // close(serverSd);
   
    return 0;   
}
