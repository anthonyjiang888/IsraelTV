package israel13.androidtv.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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
import israel13.androidtv.Fragments.Favorites_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetEpisodes;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class FavoritesEpisodesAdapter extends RecyclerView.Adapter<FavoritesEpisodesAdapter.Myviewholder>
{

    private ArrayList<SetgetEpisodes> list;
    private Context context;
    private Favorites_Fragment fav_fag;

    HomeActivity home = null;

    public class Myviewholder extends RecyclerView.ViewHolder
    {


        public TextView name_child_favorites_episodeslist;
        public ImageView image_child_favorites_episodeslist;
        public FrameLayout totalview_child_favorites_episodeslist;
        public LinearLayout linearlayout_close_favorites_episodelist;


        public Myviewholder(View itemView) {
            super(itemView);


            name_child_favorites_episodeslist=(TextView)itemView.findViewById(R.id.name_child_favorites_episodeslist);
            image_child_favorites_episodeslist = (ImageView) itemView.findViewById(R.id.image_child_favorites_episodeslist);
            totalview_child_favorites_episodeslist = (FrameLayout) itemView.findViewById(R.id.totalview_child_favorites_episodeslist);
            linearlayout_close_favorites_episodelist = (LinearLayout) itemView.findViewById(R.id.linearlayout_close_favorites_episodelist);

            totalview_child_favorites_episodeslist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    //Toast.makeText(context,"on focus change---",Toast.LENGTH_SHORT).show();
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

    public FavoritesEpisodesAdapter(ArrayList<SetgetEpisodes> list, Context context,Favorites_Fragment fav_fag) {
        this.list = list;
        this.context=context;
        this.fav_fag=fav_fag;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(home == null){
            home = (HomeActivity) parent.getContext();
        }
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_activity_favorites_episodeslist,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

        holder.name_child_favorites_episodeslist.setText(list.get(position).getEpisode_name());
        ImageCacheUtil.with(context)
                .load("http:" + list.get(position).getEpisodes_pic())
                .resize(200,200)
                .cacheUsage(false, true)
                .into(holder.image_child_favorites_episodeslist);

        if(Favorites_Fragment.liveChannelsList.size()==0)
        {
            if(position==0)
            {
                if (fav_fag.isselected == false)
                    holder.totalview_child_favorites_episodeslist.requestFocus();
                else
                    fav_fag.isselected = false;
            }
        }
        if(Favorites_Fragment.flag_delete.equalsIgnoreCase("delete_tvshow"))
        {
            if(position==0)
            {
                holder.totalview_child_favorites_episodeslist.requestFocus();
            }
        }
        if (list.get(position).getIsactive().equalsIgnoreCase("active"))
        {
            holder.linearlayout_close_favorites_episodelist.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.linearlayout_close_favorites_episodelist.setVisibility(View.GONE);
        }
        if(home != null && home.getAvailableAdapterId() == HomeActivity.FavoritesEpisodes && home.getSelectedPosition(HomeActivity.FavoritesEpisodes) == position){
            fav_fag.isselected = true;
            holder.totalview_child_favorites_episodeslist.requestFocus();
        }
     /*   holder.linearlayout_close_favorites_episodelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((Favorites_Fragment)context).Remove("episode",list.get(position).getEpisode_id());

            }
        });
*/
        holder.totalview_child_favorites_episodeslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(home != null){
                    home.setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
                    home.setSelectedPosition(HomeActivity.FavoritesEpisodes, position);
                }
                if (list.get(position).getIsactive().equalsIgnoreCase("active"))
                {
                    fav_fag.remove("episode",list.get(position).getEpisode_id(), position);
                }
                else
                {
                    VodSubcategory_Fragment.vod_subcategory_id = list.get(position).getEpisode_id();
                    VodSubcategory_Fragment.vod_subcategory_image = list.get(position).getEpisodes_pic();
                    VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getEpisode_name();
                    VodSubcategory_Fragment.Cat_name="מועדפים";
                    VodSubcategory_Fragment.is_infav ="1";


                    VodTvShowVideo_Fragment allvod_tv_show_cat_frag= new VodTvShowVideo_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                    // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.replace(R.id.fram_container, allvod_tv_show_cat_frag, "layout_tvshow_screen");
                    ft1.addToBackStack(null);
                    ft1.commit();
                }
                fav_fag.isselected = false;

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}