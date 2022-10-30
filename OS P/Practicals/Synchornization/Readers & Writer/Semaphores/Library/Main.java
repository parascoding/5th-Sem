import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
public class Main{
    public static void main(String args[]) throws Exception{
        try{
            int n = 2;
            int m = 4;
            DataBase database = new DataBase();
            for(int i = 0; i < n; i++)
                new Reader(i, database).start();
            for(int i = 0; i < m; i++)
                new Writer(i, database).start();
        } catch(Exception e){
            e.printStackTrace();
        }
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
        try{
            mutex.acquire();
            readersCount++;
            if(readersCount == 1){
                DB.acquire();
            }
            mutex.release();

            System.out.println("READER: "+readerId+" started reading");
            int DELAY = (int)(Math.random() * 5000);
        
            Thread.sleep(DELAY);
        

            System.out.println("READER: "+readerId+" stopped reading");

            mutex.acquire();
            readersCount--;
            if(readersCount == 0){
                DB.release();
            }
            mutex.release();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void write(int writerId){
        try{
            DB.acquire();
            writersCount++;
            System.out.println("WRITER: "+writerId+" started writing");
            int DELAY = (int)(Math.random() * 5000);
            
            Thread.sleep(DELAY);
            
            writersCount--;
            System.out.println("WRITER: "+writerId+" stopped writing");
            DB.release();
        } catch(Exception e){
            e.printStackTrace();
        }

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