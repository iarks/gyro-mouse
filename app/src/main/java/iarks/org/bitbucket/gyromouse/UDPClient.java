package iarks.org.bitbucket.gyromouse;

/**
 * Created by Arkadeep on 03-Aug-17.
 */


import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

class UDPClient implements Runnable {
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private int port;
    private byte[] data;
    private final BlockingQueue<String> sharedQueue;

    UDPClient(int portNumber, String ip, BlockingQueue<String> bq) {
        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("192.168.1.40");
            port = 49443;

            Log.println(Log.INFO, "UDPClient", "HERE IN CONSTRUCTOR");
        } catch (Exception e) {
        } finally {
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
        Log.e("UDPClient", "Thread start");
        while (true)
        {
            if (!sharedQueue.isEmpty())
            {
                data = sharedQueue.remove().getBytes();
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
                try
                {
                    synchronized (sharedQueue)
                    {
                        Log.println(Log.INFO, "UDPClient", "SENDING THE DATA. Run()");
                        Log.println(Log.INFO, "UDPClient", sendPacket.getData().toString());
                        clientSocket.send(sendPacket);
                        data=null;
                        Log.println(Log.INFO, "UDPClient", "Data Sent");
                    }
                } catch (Exception e) {
                    Log.e("UDPClient", "Run() Exception - " + e.getMessage());
    //              Log.d("e.message = ", e.getMessage());
                }
            }
        }
    }
}
