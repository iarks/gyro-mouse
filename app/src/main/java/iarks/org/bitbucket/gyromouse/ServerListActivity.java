package iarks.org.bitbucket.gyromouse;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.List;

import es.dmoral.toasty.Toasty;
import xdroid.toaster.Toaster;

public class ServerListActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Server> dbList;
    TextView currentServerName;
    TextView currentServerIP;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView) findViewById(R.id.recycler_view);
        currentServerName = (TextView)findViewById(R.id.serverName);
        currentServerIP= (TextView)findViewById(R.id.serverIP);

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
            else
            {
                lt.success();
                ServerAdapter ca = new ServerAdapter(dbList);
                rv.setAdapter(ca);

                LinearLayoutManager llm = new LinearLayoutManager(ServerListActivity.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llm);

                rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                        new RecyclerItemListener.RecyclerTouchListener()
                        {
                            public void onClickItem(View v, int position)
                            {
                                TextView name = (TextView)v.findViewById(R.id.name);
                                TextView ip = (TextView)v.findViewById(R.id.ip);
                                String key = "_"+name.getText()+"_"+ip.getText();
                                Log.i(getClass().getName(),key);
                                Log.i(getClass().getName(),name.getText().toString());
                                Log.i(getClass().getName(),ip.getText().toString());
                                Server server = new Server(key,name.getText().toString(),ip.getText().toString());

                                TCPConnector tcpConnector = new TCPConnector(server,ServerListActivity.this);
                                tcpConnector.execute("");
                            }

                            public void onLongClickItem(View v, int position)
                            {
                                final View vv=v;
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(ServerListActivity.this);
                                builder.setTitle("Delete entry")
                                        .setMessage("Are you sure you want to delete this entry?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                TextView name = (TextView)vv.findViewById(R.id.name);
                                                TextView ip = (TextView)vv.findViewById(R.id.ip);
                                                String del = "_"+name.getText()+"_"+ip.getText();
                                                Globals.databaseHandler.deleteServer(del);

                                                Toaster.toast("On Click Item interface");
                                                System.out.println("On Click Item interface");
                                            }
                                        })
                                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }));
            }

            currentServerName.setText(CurrentServer.serverName);
            currentServerIP.setText(CurrentServer.serverIP);
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
