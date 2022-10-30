import java.io.*;
import java.util.*;

public class Main{
    public static void main(String args[]) throws Exception{
        try{
            int n = 2;
            int m = 2;
            DataBase database = new DataBase();
            for(int i = 0; i < m; i++)
                new Writer(i, database).start();
            for(int i = 0; i < n; i++)
                new Reader(i, database).start();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Semaphore{
    private int val;
    Semaphore(int val){
        this.val = val;
    }
    public synchronized void down(){
        while(val == 0){
            // Busy wait
            try{
                this.wait();
            } catch(Exception e){}
        }
            
        this.val--;
    }
    public synchronized void up(){
        this.val++;
        this.notify();
    }
}
class DataBase{
    volatile private int readersCount;
    volatile private int writersCount;
    Semaphore mutex;
    Semaphore DB;
    DataBase(){
        readersCount = 0;
        writersCount = 0;
        mutex = new Semaphore(1);
        DB = new Semaphore(1);
    }

    public void read(int readerId){
        mutex.down();
        readersCount++;
        if(readersCount == 1){
            DB.down();
        }
        mutex.up();
        System.out.println("READER: "+readerId+" started reading");
        int DELAY = (int)(Math.random() * 5000);
        try{
            Thread.sleep(DELAY);
        } catch(Exception e){}
        System.out.println("READER: "+readerId+" stopped reading");
        mutex.down();
        readersCount--;
        if(readersCount == 0){
            DB.up();
        }
        mutex.up();
    }
    public void write(int writerId){
        DB.down();
        writersCount++;
        System.out.println("WRITER: "+writerId+" started writing");
        int DELAY = (int)(Math.random() * 5000);
        try{
            Thread.sleep(DELAY);
        } catch(Exception e){}
        writersCount--;
        System.out.println("WRITER: "+writerId+" stopped writing");
        DB.up();

    }

}
class Reader extends Thread{
    private int readerId;
    private DataBase database;

    Reader(int readerId, DataBase database){
        this.readerId = readerId;
        this.database = database;
    }

    @Override
    public void run(){
        while(true){
            this.database.read(this.readerId);
            int DELAY = (int)(Math.random() * 5000);
            try{
                Thread.sleep(DELAY);
            } catch(Exception e){}
        }
    }
}
class Writer extends Thread{
    private int writerId;
    private DataBase database;

    Writer(int writerId, DataBase database){
        this.writerId = writerId;
        this.database = database;
    }

    @Override
    public void run(){
        while(true){
            this.database.write(this.writerId);
            int DELAY = (int)(Math.random() * 5000);
            try{
                Thread.sleep(DELAY);
            } catch(Exception e){}
        }
    }
}