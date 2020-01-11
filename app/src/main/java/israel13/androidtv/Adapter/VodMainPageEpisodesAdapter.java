package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.Tv_show_play_activity;
import israel13.androidtv.CallBacks.TVShowPlayDataUpdate;
import israel13.androidtv.Fragments.VodMainPage_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetEpisodes;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class VodMainPageEpisodesAdapter extends RecyclerView.Adapter<VodMainPageEpisodesAdapter.Myviewholder> {

	private ArrayList<SetgetEpisodes> list;
	private Context context;

	HomeActivity home = null;
	int currentPlayingIndex = -1;
	public interface VodEpisodesAdapterCallback{
		void ShowNetworkAlert();
	}
	public VodEpisodesAdapterCallback mCallBack;
	public class Myviewholder extends RecyclerView.ViewHolder {


		public TextView name_home_episodeslist, season_name_home_episodeslist, tvshowname_name_home_episodeslist;
		public ImageView image_home_episodeslist;
		public RelativeLayout totalview_child_home_episodeslist;


		public Myviewholder(View itemView) {
			super(itemView);


			name_home_episodeslist = (TextView) itemView.findViewById(R.id.name_home_episodeslist);
			season_name_home_episodeslist = (TextView) itemView.findViewById(R.id.season_name_home_episodeslist);
			tvshowname_name_home_episodeslist = (TextView) itemView.findViewById(R.id.tvshowname_name_home_episodeslist);
			image_home_episodeslist = (ImageView) itemView.findViewById(R.id.image_home_episodeslist);
			totalview_child_home_episodeslist = (RelativeLayout) itemView.findViewById(R.id.totalview_child_home_episodeslist);

			totalview_child_home_episodeslist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						v.animate().scaleX(1).scaleY(1).setDuration(200);
						name_home_episodeslist.setSelected(false);
					} else {
						name_home_episodeslist.setSelected(true);
						v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);
					}
				}
			});
		}
	}

	public VodMainPageEpisodesAdapter(ArrayList<SetgetEpisodes> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(home == null){
			home = (HomeActivity) parent.getContext();
		}
		View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_activity_home_episodeslist, parent, false);
		Myviewholder holder = new Myviewholder(itemview);
		return holder;
	}

	@Override
	public void onBindViewHolder(Myviewholder holder, final int position) {

		if(home != null && home.getAvailableAdapterId() == HomeActivity.VodEpisodes){
			if(position == home.getSelectedPosition(HomeActivity.VodEpisodes)){
				holder.totalview_child_home_episodeslist.requestFocus();
			}
		}

		holder.name_home_episodeslist.setText(list.get(position).getEpisode_name());
		ImageCacheUtil.with(context)
				.load("http:" + list.get(position).getEpisodes_pic())
				.resize(200,200)
				.cacheUsage(false, true)
				.placeholder(R.drawable.channel_placeholder)
				.into(holder.image_home_episodeslist);
		holder.season_name_home_episodeslist.setText(list.get(position).getSeason_name());
		holder.tvshowname_name_home_episodeslist.setText(list.get(position).getTvshow_name());

		holder.totalview_child_home_episodeslist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (NetworkUtil.checkNetworkAvailable(context)){
					if(home != null){
						home.setAvialableAdapterId(HomeActivity.VodEpisodes);
						home.setSelectedPosition(HomeActivity.VodEpisodes, position);
					}

					/*  VodSubcategory_Fragment.vod_subcategory_id = list.get(position).getTvshow_id();
					VodSubcategory_Fragment.vod_subcategory_image = list.get(position).getEpisodes_pic();
					VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getTvshow_name();
					VodSubcategory_Fragment.Cat_name="מה חדש בסדרות/תוכניות ועוד";

					VodTvShowVideo_Fragment allvod_tv_show_cat_frag= new VodTvShowVideo_Fragment();
					FragmentTransaction ft1 =((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
					ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					ft1.replace(R.id.fram_container, allvod_tv_show_cat_frag, "layout_tvshow_screen");
					ft1.addToBackStack(null);
					ft1.commit();*/

					setPlayData(position);
					currentPlayingIndex = position;

					VodMainPage_Fragment.callbackDataUpdate = new TVShowPlayDataUpdate() {
						@Override
						public boolean update() {
							currentPlayingIndex ++;
							Log.e("Episodes Adapter", "Current Playing Index: " + currentPlayingIndex);
							if (currentPlayingIndex == list.size()) {
								currentPlayingIndex = 0;
								return false;
							}
							setPlayData(currentPlayingIndex);
							return true;
						}

						@Override
						public boolean isLastEpisode() {
							if (currentPlayingIndex == list.size() - 1) {
								return true;
							}
							return false;
						}

						@Override
						public String[] nextEpisodeSummary() {
							String[] res = new String[2];
							if (!isLastEpisode()) {
								res[0] = list.get(currentPlayingIndex + 1).getEpisode_name();
								res[1] = list.get(currentPlayingIndex + 1).getEpisodes_pic();
								return res;
							}
							return null;
						}
					};
					Intent intent = new Intent(context, Tv_show_play_activity.class);
					context.startActivity(intent);
				} else {
					mCallBack.ShowNetworkAlert();
				}
			}
		});
	}

	private void setPlayData(int position) {
		if (position < list.size()) {
			VodTvShowVideo_Fragment.selecte_epi_id = list.get(position).getEpisode_id();
			VodTvShowVideo_Fragment.selecte_epiyear = list.get(position).getEpisode_year();
			VodTvShowVideo_Fragment.selecte_epi_genre = list.get(position).getEpisode_genre();
			VodTvShowVideo_Fragment.selecte_epi_vodlist = list.get(position).getEpisode_vodlist();
			VodTvShowVideo_Fragment.selecte_epi_name = list.get(position).getEpisode_name();
			if (list.get(position).getEpisode_description().equalsIgnoreCase("null")) {
				VodTvShowVideo_Fragment.selecte_epi_description = "מידע אינו זמין";
			} else
				VodTvShowVideo_Fragment.selecte_epi_description = list.get(position).getEpisode_description();

			VodTvShowVideo_Fragment.selecte_epi_created = list.get(position).getEpisode_created();
			VodTvShowVideo_Fragment.selecte_epi_updated = list.get(position).getEpisode_updated();
			VodTvShowVideo_Fragment.selecte_epi_views = list.get(position).getEpisode_views();
			VodTvShowVideo_Fragment.selecte_epi_length = list.get(position).getEpisode_length();
			VodTvShowVideo_Fragment.selecte_epi_stars = list.get(position).getEpisode_stars();
			VodTvShowVideo_Fragment.selecte_epi_showpic = list.get(position).getEpisodes_pic();
			VodTvShowVideo_Fragment.selecte_epi_year = list.get(position).getEpisode_year();
			VodTvShowVideo_Fragment.season_name = list.get(position).getSeason_name();

			VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getTvshow_name();
		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}