package israel13.androidtv.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Records_Play_Activity;
import israel13.androidtv.Activity.Tv_show_play_activity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Adapter.VodMainPageEpisodesAdapter;
import israel13.androidtv.Adapter.VodMainPageMoviesAdapter;
import israel13.androidtv.Adapter.VodMainpageGridRecyclerAdapter;
import israel13.androidtv.CallBacks.TVShowPlayDataUpdate;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetEpisodes;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Setter_Getter.SetgetVodMainPageCategory;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.NonscrollRecylerview;
import israel13.androidtv.Utils.RtlGridLayoutManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Puspak on 22/06/17.
 */
public class VodMainPage_Fragment extends Fragment implements IJSONParseListener, VodMainPageMoviesAdapter.VodAdapterCallback, VodMainPageEpisodesAdapter.VodEpisodesAdapterCallback, VodMainpageGridRecyclerAdapter.ScrollbartoTop {

	ScrollView mScrollView;
	Toolbar toolbar_activity_vod_mainpage;
	ImageButton back_button_activity_vod_mainpage;
	NonscrollRecylerview gridview_categories_vod_main_page;
	CustomRecyclerView recycler_view_tvshows_activity_mainpage, recycler_view_movies_activity_mainpage;
	VodMainpageGridRecyclerAdapter adapterRecyclerVodCategories;
	VodMainPageMoviesAdapter adapterRecyclerMovies;
	VodMainPageEpisodesAdapter adapterRecyclerEpisodes;

	int GET_EPISODES_MOVIES_VOD = 160;
	int GET_VOD_CATEGORIES = 161;
	ProgressDialog pDialog;
	ArrayList<SetgetEpisodes> vodallEpisodesArrayList;
	ArrayList<SetgetMovies> vodallMoviesArrayList;
	ArrayList<SetgetVodMainPageCategory> vodCategoryList;
	SharedPreferences logindetails;
	private int TVSHOW_SELECTED = 1;
	private int MOVIES_SELECTED = 2;

	public static String vod_category_id = "", vod_category_name = "";
	public static TVShowPlayDataUpdate callbackDataUpdate = null;
	private boolean bFragmentCreated = false;
	private boolean bVodSubEntered = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		vodallEpisodesArrayList = new ArrayList<>();
		vodallMoviesArrayList = new ArrayList<>();
		vodCategoryList = new ArrayList<>();
		adapterRecyclerVodCategories = new VodMainpageGridRecyclerAdapter(vodCategoryList, getActivity());
		adapterRecyclerVodCategories.mScrollController = this;
		adapterRecyclerMovies = new VodMainPageMoviesAdapter(vodallMoviesArrayList, getActivity());
		adapterRecyclerMovies.adapterCallback = this;
		adapterRecyclerEpisodes = new VodMainPageEpisodesAdapter(vodallEpisodesArrayList, getActivity());
		adapterRecyclerEpisodes.mCallBack = this;
		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.activity_vod_main_page, null);

		gridview_categories_vod_main_page = (NonscrollRecylerview) layout.findViewById(R.id.gridview_categories_vod_main_page);
		recycler_view_tvshows_activity_mainpage = (CustomRecyclerView) layout.findViewById(R.id.recycler_view_tvshows_activity_mainpage);
		recycler_view_movies_activity_mainpage = (CustomRecyclerView) layout.findViewById(R.id.recycler_view_movies_activity_mainpage);
		mScrollView = (ScrollView) layout.findViewById(R.id.gridscrollview);
		recycler_view_tvshows_activity_mainpage.setItemViewCacheSize(200);
		recycler_view_movies_activity_mainpage.setItemViewCacheSize(200);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		int display_item = (width / 380);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float density = getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels / density;
		//  float dpWidth  = outMetrics.widthPixels / density;
		float dpWidth = outMetrics.widthPixels;
		float without_margin = dpWidth - (30);
		int number_of_colums = (int) without_margin / 350;

		RtlGridLayoutManager layoutManager_subcat = new RtlGridLayoutManager(getActivity(), number_of_colums);
		layoutManager_subcat.setReverseLayout(false);

		gridview_categories_vod_main_page.setLayoutManager(layoutManager_subcat);
		gridview_categories_vod_main_page.setAdapter(adapterRecyclerVodCategories);

		LinearLayoutManager layoutManager_movies = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_movies.setReverseLayout(true);

		LinearLayoutManager layoutManager_episodes = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_episodes.setReverseLayout(true);

		recycler_view_tvshows_activity_mainpage.setLayoutManager(layoutManager_episodes);
		recycler_view_tvshows_activity_mainpage.setAdapter(adapterRecyclerEpisodes);
		recycler_view_movies_activity_mainpage.setLayoutManager(layoutManager_movies);
		recycler_view_movies_activity_mainpage.setAdapter(adapterRecyclerMovies);

		recycler_view_tvshows_activity_mainpage.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);

				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					changeFoscusableState(TVSHOW_SELECTED, true);
				} else {
					changeFoscusableState(TVSHOW_SELECTED, false);
				}
			}
		});

		recycler_view_movies_activity_mainpage.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					changeFoscusableState(MOVIES_SELECTED, true);
				} else {
					changeFoscusableState(MOVIES_SELECTED, false);
				}
			}
		});

		return layout;
	}

	private void changeFoscusableState(int nSelected, boolean bFocusable) {
		Activity act = getActivity();
		if ((act != null) && (act instanceof HomeActivity)) {
			HomeActivity homeAct = (HomeActivity) act;
			homeAct.setFocusableState(bFocusable);
		}

		int nCount = gridview_categories_vod_main_page.getChildCount();
		for (int i = 0; i < nCount; i ++) {
			View v = gridview_categories_vod_main_page.getChildAt(i);
			v.setFocusable(bFocusable);
		}
		gridview_categories_vod_main_page.setFocusable(bFocusable);

		if (nSelected == MOVIES_SELECTED) {
			nCount = recycler_view_tvshows_activity_mainpage.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = recycler_view_tvshows_activity_mainpage.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			recycler_view_tvshows_activity_mainpage.setFocusable(bFocusable);
		}
	}

	@Override
	public void onResume() {

		if (!bFragmentCreated) {
			getView().setFocusableInTouchMode(false);
			getView().requestFocus();
			bFragmentCreated = true;
		}
		if (((HomeActivity) getActivity()).FlagVodTVShow == false)
			((HomeActivity) getActivity()).requestTabFocus(Constant.TAB_VOD);
		((HomeActivity) getActivity()).FlagVodTVShow = false;
		if (!bVodSubEntered) {
			Call_api();
		}

		super.onResume();
	}

	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {
			vodallEpisodesArrayList.clear();
			vodallMoviesArrayList.clear();
			vodCategoryList.clear();
			GetVodCategories();
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

	void GetVodCategories() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatemain.php", GET_VOD_CATEGORIES, this, parms, false);
	}

	void GetAll_Episodes_Movies() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodnew.php", GET_EPISODES_MOVIES_VOD, this, parms, false);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		DismissProgress(getActivity());
		if (requestCode == GET_EPISODES_MOVIES_VOD) {
			try {
				JSONObject results = response.getJSONObject("results");
				JSONObject newvodms = results.getJSONObject("newvodms");
				JSONArray episodes = newvodms.getJSONArray("episodes");

				for (int i = 0; i < episodes.length(); i++) {
					try {
						JSONObject episodeobj = episodes.getJSONObject(i);
						SetgetEpisodes setgetEpisodes = new SetgetEpisodes();
						setgetEpisodes.setEpisode_id(episodeobj.getString("id"));
						setgetEpisodes.setIsplaying("false");
						setgetEpisodes.setEpisode_year(episodeobj.optString("year", ""));
						setgetEpisodes.setEpisode_genre(episodeobj.optString("genre", "מידע אינו זמין"));
						setgetEpisodes.setEpisode_approve(episodeobj.optString("approve", ""));
						setgetEpisodes.setEpisode_name(episodeobj.getString("name"));
						setgetEpisodes.setEpisodeno(episodeobj.getString("episodeno"));
						setgetEpisodes.setEpisode_description(episodeobj.optString("description", "מידע אינו זמין"));
						setgetEpisodes.setEpisode_vodlist(episodeobj.optString("vodlist", ""));
						setgetEpisodes.setCateid(episodeobj.optString("cateid", ""));
						setgetEpisodes.setEpisode_isinfav(episodeobj.optString("isinfav", "0"));
						setgetEpisodes.setEpisode_length(episodeobj.optString("length", "0"));
						setgetEpisodes.setEpisode_views(episodeobj.optString("views", "מידע אינו זמין"));
						setgetEpisodes.setEpisode_created(episodeobj.optString("created", ""));
						setgetEpisodes.setEpisode_updated(episodeobj.optString("update", ""));
						setgetEpisodes.setEpisodes_pic(episodeobj.getString("pic"));
						setgetEpisodes.setEpisode_stars(episodeobj.optString("stars", ""));
						setgetEpisodes.setEpisode_isinfav(episodeobj.optString("isinfav", "0"));
						vodallEpisodesArrayList.add(setgetEpisodes);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				JSONArray movies = newvodms.getJSONArray("movies");

				for (int j = 0; j < movies.length(); j++) {
					try {
						JSONObject movieobj = movies.getJSONObject(j);
						SetgetMovies setgetMovies = new SetgetMovies();
						setgetMovies.setMovies_id(movieobj.getString("id"));
						setgetMovies.setMovies_genre(movieobj.optString("genre", "מידע אינו זמין"));
						setgetMovies.setMovies_year(movieobj.optString("year", "מידע אינו זמין"));
						setgetMovies.setMovies_name(movieobj.getString("name"));
						setgetMovies.setMovies_isinfav(movieobj.optString("isinfav", "0"));
						setgetMovies.setCreated(movieobj.optString("created", "0"));
						setgetMovies.setMovies_description(movieobj.optString("description", "מידע אינו זמין"));
						setgetMovies.setViews(movieobj.optString("views", ""));
						setgetMovies.setMovies_length(movieobj.optString("length", ""));
						setgetMovies.setMovies_stars(movieobj.optString("stars", "0.0"));
						setgetMovies.setMovies_pic(movieobj.getString("pic"));
						vodallMoviesArrayList.add(setgetMovies);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				JSONObject cates = results.getJSONObject("cates");

				for (int i = 0; i < vodallEpisodesArrayList.size(); i++) {
					Iterator<String> keys = cates.keys();
					while (keys.hasNext()) {

						String next = keys.next();
						if (vodallEpisodesArrayList.get(i).getCateid().equalsIgnoreCase(next)) {

							JSONArray array = cates.getJSONArray(next);

							JSONObject obj1 = array.getJSONObject(0);
							JSONObject obj2 = array.getJSONObject(1);

							vodallEpisodesArrayList.get(i).setSeason_id(obj1.getString("id"));
							vodallEpisodesArrayList.get(i).setSeason_name(obj1.getString("name"));
							vodallEpisodesArrayList.get(i).setSeason_description(obj1.getString("description"));

							vodallEpisodesArrayList.get(i).setTvshow_id(obj2.getString("id"));
							vodallEpisodesArrayList.get(i).setTvshow_name(obj2.getString("name"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			adapterRecyclerEpisodes.notifyDataSetChanged();
			adapterRecyclerMovies.notifyDataSetChanged();
			recycler_view_movies_activity_mainpage.setVisibility(vodallMoviesArrayList.size() == 0 ? View.GONE : View.VISIBLE);
			recycler_view_tvshows_activity_mainpage.setVisibility(vodallEpisodesArrayList.size() == 0 ? View.GONE : View.VISIBLE);

		} else if (requestCode == GET_VOD_CATEGORIES) {

			System.out.println("Response for Vode Categories --------------------" + response.toString());

			try {

				JSONArray results = response.getJSONArray("results");

				for (int i = 0; i < results.length(); i++) {
					try {
						JSONObject object = results.getJSONObject(i);
						SetgetVodMainPageCategory category = new SetgetVodMainPageCategory();
						category.setCat_id(object.getString("id"));
						category.setName(object.getString("name"));
						category.setStars(object.getString("stars"));
						category.setYear(object.getString("year"));
						category.setDescription(object.getString("description"));
						category.setShowpic(object.getString("showpic"));
						vodCategoryList.add(category);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			gridview_categories_vod_main_page.setVisibility(vodCategoryList.size() == 0 ? View.GONE : View.VISIBLE);
			adapterRecyclerVodCategories.notifyDataSetChanged();
			GetAll_Episodes_Movies();
		}
	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {

	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {

	}

	void ShowProgressDilog(Context c) {
		if (pDialog == null) {
			pDialog = new ProgressDialog(c);
		}
		try {
			pDialog.show();
			pDialog.setCancelable(true);
			pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			pDialog.setContentView(R.layout.layout_progress_dilog);
		} catch (Exception e) {}
	}

	void DismissProgress(Context c) {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}

	public void onFragmentBackState() {
		View v = getView();
		if (v != null) {
			((LinearLayout) (getView().findViewById(R.id.layout_root_vodmain))).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			bVodSubEntered = true;
		}
	}

	public void onFragmentTopFromBackState() {
		View v = getView();
		if (v != null) {
			((LinearLayout) (getView().findViewById(R.id.layout_root_vodmain))).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
			((HomeActivity) getActivity()).requestTabFocus(Constant.TAB_VOD);
			bVodSubEntered = false;


			Call_api();

		}
		//Log.e("test", "vod page top");
	}



	@Override
	public void showNetworkAlert() {
		ShowErrorAlert(getActivity(), "Please check your network connection..");
	}

	@Override
	public void ShowNetworkAlert() {
		ShowErrorAlert(getActivity(), "Please check your network connection..");
	}

	@Override
	public void onScrollbarTop() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mScrollView.smoothScrollTo(0,0);
			}
		}, 30);

	}
}
