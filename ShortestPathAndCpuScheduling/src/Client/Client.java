package Client;


import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        String[] shortestPath = null;


        String dataResponse = in.readLine(); // waiting data from server
        String regex = "\\d+(\\.\\d+)?";
        String cost = null;
        while (dataResponse != null) {
            if (dataResponse.matches(regex)) {
                cost = dataResponse;
                System.out.println("chi phí: " + dataResponse);
            }

            if (dataResponse != null && !dataResponse.matches(regex)) {
                System.out.println("data " + dataResponse);
                shortestPath = dataResponse.split(" ");
            }

            dataResponse = in.readLine();

        }

        Render.startDraw(filename, shortestPath, cost);
//			stopConnect();
    }

//        try {
//            FileInputStream fis = new FileInputStream(FileName);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
//            byte[] bytes = new byte[8192];
//            int count;
//            while ((count = bis.read(bytes)) > 0) {
//                out.write(bytes, 0, count);
//
//
//            }
//            out.close();
//            fis.close();
//            bis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    public void stopConnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client = new Client ("localhost", 6655);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnOpenFile) {
            int returnValue = openFileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // set the label to the path of the selected file
                filename = openFileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("file chooser: " + filename);
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
                    try {
                        line = readfile.readLine();
                        while (line != null) {
                            System.out.println(line);
                            tmp = tmp + line + ";";
                            line = readfile.readLine();
                        }
                        System.out.println(tmp);
                        out.write(tmp);
                        out.newLine();
                        out.flush();
                    } finally {
                        // TODO: handle finally clause
//						readfile.close();

                    }
//					readfile.close();


                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }


        }

    }
}
