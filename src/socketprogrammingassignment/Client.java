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
    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private BufferedReader bufferedReaderReadFromServer = null;
    private DataOutputStream dataOutputStream = null;
    private InputStream inputStream = null;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReaderReadFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            System.out.println("Enter matrix");
            int matrixLength = 5;
            dataOutputStream.writeInt(matrixLength);
            for (int i = 0; i < matrixLength; i++) {
                String line = bufferedReader.readLine();
                String[] arr = line.split(" ");
                for (String arr1 : arr) {
                    dataOutputStream.writeInt(Integer.parseInt(arr1));
                }
            }
            System.out.println("Enter n:");
            dataOutputStream.writeInt(Integer.parseInt(bufferedReader.readLine().trim()));
            System.out.println("Enter 1st node");
            dataOutputStream.writeChar(bufferedReader.readLine().trim().charAt(0));
            System.out.println("Enter 2nd node");
            dataOutputStream.writeChar(bufferedReader.readLine().trim().charAt(0));
            System.out.println(bufferedReaderReadFromServer.readLine());

            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
            ImageIO.write(image, "png", new File("image" + System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            bufferedReader.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 6789);
    }
}
