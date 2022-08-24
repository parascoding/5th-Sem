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

            takeInput();
            
            findUniqueItemsAndTheirOccurence();
            
            arrangeItemsBasedOnSupport();

            constructFPTree();

            printFPTree();

            ot.println(map+"\n"+list);
            ot.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    static void takeInput(){
        try{
            String S;
            while((S=br.readLine())!=null){
                String s[] = S.split(" ");
                List<Integer> temp = new ArrayList<>();
                for(int j=0;j<s.length;j++){
                    temp.add(Integer.parseInt(s[j]));
                }
                list.add(new ArrayList<>(temp));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    static void findUniqueItemsAndTheirOccurence(){
        map = new HashMap<>();
        for (List<Integer> x : list){
            for(int y : x){
                map.put(y, map.getOrDefault(y, 0) + 1);
            }
        }
    }
    static void arrangeItemsBasedOnSupport(){
        for(int i = 0; i < list.size(); i++){
            List<Integer> x = list.get(i);
            Collections.sort(x, 
                new Comparator<Integer>(){
                    public int compare(Integer x1, Integer x2){
                        if (map.get(x1) == map.get(x2))
                            return x1 - x2;
                        return map.get(x2) - map.get(x1);
                    }
                }
            );

            List<Integer> tempList = new ArrayList<>();
            for(int y:x){
                if(map.get(y) < 2){
                    break;
                }
                tempList.add(y);
            }

            list.set(i, new ArrayList<>(tempList));
        }
    }
    static void printBranch(Trie node){
        if(node==null) return;

        ot.print("["+node.itemId+" "+node.freq+"] ");

        for(Map.Entry<Integer, Trie> e : node.child.entrySet()){
            printBranch(e.getValue());
            // ot.println();
        }
    }
    static void printFPTree(){
        for(Map.Entry<Integer, Trie> e : root.child.entrySet()){
            printBranch(e.getValue());
            ot.println();
        }
    }
    static void insert(List<Integer> trscn){
        Trie node = root;

        for(int x : trscn){
            if (!node.child.containsKey(x)){
                node.child.put(x, new Trie());
            }
            node.child.get(x).itemId = x;
            node.child.get(x).freq++;     

            node = node.child.get(x);
        }
    }
    static void constructFPTree(){
        root = new Trie();

        for(List<Integer> x : list){
            insert(x);
        }

    }
    static Trie root;
    static Map<Integer, Integer> map;
    static List<List<Integer>> list;
    static class Trie{
        int itemId, freq;
        Map<Integer, Trie> child;

        Trie(){
            freq = 0;
            child = new HashMap<>();

        }
    }
}