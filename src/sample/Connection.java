package sample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;


public class Connection {

    public Socket socket;
    public String address;
    int port;
    public Listen listen;
    public String serverMessage="";
    public Connection(String address,int port) throws IOException {
        this.address=address;
        this.port=port;
        socket = new Socket(address,port);
    }


    public void listen(Socket socket) throws  IOException
    {
        Thread listen= new Thread(new Listen(socket));
        listen.start();
    }
    public void send(String msg) throws IOException {
        Thread send=new Thread(new Send(msg));
        send.start();
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
            try {
                line = reader.readLine();
                serverMessage=line;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class Send extends Socket implements Runnable
    {
        String msg;
        public Send(String msg) throws IOException {this.msg=msg;}
        DataOutputStream message = new DataOutputStream(socket.getOutputStream());
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
