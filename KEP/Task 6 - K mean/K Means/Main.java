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
            
            
            int k = 3;
            List<List<Double>> clusters = Kmeans(k);
            ot.println(clusters);
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
                List<Double> temp = new ArrayList<>();
                for(int j=0;j<s.length;j++){
                    temp.add(Double.parseDouble(s[j]));
                }
                list.add(new ArrayList<>(temp));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    static List<List<Double>> Kmeans(int k){
        int numberOfFeatures = list.get(0).size();
        List<List<Double>> centroids = pickKRandomPoints(k);
        // System.out.println("CENTROIDS: "+centroids);
        List<List<Double>> newCentroids = new ArrayList<>(centroids);
        int count = 0;
        System.out.println(centroids);
        while(count == 0 || checkIfCentroidsChange(centroids, newCentroids)){
            
            count++;
            System.out.println("OLD: "+centroids+" NEW:"+newCentroids);
            centroids = new ArrayList<>(newCentroids);
            newCentroids = assignClusters(newCentroids);
            System.out.println("NEW: "+newCentroids);
            
        }
        return newCentroids;
    }
    static boolean checkIfCentroidsChange(List<List<Double>> previousCentroids, List<List<Double>> currentCentroids){
        // System.out.println("PREV: "+previousCentroids+",  CUR: "+currentCentroids);
        for(int i = 0; i < previousCentroids.size(); i++){
            for(int j = 0; j < previousCentroids.get(i).size(); j++){
                if(Double.compare(previousCentroids.get(i).get(j), currentCentroids.get(i).get(j)) != 0){
                    // System.out.println("MISMATCH: "+previousCentroids.get(i).get(j)+", "+currentCentroids.get(i).get(j));
                    return true;
                }
            }
        }
        return false;
    }
    static List<List<Double>> assignClusters(List<List<Double>> centroids){
        int centroidNumber[] = new int[list.size()];
        int i = 0;
        for(List<Double> item : list){
            double min = Double.MAX_VALUE;
            int ind = -1;
            int j = 0;
            for(List<Double> centroid : centroids){
                double dist = calculateDistanceFromCentroid(centroid, item);;
                if(dist < min){
                    min = dist;
                    ind = j;
                }
                j++;
            }
            centroidNumber[i++] = ind;
        }
        List<List<Double>> redefinedClusters = findNewClusters(centroidNumber, centroids.size(), centroids.get(0).size());
        return redefinedClusters;
    }
    static List<List<Double>> findNewClusters(int centroidNumber[], int k, int d){
        // System.out.println(k+" "+d);
        List<List<Double>> centroids = new ArrayList<>();
        for(int i = 0; i < k; i++){
            centroids.add(new ArrayList<>());
            for(int j = 0; j < d; j++)
                centroids.get(i).add(0.0);
        }
        for(int i = 0; i < centroidNumber.length; i++){
            int number = centroidNumber[i];
            for(int j = 0; j < d; j++){
                centroids.get(number).set(j, centroids.get(number).get(j) + list.get(i).get(j));
            }
        }
        for(int i = 0; i < k; i++){
            for(int j = 0; j < d; j++)
                centroids.get(i).set(j, centroids.get(i).get(j) / list.size());
        }
        return centroids;
    }
    static double calculateDistanceFromCentroid(List<Double> centroid, List<Double> item){
        double dist = 0.0;
        for(int i = 0; i < centroid.size(); i++){
            dist += Math.pow(centroid.get(i) - item.get(i), 2);
        }
        return Math.sqrt(dist);
    }
    static List<List<Double>> pickKRandomPoints(int k){
        Set<Integer> set = new HashSet<>();
        while(set.size() != k){
            int temp = (int)(Math.random() * list.size());
            set.add(temp);
        }
        List<List<Double>> ret = new ArrayList<>();
        for(int x : set)
            ret.add(new ArrayList<>(list.get(x)));
        return ret;
    }
    static List<List<Double>> list;
}