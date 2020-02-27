package sample.connection;

import java.util.List;

public abstract class ClientPacket {

    public static class Join extends ClientPacket
    {
        public String nick;

        public Join(String nick) {
            this.nick = nick+"\n";
        }
    }

    public static class Message extends ClientPacket
    {
        public String message;
        public String receiver;

        public Message(String message, String receivers) {
            this.message = message+"\n";
            this.receiver = receivers;
        }
    }

}
