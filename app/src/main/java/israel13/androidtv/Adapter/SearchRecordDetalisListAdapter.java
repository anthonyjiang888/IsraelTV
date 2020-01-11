package israel13.androidtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDatesDetails;
import israel13.androidtv.Utils.Constant;

/**
 * Created by sudipta on 24/10/16.
 */
public class SearchRecordDetalisListAdapter extends ArrayAdapter {


    ArrayList<SetgetSearchRecordDatesDetails> list;
    Context mContext;
    int resourceID;

    @SuppressWarnings("unchecked")
    public SearchRecordDetalisListAdapter(Context mContext, int resourceID, ArrayList<SetgetSearchRecordDatesDetails> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceID, null);
            holder = new ViewHolder();

            holder.tv_schedule_epg_time_child_load_recordlist=(TextView)view.findViewById(R.id.tv_schedule_epg_time_child_load_recordlist);
            holder.tv_schedule_epg_title_child_load_recordlist=(TextView)view.findViewById(R.id.tv_schedule_epg_title_child_load_recordlist);
            holder.tv_schedule_epg_duration_child_load_recordlist=(TextView)view.findViewById(R.id.tv_schedule_epg_duration_child_load_recordlist);
            holder.tv_schedule_epg_description_child_load_recordlist=(TextView)view.findViewById(R.id.tv_schedule_epg_description_child_load_recordlist);
            holder.thumbnail_image_child_load_recordlist= (ImageView) view.findViewById(R.id.thumbnail_image_child_load_recordlist);
            holder.ratingBar_child_load_recordlist = (RatingBar) view.findViewById(R.id.ratingBar_child_load_recordlist);
            holder.totalview_child_load_recordlist = (LinearLayout) view.findViewById(R.id.totalview_child_load_recordlist);
            holder.text_play_child_load_recordlist = (TextView) view.findViewById(R.id.text_play_child_load_recordlist);
            holder.tv_schedule_epg_gener_child_load_recordlist = (TextView) view.findViewById(R.id.tv_schedule_epg_gener_child_load_recordlist);

            holder.play_button_child_load_recordlist = (ImageView) view.findViewById(R.id.play_button_child_load_recordlist);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();


      /*  holder.tv_schedule_epg_title_child_load_recordlist.setText(list.get(position).getName());
        holder.tv_schedule_epg_time_child_load_recordlist.setText(list.get(position).getTime());
        holder.tv_schedule_epg_duration_child_load_recordlist.setText(Constant.hour_min_format(Integer.parseInt(list.get(position).getLengthtime())));
        holder.tv_schedule_epg_description_child_load_recordlist.setText(list.get(position).getDescription());

        holder.ratingBar_child_load_recordlist.setRating(Float.parseFloat(list.get(position).getStar()));*/

        holder.tv_schedule_epg_title_child_load_recordlist.setText(list.get(position).getName());
        //  holder.tv_schedule_epg_time_child_load_recordlist.setText(list.get(position).getTime());
        holder.tv_schedule_epg_duration_child_load_recordlist.setText("זמן"+": "+Constant.hour_min_format(Integer.parseInt(list.get(position).getLengthtime())));
        holder.tv_schedule_epg_description_child_load_recordlist.setText(list.get(position).getDescription());

        holder.ratingBar_child_load_recordlist.setRating(Float.parseFloat(list.get(position).getStar()));

        String time = Constant.getTimeByAdding((Integer.parseInt(list.get(position).getLengthtime()))
                ,list.get(position).getTime());

        holder.tv_schedule_epg_time_child_load_recordlist.setText(list.get(position).getTime() + " - " + time);

        holder.tv_schedule_epg_gener_child_load_recordlist.setText("ז'אנר" +": "+list.get(position).getGenre());

        /*if (holder.text_play_child_load_recordlist.getTag().toString().equalsIgnoreCase("true"))
        {
            holder.text_play_child_load_recordlist.setVisibility(View.VISIBLE);
            holder.text_play_child_load_recordlist.setText("מתנגן כעת");
            holder.play_button_child_load_recordlist.setVisibility(View.GONE);
        }
        else
        {
            holder.text_play_child_load_recordlist.setVisibility(View.GONE);
            holder.play_button_child_load_recordlist.setVisibility(View.VISIBLE);
        }*/



        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView tv_schedule_epg_time_child_load_recordlist,tv_schedule_epg_title_child_load_recordlist,text_play_child_load_recordlist;
        public TextView tv_schedule_epg_duration_child_load_recordlist,tv_schedule_epg_description_child_load_recordlist,tv_schedule_epg_gener_child_load_recordlist;
        public ImageView thumbnail_image_child_load_recordlist,play_button_child_load_recordlist;
        public RatingBar ratingBar_child_load_recordlist;
        public LinearLayout totalview_child_load_recordlist;
    }

}