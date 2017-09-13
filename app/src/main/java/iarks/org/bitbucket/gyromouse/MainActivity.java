package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingDeque;


import es.dmoral.toasty.Toasty;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    Button buttonRight, buttonEscape, buttonLeft, buttonWindows;
    ImageButton buttonAR, buttonAL, buttonAU, buttonAD, buttonMouse, buttonScroll;
    BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(5);
    DatabaseHandler dbHandler;
    ArrayList<Server> discoveredServer = new ArrayList<>();
    List<Server> preServers = new ArrayList<>();
    FabSpeedDial fabSpeedDial;
    CyclicBarrier latch;
    static SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        latch = new CyclicBarrier(2);
        Globals.cdLatch = latch;

        // inflate layout file
        setContentView(R.layout.activity_main);

        // add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initiate database handler
        dbHandler = new DatabaseHandler(this);
        Globals.databaseHandler = dbHandler;

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        // create the udp thread
        final UDPClient udpClient = new UDPClient(sharedQueue);
        Globals.udpClient = udpClient;
        final Thread udp_thread = new Thread(udpClient);
        udp_thread.start();

        // also start the tcp handler thread
        ServerHandler serverHandler = new ServerHandler();
        Thread tcpClientThread = new Thread(serverHandler);
        tcpClientThread.start();


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
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
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

        ConnectToServers ss = new ConnectToServers();
        ss.execute("");
        // end of onCreate

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.ff);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_scanNetwork) {
                    new ScanNetwork().execute("");
                    return true;
                } else if (id == R.id.action_dbServers) {
                    Intent myIntent = new Intent(MainActivity.this, ServerListActivity.class);
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                return false;
            }
        });
    }

    // create overflow menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            KeyboardEventsHandler keyboardEvents = new KeyboardEventsHandler(event, sharedQueue);
            Thread th = new Thread(keyboardEvents);
            th.start();
        }
        return super.dispatchKeyEvent(event);
    }


    private class ConnectToServers extends AsyncTask<String, Void, String> {
        boolean connected = false;
        LoadToast lt = new LoadToast(MainActivity.this);

        @Override
        protected String doInBackground(String... params) {

            int count = dbHandler.getServerCount();

            if (count > 0) {
                preServers = dbHandler.getAllDBServers();

                for (Server server : preServers) {

                    if (TCPConnector.connectTCP(server)) {
                        connected = true;
                        return "s";
                    }
                }
            }

            if (!connected || count == 0) {
                discoveredServer = NetworkScanner.searchServer();

                if (discoveredServer.size() == 1) {
                    //auto connect if only one server is available
                    for (Server servers : discoveredServer) {
                        boolean check = dbHandler.checkAvailable(servers.getServerID());
                        if (!check) {
                            // add them to database
                            dbHandler.addServerToDB(servers);
                        }
                    }
                    if (TCPConnector.connectTCP(discoveredServer.remove(0))) {
                        connected = true;
                        return "s";
                    } else
                        return "f";
                } else if (discoveredServer.size() > 1) {
                    // show if more than 1 servers are available
                    for (Server servers : discoveredServer) {
                        boolean check = dbHandler.checkAvailable(servers.getServerID());
                        if (!check) {
                            dbHandler.addServerToDB(servers);
                        }
                    }
                    return "dialog";
                } else {
                    return "f";
                }
            }
            return "f";
        }

        @Override
        protected void onPostExecute(String result)
        {
            switch (result)
            {
                case "f":
                    lt.error();
                    Toasty.error(MainActivity.this, "Could Not Connect to any server", Toast.LENGTH_SHORT, true).show();
                    break;
                case "dialog":
                    lt.success();
                    AdapterServers adapter = new AdapterServers(MainActivity.this, discoveredServer);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.dialog_list, null);
                    alertDialog.setView(convertView);
                    alertDialog.setTitle("Serveral servers found over network");
                    ListView lv = (ListView) convertView.findViewById(R.id.lv);

                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                        {
                            TextView ip = (TextView) v.findViewById(R.id.ip);
                            TextView name = (TextView) v.findViewById(R.id.name);
                            Server server = new Server("_" + name + "_" + ip, name.getText().toString(), ip.getText().toString());
                            TryConnection tryConnection = new TryConnection(server);
                            tryConnection.execute("");
                        }
                    });

                    alertDialog.show();
                    break;
                default:
                    lt.success();
                    Toasty.success(MainActivity.this, "connected to " + CurrentServer.serverName + " at " + CurrentServer.serverIP, Toast.LENGTH_SHORT, true).show();
            }
        }

        @Override
        protected void onPreExecute() {
            lt.setText("Searching for servers");
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    private class ScanNetwork extends AsyncTask<String, Void, String> {
        LoadToast lt = new LoadToast(MainActivity.this);

        @Override
        protected String doInBackground(String... params) {

            discoveredServer = NetworkScanner.searchServer();

            if (discoveredServer.size() > 0) {
                for (Server servers : discoveredServer) {
                    boolean check = dbHandler.checkAvailable(servers.getServerID());
                    if (!check) {
                        // add newly found servers to database
                        dbHandler.addServerToDB(servers);
                    }
                }
                return "1";
            } else {
                return "0";
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("0")) {
                lt.error();
                Toasty.error(MainActivity.this, "Could Not find any servers on local network", Toast.LENGTH_SHORT, true).show();
            } else {
                lt.success();

                AdapterServers adapter = new AdapterServers(MainActivity.this, discoveredServer);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_list, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("List");
                ListView lv = (ListView) convertView.findViewById(R.id.lv);

                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                    {
                        TextView ip = (TextView) v.findViewById(R.id.ip);
                        TextView name = (TextView) v.findViewById(R.id.name);
                        Server server = new Server("_" + name + "_" + ip, name.getText().toString(), ip.getText().toString());
                        TryConnection tryConnection = new TryConnection(server);
                        tryConnection.execute("");
                    }
                });
                alertDialog.show();
            }
        }

        @Override
        protected void onPreExecute() {
            lt.setText("Searching for servers");
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class TryConnection extends AsyncTask<String, Void, String>
    {

        LoadToast lt =new LoadToast(MainActivity.this);

        Server server;
        AlertDialog.Builder dialog;

        TryConnection(Server serverp)
        {
            server = serverp;

        }


        @Override
        protected String doInBackground(String... params)
        {
            if (TCPConnector.connectTCP(server))
                return "s";
            return "f";
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("f")) {
                lt.error();
                Toasty.error(MainActivity.this, "Could Not Connect to any server", Toast.LENGTH_SHORT, true).show();
            } else {
                lt.success();
                Toasty.success(MainActivity.this, "connected to " + CurrentServer.serverName + " at " + CurrentServer.serverIP, Toast.LENGTH_SHORT, true).show();
            }

        }

        @Override
        protected void onPreExecute()
        {
            lt.setText("Searching for servers");
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    static String getTCPPort()
    {
        return SP.getString("tcpPort", "13000");
    }

    static String getUdpPort()
    {
        return SP.getString("udpPort", "9050");
    }

}
