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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetLoadScheduleRecord;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by sudipta on 24/10/16.
 */
public class LoadRecordListAdapter extends ArrayAdapter {


    ArrayList<SetgetLoadScheduleRecord> list;
    Context mContext;
    int resourceID;

    @SuppressWarnings("unchecked")
    public LoadRecordListAdapter(Context mContext, int resourceID, ArrayList<SetgetLoadScheduleRecord> list) {
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

       // holder.text_play_child_load_recordlist.setTag(list.get(position).getIsplaying());
      //  holder.play_button_child_load_recordlist.setTag(list.get(position).getIsplaying());
        if (!list.get(position).getYear().isEmpty()) {
            holder.tv_schedule_epg_title_child_load_recordlist.setText(list.get(position).getName().trim()
                    + " (" + list.get(position).getYear() + ")");
        }else{
            holder.tv_schedule_epg_title_child_load_recordlist.setText(list.get(position).getName().trim());
        }
      //  holder.tv_schedule_epg_time_child_load_recordlist.setText(list.get(position).getTime());
        holder.tv_schedule_epg_duration_child_load_recordlist.setText("זמן"+": "+Constant.hour_min_format(Constant.parseInt(list.get(position).getLengthtime())));
        holder.tv_schedule_epg_description_child_load_recordlist.setText(list.get(position).getDescription());

        holder.ratingBar_child_load_recordlist.setRating(Constant.parseFloat(list.get(position).getStar()));

        String time = Constant.getTimeByAdding((Constant.parseInt(list.get(position).getLengthtime())),list.get(position).getTime());

        holder.tv_schedule_epg_time_child_load_recordlist.setText(list.get(position).getTime() + " - " + time);

        if(list.get(position).getGenre().equalsIgnoreCase(""))
        {
            holder.tv_schedule_epg_gener_child_load_recordlist.setText("ז'אנר" +": "+"מידע אינו זמין");
        }
        else holder.tv_schedule_epg_gener_child_load_recordlist.setText("ז'אנר" +": "+list.get(position).getGenre());

        ImageCacheUtil.with(mContext).
                load("http:" + list.get(position).getShowpic())
                .resize(200, 200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.thumbnail_image_child_load_recordlist);
        view.setPadding(40, 10, 40, 10);
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