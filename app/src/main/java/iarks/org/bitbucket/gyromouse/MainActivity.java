package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
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

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();

    Button buttonRight, buttonEscape, buttonLeft, buttonWindows;
    ImageButton buttonAR, buttonAL, buttonAU, buttonAD, buttonMouse, buttonScroll;

    Thread udpClientUtilThread = null;
    Thread serverCommunicationUtilThread = null;

    final BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(5);

    DatabaseHandler dbHandler;

    ArrayList<Server> discoveredServer = new ArrayList<>();
    List<Server> preServers = new ArrayList<>();
    FabSpeedDial fabSpeedDial;
    CyclicBarrier latch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        new SearchAndConnectUtil().execute("");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(TAG, "onResume");

        if (Globals.advanceChanged == 1)
        {
            Globals.advanceChanged = 0;
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Restart App")
                    .setMessage("Restart app for changes to take effect")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (udpClientUtilThread.isAlive())
                                udpClientUtilThread.interrupt();
                            if (serverCommunicationUtilThread.isAlive())
                                serverCommunicationUtilThread.interrupt();

                            Log.e(getClass().getName(), "Threads Killed");

                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(i);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
        Log.e(getClass().getName(), "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.e(getClass().getName(), "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    // add click listeners to toolbar elements
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
        } else if (id == R.id.action_keyboard)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            return true;
        } else if (id == R.id.action_settings)
        {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // click listener for keyboard events
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if ((event.getAction() == 1 || event.getAction() == 2) && event.getKeyCode() != KEYCODE_BACK)
        {
            SoftKeyboardEventHandler keyboardEvents = new SoftKeyboardEventHandler(event, sharedQueue);
            Thread th = new Thread(keyboardEvents);
            th.start();
        }
        return super.dispatchKeyEvent(event);
    }

    //try retrieving, scanning, connecting
    private class SearchAndConnectUtil extends AsyncTask<String, Void, String>
    {
        boolean connected = false;
        LoadToast lt = new LoadToast(MainActivity.this);

        @Override
        protected String doInBackground(String... params)
        {

            int count = dbHandler.getServerCount();

            if (count > 0)
            {
                preServers = dbHandler.getAllDBServers();

                for (Server server : preServers)
                {

                    if (ClientConnection.connectClient(server, MainActivity.this))
                    {
                        connected = true;
                        return "s";
                    }
                }
            }

            if (!connected || count == 0)
            {
                discoveredServer = NetworkScannerUtil.searchServer(MainActivity.this);

                if (discoveredServer.size() == 1)
                {
                    //auto connect if only one server is available
                    for (Server servers : discoveredServer)
                    {
                        boolean check = dbHandler.checkAvailable(servers.getServerID());
                        if (!check)
                        {
                            // add them to database
                            dbHandler.addServerToDB(servers);
                        }
                    }
                    if (ClientConnection.connectClient(discoveredServer.remove(0), MainActivity.this))
                    {
                        connected = true;
                        return "s";
                    } else
                        return "f";
                } else if (discoveredServer.size() > 1)
                {
                    // show if more than 1 servers are available
                    for (Server servers : discoveredServer)
                    {
                        boolean check = dbHandler.checkAvailable(servers.getServerID());
                        if (!check)
                        {
                            dbHandler.addServerToDB(servers);
                        }
                    }
                    return "dialog";
                } else
                {
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
                    ListViewServerAdapter adapter = new ListViewServerAdapter(MainActivity.this, discoveredServer);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.dialog_list, null);
                    alertDialog.setView(convertView);
                    alertDialog.setTitle("Serveral servers found over network");
                    ListView lv = (ListView) convertView.findViewById(R.id.lv);

                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
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
                    Toasty.success(MainActivity.this, "connected to " + Session.getSessionInstance().getServerName() + " at " + Session.getSessionInstance().getServerIP(), Toast.LENGTH_SHORT, true).show();
            }
        }

        @Override
        protected void onPreExecute()
        {
            lt.setText("Searching for servers");
            lt.setTranslationY(150);
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            // not used
        }
    }

    //scan for servers
    private class ScanNetwork extends AsyncTask<String, Void, String>
    {
        LoadToast lt = new LoadToast(MainActivity.this);

        @Override
        protected String doInBackground(String... params)
        {

            discoveredServer = NetworkScannerUtil.searchServer(MainActivity.this);

            if (discoveredServer.size() > 0)
            {
                for (Server servers : discoveredServer)
                {
                    boolean check = dbHandler.checkAvailable(servers.getServerID());
                    if (!check)
                    {
                        // add newly found servers to database
                        dbHandler.addServerToDB(servers);
                    }
                }
                return "1";
            } else
            {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("0"))
            {
                lt.error();
                Toasty.error(MainActivity.this, "Could Not find any servers on local network", Toast.LENGTH_SHORT, true).show();
            } else
            {
                lt.success();

                ListViewServerAdapter adapter = new ListViewServerAdapter(MainActivity.this, discoveredServer);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_list, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("List");
                ListView lv = (ListView) convertView.findViewById(R.id.lv);

                lv.setAdapter(adapter);

                lv.setOnItemClickListener(
                        new AdapterView.OnItemClickListener()
                        {
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
        protected void onPreExecute()
        {
            lt.setText("Searching for servers");
            lt.setTranslationY(150);
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            // not used
        }
    }

    //try connecting to a given server
    private class TryConnection extends AsyncTask<String, Void, String>
    {

        LoadToast lt = new LoadToast(MainActivity.this);

        Server server;
        AlertDialog.Builder dialog;

        TryConnection(Server serverp)
        {
            server = serverp;

        }


        @Override
        protected String doInBackground(String... params)
        {
            if (ClientConnection.connectClient(server, MainActivity.this))
                return "s";
            return "f";
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("f"))
            {
                lt.error();
                Toasty.error(MainActivity.this, "Could Not Connect to any server", Toast.LENGTH_SHORT, true).show();
            } else
            {
                lt.success();
                Toasty.success(MainActivity.this, "connected to " + Session.getSessionInstance().getServerName() + " at " + Session.getSessionInstance().getServerIP(), Toast.LENGTH_SHORT, true).show();
            }

        }

        @Override
        protected void onPreExecute()
        {
            lt.setText("Searching for servers");
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }

    void init()
    {
        Log.e(getClass().getName(), "init");
        // initialise variables
        latch = new CyclicBarrier(2);
        Globals.cdLatch = latch;

        //initiate database handler
        dbHandler = new DatabaseHandler(this);
        Globals.databaseHandler = dbHandler;


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

        // create objects of other classes
        final UDPClientUtil udpClientUtil = new UDPClientUtil(sharedQueue, this);
        Globals.udpClientUtil = udpClientUtil;

        final Trackpad trackpad = new Trackpad(sharedQueue, getApplicationContext());
        final ScrollWheel scrollWheel = new ScrollWheel(sharedQueue, getApplicationContext());

        // create the udp client thread
        Log.e(TAG, "udpclientutil thread starting");
        udpClientUtilThread = new Thread(udpClientUtil);
        udpClientUtilThread.start();
        Log.e(TAG, "udpclientutil thread started");

        // also start the server communication thread
        Log.e(getClass().getName(), "Server Communication util thread starting");
        ServerCommunicationUtil serverCommunicationUtil = new ServerCommunicationUtil();
        serverCommunicationUtilThread = new Thread(serverCommunicationUtil);
        serverCommunicationUtilThread.start();
        Log.e(getClass().getName(), "Server Communication util thread started");

        // add click listeners to buttons
        buttonAD.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AD;1;" + Session.getSessionInstance().getSessionKey());
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AD;0;" + Session.getSessionInstance().getSessionKey());
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AR;1;" + Session.getSessionInstance().getSessionKey());
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AR;0;" + Session.getSessionInstance().getSessionKey());
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AL;1;" + Session.getSessionInstance().getSessionKey());
                            sharedQueue.notifyAll();
                        }

                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AL;0;" + Session.getSessionInstance().getSessionKey());
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AU;1;" + Session.getSessionInstance().getSessionKey());
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("AU;0;" + Session.getSessionInstance().getSessionKey());
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
            long timeDown=0, timeUp=0;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Log.i(TAG,"action down");
                    timeDown = event.getDownTime();
                    Thread trackpad_thread = new Thread(trackpad);
                    trackpad_thread.start();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    Log.i(TAG,"action up");
                    timeUp = event.getEventTime();
                    udpClientUtil.clearThread();
                    trackpad.stopThread();
                    Log.i(getClass().getName(), "cleared");
                    if (timeUp - timeDown < 500)
                    {
                        try
                        {
                            synchronized (sharedQueue)
                            {
                                sharedQueue.put("LD;x;" + Session.getSessionInstance().getSessionKey());
                                sharedQueue.put("LU;x;" + Session.getSessionInstance().getSessionKey());
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Thread scrollWheel_thread = new Thread(scrollWheel);
                    scrollWheel_thread.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    udpClientUtil.clearThread();
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("RD;x;" + Session.getSessionInstance().getSessionKey());
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("RU;x;" + Session.getSessionInstance().getSessionKey());
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
            long downTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    downTime = event.getDownTime();
                    try
                    {
                        synchronized (sharedQueue)
                        {
                            sharedQueue.put("LD;x;" + Session.getSessionInstance().getSessionKey());
                            sharedQueue.notifyAll();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (event.getEventTime() - downTime < 1000)
                    {
                        try
                        {
                            synchronized (sharedQueue)
                            {
                                sharedQueue.put("LU;x;" + Session.getSessionInstance().getSessionKey());
                                sharedQueue.notifyAll();
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        Toast.makeText(MainActivity.this, "Left Mouse Button Pressed", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        buttonEscape.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    synchronized (sharedQueue)
                    {
                        sharedQueue.put("ESC;x;" + Session.getSessionInstance().getSessionKey());
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
                    synchronized (sharedQueue)
                    {
                        sharedQueue.put("WIN;x;" + Session.getSessionInstance().getSessionKey());
                        sharedQueue.notifyAll();
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.ff);

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter()
        {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem)
            {
                int id = menuItem.getItemId();

                if (id == R.id.action_scanNetwork)
                {
                    new ScanNetwork().execute("");
                    return true;
                } else if (id == R.id.action_dbServers)
                {
                    Intent myIntent = new Intent(MainActivity.this, ServerListActivity.class);
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                return false;
            }
        });
    }
}
