package israel13.androidtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetEpisodes;
import israel13.androidtv.Utils.Constant;

/**
 * Created by sudipta on 24/10/16.
 */
public class VodWhatsNewTvShowVideoPlayAdapter extends ArrayAdapter {


    ArrayList<SetgetEpisodes> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;

    @SuppressWarnings("unchecked")
    public VodWhatsNewTvShowVideoPlayAdapter(Context mContext, int resourceID, ArrayList<SetgetEpisodes> list) {
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

            holder.title_child_whatsnew_tvshow_videoplay = (TextView) view.findViewById(R.id.title_child_whatsnew_tvshow_videoplay);
            holder.description_child_whatsnew_tvshow_videoplay = (TextView) view.findViewById(R.id.description_child_whatsnew_tvshow_videoplay);
            holder.duration_whatsnew_tvshow_videoplay = (TextView) view.findViewById(R.id.duration_whatsnew_tvshow_videoplay);
            holder.thumbnail_image_whatsnew_tvshow_videoplay = (ImageView) view.findViewById(R.id.thumbnail_image_whatsnew_tvshow_videoplay);
            holder.ratingBar_whatsnew_tvshow_videoplay = (RatingBar) view.findViewById(R.id.ratingBar_whatsnew_tvshow_videoplay);
            holder.text_play_whatsnew_tvshow_videoplay = (TextView) view.findViewById(R.id.text_play_whatsnew_tvshow_videoplay);
            holder.play_button_whatsnew_tvshow_videoplay = (ImageView) view.findViewById(R.id.play_button_whatsnew_tvshow_videoplay);


            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();


        holder.text_play_whatsnew_tvshow_videoplay.setTag(list.get(position).getIsplaying());
        holder.play_button_whatsnew_tvshow_videoplay.setTag(list.get(position).getIsplaying());
        holder.title_child_whatsnew_tvshow_videoplay.setText(list.get(position).getEpisode_name());
        holder.description_child_whatsnew_tvshow_videoplay.setText(list.get(position).getSeason_description());


        if (list.get(position).getEpisode_length().equalsIgnoreCase(""))
        {
            holder.duration_whatsnew_tvshow_videoplay.setText("");
        }
        else
        {
            holder.duration_whatsnew_tvshow_videoplay.setText(Constant.hour_min_format(Integer.parseInt(list.get(position).getEpisode_length())));
        }


        if (holder.text_play_whatsnew_tvshow_videoplay.getTag().toString().equalsIgnoreCase("true"))
        {
            holder.text_play_whatsnew_tvshow_videoplay.setVisibility(View.VISIBLE);
            holder.text_play_whatsnew_tvshow_videoplay.setText("מתנגן כעת");
            holder.play_button_whatsnew_tvshow_videoplay.setVisibility(View.GONE);
        }
        else
        {
            holder.text_play_whatsnew_tvshow_videoplay.setVisibility(View.GONE);
            holder.play_button_whatsnew_tvshow_videoplay.setVisibility(View.VISIBLE);
        }

        holder.ratingBar_whatsnew_tvshow_videoplay.setRating(Float.parseFloat(list.get(position).getEpisode_stars()));
        Glide.with(mContext)
                .load("http:" + list.get(position).getEpisodes_pic())
                .override(200,200)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.thumbnail_image_whatsnew_tvshow_videoplay);

        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView title_child_whatsnew_tvshow_videoplay,description_child_whatsnew_tvshow_videoplay,duration_whatsnew_tvshow_videoplay;
        public TextView text_play_whatsnew_tvshow_videoplay;
        public ImageView thumbnail_image_whatsnew_tvshow_videoplay,play_button_whatsnew_tvshow_videoplay;
        public RatingBar ratingBar_whatsnew_tvshow_videoplay;
    }

}