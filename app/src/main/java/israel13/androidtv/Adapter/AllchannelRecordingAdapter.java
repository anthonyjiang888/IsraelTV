package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;


import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.LoadRecordList_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 20/06/16.
 */
public class AllchannelRecordingAdapter extends RecyclerView.Adapter<AllchannelRecordingAdapter.Myviewholder>
{

    private ArrayList<SetgetSubchannels> list;
    private List<Myviewholder> listViewHolder = new ArrayList<Myviewholder>();
    private Context context;
    SharedPreferences logindetails;
    public static ArrayList<SetgetSubchannels> array_subchannels;
    public static int selected_position=0;

    HomeActivity home = null;
    private int channelRecordAdapterId = 0;
    private int mChildCnt = 0;

    public class Myviewholder extends RecyclerView.ViewHolder
    {

        public RoundedImageView image_recyclerview_child;
        public FrameLayout total_layout_child_recyclerview;
        public TextView show_time,show_name;
        public LinearLayout text_channel;
        public String chId;


        public Myviewholder(View itemView) {
            super(itemView);


            image_recyclerview_child = (RoundedImageView) itemView.findViewById(R.id.image_recyclerview_child);
            total_layout_child_recyclerview = (FrameLayout) itemView.findViewById(R.id.total_layout_child_recyclerview);
            show_time = (TextView) itemView.findViewById(R.id.show_time);
            show_name = (TextView) itemView.findViewById(R.id.show_name);
            text_channel = (LinearLayout) itemView.findViewById(R.id.text_channel);

            total_layout_child_recyclerview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        v.animate().scaleX(1).scaleY(1).setDuration(200);
                    }else{
                        v.animate().scaleX(1.12f).scaleY(1.12f).setDuration(200);
                    }
                }
            });


        }
    }

    public AllchannelRecordingAdapter(ArrayList<SetgetSubchannels> list, Context context) {
        this.list = list;
        this.context=context;
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
                if (channelRecordAdapterId == home.getAvailableAdapterId() && position == home.getSelectedPosition(HomeActivity.AllchannelRecording)) {// && All_record_Fragment.flag_allradio == 1
                    holder.total_layout_child_recyclerview.requestFocus();
                }
            }
        }
    }
    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(home == null){
            home = (HomeActivity)parent.getContext();
        }
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_activity_allchannels_recyclerview_child,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

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

        logindetails = context.getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        array_subchannels=new ArrayList<>();
        holder.chId = list.get(position).getSub_channelsid();

        holder.text_channel.setVisibility(View.GONE);
        ImageCacheUtil.with(context)
                .load(Constant.BASE_URL_IMAGE + list.get(position).getImage())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_recyclerview_child);

        if(home != null) {
            if (channelRecordAdapterId == home.getAvailableAdapterId() && position == home.getSelectedPosition(HomeActivity.AllchannelRecording)) {// && All_record_Fragment.flag_allradio == 1
                holder.total_layout_child_recyclerview.requestFocus();
//                All_record_Fragment.flag_allradio = 0;
            }
        }


        holder.total_layout_child_recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(home != null){
                    home.setSelectedPosition(HomeActivity.AllchannelRecording, position);
                    home.setAvialableAdapterId(channelRecordAdapterId);
                }

                AllChannels_Fragment.channel_id = list.get(position).getSub_channelsid();
                AllChannels_Fragment.channel_name=list.get(position).getName();
                AllChannels_Fragment.channel_icon=list.get(position).getImage();
                if (list.get(position).getIsradio().equalsIgnoreCase("0"))
                {

                    LoadRecordList_Fragment loadRecordList_Fragment= new LoadRecordList_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                    // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.add(R.id.fram_container, loadRecordList_Fragment, "layout_recording_frag");
                    ft1.addToBackStack(null);
                    ft1.commit();

                }
                else
                {

                   /* Intent in = new Intent(context, LiveChannels_Play_Activity.class);
                    AllChannels_Fragment.channel_name = list.get(position).getName();
                    AllChannels_Fragment.channel_icon = Constant.BASE_URL_IMAGE + list.get(position).getImage();
                    AllChannels_Fragment.is_infav = list.get(position).getIsinfav();
                    context.startActivity(in);*/
                }



            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setAllchannelRecordingAdapterId(int recordAdapterId){
        channelRecordAdapterId = recordAdapterId;
    }

    public void setList(ArrayList<SetgetSubchannels> channelList) {
        list = channelList;
    }
}
