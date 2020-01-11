package israel13.androidtv.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetAllChannels;
import israel13.androidtv.Utils.CustomRecyclerView;

/**
 * Created by sudipta on 24/10/16.
 */
public class AllChannelsAdapter extends ArrayAdapter {


    ArrayList<SetgetAllChannels> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;


    @SuppressWarnings("unchecked")
    public AllChannelsAdapter(Context mContext, int resourceID, ArrayList<SetgetAllChannels> list) {
        super(mContext, resourceID, list);
        this.mContext = mContext;
        this.resourceID = resourceID;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceID, null);
            holder = new ViewHolder();

            holder.name_child_activity_allchannels = (TextView) view.findViewById(R.id.name_child_activity_allchannels);
            holder.main = (LinearLayout) view.findViewById(R.id.main);
            holder.recycler_view_child_activity_allchannels = (CustomRecyclerView) view.findViewById(R.id.recycler_view_child_activity_allchannels);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            layoutManager.setReverseLayout(true);
            holder.recycler_view_child_activity_allchannels.setLayoutManager(layoutManager);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();


        holder.name_child_activity_allchannels.setText(list.get(position).getGname());

        adapter = new AllchannelRecyclerviewAdapter(list.get(position).getSubchannelsList(), mContext);
        holder.recycler_view_child_activity_allchannels.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        holder.recycler_view_child_activity_allchannels.setItemViewCacheSize(20);
        holder.recycler_view_child_activity_allchannels.setDrawingCacheEnabled(true);

        if (position==0)
        {
            holder.main.requestFocus();
        }


       /* holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recycler_view_child_activity_allchannels.setFocusable(true);
            }
        });*/
        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView name_child_activity_allchannels;
        public CustomRecyclerView recycler_view_child_activity_allchannels;
        public LinearLayout main;
    }

}