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
#include <ctime>   
#include<thread>
#include <time.h>

using namespace std;
sockaddr_in servAddr;
char msg[1500];
int port, serverSd, bindStatus;

string removeSpaces(string str){
    try{
        str.erase(remove(str.begin(), str.end(), ' '), str.end());
        return str;
    } catch(const std::exception& e){
         std::cout << "Caught exception 32 \"" << e.what() << "\"\n";
    }
    return "";

}

string current_date(){
    try{
        time_t now = time(NULL);
        struct tm tstruct;
        char buf[40];
        tstruct = *localtime(&now);
        //format: day DD-MM-YYYY
        strftime(buf, sizeof(buf), "%A %d/%m/%Y", &tstruct);
        return buf;
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    return "";
}
vector<string> splitString(string &s){
    try{
        stringstream ss(s);
        string temp;
        vector<string> v;
        while(getline(ss, temp, ' '))
            v.push_back(temp);
        return v;
    } catch(...){
        
    }
    vector<string> x;
    return x;
}

string findRestaurantNameByNumber(int restaurantNumber){
    try{
        fstream restaurant("Restaurants.txt");
        string s;
        
        while(restaurant){
            getline(restaurant, s);
            vector<string> vec = splitString(s);

            if(restaurantNumber == stoi(vec[0])){
                string ans = "";
                for(int i = 1; i < vec.size(); i++){
                    ans += vec[i];
                    ans += " ";
                }
                
                return ans;
            }
        }
    }catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
    return "";
}
string findDishByRestaurantNumberAndDishNumber(int restaurantNumber, int dishNumber){
    
    string res = "";
    try{
        fstream dish("Dishes.txt");
        string s;
        int count = 1;
        while(dish){
            getline(dish, s);
            vector<string> vec = splitString(s);
            if(vec[0]==to_string(restaurantNumber)){
                res += vec[dishNumber*2-1];
                res +=" Price: ";
                res += vec[dishNumber*2];
                return res;
            }
        }
    } catch(const std::exception& e){
         std::cout << "Caught exception 97 \"" << e.what() << "\"\n";
    }
    return res;
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

    } catch(...){
        
    }
}

string receiveFromClient(int Sd){
    try{
        char tempMsg[1000];
        memset(&tempMsg, 0, sizeof(tempMsg));
        recv(Sd, (char*)&tempMsg, sizeof(tempMsg), 0);
        string s(tempMsg);    
        
        return s;
    } catch(...){
        
    }
    return "-1";
}
string findRestaurantList(){
    try{
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
    } catch(...){
        
    }
    return "-1";

}
string findDishesInCart(string userName){
    try{
        fstream cart("Cart.txt");
        string s = "";
        while(cart){
            string temp;
            
            getline(cart, temp);
            

            if(temp.length() == 0)
                break;
            
            vector<string> v = splitString(temp);
            if(v[0]==userName){
                for(int i = 1; i < v.size(); i+=2){
                    s+=("Restaurant: "+findRestaurantNameByNumber(stoi(v[i]))+" Dish: "+findDishByRestaurantNumberAndDishNumber(stoi(v[i]), stoi(v[i+1]))+"\n");
                }
            }
        }
        return s;

    } catch(const std::exception& e){
         std::cout << "Caught exception 175 \"" << e.what() << "\"\n";
    }
    return "-1";
}
string findDishesAvailable(string restaurantNumber){
    try{

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
                for(int i = 1; i < v.size(); i+=2){
                    s+=to_string(i/2 + 1);
                    s+=" Name: ";
                    s+=v[i];
                    s+="  Price: ";
                    s+=v[i+1];
                    s+='\n';
                }
            }
        }
        return s;
    } catch(...){
        
    }
    return "-1";
}
bool checkIfUserExists(string &userNameResponse, string &passwordResponse){
    try{
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

    } catch(...){
        
    }
    return false;
}

bool login(int Sd, string &userName){
    try{
        string s = "Weclcome to Food-Kurnool\n Please log in - \n";
        s+="Username/email: ";
        sendToClient(Sd, s);
        sleep(0.5);
        string userNameResponse = receiveFromClient(Sd);
        
        sleep(0.5);

        memset(&s, 0, sizeof(s));
        s = "\nPassword: ";
        sendToClient(Sd, s);
        sleep(0.5);
        string passwordResponse = receiveFromClient(Sd);
        sleep(0.5);


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
    } catch(...){
        
    }
    return false;
}

int showDashboard(int Sd){
    try{
        string s = "Press 1 to Proceed to Cart \nPress 2 to see list of available restaurants\nPress 3 to Cancel Exisiting order\n";
        sendToClient(Sd, s);
        sleep(0.5);
        memset(&s, 0, sizeof(s));
        string ss = receiveFromClient(Sd);

        int temp = stoi(ss);
        return temp;
    } catch(...){
        
    }
    return -1;
}

void proceedToBrowse(int Sd, string userName){
    try{
        string s = findRestaurantList();
        sendToClient(Sd, s);

        memset(&s, 0, sizeof(s));

        sleep(0.5);

        string restaurantNumberSelectedByClient = receiveFromClient(Sd);

        string disheshAvailable = findDishesAvailable(restaurantNumberSelectedByClient);


        sendToClient(Sd, disheshAvailable);

        string dishNumber = receiveFromClient(Sd);

        sendToClient(Sd, "Dishesh Added to Cart Successfully");

        fstream cart("Cart.txt");
        if(!cart){
            cout<<"Cart Not Created"<<flush;
        }
        
        fstream newCart("newCart.txt", ios::out);
        string previousItemsInCart="";
        while(cart){
            string temp;
            
            getline(cart, temp);
            
       
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

    } catch(...){

    }

}

void generateInvoice(int Sd, string userName, string items){
    vector<string> vec;
    try{
        string s;
        s+="\t\t\t\t !! INVOICE !!\n";
        s+="\n\nDate: ";
        s += current_date();
        s+="\n\n\nCustomer's User Name: ";
    
        s+=userName;
        s+="\n\n\n\n\t\t\t\t Items\n";
        int total = 0;
        vec = splitString(items);
        for(int i = 1; i < vec.size(); i+=2){
            s+=to_string(i/2 + 1);
            s+=" Restaurant: " ;
            s+=findRestaurantNameByNumber(stoi(vec[i]));
            s+=" Dish: ";
            string temp = findDishByRestaurantNumberAndDishNumber(stoi(removeSpaces(vec[i])), stoi(removeSpaces(vec[i+1])));
            
            s+=temp;
            cout << temp << endl;
            vector<string> v = splitString(temp);
            total += stoi(removeSpaces(v[2]));
            s+="\n\n";
        }
        s += "Total : ";
        s += to_string(total);
        s += " Only";
        sendToClient(Sd, s);

    } catch(const std::exception& e){
        cout << items << endl << flush;
        for(int i = 0; i < vec.size(); i++){
            cout << vec[i] << " " << flush;
        }
        cout << "\nCaught exception 375 \"" << e.what() << "\"\n" << flush;
    }
}

void makeOrder(int Sd, string userName, string previousItemsInCart){
    try{
  
        fstream order("Orders.txt");
        fstream newOrder("newOrder.txt", ios::out);
        while(order){
            string temp;
            
            getline(order, temp);

            if(temp.length() == 0)
                break;
            
            vector<string> v = splitString(temp);
            if(v[0]!=userName)
                newOrder << temp << endl;
        }
        newOrder << previousItemsInCart << endl << flush;
        remove("Orders.txt");
        rename("newOrder.txt", "Orders.txt");

        generateInvoice(Sd, userName, previousItemsInCart);
    } catch(...){

    }
}
void cancelOrder(int Sd, string userName){
    try{
        fstream order("Orders.txt");
        fstream newOrder("newOrder.txt", ios::out);
        while(order){
            string temp;
            
            getline(order, temp);

            if(temp.length() == 0)
                break;
            
            vector<string> v = splitString(temp);
            if(v[0]!=userName)
                newOrder << temp << endl;
        }
        remove("Orders.txt");
        rename("newOrder.txt", "Orders.txt");
        string s = "ORDER CANCELLED SUCCESFULLY\n";
        sendToClient(Sd, s);
    } catch(...){

    }
}
void proceedToCheckout(int Sd, string userName){
    try{
        fstream cart("Cart.txt");

        fstream newCart("newCart.txt", ios::out);
        string previousItemsInCart="";
        while(cart){
            string temp;
            
            getline(cart, temp);
            
     
            if(temp.length() == 0)
                break;
            
            vector<string> v = splitString(temp);
            if(v[0]==userName)
                previousItemsInCart = temp;
            else
                newCart << temp << endl;
        }
        remove("Cart.txt");
        rename("newCart.txt", "Cart.txt");
        makeOrder(Sd, userName, previousItemsInCart);
        string resp = "ORDER PLACED SUCCESFULLY";
        sendToClient(Sd, resp);

        
    } catch(const std::exception& e){
         std::cout << "Caught exception 458 \"" << e.what() << "\"\n";
    }
}
void proceedToCart(int Sd, string userName){
    try{
       
        string s = "You Cart is as follows - \n";
        s += findDishesInCart(userName);

        sendToClient(Sd, s);

        string resp = receiveFromClient(Sd);

        if(resp == "1"){
            proceedToCheckout(Sd, userName);  
        } else{
            proceedToBrowse(Sd, userName);
        }
    } catch(const std::exception& e){
         std::cout << "Caught exception 477 \"" << e.what() << "\"\n";
    }
}

void loginSignUpPrompt(int Sd){
    try{
        string s = "Weclcome to Food-Kurnool\nPress 1 to Sign Up\nPress 2 to Log In\n";
        sendToClient(Sd, s);
        sleep(0.5);
        string resp = receiveFromClient(Sd);
        sleep(1);
        if(resp == "2")
            return;
        sendToClient(Sd, "Enter User Name: ");
        string userName = receiveFromClient(Sd);
        sendToClient(Sd, "\nEnter Password: ");
        string password = receiveFromClient(Sd);
        sendToClient(Sd, "SUCESS");

        fstream user("Users.txt", ios_base::app);
        user << userName << " " << password << endl;
    } catch(...){

    }
}
void fun(){
    try{
        std::ios::sync_with_stdio(false);
        
        sockaddr_in newSockAddr;
        socklen_t newSockAddrSize = sizeof(newSockAddr);

        //accept, create a new socket descriptor to 
        //handle the new connection with client

        int Sd = accept(serverSd, (sockaddr *)&newSockAddr, &newSockAddrSize);
        if(Sd < 0){
            cerr << "Error accepting request from client!" << endl;
        }

        
        bool isLoggedIn = false;
        string userName;
        while(1)
        {
            sleep(0.5);
            if(!isLoggedIn)
                loginSignUpPrompt(Sd);
            if(!isLoggedIn)
                isLoggedIn = login(Sd, userName);
        
            sleep(0.5);
            if(!isLoggedIn)
                continue;
            sleep(0.5);
            int temp = showDashboard(Sd);
            sleep(0.5);
            if(temp == 2){
                proceedToBrowse(Sd, userName);
            } else if(temp == 1){
                proceedToCart(Sd, userName);
            } else if(temp == 3){
                cancelOrder(Sd, userName);
            }
            else{
                string res = "Wrong Choice\n Closing connetion";
                close(Sd);
                break;
            }
            
        }
        close(Sd);
    }
    catch(...){

    }
}

int main(int argc, char *argv[]){
    try{
        cin.tie(nullptr);
        
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


        close(serverSd);
    } catch(const std::exception& e){
         std::cout << "Caught exception 76 \"" << e.what() << "\"\n";
    }
   
    return 0;   
}
