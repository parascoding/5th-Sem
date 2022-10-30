import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
public class Main{
    public static void main(String args[]){
        try{
            int n = 5;
            Table table = new Table(n);
            for(int i = 0; i < n; i++)
                new Philoshper(i, table).start();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Table{
    int tableSize;
    int numberOfForks;
    boolean fork[];
    Table(int tableSize){
        this.tableSize = tableSize;
        this.numberOfForks = tableSize;
        this.fork = new boolean[tableSize];
        for(int i = 0; i < tableSize; i++)
            fork[i] = true;
    }
    private void takeFork(int forkNumber) throws Exception{
        synchronized(this){
            while(!fork[forkNumber])
                this.wait();
            fork[forkNumber] = false;
        }
    }
    private void putFork(int forkNumber) throws Exception{
        synchronized(this){
            fork[forkNumber] = true;
            this.notifyAll();
        }
    }
    public void eat(int philoshperId){
        try{
            takeFork(philoshperId);
            takeFork((philoshperId + 1) % numberOfForks);
            System.out.println("PHILOSHPER: "+philoshperId+
                    " started eating");
            int DELAY = (int)(Math.random() * 2000);
            Thread.sleep(DELAY);
            System.out.println("PHILOSHPER: "+philoshperId+
                    " finished eating");
            putFork(philoshperId);
            putFork((philoshperId + 1) % numberOfForks);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Philoshper extends Thread{
    int philoshperId;
    Table table;
    Philoshper(int philoshperId, Table table){
        this.philoshperId = philoshperId;
        this.table = table;
    }
    private void think(){
        try{
            int DELAY = (int)(Math.random() * 2000);
            Thread.sleep(DELAY);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        try{
            while(true){
                think();
                this.table.eat(this.philoshperId);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}