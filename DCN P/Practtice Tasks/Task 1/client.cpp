#include <bits/stdc++.h>
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
using namespace std;
void sendToServer(int Sd, string s){
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

string receiveFromServer(int Sd){
    char tempMsg[200];
    memset(&tempMsg, 0, sizeof(tempMsg));
    recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
    string s(tempMsg);    
    return s;
}

int main(int argc, char *argv[])
{
    std::ios::sync_with_stdio(false);
    //we need 2 things: ip address and port number, in that order
    if(argc != 3)
    {
        cerr << "Usage: ip_address port" << endl; exit(0); 
    } //grab the IP address and port number 
    char *serverIp = argv[1]; int port = atoi(argv[2]); 
    //create a message buffer 
    char msg[1500]; 
    //setup a socket and connection tools 
    struct hostent* host = gethostbyname(serverIp); 
    sockaddr_in sendSockAddr;   
    bzero((char*)&sendSockAddr, sizeof(sendSockAddr)); 
    sendSockAddr.sin_family = AF_INET; 
    sendSockAddr.sin_addr.s_addr = 
        inet_addr(inet_ntoa(*(struct in_addr*)*host->h_addr_list));
    sendSockAddr.sin_port = htons(port);
    int Sd = socket(AF_INET, SOCK_STREAM, 0);
    //try to connect...
    int status = connect(Sd,
                         (sockaddr*) &sendSockAddr, sizeof(sendSockAddr));
    if(status < 0)
    {
        cout<<"Error connecting to socket!"<<endl;
        return -1;
    }
    cout << "Connected to the server!" << endl;
    int bytesRead, bytesWritten = 0;
    struct timeval start1, end1;
    gettimeofday(&start1, NULL);
    while(1){
        sleep(0.5);
        string s = receiveFromServer(Sd);
        cout << s << endl << flush;
        cout << ">" << flush;
        sleep(0.5);
        string choice;
        cin >> choice;
        sendToServer(Sd, choice);
        string resp = receiveFromServer(Sd);
        cout << resp << endl << flush;
        if(resp=="Wrong Choice\n Closing connetion")
            break;
    }
    close(Sd);
    
    cout << "Connection closed" << endl;
    return 0;    
}
