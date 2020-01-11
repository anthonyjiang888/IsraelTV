package israel13.androidtv.Adapter;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.Search_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDates;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDatesDetails;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class SearchRecordRecyclerviewAdapter extends RecyclerView.Adapter<SearchRecordRecyclerviewAdapter.Myviewholder>
{
    private ArrayList<SetgetSearchRecordDates> list;
    private Context context;
    public static ArrayList<SetgetSearchRecordDatesDetails> templist;
    private  Search_Fragment search_fragment;

    public class Myviewholder extends RecyclerView.ViewHolder
    {
        public RoundedImageView image_child_new_search_record;
        public RelativeLayout total_layout_child_new_search_recyclerview;
        public TextView child_new_search_record_result;
        public RelativeLayout relative_lay;
        public Myviewholder(View itemView) {
            super(itemView);

            image_child_new_search_record = (RoundedImageView) itemView.findViewById(R.id.image_child_new_search_record);
            child_new_search_record_result = (TextView) itemView.findViewById(R.id.child_new_search_record_result);
            total_layout_child_new_search_recyclerview = (RelativeLayout) itemView.findViewById(R.id.total_layout_child_new_search_recyclerview);
            relative_lay = (RelativeLayout)itemView.findViewById(R.id.relative_lay);
            total_layout_child_new_search_recyclerview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b){
                        view.animate().scaleX(1).scaleY(1).setDuration(200);
                    }else{
                        view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                    }
                }
            });
        }
    }

    public SearchRecordRecyclerviewAdapter(ArrayList<SetgetSearchRecordDates> list, Context context, Search_Fragment search_fragment) {
        this.list = list;
        this.context=context;
        this.search_fragment=search_fragment;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_new_search_record,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(final Myviewholder holder, final int position) {
//        holder.child_new_search_record_result.setBackgroundResource(R.color.black_transparent);
        holder.child_new_search_record_result.setText("תוצאות: " + list.get(position).getSetgetSearchRecordDatesDetailsArrayList().size());

        if (list.get(position).getIsselected().equalsIgnoreCase("true"))
        {
            holder.relative_lay.setBackground(context.getResources().getDrawable(R.drawable.custom_bg_focus_white));
        }
        else
        {
            holder.relative_lay.setBackground(context.getResources().getDrawable(R.drawable.custom_bg1));
          //  holder.child_new_search_record_result.setText("");
          //  holder.child_new_search_record_result.setBackgroundResource(R.color.transparent);
        }
        ImageCacheUtil.with(context)
                .load(Constant.BASE_URL_IMAGE + list.get(position).getChannel_logo())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_child_new_search_record);
        if (position == 0 && search_fragment.SelectedNumber == 1)
            holder.total_layout_child_new_search_recyclerview.requestFocus();
        holder.total_layout_child_new_search_recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templist = new ArrayList<>();
                Search_Fragment.record_child_new_search_recyclerview.setVisibility(View.VISIBLE);

                for (int i = 0 ; i < list.size();i++)
                {
                    if (i==position)
                    {
                        list.get(i).setIsselected("true");
                    }
                    else
                    {
                        list.get(i).setIsselected("false");
                    }
                }

                AllChannels_Fragment.channel_id=list.get(position).getChannelname();
                Search_Fragment.searchRecordRecyclerviewAdapter.notifyDataSetChanged();

                for (int i = 0 ; i <list.get(position).getSetgetSearchRecordDatesDetailsArrayList().size() ;i++ )
                {
                    templist.add(list.get(position).getSetgetSearchRecordDatesDetailsArrayList().get(i));
                }
                Collections.sort(templist, new DateComparator_subRecord());
                Search_Fragment.record_child_new_search_recyclerview.setAdapter(new SearchRecordChildRecyclerviewAdapter
                        (templist,context,search_fragment));

            }
        });
    }

    class DateComparator_subRecord implements Comparator<SetgetSearchRecordDatesDetails> {
        @Override
        public int compare(SetgetSearchRecordDatesDetails lhs, SetgetSearchRecordDatesDetails rhs) {
            Double distance = Double.valueOf(lhs.getRdatetime());
            Double distance1 = Double.valueOf(rhs.getRdatetime());
            if (distance.compareTo(distance1) < 0) {
                return 1;
            } else if (distance.compareTo(distance1) > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}