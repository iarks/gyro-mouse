package iarks.org.bitbucket.gyromouse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import xdroid.toaster.Toaster;

public class ServerListActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Server> dbList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView) findViewById(R.id.recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //// TODO: 9/10/2017 add a dialog box to add new servers
            }
        });

        new LoadDBServers().execute("");
    }

    class LoadDBServers extends AsyncTask<String, Void, String>
    {
        LoadToast lt = new LoadToast(ServerListActivity.this);

        @Override
        protected String doInBackground(String... params)
        {
            int count = Globals.databaseHandler.getServerCount();

            Toaster.toast("DATABASE COUNT "+count);

            // if server count is more than 0, then preexisting servers are present - try to connect to those
            if(count>0)
            {
                // get a list of all the servers in the database
                dbList = Globals.databaseHandler.getAllDBServers();
                return "s";
            }
            else
            {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("0"))
            {
                lt.error();
                Toasty.error(ServerListActivity.this, "No previously connected servers", Toast.LENGTH_SHORT, true).show();
            }
            else {
                lt.success();
                // TODO: 9/10/2017 load data from list to list view
                ServerAdapter ca = new ServerAdapter(dbList);
                rv.setAdapter(ca);

                LinearLayoutManager llm = new LinearLayoutManager(ServerListActivity.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llm);

                rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                        new RecyclerItemListener.RecyclerTouchListener() {
                            public void onClickItem(View v, int position)
                            {
                                // TODO: 9/10/2017 connect
                                Toaster.toast("On Click Item interface");
                                System.out.println("On Click Item interface");
                            }

                            public void onLongClickItem(View v, int position)
                            {
                                // TODO: 9/10/2017 delete dialog
                                Toaster.toast("On Click Item interface");
                                System.out.println("On Long Click Item interface");
                            }
                        }));
            }
        }

        @Override
        protected void onPreExecute()
        {
            lt.setText("Looking into database");
            lt.show();
        }

        @Override
        protected void onProgressUpdate(Void... values){}
    }

}
