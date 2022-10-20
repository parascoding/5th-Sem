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
#include <sys/stat.h>
#include <sys/types.h>
using namespace std;

void generateInvoice(string invoice){
    try{
        mkdir("invoices", 0777);
        string t = to_string(time(nullptr));
        string filePath = "invoice_";
        filePath+=t;
        filePath+=".txt";
        fstream file(filePath, ios::out);

        file << invoice;
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
}

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

bool login(int Sd){
    try{
        sleep(0.5);
        string s = receiveFromServer(Sd);
        sleep(0.5);
        cout << s << flush;
        string response;
        sleep(0.5);
        cin >> response;
        sleep(0.5);
        sendToServer(Sd, response);
        sleep(0.5);
        s = receiveFromServer(Sd);
        sleep(0.5);
        cout<<"Password: " << flush;
        sleep(0.5);
        cin >> response;
        sleep(0.5);
        sendToServer(Sd, response);
        sleep(0.5);
        s = receiveFromServer(Sd);
        cout << s << endl << flush;
        if(s == "SUCCESS")
            return true;
        else
            return false;
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    return false;
}

int showDashboard(int Sd){
    try{
        string s = receiveFromServer(Sd);
        cout << s << flush;
        sleep(0.5);
        string response;
        cin >> response;
        sleep(0.5);
        sendToServer(Sd, response);
        sleep(0.5);
        int temp = stoi(response);
        
        
        return temp;
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    return -1;
}

void proceedToBrowse(int Sd){
    try{

        string s = receiveFromServer(Sd);
        cout << s << flush;
        cout << "Enter the number of restaurant to see available dishes\n";
        string number;
        cin >> number;

        sendToServer(Sd, number);

        cout<<"Dishesh Available are - \n" << flush;
        memset(&s, 0, sizeof(s));

        s = receiveFromServer(Sd);
        
        cout << s << endl << flush;

        cout << "Enter Dish Number to add order them\n" << flush;

        string dishNumber;

        cin >> dishNumber;
        sendToServer(Sd, dishNumber);

        memset(&s, 0, sizeof(s));

        s = receiveFromServer(Sd);
        
        cout << s << endl << flush;
        
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    
}

void cancelOrder(int Sd){
    try{
        string s = receiveFromServer(Sd);
        cout << s << endl << flush;
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
}

void proceedToCart(int Sd){
    try{
        string s = receiveFromServer(Sd);

        cout << s << flush;

        cout << "Press 1 to Checkout\n";

        string resp;
        cin >> resp;

        sendToServer(Sd, resp);

        if(resp == "1"){

            string invoice = receiveFromServer(Sd);
            generateInvoice(invoice);

            string temp = receiveFromServer(Sd);
            cout << temp << endl << flush;
            cout << "Invoice Downloaded Successfully\n" << flush;

        }

    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
}

void loginSignUpPrompt(int Sd){
    try{
        string temp = receiveFromServer(Sd);
        cout << temp << endl << flush;

        string s;
        cin >> s;
        sleep(0.5);
        sendToServer(Sd, s);
        sleep(1);
        if(s == "2")
            return;
        string t1 = receiveFromServer(Sd);
        cout << t1 << flush;

        string userName;
        cin >> userName;

        sendToServer(Sd, userName);

        string t2 = receiveFromServer(Sd);
        cout << t2 << flush;

        string password;
        cin >> password;

        sendToServer(Sd, password);
        receiveFromServer(Sd);
        
    } catch(...){

    }
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
    bool isLoggedIn = false;
    while(1){
        string data;
        if(!isLoggedIn)
            loginSignUpPrompt(Sd);
        sleep(0.5);;;
        if(!isLoggedIn)
            isLoggedIn = login(Sd);
        sleep(0.5);;;
        if(!isLoggedIn){
            continue;
        }
        sleep(0.5);;;
        int dashBoard = showDashboard(Sd);
        sleep(0.5);;;
        if(dashBoard == -1)
            break;
        else if(dashBoard == 2){
            proceedToBrowse(Sd);
        }
        else if(dashBoard == 1){
            proceedToCart(Sd);
        } else if(dashBoard == 3){
            cancelOrder(Sd);
        }
    }
    close(Sd);
    
    cout << "Connection closed" << endl;
    return 0;    
}
