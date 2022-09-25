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
string findDishesInCart(string userName){
    fstream cart("Cart.txt");
    string s = "";
    while(cart){
        string temp;
        
        getline(cart, temp);
        
        cout << temp << flush;
        if(temp.length() == 0)
            break;
        
        vector<string> v = splitString(temp);
        if(v[0]==userName){
            for(int i = 1; i < v.size(); i+=2){
                s+=("Restaurant: "+v[i]+" Dish: "+v[i+1]+"\n");
            }
        }
        
    }
    return s;
}
string findDishesAvailable(string restaurantNumber){
    fstream file;
    string s;
    file.open("Dishes.txt");

    while(file){
        string temp;
        getline(file, temp);

        if(temp.length() == 0)
            break;
        vector<string> v = splitString(temp);
        
        if(v[0] == restaurantNumber){
            for(int i = 1; i < v.size(); i++){
                s+=to_string(i);
                s+=" ";
                s+=v[i];
                s+='\n';
            }
        }
    }
    return s;
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

bool login(int Sd, string &userName){
    string s = "Weclcome to Food-Kurnool\n Please log in - \n";
    s+="Username/email: ";
    sendToClient(Sd, s);
    sleep(0.1);
    string userNameResponse = receiveFromClient(Sd);
    sleep(0.1);
    
    memset(&s, 0, sizeof(s));
    s = "\nPassword: ";
    sendToClient(Sd, s);
    sleep(0.1);
    string passwordResponse = receiveFromClient(Sd);
    sleep(0.1);
    
    
    if(checkIfUserExists(userNameResponse, passwordResponse)){
        s = "SUCCESS";
        userName = userNameResponse;
        sendToClient(Sd, s);
        return true;
    } else{
        s = "FAILURE";
        sendToClient(Sd, s);
        return false;
    }
    
    
}

int showDashboard(int Sd){
    string s = "Press 1 to Proceed to Cart \nPress 2 to see list of available restaurants\n";
    sendToClient(Sd, s);
    sleep(0.1);
    memset(&s, 0, sizeof(s));
    string ss = receiveFromClient(Sd);
    
    int temp = stoi(ss);
    
    return temp;
}

void proceedToBrowse(int Sd, string userName){
    string s = findRestaurantList();
    sendToClient(Sd, s);

    memset(&s, 0, sizeof(s));

    sleep(0.1);

    string restaurantNumberSelectedByClient = receiveFromClient(Sd);

    string disheshAvailable = findDishesAvailable(restaurantNumberSelectedByClient);


    sendToClient(Sd, disheshAvailable);

    string dishNumber = receiveFromClient(Sd);

    sendToClient(Sd, "Order Placed Successfully");

    fstream cart("Cart.txt");
    if(!cart){
        cout<<"Cart Not Created"<<flush;
    }
    
    fstream newCart("newCart.txt", ios::out);
    string previousItemsInCart="";
    while(cart){
        string temp;
        
        getline(cart, temp);
        
        cout << temp << flush;
        if(temp.length() == 0)
            break;
        
        vector<string> v = splitString(temp);
        if(v[0]==userName)
            previousItemsInCart = temp;
        else
            newCart << temp << endl;
        
    }
    if(previousItemsInCart == ""){
        newCart << userName << " " << restaurantNumberSelectedByClient << " " << dishNumber <<endl;
    } else{
        newCart << previousItemsInCart << " " << restaurantNumberSelectedByClient << " " << dishNumber <<endl;
    }

    remove("Cart.txt");
    rename("newCart.txt", "Cart.txt");

}

void proceedToCart(int Sd, string userName){
    string s = "You Cart is as follows - ";
    s += findDishesInCart(userName);

    sendToClient(Sd, s);

    string resp = receiveFromClient(Sd);

    if(resp == 1){
        proceedToCheckout(Sd, userName);  
    } else{
        proceedToBrowse(Sd);
    }
}

void proceedToCart(int Sd, string userName){
    fstream orders("Orders.txt", ios::app);
    
}

void fun(){
    std::ios::sync_with_stdio(false);
    
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
        sleep(0.1);
        string userName;
        bool isLoggedIn = login(Sd, userName);

        
        if(!isLoggedIn)
            continue;
        
        int temp = showDashboard(Sd);

        if(temp == 2){
            proceedToBrowse(Sd, userName);
        } else if(temp == 1){
            proceedToCart(Sd, userName);
        } else{
            break;
        }
        
    }
    
    
    close(Sd);
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
            th1[ind++]=thread(fun);
        }
        ind =0;
        while(ind<100){
            th1[ind++].join();
        }
    goto server;

    // ----------------------------------------------------------------


    close(serverSd);
   
    return 0;   
}
