import java.util.*;
import java.io.*;
public class Main{
    public static void main(String args[]) throws IOException{
        List<Set<Integer>> setList = new ArrayList<>();
        setList.add(new HashSet<>(new ArrayList<>(List.of(11, 12, 15))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(12, 14))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(12, 13))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(11, 12, 14))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(11, 13))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(12, 13))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(11, 13))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(11, 12, 13, 15))));
        setList.add(new HashSet<>(new ArrayList<>(List.of(11, 12, 13))));
        List<Integer> items = new ArrayList<>(List.of(11,12,13,14,15));
        
        subSets = new ArrayList();
        findSubSets(items, 0, new ArrayList<>());
        Collections.sort(subSets, 
            new Comparator<List<Integer>>(){
                public int compare(List<Integer> x, List<Integer> y){
                    if(x.size()==y.size()){
                        for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
                            if (x.get(i) != y.get(i)) {
                                return x.get(i) - y.get(i);
                            }
                        }
                    }
                    return x.size()-y.size();
                }
            }
        );
        double sizeOfDataset = setList.size()*1.0;
        for(List<Integer> list : subSets){
            int count = 0;
            for(Set<Integer> hs : setList){
                boolean bool = true;
                for(int item : list){
                    if(!hs.contains(item)){
                        bool = false;
                    }
                }
                if(bool) count++;
            }
            System.out.println("Set = "+list);
            System.out.println("Occurence = " + count +
            "\nSupport = " + (count/sizeOfDataset));
            System.out.println();
        }
        
    }
    static void findSubSets(List<Integer> items, int ind, List<Integer> tempSet){
        if(ind>=items.size()){
            if(tempSet.size()>0)
            subSets.add(new ArrayList<>(tempSet));
            return;
        }
        findSubSets(items, ind+1, tempSet);
        tempSet.add(items.get(ind));
        findSubSets(items, ind+1, tempSet);
        tempSet.remove(tempSet.size()-1);
    }
    static List<List<Integer>> subSets;
}