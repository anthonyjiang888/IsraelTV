package israel13.androidtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;

/**
 * Created by sudipta on 24/10/16.
 */
public class VodSearchLiveGridAdapter extends ArrayAdapter {


    ArrayList<SetgetSubchannels> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;

    @SuppressWarnings("unchecked")
    public VodSearchLiveGridAdapter(Context mContext, int resourceID, ArrayList<SetgetSubchannels> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceID, null);
            holder = new ViewHolder();

            holder.image_recyclerview_child_live = (RoundedImageView) view.findViewById(R.id.image_recyclerview_child_live);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        Glide.with(mContext)
                .load(Constant.BASE_URL_IMAGE + list.get(position).getImage())
                .override(200,200)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .into(holder.image_recyclerview_child_live);

        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public RoundedImageView image_recyclerview_child_live;
    }

}