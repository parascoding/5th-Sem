import java.util.*;
import java.io.*;
public class Main{
    static BufferedReader br;
    static PrintWriter ot;
    public static void main(String args[]) throws IOException{
        try{
            br = new BufferedReader(new InputStreamReader(System.in));
            ot = new PrintWriter(new FileWriter("output.txt"));

            int n, d;
            System.out.println("Enter the vertical depth and horizontal width of the dataset repectively");
            String s[]=br.readLine().trim().split(" ");
            n = Integer.parseInt(s[0]);
            d = Integer.parseInt(s[1]);
            // n = 20;
            // d = 8; 
            List<List<Integer>> dataset = new ArrayList<>();
            for(int i=0;i<n;i++){
                int numOfItem = d/2 + (int)(Math.random()*(d/2) + 1);
                Set<Integer> hs = new TreeSet<>();
                for(int j=0;j<numOfItem;j++){
                    int item = (int)(Math.random()*d + 1);
                    hs.add(item);
                }
                dataset.add(new ArrayList<>(hs));
            }
            
            Collections.sort(dataset,
                new Comparator<List<Integer>>(){
                    public int compare(List<Integer> x, List<Integer> y){
                        for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
                            if (x.get(i) != y.get(i)) {
                                return x.get(i) - y.get(i);
                            }
                        }
                        return x.size() - y.size();
                    }
                }
            );    
            int count=0;
            for(List<Integer> x:dataset){
                for(int y:x){
                    ot.print(y+" ");
                    if(y==1)
                    count++;
                }
                ot.println();
            }
            System.out.println(count);
            ot.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
}