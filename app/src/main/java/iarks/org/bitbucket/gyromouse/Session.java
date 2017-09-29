package iarks.org.bitbucket.gyromouse;

import java.net.DatagramSocket;
import java.net.Socket;

class Session
{
    private static Session sessionInstance = null;

    // params shared between server and client
    private String sessionKey;

    // params describing current server
    private String serverIP = "unavailable", serverName = "unavailable";

    // params describing current client
    private Socket clientTcpSocket;
    private DatagramSocket datagramSocket;


    public static Session getNewSessionInstance()
    {
        sessionInstance = null;
        sessionInstance = new Session();
        return sessionInstance;
    }

    public static Session getSessionInstance()
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

    public void initialise(String sessionKey, String serverIP, String serverName, Socket clientTcpSocket)
    {
        this.sessionKey = sessionKey;
        this.clientTcpSocket = clientTcpSocket;
        this.serverIP = serverIP;
        this.serverName = serverName;
    }

    public String getSessionKey()
    {
        return sessionKey;
    }

    public String getServerIP()
    {
        return serverIP;
    }

    public String getServerName()
    {
        return serverName;
    }

    public Socket getClientTcpSocket()
    {
        return clientTcpSocket;
    }

    public DatagramSocket getDatagramSocket()
    {
        return datagramSocket;
    }

    // reset server details
    static void reset()
    {
        sessionInstance=null;
    }

    static void closeSockets()
    {
//        try
//        {
//            if (this.clientTcpSocket != null)
//                clientTcpSocket.close();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            Log.e("Session","ClientSocketClosed");
//        }
//
//        try
//        {
//           if(datagramSocket!=null)
//               datagramSocket.close();
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//            Log.e("Session","UDPSocketClosed");
//        }
    }
}
