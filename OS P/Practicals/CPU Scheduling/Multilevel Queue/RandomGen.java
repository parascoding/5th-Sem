import java.io.*;
import java.util.*;
public class RandomGen{
    static PrintWriter ot;
    public static void main(String args[]) throws IOException{
        ot = new PrintWriter(new FileWriter("input.txt"));
        int n = 30;
        for(int i=1;i<=n;i++){
            ot.print(i+" ");
            ot.print((int)(Math.random()*100)+" ");
            ot.print((int)(Math.random()*100)+" ");
            ot.print((int)(Math.random()*10)+" ");
            ot.println();
        }
        ot.close();
    }
}