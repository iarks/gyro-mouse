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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


import xdroid.toaster.Toaster;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();

    Button buttonRight, buttonEscape, buttonLeft, buttonWindows;
    ImageButton buttonAR, buttonAL, buttonAU, buttonAD, buttonMouse, buttonScroll;
    BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(5);
    DatabaseHandler dbHandler;
    List<Server> discoveredServer = new ArrayList<>();
    List<Server> preServers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // inflate layout file
        setContentView(R.layout.activity_main);

        // add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initiate database handler
        dbHandler = new DatabaseHandler(this);


        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //CurrentServer.serverIP = SP.getString("ip", "192.168.1.40");
        CurrentServer.tcpPort = SP.getString("tcpPort", "13000");
        CurrentServer.udpPort = SP.getString("udpPort", "9050");

        // create the udp thread
        final UDPClient udpClient = new UDPClient(sharedQueue);
        final Thread udp_thread = new Thread(udpClient);
        udp_thread.start();

        // also start the tcp handler thread
//        ServerHandler serverHandler = new ServerHandler();
//        Thread tcpClientThread = new Thread(serverHandler);
//        tcpClientThread.start();


        // associate ui elements to variables/ objects
        buttonAD = (ImageButton) findViewById(R.id.buttonADown);
        buttonAU = (ImageButton) findViewById(R.id.buttonAUp);
        buttonAR = (ImageButton) findViewById(R.id.buttonARight);
        buttonAL = (ImageButton) findViewById(R.id.buttonALeft);
        buttonMouse = (ImageButton) findViewById(R.id.buttonMouse);
        buttonScroll = (ImageButton) findViewById(R.id.buttonScroll);
        buttonRight = (Button) findViewById(R.id.buttonRight);
        buttonEscape = (Button) findViewById(R.id.buttonEscape);
        buttonWindows = (Button) findViewById(R.id.buttonWin);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);

        // create trackpad objects
        final Trackpad trackpad = new Trackpad(sharedQueue, getApplicationContext());

        // create scrollwheel objects
        final ScrollWheel scrollWheel = new ScrollWheel(sharedQueue, getApplicationContext());

        // add click listeners to buttons
        buttonAD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AD;1;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AD;0;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonAR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AR;1;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AR;0;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonAL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AL;1;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AL;0;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonAU.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AU;1;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("AU;0;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonMouse.setOnTouchListener(new View.OnTouchListener() {
            long timeDown, timeUp;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    timeDown = event.getDownTime();
                    Thread trackpad_thread = new Thread(trackpad);
                    trackpad_thread.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    timeUp = event.getEventTime();
                    udpClient.clearThread();
                    trackpad.stopThread();
                    Log.i(getClass().getName(), "cleared");
                    if (timeUp - timeDown < 500) {
                        try {
                            synchronized (sharedQueue) {
                                sharedQueue.put("LD;x;" + CurrentServer.sessionKey);
                                sharedQueue.put("LU;x;" + CurrentServer.sessionKey);
                                sharedQueue.notifyAll();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });

        buttonScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Thread scrollWheel_thread = new Thread(scrollWheel);
                    scrollWheel_thread.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    udpClient.clearThread();
                    scrollWheel.stopThread();
                }
                return false;
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("RD;x;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("RU;x;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            long downTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = event.getDownTime();
                    try {
                        synchronized (sharedQueue) {
                            sharedQueue.put("LD;x;" + CurrentServer.sessionKey);
                            sharedQueue.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getEventTime() - downTime < 1000) {
                        try {
                            synchronized (sharedQueue) {
                                sharedQueue.put("LU;x;" + CurrentServer.sessionKey);
                                sharedQueue.notifyAll();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Left Mouse Button Pressed", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        buttonEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    synchronized (sharedQueue) {
                        sharedQueue.put("ESC;x;" + CurrentServer.sessionKey);
                        sharedQueue.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonWindows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    synchronized (sharedQueue) {
                        sharedQueue.put("WIN;x;" + CurrentServer.sessionKey);
                        sharedQueue.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        SearchServers ss = new SearchServers();
        ss.setUDP(udpClient);
        ss.execute("");
        // end of onCreate
    }

    // create overflow menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    // add click listenrs to toolbar elements
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wifi) {
            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            return true;
        } else if (id == R.id.action_keyboard) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            return true;
        } else if (id == R.id.action_settings) {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // click listener for keyboard events
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getAction() == 1 || event.getAction() == 2) && event.getKeyCode() != KEYCODE_BACK) {
            KeyboardEvents keyboardEvents = new KeyboardEvents(event, sharedQueue);
            Thread th = new Thread(keyboardEvents);
            th.start();
        }
        return super.dispatchKeyEvent(event);
    }

    class SearchServers extends AsyncTask<String, Void, String>
    {
        boolean connected=false;
        UDPClient udpClient;
        void setUDP(UDPClient udpClientObject)
        {
            udpClient=udpClientObject;
        }

        @Override
        protected String doInBackground(String... params)
        {
            // ist priority - connect to already present servers
            // check number of preexisting servers
            int count = dbHandler.getServerCount();

            Toaster.toast("DATABASE COUNT "+count);

            // if server count is more than 0, then preexisting servers are present - try to connect to those
            if(count>0)
            {
                // get a list of all the servers in the database
                preServers = dbHandler.getAllDBServers();

                // iterate through list and try to connect to each server
                for (Server server: preServers)
                {
                    // connect to that servers IP - connect TCP returns true if connection is established otherwise returns false
                    boolean check = TCPConnector.connectTCP(server, udpClient);
                    //check if connection is established
                    if(check)
                    {
                        //if connection established- set a flag and break - no need to iterate any more
                        connected=true;
                        break;
                    }
                }
            }

            if(!connected||count==0)// if server count is 0 or all previous servers fail to connect come here
            {
                Toaster.toast("No Predefined Server available");
                Toaster.toast("Searching for servers on local network");

                // do a broadcasting server search - this fills the global list with any servers on the network
                discoveredServer = TCPConnector.searchServer();

                if (discoveredServer.size() > 0) {
                    // means we have servers on the network
                    Toaster.toast("We have responses");

                    //connect to the first server on this list
                    // TODO: 9/8/2017 or ask the used to manually select
                    for (Server servers : discoveredServer) {
                        Toaster.toast("checking if available");
                        boolean check = dbHandler.checkAvailable(servers.getServerID());
                        Toaster.toast("check = " + check);
                        if (!check) {
                            //so this server is not present
                            //add new server to database
                            dbHandler.addServerToDB(servers);
                        }
                    }

                    TCPConnector.connectTCP(discoveredServer.remove(0), udpClient);
                } else// if no servers are available on the network as well, just give up. ask user to connect manually or search again
                {
                    Toaster.toast("NO SERVERS AVAILABLE. CONNECT MANUALLY");
                    return "f";
                }
            }
            return "s";
        }

        @Override
        protected void onPostExecute(String result)
        {
            //fancy
        }

        @Override
        protected void onPreExecute()
        {
            //fancy
        }

        @Override
        protected void onProgressUpdate(Void... values){}
    }

}
