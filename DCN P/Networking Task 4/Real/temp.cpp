#include<bits/stdc++.h>
using namespace std;
void read_record()
{
  
    // File pointer
    fstream fin;
  
    // Open an existing file
    fin.open("Restaurants.csv", ios::in);
  
    // Get the roll number
    // of which the data is required
    int rollnum, roll2, count = 0;
    
  
    // Read the Data from the file
    // as String Vector
    vector<string> row;
    string line, word, temp;
  
    while (fin >> temp) {
  
        row.clear();
  
        // read an entire row and
        // store it in a string variable 'line'
        getline(fin, line);
  
        // used for breaking words
        stringstream s(line);
  
        // read every column data of a row and
        // store it in a string variable, 'word'
        while (getline(s, word, ',')) {
  
            // add all the column data
            // of a row to a vector
            row.push_back(word);
        }
        for(auto &x:row)
        cout<<x;
      
    }
    
    
}
int main(){
    read_record();
}