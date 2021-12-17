package Server;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import Algorithm.App;
import Algorithm.Vertex;
import Encryption.Decryption;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import Encryption.*;
import Algorithm_CPU.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private BufferedWriter writer;
    String filePath;
    String check_txt = "\\w+\\.txt";

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String line = "";
        String[] temp;
        String _quantum = "";
        Double quantum;
        String task = "";
        // obtaining input and out streams
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            SecureRandom sr = new SecureRandom();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048, sr);

            // Key Pair Initialize
            KeyPair kp = kpg.genKeyPair();

            // PublicKey
            PublicKey publicKey = kp.getPublic();

            // PrivateKey
            PrivateKey privateKey = kp.getPrivate();

            //Generator private key
            PKCS8EncodedKeySpec spec_en = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            KeyFactory factory_en = KeyFactory.getInstance("RSA");
            PrivateKey priKey = factory_en.generatePrivate(spec_en);

            //Generator public key
            X509EncodedKeySpec spec_de = new X509EncodedKeySpec(publicKey.getEncoded());
            KeyFactory factory_de = KeyFactory.getInstance("RSA");
            PublicKey pubKey = factory_de.generatePublic(spec_de);

            String pubKeyEncode = Base64.getEncoder().encodeToString(pubKey.getEncoded());

            //Server put public Key into JSONObject
            JSONObject json = new JSONObject();
            json.put("publicKey", pubKeyEncode);
            String publicKeyTrans = json.toString();

            //Server send public Key to Client
            out.write(publicKeyTrans);
            out.newLine();
            out.flush();

            //Server receive encrypted secret Key by Client
            line = in.readLine();
            System.out.println("Server received: " + line);

            System.out.println(((Object) line).getClass().getSimpleName());

            JSONObject jsonObject = new JSONObject(line);
            String secretKeyEncrypt = jsonObject.get("secretKey").toString();
            System.out.println("SecretKey: " + secretKeyEncrypt);

            //Server decrpyt secret Key by private Key from Client
            String decryptOut = Decryption.decryptDataByRSA(secretKeyEncrypt, priKey);

            SecretKeySpec skeySpec = new SecretKeySpec(decryptOut.getBytes(), "AES");

        String decryptData = "";
        while (true) {
            try {
                boolean flag = true;
                line = in.readLine();

                //Decrypt first time by RSA
                decryptData = Decryption.decryptDataByRSA(line, priKey);

                //Decrypt second time by AES
                decryptData = Decryption.decryptDataByAES(decryptData, skeySpec);
                line = decryptData;
                if (line.equalsIgnoreCase("startFindPath")) {
                    task = "1";
                }
                if (line.equalsIgnoreCase("startCpuScheduling")) {
                    task = "2";
                }

                if (line.equalsIgnoreCase("end")) {
                    System.out.println("Server ngắt kết nối 1 client");
                    break;
                }
                switch (task) {
                    case "1":
                        System.out.println("Nhảy vô tìm đường");
                        line = in.readLine();
                        //Decrypt first time by RSA
                        decryptData = Decryption.decryptDataByRSA(line, priKey);

                        //Decrypt second time by AES
                        decryptData = Decryption.decryptDataByAES(decryptData, skeySpec);
                        line = decryptData;
                        System.out.println("đọc " + line);
                        if (line.matches(check_txt)) {
                            System.out.println(line);
                            File file = new File("./src/dataServer/S_" + line);
                            file.createNewFile();
                            filePath = file.getAbsolutePath();
                            writer = new BufferedWriter(new FileWriter(file, false));

                            line = in.readLine();
                            //Decrypt first time by RSA
                            decryptData = Decryption.decryptDataByRSA(line, priKey);

                            //Decrypt second time by AES
                            decryptData = Decryption.decryptDataByAES(decryptData, skeySpec);
                            line = decryptData;
                            while (line != null && flag) {
                                System.out.println("data nhận:" + line);
                                temp = line.split(";");
                                for (int i = 0; i < temp.length; i++) {
                                    writer.write(temp[i]);
                                    writer.newLine();
                                    writer.flush();
                                }
                                flag = false;

                            }

                        }

                        System.out.println(flag);
                        if (!flag) {
                            System.out.println("filepath: " + filePath);
                            App.startDijkstra(filePath);
                            List<Vertex> shortestPath = App.path;
                            double cost = App.cost;
                            System.out.println("shortest path: " + shortestPath + " " + "Cost: " + cost);
                            if (shortestPath != null) {
                                String tmp = "";
                                for (int i = 0; i < shortestPath.size(); i++) {
                                    tmp += shortestPath.get(i).toString() + " ";
                                }
                                System.out.println("Gửi đường đi ngắn nhất đến client: " + tmp);
                                System.out.println(Thread.currentThread());
                                out.write(tmp.toString());
                                out.newLine();
                                out.flush();

                                System.out.println("Gửi chi phí tối ưu đến client:" + cost);
                                out.write(String.valueOf(cost));
                                out.newLine();
                                out.flush();
                            } else {
                                System.err.println("Không tìm ra đường đi ngắn nhất ");
                            }
                        }
                        break;

                    case "2":
                        boolean flag2 = true;
                        while(flag2) {
                            try {
                                System.out.println("Nhảy vô CPU");
                                boolean flag1 = true;
                                line = in.readLine();
                                //Decrypt first time by RSA
                                decryptData = Decryption.decryptDataByRSA(line, priKey);

                                //Decrypt second time by AES
                                decryptData = Decryption.decryptDataByAES(decryptData, skeySpec);
                                line = decryptData;
                                System.out.println(line);
                                if (line.equals("EXIT")) return;
                                if (line.matches(check_txt)) {
                                    System.out.println(line);
                                    File file = new File("./src/dataServer/S_" + line);
                                    file.createNewFile();
                                    filePath = file.getAbsolutePath();
                                    writer = new BufferedWriter(new FileWriter(file, false));

                                    line = in.readLine();
                                    //Decrypt first time by RSA
                                    decryptData = Decryption.decryptDataByRSA(line, priKey);

                                    //Decrypt second time by AES
                                    decryptData = Decryption.decryptDataByAES(decryptData, skeySpec);
                                    line = decryptData;
                                    while (line != null && flag1) {
                                        System.out.println("data nhận:" + line);
                                        temp = line.split(":");
                                        for (int i = 0; i < temp.length; i++) {
                                            writer.write(temp[i]);
                                            System.out.println(temp[i]);
                                            writer.newLine();
                                            writer.flush();
                                        }
                                        flag1 = false;
                                    }
                                }
                                else {
                                    try {
                                        System.out.println("Server received: " + line);

                                        System.out.println(filePath);
                                        Algorithm_CPU.App.actionPerformed(filePath);
                                        Algorithm_CPU.App.itemStateChanged(line);
                                        System.out.println("==================================");

                                        if (line.equals("RR")) {
                                            _quantum = in.readLine();
                                            //Decrypt first time by RSA
                                            decryptData = Decryption.decryptDataByRSA(_quantum, priKey);

                                            //Decrypt second time by AES
                                            decryptData = Decryption.decryptDataByAES(decryptData, skeySpec);

                                            quantum = Double.parseDouble(decryptData);
                                            CPU_Scheduling _solver_RR = new CPU_Scheduling(Algorithm_CPU.App._jobs, Algorithm_CPU.App._algorithm, quantum);
                                            if (_solver_RR.solve()) {
                                                String result = "";
                                                Algorithm_CPU.App.drawGanttChart(_solver_RR.getGanttChart());
                                                result = Algorithm_CPU.App.drawGanttChart(_solver_RR.getGanttChart());

                                                //Encrypt data by AES
                                                result = Encryption.encryptDataByAES(result, skeySpec);
                                                System.out.println("Dữ liệu đã dc mã hóa là: " + result);
                                                out.write(result);
                                                out.newLine();
                                                out.flush();
                                            }
                                            flag2 = false;
                                        }
                                        if (line.equals("FCFS") || line.equals("SJF") || line.equals("Prio") || line.equals("PPrio")) {
                                            CPU_Scheduling _solver = new CPU_Scheduling(Algorithm_CPU.App._jobs, Algorithm_CPU.App._algorithm);
                                            if (_solver.solve()) {
                                                String result = "";
                                                Algorithm_CPU.App.drawGanttChart(_solver.getGanttChart());
                                                result = Algorithm_CPU.App.drawGanttChart(_solver.getGanttChart());

                                                //Encrypt data by AES
                                                result = Encryption.encryptDataByAES(result, skeySpec);
                                                System.out.println("Dữ liệu đã dc mã hóa là: " + result);
                                                out.write(result);
                                                out.newLine();
                                                out.flush();
                                            }
                                            flag2 = false;
                                        }
                                    } catch (IOException i) {
                                        System.out.println(i);
                                    }
                                }
                            }
                            catch (Exception i) {
                                System.out.println(i);
                            }
                        }
                        break;
                }


            } catch (Exception e) {
                // TODO: handle exception
                e.getStackTrace();
                System.err.println(e.getMessage());
            }
        }
    }catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | JSONException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
