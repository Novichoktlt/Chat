package del;

public class NetworkHelper {
//    private final Socket socket;
//    private final DataInputStream inputStream;
//    private final DataOutputStream outputStream;
//
//    public NetworkHelper(String address, int PORT, MessageService messageService) throws IOException {
//        this.socket = new Socket(address, PORT);
//        this.inputStream = new DataInputStream(socket.getInputStream());
//        this.outputStream = new DataOutputStream(socket.getOutputStream());
//
//        new Thread(() -> {
//            while (true){
//                try {
//                    String msg = inputStream.readUTF();
//                    Platform.runLater(() -> messageService.receiveMessage(msg));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    public void writeMessage(String msg){
//        try {
//            outputStream.writeUTF(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
