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
            while((S=br.readLine())!=null){
                String s[] = S.split(" ");
                List<Integer> temp = new ArrayList<>();
                for(int j=0;j<s.length;j++){
                    temp.add(Integer.parseInt(s[j]));
                }
                list.add(new ArrayList<>(temp));
            }
            HashBasedApriori();
            ot.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    static void HashBasedApriori(){
        Set<Integer> set = new HashSet<>();
        for(List<Integer> x:list){
            for(int y:x){
                set.add(y);
            }
        }
        List<List<Integer>> C = new ArrayList<>();
        for(Integer x:set){
            C.add(new ArrayList<>(List.of(x)));
        }
        
        List<List<Integer>> L = new ArrayList<>();
        List<List<Integer>> result = new ArrayList<>();
        int k = 1;
        Map<Integer, Double> mapCtoSup = new HashMap<>();
        for(List<Integer> x:C)
            result.add(x);
        while(C.size()!=0 ){
            int counter = 0;
            for(List<Integer> x:C){
                double sup = support(x);
                mapCtoSup.put(counter++, sup);
                if(sup >= (minimumSupport*1.0)/9.0){
                    L.add(x);
                }
            }

            counter=0;
            ot.println("C"+k+" is - ");
            
            for(List<Integer> x : C){
                for(int y : x){
                    ot.print(y + " ");
                }
                ot.print(" with support "+mapCtoSup.get(counter++));
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
            
            if(k<=2){
                List<List<List<Integer>>> hashTable = new ArrayList<>();
                int tableSize = 7;
                int sizeOfBuckets[] = new int[tableSize];
                for(int i=0;i<tableSize;i++)
                    hashTable.add(new ArrayList<>());
                for(int i=0;i<L.size()-1;i++){
                    for(int j=i+1;j<L.size();j++){
                        if(checkPrefix(L.get(i),L.get(j), k-1) && checkPruning(L.get(i),L.get(j), L)){
                            int hashValue;
                            List<Integer> temp = new ArrayList<>(L.get(i));
                            temp.add(L.get(j).get(L.get(j).size()-1));
                            hashValue = Hash(temp);
                            int supportcount = supportCount(temp);
                            if(supportcount>0){
                                hashTable.get(hashValue).add(new ArrayList<>(temp));
                                sizeOfBuckets[hashValue]+=supportCount(temp);
                            }     
                        }
                       
                    }
                }
              
                for(int i=0;i<tableSize;i++){
                    if(sizeOfBuckets[i]>=minimumSupport){
                        for(List<Integer> x:hashTable.get(i)){
                            C_Kplus1.add(new ArrayList<>(x));
                        }
                    } 
                }
                
                Collections.sort(C_Kplus1,
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
            } else{
                for(int i=0;i<L.size()-1;i++){
                    for(int j=i+1;j<L.size();j++){
                        if(checkPrefix(L.get(i),L.get(j), k-1) && checkPruning(L.get(i),L.get(j), L)){
                            List<Integer> temp = new ArrayList<>(L.get(i));
                            temp.add(L.get(j).get(L.get(j).size()-1));
                            C_Kplus1.add(new ArrayList<>(temp));
                        }
                    }
                }
            }
            for(List<Integer> x:L){
                result.add(x);
            }
            L.clear();
            C=C_Kplus1;
            System.out.println();
            
            k++;
        }
    }   

    static boolean checkPrefix(List<Integer> l1, List<Integer> l2,int k){
        for(int i=0;i<k;i++){
            if(l1.get(i)!=l2.get(i)) 
                return false;
        }
        return true;
    }
    static boolean checkPruning(List<Integer> l1, List<Integer> l2, List<List<Integer>> L){
        List<Integer> temp = new ArrayList<>(l1);
        temp.add(l2.get(l2.size()-1));
        subsets=new ArrayList<>();
        findSubsets(temp, temp.size() - 1);
        for(List<Integer> subset:subsets){
            
            boolean flag = false;
            for(List<Integer> listL:L){
                if(checkIfExists(subset, listL)){
                    flag=true;
                    break;
                }
            }
            if(!flag)
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
    static int supportCount(List<Integer> itemSet){
        int count = 0;
        for(List<Integer> tempList : list){
            if(checkIfExists(itemSet, tempList)){
                count++;
            }
        }
        return count;
    }
    static boolean checkIfExists(List<Integer> l1, List<Integer> l2){
        int i = 0, j = 0;
        while(i<l1.size() && j<l2.size()){
            if(l1.get(i) == l2.get(j))
                i++;
            j++;
        }
        return i == l1.size();

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

    static void printDatasetToFile(){
        ot.println("Dataset -");
        for(List<Integer> x : list){
            for(int y : x){
                ot.print(y + " ");
            }
            ot.println();
        }
    }

    static int Hash(List<Integer> list){
        int count = 0;
        if(list.size()==2){
            count = list.get(0)*10 + list.get(1);
        } else if(list.size() == 3){
            count = list.get(0)*10 + list.get(1)*5 + list.get(2);
        }
        
        return count%7;
    }
    static List<List<Integer>> subsets;
    static List<List<Integer>> list;
    static int minimumSupport = 3;
}
