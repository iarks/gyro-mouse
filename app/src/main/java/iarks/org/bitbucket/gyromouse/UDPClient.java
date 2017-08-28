package iarks.org.bitbucket.gyromouse;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

class UDPClient implements Runnable
{
    private static final String TAG = MainActivity.class.getName();
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private int port;
    private byte[] data;
    private final BlockingQueue<String> sharedQueue;

    UDPClient(int portNumber, String ip, BlockingQueue<String> bq)
    {
        try
        {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName(ip);
            port = portNumber;

            Log.println(Log.INFO, "UDPClient", "HERE IN CONSTRUCTOR");
        }
        catch (Exception e)
        {
        }
        finally
        {
            sharedQueue = bq;
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
            Log.e(TAG,"inWhile");
            synchronized (sharedQueue)
            {
                if (!sharedQueue.isEmpty())
                {
                    try
                    {
                        data = sharedQueue.remove().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
                    try
                    {
                        clientSocket.send(sendPacket);
                        data=null;
                    }
                    catch (Exception e)
                    {

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
