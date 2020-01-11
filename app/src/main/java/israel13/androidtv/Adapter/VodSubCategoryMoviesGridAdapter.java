package israel13.androidtv.Adapter;

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
import israel13.androidtv.Setter_Getter.SetgetMovies;

/**
 * Created by sudipta on 24/10/16.
 */
public class VodSubCategoryMoviesGridAdapter extends ArrayAdapter {


    ArrayList<SetgetMovies> list;
    Context mContext;
    int resourceID;
    AllchannelRecyclerviewAdapter adapter;

    @SuppressWarnings("unchecked")
    public VodSubCategoryMoviesGridAdapter(Context mContext, int resourceID, ArrayList<SetgetMovies> list) {
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


            holder.movie_name_child_vod_subcategory_movies=(TextView)view.findViewById(R.id.movie_name_child_vod_subcategory_movies);
            holder.image_child_vod_subcategory_movies = (ImageView) view.findViewById(R.id.image_child_vod_subcategory_movies);
            holder.totalview_child_vod_subcategory_movies = (RelativeLayout) view.findViewById(R.id.totalview_child_vod_subcategory_movies);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.movie_name_child_vod_subcategory_movies.setText(list.get(position).getMovies_name());
        Glide.with(mContext)
                .load("http:" + list.get(position).getMovies_pic())
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .override(200, 200).skipMemoryCache(true)
                .into(holder.image_child_vod_subcategory_movies);



    /*    holder.totalview_child_vod_subcategory_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VodMovieVideoPlayActivity.position1 = position;
                VodMovieVideoPlayActivity.isinfav = list.get(position).getMovies_isinfav();
                VodMovieVideoPlayActivity.movie_play_cat=VodSubcategoryMovies_Fragment.Cat_name_movie;
                mContext.startActivity(new Intent(mContext,VodMovieVideoPlayActivity.class));

            }
        });*/


        return view;

    }


    //********* Create a holder Class to contain inflated xml file elements *********//*
    public static class ViewHolder {
        public TextView movie_name_child_vod_subcategory_movies;
        public ImageView image_child_vod_subcategory_movies;
        public RelativeLayout totalview_child_vod_subcategory_movies;
    }

}