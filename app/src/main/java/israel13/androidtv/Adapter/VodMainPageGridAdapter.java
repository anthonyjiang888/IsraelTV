package israel13.androidtv.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Fragments.VodMainPage_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodMainPageCategory;

/**
 * Created by Puspak on 23/06/17.
 */
public class VodMainPageGridAdapter extends ArrayAdapter {


    ArrayList<SetgetVodMainPageCategory> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;

    @SuppressWarnings("unchecked")
    public VodMainPageGridAdapter(Context mContext, int resourceID, ArrayList<SetgetVodMainPageCategory> list) {
        super(mContext, resourceID, list);
        this.mContext = mContext;
        this.resourceID = resourceID;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceID, null);
            holder = new ViewHolder();


            holder.name_child_vod_mainpage=(TextView)view.findViewById(R.id.name_child_vod_mainpage);
            holder.image_child_vod_mainpage = (ImageView) view.findViewById(R.id.image_child_vod_mainpage);
            holder.totalview_child_vod_mainpage = (RelativeLayout) view.findViewById(R.id.totalview_child_vod_mainpage);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.name_child_vod_mainpage.setText(list.get(position).getName());
        Glide.with(mContext)
                .load("http:" + list.get(position).getShowpic())
                .override(200, 200).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .into(holder.image_child_vod_mainpage);

        if (position==0)
        {
            holder.totalview_child_vod_mainpage.requestFocus();
        }
        holder.totalview_child_vod_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                VodMainPage_Fragment.vod_category_id = list.get(position).getCat_id();
                VodMainPage_Fragment.vod_category_name = list.get(position).getName();
               // mContext.startActivity(new Intent(mContext, VodSubcategory_Fragment.class));

                // Toast.makeText(HomeActivity.this,"Click Tv_line_tv",Toast.LENGTH_SHORT).show();
                VodSubcategory_Fragment allvod_cat_frag= new VodSubcategory_Fragment();
                FragmentTransaction ft1 =((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
               // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft1.replace(R.id.fram_container, allvod_cat_frag, "layout_Vodsubcategory");
                ft1.addToBackStack(null);
                ft1.commit();

            }
        });

        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView name_child_vod_mainpage;
        public ImageView image_child_vod_mainpage;
        public RelativeLayout totalview_child_vod_mainpage;
    }

}