import java.util.*;
import java.io.*;
public class Main{
    static BufferedReader br;
    static PrintWriter ot;
    public static void main(String args[]) throws IOException{
        try{
            br = new BufferedReader(new FileReader("input.txt"));
            ot = new PrintWriter(new FileWriter("output.txt"));
            list = new ArrayList<>();

            for(int i=0;i<4;i++){
                String s[] = br.readLine().trim().split(" ");
                List<Integer> temp = new ArrayList<>();
                for(int j=0;j<s.length;j++){
                    temp.add(Integer.parseInt(s[j]));
                }
                list.add(new ArrayList<>(temp));
            }
            // System.out.println(list);
            generateSets();
            // printDatasetToFile();
            ot.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    static void generateSets(){
        List<List<Integer>> C = new ArrayList<>();
        for(int i=1;i<=5;i++){
            C.add(new ArrayList<>(List.of(i)));
        }
        List<List<Integer>> L = new ArrayList<>();
        List<List<Integer>> result = new ArrayList<>();
        int k = 1;
        for(List<Integer> x:C)
            result.add(x);
        while(C.size()!=0 ){
            for(List<Integer> x:C){
                if(support(x) >= 0.5){
                    L.add(x);
                }
            }
            ot.println("C"+k+" is - ");
            for(List<Integer> x : C){
                for(int y : x){
                    ot.print(y + " ");
                }
                ot.println();
            }
            ot.println("L"+k+" is - ");
            for(List<Integer> x : L){
                for(int y : x){
                    ot.print(y + " ");
                }
                ot.println();
            }
            List<List<Integer>> C_Kplus1 = new ArrayList<>();
            for(int i=0;i<L.size()-1;i++){
                for(int j=i+1;j<L.size();j++){
                    if(checkPrefix(L.get(i),L.get(j), k-1)){
                        List<Integer> temp = new ArrayList<>(L.get(i));
                        temp.add(L.get(j).get(L.get(j).size()-1));
                        C_Kplus1.add(new ArrayList<>(temp));
                    }
                }
            }
            for(List<Integer> x:C_Kplus1){
                result.add(x);
            }
            // ot.println("Result at current level -");
            // for(List<Integer> x : result){
            //     for(int y : x){
            //         ot.print(y + " ");
            //     }
            //     ot.println();
            // }
            System.out.println(C_Kplus1);
            L.clear();
            C=C_Kplus1;
            System.out.println();
            ot.println();
            k++;
        }
            ot.println("Final Result -");
            for(List<Integer> x : result){
                for(int y : x){
                    ot.print(y + " ");
                }
                ot.println();
            }
            ot.println();
        }   

    static boolean checkPrefix(List<Integer> l1, List<Integer> l2,int k){
        // System.out.println(l1+" "+l2+" "+k);
        for(int i=0;i<k;i++){
            if(l1.get(i)!=l2.get(i)) 
                return false;
        }
        return true;
    }
    static double support(List<Integer> itemSet){
        double count = 0;
        for(List<Integer> tempList : list){
            if(checkIfExists(itemSet, tempList)){
                count++;
            }
        }
        return count/list.size();
    }
    static boolean checkIfExists(List<Integer> l1, List<Integer> l2){
        int i = 0, j = 0;
        while(i<l1.size() && j<l2.size()){
            if(l1.get(i) == l2.get(j))
                i++;
            j++;
        }
        // System.out.println(l1+" "+l2+" "+i+" "+j);
        return i == l1.size();

    }

    static void printDatasetToFile(){
        ot.println("Dataset -");
        for(List<Integer> x : list){
            for(int y : x){
                ot.print(y + " ");
            }
            ot.println();
        }
    }

    static List<List<Integer>> list;
}