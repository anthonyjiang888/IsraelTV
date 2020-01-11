package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.Activity.Records_Play_Activity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDates;
import israel13.androidtv.Utils.NonScrollListView;


/**
 * Created by sudipta on 24/10/16.
 */
public class SearchRecordListAdapter extends ArrayAdapter {


    ArrayList<SetgetSearchRecordDates> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;

    @SuppressWarnings("unchecked")
    public SearchRecordListAdapter(Context mContext, int resourceID, ArrayList<SetgetSearchRecordDates> list) {
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
    public View getView(final int position_list, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceID, null);
            holder = new ViewHolder();


            holder.text_child_listview_record=(TextView)view.findViewById(R.id.text_child_listview_record);
            holder.details_listview_child_record_search= (NonScrollListView) view.findViewById(R.id.details_listview_child_record_search);


            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.text_child_listview_record.setText(list.get(position_list).getDates());

        holder.details_listview_child_record_search.setAdapter(new SearchRecordDetalisListAdapter(mContext,
                R.layout.child_load_recordlist, list.get(position_list).getSetgetSearchRecordDatesDetailsArrayList()));


        holder.details_listview_child_record_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Records_Play_Activity.flag_delete = true;
                AllChannels_Fragment.channel_id=list.get(position_list).getSetgetSearchRecordDatesDetailsArrayList().get(position).getChannel();
                Records_Play_Activity.rdatetime=list.get(position_list).getSetgetSearchRecordDatesDetailsArrayList().get(position).getRdatetime();
                mContext.startActivity(new Intent(mContext,Records_Play_Activity.class));
            }
        });


        return view;

    }



    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView text_child_listview_record;
        public NonScrollListView details_listview_child_record_search;
    }

}