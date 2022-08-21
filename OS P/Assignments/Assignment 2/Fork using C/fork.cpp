#include <unistd.h>
#include<bits/stdc++.h>
#include <sys/types.h>
#include <sys/wait.h>
using namespace std;

int main()
{
   
    int id = fork();
    int a, b;
    wait(NULL);
    if(id==0){
        cout << "In the child process\n";
    } else{
        cout << "In the parent process\n";
    }
    cout << "Enter a and b\n";
    cin >> a >> b;

    cout << a << " + " << b << " is " << (a+b) << endl;
    
    int pid = getpid();
    int ppid = getppid();
    cout << "The pid of this process was = " << pid;
    cout << " The parent pid is = " << ppid << endl;
    return 0;
}
