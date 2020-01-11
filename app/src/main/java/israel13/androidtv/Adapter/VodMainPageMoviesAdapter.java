package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Fragments.VodMainPage_Fragment;
import israel13.androidtv.Fragments.VodSubcategoryMovies_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class VodMainPageMoviesAdapter extends RecyclerView.Adapter<VodMainPageMoviesAdapter.Myviewholder> {

	private ArrayList<SetgetMovies> list;
	private Context context;

	HomeActivity home = null;

	public interface VodAdapterCallback {
		void showNetworkAlert();
	}

	public VodAdapterCallback adapterCallback;

	public class Myviewholder extends RecyclerView.ViewHolder {

		public TextView name_home_movies_list, year_home_movies_list, duration_home_movies_list;
		public ImageView image_home_movieslist;
		public RelativeLayout totalview_home_movieslist;
		public RatingBar ratingBar_vod_home_movielist;

		public Myviewholder(View itemView) {
			super(itemView);

			name_home_movies_list = (TextView) itemView.findViewById(R.id.name_home_movies_list);
			year_home_movies_list = (TextView) itemView.findViewById(R.id.year_home_movies_list);
			image_home_movieslist = (ImageView) itemView.findViewById(R.id.image_home_movieslist);
			totalview_home_movieslist = (RelativeLayout) itemView.findViewById(R.id.totalview_home_movieslist);
			ratingBar_vod_home_movielist = (RatingBar) itemView.findViewById(R.id.ratingBar_vod_home_movielist);

			totalview_home_movieslist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						name_home_movies_list.setSelected(false);
						v.animate().scaleX(1).scaleY(1).setDuration(200);
					} else {
						name_home_movies_list.setSelected(true);
						v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
					}
				}
			});
		}
	}

	public VodMainPageMoviesAdapter(ArrayList<SetgetMovies> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (home == null) {
			home = (HomeActivity) parent.getContext();
		}
		View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_activity_home_movieslist, parent, false);
		return new Myviewholder(itemview);
	}

	@Override
	public void onBindViewHolder(Myviewholder holder, final int position) {

		if (home != null && home.getAvailableAdapterId() == HomeActivity.VodMovies) {
			if (position == home.getSelectedPosition(HomeActivity.VodMovies)) {
				holder.totalview_home_movieslist.requestFocus();
			}
		}
		holder.name_home_movies_list.setText(list.get(position).getMovies_name());
		holder.year_home_movies_list.setText(list.get(position).getMovies_year());
		ImageCacheUtil.with(context)
				.load("http:" + list.get(position).getMovies_pic())
				.resize(200,200)
				.cacheUsage(false, true)
				.into(holder.image_home_movieslist);
		holder.ratingBar_vod_home_movielist.setRating(Constant.parseFloat(list.get(position).getMovies_stars()));

		holder.totalview_home_movieslist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (NetworkUtil.checkNetworkAvailable(context)) {
					if (home != null) {
						home.setAvialableAdapterId(HomeActivity.VodMovies);
						home.setSelectedPosition(HomeActivity.VodMovies, position);
					}
					VodSubcategoryMovies_Fragment.vodMovieVideoPlayList.clear();
					for (int i = 0; i < list.size(); i++) {
						VodSubcategoryMovies_Fragment.vodMovieVideoPlayList.add(list.get(i));
					}
					if (position < list.size()) {
						VodMovieVideoPlayActivity.flag_delete = true;
						VodMovieVideoPlayActivity.isinfav = list.get(position).getMovies_isinfav();
						VodMovieVideoPlayActivity.position1 = position;
						VodMovieVideoPlayActivity.movie_play_cat = "מה חדש בסרטים";
						VodMovieVideoPlayActivity.vodid= VodSubcategoryMovies_Fragment.vodMovieVideoPlayList.get(position).getMovies_id();;
						context.startActivity(new Intent(context, VodMovieVideoPlayActivity.class));
					}
				} else {
					if (adapterCallback != null) {
						adapterCallback.showNetworkAlert();
					}
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}