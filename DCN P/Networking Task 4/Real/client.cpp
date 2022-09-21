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
    // cout<<"Sending"<<endl<<flush;
    send(Sd, (char*)&tempMsg, strlen(tempMsg), 0);
    // cout<<"Sended"<<endl<<flush;
}

string receiveFromServer(int Sd){
    char tempMsg[200];
    memset(&tempMsg, 0, sizeof(tempMsg));
    recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
    string s(tempMsg);    
    return s;
}
bool login(int Sd){
    usleep(30000);
    string s = receiveFromServer(Sd);
    usleep(30000);
    cout << s << flush;
    string response;
    usleep(30000);
    cin >> response;
    usleep(30000);
    sendToServer(Sd, response);
    usleep(30000);
    s = receiveFromServer(Sd);
    usleep(30000);
    cout<<"Password: " << flush;
    usleep(30000);
    cin >> response;
    usleep(30000);
    sendToServer(Sd, response);
    usleep(30000);
    s = receiveFromServer(Sd);
    cout << s << endl << flush;
    if(s=="SUCCESS")
        return true;
    else
        return false;
}

void showDashboard(){

}
//Client side
int main(int argc, char *argv[])
{
    cin.tie(nullptr);
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
    while(1)
    {
        string data;
        usleep(30000);
        // getline(cin, data);
        bool isLoggedIn = login(Sd);
        if(!isLoggedIn){
            cout << "Login  Failed " << endl << flush;
            continue;
        }

        showDashboard();


        recv(Sd, (char*)&msg, sizeof(msg), 0);
        cout << msg << endl;
        memset(&msg, 0, sizeof(msg));//clear the buffer

        
        cout << ">";
        getline(cin, data);
        strcpy(msg, data.c_str());
        send(Sd, (char*)&msg, strlen(msg), 0);
        // break;
        
        // bytesWritten += send(Sd, (char*)&msg, strlen(msg), 0);
        // cout << "Awaiting server response..." << endl;
        memset(&msg, 0, sizeof(msg));//clear the buffer
        // bytesRead += recv(Sd, (char*)&msg, sizeof(msg), 0);
        if(!strcmp(msg, "exit"))
        {
            cout << "Server has quit the session" << endl;
            break;
        }
        {cout << msg << endl;}
    }
    gettimeofday(&end1, NULL);
    close(Sd);
    cout << "********Session********" << endl;
    cout << "Bytes written: " << bytesWritten << 
    " Bytes read: " << bytesRead << endl;
    cout << "Elapsed time: " << (end1.tv_sec- start1.tv_sec) 
      << " secs" << endl;
    cout << "Connection closed" << endl;
    return 0;    
}
