
// CPP program to demonstrate multithreading
// using three different callables.
#include <iostream>
#include <thread>
using namespace std;
  
// A dummy function
void foo(int Z)
{
    for (int i = 0; i < 1000; i++) {
        cout << Z<<endl;
    }
}
  
// A callable object
class thread_obj {
public:
    void operator()(int x)
    {
        for (int i = 0; i < x; i++)
            cout << "Thread using function"
                  " object as  callable\n";
    }
};
  
int main()
{
    cout << "Threads 1 and 2 and 3 "
         "operating independently" << endl;
  
    // This thread is launched by using 
    // function pointer as callable
    thread th1(foo, 2);
  
    // This thread is launched by using
    // function object as callable
    thread th2(foo, 56);
  
    // This thread is launched by using 
    // lamda expression as callable
 
  
    // Wait for the threads to finish
    // Wait for thread t1 to finish
    th1.join();
    // Wait for thread t2 to finish
    th2.join();
  
    // Wait for thread t3 to finish

  
    return 0;
}