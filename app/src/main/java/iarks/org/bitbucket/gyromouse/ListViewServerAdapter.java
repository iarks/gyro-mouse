package iarks.org.bitbucket.gyromouse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


class ListViewServerAdapter extends ArrayAdapter<Server>
{
    private Activity context;

    private ArrayList<Server> servers;

    ListViewServerAdapter(Activity context, ArrayList<Server> servers)
    {
        super(context, R.layout.list_view_layout, servers);
        this.context=context;
        this.servers =servers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        View v=inflater.inflate(R.layout.list_view_layout,null,true);

        TextView txtname=(TextView)v.findViewById(R.id.name);
        TextView txtip=(TextView)v.findViewById(R.id.ip);

        Server notices = servers.get(position);

        txtname.setText(notices.getServerName());
        txtip.setText(notices.getServerIP());

        return v;
    }
}
