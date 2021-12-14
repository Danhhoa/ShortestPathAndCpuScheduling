package Algorithm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





public class App {

    public static List<Vertex> path;
    public static double cost;
    public static void startDijkstra(String filename) {
        HashMap<String, String> direction = new HashMap<>();
        ArrayList<String> listDir = new ArrayList<String>();
        String from, to, setFrom, setTo;
        String line, next;
        String[] dir;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            while ((line = br.readLine()) != null) {
                if (line.equals("-1")) {
                    next = br.readLine();
                    while (next != null && next != " ") {
                        dir = next.split(" ");
                        direction.put(dir[0], dir[1]);
                        next = br.readLine();
                    }
                    for (String i : direction.keySet()) {
                        if (i.equalsIgnoreCase("from")) {
                            from = direction.get(i);
                            listDir.add(from);
                        } else if (i.equalsIgnoreCase("to")) {
                            to = direction.get(i);
                            listDir.add(to);
                        }
                        else {
                            System.err.println("error in format input at:" +filename);
                        }

                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map map = new Map(filename);
        setFrom = listDir.get(0);
        setTo = listDir.get(1);
        if (map.map != null) {
            Vertex start = map.getVertexWithName(setFrom);

            Vertex end = map.getVertexWithName(setTo);

            // Make sure point was able to be found in map, else return
            if (start == null) {
                System.out.println("Starting point with name " + setFrom + " could not be found in map, please check the" +
                        " name specified in arguments or check the map file and try again.");
                return;
            }
            if (end == null) {
                System.out.println("Destination point with name " + setTo + " could not be found in map, please check " +
                        "the" + " name specified in arguments or make sure point exists in map " +
                        "file" + " and try again.");
                return;
            }
            Dijkstra.computePaths(start); // run Dijkstra
            cost = end.minDistance;
            System.out.println(cost);
            System.out.println("Distance to " + end + ": " + end.minDistance);
            path = Dijkstra.getShortestPathTo(end);
            System.out.println("Path: " + path);

        }
    }

//	public static void main(String[] args) throws FileNotFoundException {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					mainGUI frame = new mainGUI();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
}
