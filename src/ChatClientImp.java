import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Created by rui on 10/12/15.
 */
public class ChatClientImp extends UnicastRemoteObject implements ChatClient {

    ChatClientImp() throws RemoteException {

    }

    public void receiveMessageFromServer(ChatMessage chatMessage) {
        System.out.println("Message " + chatMessage.id + " - " + chatMessage.nickname + ": " + chatMessage.text);
    }

    @Override
    public Boolean isAlive() throws RemoteException {
        return true;
    }

    public static void main(String args[]) {
        ChatServer chatServerImp = null;
        Registry serverRegistry;
        Registry clientRegistry;

        // TODO: Improve external arguments options
        String clientHost = args[0];
        String clientPort = args[1];
        String clientNickName = args[2];
        String serverAddress = ChatServerImp.SERVER_ADDRESS;
        String serverPort = Integer.toString(ChatServerImp.SERVER_PORT);

        try {
            // Create the registry and bind the name and object
            clientRegistry = LocateRegistry.createRegistry(Integer.parseInt(clientPort));
            clientRegistry.rebind("[" + clientNickName + "]" + clientHost + ":" + clientPort, new ChatClientImp());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            serverRegistry = LocateRegistry.getRegistry(serverAddress, (new Integer(serverPort)).intValue());
            chatServerImp = (ChatServer) serverRegistry.lookup(ChatServerImp.CHAT_SERVER_NAME);
            chatServerImp.registryClient(new RegistryMessage(clientHost, clientPort, clientNickName));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        Scanner userText = new Scanner( System.in );
        while(true) {
            String textMessage;
            textMessage = userText.nextLine();

            try {
                chatServerImp.sendMessageToServer(new ChatMessage(0, clientNickName, textMessage));
            } catch (ExportException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
