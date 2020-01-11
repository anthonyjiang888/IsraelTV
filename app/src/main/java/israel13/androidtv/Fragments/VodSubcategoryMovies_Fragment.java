package israel13.androidtv.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Tv_show_play_activity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Adapter.VodSubMovieGridRecyclerAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomGridView;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.RtlGridLayoutManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by krishanu on 14/06/17.
 */
public class VodSubcategoryMovies_Fragment extends Fragment implements IJSONParseListener {

	CustomGridView gridview_vod_subcategory_movies;
	TextView cat_name_;
	SharedPreferences logindetails;
	View tempView = null;
	int VOD_MOVIE_VIDEOPLAY = 300;
	int VOD_MOVIE_VIDEOPLAY_PAGE = 311;
	int nTotalPageCnt = 0;
	int nCurPageNo = 0;
	final int PAGE_ROW_COUNT = 8;
	boolean bPageLoading = false;

	ProgressDialog pDialog;

	public static ArrayList<SetgetMovies> vodMovieVideoPlayList = new ArrayList<>();
	VodSubMovieGridRecyclerAdapter vodSubCategoryMoviesGridAdapter;
	public static String Cat_name_movie = "";

	private View mMovieBeforeView = null;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.activity_vod_subcategory_movies, null);

		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);

		gridview_vod_subcategory_movies = (CustomGridView) layout.findViewById(R.id.gridview_vod_sub_submovies);
		cat_name_ = (TextView) layout.findViewById(R.id.cat_name_);

		String str = VodSubcategory_Fragment.Cat_name;
		// String str1 = str.replace('|','>');
		cat_name_.setText(str);
		Cat_name_movie = cat_name_.getText().toString();

		//default focus set
		HomeActivity activityHome = (HomeActivity)this.getContext();
		activityHome.setSelectedPosition(HomeActivity.VodSubMovieGridRecycler, 0);

		//load first part
		Call_api();

		gridview_vod_subcategory_movies.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount == 0) return;
				if (!bPageLoading) {
					if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
						//Log.e("...", "Last Item Wow !");
						//Do pagination.. i.e. fetch new data
						if (nCurPageNo < nTotalPageCnt) {
							bPageLoading = true;
							GetMoviesListPage(String.valueOf(nCurPageNo + 1));
						}
					}
				}
			}
		});

		vodMovieVideoPlayList = new ArrayList<>();
		vodSubCategoryMoviesGridAdapter = new VodSubMovieGridRecyclerAdapter(vodMovieVideoPlayList, getActivity());
		gridview_vod_subcategory_movies.setAdapter(vodSubCategoryMoviesGridAdapter);
		gridview_vod_subcategory_movies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (tempView != null){
					tempView.animate().scaleX(1).scaleY(1).setDuration(200);
				}
				view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
				tempView = view;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		gridview_vod_subcategory_movies.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					if (tempView != null)
						tempView.animate().scaleX(1).scaleY(1).setDuration(200);
					tempView = null;
				}else{
					if (gridview_vod_subcategory_movies.getSelectedView() != null) {
						gridview_vod_subcategory_movies.getSelectedView().animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
						tempView = gridview_vod_subcategory_movies.getSelectedView();
					}
				}
			}
		});
		gridview_vod_subcategory_movies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						VodMovieVideoPlayActivity.position1 = position;
		                VodMovieVideoPlayActivity.isinfav = vodMovieVideoPlayList.get(position).getMovies_isinfav();
						VodMovieVideoPlayActivity.vodid = vodMovieVideoPlayList.get(position).getMovies_id();;
						VodMovieVideoPlayActivity.flag_delete = true;
		                getActivity().startActivity(new Intent(getActivity(),VodMovieVideoPlayActivity.class));
			}
		});

		layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					gridview_vod_subcategory_movies.requestFocus();
			}
		});

//		gridview_vod_subcategory_movies.setOnScrollListener(new RecyclerView.OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//				super.onScrollStateChanged(recyclerView, newState);
//				View v = getView().findFocus();
//				if (newState != RecyclerView.SCROLL_STATE_IDLE) {
//					changeFoscusableState(false);
//				} else {
//					changeFoscusableState(true);
//				}
//			}
//		});


		return layout;
	}

//	private void changeFoscusableState(boolean bFocusable) {
//		Activity act = getActivity();
//		if ((act != null) && (act instanceof HomeActivity)) {
//			HomeActivity homeAct = (HomeActivity) act;
//			homeAct.setFocusableState(bFocusable);
//		}
//
//		VodSubMovieGridRecyclerAdapter adpter = (VodSubMovieGridRecyclerAdapter)gridview_vod_subcategory_movies.getAdapter();
//		adpter.setMarqueState(bFocusable);
//	}


	@Override
	public void onResume() {

		getView().setFocusableInTouchMode(false);
		getView().requestFocus();
		super.onResume();
	}

	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {
			GetMoviesList("1");
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

	void GetMoviesList(String page) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("id", VodSubcategory_Fragment.vod_subcategory_id);
		parms.putString("gs", "1");
//		parms.putString("mo", "20");
		parms.putString("page", page);
		parms.putString("pagesize", String.valueOf(gridview_vod_subcategory_movies.nRowCellCount * PAGE_ROW_COUNT));
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatelist.php", VOD_MOVIE_VIDEOPLAY, this, parms, false);
	}

	void GetMoviesListPage(String page) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("id", VodSubcategory_Fragment.vod_subcategory_id);
		parms.putString("gs", "1");
		parms.putString("mo", "20");
		parms.putString("page", page);
		parms.putString("pagesize", String.valueOf(gridview_vod_subcategory_movies.nRowCellCount * PAGE_ROW_COUNT));
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatelist.php", VOD_MOVIE_VIDEOPLAY_PAGE, this, parms, false);
	}


	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		if (requestCode == VOD_MOVIE_VIDEOPLAY) {

			System.out.println("Vod Subcategory Movie Videoplay List response ----------------" + response.toString());
			try {

				JSONObject results = response.getJSONObject("results");
				JSONObject subshows = results.getJSONObject("subshows");

				nTotalPageCnt = Constant.parseInt(subshows.getString("totp"));
				nCurPageNo = 1;

				JSONArray vodms = subshows.getJSONArray("vodms");
				for (int i = 0; i < vodms.length(); i++) {
					try {
						JSONObject object = vodms.getJSONObject(i);
						SetgetMovies videoPlay = new SetgetMovies();
						videoPlay.setMovies_id(object.getString("id"));
						videoPlay.setMovies_year(object.getString("year"));
						videoPlay.setMovies_name(object.getString("name"));
						videoPlay.setMovies_description(object.getString("description"));
						videoPlay.setCreated(object.getString("created"));
						videoPlay.setMovies_genre(object.optString("genre", "מידע אינו זמין"));
						videoPlay.setViews(object.optString("views", "0"));
						videoPlay.setMovies_length(object.optString("length", "מידע אינו זמין"));
						videoPlay.setMovies_stars(object.optString("stars", "0.0"));
						videoPlay.setMovies_isinfav(object.optString("isinfav", "0"));
						videoPlay.setMovies_pic(object.getString("showpic"));
						vodMovieVideoPlayList.add(videoPlay);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (vodMovieVideoPlayList.size() > 0) {
				vodSubCategoryMoviesGridAdapter.notifyDataSetChanged();
				gridview_vod_subcategory_movies.setSelection(0);
			}
			gridview_vod_subcategory_movies.requestFocus();
			DismissProgress(getActivity());
		} else if (requestCode == VOD_MOVIE_VIDEOPLAY_PAGE) {
			System.out.println("Vod Movie Videoplay PAGE List response ----------------" + response.toString());
			try {
				JSONObject results = response.getJSONObject("results");
				JSONObject subshows = results.getJSONObject("subshows");

				JSONArray vodms = subshows.getJSONArray("vodms");

				if (vodms.length() > 0)
					nCurPageNo ++;

				for (int i = 0; i < vodms.length(); i++) {
					try {
						JSONObject object = vodms.getJSONObject(i);
						SetgetMovies videoPlay = new SetgetMovies();
						videoPlay.setMovies_id(object.getString("id"));
						videoPlay.setMovies_year(object.getString("year"));
						videoPlay.setMovies_genre(object.optString("genre", "מידע אינו זמין"));
						videoPlay.setMovies_name(object.getString("name"));
						videoPlay.setMovies_description(object.getString("description"));
						videoPlay.setCreated(object.getString("created"));
						videoPlay.setViews(object.optString("views", ""));
						videoPlay.setMovies_length(object.optString("length", ""));
						videoPlay.setMovies_stars(object.optString("stars", "0.0"));
						videoPlay.setMovies_isinfav(object.optString("isinfav", "0"));
						videoPlay.setMovies_pic(object.getString("showpic"));
						vodMovieVideoPlayList.add(videoPlay);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				vodSubCategoryMoviesGridAdapter.notifyDataSetChanged();
				vodSubCategoryMoviesGridAdapter.mbDatasetNotified = true;
				DismissProgress(getActivity());
				bPageLoading = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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


}
