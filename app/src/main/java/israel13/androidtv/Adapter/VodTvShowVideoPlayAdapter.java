package israel13.androidtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodTvShowVideoPlay;

/**
 * Created by sudipta on 24/10/16.
 */
public class VodTvShowVideoPlayAdapter extends ArrayAdapter {


    ArrayList<SetgetVodTvShowVideoPlay> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;


    @SuppressWarnings("unchecked")
    public VodTvShowVideoPlayAdapter(Context mContext, int resourceID, ArrayList<SetgetVodTvShowVideoPlay> list) {
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


            holder.thumbnail_image_vod_tvshow_videoplay = (ImageView) view.findViewById(R.id.thumbnail_image_vod_tvshow_videoplay);
            holder.totalview_child_vod_tvshow_videoplay = (LinearLayout) view.findViewById(R.id.totalview_child_vod_tvshow_videoplay);
            holder.text_episode_name = (TextView) view.findViewById(R.id.text_episode_name);


            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();



        Glide.with(mContext)
                .load("http:" + list.get(position).getShowpic())
                .override(200,200)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.thumbnail_image_vod_tvshow_videoplay);

        holder.text_episode_name.setText(list.get(position).getName());


       /* holder.totalview_child_vod_tvshow_videoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VodTvShowVideo_Fragment.selecte_epi_id =list.get(position).getTvshow_id();
                VodTvShowVideo_Fragment.selecte_epiyear =list.get(position).getYear();
                VodTvShowVideo_Fragment.selecte_epi_genre =list.get(position).getGenre();
                VodTvShowVideo_Fragment.selecte_epi_vodlist =list.get(position).getVodlist();
                VodTvShowVideo_Fragment.selecte_epi_name=list.get(position).getName();
                VodTvShowVideo_Fragment.selecte_epi_description=list.get(position).getDescription();
                VodTvShowVideo_Fragment.selecte_epi_created=list.get(position).getCreated();
                VodTvShowVideo_Fragment.selecte_epi_updated=list.get(position).getUpdated();
                VodTvShowVideo_Fragment.selecte_epi_views=list.get(position).getViews();
                VodTvShowVideo_Fragment.selecte_epi_length=list.get(position).getLength();
                VodTvShowVideo_Fragment.selecte_epi_stars =list.get(position).getStars();
                VodTvShowVideo_Fragment.selecte_epi_showpic=list.get(position).getShowpic();
                VodTvShowVideo_Fragment.selecte_epi_year=list.get(position).getYear();

                mContext.startActivity(new Intent(mContext, Tv_show_play_activity.class));
            }
        });
*/
        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {

        public ImageView thumbnail_image_vod_tvshow_videoplay;
        public LinearLayout totalview_child_vod_tvshow_videoplay;
        public TextView text_episode_name;

    }

}