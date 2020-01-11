package israel13.androidtv.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Fragments.Search_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodSubcategory;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class Search_TV_ShowAdapter extends RecyclerView.Adapter<Search_TV_ShowAdapter.Myviewholder>
{
    private ArrayList<SetgetVodSubcategory> list;
    private Context context;
    private Search_Fragment search_fragment;
    private int Flag;
    public class Myviewholder extends RecyclerView.ViewHolder
    {
        public TextView name_child_favorites_episodeslist;
        public ImageView image_child_favorites_episodeslist;
        public FrameLayout totalview_child_favorites_episodeslist;
        public LinearLayout linearlayout_close_favorites_episodelist;
        public RelativeLayout relative_lay;
        public Myviewholder(View itemView) {
            super(itemView);

            name_child_favorites_episodeslist=(TextView)itemView.findViewById(R.id.name_child_favorites_episodeslist);
            image_child_favorites_episodeslist = (ImageView) itemView.findViewById(R.id.image_child_favorites_episodeslist);
            totalview_child_favorites_episodeslist = (FrameLayout) itemView.findViewById(R.id.totalview_child_favorites_episodeslist);
            linearlayout_close_favorites_episodelist = (LinearLayout) itemView.findViewById(R.id.linearlayout_close_favorites_episodelist);
            relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);
            totalview_child_favorites_episodeslist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        name_child_favorites_episodeslist.setSelected(false);
                        v.animate().scaleX(1).scaleY(1).setDuration(200);
                    }
                    else {
                        name_child_favorites_episodeslist.setSelected(true);
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                    }
                }
            });
        }
    }
    public void setFlag(int flag){
        Flag = flag;
    }
    public Search_TV_ShowAdapter(ArrayList<SetgetVodSubcategory> list, Context context, Search_Fragment search_fragment) {
        this.list = list;
        this.context=context;
        this.search_fragment=search_fragment;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_search_episodeslist,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

        SpannableString text = (search_fragment.SettextColorNew(search_fragment.edit_text_search.getText().toString(),list.get(position).getName()));
        holder.name_child_favorites_episodeslist.setText(text);
      //  holder.name_child_favorites_episodeslist.setText(list.get(position).getName());
        ImageCacheUtil.with(context)
                .load("http:" + list.get(position).getShowpic())
                .resize(200,200)
                .cacheUsage(false, true)
                .into(holder.image_child_favorites_episodeslist);
        int selectedPos = ((HomeActivity)context).getSelectedPosition(HomeActivity.VodSearchEpisodes);
        if (position == 0 && Flag == 1) {
            holder.totalview_child_favorites_episodeslist.requestFocus();
        }
        if ( selectedPos > -1 && selectedPos == position && Flag == 2) {
            Log.e("test", "selected position: " + selectedPos);
            holder.totalview_child_favorites_episodeslist.requestFocus();
        }



        holder.totalview_child_favorites_episodeslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).setSelectedPosition(HomeActivity.VodSearchEpisodes, position);
                VodSubcategory_Fragment.vod_subcategory_id = list.get(position).getSubcat_id();
                VodSubcategory_Fragment.vod_subcategory_image = list.get(position).getShowpic();
                VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getName();
                VodSubcategory_Fragment.Cat_name="חיפוש";
                VodSubcategory_Fragment.is_infav =list.get(position).getIsinfav();
                Search_Fragment.flag_page_change=1;
                VodSubcategory_Fragment.vod_subcategory_year = list.get(position).getYear();

                VodTvShowVideo_Fragment allvod_tv_show_cat_frag= new VodTvShowVideo_Fragment();
                FragmentTransaction ft1 =((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
               // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft1.add(R.id.fram_container, allvod_tv_show_cat_frag, "layout_tvshow_screen");
                ft1.addToBackStack(null);
                ft1.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
