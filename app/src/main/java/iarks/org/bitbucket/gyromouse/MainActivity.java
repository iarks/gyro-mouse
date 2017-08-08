package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Button buttonMouse,buttonScroll,buttonRight,buttonKeyboard;
    BlockingQueue<String> sharedQueue = new LinkedBlockingDeque<>(2);
//    EditText edit;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final UDPClient udpClient = new UDPClient(49443,"192.168.1.39", sharedQueue);
        final Thread udp_thread = new Thread(udpClient);
        udp_thread.start();

        buttonMouse = (Button)findViewById(R.id.buttonMouse);
        buttonScroll = (Button)findViewById(R.id.buttonScroll);
        buttonRight = (Button)findViewById(R.id.buttonRight);
        buttonKeyboard = (Button)findViewById(R.id.buttonKeyboard);
//        edit=(EditText)findViewById(R.id.edit);
//        keyboard_visibility.setSelected(false);


        final Trackpad trackpad = new Trackpad(sharedQueue,getApplicationContext());
        final ScrollWheel scrollWheel = new ScrollWheel(sharedQueue,getApplicationContext());

        buttonMouse.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Thread trackpad_thread = new Thread(trackpad);
                    trackpad_thread.start();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    udpClient.clearThread();
                    trackpad.stopThread();
                    Log.e("MainActivity","cleared");
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

        buttonKeyboard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if ((event.getAction() == 1 || event.getAction() == 2) && event.getKeyCode()!=KEYCODE_BACK)
        {
            KeyboardEvents keyboardEvents = new KeyboardEvents(event, sharedQueue, getApplicationContext());
            Thread th = new Thread(keyboardEvents);
            th.start();
        }
        return super.dispatchKeyEvent(event);
    }
}
