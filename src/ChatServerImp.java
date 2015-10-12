import java.net.InetAddress;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ChatServerImp extends UnicastRemoteObject implements ChatServer {
    public static final int SERVER_PORT = 1099;
    public static String SERVER_ADDRESS;
    public static final String CHAT_SERVER_NAME = "ChatServer";
    public static int messageCounter;
    Registry serverRegistry;
    Registry clientRegistry;
    public static ArrayList<RegistryMessage> clientRegistries;
    public static ArrayList<RegistryMessage> clientsToRemove;

    static {
        try {
            // Get address of host
            SERVER_ADDRESS = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        messageCounter = 0;
    }

    ChatServerImp() throws RemoteException{
        try {
            // Create the registry and bind the name and object
            serverRegistry = LocateRegistry.createRegistry(SERVER_PORT);
            serverRegistry.rebind(CHAT_SERVER_NAME, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        clientRegistries = new ArrayList<RegistryMessage>();
        clientsToRemove = new ArrayList<RegistryMessage>();
    }

    @Override
    public void registryClient(RegistryMessage rMessage) {
        // TODO: Check client replications
        clientRegistries.add(rMessage);

        System.out.println("Number of clients: " + clientRegistries.size());
        System.out.println("Nickname: " + rMessage.nickName + "\nHost: " +
                rMessage.host + "\nPort: " + rMessage.port + "\n");

        // TODO: to create a callback to this behaviour.
        // TODO: Check for ExportException: Port already in use.
        String newClientAlert = rMessage.nickName + " entered in the chat.";
        try {
            sendMessageToServer(new ChatMessage(messageCounter, CHAT_SERVER_NAME, newClientAlert));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageToServer(ChatMessage chatMessage) throws RemoteException {

        // TODO: to create a callback to this behaviour.
        updateAliveClients();

        // TODO: to create a callback to this behaviour.
        alertClients();

        for(RegistryMessage clientMessage : clientRegistries) {
            try {
                ChatClient tempClient;
                clientRegistry = LocateRegistry.getRegistry(
                        clientMessage.host, (new Integer(clientMessage.port)).intValue());
                tempClient = (ChatClient) clientRegistry.lookup("[" + clientMessage.nickName + "]" + clientMessage.host + ":" + clientMessage.port);
                chatMessage.id = messageCounter;
                tempClient.receiveMessageFromServer(chatMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        messageCounter++;
    }

    public void updateAliveClients() {
        for(RegistryMessage clientMessage : clientRegistries) {
            boolean clientIsAlive = false;
            try {
                ChatClient tempClient;
                clientRegistry = LocateRegistry.getRegistry(
                        clientMessage.host, (new Integer(clientMessage.port)).intValue());
                tempClient = (ChatClient) clientRegistry.lookup("[" + clientMessage.nickName + "]" + clientMessage.host + ":" + clientMessage.port);
                clientIsAlive = tempClient.isAlive();
            } catch (Exception e) {
                //e.printStackTrace();
            } finally {
                if(!clientIsAlive) {
                    clientsToRemove.add(clientMessage);
                }
            }
        }
    }

    public void alertClients() {
        if (!clientsToRemove.isEmpty()) {
            for (RegistryMessage clientMessage : clientsToRemove) {
                clientRegistries.remove(clientMessage);
            }

            for (RegistryMessage clientToAlertMessage : clientRegistries) {
                for (RegistryMessage clientToRemoved : clientsToRemove) {
                    try {
                        Registry clientRegistry;
                        ChatClient tempClient;
                        String leftMessage;
                        clientRegistry = LocateRegistry.getRegistry(
                                clientToAlertMessage.host, (new Integer(clientToAlertMessage.port)).intValue());
                        tempClient = (ChatClient) clientRegistry.lookup("[" + clientToAlertMessage.nickName + "]" + clientToAlertMessage.host + ":" + clientToAlertMessage.port);
                        leftMessage = clientToRemoved.nickName + " left the chat";
                        tempClient.receiveMessageFromServer(new ChatMessage(messageCounter, CHAT_SERVER_NAME, leftMessage));
                        messageCounter++;
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            ChatServerImp.clientsToRemove.clear();
            System.out.println("Number of clients: " + clientRegistries.size());
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServerImp();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server Started with success on " + SERVER_ADDRESS + ":" + SERVER_PORT + "\n");
    }


}
