import java.util.*;
import java.io.*;
public class Main{
    static BufferedReader br;
    static PrintWriter ot;
    public static void main(String args[]) throws IOException{
        br = new BufferedReader(new FileReader("input.txt"));
        ot = new PrintWriter(new FileWriter("output.txt"));
        listOfProcesses = new ArrayList<>();
        numberOfProcesses = Integer.parseInt(br.readLine().trim());
        
        getInput();

        Collections.shuffle(listOfProcesses);

        List<Process> listForFCFS = new ArrayList<>();
        List<Process> listForSJF = new ArrayList<>();
        List<Process> listForSRTF = new ArrayList<>();
        List<Process> listForPriorityNP = new ArrayList<>();
        List<Process> listForPriorityP = new ArrayList<>();
        List<Process> listForRR = new ArrayList<>();


        int counter = 1;
        int temp = 0;
        for(int i=0;i<numberOfProcesses;i++){
            
            switch(counter){
                case 1:
                    listForFCFS.add(listOfProcesses.get(i));
                    break;
                case 2:
                    listForSJF.add(listOfProcesses.get(i));
                    break;
                case 3:
                    listForSRTF.add(listOfProcesses.get(i));
                    break;
                case 4:
                    listForPriorityNP.add(listOfProcesses.get(i));
                    break;
                case 5:
                    listForPriorityP.add(listOfProcesses.get(i));
                    break;
                case 6:
                    listForRR.add(listOfProcesses.get(i));
                    break;
            }

            temp++;
            if(temp%5==0){
                counter++;
                temp=0;
            }
        }

        // for(int i=0;i<numberOfProcesses;i++){
        //     int random = (int)(Math.random()*6 + 1);
        //     switch(random){
        //         case 1:
        //             listForFCFS.add(listOfProcesses.get(i));
        //             break;
        //         case 2:
        //             listForSJF.add(listOfProcesses.get(i));
        //             break;
        //         case 3:
        //             listForSRTF.add(listOfProcesses.get(i));
        //             break;
        //         case 4:
        //             listForPriorityNP.add(listOfProcesses.get(i));
        //             break;
        //         case 5:
        //             listForPriorityP.add(listOfProcesses.get(i));
        //             break;
        //         case 6:
        //             listForRR.add(listOfProcesses.get(i));
        //             break;
        //     }
        // }
        FCFS(listForFCFS, listForFCFS.size());
        SJF(listForSJF, listForSJF.size());
        SRTF(listForSRTF, listForSRTF.size());
        PriorityBased(listForPriorityNP, listForPriorityNP.size());
        PriorityBasedP(listForPriorityP, listForPriorityP.size());
        RR(listForRR, listForRR.size());
 
        ot.close();
        br.close();

        System.out.println("DONE");
    }
    static void FCFS(List<Process> listOfProcesses, int numberOfProcesses){
        ot.println("--------------- FCFS ----------------------");
        if(numberOfProcesses < 1)
            return;
        sortProcessesBasedOnAT(listOfProcesses);
        double time = 0;
        int index = 0;
        while (index < listOfProcesses.size()){
            time = Math.max(listOfProcesses.get(index).at, time);
            double bt = listOfProcesses.get(index).bt;
            listOfProcesses.get(index).ct = time + bt;
            listOfProcesses.get(index).tat = listOfProcesses.get(index).ct - listOfProcesses.get(index).at;
            listOfProcesses.get(index).wt = listOfProcesses.get(index).tat - listOfProcesses.get(index).bt;
            time += bt;
            index++;
        }
        printProcesses(listOfProcesses);
        computeAvgTimes(listOfProcesses);
    }
    static void SJF(List<Process> listOfProcesses, int numberOfProcesses){
        ot.println("--------------- SJF ----------------------");
        if(numberOfProcesses < 1)
            return;
        sortProcessesBasedOnAT(listOfProcesses);
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
        printProcesses(listOfProcesses);
 
        computeAvgTimes(listOfProcesses);

    }
    static void SRTF(List<Process> listOfProcesses, int numberOfProcesses){
        ot.println("--------------- SRTF ----------------------");
        if(numberOfProcesses < 1)
            return;
        sortProcessesBasedOnAT(listOfProcesses);
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
        StringBuilder sb = new StringBuilder();
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
        ot.println(sb);
        printProcesses(listOfProcesses);
 
        computeAvgTimes(listOfProcesses);

    }
    static void PriorityBased(List<Process> listOfProcesses, int numberOfProcesses){
        ot.println("--------------- Priority (Non - Preemtive) ----------------------");
        if(numberOfProcesses < 1)
            return;
        sortProcessesBasedOnAT(listOfProcesses);
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

        printProcesses(listOfProcesses);
 
        computeAvgTimes(listOfProcesses);
    }
    static void PriorityBasedP(List<Process> listOfProcesses, int numberOfProcesses){
        ot.println("--------------- Priority Preemptive ----------------------");
        if(numberOfProcesses < 1)
            return;
        sortProcessesBasedOnAT(listOfProcesses);
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
        StringBuilder sb = new StringBuilder();
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
        ot.println(sb);
        printProcesses(listOfProcesses);
 
        computeAvgTimes(listOfProcesses);
    }
    static void RR(List<Process> listOfProcesses, int numberOfProcesses){
        ot.println("--------------- Round Robin ----------------------");
        if(numberOfProcesses < 1)
            return;
        sortProcessesBasedOnAT(listOfProcesses);
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

        printProcesses(listOfProcesses);
 
        computeAvgTimes(listOfProcesses);

    }
    static void printProcesses(List<Process> listOfProcesses){
        for(Process process : listOfProcesses)
            ot.println(process);
    }
    static void computeAvgTimes(List<Process> listOfProcesses){
        double tat=0, wt=0;
        for(Process process : listOfProcesses){
            tat += process.tat;
            wt += process.wt;
        }
        tat/=listOfProcesses.size();
        wt/=listOfProcesses.size();
        ot.println("Avergae TAT: "+tat+", Average WT: "+wt);
        ot.println("----------------------------------------------------------");
        
    }
    static void sortProcessesBasedOnAT(List<Process> listOfProcesses){
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
                    Double.parseDouble(s[2]),
                    Integer.parseInt(s[3])
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
        double btCopy, rt;
        int priority;
        Process(int pid, double at, double bt){
            this.pid = pid;
            this.at = at;
            this.bt = bt;
            this.btCopy = bt;
            this.rt = Integer.MAX_VALUE;
        }
        Process(int pid, double at, double bt, int priority){
            this.pid = pid;
            this.at = at;
            this.bt = bt;
            this.btCopy = bt;
            this.priority = priority;
            this.rt = Integer.MAX_VALUE;
        }
        public String toString(){
            return "{PID: "+pid+", AT: "+at+", BT: "+btCopy+
                ", CT: "+ct+", TAT: "+tat+", WT: "+wt+"}";
            
        }
    }
}