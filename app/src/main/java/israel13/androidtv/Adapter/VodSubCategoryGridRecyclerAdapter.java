package israel13.androidtv.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodSubcategory;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 07/07/17.
 */
public class VodSubCategoryGridRecyclerAdapter extends BaseAdapter {
        public ArrayList<SetgetVodSubcategory> list;
        public Context mContext;

        HomeActivity home = null;

        public VodSubCategoryGridRecyclerAdapter(ArrayList<SetgetVodSubcategory> list, Context mContext) {
                this.list = list;
                this.mContext=mContext;
        }

        public class ViewHolder {
                public TextView name_child_vod_subcategory;
                public ImageView image_child_vod_subcategory;
                public RelativeLayout totalview_child_vod_subcategory;

                public ViewHolder(View itemView) {
                        name_child_vod_subcategory=(TextView)itemView.findViewById(R.id.name_child_vod_subcategory);
                        image_child_vod_subcategory = (ImageView) itemView.findViewById(R.id.image_child_vod_subcategory);
                        totalview_child_vod_subcategory = (RelativeLayout) itemView.findViewById(R.id.totalview_child_vod_subcategory);
                        totalview_child_vod_subcategory.setFocusable(false);
                        totalview_child_vod_subcategory.setClickable(false);

                }
        }

        @Override
        public int getCount() {
                return list.size();
        }

        @Override
        public Object getItem(int position) {
                return list.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                ViewHolder holder = null;
                if (view == null) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_gridview_vod_subcategorypage,parent,false);
                        holder = new ViewHolder(view);
                        view.setTag(holder);
                } else {
                        holder = (ViewHolder)view.getTag();
                }
                holder.name_child_vod_subcategory.setText(list.get(position).getName());
                ImageCacheUtil.with(mContext)
                        .load("http:" + list.get(position).getShowpic())
                        .resize(200, 200)
                        .cacheUsage(false, true)
                        .into(holder.image_child_vod_subcategory);
                return view;
        }
}

