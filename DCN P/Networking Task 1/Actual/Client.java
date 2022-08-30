// A Java program for a Client
import java.net.*;
import java.io.*;
 
 public class Client{
    public static void main(String args[]){
        Client client = new Client(args[0], Integer.parseInt(args[1]));
        int count=0;
        while(true){
            String input = client.readClient();
            if(input.equals("stop"))
                break; 
            client.writeServer(input);

            String serverInput = client.readServer();
            client.writeClient(serverInput);

        }
        client.close();
    }
    
    static Socket socket;
    static BufferedReader br;
    static BufferedReader serverReader;
    static PrintWriter ot;
    static void close(){
        try{
            br.close();
            ot.close();
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    static void writeClient(String s){
        System.out.println(s);
    }
    static String readServer(){
        String response = "";
        try{
            response = serverReader.readLine();
        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
    static void writeServer(String s){
        try{
            ot.println(s);
            ot.flush();
        } catch(Exception e){
        
            e.printStackTrace();
        }
    }
    static String readClient(){
        String s = "";
        System.out.println("Enter twp numbers separated by space to check if they are agreaable");
        try{
            s = br.readLine();
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return s;
    }
    public Client(String address, int port){
        try{
            socket = new Socket(address, port);
            System.out.println("Connected to the server");
            br = new BufferedReader(new InputStreamReader(System.in));
            ot = new PrintWriter(socket.getOutputStream());
            serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (Exception e){
            System.out.println("Can't connect with server");
            e.printStackTrace();
        }
        
    }
 }