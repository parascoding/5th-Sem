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
        sb = new StringBuilder();

        getInput();

        sortProcessesBasedOnAT();
        
        PriorityBased();

        sortProcessesBasedOnCT(); 

        printProcesses();

        ot.println(sb);
        
        computeAvgTimes();

        ot.close();
        br.close();
    }
    static void PriorityBased(){
        PriorityQueue<Process> pq = new PriorityQueue<>(
            new Comparator<Process>(){
                public int compare(Process p1, Process p2){
                    if(p1.priority == p2.priority){
                        if(p1.at > p2.at)
                            return 1;
                        return -1;
                    }
                    if(p1.priority > p2.priority)
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
            Process process = pq.peek();
            double curTimeCopy = curTime;
            curTime = Math.max(curTime, process.at);
            if(process.rt == Integer.MAX_VALUE)
                process.rt = curTime;
            while (process.bt  > 0 &&
                    pq.peek().equals(process)){
                process.bt--;
                curTime++;
                while(index < numberOfProcesses &&
                    curTime >= listOfProcesses.get(index).at){
                    pq.add(listOfProcesses.get(index++));
                }
            }
            if(process.bt == 0){
                pq.remove(process);
                process.ct = curTime;
                process.tat = process.ct - process.at;
                process.wt = process.tat - process.btCopy;
            }
            sb.append(process.pid+" = {Start: "+curTimeCopy+", End: "+curTime+"}\n");
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
    static void getInput(){
        try{
            for(int i = 0; i < numberOfProcesses; i++){
                String s[] = br.readLine().trim().split(" ");

                listOfProcesses.add(new Process(
                    Integer.parseInt(s[0]),
                    Double.parseDouble(s[1]),
                    Double.parseDouble(s[2]),
                    Integer.parseInt(s[3])
                ));
            }
        } catch(Exception e){
            e.printStackTrace();
            return;
        }

    }
    static StringBuilder sb;
    static List<Process> listOfProcesses;
    static int numberOfProcesses;
    static class Process{
        int pid;
        double at, bt, ct, wt, tat;
        double btCopy, rt;

        int priority;
        Process(int pid, double at, double bt, int priority){
            this.pid = pid;
            this.at = at;
            this.bt = bt;
            this.priority = priority;
        }
        public String toString(){
            return "{PID: "+pid+", AT: "+at+", BT: "+bt+
                ", CT: "+ct+", TAT: "+tat+", WT: "+wt+"}";
            
        }
        public boolean equals(Process p){
            return this.pid == p.pid;
        }
    }
}