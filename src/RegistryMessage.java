/**
 * Created by rui on 10/12/15.
 */
public class RegistryMessage extends Message {
    public String host;
    public String port;
    public String nickName;

    RegistryMessage(String host, String port, String nickName) {
        this.host = host;
        this.port = port;
        this.nickName = nickName;
    }
}
