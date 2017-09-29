package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

class UDPClientUtil implements Runnable
{
    private static final String TAG = UDPClientUtil.class.getName();
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private int port;
    private byte[] data;
    private final BlockingQueue<String> sharedQueue;
    private Context contextt;
    private SharedPreferences prefs;

    UDPClientUtil(BlockingQueue<String> blockingQueue, Context context)
    {
        try
        {
            Log.i(TAG, " in construnctor. Creating socket on this client");
            clientSocket = new DatagramSocket();
            contextt=context;
            Log.i(TAG, " in construnctor. Creating socket on this client complete");
        }
        catch (Exception e)
        {
            Log.i(TAG,"exception occured when creating socket. aborting thread");
            e.printStackTrace();
            return;
        }
        finally
        {
            sharedQueue = blockingQueue;
        }
    }

    void udpSetup()
    {
        Log.i(TAG, "setting up the udp object instance");
        try
        {
            prefs = PreferenceManager.getDefaultSharedPreferences(contextt);
            IPAddress = InetAddress.getByName(Session.getSessionInstance().getServerIP());
            port = Integer.parseInt(prefs.getString("udpPort","9050"));
        }
        catch (UnknownHostException e)
        {
            Log.i(TAG, "unknown host exception occurred");
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
        Log.i(TAG, "thread is running");
        while (true)
        {
            synchronized (sharedQueue)
            {
                if (!sharedQueue.isEmpty())
                {
                    //Log.i(TAG, "data present in queue");
                    try
                    {
                        data = sharedQueue.remove().getBytes("UTF-8");
                        //Log.i(TAG, "obtained data from queue");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        //Log.i(TAG, "encoding not supported");
                        e.printStackTrace();
                    }
                    try
                    {
                        //Log.i(TAG, "sending command to server");
                        DatagramPacket clientRequestPacket = new DatagramPacket(data, data.length, IPAddress, port);
                        clientSocket.send(clientRequestPacket);
                        data=null;
                        //Log.i(TAG, "sending command to server complete");
                    }
                    catch (Exception e)
                    {
                        //Log.i(TAG, "exception occured. unable to send this data");
                        e.printStackTrace();
                    }
                }
                else
                {
                    //Log.i(TAG, "no data in queue");
                    try
                    {
                        //Log.i(TAG, "waiting on queue");
                        sharedQueue.wait();
                    }
                    catch (InterruptedException e)
                    {
                        //Log.i(TAG, "waiting on queue was interrupted");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
