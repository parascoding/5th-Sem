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
    Semaphore forksSemaphore[];
    Table(int tableSize){
        this.tableSize = tableSize;
        this.numberOfForks = tableSize;
        this.forksSemaphore = new Semaphore[numberOfForks];
        for(int i = 0; i < numberOfForks; i++)
            this.forksSemaphore[i] = new Semaphore(1);
    }
    private void takeFork(int forkNumber){
        try{
            this.forksSemaphore[forkNumber].acquire();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private void putFork(int forkNumber){
        try{
            this.forksSemaphore[forkNumber].release();
        } catch(Exception e){
            e.printStackTrace();
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