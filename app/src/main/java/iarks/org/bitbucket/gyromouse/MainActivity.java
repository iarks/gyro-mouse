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

    Button buttonRight, buttonEscape, buttonLeft, buttonWindows;
    ImageButton buttonAR, buttonAL, buttonAU, buttonAD, buttonMouse, buttonScroll;
    BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(5);
    DatabaseHandler dbHandler;

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
        ServerHandler serverHandler = new ServerHandler(dbHandler,udpClient);
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

}
