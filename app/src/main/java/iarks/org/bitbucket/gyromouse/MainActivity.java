package iarks.org.bitbucket.gyromouse;

import android.os.Bundle;
import android.util.Log;
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
import android.widget.Button;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Button buttonMouse,buttonScroll,buttonRight;
    BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final UDPClient udpClient = new UDPClient(49443,"192.168.1.39",blockingQueue);
        final Thread udp_thread = new Thread(udpClient);
        udp_thread.start();

        buttonMouse = (Button)findViewById(R.id.buttonMouse);
        buttonScroll = (Button)findViewById(R.id.buttonScroll);
        buttonRight = (Button)findViewById(R.id.buttonRight);

        final Trackpad mouse = new Trackpad(blockingQueue,getApplicationContext());

        final ScrollWheel scrollWheel = new ScrollWheel(blockingQueue,getApplicationContext());

        // Start UDP thread
//        udp_thread.start();

        // Add listeners for buttons
        buttonMouse.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {

                    Thread th = new Thread(mouse);
                    th.start();
//                    mThread = new HandlerThread("Gyro");
//                    mHandlerThread.start();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    udpClient.clearThread();
                    mouse.cleanThread();
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
                    Thread th = new Thread(scrollWheel);
                    th.start();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    udpClient.clearThread();
                    scrollWheel.cleanThread();
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
                        blockingQueue.put("{\"X\":" + "\"" + "RD" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
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
                        blockingQueue.put("{\"X\":" + "\"" + "RU" + "\"," + "\"Y\":\"" + 0.00 + "\"}" + "\0");
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
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
}
