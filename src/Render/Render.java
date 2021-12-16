package Render;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import Client.Client;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxConstants;

public class Render extends JFrame {
    public static ArrayList<String> listNodes;
    public static Object[] arrV = null;
    public static ArrayList<Object> obj;
    public static HashMap<String, Object> hm;
    private static mxGraph graph;

    static JLabel lbPath;
    static JLabel label;
    static JLabel label2;
    static JFrame frameVisual;
    static JPanel controlPanel;

    public Render(String filename, String[] shortestPath, String cost) throws NumberFormatException, IOException {
        createAndShowGUI(filename, shortestPath, cost);

    }

    private static void createAndShowGUI(String filename, String[] shortestPath, String cost)
            throws NumberFormatException, IOException {
        frameVisual = new JFrame("Đồ thị đường đi ngắn nhất");
        frameVisual.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameVisual.setBounds(50, 150, 800, 400);

        label = new JLabel("Đường đi ngắn nhất: ");
        label2 = new JLabel("Chi phí ngắn nhất: " + cost);
        lbPath = new JLabel();
        JButton btnExport = new JButton("Xuất ảnh");
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exportGraph();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        controlPanel = new JPanel();

        controlPanel.add(btnExport);

        controlPanel.setLayout(new FlowLayout());
        BoxLayout boxlayout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
        controlPanel.setLayout(boxlayout);
        controlPanel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));
        graph = buildGraph(filename, shortestPath);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setOrientation(SwingConstants.WEST);
        layout.execute(graph.getDefaultParent());

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        controlPanel.add(graphComponent);
        controlPanel.add(label);
        controlPanel.add(label2);
        frameVisual.add(controlPanel);
        frameVisual.setVisible(true);


    }

    private void showLabelDemo(String[] listShortestPath) {
        String tmp = "";
        for (int i = 0; i < listShortestPath.length; i++) {
            tmp += " qua đỉnh " + listShortestPath[i];
        }

        System.out.println(tmp);
        lbPath.setText(tmp);
        controlPanel.add(lbPath);
        frameVisual.setVisible(true);
    }

    public static void startDraw(String filename, String[] shortestPath, String cost) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Render render = new Render(filename, shortestPath, cost);
                    render.showLabelDemo(shortestPath);

                } catch (NumberFormatException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    public static void exportGraph() throws IOException {
        int random = (int) Math.floor(((Math.random() * 1000)));
        System.out.println("Ảnh: " +random);
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
        ImageIO.write(image, "PNG", new File("./src/images/graph" +random+ ".png"));
    }

    private static mxGraph buildGraph(String filename, String[] listShortestPath)
            throws NumberFormatException, IOException {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        style.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        // style.put(mxConstants.STYLE_ROUNDED, "true");
        stylesheet.putCellStyle("ROUNDED", style);

        graph.getModel().beginUpdate();
        try {

            HashMap<String, String> shortestHM = new HashMap<String, String>();
            for (int i = 0; i < listShortestPath.length - 1; i++) {
                shortestHM.put(listShortestPath[i], listShortestPath[i + 1]);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            listNodes = new ArrayList<>();
            hm = new HashMap<>();
//            int vertices = Integer.valueOf(br.readLine());
//            if (vertices <= 0) {
//                System.out.println("Hãy điền tổng số đỉnh của đồ thị vào dòng đầu tiên của file ");
//				return null;
//            }
            String line;
            Object v;
            arrV = new Object[20];
            while (!(line = br.readLine()).equals("-1")) {
                String[] tokens = line.split("\\s+");
                System.out.println("XÉT " + line);
                if (!listNodes.contains(tokens[0])) {
                    v = graph.insertVertex(parent, null, tokens[0], 20, 20, 50, 50, "ROUNDED");
                    listNodes.add(tokens[0]);
                    hm.put(tokens[0], v);
                }

                if (!listNodes.contains(tokens[1])) {
                    v = graph.insertVertex(parent, null, tokens[1], 240, 150, 50, 50, "ROUNDED");
                    hm.put(tokens[1], v);
                    listNodes.add(tokens[1]);

                    if (shortestHM.containsKey(tokens[0]) && shortestHM.get(tokens[0]).equals(tokens[1])) {

                        graph.insertEdge(parent, null, tokens[2], hm.get(tokens[0]), hm.get(tokens[1]),
                                "defaultEdge;strokeColor=red");
                    } else {
                        graph.insertEdge(parent, null, tokens[2], hm.get(tokens[0]), hm.get(tokens[1]));
                    }

                } else {
                    if (shortestHM.containsKey(tokens[0]) && shortestHM.get(tokens[0]).equals(tokens[1])) {

                        graph.insertEdge(parent, null, tokens[2], hm.get(tokens[0]), hm.get(tokens[1]),
                                "defaultEdge;strokeColor=red");
                    } else {
                        graph.insertEdge(parent, null, tokens[2], hm.get(tokens[0]), hm.get(tokens[1]));
                    }

//					for (int i = 0; i < shortestPath.size()-1; i++) {
//						System.out.println("shortest: " + i + shortestPath.get(i) + shortestPath.get(i+1));
//						if (tokens[0].equals(shortestPath.get(i)) && tokens[1].equals(shortestPath.get(i+1))) {
//							graph.insertEdge(parent, null, tokens[2], hm.get(tokens[0]), hm.get(tokens[1]), "strokeColor=red" );
//						} else {
//							graph.insertEdge(parent, null, tokens[2], hm.get(tokens[0]), hm.get(tokens[1]));
//						}
//					}

                }

//			Object v1 =  graph.insertVertex(parent, null, "A", 0, 0, 20, 20, "ROUNDED");
//            Object v2 =  graph.insertVertex(parent, null, "B", 0, 0, 20, 20, "ROUNDED");
//            Object v3 =  graph.insertVertex(parent, null, "C", 0, 0, 20, 20, "ROUNDED");
//            Object v4 =  graph.insertVertex(parent, null, "D", 0, 0, 20, 20, "ROUNDED");
//
//            graph.insertEdge(parent, null, "10", v3, v4);
//            graph.insertEdge(parent, null, "10", v2, v3);
//            graph.insertEdge(parent, null, "15", v1, v2, "strokeColor=red");

            }

        } finally {
            graph.getModel().endUpdate();
        }
        return graph;
    }

}
