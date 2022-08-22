import java.io.*;
import java.net.*;

public class Server{
    public static void main(String args[]){
        Server server = new Server(Integer.parseInt(args[0]));
    }
    static Socket socket;
    static ServerSocket server;
    static BufferedReader br;
    static PrintWriter ot;

    public Server(int port){
        try{

            server = new ServerSocket(port);

            System.out.println("Server started at port: "+port);
            System.out.println("Waiting for a client to connect....");
            socket = server.accept();
            System.out.println("Client accepted");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ot = new PrintWriter(socket.getOutputStream());

            String input = "";

            while (input != null || !input.equals("stop")){
                try{
                    input = br.readLine();
                    System.out.println("Client Entered: " + input);
                    String ans = "";
                    try{
                        int n = Integer.parseInt(input);
                        if(isPrime(n)){
                            ans = n + " is a prime number";
                        } else{
                            ans = n + " is a composite (non-prime) number";
                        }
                    } catch (Exception e){
                        ans = "Enter a valid number";
                        // e.printStackTrace();
                    }
                    ot.println(ans);
                    ot.flush();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            System.out.println("Closing the connection");
            socket.close();
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    static boolean isPrime(int n){
        for(int i=2;i*i<=n;i++){
            if(n%i==0) return false;
        }
        return true;
    }
}