package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static iarks.org.bitbucket.gyromouse.R.id.serverIP;
import static iarks.org.bitbucket.gyromouse.R.id.textView;

/**
 * Created by Arkadeep on 9/11/2017.
 */

public class ArrayAdapterItem extends ArrayAdapter<Server>
{
    private final Context context;
    List<Server> list;

    public ArrayAdapterItem(Context context, int layoutResourceId, List<Server> als)
    {
        super(context, layoutResourceId, als);
        this.context = context;
        this.list = als;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_view_layout, parent, false);
        TextView textViewip = (TextView) rowView.findViewById(R.id.ip);
        TextView textViewname = (TextView) rowView.findViewById(R.id.name);

        Server server = list.remove(position);
        textViewip.setText(server.getServerIP());
        textViewname.setText(server.getServerIP());

        return rowView;
    }
}