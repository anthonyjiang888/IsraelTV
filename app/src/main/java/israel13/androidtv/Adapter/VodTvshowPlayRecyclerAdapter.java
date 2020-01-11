package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.Tv_show_play_activity;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodTvShowVideoPlay;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 7/7/17.
 */

public class VodTvshowPlayRecyclerAdapter extends BaseAdapter {

    ArrayList<SetgetVodTvShowVideoPlay> list;
    Context mContext;

    HomeActivity home = null;

    public class ViewHolder {

        public ImageView thumbnail_image_vod_tvshow_videoplay;
        public LinearLayout totalview_child_vod_tvshow_videoplay;
        public TextView text_episode_name;

        public ViewHolder(View itemView) {
            thumbnail_image_vod_tvshow_videoplay = (ImageView) itemView.findViewById(R.id.thumbnail_image_vod_tvshow_videoplay);
            text_episode_name = (TextView) itemView.findViewById(R.id.text_episode_name);
            totalview_child_vod_tvshow_videoplay = (LinearLayout) itemView.findViewById(R.id.totalview_child_vod_tvshow_videoplay);
            totalview_child_vod_tvshow_videoplay.setFocusable(false);
            totalview_child_vod_tvshow_videoplay.setClickable(false);
        }
    }

    public VodTvshowPlayRecyclerAdapter(ArrayList<SetgetVodTvShowVideoPlay> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            if ( list.get(position).getName().equals("EmptyEmpty"))
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vod_tvshow_empty, parent, false);
            else
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.child_activity_vod_tvshow_videoplay,parent,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if ( !list.get(position).getName().equals("EmptyEmpty")) {
            holder.totalview_child_vod_tvshow_videoplay.setVisibility(View.VISIBLE);
            ImageCacheUtil.with(mContext)
                    .load("http:" + list.get(position).getShowpic())
                    .resize(200, 200)
                    .cacheUsage(false, true)
                    .placeholder(R.drawable.channel_placeholder)
                    .into(holder.thumbnail_image_vod_tvshow_videoplay);
            holder.text_episode_name.setText(list.get(position).getName());

        } else {
            holder.totalview_child_vod_tvshow_videoplay.setVisibility(View.GONE);
            holder.totalview_child_vod_tvshow_videoplay.setFocusable(false);
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
