
// A Java program for a Client
import java.net.*;
import java.io.*;
 
public class Client{
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
    DataInputStream serverInput;
    // constructor to put ip address and port
    public Client(String address, int port){
        // establish a connection
        try{
            socket = new Socket(address, port);
            System.out.println("Connected");
 
            // takes input from terminal
            input  = new DataInputStream(System.in);
            serverInput=new DataInputStream(socket.getInputStream());  
            // sends output to the socket
            out    = new DataOutputStream(socket.getOutputStream());
        } catch(Exception e){
            e.printStackTrace();
        }
        
 
        // string to read message from input
        String line = "";
 
        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try {
                line = input.readLine();
                out.writeUTF(line);
                out.flush();
                String s = serverInput.readUTF();
                System.out.println("Server says : " + s);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
 
        // close the connection
        try{
            input.close();
            out.close();
            socket.close();
        } catch(IOException i){
            System.out.println(i);
        }
    }
 
    public static void main(String args[]){
        Client client = new Client("127.0.0.1", 11000);
    }
}