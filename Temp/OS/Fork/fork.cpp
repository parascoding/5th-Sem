#include<bits/stdc++.h>
#include <sys/types.h>
#include <unistd.h>
using namespace std;
int x;
int main(){
    x = 1;
    if(fork()==0){
        x++;
        cout <<"Child has x = "<<x;
    } else{
        x--;
        cout <<"Parent has x = "<<x;
    }
    cout << "\n";
    return 0;
}