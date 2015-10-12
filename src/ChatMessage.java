/**
 * Created by rui on 10/12/15.
 */
public class ChatMessage extends Message {
    public String nickname;
    public String text;

    ChatMessage(int id, String nickname, String text) {
        this.id = id;
        this.nickname = nickname;
        this.text = text;
    }
}
