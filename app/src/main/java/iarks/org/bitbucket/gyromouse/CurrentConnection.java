package iarks.org.bitbucket.gyromouse;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

class CurrentConnection
{
    // params shared between server and client
    static String sessionKey;

    // params describing current server
    static String serverIP = "unavailable", serverName = "unavailable";

    // params describing current client
    static Socket clientTcpSocket;
    static DatagramSocket datagramSocket;


    private CurrentConnection(){}

    // reset server details
    static void reset()
    {
        serverName = "unavailable";
        serverIP = "unavailable";
        sessionKey="";
        clientTcpSocket =null;
    }

    static void closeSockets()
    {
        try
        {
            if (clientTcpSocket != null)
                clientTcpSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("CurrentConnection","ClientSocketClosed");
        }

        try
        {
           if(datagramSocket!=null)
               datagramSocket.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e("CurrentConnection","UDPSocketClosed");
        }
    }
}
