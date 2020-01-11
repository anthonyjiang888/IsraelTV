package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LiveChannels_Play_Activity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetLoadScheduleRecord;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;


/**
 * Created by puspak on 20/06/16.
 */
public class AllRadioRecyclerviewAdapter extends RecyclerView.Adapter<AllRadioRecyclerviewAdapter.Myviewholder>
{

    private ArrayList<SetgetSubchannels> list;
    public Context context;
    SharedPreferences logindetails;
    public static ArrayList<SetgetSubchannels> array_subchannels = new ArrayList<>();
    public static int selected_position=0;
    int LOAD_RECORD_SCHEDULE = 160;
    ArrayList<SetgetLoadScheduleRecord> LoadScheduleRecordsList;
    int running_show_time;
    public static TextView show_time,show_name;
    public String channel_id="";
    View.OnFocusChangeListener listener;

    HomeActivity home = null;
    private int recordAdapterId = 0;
    private int mChildCnt = 0;

    public class Myviewholder extends RecyclerView.ViewHolder
    {

        public RoundedImageView image_recyclerview_child;
        public LinearLayout text_channel;
        public FrameLayout total_layout_child_recyclerview;
        public RelativeLayout relative_lay;



        public Myviewholder(View itemView) {
            super(itemView);


            image_recyclerview_child = (RoundedImageView) itemView.findViewById(R.id.image_recyclerview_child);
            total_layout_child_recyclerview = (FrameLayout) itemView.findViewById(R.id.total_layout_child_recyclerview);
            text_channel = (LinearLayout) itemView.findViewById(R.id.text_channel);
            show_time = (TextView) itemView.findViewById(R.id.show_time);
            show_name = (TextView) itemView.findViewById(R.id.show_name);
            relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);

            total_layout_child_recyclerview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        view.animate().scaleX(1).scaleY(1).setDuration(200);
                    }else{
                        view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                    }
                }
            });
           /* total_layout_child_recyclerview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                   //Toast.makeText(context,"on focus change---",Toast.LENGTH_SHORT).show();
                    if (!hasFocus)
                    {Toast.makeText(context,"on focus change---",Toast.LENGTH_SHORT).show();
                        text_channel.setVisibility(View.GONE);
                    }
                    else text_channel.setVisibility(View.VISIBLE);

                }
            });*/
        }

    }

    public AllRadioRecyclerviewAdapter(ArrayList<SetgetSubchannels> list, Context context) {
        this.list = list;
        this.context=context;
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
    public void onBindViewHolder(final Myviewholder holder, final int position) {
        String time="";
        holder.text_channel.setVisibility(View.GONE);

        logindetails = context.getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        ImageCacheUtil.with(context)
                .load(Constant.BASE_URL_IMAGE + list.get(position).getImage())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_recyclerview_child);

        if(home != null && home.getAvailableAdapterId() == recordAdapterId && home.getSelectedPosition(HomeActivity.AllRadioRecyclerview) == position){
            holder.total_layout_child_recyclerview.requestFocus();
        }


        holder.total_layout_child_recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(home != null){
                    home.setAvialableAdapterId(recordAdapterId);
                    home.setSelectedPosition(HomeActivity.AllRadioRecyclerview, position);
                }
                array_subchannels.clear();
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

    public void setAllRadioRecyclerviewAdapterId(int adapterId){
        recordAdapterId = adapterId;
    }

    public void setChildCnt(int nCnt){
        mChildCnt = nCnt;
    }

    public int getChildCnt(){
        return mChildCnt;
    }
}
