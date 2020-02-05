package sample.connection;

public abstract class ServerPacket {
    public enum Type
    {
        JOIN,LEAVE,MSG,ERROR
    }
    public Type type;

    public static class JoinOrLeave extends ServerPacket
    {
        public String nick;
        public JoinOrLeave(Type type,String nick)
        {
            this.type=type;
            this.nick=nick;
        }
    }

    public static class Message extends ServerPacket
    {
        public String nick;
        public String message;

        public Message(Type type,String nick, String message) {
            this.nick = nick;
            this.message = message;
            this.type=type;
        }
    }

    public static class Error extends ServerPacket
    {
        public String message;

        public Error(Type type, String message)
        {
            this.type=type;
            this.message=message;
        }
    }
}
