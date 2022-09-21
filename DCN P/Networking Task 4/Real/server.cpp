#include <iostream>
#include <string>
// #include<vector>
#include<bits/stdc++.h>
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

void sendToClient(int Sd, string s){
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
}

string receiveFromClient(int Sd){
    char tempMsg[100];
    memset(&tempMsg, 0, sizeof(tempMsg));
    recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
    string s(tempMsg);    
    return s;
}
void findRestaurantList(string &s){
    fstream file;
    file.open("Restaurants.txt");

    while(file){
        string temp;
        getline(file, temp);
        s+=temp;
        s+='\n';
    }

}
vector<string> splitString(string &s){
    stringstream ss(s);
    string temp;
    vector<string> v;
    while(getline(ss, temp, ' '))
        v.push_back(temp);
    return v;
}
bool checkIfUserExists(string &userNameResponse, string &passwordResponse){
    fstream file;
    file.open("Users.txt");

    while(file){
        string temp;
        
        getline(file, temp);
        if(temp.length() == 0)
            break;
        vector<string> v = splitString(temp);
        if(v[0]==userNameResponse && v[1] == passwordResponse)
            return true;
    }
    return false;
}

bool login(int Sd){
    string s = "Weclcome to Food-Kurnool\n Please log in - \n";
    s+="Username/email: ";
    sendToClient(Sd, s);
    usleep(30000);
    string userNameResponse = receiveFromClient(Sd);
    usleep(30000);
    cout << "UserName: "<<userNameResponse << flush;
    memset(&s, 0, sizeof(s));
    s = "\nPassword: ";
    sendToClient(Sd, s);
    usleep(30000);
    string passwordResponse = receiveFromClient(Sd);
    usleep(30000);
    cout << "\nPassword: "<<passwordResponse << flush;
    
    if(checkIfUserExists(userNameResponse, passwordResponse)){
        s = "SUCCESS";
        sendToClient(Sd, s);
        return true;
    } else{
        s = "FAILURE";
        sendToClient(Sd, s);
        return false;
    }
    
    
}

void showDashboard(){
    
}
void fun(){
    sockaddr_in newSockAddr;
    socklen_t newSockAddrSize = sizeof(newSockAddr);
    //accept, create a new socket descriptor to 
    //handle the new connection with client
    int Sd = accept(serverSd, (sockaddr *)&newSockAddr, &newSockAddrSize);
    if(Sd < 0)
    {
        cerr << "Error accepting request from client!" << endl;
        exit(1);
    }
    cout << "Connected with client!" << endl;
    
    
    while(1)
    {
        usleep(30000);
        bool isLoggedIn = login(Sd);

        cout << "IsLoggedIn: "<<isLoggedIn << flush;
        if(!isLoggedIn)
            continue;
        
        showDashboard();
        string s="Select restaurant from the list of available ones";
        
        findRestaurantList(s);
        
        int n = s.length();
        
        char tempMsg[n+1];
        
        int ind = 0;
        for(int i=0;i<=n;i++){
            tempMsg[ind]=s[i];
            if(tempMsg[ind]=='\0')
                ind--;
            ind++;
        }
        
        sendToClient(Sd, s);
        
        s = receiveFromClient(Sd);
        
        // recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
            
        if(!strcmp(msg, "exit")){
            cout << "Client has quit the session" << endl;
            break;
        } else{
            int num = atoi(tempMsg);
            cout<<num;
        }
        
        
        
        // memset(&msg, 0, sizeof(msg)); //clear the buffer
        // strcpy(msg, data.c_str());
        if(msg == "exit")
        {
            //send to the client that server has closed the connection
            send(Sd, (char*)&msg, strlen(msg), 0);
            break;
        }
        //send the message to client
        int temp = send(Sd, (char*)&msg, strlen(msg), 0);
        if(temp<=0)
            break;
        
    }
    
    
    close(Sd);
}
int main(int argc, char *argv[])
{
    cin.tie(nullptr);
    // ios::sync_with_stdio(true);
    //for the server, we only need to specify a port number
    if(argc != 2)
    {
        cerr << "Usage: port" << endl;
        exit(0);
    }
    //grab the port number
    port = atoi(argv[1]);
    //buffer to send and receive messages with
     
    //setup a socket and connection tools
    bzero((char*)&servAddr, sizeof(servAddr));
    servAddr.sin_family = AF_INET;
    servAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servAddr.sin_port = htons(port);
 
    //open stream oriented socket with internet address
    //also keep track of the socket descriptor
    serverSd = socket(AF_INET, SOCK_STREAM, 0);
    if(serverSd < 0)
    {
        cerr << "Error establishing the server socket" << endl;
        exit(0);
    }
    //bind the socket to its local address
    bindStatus = bind(serverSd, (struct sockaddr*) &servAddr, 
        sizeof(servAddr));
    if(bindStatus < 0)
    {
        cerr << "Error binding socket to local address" << endl;
        exit(0);
    }
    cout << "Waiting for a client to connect..." << endl;
    //listen for up to 5 requests at a time
    listen(serverSd, 100);
    // fun();
    // ----------------------------------------------------------------
    //                                    Multi 
    thread th1[100];
    int ind = 0;
    while(ind<100){
        th1[ind++]=thread(fun);
    }
    ind =0;
    while(ind<100){
        th1[ind++].join();
    }
    // ----------------------------------------------------------------


    close(serverSd);
    cout << "********Session********" << endl;
    // cout << "Bytes written: " << bytesWritten << " Bytes read: " << bytesRead << endl;
    // cout << "Elapsed time: " << (end1.tv_sec - start1.tv_sec) 
    //     << " secs" << endl;
    cout << "Connection closed..." << endl;
    return 0;   
}
