package iarks.org.bitbucket.gyromouse;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

class Session implements Runnable
{
    private static Session sessionInstance = null;

    // params shared between server and client
    private String sessionKey;

    private String message;


    // params describing current server
    private String serverIP = "unavailable", serverName = "unavailable";

    // params describing current client
    private Socket clientTcpSocket;
    private DatagramSocket datagramSocket;


    static Session getNewSessionInstance()
    {
        sessionInstance = null;
        sessionInstance = new Session();
        return sessionInstance;
    }

    static Session getSessionInstance()
    {
        if (sessionInstance == null)
            sessionInstance = new Session();
        return sessionInstance;
    }

    // prevent initialisation
    private Session()
    {
        sessionKey="";
        serverIP = "unavailable";
        serverName = "unavailable";
        clientTcpSocket=null;
        datagramSocket=null;
    }

    void initialise(String sessionKey, String serverIP, String serverName, Socket clientTcpSocket)
    {
        this.sessionKey = sessionKey;
        this.clientTcpSocket = clientTcpSocket;
        this.serverIP = serverIP;
        this.serverName = serverName;
    }

    String getSessionKey()
    {
        return sessionKey;
    }

    String getServerIP()
    {
        return serverIP;
    }

    String getServerName()
    {
        return serverName;
    }

    Socket getClientTcpSocket()
    {
        return this.clientTcpSocket;
    }

    public DatagramSocket getDatagramSocket()
    {
        return datagramSocket;
    }

    private String getMessage()
    {
        return this.message;
    }

    // reset server details
    static void reset()
    {
        try
        {
            sessionInstance.getClientTcpSocket().close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        sessionInstance=null;
    }

    void sendDataToServer(String message)
    {
        Log.i(getClass().getName(),"sending data asynchronously");
        Thread sendOut = new Thread(this);
        this.message = message;
        sendOut.start();
        Log.i(getClass().getName(),"sending data asynchronously done");
    }



    // this sends out data to the server asynchronously
    @Override
    public void run()
    {
        message = getMessage();
        DataOutputStream outToServer;
        try
        {
            outToServer = new DataOutputStream(getClientTcpSocket().getOutputStream());
            outToServer.write(message.getBytes(), 0, message.getBytes().length);
            outToServer.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

