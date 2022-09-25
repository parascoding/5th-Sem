import java.util.*;
import java.io.*;
public class Main{
    static BufferedReader br;
    static PrintWriter ot;
    public static void main(String args[]) throws Exception{
        try{
            // br = new BufferedReader(new InputStreamReader(System.in));
            br = new BufferedReader(new FileReader("input.txt"));
            ot = new PrintWriter(System.out);
            
            List<Process> listOfProcess = new ArrayList<>();
            int numberOfProcesses = Integer.parseInt(br.readLine().trim());

            for(int i = 0; i < numberOfProcesses; i++){
                String s[] = br.readLine().trim().split(" ");
                int id = Integer.parseInt(s[0]);
                double at = Double.parseDouble(s[1]);
                double bt = Double.parseDouble(s[2]);
                listOfProcess.add(new Process(id, at, bt));
            }

            sort(listOfProcess, numberOfProcesses);
            FCFS(listOfProcess, numberOfProcesses);
            double tat = 0, wt = 0;
            for(Process process : listOfProcess){
                ot.println(process);
                tat += process.tat;
                wt += process.wt;
            }

            ot.println("Average TAT = " + tat/numberOfProcesses + "  Average WT = " + wt/numberOfProcesses);

            ot.close();
            br.close();
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    static void FCFS(List<Process> listOfProcess, int numberOfProcesses){
        double time = 0;
        int index = 0;
        while (index < numberOfProcesses){
            time = Math.max(listOfProcess.get(index).at, time);
            double bt = listOfProcess.get(index).bt;
            listOfProcess.get(index).ct = time + bt;
            listOfProcess.get(index).tat = listOfProcess.get(index).ct - listOfProcess.get(index).at;
            listOfProcess.get(index).wt = listOfProcess.get(index).tat - listOfProcess.get(index).bt;
            time += bt;
            index++;
        }
    }
    static void sort(List<Process> listOfProcess, int numberOfProcesses){
        Collections.sort(listOfProcess,
            new Comparator<Process>(){
                public int compare(Process p1, Process p2){
                    if (p1.at == p2.at){
                        if (p1.bt > p2.bt) return 1;
                        else return -1;
                    }
                    if (p1.at > p2.at) return 1;
                    else return -1;
                }
            }
        );
    }
    static class Process{
        int id;
        double at, bt;
        double ct, tat, wt;
        Process(int id, double at, double bt){
            this.at = at;
            this.bt = bt;
            this.id = id;
        }
        public String toString(){
            return "PID = " + id + " AT = " + at + 
                        " BT = " + bt + " CT = " + ct +
                        " TAT = " + tat + " WT = " + wt;
        }
    }
}