package iarks.org.bitbucket.gyromouse;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import xdroid.toaster.Toaster;

public class ServerHandler implements Runnable
{
   @Override
    public void run()
    {
        if (!connectTCP("192.168.1.40"))
        {
            Toaster.toastLong("Cannot find any server.\nTry connecting manually");
            // TODO: 9/7/2017 consider adding a popup to take to connections page
            return;
        }
        while (true)
        {
            try {
                DataOutputStream outToServer = new DataOutputStream(Server.tcpSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(Server.tcpSocket.getInputStream());

                String receivedString = null;
                byte[] receivedBytes = new byte[256];
                int i;


                while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0) {
                    // Translate data bytes to a ASCII string.
                    receivedString = new String(receivedBytes);
                    receivedString = receivedString.trim();
                    Log.e("Received from client>> ", receivedString.trim());
                    break;
                }

                if (receivedString.equals("UDERE?"))
                {
                    Log.e(getClass().getName(), "SERVER IS PINGING");
                    outToServer.write("YES".getBytes(), 0, "YES".getBytes().length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    boolean connectTCP(String ip)
    {
        try
        {
            // this connects client to server
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, 13000), 2000);

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write("CANCONNECT?".getBytes(), 0, "CANCONNECT?".getBytes().length);

            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            String receivedString = null;
            byte[] receivedBytes = new byte[256];
            int i;

            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                // Translate data bytes to a ASCII string.
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.e("Received from client>> ", receivedString.trim());
                break;
            }

            if (receivedString.equals("BUSY"))
            {
                Log.e(getClass().getName(), "Server busy at the moment cannot connect now");
                return false;
            }
            Server.sessionKey = receivedString;
            Server.ServerIP = ip;
            Server.tcpSocket = clientSocket;
            // TODO: 9/7/2017 Server.udpPort= something
            // TODO: 9/7/2017 Server.tcpPort = something
            // TODO: 9/7/2017 Server.name = get the server name
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("", "Whoops! It didn't work!\n");
        }
        return false;
    }
}
