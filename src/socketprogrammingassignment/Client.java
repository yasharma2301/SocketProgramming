/*
    Socket Programming assignment client file
    @author Yash Sharma
    Roll no. 1810110283
*/

package socketprogrammingassignment;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class Client {

    private Socket socket = null;
    private BufferedReader bufferedReader = null;
    private BufferedReader bufferedReaderReadFromServer = null;
    private DataOutputStream dataOutputStream = null;
    private InputStream inputStream = null;

    public Client(String address, int port) {
        try {
            // create a new socket object with given address and port
            socket = new Socket(address, port);

            System.out.println("Connected"); // at this point client is connected to server via the mentioned port

            // take input from client/user through terminal
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            // send output to socket
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // read from server
            bufferedReaderReadFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // read from input Stream
            inputStream = socket.getInputStream();

        } catch (IOException e) {
            System.out.println(e);
        }

        // take inputs from client
        try {
            // take input for adjacency matrix
            System.out.println("Enter a space seperated adjancency matrix (5x5):");
            int matrixLength = 5;
            dataOutputStream.writeInt(matrixLength);
            for (int i = 0; i < matrixLength; i++) {
                String line = bufferedReader.readLine();
                String[] arr = line.split(" ");
                for (String arr1 : arr) {
                    dataOutputStream.writeInt(Integer.parseInt(arr1));
                }
            }
            // take input for path length
            System.out.println("Enter the length of the path (n):");
            dataOutputStream.writeInt(Integer.parseInt(bufferedReader.readLine().trim()));

            // take input for node1
            System.out.println("Enter node 1 (upper cased letter, e.g., 'A', 'B' ... 'E'):");
            dataOutputStream.writeChar(bufferedReader.readLine().trim().charAt(0));

            // take input for node2
            System.out.println("Enter node 2 (upper cased letter,e.g., 'A', 'B' ... 'E'):");
            dataOutputStream.writeChar(bufferedReader.readLine().trim().charAt(0));

            // console log the value (YES/NO)
            System.out.println(bufferedReaderReadFromServer.readLine());
            
            // receive the image from server via input stream
            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
            
            // console logs for extra confirmation
            System.out.println("Received an image of dimensions" + image.getHeight() + "x" + image.getWidth() + ": at timeStamp: " + System.currentTimeMillis()+" for confirmation.");
            System.out.println("Image name: "+"GraphImageFrame" + System.currentTimeMillis() + ".png");
            
            // wites the image
            ImageIO.write(image, "png", new File("GraphImageFrame" + System.currentTimeMillis() + ".png"));

        } catch (IOException e) {
            System.out.println(e);
        }

        // close all the connections
        try {
            bufferedReader.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        // create a client class object
        Client client = new Client("127.0.0.1", 9999);
    }
}
