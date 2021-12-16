package Client;


import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Render.Render;

public class Client extends JFrame implements ActionListener {
    private JFrame frame;
    private JPanel contentPane;
    private JFileChooser openFileChooser;
    public String filename;
    public String fileNeedCreate;
    private JButton btnSelectFindPath;
    private JButton btnScheduleCPU;
    private JButton btnOpenFile;
    private JButton btnStartFindPath;
    private JButton btnEnd;
    private JLabel messageLabel;
    private JPanel panel;

    private Socket socket = null;
    BufferedWriter out = null;
    BufferedReader in = null;
    BufferedReader stdIn = null;



    void initGui() {
        frame = new JFrame("Đường đi ngắn nhất và lập lịch cpu");
        frame.setBounds(0, 0, 800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 773, 394);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    System.out.println("Client close");
                    frame.dispose();
                    out.write("end");
                    out.newLine();
                    out.flush();

                    stopConnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File(s));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Chỉ chọn file đuôi .txt", "txt"));

        panel = new JPanel();
        panel.setBackground(UIManager.getColor("InternalFrame.activeTitleGradient"));
        panel.setBounds(0, 0, 218, 355);
        contentPane.add(panel);
        panel.setLayout(null);

        btnSelectFindPath = new JButton("Tìm đường đi ngắn nhất");
        btnSelectFindPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnSelectFindPath.setForeground(SystemColor.menuText);
        btnSelectFindPath.setBackground(UIManager.getColor("InternalFrame.activeTitleGradient"));
        btnSelectFindPath.setBounds(10, 26, 198, 58);
        panel.add(btnSelectFindPath);

        btnScheduleCPU = new JButton("Lập lịch CPU");
        btnScheduleCPU.setBounds(10, 193, 198, 58);
        panel.add(btnScheduleCPU);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(228, 11, 519, 344);
        contentPane.add(tabbedPane);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Tìm đường đi ngắn nhất", null, panel_1, null);
        panel_1.setLayout(null);

        btnOpenFile = new JButton("Open file...");
        btnOpenFile.setBounds(10, 108, 113, 23);
        panel_1.add(btnOpenFile);
        btnOpenFile.addActionListener(this);

        messageLabel = new JLabel("New label");
        messageLabel.setBounds(133, 108, 371, 23);
        panel_1.add(messageLabel);

        btnStartFindPath = new JButton("Start find path");
        btnStartFindPath.setBounds(321, 154, 129, 23);
        panel_1.add(btnStartFindPath);
        btnStartFindPath.addActionListener(this);

        JLabel lblNewLabel = new JLabel("Chọn file đuôi .txt chứa các đỉnh và cạnh cần xử lý");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel.setBounds(10, 28, 376, 35);
        panel_1.add(lblNewLabel);
        frame.add(contentPane);
        frame.setVisible(true);

    }

    public Client(String host, int port) throws UnknownHostException, IOException {
        initGui();

        socket = new Socket(host, port);
        System.out.println("Connected");

        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        String[] shortestPath = null;


//        String dataResponse = in.readLine(); // waiting data from server
        String dataResponse;
        String regex = "\\d+(\\.\\d+)?";

        while ((dataResponse = in.readLine()) != null) {
            String cost = null;
            if (dataResponse.matches(regex)) {
                cost = dataResponse;
                System.out.println("chi phí: " + dataResponse);
            }

            if (dataResponse != null && !dataResponse.matches(regex)) {
                System.out.println("data " + dataResponse);
                shortestPath = dataResponse.split(" ");
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

    public static void main(String[] args) throws IOException {
        Client client = new Client ("localhost", 6655);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

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
            BufferedReader readfile = null;
            System.out.println(fileNeedCreate + filename);
            if (fileNeedCreate == null && filename == null) {
                JOptionPane.showMessageDialog(frame, "Hãy chọn file cần thực hiện tìm đường đi ngắn nhất", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                messageLabel.setText(" Hãy chọn file cần thực hiện!!");
            }
            else {

                try {
                    readfile = new BufferedReader(new FileReader(filename));
                    String line = "";

                    System.out.println("name: " + fileNeedCreate);
                    out.write(fileNeedCreate);
                    out.newLine();
                    out.flush();

                    String tmp = "";
                    String checkMap = "\\w+\\s\\w+\\s\\b((0*[1-9]\\d*)(\\.\\d)?)|(\\d+(\\.\\d)+)\b";
                    String checkSrcDes = "^(from|From|FROM|to|To|TO)\\s\\w+$";
                    HashMap<String,String> arrNode = new HashMap<>();
                    String[] tmpArr;
                    boolean flag = true;
                    try {

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
                            System.out.println(tmp);
                            out.write(tmp);
                            out.newLine();
                            out.flush();
                        }
                    } finally {
                        // TODO: handle finally clause
//						readfile.close();
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
