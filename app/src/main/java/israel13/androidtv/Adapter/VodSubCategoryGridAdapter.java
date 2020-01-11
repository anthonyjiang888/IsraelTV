package israel13.androidtv.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
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

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodSubcategory;


/**
 * Created by sudipta on 24/10/16.
 */
public class VodSubCategoryGridAdapter extends ArrayAdapter  {


    ArrayList<SetgetVodSubcategory> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;
    int GET_VOD_SUBCATEGORIES=170;
    ProgressDialog pDialog;

    ArrayList<SetgetVodSubcategory> subcategoriesList;

    @SuppressWarnings("unchecked")
    public VodSubCategoryGridAdapter(Context mContext, int resourceID, ArrayList<SetgetVodSubcategory> list) {
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


            holder.name_child_vod_subcategory=(TextView)view.findViewById(R.id.name_child_vod_subcategory);
            holder.image_child_vod_subcategory = (ImageView) view.findViewById(R.id.image_child_vod_subcategory);
            holder.totalview_child_vod_subcategory = (RelativeLayout) view.findViewById(R.id.totalview_child_vod_subcategory);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.name_child_vod_subcategory.setText(list.get(position).getName());
        Glide.with(mContext)
                .load("http:" + list.get(position).getShowpic())
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .override(200, 200).skipMemoryCache(true)
                .into(holder.image_child_vod_subcategory);


        return view;

    }





    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView name_child_vod_subcategory;
        public ImageView image_child_vod_subcategory;
        public RelativeLayout totalview_child_vod_subcategory;
    }

}