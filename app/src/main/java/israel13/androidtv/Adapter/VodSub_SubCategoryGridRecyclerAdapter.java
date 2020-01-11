package israel13.androidtv.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Fragments.VodSub_Sub_Subcategory_Fragment;
import israel13.androidtv.Fragments.VodSub_Subcategory_Fragment;
import israel13.androidtv.Fragments.VodSubcategoryMovies_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodSubcategory;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 07/07/17.
 */
public class VodSub_SubCategoryGridRecyclerAdapter extends RecyclerView.Adapter<VodSub_SubCategoryGridRecyclerAdapter.Myviewholder>
{
    public ArrayList<SetgetVodSubcategory> list;
    public Context mContext;
    HomeActivity home = null;

    public class Myviewholder extends RecyclerView.ViewHolder
    {
        public TextView name_child_vod_subcategory;
        public ImageView image_child_vod_subcategory;
        public RelativeLayout totalview_child_vod_subcategory;

        public Myviewholder(View itemView) {
            super(itemView);


            name_child_vod_subcategory=(TextView)itemView.findViewById(R.id.name_child_vod_subcategory);
            image_child_vod_subcategory = (ImageView) itemView.findViewById(R.id.image_child_vod_subcategory);
            totalview_child_vod_subcategory = (RelativeLayout) itemView.findViewById(R.id.totalview_child_vod_subcategory);

            totalview_child_vod_subcategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                    {
                        name_child_vod_subcategory.setSelected(false);
                    }
                    else name_child_vod_subcategory.setSelected(true);
                }
            });

        }
    }

    public VodSub_SubCategoryGridRecyclerAdapter(ArrayList<SetgetVodSubcategory> list, Context mContext) {
        this.list = list;
        this.mContext=mContext;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(home == null){
            home = (HomeActivity) parent.getContext();
        }
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_gridview_vod_subcategorypage,parent,false);
        return new Myviewholder(itemview);
    }
    @Override
    public void onBindViewHolder(final Myviewholder holder, final int position) {

        holder.name_child_vod_subcategory.setText(list.get(position).getName());
        ImageCacheUtil.with(mContext)
                .load("http:" + list.get(position).getShowpic())
                .resize(200, 200)
                .cacheUsage(false, true)
                .into(holder.image_child_vod_subcategory);

        if(home != null && position == home.getSelectedPosition(HomeActivity.VodSub_SubCategoryGridRecycler))
        {
            holder.totalview_child_vod_subcategory.requestFocus();
        }

        holder.totalview_child_vod_subcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(home != null){
                    home.setSelectedPosition(HomeActivity.VodSub_SubCategoryGridRecycler, position);
                }
                if (list.get(position).getGotoact().equalsIgnoreCase("0")) {

                    VodSub_Sub_Subcategory_Fragment.vod_category_id = list.get(position).getSubcat_id();
                    VodSub_Subcategory_Fragment.cat_name.setText(VodSubcategory_Fragment.Cat_name+" > "+list.get(position).getName());
                    VodSub_Sub_Subcategory_Fragment.Cat_name=VodSub_Subcategory_Fragment.Cat_name+" > "+list.get(position).getName();

                    VodSub_Sub_Subcategory_Fragment Vod_sub_sub_sub_cat_frag= new VodSub_Sub_Subcategory_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                   // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.replace(R.id.fram_container, Vod_sub_sub_sub_cat_frag, "layout_sub_sub_subcat");
                    ft1.addToBackStack(null);
                    ft1.commit();

                } else if (list.get(position).getGotoact().equalsIgnoreCase("2")) {

                    VodSubcategory_Fragment.vod_subcategory_id = list.get(position).getSubcat_id();
                    VodSubcategory_Fragment.vod_subcategory_image = list.get(position).getShowpic();
                    VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getName();
                    VodSubcategory_Fragment.vod_subcategory_year = list.get(position).getYear();
                    VodSubcategory_Fragment.is_infav =list.get(position).getIsinfav();
                    VodSubcategory_Fragment.Cat_name=VodSubcategory_Fragment.cat_name.getText().toString();
                    //Cat_name=Cat_name+" | "+subcategoriesList.get(position).getName();


                    VodTvShowVideo_Fragment allvod_tv_show_cat_frag= new VodTvShowVideo_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                   // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.replace(R.id.fram_container, allvod_tv_show_cat_frag, "layout_tvshow_screen");
                    ft1.addToBackStack(null);
                    ft1.commit();


                } else {
                    VodSubcategory_Fragment.vod_subcategory_id = list.get(position).getSubcat_id();
                    VodSubcategory_Fragment.vod_subcategory_image = list.get(position).getShowpic();
                    VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getName();
                    VodSubcategory_Fragment.Cat_name=VodSubcategory_Fragment.cat_name.getText().toString();
                    VodSubcategory_Fragment.Cat_name=VodSubcategory_Fragment.Cat_name+" > "+list.get(position).getName();
                    VodMovieVideoPlayActivity.movie_play_cat=list.get(position).getName();
                    VodSubcategoryMovies_Fragment allvod_sub_movie_cat_frag= new VodSubcategoryMovies_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                   // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.replace(R.id.fram_container, allvod_sub_movie_cat_frag, "layout_VodMoviesubcategory");
                    ft1.addToBackStack(null);
                    ft1.commit();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}

