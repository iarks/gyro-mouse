package iarks.org.bitbucket.gyromouse;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import xdroid.toaster.Toaster;

class ServerListenerUtil implements Runnable
{
    private int resetLatch = 0;
    private int firstTime;

    private final String AM_ALIVE_RESPONSE_STRING = "YES";
    private final String SERVER_DEAD_STRING = "";
    private final String SERVER_ASKING_RESPONSE = "UDERE?";

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
                DataInputStream inFromServer = new DataInputStream(Session.getSessionInstance().getClientTcpSocket().getInputStream());

                String receivedString = null;
                byte[] receivedBytes = new byte[256];
                int i;


                while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
                {
                    receivedString = new String(receivedBytes);
                    receivedString = receivedString.trim();
                    Log.i(getClass().getName(), "Received from server: " + receivedString.trim());
                    break;
                }
                try
                {
                    if (receivedString.equals(SERVER_ASKING_RESPONSE))
                    {
                        Log.i(getClass().getName(), "SERVER IS PINGING");
                        Log.i(getClass().getName(), "send data back to server saying we are alive");
                        Session.getSessionInstance().sendDataToServer(AM_ALIVE_RESPONSE_STRING);
                    }
                    else if (receivedString.equals(SERVER_DEAD_STRING))
                    {
                        Log.i(getClass().getName(), "SERVER IS DEAD?");
                        Toaster.toast("Server has disconnected");
                        resetLatch = 1;
                        Session.reset();
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
}

