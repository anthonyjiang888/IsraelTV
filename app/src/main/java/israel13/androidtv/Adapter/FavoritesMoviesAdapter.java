package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Fragments.Favorites_Fragment;
import israel13.androidtv.Fragments.VodSubcategoryMovies_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by puspak on 04/07/17.
 */
public class FavoritesMoviesAdapter extends RecyclerView.Adapter<FavoritesMoviesAdapter.Myviewholder>
{

    private ArrayList<SetgetMovies> list;
    private Context context;
    private Favorites_Fragment fav_fag;

    HomeActivity home = null;

    public class Myviewholder extends RecyclerView.ViewHolder
    {


        public TextView name_child_favorites_movies_list,year_child_favorites_movies_list,duration_child_favorites_movies_list;
        public ImageView image_child_favorites_movieslist;
        public FrameLayout totalview_child_favorites_movieslist;
        public RatingBar ratingBar_child_favorites_movieslist;
        public LinearLayout linearlayout_close_favorites_movieslist;


        public Myviewholder(View itemView) {
            super(itemView);


            name_child_favorites_movies_list=(TextView)itemView.findViewById(R.id.name_child_favorites_movies_list);
            year_child_favorites_movies_list=(TextView)itemView.findViewById(R.id.year_child_favorites_movies_list);
            //duration_child_favorites_movies_list=(TextView)itemView.findViewById(R.id.duration_child_favorites_movies_list);
            image_child_favorites_movieslist = (ImageView) itemView.findViewById(R.id.image_child_favorites_movieslist);
            totalview_child_favorites_movieslist = (FrameLayout) itemView.findViewById(R.id.totalview_child_favorites_movieslist);
            ratingBar_child_favorites_movieslist = (RatingBar) itemView.findViewById(R.id.ratingBar_child_favorites_movieslist);
            linearlayout_close_favorites_movieslist = (LinearLayout) itemView.findViewById(R.id.linearlayout_close_favorites_movieslist);

            totalview_child_favorites_movieslist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    //Toast.makeText(context,"on focus change---",Toast.LENGTH_SHORT).show();
                    if (!hasFocus) {
                        name_child_favorites_movies_list.setSelected(false);
                        v.animate().scaleX(1).scaleY(1).setDuration(200);
                    } else {
                        name_child_favorites_movies_list.setSelected(true);
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                    }

                }
            });
        }
    }

    public FavoritesMoviesAdapter(ArrayList<SetgetMovies> list, Context context,Favorites_Fragment fav_fag) {
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
                .inflate(R.layout.child_activity_favorites_movieslist,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

        holder.name_child_favorites_movies_list.setText(list.get(position).getMovies_name());
        holder.year_child_favorites_movies_list.setText(list.get(position).getMovies_year());
        ImageCacheUtil.with(context)
                .load("http:"+list.get(position).getMovies_pic())
                .resize(200,200)
                .cacheUsage(false, true)
                .into(holder.image_child_favorites_movieslist);
        holder.ratingBar_child_favorites_movieslist.setRating(Constant.parseFloat(list.get(position).getMovies_stars()));

        if(Favorites_Fragment.liveChannelsList.size()==0 && Favorites_Fragment.favEpisodesList.size()==0)
        {
            if(position == 0)
            {
                if (fav_fag.isselected == false)
                holder.totalview_child_favorites_movieslist.requestFocus();
            else
                fav_fag.isselected = false;
            }
        }
        if(Favorites_Fragment.flag_delete.equalsIgnoreCase("delete_movie"))
        {
            if(position==0)
            {
                holder.totalview_child_favorites_movieslist.requestFocus();
            }
        }
        if (list.get(position).getIsactive().equalsIgnoreCase("active"))
        {
            holder.linearlayout_close_favorites_movieslist.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.linearlayout_close_favorites_movieslist.setVisibility(View.GONE);
        }
        if(home != null && home.getAvailableAdapterId() == HomeActivity.FavoritesMovies && home.getSelectedPosition(HomeActivity.FavoritesMovies) == position){
            fav_fag.isselected = true;
            holder.totalview_child_favorites_movieslist.requestFocus();
        }
      /*  holder.linearlayout_close_favorites_movieslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((Favorites_Fragment)context).Remove("movie", list.get(position).getMovies_id());

            }
        });
*/
        holder.totalview_child_favorites_movieslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(home != null){
                    home.setAvialableAdapterId(HomeActivity.FavoritesMovies);
                    home.setSelectedPosition(HomeActivity.FavoritesMovies, position);
                }
                if (list.get(position).getIsactive().equalsIgnoreCase("active"))
                {
                    fav_fag.remove("movie", list.get(position).getMovies_id(), position);
                    /*Interface1 in = (Interface1)fav_fag;
                    in.remove("movie", list.get(position).getMovies_id());*/
                }
                else
                {
                    VodSubcategoryMovies_Fragment.vodMovieVideoPlayList.clear();
                    for (int i=0;i<list.size();i++)
                    {
                        VodSubcategoryMovies_Fragment.vodMovieVideoPlayList.add(list.get(i));
                    }

                    VodMovieVideoPlayActivity.isinfav=list.get(position).getMovies_isinfav();
                    VodMovieVideoPlayActivity.position1=position;
                    VodMovieVideoPlayActivity.movie_play_cat=list.get(position).getMovies_category();
                    VodMovieVideoPlayActivity.vodid= list.get(position).getMovies_id();
                    VodMovieVideoPlayActivity.flag_delete = true;
                    context.startActivity(new Intent(context,VodMovieVideoPlayActivity.class));
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