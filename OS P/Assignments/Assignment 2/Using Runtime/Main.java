import java.util.*;

public class Main{
    public static void main(String args[]) throws Exception{
        try{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("gedit");
            // process.waitFor(5, TimeUnit.SECONDS);
            process.destroy();

        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
}