package iarks.org.bitbucket.gyromouse;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import xdroid.toaster.Toaster;

class ServerCommunicationUtil implements Runnable
{
    private int resetLatch = 0;
    private int firstTime;

    @Override
    public void run()
    {
        firstTime = 1;
        while (true)
        {
            if (resetLatch == 1 || firstTime == 1)
            {
                try
                {
                    resetLatch = 0;
                    Globals.cdLatch.await();
                }
                catch (InterruptedException e)
                {
                    Log.e(getClass().getName(), "Interrupted Exception");
                    e.printStackTrace();
                    return;

                }
                catch (BrokenBarrierException e)
                {
                    Log.e(getClass().getName(), "BrokenBarrier Exception");
                    e.printStackTrace();
                    return;
                }
            }


            try
            {
                firstTime = 0;
                DataOutputStream outToServer = new DataOutputStream(Client.clientTcpSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(Client.clientTcpSocket.getInputStream());

                String receivedString = null;
                byte[] receivedBytes = new byte[256];
                int i;


                while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
                {
                    receivedString = new String(receivedBytes);
                    receivedString = receivedString.trim();
                    Log.i(getClass().getName(), "Received from server>> " + receivedString.trim());
                    break;
                }
                try
                {
                    if (receivedString.equals("UDERE?"))
                    {
                        Log.i(getClass().getName(), "SERVER IS PINGING");
                        outToServer.write("YES".getBytes(), 0, "YES".getBytes().length);
                        outToServer.flush();
                    } else if (receivedString.equals(""))
                    {
                        Log.i(getClass().getName(), "SERVER IS DEAD?");
                        Toaster.toast("SERVER IS PROBABLY DEAD");
                        resetLatch = 1;
                        Client.reset();
                    }
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }



    static void closePortAndExit()
    {

    }

}

