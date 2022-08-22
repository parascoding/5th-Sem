// A Java program for a Server
import java.net.*;
import java.io.*;
 
public class Server{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    private DataOutputStream out     = null;
    // constructor with port
    public Server(int port){
        // starts server and waits for a connection
        try{
            server = new ServerSocket(port);
            System.out.println("Server started");
 
            System.out.println("Waiting for a client ...");
 
            socket = server.accept();
            System.out.println("Client accepted");
 
            // takes input from the client socket
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
            String line = "";
 
            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println("Client Entered "+line);
                    String ans = "";
                    try{
                        int n = Integer.parseInt(line);
                        if(isPrime(n)){
                            ans = n + " is a prime number";
                        } else{
                            ans = n + " is a composite (non-prime) number";
                        }
                    } catch (Exception e){
                        ans = "Enter a valid number";
                        e.printStackTrace();
                    }
                    
                    out.writeUTF(ans);
                    out.flush();
 
                }
                catch(IOException i)
                {
                    System.out.println("Client Closed connection");
                    break;
                }
            }
            System.out.println("Closing connection");
 
            // close connection
            socket.close();
            in.close();
        } catch(IOException i){
            System.out.println(i);
        }
    }
    static boolean isPrime(int n){
        for(int i=2;i*i<=n;i++){
            if(n%i==0) return false;
        }
        return true;
    }
    public static void main(String args[]){
        Server server = new Server(11000);
    }
}