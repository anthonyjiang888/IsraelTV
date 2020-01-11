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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Fragments.VodMainPage_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodMainPageCategory;
import israel13.androidtv.Utils.ImageCacheUtil;


/**
 * Created by puspak on 07/07/17.
 */
public class VodMainpageGridRecyclerAdapter extends RecyclerView.Adapter<VodMainpageGridRecyclerAdapter.Myviewholder>
{
    public interface ScrollbartoTop{
        void onScrollbarTop();
    }
    public ScrollbartoTop mScrollController;
    ArrayList<SetgetVodMainPageCategory> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;

    HomeActivity home = null;

    public class Myviewholder extends RecyclerView.ViewHolder
    {


        public TextView name_child_vod_mainpage;
        public ImageView image_child_vod_mainpage;
        public RelativeLayout totalview_child_vod_mainpage;



        public Myviewholder(View itemView) {
            super(itemView);


            name_child_vod_mainpage=(TextView)itemView.findViewById(R.id.name_child_vod_mainpage);
            image_child_vod_mainpage = (ImageView) itemView.findViewById(R.id.image_child_vod_mainpage);
            totalview_child_vod_mainpage = (RelativeLayout)itemView.findViewById(R.id.totalview_child_vod_mainpage);

        }
    }

    public VodMainpageGridRecyclerAdapter(ArrayList<SetgetVodMainPageCategory> list, Context mContext) {
        this.list = list;
        this.mContext=mContext;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(home == null){
            home = (HomeActivity) parent.getContext();
        }
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_gridview_vod_mainpage,parent,false);
        return new Myviewholder(itemview);
    }
    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

        holder.name_child_vod_mainpage.setText(list.get(position).getName());
        ImageCacheUtil.with(mContext)
                .load("http:" + list.get(position).getShowpic())
                .resize(200, 200)
                .cacheUsage(false, true)
                .into(holder.image_child_vod_mainpage);

        if (home != null && home.getAvailableAdapterId() == HomeActivity.VodGridRecycler)
        {
            if(position == home.getSelectedPosition(HomeActivity.VodGridRecycler)) {
                holder.totalview_child_vod_mainpage.requestFocus();
            }
        }
        holder.totalview_child_vod_mainpage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    view.animate().scaleX(1).scaleY(1).setDuration(200);
                }else{
                    if (position >= 0 && position <= 4 && mScrollController != null)
                        mScrollController.onScrollbarTop();
                    view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                }
            }
        });
        holder.totalview_child_vod_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > position) {
                    if(home != null){
                        home.setAvialableAdapterId(HomeActivity.VodGridRecycler);
                        home.setSelectedPosition(HomeActivity.VodGridRecycler, position);
                    }

                    VodMainPage_Fragment.vod_category_id = list.get(position).getCat_id();
                    VodMainPage_Fragment.vod_category_name = list.get(position).getName();
                    // mContext.startActivity(new Intent(mContext, VodSubcategory_Fragment.class));

                    // Toast.makeText(HomeActivity.this,"Click Tv_line_tv",Toast.LENGTH_SHORT).show();
                    VodSubcategory_Fragment allvod_cat_frag = new VodSubcategory_Fragment();
                    FragmentTransaction ft1 = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                    // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.add(R.id.fram_container, allvod_cat_frag, "layout_Vodsubcategory");
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

