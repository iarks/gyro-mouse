package iarks.org.bitbucket.gyromouse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


class AdapterServers extends ArrayAdapter<Server>
{
    private Activity context;

    ArrayList<Server> products;

    AdapterServers(Activity context, ArrayList<Server> objects)
    {
        super(context, R.layout.list_view_layout, objects);
        this.context=context;
        this.products=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        View v=inflater.inflate(R.layout.list_view_layout,null,true);

        TextView txtname=(TextView)v.findViewById(R.id.name);
        TextView txtip=(TextView)v.findViewById(R.id.ip);

        Server notices = new Server();

        notices = products.get(position);

        //we initialise the layout items in the XML files by using the variables in class Products through the object of the class, namely p
        txtname.setText(notices.getServerName());
        txtip.setText(notices.getServerIP());

        //we return this completed view
        return v;
    }
}
