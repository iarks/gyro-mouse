package iarks.org.bitbucket.gyromouse;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class RecyclerViewServerAdapter extends RecyclerView.Adapter<RecyclerViewServerAdapter.MyViewHolder>
{

    private List<Server> serverList;

    /**
     * View holder class
     * */
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView serverName;
        public TextView serverIP;

        MyViewHolder(View view)
        {
            super(view);
            serverName = (TextView) view.findViewById(R.id.name);
            serverIP = (TextView) view.findViewById(R.id.ip);
        }
    }

    public RecyclerViewServerAdapter(List<Server> serverList)
    {
        this.serverList = serverList;
    }
    RecyclerViewServerAdapter()
    {

    }
    void setUpServerAdapter(List<Server> serverList)
    {
        this.serverList = serverList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Server c = serverList.get(position);
        holder.serverName.setText(c.getServerName());
        holder.serverIP.setText(c.getServerIP());
    }

    @Override
    public int getItemCount()
    {
        return serverList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_layout,parent, false);
        return new MyViewHolder(v);
    }
}