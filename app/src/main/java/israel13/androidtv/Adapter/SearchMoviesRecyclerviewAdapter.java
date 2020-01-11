package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import israel13.androidtv.Fragments.Search_Fragment;
import israel13.androidtv.Fragments.VodSubcategoryMovies_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class SearchMoviesRecyclerviewAdapter extends RecyclerView.Adapter<SearchMoviesRecyclerviewAdapter.Myviewholder>
{
    private ArrayList<SetgetMovies> list;
    private Context context;
    private ArrayList<SetgetMovies> vodMovieVideoPlayList;
    private Search_Fragment search_fragment;

    public class Myviewholder extends RecyclerView.ViewHolder
    {
        public ImageView image_child_new_search_movies;
        TextView movie_name_child_new_search_movies;
        public RelativeLayout totalview_child_new_search_movies;
        public TextView movie_year;
        public RatingBar movie_ratingstar;
        public RelativeLayout relative_lay;
        public Myviewholder(View itemView) {
            super(itemView);

            image_child_new_search_movies = (ImageView) itemView.findViewById(R.id.image_child_new_search_movies);
            movie_name_child_new_search_movies = (TextView) itemView.findViewById(R.id.movie_name_child_new_search_movies);
            totalview_child_new_search_movies = (RelativeLayout) itemView.findViewById(R.id.totalview_child_new_search_movies);
            movie_year = (TextView) itemView.findViewById(R.id.txt_search_movie_year);
            movie_ratingstar = (RatingBar)itemView.findViewById(R.id.ratingBar_search_movie_star);
            relative_lay = (RelativeLayout)itemView.findViewById(R.id.relative_lay);
            totalview_child_new_search_movies.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus)
                    {
                        totalview_child_new_search_movies.animate().scaleX(1).scaleY(1).setDuration(200);
                        totalview_child_new_search_movies.setSelected(false);
                    }
                    else {
                        totalview_child_new_search_movies.setSelected(true);
                        totalview_child_new_search_movies.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
                    }
                }
            });
        }
    }

    public SearchMoviesRecyclerviewAdapter(ArrayList<SetgetMovies> list, Context context,Search_Fragment search_fragment) {
        this.list = list;
        this.context=context;
        this.search_fragment=search_fragment;
    }

    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_activity_new_search_movies,parent,false);
        return new Myviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(Myviewholder holder, final int position) {

        SpannableString text = (search_fragment.SettextColorNew(search_fragment.edit_text_search.getText().toString(),list.get(position).getMovies_name()));
        holder.movie_name_child_new_search_movies.setText(text);

        ImageCacheUtil.with(context)
                .load("http:" + list.get(position).getMovies_pic())
                .resize(200,200)
                .cacheUsage(false, true)
                .placeholder(R.drawable.channel_placeholder)
                .into(holder.image_child_new_search_movies);
        holder.movie_year.setText(list.get(position).getMovies_year());
        holder.movie_ratingstar.setRating(Constant.parseFloat(list.get(position).getMovies_stars()));

       // holder.movie_name_child_new_search_movies.setText(list.get(position).getMovies_name());
        if (position == 0 && search_fragment.SelectedNumber == 3) {
            holder.totalview_child_new_search_movies.requestFocus();
        }
        holder.totalview_child_new_search_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VodMovieVideoPlayActivity.flag_delete = true;
                VodSubcategoryMovies_Fragment.vodMovieVideoPlayList=new ArrayList<SetgetMovies>();
                for (int i=0;i<list.size();i++)
                {
                    VodSubcategoryMovies_Fragment.vodMovieVideoPlayList.add(list.get(i));
                }
                Search_Fragment.flag_page_change=1;
                VodMovieVideoPlayActivity.isinfav=list.get(position).getMovies_isinfav();
                VodMovieVideoPlayActivity.position1=position;
                VodMovieVideoPlayActivity.movie_play_cat="חיפוש";
                VodMovieVideoPlayActivity.vodid= list.get(position).getMovies_id();
                VodMovieVideoPlayActivity.flag_delete = true;
                context.startActivity(new Intent(context,VodMovieVideoPlayActivity.class));
            }
        });


//        int selectedPos = ((HomeActivity)context).getSelectedPosition(HomeActivity.VodMovies);
//        if (selectedPos > -1 && position == selectedPos) {
//            Log.e("test", "selected position: " + selectedPos);
//            holder.totalview_child_new_search_movies.requestFocus();
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}