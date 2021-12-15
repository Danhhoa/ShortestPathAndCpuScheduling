package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private Socket socket = null;
    private ServerSocket server = null;
    BufferedWriter out = null;
    BufferedReader in = null;
    BufferedWriter writer;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client...");

            while (true) {
                Socket socket = null;

                try
                {
                    // socket object to receive incoming client requests
                    socket = server.accept();
                    System.out.println("Kết nối một client mới: " + socket);
                    System.out.println("Đăng ký tiến trình mới cho Client");
                    // create a new thread object
                    Thread t = new ClientHandler(socket);
                    // Invoking the start() method
                    t.start();

                }
                catch (Exception e){
                    socket.close();
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopConnect() throws IOException {
        in.close();
        out.close();
        socket.close();
        server.close();
    }

    public static void main(String[] args) {
        Server server = new Server(6655);
    }
}
