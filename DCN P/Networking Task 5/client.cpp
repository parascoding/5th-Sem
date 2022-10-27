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
using namespace std;
void sendToServer(int Sd, string s){
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
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    
}

string receiveFromServer(int Sd){
    try{
        char tempMsg[10000];
        memset(&tempMsg, 0, sizeof(tempMsg));
        recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
        string s(tempMsg);    
        return s;
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    return "";
}

int main(int argc, char *argv[])
{


	if (argc <= 1)
		printf("Please pass the port number to use\n");
	else
	{
		int PORT_NO = atoi(argv[1]);
		int sock_fd;

		struct sockaddr_in server;
		if ((sock_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
		{

			printf("Creating Socket Failed......Please Try Again\n");
			return 0;
		}

		bzero(&server, sizeof(server));
		server.sin_family = AF_INET;
		server.sin_port = htons(PORT_NO);
		server.sin_addr.s_addr = INADDR_ANY;

		if (connect(sock_fd, (struct sockaddr *)&server, (socklen_t)sizeof(struct sockaddr_in)) == -1)
		{

			printf("Failed to Connect to Server.........\n");
			return 0;
		}

		printf("Connection Successfull with Server..........\n");

		
		char recvBuffer[1024] = {0};
		char sendBuffer[1024] = {0};

		ssize_t flag;


		while (1)
		{
			string s = "Enter Two Numbers to check if they are agreeable";
			cout << s << endl << flush;
			string resp;
			getline(cin, resp);
			sendToServer(sock_fd, resp);
			string answer = receiveFromServer(sock_fd);
			cout << answer << endl << flush;
		}

		close(sock_fd);
	}

	return 0;
}