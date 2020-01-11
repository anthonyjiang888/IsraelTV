package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Activity.Records_Play_Activity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.LoadRecordList_Fragment;
import israel13.androidtv.Fragments.Search_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetLoadScheduleRecord;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDatesDetails;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class SearchRecordChildRecyclerviewAdapter extends RecyclerView.Adapter<SearchRecordChildRecyclerviewAdapter.Myviewholder>
{

    private ArrayList<SetgetSearchRecordDatesDetails> list;
    private Context context;
    private Search_Fragment search_fragment;



    public class Myviewholder extends RecyclerView.ViewHolder
    {

        public ImageView image_record_search_each_channel;
        public RelativeLayout total_layout_child_record_each_channel;
        public TextView date_day_record_search_each_channel,time_epgname_record_search_each_channel,time_epgtime;
        public RelativeLayout relative_lay;
        public Myviewholder(View itemView) {
            super(itemView);


            image_record_search_each_channel = (ImageView) itemView.findViewById(R.id.image_record_search_each_channel);
            date_day_record_search_each_channel = (TextView) itemView.findViewById(R.id.date_day_record_search_each_channel);
            time_epgname_record_search_each_channel = (TextView) itemView.findViewById(R.id.time_epgname_record_search_each_channel);
            time_epgtime = (TextView) itemView.findViewById(R.id.time_epgtime);
            total_layout_child_record_each_channel = (RelativeLayout) itemView.findViewById(R.id.total_layout_child_record_each_channel);
            relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);

            total_layout_child_record_each_channel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                    {
                        time_epgname_record_search_each_channel.setSelected(false);
                        v.animate().scaleX(1).scaleY(1).setDuration(200);
                    }
                    else {
                        time_epgname_record_search_each_channel.setSelected(true);
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                    }
                }
            });


        }
    }

    public SearchRecordChildRecyclerviewAdapter(ArrayList<SetgetSearchRecordDatesDetails> list, Context context,Search_Fragment search_fragment) {
        this.list = list;
        this.context=context;
        this.search_fragment=search_fragment;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_record_search_each_channel,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

if (position==0)
{
    holder.total_layout_child_record_each_channel.requestFocus();
}
        ImageCacheUtil.with(context)
                .load("https:"+list.get(position).getShowpic())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_record_search_each_channel);



        holder.date_day_record_search_each_channel.setText( "יום "+ Constant.dayByGivenDate(list.get(position).getWday())+" "+
                Constant.getUnixDate(list.get(position).getRdatetime()));
        SpannableString text = (Search_Fragment.Settext_ColorNew(search_fragment.edit_text_search.getText().toString(),(list.get(position).getName()),context));

          holder.time_epgtime.setText("- "+list.get(position).getTime());
          holder.time_epgname_record_search_each_channel.setText(text);
        RelativeLayout.LayoutParams mLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayout.setMargins(30, 30, 30, 30);
        holder.relative_lay.setLayoutParams(mLayout);
        holder.total_layout_child_record_each_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Records_Play_Activity.flag_delete = true;
                LoadRecordList_Fragment.LoadScheduleRecordsList=new ArrayList<SetgetLoadScheduleRecord>();
                for (int i=0;i<list.size();i++)
                {
                    SetgetLoadScheduleRecord setget=new SetgetLoadScheduleRecord();
                    setget.setRdatetime(list.get(i).getRdatetime());
                    setget.setShowpic(list.get(i).getShowpic());
                    setget.setStar(list.get(i).getStar());
                    setget.setGenre(list.get(i).getGenre());
                    setget.setChannel(list.get(i).getChannel());
                    setget.setDescription(list.get(i).getDescription());
                    setget.setLengthtime(list.get(i).getLengthtime());
                    setget.setName(list.get(i).getName());
                    setget.setTime(list.get(i).getTime());
                    setget.setWday(list.get(i).getWday());
                    setget.setWeekno(list.get(i).getWeekno());
                    setget.setIsinfav(list.get(i).getIsinfav());
                    setget.setIsradio(list.get(i).getIsradio());
                    setget.setLogo(list.get(i).getLogo());
                    setget.setRdate(list.get(i).getDate());
                    LoadRecordList_Fragment.LoadScheduleRecordsList.add(setget);
                }
                Search_Fragment.flag_page_change=1;
                Records_Play_Activity.current_position=position;
                Records_Play_Activity.rdatetime=list.get(position).getRdatetime();
                    Records_Play_Activity.show_pic=list.get(position).getShowpic();
                AllChannels_Fragment.channel_name=list.get(position).getChannelname();
                Records_Play_Activity.rating_record=list.get(position).getStar();
                Records_Play_Activity.isSearch = true;
                context.startActivity(new Intent(context,Records_Play_Activity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}