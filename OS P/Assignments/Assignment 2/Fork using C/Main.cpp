#include <unistd.h>
#include<bits/stdc++.h>
using namespace std;

int main()
{
   
    int a, b;
    cout << "Enter a and b\n";
    // fork();
    cin >> a >> b;

    cout << a << " + " << b << " is " << (a+b) << endl;
    
    int pid = getpid();

    cout << "The pid of this process was = " << pid << endl;
    return 0;
}
