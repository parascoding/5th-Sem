import java.util.*;
import java.io.*;
public class Main{
    static BufferedReader br;
    static PrintWriter ot;
    public static void main(String args[]) throws IOException{
        br = new BufferedReader(new FileReader("input2.txt"));
        ot = new PrintWriter(System.out);
        listOfProcesses = new ArrayList<>();
        numberOfProcesses = Integer.parseInt(br.readLine().trim());
        
        getInput();

        sortProcessesBasedOnAT();
        
        SJF();

        sortProcessesBasedOnCT();
        printProcesses();

        computeAvgTimes();

        
        ot.close();
        br.close();
    }
    static void SJF(){
        PriorityQueue<Process> pq = new PriorityQueue<>(
            new Comparator<Process>(){
                public int compare(Process p1, Process p2){
                    if(p1.bt == p2.bt){
                        if(p1.at > p2.at)
                            return 1;
                        return -1;
                    }
                    if(p1.bt > p2.bt)
                        return 1;
                    return -1;
                }
            }
        );

        double curTime = listOfProcesses.get(0).at;

        int index = 0;
        while(index < numberOfProcesses &&
                curTime >= listOfProcesses.get(index).at){
            pq.add(listOfProcesses.get(index++));
        }

        while(!pq.isEmpty()){
            Process process = pq.remove();
            curTime = Math.max(curTime, process.at);
            process.ct = curTime + process.bt;
            process.tat = process.ct - process.at;
            process.wt = process.tat - process.bt;
            curTime += process.bt;
            while(index < numberOfProcesses &&
                    curTime >= listOfProcesses.get(index).at){
                pq.add(listOfProcesses.get(index++));
            }
        }
    }
    static void printProcesses(){
        for(Process process : listOfProcesses)
            ot.println(process);
    }
    static void sortProcessesBasedOnAT(){
        Collections.sort(listOfProcesses,
            new Comparator<Process>(){
                public int compare(Process p1, Process p2){
                    if(p1.at == p2.at){
                        return p1.pid - p2.pid;
                    }
                    if(p1.at > p2.at)
                        return 1;
                    return -1;
                }
            }
        );
    }
    static void sortProcessesBasedOnCT(){
        Collections.sort(listOfProcesses,
            new Comparator<Process>(){
                public int compare(Process p1, Process p2){
                    // if(p1.at == p2.at){
                    //     return p1.pid - p2.pid;
                    // }
                    if(p1.ct > p2.ct)
                        return 1;
                    return -1;
                }
            }
        );
    }
    static void computeAvgTimes(){
        double tat=0, wt=0;
        for(Process process : listOfProcesses){
            tat += process.tat;
            wt += process.wt;
        }
        tat/=numberOfProcesses;
        wt/=numberOfProcesses;
        ot.println("Avergae TAT: "+tat+", Average WT: "+wt);
        
    }
    static void getInput(){
        try{
            for(int i = 0; i < numberOfProcesses; i++){
                String s[] = br.readLine().trim().split(" ");

                listOfProcesses.add(new Process(
                    Integer.parseInt(s[0]),
                    Double.parseDouble(s[1]),
                    Double.parseDouble(s[2])
                ));
            }
        } catch(Exception e){
            e.printStackTrace();
            return;
        }

    }
    static List<Process> listOfProcesses;
    static int numberOfProcesses;
    static class Process{
        int pid;
        double at, bt, ct, wt, tat;
        
        Process(int pid, double at, double bt){
            this.pid = pid;
            this.at = at;
            this.bt = bt;
        }
        public String toString(){
            return "{PID: "+pid+", AT: "+at+", BT: "+bt+
                ", CT: "+ct+", TAT: "+tat+", WT: "+wt+"}";
            
        }
    }
}