package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();

    Button buttonRight,buttonEscape,buttonLeft,buttonWindows;
    ImageButton buttonAR,buttonAL,buttonAU,buttonAD,buttonMouse,buttonScroll;
    BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(5);

    Thread tcpClientThread;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String ip = SP.getString("ip", "192.168.1.40");
        String port = SP.getString("port","49443");

        final UDPClient udpClient = new UDPClient(Integer.parseInt(port),ip, sharedQueue);
        final Thread udp_thread = new Thread(udpClient);
        udp_thread.start();

        buttonAD=(ImageButton)findViewById(R.id.buttonADown);
        buttonAU=(ImageButton)findViewById(R.id.buttonAUp);
        buttonAR=(ImageButton)findViewById(R.id.buttonARight);
        buttonAL=(ImageButton)findViewById(R.id.buttonALeft);
        buttonMouse = (ImageButton)findViewById(R.id.buttonMouse);
        buttonScroll = (ImageButton)findViewById(R.id.buttonScroll);
        buttonRight = (Button)findViewById(R.id.buttonRight);

        buttonEscape = (Button)findViewById(R.id.buttonEscape);
        buttonWindows = (Button)findViewById(R.id.buttonWin);
        buttonLeft=(Button)findViewById(R.id.buttonLeft);


        final Trackpad trackpad = new Trackpad(sharedQueue,getApplicationContext());
        final ScrollWheel scrollWheel = new ScrollWheel(sharedQueue,getApplicationContext());


        buttonAD.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AD" + "\"," + "\"Y\":\"" + 1 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AD" + "\"," + "\"Y\":\"" + 0 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonAR.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AR" + "\"," + "\"Y\":\"" + 1 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AR" + "\"," + "\"Y\":\"" + 0 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonAL.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AL" + "\"," + "\"Y\":\"" + 1 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }

                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AL" + "\"," + "\"Y\":\"" + 0 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonAU.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AU" + "\"," + "\"Y\":\"" + 1 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "AU" + "\"," + "\"Y\":\"" + 0 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonMouse.setOnTouchListener(new View.OnTouchListener()
        {
            long timeDown,timeUp;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    timeDown=event.getDownTime();
                    Thread trackpad_thread = new Thread(trackpad);
                    trackpad_thread.start();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    timeUp=event.getEventTime();
                    udpClient.clearThread();
                    trackpad.stopThread();
                    Log.e("MainActivity","cleared");
                    if(timeUp-timeDown<500)
                    {
                        try
                        {
                            synchronized (sharedQueue)
                            {
                                sharedQueue.put("{\"X\":" + "\"" + "LD" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                                sharedQueue.put("{\"X\":" + "\"" + "LU" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                                sharedQueue.notifyAll();
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });

        buttonScroll.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Thread scrollWheel_thread = new Thread(scrollWheel);
                    scrollWheel_thread.start();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    udpClient.clearThread();
                    scrollWheel.stopThread();
                }
                return false;
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "RD" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "RU" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener()
        {
            long downTime=0;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    downTime=event.getDownTime();
                    try
                    {
                        synchronized (sharedQueue) {
                            sharedQueue.put("{\"X\":" + "\"" + "LD" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if(event.getEventTime()-downTime<1000)
                    {
                        try
                        {
                            synchronized (sharedQueue) {
                                sharedQueue.put("{\"X\":" + "\"" + "LU" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                                sharedQueue.notifyAll();
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Left Mouse Button Pressed", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        buttonEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    synchronized (sharedQueue) {
                        sharedQueue.put("{\"X\":" + "\"" + "ESC" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                        sharedQueue.notifyAll();
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        buttonWindows.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    synchronized (sharedQueue) {
                        sharedQueue.put("{\"X\":" + "\"" + "WIN" + "\"," + "\"Y\":\"" + 0.00 + "\","+ "\"Z\":"+ "\""+Server.sessionKey+"\""+"}\0");
                        sharedQueue.notifyAll();
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        //UI initialised - now look for servers
        new ServerFind().execute();

        // also start the tcp thread and connect to already present servers
        Thread tcpClientThread = new Thread(new ServerHandler());
        tcpClientThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wifi)
        {
            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            return true;
        }
        else if(id == R.id.action_keyboard)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
            return true;
        }
        else if(id == R.id.action_settings)
        {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if ((event.getAction() == 1 || event.getAction() == 2) && event.getKeyCode()!=KEYCODE_BACK)
        {
            KeyboardEvents keyboardEvents = new KeyboardEvents(event, sharedQueue);
            Thread th = new Thread(keyboardEvents);
            th.start();
        }
        return super.dispatchKeyEvent(event);
    }



    private class ServerFind extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                byte[] sendData = "{\"X\":\"CANHAVEIP?\",\"Y\":\"0\"}".getBytes("UTF-8");
                DatagramSocket datagramSocket=null;

                try
                {
                    for(int i=1;i<=10;i++)
                    {
                        datagramSocket = new DatagramSocket();
                        datagramSocket.setBroadcast(true);

                        //                    try
                        //                    {
                        //                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 9050);
                        //                        datagramSocket.send(sendPacket);
                        //                        Log.e(getClass().getName(), ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
                        //                    }
                        //                    catch (Exception e)
                        //                    {
                        //                        e.printStackTrace();
                        //                    }

                        // Broadcast the message over all the network interfaces
                        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                        while (interfaces.hasMoreElements())
                        {
                            Log.e(getClass().getName(), ">>> In While, looping network interfaces");
                            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                            if (networkInterface.isLoopback() || !networkInterface.isUp())
                            {
                                continue; // Don't want to broadcast to the loopback interface
                            }

                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                            {
                                InetAddress broadcast = interfaceAddress.getBroadcast();
                                if (broadcast == null)
                                {
                                    continue;
                                }

                                // Send the broadcast package!
                                try
                                {
                                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 9050);
                                    datagramSocket.send(sendPacket);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                Log.e(getClass().getName(), ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                            }
                        }

                        Log.e(getClass().getName(), ">>> Done looping over all network interfaces. Now waiting for a reply!");

                        //Wait for a response
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length, 2000);
                        datagramSocket.receive(receivePacket);

                        //We have a response
                        Log.e(getClass().getName(), ">>> Broadcast response from server at: " + receivePacket.getAddress().getHostAddress());
                    }

                    datagramSocket.close();
                }
                catch (Exception e)
                {
                    Log.i("", "ERROR");
                    Log.e("ERROR", "HERE");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        public void showToast(final String toast)
        {
            runOnUiThread(new Runnable() {
                public void run()
                {
                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
