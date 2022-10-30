import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
public class Main{
    public static void main(String args[]) throws Exception{
        try{
            int n = 5;
            int m = 5;
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
    private int size;
    private int curEle;
    private Item[] items;
    Buffer(int size){
        try{
            this.size = size;
            this.items = new Item[size];
            this.curEle = 0;
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void produce(int producerId, Item item){
        try{
            synchronized(this){
                while(curEle == size){
                    this.wait();
                }
                items[in] = item;
                in = (in + 1) % size;
                curEle++;
                System.out.println("PRDOCUER: "+producerId+
                                " produced ITEM: "+item+" UPDATED IN: "+in);
                this.notifyAll();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void consume(int consumerId){
        try{
            synchronized(this){
                while(curEle == 0){
                    this.wait();
                }
                Item item = items[out];
                out = (out + 1) % size;
                curEle--;
                System.out.println("CONSUMER: "+consumerId+
                                " consumed ITEM: "+item+" UPDATED OUT: "+out);
                this.notifyAll();
            }
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