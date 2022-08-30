import java.io.*;
import java.net.*;

public class Server{
    public static void main(String args[]){
        try{
            Server server = new Server(Integer.parseInt("12000"));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ot = new PrintWriter(socket.getOutputStream());
            
            while(true){
                String s = server.read();
                if(s==null||s.equals("stop"))
                    break;
                String res = server.find(s);

                server.write(res);
            }
            server.close();
            ot.close();
            br.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    static Socket socket;
    static ServerSocket server;
    static BufferedReader br;
    static PrintWriter ot;

    static String find(String input){
        String ans;
        try{
            int n = Integer.parseInt(input);
            if(isPrime(n)){
                ans = n + " is a prime number";
            } else{
                ans = n + " is a composite (non-prime) number";
            }
        } catch (Exception e){
            ans = "Enter a valid number";
        }
        return ans;
    }
    static void write(String ans){
        ot.println(ans);
        ot.flush();
    }
    static String read(){
        String input=new String();
        try{
            input = br.readLine();
            System.out.println("Client Entered: " + input);
            return input;
            
        } catch(Exception e){
            e.printStackTrace();
        }       
        return input;
    }
    static void close(){
        try{
            System.out.println("Closing the connection");
            socket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public Server(int port){
        try{

            server = new ServerSocket(port);

            System.out.println("Server started at port: "+port);
            System.out.println("Waiting for a client to connect....");
            socket = server.accept();
            System.out.println("Client accepted");
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