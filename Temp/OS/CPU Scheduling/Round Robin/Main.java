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
        
        RR();
 
        printProcesses();
 
        computeAvgTimes();

        ot.close();
        br.close();
    }
    static void RR(){
        Queue<Process> q = new LinkedList<>();
        double curTime = Math.max(0, listOfProcesses.get(0).at);
        double timeQuantum = 2;
        int index = 0;

        while (index < numberOfProcesses &&
            curTime >= listOfProcesses.get(index).at)
            q.add(listOfProcesses.get(index++));
        
        while(!q.isEmpty()){
            Process process = q.poll();
            Process toBeAdded = null;
            double curTimeRec = curTime;
            if(process.bt <= timeQuantum){
                curTime+=process.bt;
                process.bt = 0;
                process.ct = curTime;
                process.tat = process.ct - process.at;
                process.wt = process.tat - process.btCopy;
            } else{
                curTime+=timeQuantum;
                process.bt -= timeQuantum;
                toBeAdded = process;
            }
            ot.println(process.pid+" ( "+curTimeRec+" <----> "+curTime+" ) ");
            while(index < numberOfProcesses &&
                curTime >= listOfProcesses.get(index).at)
                q.add(listOfProcesses.get(index++));
            if(toBeAdded!=null)
                q.add(toBeAdded);
            if(q.isEmpty()&&index<numberOfProcesses){
                curTime = listOfProcesses.get(index).at;
                q.add(listOfProcesses.get(index++));
            }
        }

    }
    static void printProcesses(){
        for(Process process : listOfProcesses)
            ot.println(process);
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
        double btCopy;
        Process(int pid, double at, double bt){
            this.pid = pid;
            this.at = at;
            this.bt = bt;
            this.btCopy = bt;
        }
        public String toString(){
            return "{PID: "+pid+", AT: "+at+", BT: "+btCopy+
                ", CT: "+ct+", TAT: "+tat+", WT: "+wt+"}";
            
        }
    }
}