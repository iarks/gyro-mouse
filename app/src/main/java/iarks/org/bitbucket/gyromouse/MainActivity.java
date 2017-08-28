package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();

    Button buttonRight,buttonEscape,buttonLeft,buttonWindows;
    ImageButton buttonAR,buttonAL,buttonAU,buttonAD,buttonMouse,buttonScroll;
    BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(5);

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
                        sharedQueue.put("{\"X\":" + "\"" + "AD" + "\"," + "\"Y\":\"" + 1 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AD" + "\"," + "\"Y\":\"" + 0 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AR" + "\"," + "\"Y\":\"" + 1 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AR" + "\"," + "\"Y\":\"" + 0 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AL" + "\"," + "\"Y\":\"" + 1 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AL" + "\"," + "\"Y\":\"" + 0 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AU" + "\"," + "\"Y\":\"" + 1 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "AU" + "\"," + "\"Y\":\"" + 0 + "\"}" + "\0");
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
                            sharedQueue.put("{\"X\":" + "\"" + "LD" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
                            sharedQueue.put("{\"X\":" + "\"" + "LU" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "RD" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "RU" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                        sharedQueue.put("{\"X\":" + "\"" + "LD" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                            sharedQueue.put("{\"X\":" + "\"" + "LU" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                    sharedQueue.put("{\"X\":" + "\"" + "ESC" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                    sharedQueue.put("{\"X\":" + "\"" + "WIN" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

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
}