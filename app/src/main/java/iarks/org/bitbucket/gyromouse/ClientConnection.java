package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;

class ClientConnection
{
    private final static String SESSION_REQUEST_STRING = "CANCONNECT?";
    private final static String SESSION_RESPONSE_STRING = "BUSY";

    // attempts to connect the client to the server passed as argument
    static boolean connectClient(Server server, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String receivedString = null;

        // initiate a socket for connecting to the server
        Socket clientSocket=null;
        try
        {
            Log.e(context.getClass().getName(), "attempting to connect to server");
            clientSocket = new Socket();
            int portNumber = Integer.parseInt(prefs.getString("tcpPort", "13000"));
            // connect the client socket to the server socket
            clientSocket.connect(new InetSocketAddress(server.getServerIP(), portNumber), 2000);
            Log.e(context.getClass().getName(), "attempting to connect to server complete");
        }
        catch (Exception e)
        {
            Log.e(context.getClass().getName(), "could not connect to given server");
            e.printStackTrace();
            if (clientSocket!= null)
            {
                try
                {
                    clientSocket.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
            return false;
        }


        try
        {

            Log.i(context.getClass().getName(), "requesting session from server");
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            String configuredUdpPort = prefs.getString("udpPort", "9050");
            outToServer.write((SESSION_REQUEST_STRING + configuredUdpPort).getBytes(), 0, (SESSION_REQUEST_STRING + configuredUdpPort).getBytes().length);
            Log.i(context.getClass().getName(), "requesting session from server complete");
        }
        catch (IOException e)
        {
            Log.e(context.getClass().getName(), "could not request server for session");
            e.printStackTrace();
            if (clientSocket!= null)
            {
                try
                {
                    clientSocket.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
            return false;
        }


        try
        {
            Log.e(context.getClass().getName(), "preparing for server response");
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
            byte[] receivedBytes = new byte[256];
            int i;
            Log.i(context.getClass().getName(), "waiting for server to respond to can connect request");
            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.e(context.getClass().getName(), "received from server : " + receivedString.trim());
                break;
            }
            Log.i("ClientConnection", "server has responded");
        }
        catch (IOException e)
        {
            Log.e(context.getClass().getName(), "unable to obtain client response");
            e.printStackTrace();
            if (clientSocket!= null)
            {
                try
                {
                    clientSocket.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
            return false;
        }

        if (receivedString != null && receivedString.equals(SESSION_RESPONSE_STRING))
        {
            // if server returns busy it may be connected to another client
            Log.e(context.getClass().getName(), "server responded with busy");
            return false;
        }
        else
        {
            Client.serverIP = server.getServerIP();
            Client.sessionKey = receivedString;
            Client.clientTcpSocket = clientSocket;
            //Client.serverInetAddress = InetAddress.getByName(server.getServerIP());
            Client.serverName = server.getServerName();
            Globals.udpClientUtil.udpSetup();
            try
            {
                Globals.cdLatch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Log.e("ClientConnection", "CAUSE " + e.getCause().toString());
                Log.e("ClientConnection", "MESSAGE " + e.getMessage());
                return false;
            }
            catch (BrokenBarrierException e)
            {
                e.printStackTrace();
                Log.e("ClientConnection", "CAUSE " + e.getCause().toString());
                Log.e("ClientConnection", "MESSAGE " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}