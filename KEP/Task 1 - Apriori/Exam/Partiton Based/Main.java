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

            int partitionSize = 300;
            List<List<Integer>> finalRes = new ArrayList<>();

            int partitionNumber=1;
            for(int i=0;i<list.size();i+=partitionSize){
                List<List<Integer>> tempAns = NormalApriori(list.subList(i, Math.min(list.size(), i+partitionSize)), partitionNumber);

                for(List<Integer> x:tempAns)
                    finalRes.add(x);

                partitionNumber++;
            }

            finalRes = removeDuplicates(finalRes);

            List<List<Integer>> finalResReal = new ArrayList<>();

            for(List<Integer> x:finalRes){
                if(support(x, list)>=0.22)
                    finalResReal.add(new ArrayList<>(x));
            }

            printList(finalResReal);

            ot.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    static List<List<Integer>> NormalApriori(List<List<Integer>> list, int partitionNumber){
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
      
        while(C.size()!=0 ){
            
            int counter = 0;
            for(List<Integer> x:C){
                double sup = support(x, list);
                mapCtoSup.put(counter++, sup);
                if(sup >= 0.22){
                    L.add(x);
                }
            }
            counter=0;
            ot.println("C"+k+" is - (For Partition : "+partitionNumber+")");
            for(List<Integer> x : C){
                for(int y : x){
                    ot.print(y + " ");
                }
                ot.print(" with support "+mapCtoSup.get(counter++));
                ot.println();
            }
            ot.println("L"+k+" is -(For Partition : "+partitionNumber+")");
            for(List<Integer> x : L){
                for(int y : x){
                    ot.print(y + " ");
                }
                ot.println();
            }

            for(List<Integer> x:L)
                result.add(x);

            List<List<Integer>> C_Kplus1 = new ArrayList<>();
            for(int i=0;i<L.size()-1;i++){
                for(int j=i+1;j<L.size();j++){
                    if(checkPrefix(L.get(i),L.get(j), k-1) && checkPruning(L.get(i),L.get(j), L)){

                        List<Integer> temp = new ArrayList<>(L.get(i));

                        temp.add(L.get(j).get(L.get(j).size()-1));

                        C_Kplus1.add(new ArrayList<>(temp));

                    }
                }
            }

            for(List<Integer> x:C_Kplus1){
                result.add(x);
            }

            L.clear();

            C=C_Kplus1;

            k++;
        }
        
        return result;
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

    static double support(List<Integer> itemSet, List<List<Integer>> list){
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

    static List<List<Integer>> removeDuplicates(List<List<Integer>> res){
        List<List<Integer>> ans = new ArrayList<>();
        Collections.sort(res,
            new Comparator<List<Integer>>(){
                public int compare(List<Integer> x, List<Integer> y){
                    if(x.size()!=y.size()){
                        return x.size()-y.size();
                    }
                    for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
                        if (x.get(i) != y.get(i)) {
                            return x.get(i) - y.get(i);
                        }
                    }
                    return x.size() - y.size();
                }
            }
        );    

        ans.add(new ArrayList<>(res.get(0)));

        for(int i=1;i<res.size();i++){
            if(check(res.get(i), ans.get(ans.size()-1)))
                ans.add(new ArrayList<>(res.get(i)));
        }
        return ans;
    }

    static boolean check(List<Integer> l1, List<Integer> l2){
        if(l1.size()!=l2.size()) return true;
        for(int i=0;i<l1.size();i++)
            if(l1.get(i)!=l2.get(i))
                return true;
        return false;
    }

    static void printList(List<List<Integer>> list){
        ot.println("The Frequent Itemsets (Global) are -");
        for(List<Integer> x : list){
            for(int y : x)
                ot.print(y+" ");
            ot.println();
        }
    }

    static List<List<Integer>> subsets;
    static List<List<Integer>> list;
}
