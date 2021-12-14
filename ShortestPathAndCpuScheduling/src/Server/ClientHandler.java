package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import Algorithm.App;
import Algorithm.Vertex;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private BufferedWriter writer;
    String filePath;
    String check_txt = "\\w+\\.txt";

    public ClientHandler(Socket socket, BufferedWriter out, BufferedReader in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    @Override
    public void run() {
        String line = "";
        String[] temp;
        boolean flag = true;

        while (true) {
            try {
                line = in.readLine();
                if (line.matches(check_txt)) {
                    System.out.println(line);
                    File file = new File("./src/dataServer/S_" + line);
                    file.createNewFile();
                    filePath = file.getAbsolutePath();
                    writer = new BufferedWriter(new FileWriter(file, false));

                    line =  in.readLine();
                    while (line != null && flag) {
                        System.out.println("data nhận:" + line);
                        temp = line.split(";");
                        for (int i = 0; i<temp.length; i++) {
                            writer.write(temp[i]);
                            writer.newLine();
                            writer.flush();
                        }
                        flag = false;

                    }

                }
                if (line.toLowerCase().equals("exit")) {
                    break;
                }
                System.out.println(flag);
                if (!flag) {
                    System.out.println("filepath: " + filePath);
                    App.startDijkstra(filePath);
                    List<Vertex> shortestPath = App.path;
                    double cost = App.cost;
                    System.out.println("shortest path: " + shortestPath + " " + "Cost: " +cost);
                    if (shortestPath != null) {
                        String tmp = "";
                        for (int i = 0; i < shortestPath.size(); i++) {
                            tmp += shortestPath.get(i).toString() + " ";
                        }
                        System.out.println("Gửi đường đi ngắn nhất đến client: " +tmp);
                        System.out.println(Thread.currentThread());
                        out.write(tmp.toString());
                        out.newLine();
                        out.flush();

                        System.out.println("Gửi chi phí tối ưu đến client:" + String.valueOf(cost));
                        out.write(String.valueOf(cost));
                        out.newLine();
                        out.flush();
                        break;
                    } else {
                        System.err.println("Không tìm ra đường đi ngắn nhất ");
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.getStackTrace();
                System.err.println(e.getMessage());
            }

        }

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
