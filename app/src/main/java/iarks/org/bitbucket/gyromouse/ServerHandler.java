package iarks.org.bitbucket.gyromouse;
import android.util.Log;

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
import java.util.List;

import xdroid.toaster.Toaster;

class ServerHandler implements Runnable
{

    ServerHandler()
    {

    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                DataOutputStream outToServer = new DataOutputStream(CurrentServer.tcpSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(CurrentServer.tcpSocket.getInputStream());

                String receivedString = null;
                byte[] receivedBytes = new byte[256];
                int i;


                while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
                {
                    // Translate data bytes to a ASCII string.
                    receivedString = new String(receivedBytes);
                    receivedString = receivedString.trim();
                    Log.i(getClass().getName(),"Received from server>> "+ receivedString.trim());
                    break;
                }

                if (receivedString.equals("UDERE?"))
                {
                    Log.i(getClass().getName(), "SERVER IS PINGING");
                    outToServer.write("YES".getBytes(), 0, "YES".getBytes().length);
                    outToServer.flush();
                }
                else if (receivedString.equals(""))
                {
                    Log.i(getClass().getName(), "SERVER IS DEAD?");
                    // TODO: 9/8/2017 goback to the start
                    Toaster.toast("SERVER IS PROBABLY DEAD");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

