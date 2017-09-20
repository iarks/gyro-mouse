package iarks.org.bitbucket.gyromouse;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import xdroid.toaster.Toaster;

class ServerHandler implements Runnable
{
    private int resetLatch=0;
    private int firstTime;



    @Override
    public void run()
    {
        firstTime=1;
        while (true)
        {
            if(resetLatch == 1 || firstTime==1)
            {
                try
                {
                    resetLatch=0;
                    Globals.cdLatch.await();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }catch (BrokenBarrierException e)
                {
                    e.printStackTrace();
                }
            }



            try
            {
                firstTime=0;
                DataOutputStream outToServer = new DataOutputStream(ConnectedServer.tcpSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(ConnectedServer.tcpSocket.getInputStream());

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
                    Toaster.toast("SERVER IS PROBABLY DEAD");
                    resetLatch=1;
                    ConnectedServer.reset();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

}

