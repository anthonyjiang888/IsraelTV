package israel13.androidtv.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Tv_show_play_activity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Adapter.FavoritesEpisodesAdapter;
import israel13.androidtv.Adapter.FavoritesLiveChannelAdapter;
import israel13.androidtv.Adapter.FavoritesLiveRadioAdapter;
import israel13.androidtv.Adapter.FavoritesMoviesAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.Others.Interface1;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetEpisodes;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by krishanu on 30/05/17.
 */
public class Favorites_Fragment extends Fragment implements IJSONParseListener, Interface1 , HomeActivity.BackInterface, FavoritesLiveChannelAdapter.FirstFocusInterface{


	int FAVORITES = 130;
	int REMOVE_FAVORITES = 131;
	ProgressDialog pDialog;
	SharedPreferences logindetails;
	public boolean isselected = false;
	public static ArrayList<SetgetSubchannels> liveChannelsList;
	public static ArrayList<SetgetSubchannels> liveRadioList;
	public static ArrayList<SetgetMovies> favMoviesList;
	public static ArrayList<SetgetEpisodes> favEpisodesList;

	CustomRecyclerView recycler_view_live_section_favorites, recycler_view_tvshow_section_favorites;
	CustomRecyclerView recycler_view_movies_section_favorites, recycler_view_radio_section_favorites;

	FavoritesLiveChannelAdapter liveChannelAdapter = null;
	FavoritesLiveRadioAdapter liveRadioAdapter = null;
	FavoritesMoviesAdapter moviesAdapter = null;
	FavoritesEpisodesAdapter episodesAdapter = null;

	TextView edit_favorites;
	TextView live_section_favorites;
	LinearLayout layout1;
	boolean flag = false;

	TextView live_section_favorites_noitems, broadcast_section_favorites_noitems;
	TextView movies_section_favorites_noitems, radio_section_favorites_noitems;
	public static String flag_delete = "";

	private final int LIVE_SELECTED = 1;
	private final int TVSHOW_SELECTED = 2;
	private final int MOVIES_SELECTED = 3;
	private final int RADIO_SELECTED = 4;
	private boolean bFragmentCreated = false;
	public static boolean is_editfav = false;
	int nRemovePosition = -1;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		liveChannelsList = new ArrayList<>();
		liveRadioList = new ArrayList<>();
		favEpisodesList = new ArrayList<>();
		favMoviesList = new ArrayList<>();
		liveChannelAdapter = new FavoritesLiveChannelAdapter(liveChannelsList, getActivity(), Favorites_Fragment.this);
		liveChannelAdapter.onFavoritefirstItemFocus = this;
		liveRadioAdapter = new FavoritesLiveRadioAdapter(liveRadioList, getActivity(), Favorites_Fragment.this);
		moviesAdapter = new FavoritesMoviesAdapter(favMoviesList, getActivity(), Favorites_Fragment.this);
		episodesAdapter = new FavoritesEpisodesAdapter(favEpisodesList, getActivity(), Favorites_Fragment.this);
		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);

		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.activity_favorites, null);
		final ScrollView scrollView = (ScrollView)layout.findViewById(R.id.scrollview1);
		((HomeActivity)getActivity()).onFavoriteFragmentBack = this;
		((HomeActivity)getActivity()).flagReady = false;
		((HomeActivity)getActivity()).isFavoritesFragment = true;
		AllChannels_Fragment.is_infav = "";
		VodSubcategory_Fragment.is_infav = "";
		VodMovieVideoPlayActivity.isinfav = "";

		recycler_view_live_section_favorites = (CustomRecyclerView) layout.findViewById(R.id.recycler_view_live_section_favorites);
		recycler_view_tvshow_section_favorites = (CustomRecyclerView) layout.findViewById(R.id.recycler_view_tvshow_section_favorites);
		recycler_view_movies_section_favorites = (CustomRecyclerView) layout.findViewById(R.id.recycler_view_movies_section_favorites);
		recycler_view_radio_section_favorites = (CustomRecyclerView) layout.findViewById(R.id.recycler_view_radio_section_favorites);

		recycler_view_live_section_favorites.setAdapter(liveChannelAdapter);
		recycler_view_tvshow_section_favorites.setAdapter(episodesAdapter);
		recycler_view_movies_section_favorites.setAdapter(moviesAdapter);
		recycler_view_radio_section_favorites.setAdapter(liveRadioAdapter);

		recycler_view_live_section_favorites.setItemViewCacheSize(200);
		recycler_view_tvshow_section_favorites.setItemViewCacheSize(200);
		recycler_view_movies_section_favorites.setItemViewCacheSize(200);
		recycler_view_radio_section_favorites.setItemViewCacheSize(200);

		live_section_favorites_noitems = (TextView) layout.findViewById(R.id.live_section_favorites_noitems);
		broadcast_section_favorites_noitems = (TextView) layout.findViewById(R.id.broadcast_section_favorites_noitems);
		movies_section_favorites_noitems = (TextView) layout.findViewById(R.id.movies_section_favorites_noitems);
		radio_section_favorites_noitems = (TextView) layout.findViewById(R.id.radio_section_favorites_noitems);

		edit_favorites = (TextView) layout.findViewById(R.id.edit_favorites);
		live_section_favorites = (TextView) layout.findViewById(R.id.live_section_favorites);
		layout1 = (LinearLayout) layout.findViewById(R.id.layout);
		live_section_favorites.setFocusable(true);
		LinearLayoutManager layoutManager_live = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_live.setReverseLayout(true);

		LinearLayoutManager layoutManager_tvshow = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_tvshow.setReverseLayout(true);

		LinearLayoutManager layoutManager_movies = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_movies.setReverseLayout(true);

		LinearLayoutManager layoutManager_radio = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_radio.setReverseLayout(true);

		recycler_view_live_section_favorites.setLayoutManager(layoutManager_live);
		recycler_view_tvshow_section_favorites.setLayoutManager(layoutManager_tvshow);
		recycler_view_movies_section_favorites.setLayoutManager(layoutManager_movies);
		recycler_view_radio_section_favorites.setLayoutManager(layoutManager_radio);
		edit_favorites.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
					live_section_favorites.setFocusable(true);

				}else{
					view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).x(view.getWidth() * 0.09f);
					live_section_favorites.setFocusable(false);
					scrollView.smoothScrollTo(0,0);
					is_editfav = true;
				}
			}
		});
		live_section_favorites.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b) {
					edit_favorites.requestFocus();
				}
			}
		});
		layout1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b) {
					edit_favorites.requestFocus();
				}
			}
		});

		edit_favorites.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!flag) {
					flag = true;
					((HomeActivity)getActivity()).flagReady = true;
					edit_favorites.setText("סגור עריכה");
				} else {
					edit_favorites.setText("עריכה");
					((HomeActivity)getActivity()).flagReady = false;
					flag = false;
				}
				if (liveChannelsList.size() != 0) {
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, 0);
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, -1);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, -1);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, -1);

				}
				else if (favEpisodesList.size() != 0) {
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, -1);
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, -1);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, -1);
					}
				else if (favMoviesList.size() != 0) {
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, -1);
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, -1);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, 0);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, -1);
				}
				else if (liveRadioList.size() != 0) {
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, -1);
					((HomeActivity) getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, -1);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, -1);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, 0);
				}//GetFavorites();
				isselected = false;
				setEditFlagOnLists();
				liveChannelAdapter.notifyDataSetChanged();
				liveRadioAdapter.notifyDataSetChanged();
				moviesAdapter.notifyDataSetChanged();
				episodesAdapter.notifyDataSetChanged();
				live_section_favorites.setFocusable(true);
				recycler_view_live_section_favorites.smoothScrollToPosition(0);
				recycler_view_movies_section_favorites.smoothScrollToPosition(0);
				recycler_view_radio_section_favorites.smoothScrollToPosition(0);
				recycler_view_tvshow_section_favorites.smoothScrollToPosition(0);
			}
		});

		recycler_view_live_section_favorites.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					changeFoscusableState(LIVE_SELECTED, true);
				} else {
					changeFoscusableState(LIVE_SELECTED, false);
				}
			}
		});

		recycler_view_tvshow_section_favorites.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

		recycler_view_movies_section_favorites.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

		recycler_view_radio_section_favorites.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					changeFoscusableState(RADIO_SELECTED, true);
				} else {
					changeFoscusableState(RADIO_SELECTED, false);
				}
			}
		});

		return layout;
	}

	private void setEditFlagOnLists() {
		for (int i = 0; i < liveChannelsList.size(); i ++) {
			liveChannelsList.get(i).setIsactive(flag?"active":"");
		}
		for (int i = 0; i < liveRadioList.size(); i ++) {
			liveRadioList.get(i).setIsactive(flag?"active":"");
		}
		for (int i = 0; i < favEpisodesList.size(); i ++) {
			favEpisodesList.get(i).setIsactive(flag?"active":"");
		}
		for (int i = 0; i < favMoviesList.size(); i ++) {
			favMoviesList.get(i).setIsactive(flag?"active":"");
		}
	}

	private void changeFoscusableState(int nSelected, boolean bFocusable) {
		Activity act = getActivity();
		if ((act != null) && (act instanceof HomeActivity)) {
			HomeActivity homeAct = (HomeActivity) act;
			homeAct.setFocusableState(bFocusable);
		}

		if (edit_favorites != null)
			edit_favorites.setFocusable(bFocusable);

		int nCount = 0;
		if (nSelected >= TVSHOW_SELECTED) {
			nCount = recycler_view_live_section_favorites.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = recycler_view_live_section_favorites.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			recycler_view_live_section_favorites.setFocusable(bFocusable);
		}
		if (nSelected >= MOVIES_SELECTED) {
			nCount = recycler_view_tvshow_section_favorites.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = recycler_view_tvshow_section_favorites.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			recycler_view_tvshow_section_favorites.setFocusable(bFocusable);
		}
		if (nSelected >= RADIO_SELECTED) {
			nCount = recycler_view_movies_section_favorites.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = recycler_view_movies_section_favorites.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			recycler_view_movies_section_favorites.setFocusable(bFocusable);
		}
	}

	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {
			GetFavorites();
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

	void GetFavorites() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("stfor", "99");
		parms.putString("act", "0");
		parms.putString("ch", "");
		parms.putString("datetime", "");
		parms.putString("vodid", "");
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", FAVORITES, this, parms, false);
	}

	void RemoveFavoritesMovies(String vodid) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("stfor", "2");
		parms.putString("act", "11");
		parms.putString("ch", "");
		parms.putString("datetime", "");
		parms.putString("vodid", vodid);
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", REMOVE_FAVORITES, this, parms, false);
	}

	void RemoveFavoritesTvshow(String subcat_id) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("stfor", "3");
		parms.putString("act", "11");
		parms.putString("ch", "");
		parms.putString("datetime", "");
		parms.putString("vodid", subcat_id);
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", REMOVE_FAVORITES, this, parms, false);
	}

	void RemoveFavoritesLiveChannelRadio(String channelid) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("stfor", "0");
		parms.putString("act", "11");
		parms.putString("ch", channelid);
		parms.putString("datetime", "");
		parms.putString("vodid", "");
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", REMOVE_FAVORITES, this, parms, false);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		DismissProgress(getActivity());

//		liveChannelsList.clear();
//		liveRadioList.clear();
//		favEpisodesList.clear();
//		favMoviesList.clear();

		if (requestCode == FAVORITES) {

			liveChannelsList.clear();
			liveRadioList.clear();
			favEpisodesList.clear();
			favMoviesList.clear();

			JSONObject results = response.optJSONObject("results");
			if (results != null)
			{
				JSONArray live = results.optJSONArray("live");
				if (live != null) {
					for (int i = 0; i < live.length(); i++) {
						try {
							JSONObject object = live.getJSONObject(i);
							JSONObject channel = object.optJSONObject("channel");
							if (channel == null)
								continue;

							if (channel.getString("isradio").equalsIgnoreCase("0")) {
								SetgetSubchannels subchannels = new SetgetSubchannels();
								subchannels.setSub_channelsid(channel.getString("id"));
								subchannels.setName(channel.getString("name"));
								subchannels.setEname(channel.getString("ename"));
								subchannels.setImage(channel.getString("image"));
								subchannels.setGname(channel.getString("gname"));
								subchannels.setIsradio(channel.getString("isradio"));
								subchannels.setIsinfav("1");

								if (flag) {
									subchannels.setIsactive("active");
								} else {
									subchannels.setIsactive("");
								}
								try {
									subchannels.setChid(channel.getString("chid"));
								} catch (Exception e) {
									subchannels.setChid("0");
								}

								JSONObject show = channel.getJSONObject("show");
								subchannels.setRunning_epg_name(show.getString("name"));
								subchannels.setEpg_time(show.getString("time"));
								subchannels.setEpg_length(show.getString("lengthtime"));

								liveChannelsList.add(subchannels);
							}
							else {
								SetgetSubchannels subchannels = new SetgetSubchannels();
								subchannels.setSub_channelsid(channel.getString("id"));
								subchannels.setName(channel.getString("name"));
								subchannels.setEname(channel.getString("ename"));
								subchannels.setImage(channel.getString("image"));
								subchannels.setGname(channel.getString("gname"));
								subchannels.setIsradio(channel.getString("isradio"));
								subchannels.setIsinfav("1");

								if (flag) {
									subchannels.setIsactive("active");
								} else {
									subchannels.setIsactive("");
								}

								liveRadioList.add(subchannels);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				JSONArray vod = results.optJSONArray("vod");
				if (vod != null) {
					try {
						for (int j = 0; j < vod.length(); j++) {
							JSONObject object = vod.getJSONObject(j);
							JSONObject show = object.getJSONObject("show");

							SetgetMovies setgetMovies = new SetgetMovies();
							setgetMovies.setMovies_id(show.getString("id"));
							setgetMovies.setMovies_pic(show.getString("picture"));
							setgetMovies.setMovies_name(show.getString("name"));
							setgetMovies.setMovies_length(show.optString("length", "0"));
							setgetMovies.setViews(show.optString("views", ""));
							setgetMovies.setMovies_genre(show.optString("genre", ""));

							if (flag) {
								setgetMovies.setIsactive("active");
							} else {
								setgetMovies.setIsactive("");
							}

							setgetMovies.setMovies_year(show.getString("year"));
							setgetMovies.setCreated(show.getString("created"));
							setgetMovies.setMovies_description(show.getString("description"));
							setgetMovies.setCateid(show.getString("cateid"));

							JSONObject category = object.getJSONObject("category");
							setgetMovies.setMovies_stars(category.optString("stars", "0"));
							setgetMovies.setMovies_category(category.optString("name", "מידע אינו זמין"));
							setgetMovies.setMovies_isinfav("1");
							favMoviesList.add(setgetMovies);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				JSONArray tvshow = results.optJSONArray("tvshow");
				if (tvshow != null) {
					try {
						for (int k = 0; k < tvshow.length(); k++) {

							JSONObject object = tvshow.getJSONObject(k);
							JSONObject category = object.getJSONObject("category");

							SetgetEpisodes setgetEpisodes = new SetgetEpisodes();
							setgetEpisodes.setEpisode_id(category.getString("id"));
							setgetEpisodes.setEpisode_name(category.getString("name"));
							setgetEpisodes.setEpisodes_pic(category.getString("showpic"));
							setgetEpisodes.setEpisode_isinfav("1");
							if (flag) {
								setgetEpisodes.setIsactive("active");
							} else {
								setgetEpisodes.setIsactive("");
							}

							favEpisodesList.add(setgetEpisodes);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			if (liveChannelsList.size() == 0) {
				live_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_live_section_favorites.setVisibility(View.GONE);
			} else {
				live_section_favorites_noitems.setVisibility(View.GONE);
				recycler_view_live_section_favorites.setVisibility(View.VISIBLE);
			}
			if (liveRadioList.size() == 0) {
				radio_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_radio_section_favorites.setVisibility(View.GONE);
			} else {
				radio_section_favorites_noitems.setVisibility(View.GONE);
				recycler_view_radio_section_favorites.setVisibility(View.VISIBLE);
			}
			if (favMoviesList.size() == 0) {
				movies_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_movies_section_favorites.setVisibility(View.GONE);
			} else {
				movies_section_favorites_noitems.setVisibility(View.GONE);
				recycler_view_movies_section_favorites.setVisibility(View.VISIBLE);
			}
			if (favEpisodesList.size() == 0) {
				broadcast_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_tvshow_section_favorites.setVisibility(View.GONE);
			} else {
				broadcast_section_favorites_noitems.setVisibility(View.GONE);
				recycler_view_tvshow_section_favorites.setVisibility(View.VISIBLE);
			}

			liveChannelAdapter.notifyDataSetChanged();
			liveRadioAdapter.notifyDataSetChanged();
			moviesAdapter.notifyDataSetChanged();
			episodesAdapter.notifyDataSetChanged();
			live_section_favorites.setFocusable(true);
		}
		else if (requestCode == REMOVE_FAVORITES) {

			DismissProgress(getActivity());
			System.out.println("Response for Remove Live Channels --------" + response.toString());
			Toast.makeText(getActivity(), "הוסר בהצלחה ממועדפים", Toast.LENGTH_SHORT).show();

			//GetFavorites();
			if (flag_delete.equalsIgnoreCase("delete_live")) {
				resetLiveChannelSelectionOnRemove();
			} else if (flag_delete.equalsIgnoreCase("delete_radio")) {
				resetLiveRadioSelectionOnRemove();
			} else if (flag_delete.equalsIgnoreCase("delete_movie")) {
				resetMovieSelectionOnRemove();
			} else if (flag_delete.equalsIgnoreCase("delete_tvshow")) {
				resetEpisodeSelectionOnRemove();
			}
			live_section_favorites.setFocusable(true);
			flag_delete = "";
		}
	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {
	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {
	}

	private void resetLiveChannelSelectionOnRemove() {
		if (nRemovePosition == liveChannelsList.size() - 1) {
			if (nRemovePosition != 0) ((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, nRemovePosition - 1);
			else {
				((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, -1);
				live_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_live_section_favorites.setVisibility(View.GONE);
				if (favEpisodesList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					episodesAdapter.notifyDataSetChanged();
				} else if (favMoviesList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					moviesAdapter.notifyDataSetChanged();
				} else if (liveRadioList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesLiveRadio);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, 0);
					liveRadioAdapter.notifyDataSetChanged();
				}
			}
		} else {
			((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, nRemovePosition);
		}
		liveChannelsList.remove(nRemovePosition);
		liveChannelAdapter.notifyDataSetChanged();

	}

	private void resetLiveRadioSelectionOnRemove() {
		if (nRemovePosition == liveRadioList.size() - 1) {
			if (nRemovePosition != 0) ((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, nRemovePosition - 1);
			else {
				((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, -1);
				radio_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_radio_section_favorites.setVisibility(View.GONE);
				if (favMoviesList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesMovies);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, 0);
					moviesAdapter.notifyDataSetChanged();
				} else if (favEpisodesList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					episodesAdapter.notifyDataSetChanged();
				} else if (liveChannelsList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesLiveChannel);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, 0);
					liveChannelAdapter.notifyDataSetChanged();
				}
			}
		} else {
			((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, nRemovePosition);
		}
		liveRadioList.remove(nRemovePosition);
		liveRadioAdapter.notifyDataSetChanged();
	}

	private void resetEpisodeSelectionOnRemove() {
		if (nRemovePosition == favEpisodesList.size() - 1) {
			if (nRemovePosition != 0) ((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, nRemovePosition - 1);
			else {
				((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, -1);
				broadcast_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_tvshow_section_favorites.setVisibility(View.GONE);
				if (liveChannelsList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesLiveChannel);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveChannel, 0);
					liveChannelAdapter.notifyDataSetChanged();
				} else if (favMoviesList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					moviesAdapter.notifyDataSetChanged();
				} else if (liveRadioList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesLiveRadio);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, 0);
					liveRadioAdapter.notifyDataSetChanged();
				}
			}
		} else {
			((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, nRemovePosition);
		}
		favEpisodesList.remove(nRemovePosition);
		episodesAdapter.notifyDataSetChanged();
	}

	private void resetMovieSelectionOnRemove() {
		if (nRemovePosition == favMoviesList.size() - 1) {
			if (nRemovePosition != 0) ((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, nRemovePosition - 1);
			else {
				((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, -1);
				movies_section_favorites_noitems.setVisibility(View.VISIBLE);
				recycler_view_movies_section_favorites.setVisibility(View.GONE);
				if (favEpisodesList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					episodesAdapter.notifyDataSetChanged();
				} else if (liveChannelsList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesEpisodes);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesEpisodes, 0);
					liveChannelAdapter.notifyDataSetChanged();
				} else if (liveRadioList.size() > 0) {
					((HomeActivity)getActivity()).setAvialableAdapterId(HomeActivity.FavoritesLiveRadio);
					((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesLiveRadio, 0);
					liveRadioAdapter.notifyDataSetChanged();
				}
			}
		} else {
			((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.FavoritesMovies, nRemovePosition);
		}
		favMoviesList.remove(nRemovePosition);
		moviesAdapter.notifyDataSetChanged();
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

	@Override
	public void onResume() {
		/*if (!bFragmentCreated) {
			getView().setFocusableInTouchMode(false);
			getView().requestFocus();
			bFragmentCreated = true;
		}*/
		((HomeActivity)getActivity()).isFavoritesFragment = true;
		((HomeActivity)getActivity()).flagReady = false;
		((HomeActivity)getActivity()).requestTabFocus(Constant.TAB_FAVOURITE);

		Call_api();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		flag_delete = "";
		((HomeActivity)getActivity()).isFavoritesFragment = false;
		super.onDestroy();
	}

	@Override
	public void remove(String category, String id, int position) {
		nRemovePosition = position;
		if (category.equalsIgnoreCase("movie")) {
			RemoveFavoritesMovies(id);
			flag_delete = "delete_movie";
		} else if (category.equalsIgnoreCase("live")) {
			RemoveFavoritesLiveChannelRadio(id);
			flag_delete = "delete_live";
		} else if (category.equalsIgnoreCase("radio")) {
			RemoveFavoritesLiveChannelRadio(id);
			flag_delete = "delete_radio";
		} else {
			RemoveFavoritesTvshow(id);
			flag_delete = "delete_tvshow";
		}
	}

	public void onFragmentTopFromBackState() {
		((HomeActivity)getActivity()).isFavoritesFragment = true;
		((HomeActivity)getActivity()).requestTabFocus(Constant.TAB_FAVOURITE);
		((HomeActivity)getActivity()).flagReady = false;
//		Call_api();
	}

	@Override
	public void onBackKeyPress() {
		edit_favorites.callOnClick();
		((HomeActivity)getActivity()).flagReady = false;
	}

	@Override
	public void onfirstFocusLive() {

	}
}
