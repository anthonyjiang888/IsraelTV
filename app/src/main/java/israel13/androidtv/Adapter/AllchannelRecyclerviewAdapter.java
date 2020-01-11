package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LiveChannels_Play_Activity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 20/06/16.
 */
public class AllchannelRecyclerviewAdapter extends RecyclerView.Adapter<AllchannelRecyclerviewAdapter.Myviewholder>
{
    public Context context;
    public static ArrayList<SetgetSubchannels> array_subchannels = new ArrayList<>();
    public static int selected_position=0;
    // public static TextView show_time,show_name;
    public String channel_id="";

    private ArrayList<SetgetSubchannels> list;
    private List<Myviewholder> listViewHolder = new ArrayList<Myviewholder>();
    //private ArrayList<SetgetLoadScheduleRecord> LoadScheduleRecordsList;
    //private View.OnFocusChangeListener listener;

    HomeActivity home = null;
    private int recordAdapterId = 0;
    private int mChildCnt = 0;

    public class Myviewholder extends RecyclerView.ViewHolder
    {
        public RoundedImageView image_recyclerview_child;
        public LinearLayout text_channel;
        public RelativeLayout relative_lay;
        public FrameLayout total_layout_child_recyclerview;
        public TextView show_time,show_name;
        public String chId;
        public Myviewholder(View itemView) {
            super(itemView);

            image_recyclerview_child = (RoundedImageView) itemView.findViewById(R.id.image_recyclerview_child);
            total_layout_child_recyclerview = (FrameLayout) itemView.findViewById(R.id.total_layout_child_recyclerview);
            text_channel = (LinearLayout) itemView.findViewById(R.id.text_channel);
            show_time = (TextView) itemView.findViewById(R.id.show_time);
            show_name = (TextView) itemView.findViewById(R.id.show_name);
            relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);


        }
    }

    public AllchannelRecyclerviewAdapter(ArrayList<SetgetSubchannels> list, Context context) {
        this.list = list;
        this.context=context;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(home == null){
            home = (HomeActivity) parent.getContext();
        }
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_activity_allchannels_recyclerview_child,parent,false);
        return new Myviewholder(itemview);
    }

    public void notifyAllData() {

        for (int position = 0; position < list.size(); position++) {
            int listPosition = 0;
            boolean isExist = false;
            for (listPosition = 0; listPosition < listViewHolder.size(); listPosition++) {
                if (listViewHolder.get(listPosition).chId.equals(list.get(position).getSub_channelsid())) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                Myviewholder holder = listViewHolder.get(listPosition);
                String time = "";
                try {
                    time = Constant.getTimeByAdding((Constant.parseInt(list.get(position).getEpg_length()))
                            , list.get(position).getEpg_time());
                } catch (NumberFormatException e) {
                    System.out.println("--------position" + list.get(position).getEpg_length());
                }

                String epgName = list.get(position).getRunning_epg_name();
                String year = list.get(position).getYear();
                if (year != null && !year.trim().isEmpty()) {
                    epgName += " (" + year + ")";
                }
                holder.show_name.setText(epgName);
                holder.show_time.setText(list.get(position).getEpg_time() + " - " + time);
            }
        }
    }

    @Override
    public void onBindViewHolder(final Myviewholder holder, final int position) {

        boolean isExist  = false;
        for (int i = 0; i < listViewHolder.size(); i++)
        {
            if (listViewHolder.get(i).chId.equals(list.get(position).getSub_channelsid()))
            {
                isExist = true;
                break;
            }
        }

        if (!isExist)
            listViewHolder.add(holder);

        String time="";
        // holder.text_channel.setVisibility(View.VISIBLE);
        ImageCacheUtil.with(context)
                .load(Constant.BASE_URL_IMAGE + list.get(position).getImage())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_recyclerview_child);
        try {
            time = Constant.getTimeByAdding((Constant.parseInt(list.get(position).getEpg_length()))
                    , list.get(position).getEpg_time());
        } catch (NumberFormatException e) {
            System.out.println("--------position" + list.get(position).getEpg_length());
        }

        String epgName = list.get(position).getRunning_epg_name();
        String year = list.get(position).getYear();
        if (year != null && !year.trim().isEmpty()) {
            epgName += " (" + year + ")";
        }
        holder.show_name.setText(epgName);
        holder.show_time.setText(list.get(position).getEpg_time() + " - " + time);
        holder.chId = list.get(position).getSub_channelsid();
        //show_name.setSelected(false);

        if (home != null && home.getAvailableAdapterId() == recordAdapterId && home.getSelectedPosition(HomeActivity.AllchannelRecyclerview) == position)//AllChannels_Fragment.flag_allchannel==1 && position==0
        {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    holder.total_layout_child_recyclerview.setFocusable(true);
                    holder.total_layout_child_recyclerview.requestFocus();
                }
            });
//            AllChannels_Fragment.flag_allchannel=0;
        }

        holder.total_layout_child_recyclerview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    holder.show_name.setSelected(false);
                    v.animate().scaleX(1).scaleY(1).setDuration(200);
                } else {
                    AllChannels_Fragment.itemPosition = position;
                    holder.show_name.setSelected(true);
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                }
            }
        });

        holder.total_layout_child_recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(home != null){
                    home.setAvialableAdapterId(recordAdapterId);
                    home.setSelectedPosition(HomeActivity.AllchannelRecyclerview, position);
                }
                AllChannels_Fragment.channel_id = list.get(position).getSub_channelsid();

                Intent in = new Intent(context, LiveChannels_Play_Activity.class);

                in.putExtra(LiveChannels_Play_Activity.CHANNEL_ID, list.get(position).getSub_channelsid());
                in.putExtra(LiveChannels_Play_Activity.MAIN_CH_ID, list.get(position).getChid());
                in.putExtra(LiveChannels_Play_Activity.CH_ICON, list.get(position).getImage());
                context.startActivity(in);
            }
        });

       /* holder.total_layout_child_recyclerview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                channel_id=list.get(position).getSub_channelsid();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                LoadScheduleRecord(date);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setAllchannelRecyclerviewAdapterId(int adapterId){
        recordAdapterId = adapterId;
    }

    public void setList(ArrayList<SetgetSubchannels> channelList) {
        list = channelList;
    }
}