package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

class UDPClientUtil implements Runnable
{
    private static final String TAG = MainActivity.class.getName();
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private int port;
    private byte[] data;
    private final BlockingQueue<String> sharedQueue;
    private Context contextt;

    UDPClientUtil(BlockingQueue<String> bq, Context context)
    {
        try
        {
            clientSocket = new DatagramSocket();
//            Client.datagramSocket=clientSocket;
            contextt=context;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            sharedQueue = bq;
        }
    }

    void udpSetup()
    {
        try
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contextt);
            IPAddress = InetAddress.getByName(Client.serverIP);
            port = Integer.parseInt(prefs.getString("udpPort","9050"));
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    void clearThread()
    {
        sharedQueue.clear();
    }

    @Override
    public void run()
    {
        while (true)
        {
            synchronized (sharedQueue)
            {
                if (!sharedQueue.isEmpty())
                {
                    try
                    {
                        data = sharedQueue.remove().getBytes("UTF-8");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        DatagramPacket clientRequestPacket = new DatagramPacket(data, data.length, IPAddress, port);
                        clientSocket.send(clientRequestPacket);
                        data=null;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try
                    {
                        sharedQueue.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
