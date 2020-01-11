package israel13.androidtv.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Tv_show_play_activity;
import israel13.androidtv.Adapter.Season_list_adapter;
import israel13.androidtv.Adapter.VodTvshowPlayRecyclerAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetTvShowSeason;
import israel13.androidtv.Setter_Getter.SetgetVodTvShowVideoPlay;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomGridView;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.RtlGridLayoutManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by krishanu on 02/06/17.
 */
public class VodTvShowVideo_Fragment extends Fragment implements IJSONParseListener {
	int position = -1;
	ImageView channel_icon_vod_tvshow_videoplay;
	ImageView up_arrow_show, down_arrow_show;
	TextView channelname_vod_tvshow_videoplay, description_vod_tvshow_videoplay, genre_vod_tvshow_videoplay, tv_year_tvshow, main_cat_name, add_fav_episode_text;
	RatingBar ratingBar_vod_tvshow_videoplay;
	ListView season_vod_tvshow_videoplay;
	CustomGridView gridview_tv_show;
	LinearLayout linearlayout_vod_movie_videoplay_close;
	ImageView imgButSeasonListUp, imgButSeasonListDown;
	View tempView = null;
	View temp_tvshow_videoplayView = null;
	int GLOBAL_COUNTER = 0;
	int VOD_TVSHOW_VIDEOPLAY = 201;
	int ADD_FAVORITES = 213;
	int REMOVE_FAVORITES = 214;
	int VOD_TVSHOW_VIDEOPLAY_PAGE = 212;
	int TVSHOW_PLAY = 211;

	ProgressDialog pDialog;
	SharedPreferences logindetails;
	StringBuilder videoplayurl;
	ArrayList<SetgetVodTvShowVideoPlay> tvShowVideoPlayList;
	ArrayList<SetgetTvShowSeason> tvShowSeasonsList;
	ArrayList<String> seasonsList;
	String season_number = "";
	String vodid = "";
	int check = 0;
	Season_list_adapter adapter;
	VodTvshowPlayRecyclerAdapter vodTvShowVideoPlayAdapter;
	String fav_flag = "";
	Button btn_episodelist_up_down;
	public int flag_episode_list = 0;
	LinearLayout llay_holder_recycler;
	int linear_width = 0;

	public static String selecte_epi_id, selecte_epiyear, selecte_epi_genre, selecte_epi_vodlist, selecte_epi_name,
			selecte_epi_description,
			selecte_epi_created,
			selecte_epi_updated,
			selecte_epi_views,
			selecte_epi_length,
			selecte_epi_stars, selecte_epi_showpic, selecte_epi_year, season_name;
	LinearLayout add_to_fav_tvshow;
	ImageView add_To_fav_img_episode;
	boolean once = true;
	String currentSubID = "";
	boolean isFirstEvent = false;
	int season_list_selected_item_id = 0;
	int horizontalItemCount = 0;
	private boolean mBGetData = false;
	private View mFocusedView = null;
	private boolean bPageLoading = false;
	private int nCurPageNo = 0;
	private int nTotalPageCnt = 0;

	private HomeActivity mHome;
	private boolean bFragmentCreated = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		tvShowVideoPlayList = new ArrayList<>();
		tvShowSeasonsList = new ArrayList<>();
		vodTvShowVideoPlayAdapter = new VodTvshowPlayRecyclerAdapter(tvShowVideoPlayList, getActivity());
		super.onCreate(savedInstanceState);
		Tv_show_play_activity.isSortDown = false;
		currentSubID = VodSubcategory_Fragment.vod_subcategory_id;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.tv_show_screen, null);

		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);

		//  playerView_vod_tvshow_videoplay = (JWPlayerView) findViewById(R.id.playerView_vod_tvshow_videoplay);
		add_to_fav_tvshow = (LinearLayout) layout.findViewById(R.id.add_to_fav_tvshow);
		main_cat_name = (TextView) layout.findViewById(R.id.main_cat_name);
		channel_icon_vod_tvshow_videoplay = (ImageView) layout.findViewById(R.id.channel_icon_vod_tvshow_videoplay);
		up_arrow_show = (ImageView) layout.findViewById(R.id.up_arrow_show);
		down_arrow_show = (ImageView) layout.findViewById(R.id.down_arrow_show);
		channelname_vod_tvshow_videoplay = (TextView) layout.findViewById(R.id.channelname_vod_tvshow_videoplay);
		description_vod_tvshow_videoplay = (TextView) layout.findViewById(R.id.description_vod_tvshow_videoplay);
		genre_vod_tvshow_videoplay = (TextView) layout.findViewById(R.id.genre_vod_tvshow_videoplay);
		add_fav_episode_text = (TextView) layout.findViewById(R.id.add_fav_episode_text);
		ratingBar_vod_tvshow_videoplay = (RatingBar) layout.findViewById(R.id.ratingBar_vod_tvshow_videoplay);
		season_vod_tvshow_videoplay = (ListView) layout.findViewById(R.id.Select_season);
		gridview_tv_show = (CustomGridView) layout.findViewById(R.id.gridview_tv_show);
		btn_episodelist_up_down = (Button) layout.findViewById(R.id.btn_episodelist_up_down);
		imgButSeasonListUp = (ImageView)layout.findViewById(R.id.img_tvshowlist_uparrow_channels);
		imgButSeasonListDown = (ImageView)layout.findViewById(R.id.img_tvshowlist_downarrow_channels);

		llay_holder_recycler = (LinearLayout) layout.findViewById(R.id.llay_holder_recycler);

		add_To_fav_img_episode = (ImageView) layout.findViewById(R.id.add_To_fav_img_episode);
		gridview_tv_show.setAdapter(vodTvShowVideoPlayAdapter);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			gridview_tv_show.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		else
			gridview_tv_show.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

		layout.findViewById(R.id.back_tv_show).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
				}
			}
		});
		layout.findViewById(R.id.back_tv_show).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity homeActivity = getActivity();
				homeActivity.getSupportFragmentManager().popBackStack();
			}
		});

		ratingBar_vod_tvshow_videoplay.setClickable(false);

		mHome = (HomeActivity) getContext();
		mHome.setSelectedPosition(HomeActivity.VodTvshowPlayRecycler, 0);

		seasonsList = new ArrayList<>();
		tvShowSeasonsList = new ArrayList<>();

		String str = VodSubcategory_Fragment.Cat_name;
		String str1 = str.replace('|', '>');

		main_cat_name.setText(str1);

		ImageCacheUtil.with(getActivity())
				.load("http:" + VodSubcategory_Fragment.vod_subcategory_image)
				.resize(200,200)
				.cacheUsage(false, true)
				.into(channel_icon_vod_tvshow_videoplay);

		channelname_vod_tvshow_videoplay.setText(VodSubcategory_Fragment.vod_subcategory_name);

		if (VodSubcategory_Fragment.is_infav.equalsIgnoreCase("1")) {
			add_fav_episode_text.setText("הסר ממועדפים");
			add_To_fav_img_episode.setImageResource(R.drawable.remove_fav);
			fav_flag = "remove_fav";
		} else {
			add_fav_episode_text.setText("הוסף למועדפים");
			add_To_fav_img_episode.setImageResource(R.drawable.add_fav);
			fav_flag = "add_fav";
		}

		Call_api();

		season_vod_tvshow_videoplay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (season_vod_tvshow_videoplay.getChildCount() == 0)
					return;

				isFirstEvent = true;
				season_vod_tvshow_videoplay.setItemChecked(0, true);
				if (season_vod_tvshow_videoplay.getSelectedView() != null)
					season_vod_tvshow_videoplay.getSelectedView().findViewById(R.id.season_name).setSelected(false);
				season_vod_tvshow_videoplay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		season_vod_tvshow_videoplay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT)
					return;
				if (!hasFocus) {
					if (season_vod_tvshow_videoplay.getSelectedView() != null)
						season_vod_tvshow_videoplay.getSelectedView().findViewById(R.id.season_name).setSelected(false);
				}else{
					if (season_vod_tvshow_videoplay.getSelectedView() != null)
						season_vod_tvshow_videoplay.getSelectedView().findViewById(R.id.season_name).setSelected(true);
				}
				if (!hasFocus){

					if (temp_tvshow_videoplayView != null){
						temp_tvshow_videoplayView.animate().scaleX(1).scaleY(1).setDuration(200);
						temp_tvshow_videoplayView = null;
					}
				} else{
					if (season_vod_tvshow_videoplay.getSelectedView() != null ) {
						season_vod_tvshow_videoplay.getSelectedView().animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
						temp_tvshow_videoplayView = season_vod_tvshow_videoplay.getSelectedView();
					}

				}
			}
		});


		season_vod_tvshow_videoplay.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int myFirstVisibleItem = firstVisibleItem;
				if (view.getChildCount() != 0) {
					myFirstVisibleItem += -(view.getChildAt(0).getTop() - 10) / view.getChildAt(0).getHeight();
					if (myFirstVisibleItem != 0) {
						imgButSeasonListUp.setVisibility(View.VISIBLE);
					} else {
						imgButSeasonListUp.setVisibility(View.INVISIBLE);
					}

					if (view.getLastVisiblePosition() == totalItemCount - 1 &&
							view.getChildAt(view.getChildCount() - 1).getBottom() < view.getHeight() + 10) {
						imgButSeasonListDown.setVisibility(View.INVISIBLE);
					}else{
						imgButSeasonListDown.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		season_vod_tvshow_videoplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				season_vod_tvshow_videoplay.smoothScrollToPosition(position);
				view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
				if (temp_tvshow_videoplayView != null && temp_tvshow_videoplayView != view) {
					temp_tvshow_videoplayView.animate().scaleY(1).scaleX(1).setDuration(200);
				}
				temp_tvshow_videoplayView = view;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		season_vod_tvshow_videoplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				once = true;
				adapter.setSelectedIndex(position);
				season_name = seasonsList.get(position);
				VodSubcategory_Fragment.vod_subcategory_id = tvShowSeasonsList.get(position).getSeason_id();
				nCurPageNo = 0;
				mHome.setSelectedPosition(HomeActivity.VodTvshowPlayRecycler, 0);
				Call_api();
				description_vod_tvshow_videoplay.setText(tvShowSeasonsList.get(Integer.valueOf(position)).getDescription());
				ratingBar_vod_tvshow_videoplay.setRating(Constant.parseFloat(tvShowSeasonsList.get(Integer.valueOf(position)).getStars()));
				genre_vod_tvshow_videoplay.setText("ז'אנר" + ": " + tvShowSeasonsList.get(position).getGenre());
				mBGetData = false;

			}
		});

		gridview_tv_show.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
				if (totalItemCount == 0) {
					return;
				}
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					view.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (gridview_tv_show.getLastVisiblePosition() == totalItemCount - 1) {
								down_arrow_show.setVisibility(View.INVISIBLE);
							}
						}
					}, 100);
				} else {
					down_arrow_show.setVisibility(View.VISIBLE);
				}
				if (firstVisibleItem != 0) {
					up_arrow_show.setVisibility(View.VISIBLE);
				} else {
					view.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (gridview_tv_show.getFirstVisiblePosition() == 0) {
								up_arrow_show.setVisibility(View.INVISIBLE);
							}
						}
					}, 100);
				}
				if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
					//Log.e("...", "Last Item Wow !");
					//Do pagination.. i.e. fetch new data
					if (!bPageLoading && nCurPageNo < nTotalPageCnt) {
						bPageLoading = true;
						GetTvShowListPage(nCurPageNo + 1);
					}
				}
			}
		});

		gridview_tv_show.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					if (keycode == KeyEvent.KEYCODE_DPAD_LEFT)
						if (position - 1 >= 0)
							if (tvShowVideoPlayList.get(position - 1).getName().equals("EmptyEmpty"))
								return true;
					if (keycode == KeyEvent.KEYCODE_DPAD_DOWN) {
						if (tvShowVideoPlayList.size() > position + 4) {
							if (tvShowVideoPlayList.get(position + 4).getName().equals("EmptyEmpty")) {
								return false;
							}
						} else {
							if (tvShowVideoPlayList.size() == position + 1)
								return true;
						}
					}
				}
				return  false;
			}
		});
		gridview_tv_show.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (tvShowVideoPlayList.get(i).getName().equals("EmptyEmpty")) {
						gridview_tv_show.setSelection(tvShowVideoPlayList.size() - 1);
						return;
				}
				if (tempView != null){

					tempView.animate().scaleX(1).scaleY(1).setDuration(200);
				}
				view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);

				tempView = view;
				position = i;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		gridview_tv_show.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					if (tempView != null)
						tempView.animate().scaleX(1).scaleY(1).setDuration(200);
					tempView = null;
				}else{
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
						if (gridview_tv_show.getFirstVisiblePosition() != 0)
							gridview_tv_show.setSelection(gridview_tv_show.getFirstVisiblePosition());
						else if (gridview_tv_show.pointToPosition(100, 100) == -1 || gridview_tv_show.pointToPosition(100, 100) == 3)
							gridview_tv_show.setSelection(gridview_tv_show.getFirstVisiblePosition());
						else
							gridview_tv_show.setSelection(gridview_tv_show.getFirstVisiblePosition() + 4);
					}
					if (gridview_tv_show.getSelectedView() != null){
						gridview_tv_show.getSelectedView().animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
						tempView = gridview_tv_show.getSelectedView();
					}
				}
			}

		});
		gridview_tv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tv_show_play_activity.flag_delete = true;
				((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.VodTvshowPlayRecycler, position);
				mHome.FlagVodTVShow = true;
				if (tvShowVideoPlayList != null) {

					VodTvShowVideo_Fragment.selecte_epi_id = tvShowVideoPlayList.get(position).getTvshow_id();
					VodTvShowVideo_Fragment.selecte_epiyear = tvShowVideoPlayList.get(position).getYear();
					VodTvShowVideo_Fragment.selecte_epi_genre = tvShowVideoPlayList.get(position).getGenre();
					VodTvShowVideo_Fragment.selecte_epi_vodlist = tvShowVideoPlayList.get(position).getVodlist();
					VodTvShowVideo_Fragment.selecte_epi_name = tvShowVideoPlayList.get(position).getName();
					VodTvShowVideo_Fragment.selecte_epi_description = tvShowVideoPlayList.get(position).getDescription();
					VodTvShowVideo_Fragment.selecte_epi_created = tvShowVideoPlayList.get(position).getCreated();
					VodTvShowVideo_Fragment.selecte_epi_updated = tvShowVideoPlayList.get(position).getUpdated();
					VodTvShowVideo_Fragment.selecte_epi_views = tvShowVideoPlayList.get(position).getViews();
					VodTvShowVideo_Fragment.selecte_epi_length = tvShowVideoPlayList.get(position).getLength();
					VodTvShowVideo_Fragment.selecte_epi_stars = tvShowVideoPlayList.get(position).getStars();
					VodTvShowVideo_Fragment.selecte_epi_showpic = tvShowVideoPlayList.get(position).getShowpic();
					VodTvShowVideo_Fragment.selecte_epi_year = tvShowVideoPlayList.get(position).getYear();
					startActivity(new Intent(getActivity(), Tv_show_play_activity.class));
				}
			}
		});
		btn_episodelist_up_down.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					btn_episodelist_up_down.animate().scaleX(1).scaleY(1).setDuration(200);
				}else {
					btn_episodelist_up_down.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
					}
			}
		});
		btn_episodelist_up_down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((tvShowVideoPlayList == null) || (tvShowVideoPlayList.size() == 0))
					return;
				if (flag_episode_list == 0) {
					btn_episodelist_up_down.setText("מהישן לחדש");
					btn_episodelist_up_down.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down_arrow_espisode, 0, 0, 0);
					flag_episode_list = 1;
					Tv_show_play_activity.isSortDown = true;
				} else {
					btn_episodelist_up_down.setText("מהחדש לישן");
					btn_episodelist_up_down.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up_arrow_espisode, 0, 0, 0);
					flag_episode_list = 0;
					Tv_show_play_activity.isSortDown = false;
				}
				mHome.setSelectedPosition(HomeActivity.VodTvshowPlayRecycler, 0);
				nCurPageNo = 0;
				GetTvShowList(String.valueOf(nCurPageNo+1));
			}
		});
		add_to_fav_tvshow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
				}
			}
		});
		add_to_fav_tvshow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!VodSubcategory_Fragment.vod_subcategory_id.equalsIgnoreCase("")) {
					if (fav_flag.equalsIgnoreCase("add_fav")) {
						AddFavorites();

					} else if (fav_flag.equalsIgnoreCase("remove_fav")) {
						RemoveFavorites();

					}
				} else {
					Toast.makeText(getActivity(), "בחר תוכנית טלוויזיה", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return layout;
	}



	@Override
	public void onResume() {

		if (!bFragmentCreated) {
			getView().setFocusableInTouchMode(false);
			getView().requestFocus();
			bFragmentCreated = true;
		}
		super.onResume();
	}

	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {
			tvShowVideoPlayList.clear();
			vodTvShowVideoPlayAdapter.notifyDataSetChanged();
			GetTvShowList("1");
		} else {
			ShowErrorAlert(getActivity(), "Please check your network connection..");
		}
	}

	public void ShowErrorAlert(final Context c, String text) {
		android.app.AlertDialog.Builder alertDialogBuilder;
		ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
		alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);

		alertDialogBuilder.setTitle("Alert!");
		alertDialogBuilder.setMessage(text);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Call_api();
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		android.app.AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();

	}

	public int pxToDp(int px) {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, displayMetrics);
		return dp;
	}

	void AddFavorites() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("stfor", "3");
		parms.putString("act", "1");
		parms.putString("ch", "");
		parms.putString("datetime", "");
		parms.putString("vodid", currentSubID);
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", ADD_FAVORITES, this, parms, false);
	}

	void RemoveFavorites() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("stfor", "3");
		parms.putString("act", "11");
		parms.putString("ch", "");
		parms.putString("datetime", "");
		parms.putString("vodid", currentSubID);
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", REMOVE_FAVORITES, this, parms, false);
	}

	void GetTvShowList(String page) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("id", VodSubcategory_Fragment.vod_subcategory_id);
		parms.putString("gs", "1");
		parms.putString("page", page);
		parms.putString("mo", "20");
		parms.putString("pagesize", String.valueOf(Constant.RESPONSE_DATACOUNT));
		if (flag_episode_list == 1) {
			parms.putString("sort", "1");
		}
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatelist.php", VOD_TVSHOW_VIDEOPLAY, this, parms, false);
	}

	void GetTvShowListPage(int page) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("id", VodSubcategory_Fragment.vod_subcategory_id);
		parms.putString("gs", "1");
		parms.putString("mo", "20");
		parms.putString("page", String.valueOf(page));
		parms.putString("pagesize", String.valueOf(Constant.RESPONSE_DATACOUNT));
		if (flag_episode_list == 1) {
			parms.putString("sort", "1");
		}
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatelist.php", VOD_TVSHOW_VIDEOPLAY_PAGE, this, parms, false);
	}


	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());
		if (requestCode == VOD_TVSHOW_VIDEOPLAY_PAGE) {
			bPageLoading = false;
		}
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {


//		DismissProgress(getActivity());
		if (requestCode == VOD_TVSHOW_VIDEOPLAY) {
			System.out.println("Vod Tvshow Videoplay List response ----------------" + response.toString());
			tvShowVideoPlayList.clear();
			try {
				JSONObject results = response.getJSONObject("results");
				JSONObject subshows = results.getJSONObject("subshows");

				nTotalPageCnt = subshows.getInt("totp");
				nCurPageNo = 1;

				JSONArray vodms = subshows.getJSONArray("vodms");

				for (int i = 0; i < vodms.length(); i++) {
					try {
						JSONObject object = vodms.getJSONObject(i);
						SetgetVodTvShowVideoPlay videoPlay = new SetgetVodTvShowVideoPlay();
						videoPlay.setTvshow_id(object.getString("id"));
						videoPlay.setYear(object.optString("year", "מידע אינו זמין"));
						videoPlay.setGenre(object.optString("genre", "מידע אינו זמין"));
						videoPlay.setDescription(object.optString("description", "מידע אינו זמין"));
						videoPlay.setCreated(object.optString("created", "מידע אינו זמין"));
						videoPlay.setVodlist(object.getString("vodlist"));
						videoPlay.setName(object.getString("name"));
						videoPlay.setUpdated(object.getString("updated"));
						videoPlay.setViews(object.optString("views", "מידע אינו זמין"));
						videoPlay.setLength(object.optString("length", "0"));
						videoPlay.setStars(object.getString("stars"));
						videoPlay.setShowpic(object.getString("showpic"));
						videoPlay.setIsplaying("false");
						videoPlay.setCounter(String.valueOf(GLOBAL_COUNTER));
						GLOBAL_COUNTER++;
						tvShowVideoPlayList.add(videoPlay);
					} catch (JSONException e) {}
				}


					if (tvShowSeasonsList.size() == 0) {
					JSONObject checksea = results.getJSONObject("checksea");
					JSONArray sons = checksea.getJSONArray("sons");

					for (int i = 0; i < sons.length(); i++) {
						try {
							JSONObject obj = sons.getJSONObject(i);
							SetgetTvShowSeason season = new SetgetTvShowSeason();
							season.setSeason_id(obj.getString("id"));
							season.setViews(obj.optString("views", "0"));
							season.setStars(obj.optString("stars", "0"));
							season.setName(obj.getString("name"));
							season.setDescription(obj.getString("description"));
							try {
								if (obj.getString("genre").equalsIgnoreCase("")) {
									season.setGenre("מידע אינו זמין");
								} else season.setGenre(obj.getString("genre"));
							} catch (Exception e) {
								season.setGenre("מידע אינו זמין");
							}
							season.setYear(obj.optString("year", ""));
							JSONArray picture = obj.getJSONArray("picture");
							JSONObject object = picture.getJSONObject(0);
							season.setShowpic(object.getString("small"));
							tvShowSeasonsList.add(season);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					for (int i = 0; i < tvShowSeasonsList.size(); i++) {
						seasonsList.add(tvShowSeasonsList.get(i).getName());
//						seasonsList.add("עונה " + i);
					}
					description_vod_tvshow_videoplay.setText(tvShowSeasonsList.get(0).getDescription());
					genre_vod_tvshow_videoplay.setText("ז'אנר" + ": " + tvShowSeasonsList.get(0).getGenre());
					//  tv_year_tvshow.setText(tvShowSeasonsList.get(0).getYear());
					ratingBar_vod_tvshow_videoplay.setRating(Constant.parseFloat(tvShowSeasonsList.get(0).getStars()));
					// ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_, seasonsList);
					// ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(), R.layout.season_list_child, seasonsList);
					adapter = new Season_list_adapter(getActivity(), R.layout.season_list_child, seasonsList, false);
					season_vod_tvshow_videoplay.setAdapter(adapter);
					adapter.setSelectedIndex(0);
					season_name = seasonsList.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				int temp = tvShowVideoPlayList.size();
				SetgetVodTvShowVideoPlay video = new SetgetVodTvShowVideoPlay();
				video.setName("EmptyEmpty");

				for (int k = 0; k < (4 - temp % 4) % 4; k++)
					tvShowVideoPlayList.add( tvShowVideoPlayList.size(), video);
				ArrayList<SetgetVodTvShowVideoPlay> tvShowvideotemp = new ArrayList<>();
				for (int i = 0; i < tvShowVideoPlayList.size() / 4; i ++) {

					tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4 + 3));
					tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4 + 2));
					tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4 + 1));
					tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4));
				}
				tvShowVideoPlayList.clear();
				for (int i = 0; i < tvShowvideotemp.size(); i ++)
					tvShowVideoPlayList.add(tvShowvideotemp.get(i));
			}
			vodTvShowVideoPlayAdapter.notifyDataSetChanged();
			if (tvShowVideoPlayList.size() > 0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
					gridview_tv_show.setSelection(3);
				else
					gridview_tv_show.setSelection(0);
			}
			gridview_tv_show.requestFocus();
			if(tvShowSeasonsList != null && tvShowSeasonsList.size() == 0) {
				gridview_tv_show.setNextFocusRightId(R.id.add_to_fav_tvshow);
			}
			DismissProgress(getActivity());
		} else if (requestCode == VOD_TVSHOW_VIDEOPLAY_PAGE) {
			System.out.println("Vod Tvshow Videoplay Page List response ----------------" + response.toString());
			try {
				JSONObject results = response.getJSONObject("results");
				JSONObject subshows = results.getJSONObject("subshows");
				JSONArray vodms = subshows.getJSONArray("vodms");
				if (vodms.length() > 0) nCurPageNo ++;

				for (int i = 0; i < vodms.length(); i++) {
					try {
						JSONObject object = vodms.getJSONObject(i);
						SetgetVodTvShowVideoPlay videoPlay = new SetgetVodTvShowVideoPlay();
						videoPlay.setTvshow_id(object.getString("id"));
						videoPlay.setYear(object.getString("year"));
						videoPlay.setGenre(object.getString("genre"));
						videoPlay.setVodlist(object.getString("vodlist"));
						videoPlay.setName(object.getString("name"));
						videoPlay.setDescription(object.getString("description"));
						videoPlay.setCreated(object.getString("created"));
						videoPlay.setUpdated(object.getString("updated"));
						try {
							videoPlay.setViews(object.getString("views"));
						} catch (JSONException e) {
							videoPlay.setViews("");
						}
						try {
							videoPlay.setLength(object.getString("length"));
						} catch (JSONException e) {
							videoPlay.setLength("0");
						}
						videoPlay.setStars(object.getString("stars"));
						videoPlay.setShowpic(object.getString("showpic"));
						videoPlay.setIsplaying("false");
						videoPlay.setCounter(String.valueOf(GLOBAL_COUNTER));
						GLOBAL_COUNTER++;
						tvShowVideoPlayList.add(videoPlay);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}


				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					int temp = tvShowVideoPlayList.size();
					SetgetVodTvShowVideoPlay video = new SetgetVodTvShowVideoPlay();
					video.setName("EmptyEmpty");

					for (int k = 0; k < (4 - temp % 4) % 4; k++)
						tvShowVideoPlayList.add( tvShowVideoPlayList.size(), video);
					ArrayList<SetgetVodTvShowVideoPlay> tvShowvideotemp = new ArrayList<>();
					for (int i = 0; i < tvShowVideoPlayList.size() / 4; i ++) {
						tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4 + 3));
						tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4 + 2));
						tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4 + 1));
						tvShowvideotemp.add(tvShowVideoPlayList.get(i * 4));
					}
					tvShowVideoPlayList.clear();
					for (int i = 0; i < tvShowvideotemp.size(); i ++)
						tvShowVideoPlayList.add(tvShowvideotemp.get(i));
				}
				vodTvShowVideoPlayAdapter.notifyDataSetChanged();
				bPageLoading = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mFocusedView != null)
						mFocusedView.requestFocus();
				}
			}, 10);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mBGetData = false;
				}
			}, 100);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					DismissProgress(getActivity());
				}
			}, 500);
		} else if (requestCode == ADD_FAVORITES) {
			DismissProgress(getActivity());
			System.out.println("Response for add Live Channels --------" + response.toString());
			Toast.makeText(getActivity(), "הוסף בהצלחה למועדפים", Toast.LENGTH_SHORT).show();
			add_fav_episode_text.setText("הסר ממועדפים");
			add_To_fav_img_episode.setImageResource(R.drawable.remove_fav);
			fav_flag = "remove_fav";
			VodSubcategory_Fragment.is_infav = "1";
			//LoadScheduleRecord(date);
		} else if (requestCode == REMOVE_FAVORITES) {
			DismissProgress(getActivity());
			System.out.println("Response for Remove Live Channels --------" + response.toString());
			Toast.makeText(getActivity(), "הוסר בהצלחה מהמועדפים", Toast.LENGTH_SHORT).show();
			// LoadScheduleRecord(date);
			add_fav_episode_text.setText("הוסף למועדפים");
			add_To_fav_img_episode.setImageResource(R.drawable.add_fav);
			fav_flag = "add_fav";
			VodSubcategory_Fragment.is_infav = "0";
		}
		// System.out.println("arraylist---------tvshow---"+tvShowVideoPlayList.size());
	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {

	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {

	}

	void ShowProgressDilog(Context c) {
		pDialog = new ProgressDialog(c);
		pDialog.show();
		pDialog.setCancelable(true);
		pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		pDialog.setContentView(R.layout.layout_progress_dilog);
	}

	void DismissProgress(Context c) {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}


	public class DateComparator implements Comparator<SetgetVodTvShowVideoPlay> {
		@Override
		public int compare(SetgetVodTvShowVideoPlay lhs, SetgetVodTvShowVideoPlay rhs) {
			Double distance = Double.valueOf(rhs.getCounter());
			Double distance1 = Double.valueOf(lhs.getCounter());
			if (distance.compareTo(distance1) < 0) {
				return -1;
			} else if (distance.compareTo(distance1) > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public class DateComparator_rev implements Comparator<SetgetVodTvShowVideoPlay> {
		@Override
		public int compare(SetgetVodTvShowVideoPlay lhs, SetgetVodTvShowVideoPlay rhs) {
			Double distance = Double.valueOf(lhs.getCounter());
			Double distance1 = Double.valueOf(rhs.getCounter());
			if (distance.compareTo(distance1) < 0) {
				return -1;
			} else if (distance.compareTo(distance1) > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private boolean isViewDownIcon(CustomRecyclerView _gridview_tv_show, RtlGridLayoutManager _layoutManager, int _itemCount) {
		if (!once == true || _gridview_tv_show == null || _layoutManager == null) {
			return false;
		}
		int screenWidth = _gridview_tv_show.getWidth();
		int screenHeight = _gridview_tv_show.getHeight();
		if (_itemCount <= 0) {
			return false;
		}
		int itemWidth = _layoutManager.getChildAt(0).getWidth();
		int itemHeight = _layoutManager.getChildAt(0).getHeight();

		horizontalItemCount = (int) (screenWidth / itemWidth);
		int verticalItemCount = (int) (screenHeight / itemHeight);
		return (horizontalItemCount * verticalItemCount < _itemCount) ? true : false;
	}

	/*private void requestData(final int focusPosition){
		if(totalpage < page_count) return;
		if(page_count == 1){
			GetTvShowList(String.valueOf(page_count));
		}else{
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mHome.setSelectedPosition(HomeActivity.VodTvshowPlayRecycler, focusPosition);
					GetTvShowListPage(String.valueOf(page_count));
				}
			}, 500);
		}
	}*/
}
