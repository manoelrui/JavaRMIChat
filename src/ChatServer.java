import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by rui on 10/12/15.
 */
public interface ChatServer extends Remote {
    public void registryClient(RegistryMessage rMessage) throws RemoteException;

    public void sendMessageToServer(ChatMessage chatMessage) throws RemoteException;
}
