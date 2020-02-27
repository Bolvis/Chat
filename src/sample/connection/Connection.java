package sample.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Connection {

    public Socket socket;
    public boolean connected;
    public  ServerPacket serverPacket;
    public List<ServerPacket> serverMessages;


    public Connection(String address,int port) throws IOException {
        serverMessages = new ArrayList<>();
        socket = new Socket(address,port);
        connected = true;
    }


    public void listen(Socket socket) throws  IOException
    {
        Thread listen = new Thread(new Listen(socket));
        listen.start();
    }
    public void send(String msg) throws IOException {
        Thread send=new Thread(new Send(msg));
        send.start();
    }

    public void flush()
    {
        serverMessages = new ArrayList<>();
    }

    private class Listen extends Socket implements Runnable
    {

        BufferedReader reader;
        String line;
        Listen(Socket socket) throws IOException {
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(socket).getInputStream()));
        }
        public void run()
        {
            while (connected){
            try {
                String[] convertedLine;
                line = reader.readLine();
                convertedLine=line.split(";");
                switch(convertedLine[0])
                {
                    case "JOIN":
                       serverPacket = new ServerPacket.JoinOrLeave(ServerPacket.Type.JOIN,convertedLine[1]);
                        break;

                    case "LEAVE":
                       serverPacket = new ServerPacket.JoinOrLeave(ServerPacket.Type.LEAVE,convertedLine[1]);
                        break;

                    case "MSG":
                       serverPacket = new ServerPacket.Message(ServerPacket.Type.MSG,convertedLine[1],convertedLine[2]);
                        break;

                    case "ERROR":
                       serverPacket = new ServerPacket.Error(ServerPacket.Type.ERROR,convertedLine[1]);
                        break;

                    default:
                        break;
                }
                serverMessages.add(serverPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }
    }


    private class Send extends Socket implements Runnable
    {
        String msg;
        DataOutputStream message;

        public Send(String msg) throws IOException
        {
            this.msg = msg;
            message = new DataOutputStream(socket.getOutputStream());
        }

        public void run()
        {
            try {
                message.write(msg.getBytes());
                message.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
