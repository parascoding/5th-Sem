import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
public class Main{
    public static void main(String args[]) throws Exception{
        try{
            int n = 2;
            int m = 2;
            Buffer buffer = new Buffer(5);
            for(int i = 0; i < n; i++)
                new Producer(i, buffer).start();
            for(int i = 0; i < m; i++)
                new Consumer(i, buffer).start();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}


class Buffer{
    volatile private int in;
    volatile private int out;
    Semaphore empty, full, mutex;
    private int size;
    private Item[] items;
    Buffer(int size){
        try{
            this.size = size;
            empty = new Semaphore(size);
            full = new Semaphore(0);
            mutex = new Semaphore(1);
            this.items = new Item[size];
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void produce(int producerId, Item item){
        try{
            empty.acquire();
            mutex.acquire();
            items[in] = item;
            in = (in + 1) % size;
            System.out.println("PRDOCUER: "+producerId+
                            " produced ITEM: "+item+" UPDATED IN: "+in);
            mutex.release();
            full.release();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void consume(int consumerId){
        try{
            full.acquire();
            mutex.acquire();
            Item item = items[out];
            out = (out + 1) % size;
            System.out.println("CONSUMER: "+consumerId+
                            " consumed ITEM: "+item+" UPDATED OUT: "+out);
            mutex.release();
            empty.release();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    

}
class Producer extends Thread{
    int producerId;
    Buffer buffer;
    Producer(int producerId, Buffer buffer){
        this.producerId = producerId;
        this.buffer = buffer;
    }
    @Override
    public void run(){
        try{
            while(true){
                int DELAY = (int)(Math.random() * 1000);
                Thread.sleep(DELAY);
                this.buffer.produce(this.producerId, new Item((int)(Math.random() * 100)));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Consumer extends Thread{
    int consumerId;
    Buffer buffer;
    Consumer(int consumerId, Buffer buffer){
        this.consumerId = consumerId;
        this.buffer = buffer;
    }
    @Override
    public void run(){
        try{
            while(true){
                int DELAY = (int)(Math.random() * 1000);
                Thread.sleep(DELAY);
                this.buffer.consume(this.consumerId);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Item{
    private int itemId;
    Item(int itemId){
        this.itemId = itemId;
    }
    @Override
    public String toString(){
        return this.itemId+"";
    }
}