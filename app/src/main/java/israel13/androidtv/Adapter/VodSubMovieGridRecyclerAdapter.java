package israel13.androidtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 7/7/17.
 */

public class VodSubMovieGridRecyclerAdapter extends BaseAdapter {

    ArrayList<SetgetMovies> list;
    Context mContext;
    HomeActivity home = null;
    public boolean mbDatasetNotified = false;

    public VodSubMovieGridRecyclerAdapter(ArrayList < SetgetMovies > list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public class ViewHolder {
        public TextView movie_name_child_vod_subcategory_movies;
        public ImageView image_child_vod_subcategory_movies;
        public RelativeLayout totalview_child_vod_subcategory_movies;
        public TextView movie_year;
        public RatingBar movie_ratingstar;
        public RelativeLayout relative_lay;

        public ViewHolder(View itemView) {
            movie_name_child_vod_subcategory_movies = (TextView) itemView.findViewById(R.id.movie_name_child_vod_subcategory_movies);
            image_child_vod_subcategory_movies = (ImageView) itemView.findViewById(R.id.image_child_vod_subcategory_movies);
            totalview_child_vod_subcategory_movies = (RelativeLayout) itemView.findViewById(R.id.totalview_child_vod_subcategory_movies);
            relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);
            movie_year = (TextView) itemView.findViewById(R.id.txt_movie_year);
            movie_ratingstar = (RatingBar)itemView.findViewById(R.id.ratingBar_movie_star);
            totalview_child_vod_subcategory_movies.setFocusable(false);
            totalview_child_vod_subcategory_movies.setClickable(false);
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
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.child_activity_vod_subcategory_movies, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.movie_name_child_vod_subcategory_movies.setText(list.get(position).getMovies_name());
        ImageCacheUtil.with(mContext)
                .load("http:" + list.get(position).getMovies_pic())
                .resize(200,200)
                .cacheUsage(false, true)
                .into(holder.image_child_vod_subcategory_movies);
        holder.movie_year.setText(list.get(position).getMovies_year());
        holder.movie_ratingstar.setRating(Constant.parseFloat(list.get(position).getMovies_stars()));

        return view;
    }
}
