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

vector<int> findDivisors(int n){
    vector<int> list;

    for (int i = 1; i * i <= n; i++){
        if (n % i == 0){
            list.push_back(i);

            if((n / i) != i && (n / i) != n)
                list.push_back(n / i);
        }
    }
    return list;
}
int findSum(vector<int> list){
    int sum = 0;
    for(int x : list)
        sum += x;

    return sum;
}
bool isAgreeable(int n, int m){
    vector<int> divisorsOfN = findDivisors(n);
    vector<int> divisorsOfM = findDivisors(m);

    int sumOfDivisorsOfN = findSum(divisorsOfN);

    int sumOfDivisorsOfM = findSum(divisorsOfM);
    
    return sumOfDivisorsOfN == m && sumOfDivisorsOfM == n;
}

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
        char tempMsg[1000];
        memset(&tempMsg, 0, sizeof(tempMsg));
        recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
        string s(tempMsg);    
        
        return s;
    } catch(int x){

    }
    return "";
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
        
        while(1){
            string s = "Enter two numbers";
            sendToClient(Sd, s);
            string res = receiveFromClient(Sd);

            vector<string> v = splitString(res);
            if(v.size() < 2){
                cout << "ERROR";
                sendToClient(Sd, s);
                continue;
            }
            int n = stoi(v[0]);
            int m = stoi(v[1]);
            string resp;
            if(isAgreeable(n, m)){
                resp = "YES";
            } else{
                resp = "NO";
            }
            sendToClient(Sd, resp);
            sleep(1);
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
