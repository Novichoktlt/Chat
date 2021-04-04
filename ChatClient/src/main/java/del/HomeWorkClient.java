package del;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class HomeWorkClient {

    private static final String HOST = "localHost";
    private static final int PORT = 65500;
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Scanner sc;
    private static Thread t2;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Connected!");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            clientThread();

            while (true) {
                String mas = in.readUTF();

                System.out.println("Client: " + mas);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static void clientThread(){
        t2 = new Thread(() -> {
            sc = new Scanner(System.in);
            System.out.println("Введите сообщение: ");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (sc.hasNext()) {
                        String messageFromClient = sc.nextLine();
                        out.writeUTF("Client: " + messageFromClient);
                    }

                } catch (IOException e) {
                    break;
                }
            }
            System.out.println("Close System.in thread");
        });

        t2.start();
    }

}
