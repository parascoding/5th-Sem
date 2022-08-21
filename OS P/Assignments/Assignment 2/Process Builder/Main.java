import java.io.*;
import java.util.*;
import java.lang.*;
public class Main{
    public static void main(String args[]) throws Exception{
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("gedit");
            
            Process process = processBuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while((s=br.readLine())!=null){
                System.out.println(s);
            }
            System.out.println(process.info()+" "+process.pid());
            long pid = ProcessHandle.current().pid();
            System.out.println(pid);
            process.destroy();

        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
}   