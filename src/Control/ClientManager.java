package Control;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public abstract class ClientManager
{
    private Socket socket;
    private Thread msgListenerThread;
    private final String ip = "127.0.0.1";


    public ClientManager( int serverPort) throws UnknownHostException, IOException
    {
        socket = new Socket( ip ,  serverPort);
        msgListenerThread = new Thread( new Runnable() {
            public void run()
            {
                listenForMessages();
            }
        });
        msgListenerThread.start();
        System.out.println("CLIENT IS CREATED");
    }
    public void sendMessage( String msg)
    {
        byte[] data = msg.getBytes();
        try
        {
            socket.getOutputStream().write( data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void listenForMessages()
    {
        String msg;
        InputStream stream;

        try
        {
            stream = socket.getInputStream();
        }
        catch (IOException e)
        {
            return;
        }

        while ( true)
        {
            msg = "";
            try
            {
                msg += (char) stream.read();
            }
            catch (IOException e)
            {
                return;
            }
        }
    }

    public abstract void received(String msg) throws URISyntaxException;

    public void close()
    {
        msgListenerThread.interrupt();
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}