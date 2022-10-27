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
#include <sys/unistd.h>
#include <errno.h>
#include <sys/select.h>

#define MAX_CLIENTS 10
#define TRUE 1
using namespace std;

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

int getMaxFD(int sock_fd, int clients_fd[], fd_set *read_fds)
{

    int max_fd = sock_fd;

    for (int i = 0; i < MAX_CLIENTS; i++)
    {

        int sd = clients_fd[i];


        if (sd > 0)
            FD_SET(sd, read_fds);

        if (sd > max_fd)
            max_fd = sd;
    }

    return max_fd;
}


void createConnection(int sock_fd, struct sockaddr_in *client, int clients_fd[])
{

    int client_new_fd;
    socklen_t length = sizeof(struct sockaddr_in);


    if ((client_new_fd = accept(sock_fd, (struct sockaddr *)client, &length)) == -1)
    {

        printf("Failed to Connect to Client.........\n");
        return;
    }


    for (int i = 0; i < MAX_CLIENTS; i++)
    {

        if (clients_fd[i] == 0)
        {

            clients_fd[i] = client_new_fd;

            break;
        }
    }


    printf("Successfully Connected with Client %d, ip is %s\n", client_new_fd, inet_ntoa((*client).sin_addr));
}


int main(int argc, char *argv[])
{
    if (argc <= 1)
        printf("Please Pass PORT_NO as command line argument.\n");
    
    else{
        int PORT_NO = atoi(argv[1]);
        int sock_fd, clients_fd[MAX_CLIENTS] = {0};
        struct sockaddr_in server, client;

        if ((sock_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1){
            printf("Creating Socket Failed......Please Try Again\n");
            return 0;
        }

        printf("Socket Created Successfully .....\n");

        int opt = TRUE;

        if (setsockopt(sock_fd, SOL_SOCKET, SO_REUSEADDR, (void *)&opt, (socklen_t)sizeof(opt)) == -1){
            printf("Failed to update server options for handling multiple clients.\n");
            return 0;
        }
        bzero(&server, sizeof(server));
        server.sin_family = AF_INET;
        server.sin_port = htons(PORT_NO);
        server.sin_addr.s_addr = INADDR_ANY;


        if (bind(sock_fd, (struct sockaddr *)&server, (socklen_t)sizeof(struct sockaddr_in)) == -1){
            printf("Binding of Server to address failed.....\n");
            return 0;
        }

        printf("Socket Successfully Binded .....\n");

        if (listen(sock_fd, 3) == -1){
            printf("Socket failed to Listen ......\n");
            return 0;
        }

        printf("Socket successfully Created and went to Listen Connections.......\n");
        unsigned int length = sizeof(struct sockaddr_in);
        fd_set read_fds;
        
        while (TRUE){
            FD_ZERO(&read_fds);

            FD_SET(sock_fd, &read_fds);
            int max_sd = getMaxFD(sock_fd, clients_fd, &read_fds);

            int activity = select(max_sd + 1, &read_fds, NULL, NULL, NULL);

            if (activity == -1 && errno != EINTR){
                printf("Select Throws ERROR!!\n");
            }

            if (FD_ISSET(sock_fd, &read_fds))            {
                createConnection(sock_fd, &client, clients_fd);
            }

            for (int i = 0; i < MAX_CLIENTS; i++){

                int sd = clients_fd[i];
                if (FD_ISSET(sd, &read_fds))
                {

                    char buffer[1024] = {0};
                    string resp = receiveFromClient(sd);
                    if (resp.length() <= 0)
                    {

                        printf("Client %d Disconnected..........\n", sd);

                        close(sd);

                        clients_fd[i] = 0;
                    }
                    else{
                        vector<string> v = splitString(resp);
                        if(v.size() < 2){
                            cout << "ERROR";
                            sendToClient(sd, "ERROR");
                            continue;
                        }
                        int n = stoi(v[0]);
                        int m = stoi(v[1]);
                        string answer;
                        if(isAgreeable(n, m)){
                            answer = "Agreeable numbers";
                        } else{
                            answer = "Not agreeable numbers";
                        }
                        sendToClient(sd, answer);
                    }
                }
            }
        }
    }
}