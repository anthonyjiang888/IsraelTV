package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LiveChannels_Play_Activity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.Search_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class SearchLiveRecyclerviewAdapter extends RecyclerView.Adapter<SearchLiveRecyclerviewAdapter.Myviewholder>
{
    private ArrayList<SetgetSubchannels> list;
    private Context context;
    private Search_Fragment search_fragment;

    public class Myviewholder extends RecyclerView.ViewHolder
    {
        public RoundedImageView image_child_new_search_live;
        public LinearLayout linear_layout_new_search_live_tv;
        public TextView epg_title_child_new_search_live,time_child_new_search_live;
        public RelativeLayout total_layout_child_live_search;
        public RelativeLayout relative_lay;

        public Myviewholder(View itemView) {
            super(itemView);

            image_child_new_search_live = (RoundedImageView) itemView.findViewById(R.id.image_child_new_search_live);
            linear_layout_new_search_live_tv = (LinearLayout) itemView.findViewById(R.id.linear_layout_new_search_live_tv);
            total_layout_child_live_search = (RelativeLayout) itemView.findViewById(R.id.total_layout_child_live_search);
            epg_title_child_new_search_live = (TextView) itemView.findViewById(R.id.epg_title_child_new_search_live);
            time_child_new_search_live = (TextView) itemView.findViewById(R.id.time_child_new_search_live);
            relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);
            total_layout_child_live_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                    {
                        v.animate().scaleX(1).scaleY(1).setDuration(200);
                        epg_title_child_new_search_live.setSelected(false);
                    }
                    else {
                        epg_title_child_new_search_live.setSelected(true);
                        v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(200);
                    }
                }
            });
        }
    }

    public SearchLiveRecyclerviewAdapter(ArrayList<SetgetSubchannels> list, Context context,Search_Fragment search_fragment) {
        this.list = list;
        this.context=context;
        this.search_fragment=search_fragment;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_new_search_live_tv,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {
        String time = "";
        ImageCacheUtil.with(context)
                .load(Constant.BASE_URL_IMAGE + list.get(position).getImage())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_child_new_search_live);
        if (list.get(position).getIsradio().equalsIgnoreCase("0")){
            if (list.get(position).getEpg_name().equalsIgnoreCase(""))
            {
                holder.linear_layout_new_search_live_tv.setVisibility(View.GONE);
            }
            else
            {
                holder.linear_layout_new_search_live_tv.setVisibility(View.VISIBLE);
                //
                try {
                    time = Constant.getTimeByAdding((Constant.parseInt(list.get(position).getDuration()))
                            , list.get(position).getTime());
                } catch (NumberFormatException e) {
                    System.out.println("--------position" + list.get(position).getDuration());
                }
                SpannableString text = (search_fragment.SettextColorNew(search_fragment.edit_text_search.getText().toString(),list.get(position).getEpg_name()));
                holder.epg_title_child_new_search_live.setText(text);
//                holder.epg_title_child_new_search_live.setText(list.get(position).getEpg_name());
                holder.time_child_new_search_live.setText(list.get(position).getTime() + " - " + time);
            }
        } else
            holder.linear_layout_new_search_live_tv.setVisibility(View.GONE);

        int selectedPos = ((HomeActivity)context).getSelectedPosition(HomeActivity.AllchannelRecyclerview);

        if (search_fragment.SelectedNumber == 0 && position == 0) {
            Log.e("test", "selected position: " + selectedPos);
            holder.total_layout_child_live_search.requestFocus();
        }
        holder.total_layout_child_live_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getIsradio().equalsIgnoreCase("0")) {
                    Intent in = new Intent(context, LiveChannels_Play_Activity.class);
                    Search_Fragment.flag_page_change=1;
                    in.putExtra(LiveChannels_Play_Activity.CHANNEL_ID, list.get(position).getSub_channelsid());
                    in.putExtra(LiveChannels_Play_Activity.MAIN_CH_ID, list.get(position).getChid());
                    in.putExtra(LiveChannels_Play_Activity.CH_ICON, list.get(position).getImage());
                    context.startActivity(in);
                } else {
                    Intent in = new Intent(context, LiveChannels_Play_Activity.class);
                    Search_Fragment.flag_page_change=1;
                    in.putExtra(LiveChannels_Play_Activity.CHANNEL_ID, list.get(position).getSub_channelsid());
                    in.putExtra(LiveChannels_Play_Activity.MAIN_CH_ID, list.get(position).getChid());
                    in.putExtra(LiveChannels_Play_Activity.CH_ICON, list.get(position).getImage());
                    context.startActivity(in);
                }
            }


        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}