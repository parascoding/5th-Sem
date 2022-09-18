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

bool findRestaurantList(string &s){
    fstream fin;
    fin.open("Restaurants.csv", ios::in);
    string temp;
    
    while(fin >> temp){
        vector<string> vec;
        vec.clear();
        string line;
        getline(fin, line);

        stringstream ss(line);
        string word;
        while(getline(ss, word, ',')){
            vec.push_back(word);
        }
        if(vec.size()>0){
            s+=vec[0];
            s+="\n";
        }
    
    }
    return true;
}
void fun(){
    sockaddr_in newSockAddr;
    socklen_t newSockAddrSize = sizeof(newSockAddr);
    //accept, create a new socket descriptor to 
    //handle the new connection with client
    int newSd = accept(serverSd, (sockaddr *)&newSockAddr, &newSockAddrSize);
    if(newSd < 0)
    {
        cerr << "Error accepting request from client!" << endl;
        exit(1);
    }
    cout << "Connected with client!" << endl;
    //lets keep track of the session time
    struct timeval start1, end1;
    gettimeofday(&start1, NULL);
    //also keep track of the amount of data sent as well
    int bytesRead, bytesWritten = 0;
    while(1)
    {
        string s="Select restaurant from the list of available ones";
        
        bool x = findRestaurantList(s);
        
        int n = s.length();
        
        char tempMsg[n+1];
        
        int ind = 0;
        for(int i=0;i<=n;i++){
            tempMsg[ind]=s[i];
            if(tempMsg[ind]=='\0')
            ind--;
            ind++;
            
        }
        
        
        send(newSd, (char*)&tempMsg, strlen(tempMsg), 0);

        memset(&tempMsg, 0, sizeof(tempMsg));//clear the buffer

        recv(newSd, (char*)&tempMsg, sizeof(tempMsg), 0);
            
        if(!strcmp(msg, "exit")){
            cout << "Client has quit the session" << endl;
            break;
        } else{
            int num = atoi(tempMsg);
            cout<<num;
        }
        // cout << ">";
        string data;
        // getline(cin, data);
        // memset(&msg, 0, sizeof(msg)); //clear the buffer
        // strcpy(msg, data.c_str());
        if(msg == "exit")
        {
            //send to the client that server has closed the connection
            send(newSd, (char*)&msg, strlen(msg), 0);
            break;
        }
        //send the message to client
        int temp = send(newSd, (char*)&msg, strlen(msg), 0);
        if(temp<=0)
            break;
        
    }
    //we need to close the socket descriptors after we're all done
    gettimeofday(&end1, NULL);
    close(newSd);
}
int main(int argc, char *argv[])
{
    ios::sync_with_stdio(false);
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
    listen(serverSd, 5);
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
