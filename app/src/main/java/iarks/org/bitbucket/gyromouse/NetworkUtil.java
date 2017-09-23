package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
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

    static boolean connectTCP(Server server,Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try
        {
            Socket clientSocket;
            try
            {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(server.getServerIP(), Integer.parseInt(prefs.getString("tcpPort","13000"))), 2000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            Log.i("NetworkUtil", "Asking server for connection request");

            String temp = prefs.getString("udpPort","9050");
            Log.e(context.getClass().getName(),temp);
            outToServer.write(("CANCONNECT?"+temp).getBytes(), 0, ("CANCONNECT?"+temp).getBytes().length);


            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            String receivedString = null;
            byte[] receivedBytes = new byte[256];
            int i;

            Log.i("NetworkUtil", "waiting for server to respond to can connect request");
            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.e("NetworkUtil", "Received from server>> "+receivedString.trim());
                break;
            }

            Log.i("NetworkUtil", "server has responded");

            try
            {
                if (receivedString.equals("BUSY"))
                {
                    // if server returns busy it may be connected to another client
                    Log.e("NetworkUtil", "Server busy at the moment cannot connect now");
                    return false;
                }
                else
                {
                    CurrentConnection.serverIP = server.getServerIP();

                    CurrentConnection.sessionKey = receivedString;

                    CurrentConnection.clientTcpSocket = clientSocket;

                    //CurrentConnection.serverInetAddress = InetAddress.getByName(server.getServerIP());

                    CurrentConnection.serverName = server.getServerName();

                    Globals.udpClientUtil.udpSetup();

                    try
                    {
                        Globals.cdLatch.await();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        Log.e("NetworkUtil","CAUSE " +e.getCause().toString());
                        Log.e("NetworkUtil","MESSAGE " +e.getMessage());
                        return false;
                    }
                    catch (BrokenBarrierException e)
                    {
                        e.printStackTrace();
                        Log.e("NetworkUtil","CAUSE " +e.getCause().toString());
                        Log.e("NetworkUtil","MESSAGE " +e.getMessage());
                        return false;
                    }
                }

                return true;
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
                Log.e("NetworkUtil","CAUSE " +e.getCause().toString());
                Log.e("NetworkUtil","MESSAGE " +e.getMessage());
                return false;
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("NetworkUtil","CAUSE " +e.getCause().toString());
            Log.e("NetworkUtil","MESSAGE " +e.getMessage());
            return false;
        }

    }
}
