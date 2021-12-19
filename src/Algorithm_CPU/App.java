package Algorithm_CPU;

import Algorithm_CPU.Algorithm;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class App {
    public static List<Job> _jobs;
    public static Algorithm _algorithm;
    Double quantum;
    JFileChooser fileChooser;
    Scanner scanner;

    public static void actionPerformed(String filePath) {
        _jobs = new ArrayList<Job>();
        try {
            int index = 0;
            File file = new File(filePath);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                _jobs.add(new Job(_jobs.size() + 1, 0, 0, 0, Double.POSITIVE_INFINITY));
                String input = sc.nextLine();
                StringTokenizer st = new StringTokenizer(input, ";");
                String arrival = st.nextToken();
                String burst = st.nextToken();
                String priority = st.nextToken();
                if (_jobs.isEmpty()) return;
                try {
                    _jobs.get(index).setArrivalTime(Double.parseDouble(arrival));
                }
                catch (Exception ex) {
                }
                try {
                    _jobs.get(index).setBurstTime(Double.parseDouble(burst));

                }
                catch (Exception ex) {
                }
                try {
                    _jobs.get(index).setPriority(Double.parseDouble(priority));
                }
                catch (Exception ex) {
                }
                index++;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (_jobs.isEmpty()) {
            return;
        }
        for (int i = 1; i <= _jobs.size(); i++)
            _jobs.get(i - 1).setJobNumber(i);
    }

    public static String drawGanttChart(GanttChart _gantt_chart) {
        String tempGantt = "";
        System.out.print("0.0");
        String result = "0.0 ";
        for(int i = 0; i < _gantt_chart.getJobList().size(); i++) {
            System.out.print(" -----> " + _gantt_chart.getJobList().get(i) + " <----- " + _gantt_chart.getTimeList().get(i));
            tempGantt = " -----> " + _gantt_chart.getJobList().get(i) + " <----- " + _gantt_chart.getTimeList().get(i);
            result += tempGantt;
        }
        System.out.println("\n\n");
        System.out.println(result);
        System.out.println("\n\n");
        return result;
    }

    public static void itemStateChanged(String state)	{
        switch(state) {
            case "FCFS":
                // code block
                _algorithm = Algorithm.FCFS;
                break;
            case "SJF":
                // code block
                _algorithm = Algorithm.SJF;
                break;
            case "Prio":
                // code block
                _algorithm = Algorithm.Prio;
                break;
            case "PPrio":
                // code block
                _algorithm = Algorithm.PPrio;
                break;
            case "RR":
                // code block
                _algorithm = Algorithm.RR;
                break;
            default:
                // code block
                break;
        }
    }


}
