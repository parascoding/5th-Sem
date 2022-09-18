import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
    public static void main(String args[]){
        try{
            Server server = new Server(Integer.parseInt(args[0]));
            
            while(true){
                Socket s = null;
                try{
                    s = serverSocket.accept();

                    System.out.println("New client got connected");
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter ot = new PrintWriter(s.getOutputStream());
                    System.out.println("Assigning new thread for this client");

                    Thread thread = new ClientHandler(br, ot, s);

                    thread.start();

                } catch(Exception e){
                    s.close();
//                    e.printStackTrace();
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    static ServerSocket serverSocket;
    static BufferedReader br;
    static PrintWriter ot;

    
    public Server(int port){
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server started at port: "+port);
            System.out.println("Waiting for a client to connect....");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
}
class ClientHandler extends Thread{
    static BufferedReader br;
    static PrintWriter ot;
    static Socket socket;
    public ClientHandler(BufferedReader br, PrintWriter ot, Socket socket){
        try{
            this.br = br;
            this.ot = ot;
            this.socket = socket;
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        while(true){
            try{
                String s = read();
                if(s==null||s.equals("stop"))
                    break;
                String res = find(s);

                write(res);
            } catch(Exception e){
                e.printStackTrace();
                break;
            }
        }
        try{
            br.close();
            ot.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    static String find(String input){
        String ans;
        try{
            String s[] = input.split(" ");
            int n = Integer.parseInt(s[0]);
            int m = Integer.parseInt(s[1]);
            if(isAgreeable(n, m)){
                ans = n + " "+m+" are agreaable numbers";
            } else{
                ans = n + " "+m+" are not agreaable numbers";
            }
        } catch (Exception e){
            ans = "Enter valid numbers";
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
    static boolean isAgreeable(int n, int m){
        List<Integer> divisorsOfN = findDivisors(n);
        List<Integer> divisorsOfM = findDivisors(m);

        int sumOfDivisorsOfN = findSum(divisorsOfN);

        int sumOfDivisorsOfM = findSum(divisorsOfM);
	
        return sumOfDivisorsOfN == m && sumOfDivisorsOfM == n;
    }
    static List<Integer> findDivisors(int n){
        List<Integer> list = new ArrayList<>();

        for (int i = 1; i * i <= n; i++){
            if (n % i == 0){
                list.add(i);

                if((n / i) != i && (n / i) != n)
                    list.add(n / i);
            }
        }
        return list;
    }
    static int findSum(List<Integer> list){
        int sum = 0;
        for(int x : list)
            sum += x;

        return sum;
    }
}
