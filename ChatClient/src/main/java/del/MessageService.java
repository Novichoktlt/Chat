package del;

//public class MessageService {
//    private static final String HOST = "localHost";
//    private static final int PORT = 65500;
//    private NetworkHelper networkHelper;
//    private TextArea chatArea;
//
//    public MessageService(TextArea chatArea){
//        this.chatArea = chatArea;
//        connectToServer();
//    }
//
//    private void connectToServer(){
//        try {
//            this.networkHelper = new NetworkHelper(HOST, PORT, this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMessage(String msg){
//        networkHelper.writeMessage(msg);
//    }
//
//    public void  receiveMessage(String msg){
//        chatArea.appendText(msg + System.lineSeparator());
//    }
//}
