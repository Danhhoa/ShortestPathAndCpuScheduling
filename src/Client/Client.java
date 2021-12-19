package Client;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Algorithm_CPU.Algorithm;
import Render.Render;
import org.json.JSONException;
import org.json.JSONObject;
import Encryption.Encryption;
import Encryption.Decryption;

public class Client extends JFrame implements ActionListener, ItemListener {
    private JFrame frame;
    private JPanel contentPane;
    private JFileChooser openFileChooser;
    public String filename;
    public String fileNeedCreate;
    private JButton btnOpenFile;
    private JButton btnStartFindPath;
    private JLabel messageLabel;

    JPanel _panel_gantt;
    JScrollPane _scrollpane;
    Font _font_job;

    JLabel _lbl_algorithm;

    JTextField msg_text;
    JTextField nameFile;

    JButton _btn_open;
    JButton _btn_submit;
    JButton _btn_send;

    CheckboxGroup _cbg_algo;
    Checkbox _cb_FCFS;
    Checkbox _cb_SJF;
    Checkbox _cb_PRIO;
    Checkbox _cb_PPRIO;
    Checkbox _cb_RR;

    Algorithm _algorithm;
    String SECRET_KEY = "stackjava.com.if";
    String PUBLIC_KEY;

    private Socket socket = null;
    BufferedWriter out = null;
    BufferedReader in = null;

    void initGui() {
        frame = new JFrame("Đường đi ngắn nhất và lập lịch cpu");
        frame.setBounds(0, 0, 650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

//        GUI main
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File(s));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Chỉ chọn file đuôi .txt", "txt"));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(20, 11, 580, 400);
        contentPane.add(tabbedPane);

//        Tabber panel find path
        JPanel panel_findShortest = new JPanel();
        tabbedPane.addTab("Tìm đường đi ngắn nhất", null, panel_findShortest, null);
        panel_findShortest.setLayout(null);

        btnOpenFile = new JButton("Open file...");
        btnOpenFile.setBounds(10, 108, 113, 23);
        panel_findShortest.add(btnOpenFile);
        btnOpenFile.addActionListener(this);

        messageLabel = new JLabel("địa chỉ đường dẫn");
        messageLabel.setBounds(133, 108, 371, 23);
        panel_findShortest.add(messageLabel);

        btnStartFindPath = new JButton("Start find path");
        btnStartFindPath.setBounds(321, 154, 129, 23);
        panel_findShortest.add(btnStartFindPath);
        btnStartFindPath.addActionListener(this);

        JLabel lblNewLabel = new JLabel("Chọn file đuôi .txt chứa các đỉnh và cạnh cần xử lý");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel.setBounds(10, 28, 376, 35);
        panel_findShortest.add(lblNewLabel);


//        Tabber pane CPU scheduling
        JPanel panel_CpuScheduling = new JPanel();
        tabbedPane.addTab("Lập lịch CPU", null, panel_CpuScheduling, null);
        panel_CpuScheduling.setLayout(null);

        _panel_gantt = new JPanel();

        _scrollpane = new JScrollPane(_panel_gantt);
        _scrollpane.setBounds(20, 20, 540, 100);
        _font_job = new Font("Times New Roman", Font.PLAIN, 16);

        _lbl_algorithm = new JLabel("Algorithm: ");
        _lbl_algorithm.setBounds(20, 130, 100, 30);

        msg_text = new JTextField("");
        msg_text.setBounds(110, 135, 100, 30);
        msg_text.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        msg_text.setEditable(false);

        nameFile = new JTextField("");
        nameFile.setBounds(130, 200, 100, 40);
        nameFile.setFont(new Font("Times New Roman", Font.BOLD, 20));
        nameFile.setEditable(false);

        _btn_submit = new JButton("Submit");
        _btn_submit.setBounds(345, 280, 100, 30);
        _btn_submit.addActionListener(this);

        _btn_open = new JButton("Open File");
        _btn_open.setBounds(20, 200, 100, 40);
        _btn_open.addActionListener(this);

        _btn_send = new JButton("Send File");
        _btn_send.setBounds(200, 200, 100, 40);
        _btn_send.addActionListener(this);

        _cbg_algo = new CheckboxGroup();

        _cb_FCFS = new Checkbox("First Come First Serve (FCFS)", false, _cbg_algo);
        _cb_FCFS.setBounds(345, 130, 200, 20);
        _cb_FCFS.addItemListener(this);

        _cb_SJF = new Checkbox("Shortest Algorithm.Job First (SJF)", false, _cbg_algo);
        _cb_SJF.setBounds(345, 160, 200, 20);
        _cb_SJF.addItemListener(this);

        _cb_PRIO = new Checkbox("Priority (Prio)", false, _cbg_algo);
        _cb_PRIO.setBounds(345, 190, 200, 20);
        _cb_PRIO.addItemListener(this);

        _cb_PPRIO = new Checkbox("Preemptive Priority (P-Prio)", false, _cbg_algo);
        _cb_PPRIO.setBounds(345, 220, 200, 20);
        _cb_PPRIO.addItemListener(this);

        _cb_RR = new Checkbox("Round Robin (RR)", false, _cbg_algo);
        _cb_RR.setBounds(345, 250, 200, 20);
        _cb_RR.addItemListener(this);


        this.setLayout(null);

        panel_CpuScheduling.add(_scrollpane);


        panel_CpuScheduling.add(_lbl_algorithm);

        panel_CpuScheduling.add(msg_text);

        panel_CpuScheduling.add(_btn_open);
        panel_CpuScheduling.add(_btn_submit);
        panel_CpuScheduling.add(_btn_send);

        panel_CpuScheduling.add(_cb_FCFS);
        panel_CpuScheduling.add(_cb_SJF);
        panel_CpuScheduling.add(_cb_PRIO);
        panel_CpuScheduling.add(_cb_PPRIO);
        panel_CpuScheduling.add(_cb_RR);


//        this.setSize(600, 500);
//        this.setTitle("CPU Scheduling Algorithms");
//        this.setLocationRelativeTo(null);
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.add(contentPane);
        frame.setVisible(true);


    }

    PublicKey publicKey (String publicKey) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = factory.generatePublic(spec);
            return pubKey;
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client(String host, int port) throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        initGui();

        socket = new Socket(host, port);
        System.out.println("Connected");

        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String[] shortestPath = null;

        String line = "";
        String message = "";

        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

        System.out.println("CLient send: " + message);
        line = in.readLine();
        System.out.println("PublicKey: " + line);

        JSONObject jsonObject = new JSONObject(line);

        String publicKey = jsonObject.get("publicKey").toString();

        PUBLIC_KEY = publicKey;

        byte[] decodedBytes = Base64.getDecoder().decode(publicKey);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = factory.generatePublic(spec);

        //Encrypt secretKey by RSA
        String strEncrypt = Encryption.encryptDataByRSA(SECRET_KEY, pubKey);

        String encryptSecretKey = strEncrypt;
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("secretKey", encryptSecretKey);
        encryptSecretKey = jsonObject1.toString();

        out.write(encryptSecretKey);
        out.newLine();
        out.flush();

        String dataResponse;


        String regex = "\\d+(\\.\\d+)?";
        String checkPath ="([\\w]\\s)*";

        while ((dataResponse = in.readLine()) != null) {// waiting data from server
            String data = "";
            //Decrypt data by AES
            String dataDecrypt = Decryption.decryptDataByAES(dataResponse, skeySpec);
            data = dataDecrypt;
            String cost = null;
            if (data.matches(regex)) {
                cost = data;
                System.out.println("chi phí: " + data);
            }

            if (data.matches(checkPath)) {
                System.out.println("data " + data);
                shortestPath = data.split(" ");
            }
            if (!data.matches(regex) && !data.matches(checkPath)){
                String tmpResult = "";
                JTextArea gantt = new JTextArea(5, 20);
                _scrollpane = new JScrollPane(gantt);
                gantt.setEditable(false);
                gantt.setFont(_font_job);
                gantt.setBackground(getBackground());
                tmpResult = data;
                tmpResult = tmpResult.replace(" <-- ", " --> ");

                    gantt.setText(data);
                    _panel_gantt.add(gantt);
                    _panel_gantt.validate();
                System.out.println("gantt:" +tmpResult);
            }
            if (filename != null && shortestPath != null && cost != null) {
                Render.startDraw(filename, shortestPath, cost);
            }
        }

    }

    public void stopConnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        new Client ("localhost", 6655);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        String encryptedMessage = "";
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    frame.dispose();
                    String endTask;
                    String endConnect = "end";
                    SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                    endTask = Encryption.encryptDataByAES(endConnect, skeySpec);

                    //Encrypt second time by publicKey RSA
                    PublicKey pubKey = publicKey(PUBLIC_KEY);
                    endTask = Encryption.encryptDataByRSA(endTask, pubKey);
                    out.write(endTask);
                    out.newLine();
                    out.flush();

                    System.out.println("client close");
                    stopConnect();
                } catch (IOException ex) {

                }

            }
        });

        if (e.getSource() == btnOpenFile) {
            int returnValue = openFileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // set the label to the path of the selected file
                filename = openFileChooser.getSelectedFile().getAbsolutePath();
                messageLabel.setText(filename);
                fileNeedCreate = openFileChooser.getSelectedFile().getName();
            }
            // if the user cancelled the operation
            else {
                JOptionPane.showMessageDialog(frame, "Hãy chọn file cần thực hiện tìm đường đi ngắn nhất", "Alert",
                        JOptionPane.WARNING_MESSAGE);
                messageLabel.setText("hãy chọn file cần thực hiện");
            }
        }

        if (e.getSource() == btnStartFindPath) {
            BufferedReader readfile;
            System.out.println(fileNeedCreate + filename);
            if (fileNeedCreate == null && filename == null) {
                JOptionPane.showMessageDialog(frame, "Hãy chọn file cần thực hiện tìm đường đi ngắn nhất", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                messageLabel.setText(" Hãy chọn file cần thực hiện!!");
            }
            else {

                try {
                    String chooseTask = "startFindPath";
                    SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                    encryptedMessage = Encryption.encryptDataByAES(chooseTask, skeySpec);

                    //Encrypt second time by publicKey RSA
                    PublicKey pubKey = publicKey(PUBLIC_KEY);
                    encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);

                    out.write(encryptedMessage);
                    out.newLine();
                    out.flush();

                    readfile = new BufferedReader(new FileReader(filename));
                    String line;

                    System.out.println("name: " + fileNeedCreate);
                    skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                    encryptedMessage = Encryption.encryptDataByAES(fileNeedCreate, skeySpec);

                    //Encrypt second time by publicKey RSA
                    pubKey = publicKey(PUBLIC_KEY);
                    encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);
                    out.write(encryptedMessage);
                    out.newLine();
                    out.flush();

                    String tmp = "";
                    String checkMap = "\\w+\\s\\w+\\s\\b((0*[1-9]\\d*)(\\.\\d)?)|(\\d+(\\.\\d)+)\b";
                    String checkSrcDes = "^(from|From|FROM|to|To|TO)\\s\\w+$";
                    HashMap<String,String> arrNode = new HashMap<>();
                    String[] tmpArr;
                    boolean flag = true;
                    while ((line = readfile.readLine()) != null) {
                        System.out.println(line);
                        tmpArr = line.split(" ");
                        if (line.matches(checkMap)) {
                            tmp = tmp + line + ";";
                            arrNode.put(tmpArr[0], tmpArr[1]);
                        }
                        else if(line.equals("-1") || (line.matches(checkSrcDes) && (arrNode.containsKey(tmpArr[1]) || arrNode.containsValue(tmpArr[1])))) {
                            tmp += line +";";
                        } else {
                            JOptionPane.showMessageDialog(frame, "Dữ liệu trong " +fileNeedCreate+ " sai yêu cầu \n"
                                            + "hãy kiểm tra tại dòng (" + line + ") và sửa theo đúng format \n"
                                            + "Tên đỉnh không dùng kí tự đặt biệt, trọng số phải > 0\n"
                                            + "Phải có flag -1 để phân tách dữ liệu các đỉnh và đỉnh nguồn đích cần tìm\n"
                                            + "đỉnh nguồn và đích cần tìm phải tồn tại trong đồ thị\n"
                                            +"Ví dụ: \n" + "A B 10\n" +"A C 1\n"+"...\n" + "-1\n" + "from A\n" + "to B\n", "Thông báo",
                                    JOptionPane.WARNING_MESSAGE);
                            messageLabel.setText("Hãy nhập dữ liệu đúng");
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        System.out.println("gửi: " + tmp);
                        skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                        encryptedMessage = Encryption.encryptDataByAES(tmp, skeySpec);

                        //Encrypt second time by publicKey RSA
                        pubKey = publicKey(PUBLIC_KEY);
                        encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);
                        out.write(encryptedMessage);
                        out.newLine();
                        out.flush();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

        JFileChooser openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File(s));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Chỉ chọn file đuôi .txt", "txt"));

        if (e.getSource() == _btn_open) {
            _panel_gantt.removeAll();
            _panel_gantt.repaint();
            _panel_gantt.validate();
            int returnValue = openFileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // set the label to the path of the selected file
                filename = openFileChooser.getSelectedFile().getAbsolutePath();
                JTextArea gantt = new JTextArea(5, 20);
                _scrollpane = new JScrollPane(gantt);
                gantt.setEditable(false);
                gantt.setFont(_font_job);
                gantt.setBackground(getBackground());
                gantt.setText(filename);
                _panel_gantt.add(gantt);
                _panel_gantt.validate();
                fileNeedCreate = openFileChooser.getSelectedFile().getName();
            }
            // if the user cancelled the operation
            else {
                JOptionPane.showMessageDialog(_panel_gantt, "Hãy chọn file cần thực hiện tìm đường đi ngắn nhất", "Alert",
                        JOptionPane.WARNING_MESSAGE);
                JTextArea gantt = new JTextArea(5, 20);
                _scrollpane = new JScrollPane(gantt);
                gantt.setEditable(false);
                gantt.setFont(_font_job);
                gantt.setBackground(getBackground());
                gantt.setText("hãy chọn file cần thực hiện");
                _panel_gantt.add(gantt);
                _panel_gantt.validate();
            }
        }
        if (e.getSource() == _btn_send) {
            _panel_gantt.removeAll();
            _panel_gantt.repaint();
            _panel_gantt.validate();
            _scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            String chooseTask = "startCpuScheduling";
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            encryptedMessage = Encryption.encryptDataByAES(chooseTask, skeySpec);

            //Encrypt second time by publicKey RSA
            PublicKey pubKey = publicKey(PUBLIC_KEY);
            encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);

            try {
                out.write(encryptedMessage);
                out.newLine();
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            BufferedReader readfile;
            if (fileNeedCreate == null && filename == null) {
                JOptionPane.showMessageDialog(_panel_gantt, "Hãy chọn file cần thực hiện lập lịch CPU", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                JTextArea gantt = new JTextArea(5, 20);
                _scrollpane = new JScrollPane(gantt);
                gantt.setEditable(false);
                gantt.setFont(_font_job);
                gantt.setBackground(getBackground());
                gantt.setText("hãy chọn file cần thực hiện");
                _panel_gantt.add(gantt);
                _panel_gantt.validate();
            } else {
                try {
                    boolean flag = true;
                    readfile = new BufferedReader(new FileReader(filename));
                    String line;
                    String tmp = "";

                    System.out.println("name: " + fileNeedCreate);
                    //Encrypt first time by secretKey AES
                    skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                    encryptedMessage = Encryption.encryptDataByAES(fileNeedCreate, skeySpec);

                    //Encrypt second time by publicKey RSA
                    pubKey = publicKey(PUBLIC_KEY);
                    encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);

                    out.write(encryptedMessage);
                    out.newLine();
                    out.flush();

                    String map = "\\d+(\\.\\d+)?+;{1}+\\d+(\\.\\d+)?+;{1}+\\d+(\\.\\d+)?+";

                    while ((line = readfile.readLine()) != null) {
                        if (line.matches(map)) {
                            tmp = tmp + line + ":";
                        }
                        else {
                            JOptionPane.showMessageDialog(_panel_gantt, "Dữ liệu trong " + fileNeedCreate + " sai yêu cầu \n"
                                            + "hãy kiểm tra tại dòng (" + line + ") và sửa theo đúng format \n", "Thông báo",
                                    JOptionPane.WARNING_MESSAGE);
                            JTextArea gantt = new JTextArea(5, 20);
                            _scrollpane = new JScrollPane(gantt);
                            gantt.setEditable(false);
                            gantt.setFont(_font_job);
                            gantt.setBackground(getBackground());
                            gantt.setText("Hãy nhập dữ liệu đúng");
                            _panel_gantt.add(gantt);
                            _panel_gantt.validate();
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        System.out.println(tmp);
                        //Encrypt first time by secretKey AES
                        skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                        encryptedMessage = Encryption.encryptDataByAES(tmp, skeySpec);

                        //Encrypt second time by publicKey RSA
                        pubKey = publicKey(PUBLIC_KEY);
                        encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);

                        System.out.println("file sau khi mã hóa 2 lần :" + encryptedMessage);
                        out.write(encryptedMessage);
                        out.newLine();
                        out.flush();
                    }
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        }
        if (e.getSource() == _btn_submit) {
            try {
                String chooseTask = "startCpuScheduling";
                SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                encryptedMessage = Encryption.encryptDataByAES(chooseTask, skeySpec);

                //Encrypt second time by publicKey RSA
                PublicKey pubKey = publicKey(PUBLIC_KEY);
                encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);

                try {
                    out.write(encryptedMessage);
                    out.newLine();
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                _panel_gantt.removeAll();
                _panel_gantt.repaint();
                _panel_gantt.validate();
                _scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                String algorithm;
                if (fileNeedCreate == null && filename == null) {
                    JOptionPane.showMessageDialog(_panel_gantt, "Hãy chọn file cần thực hiện dể giải bài toán lập lịch CPU", "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    JTextArea gantt = new JTextArea(5, 20);
                    _scrollpane = new JScrollPane(gantt);
                    gantt.setEditable(false);
                    gantt.setFont(_font_job);
                    gantt.setBackground(getBackground());
                    gantt.setText("hãy chọn file cần thực hiện");
                    _panel_gantt.add(gantt);
                    _panel_gantt.validate();
                }
                if (this._algorithm == null) {
                    JOptionPane.showMessageDialog(_panel_gantt, "Chọn thuật toán lập lịch cần tính!"
                            , "Alert", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    algorithm = _algorithm.toString();
                    System.out.println(algorithm);
                    msg_text.setText(this._algorithm.toString());
                    String algorithmTmp = algorithm;

                    //Encrypt first time by secretKey AES
                    skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                    encryptedMessage = Encryption.encryptDataByAES(algorithmTmp, skeySpec);

                    //Encrypt second time by publicKey RSA
                    pubKey = publicKey(PUBLIC_KEY);
                    encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);
                    System.out.println("encryp submit: " +encryptedMessage);
                    out.write(encryptedMessage);
                    out.newLine();
                    out.flush();

                    if (algorithm.equals("RR")) {
                        while (true) {
                            try {
                                String temp = JOptionPane.showInputDialog("Quantum time: ");
                                if (temp == null) return;
                                if (temp.matches("\\d+(\\.\\d+)?")) {
                                    //Encrypt first time by secretKey AES
                                    skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
                                    encryptedMessage = Encryption.encryptDataByAES(temp, skeySpec);

                                    //Encrypt second time by publicKey RSA
                                    pubKey = publicKey(PUBLIC_KEY);
                                    encryptedMessage = Encryption.encryptDataByRSA(encryptedMessage, pubKey);
                                    out.write(encryptedMessage);
                                    out.newLine();
                                    out.flush();
                                    break;
                                } else {
                                    JOptionPane.showMessageDialog(_panel_gantt, "Dữ liệu truyền vào không đúng. Nhập lại"
                                            , "Alert", JOptionPane.WARNING_MESSAGE);
                                }
                            } catch (NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(_panel_gantt, "Dữ liệu truyền vào không đúng. Nhập lại"
                                        , "Alert", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        msg_text.setText("");
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(_cb_FCFS.getState())	{
            _algorithm = Algorithm.FCFS;
            msg_text.setText(_algorithm.toString());
        }
        if(_cb_SJF.getState())	{
            _algorithm = Algorithm.SJF;
            msg_text.setText(_algorithm.toString());
        }
        if(_cb_PRIO.getState())	{
            _algorithm = Algorithm.Prio;
            msg_text.setText(_algorithm.toString());
        }
        if(_cb_PPRIO.getState())	{
            _algorithm = Algorithm.PPrio;
            msg_text.setText(_algorithm.toString());
        }
        if(_cb_RR.getState())	{
            _algorithm = Algorithm.RR;
            msg_text.setText(_algorithm.toString());
        }
    }
}
