package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LiveChannels_Play_Activity;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.Favorites_Fragment;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;

/**
 * Created by jeeban on 07/06/16.
 */
public class FavoritesLiveChannelAdapter extends RecyclerView.Adapter<FavoritesLiveChannelAdapter.Myviewholder> {

	private ArrayList<SetgetSubchannels> list;
	private Context context;
	private Favorites_Fragment fav_fag;
	String time;
	public interface FirstFocusInterface{
		void onfirstFocusLive();
	}
	public FirstFocusInterface onFavoritefirstItemFocus;
	HomeActivity home = null;
	public class Myviewholder extends RecyclerView.ViewHolder {


		public TextView name_child_favorites_live_channles, show_time_fav, show_name_fav;
		public RoundedImageView image_child_favorites_live_channles;
		public FrameLayout total_layout_favorites_live_channles;
		public LinearLayout linearlayout_close_favorites_livechannels, text_channel_fav;
		public RelativeLayout relative_lay;

		public Myviewholder(View itemView) {
			super(itemView);


			image_child_favorites_live_channles = (RoundedImageView) itemView.findViewById(R.id.image_child_favorites_live_channles);
			total_layout_favorites_live_channles = (FrameLayout) itemView.findViewById(R.id.total_layout_favorites_live_channles);
			linearlayout_close_favorites_livechannels = (LinearLayout) itemView.findViewById(R.id.linearlayout_close_favorites_livechannels);
			show_time_fav = (TextView) itemView.findViewById(R.id.show_time_fav);
			show_name_fav = (TextView) itemView.findViewById(R.id.show_name_fav);
			text_channel_fav = (LinearLayout) itemView.findViewById(R.id.text_channel_fav);
			relative_lay = (RelativeLayout) itemView.findViewById(R.id.relative_lay);

			total_layout_favorites_live_channles.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					//Toast.makeText(context,"on focus change---",Toast.LENGTH_SHORT).show();
					if (!hasFocus) {
						total_layout_favorites_live_channles.animate().scaleX(1).scaleY(1).setDuration(200);
						show_name_fav.setSelected(false);
					} else {
						show_name_fav.setSelected(true);
						total_layout_favorites_live_channles.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
					}

				}
			});


		}
	}

	public FavoritesLiveChannelAdapter(ArrayList<SetgetSubchannels> list, Context context, Favorites_Fragment fav_fag) {
		this.list = list;
		this.context = context;
		this.fav_fag = fav_fag;
	}

	@Override
	public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(home == null){
			home = (HomeActivity) parent.getContext();
		}
		View itemview = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.child_activity_favorites_live_channels, parent, false);
		return new Myviewholder(itemview);
	}

	@Override
	public void onBindViewHolder(Myviewholder holder, final int position) {

		Transformation transformation = new RoundedTransformationBuilder()
				.borderColor(R.color.colorPrimary)
				.borderWidthDp(2)
				.cornerRadiusDp(20)
				.oval(false)
				.build();


		ImageCacheUtil.with(context)
				.load(Constant.BASE_URL_IMAGE + list.get(position).getImage())
				.placeholder(R.drawable.channel_placeholder)
				.resize(200,200)
				.cacheUsage(false, true)
				.into(holder.image_child_favorites_live_channles);


		try {
			time = Constant.getTimeByAdding((Constant.parseInt(list.get(position).getEpg_length()))
					, list.get(position).getEpg_time());
		} catch (NumberFormatException e) {
			System.out.println("--------position" + list.get(position).getEpg_length());
		}

		String strName = list.get(position).getRunning_epg_name();
		String year = list.get(position).getYear();
		if (year != null && !year.trim().isEmpty()) {
			strName += " (" + year + ")";
		}
		holder.show_name_fav.setText(strName);

		holder.show_time_fav.setText(list.get(position).getEpg_time() + " - " + time);
		// holder.show_name_fav.setSelected(true);

		if (position == 0) {
			if (fav_fag.isselected == false) {
				if (onFavoritefirstItemFocus != null)
					onFavoritefirstItemFocus.onfirstFocusLive();
				holder.total_layout_favorites_live_channles.requestFocus();

			}
			else
				fav_fag.isselected = false;
		}

		if (list.get(position).getIsactive().equalsIgnoreCase("active")) {
			holder.linearlayout_close_favorites_livechannels.setVisibility(View.VISIBLE);
		} else {
			holder.linearlayout_close_favorites_livechannels.setVisibility(View.GONE);
		}

		if(home != null && home.getAvailableAdapterId() == HomeActivity.FavoritesLiveChannel && home.getSelectedPosition(HomeActivity.FavoritesLiveChannel) == position){
			fav_fag.isselected = true;
			holder.total_layout_favorites_live_channles.requestFocus();
		}



		holder.total_layout_favorites_live_channles.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(home != null){
					home.setAvialableAdapterId(HomeActivity.FavoritesLiveChannel);
					home.setSelectedPosition(HomeActivity.FavoritesLiveChannel, position);
				}
				if (list.get(position).getIsactive().equalsIgnoreCase("active")) {

					// Interface1 in = (Interface1)fav_fag;
					fav_fag.remove("live", list.get(position).getSub_channelsid(), position);

				} else {
					AllChannels_Fragment.channel_id = list.get(position).getSub_channelsid();

					Intent in = new Intent(context, LiveChannels_Play_Activity.class);
					in.putExtra(LiveChannels_Play_Activity.CHANNEL_ID, list.get(position).getSub_channelsid());
					in.putExtra(LiveChannels_Play_Activity.MAIN_CH_ID, list.get(position).getChid());
					in.putExtra(LiveChannels_Play_Activity.CH_ICON, list.get(position).getImage());
					context.startActivity(in);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}