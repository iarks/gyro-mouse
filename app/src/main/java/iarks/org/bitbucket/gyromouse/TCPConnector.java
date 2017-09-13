package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.BrokenBarrierException;

import es.dmoral.toasty.Toasty;
import xdroid.toaster.Toaster;

class TCPConnector
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
                clientSocket.connect(new InetSocketAddress(server.getServerIP(), Integer.parseInt(CurrentServer.tcpPort)), 2000);
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
            Log.e("TCPConnector", "waiting here");
            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                // Translate data bytes to a ASCII string.
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.e("TCPConnector", "Received from server>> "+receivedString.trim());
                break;
            }

            Log.e("TCPConnector", "here now");

            if (receivedString.equals("BUSY"))
            {
                // if server returns busy it may be connected to another client
                Log.e("TCPConnector", "Server busy at the moment cannot connect now");
                return false;
            }
            else {
                Log.e("inside else", "waiting here");
                CurrentServer.serverIP = server.getServerIP();

                CurrentServer.sessionKey = receivedString;

                CurrentServer.tcpSocket = clientSocket;

                CurrentServer.inetAddress = InetAddress.getByName(server.getServerIP());

                CurrentServer.serverName = server.getServerName();

                Globals.udpClient.udpSetup();

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

            Log.e("TCPConnector", "Whoops! It didn't work!\n");
            e.printStackTrace();
            Log.e("TCPConnector","CAUSE " +e.getCause().toString());
            Log.e("TCPConnector","MESSAGE " +e.getMessage());
            return false;
        }

    }
}
