package del;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class HomeWorkServer {

    private static DataInputStream in;
    private static DataOutputStream out;
    private static Scanner sc;
    private static Thread t1;
    private static Socket socket;

    public static void main(String[] args){

        try (ServerSocket serverSocket = new ServerSocket(65500)) {
            System.out.println("Server started!");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            serverThread();

            while (true) {
                String mas = in.readUTF();


                System.out.println("Server: " + mas);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void serverThread(){
        t1 = new Thread(() -> {
            sc = new Scanner(System.in);
            System.out.println("Введите сообщение: ");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (sc.hasNextLine()) {
                        String messageFromServer = sc.nextLine();
                        out.writeUTF("Server: " + messageFromServer);
                    }

                } catch (IOException e) {
                    break;
                }
            }
            System.out.println("Close System.in thread");
        });

        t1.start();
    }
}
