package iarks.org.bitbucket.gyromouse;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;

class NetworkUtil
{

    static boolean connectTCP(Server server)
    {
        try
        {
            Socket clientSocket;
            try
            {
                // create a new socket for a new client
                clientSocket = new Socket();

                //connect this socket to the servers - details are provided
                clientSocket.connect(new InetSocketAddress(server.getServerIP(), Integer.parseInt(ConnectedServer.tcpPort)), 2000);
            }
            catch (Exception e)
            {
                return false;
            }

            // initiate OP stream and ask for connection
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write("CANCONNECT?".getBytes(), 0, "CANCONNECT?".getBytes().length);

            // initiate IP stream
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            // prepare to read
            String receivedString = null;
            byte[] receivedBytes = new byte[256];
            int i;

            // read from server
            Log.e("NetworkUtil", "waiting here");
            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                // Translate data bytes to a ASCII string.
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.e("NetworkUtil", "Received from server>> "+receivedString.trim());
                break;
            }

            Log.e("NetworkUtil", "here now");

            if (receivedString.equals("BUSY"))
            {
                // if server returns busy it may be connected to another client
                Log.e("NetworkUtil", "Server busy at the moment cannot connect now");
                return false;
            }
            else {
                Log.e("inside else", "waiting here");
                ConnectedServer.serverIP = server.getServerIP();

                ConnectedServer.sessionKey = receivedString;

                ConnectedServer.tcpSocket = clientSocket;

                ConnectedServer.inetAddress = InetAddress.getByName(server.getServerIP());

                ConnectedServer.serverName = server.getServerName();

                Globals.udpClientUtil.udpSetup();

                try {
                    Globals.cdLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
        catch (IOException e)
        {

            Log.e("NetworkUtil", "Whoops! It didn't work!\n");
            e.printStackTrace();
            Log.e("NetworkUtil","CAUSE " +e.getCause().toString());
            Log.e("NetworkUtil","MESSAGE " +e.getMessage());
            return false;
        }

    }
}
