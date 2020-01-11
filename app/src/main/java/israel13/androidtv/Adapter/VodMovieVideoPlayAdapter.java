package israel13.androidtv.Adapter;

import android.content.Context;
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

import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class VodMovieVideoPlayAdapter extends RecyclerView.Adapter<VodMovieVideoPlayAdapter.Myviewholder>
{

    private ArrayList<SetgetMovies> list;
    private Context context;



    public class Myviewholder extends RecyclerView.ViewHolder
    {


        public TextView name_vod_movie_videoplay;
        public ImageView image_vod_movie_videoplay;
        public RelativeLayout totalview_child_vod_movie_videoplay;


        public Myviewholder(View itemView) {
            super(itemView);


            name_vod_movie_videoplay=(TextView)itemView.findViewById(R.id.name_vod_movie_videoplay);
            image_vod_movie_videoplay = (ImageView) itemView.findViewById(R.id.image_vod_movie_videoplay);
            totalview_child_vod_movie_videoplay = (RelativeLayout) itemView.findViewById(R.id.totalview_child_vod_movie_videoplay);


        }
    }

    public VodMovieVideoPlayAdapter(ArrayList<SetgetMovies> list, Context context) {
        this.list = list;
        this.context=context;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_activity_vod_movie_videoplay,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

        holder.name_vod_movie_videoplay.setText(list.get(position).getMovies_name());

        ImageCacheUtil.with(context)
                .load("http:" + list.get(position).getMovies_pic())
                .resize(200,200)
                .cacheUsage(false, true)
                .into(holder.image_vod_movie_videoplay);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}