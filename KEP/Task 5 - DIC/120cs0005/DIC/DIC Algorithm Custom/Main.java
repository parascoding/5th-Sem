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
            String S;
            int max = 0;
            while((S=br.readLine())!=null){
                String s[] = S.split(" ");
                List<Integer> temp = new ArrayList<>();
                for(int j=0;j<s.length;j++){
                    temp.add(Integer.parseInt(s[j]));
                }
                list.add(new ArrayList<>(temp));
                max = Math.max(max, temp.size());
            }
            DIC(max);
            ot.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    static void DIC(int max){
        int M = 1000;
        int k = 0;
        map = new HashMap<>();
        finalAns = new ArrayList<>();
        int part[]=new int[7];
        for(int i=0;i<7;i++)
            part[i]=i;
        int i=0;
        while(min(part)<=max){
            if(i == 0 || i%M==0){
                k=(k+1)%7;
                if(k==0)
                    k=1;
                part[k]++;
                print(map);
                map = new HashMap<>();
            }
            subsets = new ArrayList<>();
            findSubsets(list.get(i), part[k]);
            for(List<Integer> x:subsets){
                String s = findString(x);
                map.put(s, map.getOrDefault(s, 0)+1);
            }
            i = (i+1)%list.size();
        }
        print(map);

        Collections.sort(finalAns,
            new Comparator<List<Integer>>(){
                public int compare(List<Integer> l1, List<Integer> l2){
                    if(l1.size()==l2.size())
                        return l1.get(0) - l2.get(0);
                    return l1.size()-l2.size();
                }
            }
        );      

        printList(finalAns);
    }
    static void print(Map<String, Integer> map){
        for(Map.Entry<String, Integer> e : map.entrySet()){
            if(e.getValue()<10)
                continue;
            List<Integer> temp = new ArrayList<>();
            stringToInteger(e.getKey(), temp);
            temp.add(e.getValue());
            finalAns.add(new ArrayList<>(temp));
        }
    }
    static void stringToInteger(String s, List<Integer> temp){
        String ss[]=s.split(" ");
        for(String x:ss)
            temp.add(Integer.parseInt(x));
    }
    static void printList(List<List<Integer>> list){
        for(List<Integer> x:list){
            for(int i=0;i<x.size();i++)
                ot.print(x.get(i)+" ");

            ot.println("Support Count: "+x.get(x.size()-1));
        }
    }
    static String findString(List<Integer> list){
        StringBuilder sb = new StringBuilder();
        for(int x:list)
            sb.append(x).append(" ");
        return sb.toString();
    }
    static void findSubsets(List<Integer> list, int k){
        findSubsetsUtil(list, 0, k, new ArrayList<>());
    }
    static void findSubsetsUtil(List<Integer> list, int ind, int k, List<Integer> temp){
        if(temp.size() > k) return;
        if(ind==list.size()){
            if(temp.size() != k) return;
            subsets.add(new ArrayList<>(temp));
            return;
        }
        findSubsetsUtil(list, ind+1, k, temp);
        temp.add(list.get(ind));
        findSubsetsUtil(list, ind+1, k, temp);
        temp.remove(temp.size()-1);
    }
    static int min(int a[]){
        int Min = Integer.MAX_VALUE;
        for(int i=1;i<a.length;i++)
            Min = Math.min(Min, a[i]);
        return Min;
    }
    static Map<String, Integer> map;
    static List<List<Integer>> subsets;
    static List<List<Integer>> list;
    static List<List<Integer>> finalAns;
}
