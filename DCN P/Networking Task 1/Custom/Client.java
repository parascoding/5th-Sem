// A Java program for a Client
import java.net.*;
import java.io.*;
 
 public class Client{
    public static void main(String args[]){
        Client client = new Client(args[0], Integer.parseInt(args[1]));
    }
    static Socket socket;
    static BufferedReader br;
    static BufferedReader serverReader;
    static PrintWriter ot;

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
        
        String s = "";
        while(!s.equals("stop")){
            try{
                s = br.readLine();
                ot.println(s);
                ot.flush();
                String response = serverReader.readLine();
                System.out.println("Server says: " + response);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        try{
            br.close();
            ot.close();
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
 }