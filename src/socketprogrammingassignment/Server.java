/*
Socket Programming assignment server file
@author Yash Sharma
Roll no. 1810110283
 */
package socketprogrammingassignment;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public final class Server {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream dataInputStream = null;
    private ArrayList<Integer>[] adjList;
    private ArrayList<Integer>[] adjList2;
    private List<List<Integer>> answerPathList = new ArrayList<>();
    private PrintStream ps = null;
    private OutputStream outputStream = null;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started at port " + port + ".\nWaiting for client...");
            socket = serverSocket.accept(); // accepts the client
            System.out.println("Client accepted succesfully!");
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
// for writing to client
            ps = new PrintStream(socket.getOutputStream());
// outputStream to send the image
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
// receive the length of adjancency list i.e., 5 here
            int length = dataInputStream.readInt();
// receive values entered by client and make a adjancy list out of the values
            adjList = new ArrayList[length];
            adjList2 = new ArrayList[length];
            for (int i = 0; i < length; i++) {
                adjList[i] = new ArrayList<>();
                adjList2[i] = new ArrayList<>();
            }
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    int next = dataInputStream.readInt();
                    adjList2[i].add(next);
                    if (next == 1) {
                        adjList[i].add(j);
                    }
                }
            }
// reading the other 3 values
            int pathLength = dataInputStream.readInt();
            Character node1 = dataInputStream.readChar();
            Character node2 = dataInputStream.readChar();
// map letters with the adjacency list
            HashMap<Character, Integer> map = new HashMap<>();
            map.put('A', 0);
            map.put('B', 1);
            map.put('C', 2);
            map.put('D', 3);
            map.put('E', 4);
// function which runs the algorithm
// return the String to client {YES/NO}
            if (answerPathList.size() > 0) {
                ps.println("Yes, there exists a path of length " + pathLength + " from node " + node1 + " to node " + node2 + ".");
            } else {
                ps.println("No, there is no path of length " + pathLength + " from node " + node1 + " to node " + node2 + ".");
            }
// open the JavaFrame for cutom painting
            GraphFrame gf = new GraphFrame(adjList2, outputStream);
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Closing connection");
// close all the connections
        try {
            socket.close();
            dataInputStream.close();
            ps.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
// Algorithm

    public void getAllPaths(int node1, int node2, int length, int pathLength) {
        boolean[] visitedArray = new boolean[length];
        ArrayList<Integer> pathList = new ArrayList<>();
        pathList.add(node1);
        getAllPathsUtil(node1, node2, visitedArray, pathList, pathLength);
    }
// Algorithm util function

    private void getAllPathsUtil(Integer u, Integer d, boolean[] isVisited, List<Integer> localPathList, int pathLength) {
        if (u.equals(d) && localPathList.size() - 1 == pathLength) {
            answerPathList.add(localPathList);
            return;
        }
        isVisited[u] = true;
        for (Integer i : adjList[u]) {
            if (!isVisited[i]) {
                localPathList.add(i);
                getAllPathsUtil(i, d, isVisited, localPathList, pathLength);
                localPathList.remove(i);
            }
        }
        isVisited[u] = false;
    }

    public static void main(String[] args) {
// create a server object
        Server server = new Server(9999);
    }
}

class GraphFrame extends JFrame {
// receive the adjacencyList and outputStream from server class in constructor

    public GraphFrame(ArrayList<Integer>[] adjList, OutputStream outputStream) {
        super("GraphFrame");
// create a plain canvas for painting graph
        Canvas c = new Canvas() {
            @Override
            public void paint(Graphics g) {
                ArrayList<Integer>[] centers = new ArrayList[5];
                for (int i = 0; i < 5; i++) {
                    centers[i] = new ArrayList<>();
                }
// center top
                centers[0].add(275);
                centers[0].add(75);
// left top
                centers[1].add(75);
                centers[1].add(225);
// right top
                centers[2].add(475);
                centers[2].add(225);
// left bottom
                centers[3].add(125);
                centers[3].add(425);
// right bottom
                centers[4].add(425);
                centers[4].add(425);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.decode("#0a1430"));
                g2.fillRect(0, 0, 550, 550);
                g2.setStroke(new BasicStroke(3));
                g2.setColor(Color.decode("#357aa6"));
                boolean[] ar = {false, false, false, false, false};
                for (int i = 0; i < adjList.length; i++) {
                    for (int j = 0; j < adjList.length; j++) {
                        if (adjList[i].get(j) == 1) {
                            ar[i] = true;
                            ar[j] = true;
                        }
                    }
                }
                int chNumber = 65;
                for (int counter = 0; counter < ar.length; counter++) {
                    if (ar[counter]) {
                        g2.fillOval(centers[counter].get(0) - 25, centers[counter].get(1) - 25, 50, 50);
                        g2.setColor(Color.WHITE);
                        g2.drawOval(centers[counter].get(0) - 25, centers[counter].get(1) - 25, 50, 50);
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 25));
                        g2.drawString(Character.toString((char) (chNumber + counter)), centers[counter].get(0) - 10, centers[counter].get(1) + 10);
                        g2.setColor(Color.decode("#357aa6"));
                    }
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.WHITE);
// A-B
                Point sw = new Point(250, 100);
                Point ne = new Point(100, 200);
                drawLogicLine(g2, 0, 1, sw, ne, adjList);
// B-D
                sw = new Point(75, 260);
                ne = new Point(100, 400);
                drawLogicLine(g2, 1, 3, sw, ne, adjList);
// D-E
                sw = new Point(160, 425);
                ne = new Point(390, 425);
                drawLogicLine(g2, 3, 4, sw, ne, adjList);
// C-E
                sw = new Point(475, 260);
                ne = new Point(440, 395);
                drawLogicLine(g2, 2, 4, sw, ne, adjList);
// B-C
                sw = new Point(110, 225);
                ne = new Point(440, 225);
                drawLogicLine(g2, 1, 2, sw, ne, adjList);
// A-C
                sw = new Point(300, 100);
                ne = new Point(450, 200);
                drawLogicLine(g2, 0, 2, sw, ne, adjList);
// A-D
                sw = new Point(265, 120);
                ne = new Point(130, 390);
                drawLogicLine(g2, 0, 3, sw, ne, adjList);
// A-E
                sw = new Point(285, 120);
                ne = new Point(420, 390);
                drawLogicLine(g2, 0, 4, sw, ne, adjList);
// B-E
                sw = new Point(100, 250);
                ne = new Point(400, 400);
                drawLogicLine(g2, 1, 4, sw, ne, adjList);
// D-C
                sw = new Point(150, 400);
                ne = new Point(450, 250);
                drawLogicLine(g2, 3, 2, sw, ne, adjList);
            }
        };
        add(c);
        setSize(550, 550);
        c.setBackground(Color.GRAY);
// set visibility to false as we don't need to see the frame here
// toggle it to true if you want to see the JFrame as well
        setVisible(false);
// capture image function
        clickImage(c, outputStream);
    }
// draw arrows based on graph

    private void drawArrowHead(Graphics2D g2, Point tip, Point tail) {
        double phi = Math.toRadians(40);
// set the width of arrowhead
        double wid = 12;
        g2.setPaint(Color.WHITE);
// set the value for dy and dx
        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
// angle substended by dy and dx called theta
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + phi;
// draw the arrows by looping through
        for (int j = 0; j < 2; j++) {
            x = tip.x - wid * Math.cos(rho);
            y = tip.y - wid * Math.sin(rho);
// actual code which draws the arrows
            g2.draw(new Line2D.Double(tip.x, tip.y, x, y));
// finally change the rho value
            rho = theta - phi;
        }
    }
// draw arrows and edges based on the graph

    private void drawLogicLine(Graphics2D g2, int row, int col, Point sw, Point ne, ArrayList<Integer>[] adjList) {
        if (adjList[row].get(col) == 1 && adjList[col].get(row) == 1) {
            drawLineWithArrows(sw, ne, g2, true, true);
        } else if (adjList[row].get(col) == 1) {
            drawLineWithArrows(sw, ne, g2, true, false);
        } else if (adjList[col].get(row) == 1) {
            drawLineWithArrows(sw, ne, g2, false, true);
        } else {
            drawLineWithArrows(sw, ne, g2, false, false);
        }
    }
// draw arrows util function

    private void drawLineWithArrows(Point sw, Point ne, Graphics2D g2, boolean smallSide, boolean largeSide) {
        if (smallSide || largeSide) {
            g2.draw(new Line2D.Double(sw, ne));
        }
        if (largeSide) {
            drawArrowHead(g2, sw, ne);
        }
        if (smallSide) {
            drawArrowHead(g2, ne, sw);
        }
    }
// click the image of Canvas and send it to receiver using the outputStream

    private void clickImage(Canvas yourComponent, OutputStream outputStream) {
        try {
            BufferedImage img = new BufferedImage(550, 550, BufferedImage.TYPE_INT_RGB);
            yourComponent.paint(img.getGraphics());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(img, "png", byteArrayOutputStream);
            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            outputStream.write(size);
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
