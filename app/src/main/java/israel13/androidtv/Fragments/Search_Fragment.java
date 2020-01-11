package israel13.androidtv.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Adapter.SearchLiveRecyclerviewAdapter;
import israel13.androidtv.Adapter.SearchRadioRecyclerviewAdapter;
import israel13.androidtv.Adapter.SearchMoviesRecyclerviewAdapter;
import israel13.androidtv.Adapter.SearchRecordRecyclerviewAdapter;
import israel13.androidtv.Adapter.Search_TV_ShowAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetAllChannels;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDates;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDatesDetails;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Setter_Getter.SetgetVodSubcategory;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.NonscrollRecylerview;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS;
import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

/**
 * Created by puspak on 06/07/17.
 */
public class Search_Fragment extends Fragment implements IJSONParseListener {
	private TextView search_button;
	private NonscrollRecylerview tvshow_new_search_recyclerview, movies_new_search_recyclerview;
	private int VOD_SEARCH_ALL = 101;
	private int GET_ALLCHANNEL = 104;
	private int GET_ALLRECORD = 105;
	private ProgressDialog pDialog;
	private SharedPreferences logindetails;
	private TextView live_tv_numberof_results, live_tv_numberof_no_results, radio_number_of_results, radio_number_of_no_results, tvshow_number_of_results,
			tvshow_number_of_no_results, movies_number_of_results, movies_number_of_no_results, record_number_of_Results, record_number_of_no_Results;
	private LinearLayout container_live_tv_search, container_radio_search, container_tv_show_search, container_movies_search, container_record_search;

	private ArrayList<SetgetSearchRecordDatesDetails> setgetSearchRecordDatesDetailsArrayList;
	private ArrayList<SetgetSubchannels> channelsList;
	private ArrayList<SetgetSubchannels> radioList;
	private ArrayList<SetgetVodSubcategory> tvshowsList;
	private ArrayList<SetgetMovies> moviesList;
	private ArrayList<SetgetSearchRecordDates> recordList;

	public TextView edit_text_search;
	public static CustomRecyclerView live_tv_new_search_recyclerview, radio_new_search_recyclerview, record_new_search_recyclerview, record_child_new_search_recyclerview;
	public static SearchRecordRecyclerviewAdapter searchRecordRecyclerviewAdapter;
	public int SelectedNumber = -1;
	private SearchLiveRecyclerviewAdapter adapterSearchLiveRecycler;
	private SearchRadioRecyclerviewAdapter adapterSearchRadioRecycler;
	private Search_TV_ShowAdapter adapterSearchTvShowRecycler;
	private SearchMoviesRecyclerviewAdapter adapterSearchMoviesRecycler;

	public static int flag_page_change = 0;

	private int LIVE_SELECTED = 1;
	private int RECORD_SELECTED = 2;
	private int RECORD_CHILD_SELECTED = 3;
	private int TVSHOW_SELECTED = 4;
	private int MOVIES_SELECTED = 5;
	private int RADIO_SELECTED = 6;
	private int SEARCH_ALL = 7;
	////////////Button//////////////
	private int w, mWindowWidth;
	private LinearLayout mKLayout, key_digit;
	private Button mB[] = new Button[42];
	private Button   mBLeft, mBRight, mBsubmit;
	private boolean isEdit = false;
	private ImageButton mBSpace,mBack;
	private ScrollView search_result;

	private boolean bEnableKeyboard = true;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		channelsList = new ArrayList<>();
		radioList = new ArrayList<>();
		tvshowsList = new ArrayList<>();
		moviesList = new ArrayList<>();
		recordList = new ArrayList<>();
		adapterSearchLiveRecycler = new SearchLiveRecyclerviewAdapter(channelsList, getActivity(), Search_Fragment.this);
		adapterSearchRadioRecycler = new SearchRadioRecyclerviewAdapter(radioList, getActivity(), Search_Fragment.this);
		searchRecordRecyclerviewAdapter = new SearchRecordRecyclerviewAdapter(recordList, getActivity(), Search_Fragment.this);
		adapterSearchTvShowRecycler = new Search_TV_ShowAdapter(tvshowsList, getActivity(), Search_Fragment.this);
		adapterSearchMoviesRecycler = new SearchMoviesRecyclerviewAdapter(moviesList, getActivity(), Search_Fragment.this);
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout = layoutInflater.inflate(R.layout.activity_search, null);

////////////////////////key//////////////////////////
		mKLayout = (LinearLayout) layout.findViewById(R.id.keyboard_layout);
		key_digit = (LinearLayout) layout.findViewById(R.id.key_digit);

		mB[0] = (Button) layout.findViewById(R.id.key_0);
		mB[1] = (Button) layout.findViewById(R.id.key_1);
		mB[2] = (Button) layout.findViewById(R.id.key_2);
		mB[3] = (Button) layout.findViewById(R.id.key_3);
		mB[4] = (Button) layout.findViewById(R.id.key_4);
		mB[5] = (Button) layout.findViewById(R.id.key_5);
		mB[6] = (Button) layout.findViewById(R.id.key_6);
		mB[7] = (Button) layout.findViewById(R.id.key_7);
		mB[8] = (Button) layout.findViewById(R.id.key_8);
		mB[9] = (Button) layout.findViewById(R.id.key_9);
		mB[10] = (Button) layout.findViewById(R.id.key_Qof);
		mB[11] = (Button) layout.findViewById(R.id.key_Resh);
		mB[12] = (Button) layout.findViewById(R.id.key_Alef);
		mB[13] = (Button) layout.findViewById(R.id.key_Tet);
		mB[14] = (Button) layout.findViewById(R.id.key_Vav);
		mB[15] = (Button) layout.findViewById(R.id.key_NunSofit);
		mB[16] = (Button) layout.findViewById(R.id.key_MemSofit);
		mB[17] = (Button) layout.findViewById(R.id.key_Pe);
		mB[18] = (Button) layout.findViewById(R.id.key_bracket);
		mB[19] = (Button) layout.findViewById(R.id.key_line);
		mB[20] = (Button) layout.findViewById(R.id.key_Shin);
		mB[21] = (Button) layout.findViewById(R.id.key_Dalet);
		mB[22] = (Button) layout.findViewById(R.id.key_Gimel);
		mB[23] = (Button) layout.findViewById(R.id.key_Kaf);
		mB[24] = (Button) layout.findViewById(R.id.key_Yod);
		mB[25] = (Button) layout.findViewById(R.id.key_Het);
		mB[26] = (Button) layout.findViewById(R.id.key_Lamed);
		mB[27] = (Button) layout.findViewById(R.id.key_Koof);
		mB[28] = (Button) layout.findViewById(R.id.key_PeSofit);
		mB[29] = (Button) layout.findViewById(R.id.key_exmark);
		mB[30] = (Button) layout.findViewById(R.id.key_Zayin);
		mB[31] = (Button) layout.findViewById(R.id.key_Samekh);
		mB[32] = (Button) layout.findViewById(R.id.key_Bet);
		mB[33] = (Button) layout.findViewById(R.id.key_He);
		mB[34] = (Button) layout.findViewById(R.id.key_Nun);
		mB[35] = (Button) layout.findViewById(R.id.key_Mem);
		mB[36] = (Button) layout.findViewById(R.id.key_Tsadi);
		mB[37] = (Button) layout.findViewById(R.id.key_TsadiSofit);
		mB[38] = (Button) layout.findViewById(R.id.key_Ayin);
		mB[39] = (Button) layout.findViewById(R.id.key_Tav);
		mB[40] = (Button) layout.findViewById(R.id.key_dot);
		mB[41] = (Button) layout.findViewById(R.id.key_comma);
		for (int i = 0; i < mB.length; i++){
			mB[i].setTextColor(getResources().getColor(R.color.keyboard_color));
			mB[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			mB[i].setOnClickListener(btnClickListener);
			mB[i].setOnFocusChangeListener(btnFocusListener);
		}

		mBSpace = (ImageButton) layout.findViewById(R.id.xSpace);
		mBsubmit  = (Button) layout.findViewById(R.id.xSubmit);
		mBack   = (ImageButton) layout.findViewById(R.id.xBack);
		mBLeft  = (Button) layout.findViewById(R.id.key_left);
		mBRight   = (Button) layout.findViewById(R.id.key_right);
		mBSpace.setOnFocusChangeListener(btnFocusListener1);
		mBsubmit.setOnFocusChangeListener(btnFocusListener1);
		mBack.setOnFocusChangeListener(btnFocusListener1);
		mBSpace.setOnClickListener(btnClickListener);
		mBsubmit.setOnClickListener(btnClickListener);
		mBLeft.setOnClickListener(btnClickListener);
		mBRight.setOnClickListener(btnClickListener);
		mBack.setOnClickListener(btnClickListener);
		search_result = (ScrollView) layout.findViewById(R.id.search_result);
///////////////////////////////////////////////////////////////
		live_tv_numberof_results = (TextView) layout.findViewById(R.id.live_tv_numberof_results);
		live_tv_numberof_no_results = (TextView) layout.findViewById(R.id.live_tv_numberof_no_results);
		radio_number_of_results = (TextView) layout.findViewById(R.id.radio_number_of_results);
		radio_number_of_no_results = (TextView) layout.findViewById(R.id.radio_number_of_no_results);
		tvshow_number_of_results = (TextView) layout.findViewById(R.id.tvshow_number_of_results);
		tvshow_number_of_no_results = (TextView) layout.findViewById(R.id.tvshow_number_of_no_results);
		movies_number_of_results = (TextView) layout.findViewById(R.id.movies_number_of_results);
		movies_number_of_no_results = (TextView) layout.findViewById(R.id.movies_number_of_no_results);
		record_number_of_Results = (TextView) layout.findViewById(R.id.record_number_of_Results);
		record_number_of_no_Results = (TextView) layout.findViewById(R.id.record_number_of_no_Results);

		search_button = (TextView) layout.findViewById(R.id.search_button);
		edit_text_search = (TextView) layout.findViewById(R.id.edit_text_search);
		hideDefaultKeyboard();

		live_tv_new_search_recyclerview = (CustomRecyclerView) layout.findViewById(R.id.live_tv_new_search_recyclerview);
		radio_new_search_recyclerview = (CustomRecyclerView) layout.findViewById(R.id.radio_new_search_recyclerview);
		tvshow_new_search_recyclerview = (NonscrollRecylerview) layout.findViewById(R.id.tvshow_new_search_recyclerview);
		movies_new_search_recyclerview = (NonscrollRecylerview) layout.findViewById(R.id.movies_new_search_recyclerview);
		record_new_search_recyclerview = (CustomRecyclerView) layout.findViewById(R.id.record_new_search_recyclerview);
		record_child_new_search_recyclerview = (CustomRecyclerView) layout.findViewById(R.id.record_child_new_search_recyclerview);

		live_tv_new_search_recyclerview.setAdapter(adapterSearchLiveRecycler);
		radio_new_search_recyclerview.setAdapter(adapterSearchRadioRecycler);
		record_new_search_recyclerview.setAdapter(searchRecordRecyclerviewAdapter);
		tvshow_new_search_recyclerview.setAdapter(adapterSearchTvShowRecycler);
		movies_new_search_recyclerview.setAdapter(adapterSearchMoviesRecycler);
		((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.VodSearchEpisodes, -1);

		live_tv_new_search_recyclerview.setItemViewCacheSize(200);
		radio_new_search_recyclerview.setItemViewCacheSize(200);
		record_new_search_recyclerview.setItemViewCacheSize(200);
		record_child_new_search_recyclerview.setItemViewCacheSize(200);
		tvshow_new_search_recyclerview.setItemViewCacheSize(200);
		movies_new_search_recyclerview.setItemViewCacheSize(200);

		record_new_search_recyclerview.setItemAnimator(null);

		container_live_tv_search = (LinearLayout) layout.findViewById(R.id.container_live_tv_search);
		container_radio_search = (LinearLayout) layout.findViewById(R.id.container_radio_search);
		container_tv_show_search = (LinearLayout) layout.findViewById(R.id.container_tv_show_search);
		container_movies_search = (LinearLayout) layout.findViewById(R.id.container_movies_search);
		container_record_search = (LinearLayout) layout.findViewById(R.id.container_record_search);

		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		int display_item_movie = (width / 250);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float density = getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels / density;
		float dpWidth = outMetrics.widthPixels / density;
		float without_margin = dpWidth - (30 + 40);

		int number_of_colums_tvshow = (int) without_margin / 240;
		int number_of_colums_livechannel = (int) without_margin / 250;

		LinearLayoutManager layoutManager_subcat = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_subcat.setReverseLayout(true);
		live_tv_new_search_recyclerview.setLayoutManager(layoutManager_subcat);

		LinearLayoutManager layoutManager_radio = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_radio.setReverseLayout(true);
		radio_new_search_recyclerview.setLayoutManager(layoutManager_radio);

		LinearLayoutManager layoutManager_subcat2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_subcat2.setReverseLayout(true);
		tvshow_new_search_recyclerview.setLayoutManager(layoutManager_subcat2);

		LinearLayoutManager layoutManager_subcat3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_subcat3.setReverseLayout(true);
		movies_new_search_recyclerview.setLayoutManager(layoutManager_subcat3);

		LinearLayoutManager layoutManager_record = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_record.setReverseLayout(true);
		record_new_search_recyclerview.setLayoutManager(layoutManager_record);

		LinearLayoutManager layoutManager_subcat4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager_subcat4.setReverseLayout(true);
		record_child_new_search_recyclerview.setLayoutManager(layoutManager_subcat4);

		// show keyboard
		enableKeyboard();

		search_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(search_button.getWindowToken(), 0);
				mKLayout.setVisibility(View.GONE);

				if (edit_text_search.getText().length() >= 3) {
					Call_api();
				} else {
					Toast toast = Toast.makeText(getActivity(), "אנא רשום לפחות 3 אותיות/מספרים", Toast.LENGTH_SHORT);

					TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
					toastMessage.setTextColor(getResources().getColor(R.color.white));
					toast.show();
				}
			}
		});
		search_button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);

				}
			}
		});
		edit_text_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideDefaultKeyboard();
				enableKeyboard();
			}
		});

		live_tv_new_search_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

		record_new_search_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					changeFoscusableState(RECORD_SELECTED, true);
				} else {
					changeFoscusableState(RECORD_SELECTED, false);
				}
			}
		});

		record_child_new_search_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					changeFoscusableState(RECORD_CHILD_SELECTED, true);
				} else {
					changeFoscusableState(RECORD_CHILD_SELECTED, false);
				}
			}
		});

		tvshow_new_search_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

		movies_new_search_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

		radio_new_search_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

	private void changeFoscusableState(int nSelected, boolean bFocusable) {
		Activity act = getActivity();
		if ((act != null) && (act instanceof HomeActivity)) {
			HomeActivity homeAct = (HomeActivity) act;
			homeAct.setFocusableState(bFocusable);
		}

		if (search_button != null)
			search_button.setFocusable(bFocusable);
		if (edit_text_search != null)
			edit_text_search.setFocusable(bFocusable);

		int nCount = 0;
		if (nSelected >= RECORD_SELECTED) {
			nCount = live_tv_new_search_recyclerview.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = live_tv_new_search_recyclerview.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			live_tv_new_search_recyclerview.setFocusable(bFocusable);
		}
		if (nSelected >= RECORD_CHILD_SELECTED) {
			nCount = record_new_search_recyclerview.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = record_new_search_recyclerview.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			record_new_search_recyclerview.setFocusable(bFocusable);
		}
		if (nSelected >= TVSHOW_SELECTED) {
			nCount = record_child_new_search_recyclerview.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = record_child_new_search_recyclerview.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			record_child_new_search_recyclerview.setFocusable(bFocusable);
		}
		if (nSelected >= MOVIES_SELECTED) {
			nCount = tvshow_new_search_recyclerview.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = tvshow_new_search_recyclerview.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			tvshow_new_search_recyclerview.setFocusable(bFocusable);
		}
		if (nSelected >= RADIO_SELECTED) {
			nCount = movies_new_search_recyclerview.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = movies_new_search_recyclerview.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			movies_new_search_recyclerview.setFocusable(bFocusable);
		}
		if (nSelected >= SEARCH_ALL) {
			nCount = radio_new_search_recyclerview.getChildCount();
			for (int i = 0; i < nCount; i ++) {
				View v = radio_new_search_recyclerview.getChildAt(i);
				v.setFocusable(bFocusable);
			}
			radio_new_search_recyclerview.setFocusable(bFocusable);
		}
	}

	@Override
	public void onResume() {
		/*if (edit_text_search.getText().length() >= 3 && flag_page_change == 1) {
			search_button.requestFocus();
			Call_api();
		}*/
		super.onResume();
	}


	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {

			SearchAll(edit_text_search.getText().toString().trim());
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

	void SearchAll(String key) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
//		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("act", "all");
		parms.putString("key", key);
		parms.putString("page", "1");
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
			mResponse.getResponse(Request.Method.POST, Constant.BASE_URL + "search.php", VOD_SEARCH_ALL, this, parms, false);
		} else {
			mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "search.php", VOD_SEARCH_ALL, this, parms, false);
		}
	}
	void GetAllChannelsList(String isradio) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid",logindetails.getString("sid",""));
		parms.putString("isradio", isradio);
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "channels.php", GET_ALLCHANNEL, this, parms, false);
	}

	void GetAllRecordList() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid",logindetails.getString("sid",""));
		parms.putString("isradio", "1");
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "channels.php", GET_ALLRECORD, this, parms, false);
	}
	public float dpToPx(Context context, float valueInDp) {
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
	}

	public int pxToDp(int px) {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, displayMetrics);
		return dp;
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
//		if (requestCode == GET_ALLRECORD) {
//			DismissProgress(getActivity());
//			try {
//				JSONArray results = response.getJSONArray("results");
//				for (int i = 0; i < results.length(); i++) {
//					JSONArray channels;
//					try {
//						JSONObject resultobj = results.getJSONObject(i);
//						channels = resultobj.getJSONArray("channels");
//					} catch (JSONException e) {
//						e.printStackTrace();
//						continue;
//					}
//
//					for (int j = 0; j < channels.length(); j++) {
//						try {
//							JSONObject channelobj = channels.getJSONObject(j);
//
//							for (int k = 0; k < recordList.size(); k ++ ){
//								if (recordList.get(k)() == channelobj.getString("chid")){
//									recordList.get(k).setChannel_odid(channelobj.getString("gid") + channelobj.getString("odid"));
//									break;
//								}
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		if (requestCode == GET_ALLCHANNEL) {
			DismissProgress(getActivity());
			try {
				JSONArray results = response.getJSONArray("results");
				for (int i = 0; i < results.length(); i++) {

					try {
						JSONObject resultobj = results.getJSONObject(i);
						for (int k = 0; k < channelsList.size(); k ++ ){
							if (channelsList.get(k).getGid().equals(resultobj.getString("id"))){
								String tmp = channelsList.get(k).getOdid();
								channelsList.get(k).setOdid(resultobj.getString("odid")+tmp);
								break;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Collections.sort(channelsList, new DateComparator());
			adapterSearchLiveRecycler.notifyDataSetChanged();
			live_tv_new_search_recyclerview.requestFocus();
			live_tv_numberof_results.setText("שידורים חיים | נמצאו: " + channelsList.size() + " תוצאות");


		} else if (requestCode == VOD_SEARCH_ALL) {
			DismissProgress(getActivity());
			System.out.println("Response for Search list `--------------------" + response.toString());

			moviesList.clear();
			tvshowsList.clear();

			container_live_tv_search.setVisibility(View.VISIBLE);
			container_radio_search.setVisibility(View.VISIBLE);
			container_tv_show_search.setVisibility(View.VISIBLE);
			container_movies_search.setVisibility(View.VISIBLE);
			container_record_search.setVisibility(View.VISIBLE);
			record_child_new_search_recyclerview.setVisibility(View.GONE);

			try {
				JSONObject results = response.getJSONObject("results");
				JSONObject vod = results.getJSONObject("vod");
				JSONArray movie = new JSONArray();
				try {
					movie = vod.getJSONArray("movie");
					container_movies_search.setVisibility(View.VISIBLE);
					if (movie.length() == 0) {
						movies_new_search_recyclerview.setVisibility(View.GONE);
						movies_number_of_no_results.setVisibility(View.VISIBLE);
						movies_number_of_no_results.setText("לא נמצאו תוצאות");
						movies_number_of_results.setText("וי או די סרטים | נמצאו: " + moviesList.size() + " תוצאות");
					} else {
						for (int i = 0; i < movie.length(); i++) {
							try {
								JSONObject object = movie.getJSONObject(i);
								SetgetMovies videoPlay = new SetgetMovies();
								videoPlay.setMovies_id(object.getString("id"));
								videoPlay.setMovies_year(object.getString("year"));
								videoPlay.setMovies_name(object.getString("name"));
								videoPlay.setMovies_description(object.getString("description"));
								videoPlay.setCreated(object.getString("created"));
								videoPlay.setMovies_genre(object.optString("genre", "מידע אינו זמין"));
								videoPlay.setViews(object.optString("views", "מידע אינו זמין"));
								videoPlay.setMovies_length(object.optString("length", "0"));
								videoPlay.setMovies_stars(object.optString("stars", "0.0"));
								videoPlay.setMovies_isinfav(object.optString("isinfav", "0"));
								videoPlay.setMovies_pic(object.getString("picture"));
								// videoPlay.setIs("0");
								moviesList.add(videoPlay);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						adapterSearchMoviesRecycler.notifyDataSetChanged();

						movies_number_of_results.setText("וי או די סרטים | נמצאו: " + moviesList.size() + " תוצאות");
						movies_number_of_no_results.setVisibility(View.GONE);
						movies_new_search_recyclerview.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					movies_new_search_recyclerview.setVisibility(View.GONE);
					movies_number_of_no_results.setVisibility(View.VISIBLE);
					movies_number_of_no_results.setText("לא נמצאו תוצאות");
					movies_number_of_results.setText("וי או די סרטים | נמצאו: " + moviesList.size() + " תוצאות");
				}

				JSONArray tvshow = vod.optJSONArray("tvshow");

				if (tvshow == null || tvshow.length() == 0) {
					tvshow_new_search_recyclerview.setVisibility(View.GONE);
					tvshow_number_of_no_results.setVisibility(View.VISIBLE);
					tvshow_number_of_no_results.setText("לא נמצאו תוצאות");
					tvshow_number_of_results.setText("וי או די תוכניות/סדרות ועוד... | נמצאו: " + tvshowsList.size() + " תוצאות");
				} else {
					for (int i = 0; i < tvshow.length(); i++) {
						try {
							JSONObject object = tvshow.getJSONObject(i);
							SetgetVodSubcategory videoPlay = new SetgetVodSubcategory();
							videoPlay.setSubcat_id(object.getString("id"));
							videoPlay.setName(object.getString("name"));
							videoPlay.setCreated(object.getString("created"));
							videoPlay.setGenre(object.optString("genre", "מידע אינו זמין"));
							videoPlay.setViews(object.optString("views", "מידע אינו זמין"));
							videoPlay.setLength(object.optString("length", "0"));
							videoPlay.setStars(object.optString("stars", "0.0"));
							videoPlay.setIsinfav(object.optString("isinfav", "0"));
							videoPlay.setShowpic(object.getString("showpic"));
							// videoPlay.setIsepisode(object.getString("isepisode"));
							tvshowsList.add(videoPlay);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					adapterSearchTvShowRecycler.notifyDataSetChanged();
					tvshow_number_of_results.setText("וי או די תוכניות/סדרות ועוד... | נמצאו: " + tvshowsList.size() + " תוצאות");
					tvshow_new_search_recyclerview.setVisibility(View.VISIBLE);

					tvshow_number_of_no_results.setVisibility(View.GONE);
				}

				if (movie.length() == 0 && tvshow.length() == 0) {
					Toast.makeText(getActivity(), "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
				}

				channelsList.clear();

				JSONArray channel = results.optJSONArray("channel");
				container_live_tv_search.setVisibility(View.VISIBLE);
				if (channel == null || channel.length() == 0) {
					// Toast.makeText(getActivity(), "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
					live_tv_new_search_recyclerview.setVisibility(View.GONE);
					live_tv_numberof_no_results.setVisibility(View.VISIBLE);
					live_tv_numberof_no_results.setText("לא נמצאו תוצאות");
					live_tv_numberof_results.setText("שידורים חיים | נמצאו: " + channelsList.size() + " תוצאות");
				} else {
					for (int i = 0; i < channel.length(); i++) {
						try {
							JSONObject channelobj = channel.getJSONObject(i);
							SetgetSubchannels setgetSubchannels = new SetgetSubchannels();
							setgetSubchannels.setSub_channelsid(channelobj.getString("id"));
							setgetSubchannels.setName(channelobj.getString("name"));
							setgetSubchannels.setEname(channelobj.getString("ename"));
							setgetSubchannels.setImage(channelobj.getString("image"));
							setgetSubchannels.setIsradio(channelobj.getString("isradio"));
							setgetSubchannels.setOdid(channelobj.getString("odid"));
							setgetSubchannels.setGid(channelobj.getString("gid"));
							setgetSubchannels.setIsinfav(channelobj.optString("isinfav", "0"));
							setgetSubchannels.setChid(channelobj.optString("chid", "0"));

							try {
								JSONArray schedule = channelobj.getJSONArray("schedule");
								JSONObject obj = schedule.getJSONObject(0);
								setgetSubchannels.setEpg_name(obj.getString("name"));
								setgetSubchannels.setTime(obj.getString("time"));
								setgetSubchannels.setDuration(obj.getString("lengthtime"));
							} catch (Exception e) {
								e.printStackTrace();
								setgetSubchannels.setEpg_name("");
								setgetSubchannels.setTime("");
								setgetSubchannels.setDuration("");
							}

							channelsList.add(setgetSubchannels);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					live_tv_numberof_no_results.setVisibility(View.GONE);
				}

				live_tv_new_search_recyclerview.setVisibility(View.VISIBLE);

				live_tv_numberof_results.setVisibility(View.VISIBLE);
				GetAllChannelsList("0");
				//Collections.sort(channelsList, new DateComparator());
				//adapterSearchLiveRecycler.notifyDataSetChanged();
				//live_tv_new_search_recyclerview.requestFocus();
				live_tv_numberof_results.setText("שידורים חיים | נמצאו: " + channelsList.size() + " תוצאות");

				radioList.clear();

				JSONArray radio = results.optJSONArray("radio");
				container_radio_search.setVisibility(View.VISIBLE);

				if (radio == null || radio.length() == 0) {
					radio_new_search_recyclerview.setVisibility(View.GONE);
					radio_number_of_no_results.setVisibility(View.VISIBLE);
					radio_number_of_no_results.setText("לא נמצאו תוצאות");
					radio_number_of_results.setText("רדיו | נמצאו: " + radioList.size() + " תוצאות");
				} else {
					for (int i = 0; i < radio.length(); i++) {
						try {
							JSONObject channelobj = radio.getJSONObject(i);
							SetgetSubchannels setgetSubchannels = new SetgetSubchannels();
							setgetSubchannels.setSub_channelsid(channelobj.getString("id"));
							setgetSubchannels.setName(channelobj.getString("name"));
							setgetSubchannels.setEname(channelobj.getString("ename"));
							setgetSubchannels.setImage(channelobj.getString("image"));
							setgetSubchannels.setIsradio(channelobj.getString("isradio"));
							setgetSubchannels.setIsinfav(channelobj.optString("isinfav", "0"));
							try {
								JSONArray schedule = channelobj.getJSONArray("schedule");
								JSONObject obj = schedule.getJSONObject(0);
								setgetSubchannels.setEpg_name(obj.getString("name"));
								setgetSubchannels.setTime(obj.getString("time"));
								setgetSubchannels.setDuration(obj.getString("lengthtime"));
							} catch (Exception e) {
								setgetSubchannels.setEpg_name("");
								setgetSubchannels.setTime("");
								setgetSubchannels.setDuration("");
							}
							radioList.add(setgetSubchannels);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					radio_new_search_recyclerview.setVisibility(View.VISIBLE);
					radio_number_of_results.setVisibility(View.VISIBLE);
					adapterSearchRadioRecycler.notifyDataSetChanged();
					radio_number_of_results.setText("רדיו | נמצאו: " + radioList.size() + " תוצאות");
					radio_number_of_no_results.setVisibility(View.GONE);
				}

				recordList.clear();
				JSONObject record = results.getJSONObject("record");
				Iterator<String> keys = record.keys();

				while (keys.hasNext()) {
					container_record_search.setVisibility(View.VISIBLE);
					String next = keys.next();
					JSONObject object = null;
					try {
						object = record.getJSONObject(next);
					} catch(Exception ex) {
						ex.printStackTrace();
						continue;
					}
					Iterator<String> keys_obj = object.keys();

					while (keys_obj.hasNext()) {

						String next1 = keys_obj.next();
						SetgetSearchRecordDates setgetSearchRecordDates = new SetgetSearchRecordDates();

						if (recordList.size() == 0) {
							setgetSearchRecordDates.setChannelname(next1);

							JSONArray array = object.getJSONArray(next1);

							setgetSearchRecordDatesDetailsArrayList = new ArrayList<>();
							String logo = "";
							String odid = "";

							for (int i = 0; i < array.length(); i++) {
								try {
									JSONObject object1 = array.getJSONObject(i);
									SetgetSearchRecordDatesDetails details = new SetgetSearchRecordDatesDetails();
									details.setChannel(object1.getString("channel"));
									details.setRdatetime(object1.getString("rdatetime"));
									details.setDate(Constant.getUnixDate_record_date(object1.getString("rdatetime")));
									logo = object1.optString("logo", "");
									details.setTime(object1.optString("time", ""));
									details.setName(object1.optString("name", ""));
									details.setDescription(object1.optString("description", ""));
									details.setWday(object1.optString("wday", ""));
									details.setWeekno(object1.optString("weekno", ""));
									details.setGenre(object1.optString("genre", "מידע אינו זמין"));
									odid = (object1.optString("odid", "0"));
									details.setIsinfav(object1.optString("isinfav", "0"));
									details.setIsradio(object1.optString("isradio", "1"));
									details.setLogo(object1.optString("logo", ""));
									details.setChannelname(object1.optString("channelname", ""));
									details.setLengthtime(object1.optString("lengthtime", "מידע אינו זמין"));
									details.setStar(object1.optString("star", "0"));
									details.setShowpic(object1.optString("showpic", ""));
									setgetSearchRecordDatesDetailsArrayList.add(details);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							setgetSearchRecordDates.setSetgetSearchRecordDatesDetailsArrayList(setgetSearchRecordDatesDetailsArrayList);
							setgetSearchRecordDates.setChannel_logo(logo);
							setgetSearchRecordDates.setChannel_odid(odid);
							recordList.add(setgetSearchRecordDates);
						} else {
							boolean flag = false;
							int position = 0;
							for (int i = 0; i < recordList.size(); i++) {
								if (recordList.get(i).getChannelname().equalsIgnoreCase(next1)) {

									position = i;
									flag = true;
									break;

								} else {
									flag = false;
								}
							}

							if (flag) {
								JSONArray array = object.getJSONArray(next1);

								setgetSearchRecordDatesDetailsArrayList = new ArrayList<>();
								String logo = "";
								String odid = "";

								for (int j = 0; j < array.length(); j++) {
									try {
										JSONObject object1 = array.getJSONObject(j);
										SetgetSearchRecordDatesDetails details = new SetgetSearchRecordDatesDetails();
										details.setChannel(object1.getString("channel"));
										details.setRdatetime(object1.getString("rdatetime"));
										details.setDate(Constant.getUnixDate_record_date(object1.getString("rdatetime")));
										logo = object1.optString("logo", "");
										details.setTime(object1.optString("time", ""));
										details.setName(object1.optString("name", ""));
										details.setDescription(object1.optString("description", ""));
										details.setWday(object1.optString("wday", ""));
										details.setWeekno(object1.optString("weekno", ""));
										details.setGenre(object1.optString("genre", "מידע אינו זמין"));
										odid = (object1.optString("odid", "0"));
										details.setIsinfav(object1.optString("isinfav", "0"));
										details.setIsradio(object1.optString("isradio", "1"));
										details.setLogo(object1.optString("logo", ""));
										details.setChannelname(object1.optString("channelname", ""));
										details.setLengthtime(object1.optString("lengthtime", "מידע אינו זמין"));
										details.setStar(object1.optString("star", "0"));
										details.setShowpic(object1.optString("showpic", ""));
										//setgetSearchRecordDatesDetailsArrayList.add(details);
										recordList.get(position).getSetgetSearchRecordDatesDetailsArrayList().add(details);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								//recordList.get(i).setSetgetSearchRecordDatesDetailsArrayList(setgetSearchRecordDatesDetailsArrayList);
							} else {
								setgetSearchRecordDates.setChannelname(next1);

								JSONArray array = object.getJSONArray(next1);
								setgetSearchRecordDatesDetailsArrayList = new ArrayList<>();
								String logo = "";
								String odid = "";

								for (int j = 0; j < array.length(); j++) {
									try {
										JSONObject object1 = array.getJSONObject(j);
										SetgetSearchRecordDatesDetails details = new SetgetSearchRecordDatesDetails();
										details.setChannel(object1.getString("channel"));
										details.setRdatetime(object1.getString("rdatetime"));
										details.setDate(Constant.getUnixDate_record_date(object1.getString("rdatetime")));
										logo = object1.optString("logo", "");
										details.setTime(object1.optString("time", ""));
										details.setName(object1.optString("name", ""));
										details.setDescription(object1.optString("description", ""));
										details.setWday(object1.optString("wday", ""));
										details.setWeekno(object1.optString("weekno", ""));
										details.setGenre(object1.optString("genre", "מידע אינו זמין"));
										details.setIsinfav(object1.optString("isinfav", "0"));
										details.setIsradio(object1.optString("isradio", "1"));
										details.setLogo(object1.optString("logo", ""));
										details.setChannelname(object1.optString("channelname", ""));
										details.setLengthtime(object1.optString("lengthtime", "מידע אינו זמין"));
										details.setStar(object1.optString("star", "0"));
										odid = (object1.optString("odid", "0"));
										details.setShowpic(object1.optString("showpic", ""));
										setgetSearchRecordDatesDetailsArrayList.add(details);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								setgetSearchRecordDates.setSetgetSearchRecordDatesDetailsArrayList(setgetSearchRecordDatesDetailsArrayList);
								setgetSearchRecordDates.setChannel_logo(logo);
								setgetSearchRecordDates.setChannel_odid(odid);
								recordList.add(setgetSearchRecordDates);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (recordList.size() == 0) {
				record_new_search_recyclerview.setVisibility(View.GONE);
				record_number_of_no_Results.setVisibility(View.VISIBLE);
				record_number_of_no_Results.setText("לא נמצאו תוצאות");
				record_number_of_Results.setText("הקלטות | נמצאו: " + recordList.size() + " תוצאות");
			} else {
				record_number_of_Results.setVisibility(View.VISIBLE);
				record_number_of_Results.setText("הקלטות | נמצאו: " + recordList.size() + " תוצאות");
				Collections.sort(recordList, new DateComparator_record());
				searchRecordRecyclerviewAdapter.notifyDataSetChanged();
				record_new_search_recyclerview.setVisibility(View.VISIBLE);
				record_number_of_no_Results.setVisibility(View.GONE);
			}
			if (!channelsList.isEmpty()){
				adapterSearchTvShowRecycler.setFlag(0);
				SelectedNumber = 0;
			}
			else if (!recordList.isEmpty()) {
				adapterSearchTvShowRecycler.setFlag(0);
				SelectedNumber = 1;
			}
			else if (!tvshowsList.isEmpty())
				adapterSearchTvShowRecycler.setFlag(1);
			else if (!moviesList.isEmpty()) {
				adapterSearchTvShowRecycler.setFlag(0);
				SelectedNumber = 3;
			}
			else if (!radioList.isEmpty()) {
				SelectedNumber = 4;
				adapterSearchTvShowRecycler.setFlag(0);
			}
			else {
				SelectedNumber = -1;
				adapterSearchTvShowRecycler.setFlag(0);
			}
			if (SelectedNumber == -1)
				search_button.requestFocus();
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
		pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		pDialog.setContentView(R.layout.layout_progress_dilog);
	}

	void DismissProgress(Context c) {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}

	public SpannableString SettextColorNew(String mySearchedString, String myString) {
		SpannableString ss = new SpannableString(myString);
		Pattern pattern = Pattern.compile(mySearchedString);
		Matcher matcher = pattern.matcher(ss);
		while (matcher.find()) {
			ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow_search)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		return ss;
	}

	public static SpannableString Settext_ColorNew(String mySearchedString, String myString, Context context) {
		SpannableString ss = new SpannableString(myString);
		Pattern pattern = Pattern.compile(mySearchedString);
		Matcher matcher = pattern.matcher(ss);
		while (matcher.find()) {
			ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.yellow_search)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		return ss;
	}

	public class DateComparator implements Comparator<SetgetSubchannels> {
		@Override
		public int compare(SetgetSubchannels lhs, SetgetSubchannels rhs) {
			Double distance = Double.valueOf(lhs.getOdid());
			Double distance1 = Double.valueOf(rhs.getOdid());
			if (distance.compareTo(distance1) < 0) {
				return -1;
			} else if (distance.compareTo(distance1) > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public class DateComparator_record implements Comparator<SetgetSearchRecordDates> {
		@Override
		public int compare(SetgetSearchRecordDates lhs, SetgetSearchRecordDates rhs) {
			Double distance = Double.valueOf(lhs.getChannel_odid());
			Double distance1 = Double.valueOf(rhs.getChannel_odid());
			if (distance.compareTo(distance1) < 0) {
				return -1;
			} else if (distance.compareTo(distance1) > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/////////////////////////////Button/////////////


	private int mInsertPos = 0;
	View.OnClickListener btnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v != mBsubmit && v != mBack && v != mBLeft && v != mBRight && v != mBSpace) {
				addText(v);
			} else if (v == mBsubmit) {
				if (edit_text_search.getText().length() >= 3) {
					disableKeyboard();

					Call_api();
				} else {
					Toast toast = Toast.makeText(getActivity(), "אנא רשום לפחות 3 אותיות/מספרים", Toast.LENGTH_SHORT);

					TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
					toastMessage.setTextColor(getResources().getColor(R.color.white));
					toast.show();
				}

			} else if (v == mBack) {
				isBack(v);
			} else if (v == mBLeft){
				//mInsertPos = Math.min(edit_text_search.getText().length(), mInsertPos + 1);
			} else if (v == mBRight){
				//mInsertPos = Math.max(0, mInsertPos - 1);
			} else if (v == mBSpace){
				mInsertPos = Math.min(edit_text_search.getText().length(), mInsertPos);
				edit_text_search.setText(edit_text_search.getText() + " ");
				mInsertPos++;
			}

		}
	};
	View.OnFocusChangeListener btnFocusListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View view, boolean b) {
			if (!b){
				view.animate().scaleX(1).scaleY(1).setDuration(200);
			}else{
				view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);

			}
		}
	};
	View.OnFocusChangeListener btnFocusListener1 = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View view, boolean b) {
			if (!b)
				view.animate().scaleX(1).scaleY(1).setDuration(200);
			else
				view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
		}
	};
	private void isBack(View v) {
		if (isEdit == true) {
			CharSequence cc = edit_text_search.getText();
			if (cc != null && cc.length() > 0) {
				{
					edit_text_search.setText("");
					edit_text_search.append(cc.subSequence(0, cc.length() - 1));
				}

			}
		}
	}

	private void addText(View v) {

		String b = "";
		b = (String) ((Button)v).getText();
		if (b != null) {
			// adding text in Edittext
			edit_text_search.setText(edit_text_search.getText() + b);
			mInsertPos++;
		}
	}
	// enabling customized keyboard
	private void enableKeyboard() {
		isEdit = true;
		mKLayout.setFocusable(false);
		search_button.setFocusable(false);
		edit_text_search.setFocusable(false);
		search_result.setFocusable(false);
		mKLayout.setVisibility(View.VISIBLE);
		mBack.requestFocus();
		edit_text_search.setBackgroundResource(R.drawable.edit_search_focus);
		changeFoscusableState(SEARCH_ALL, false);
	}

	public boolean hideIMEAndEnableFocus() {
		boolean bRet = false;
		int visStatus = mKLayout.getVisibility();
		if (visStatus == View.VISIBLE) {
			disableKeyboard();
			search_button.requestFocus();
			bRet = true;
		}
		return bRet;
	}

	// Disable customized keyboard
	private void disableKeyboard() {
		mKLayout.setVisibility(View.GONE);
		search_button.setFocusable(true);
		edit_text_search.setFocusable(true);
		changeFoscusableState(SEARCH_ALL, true);
		edit_text_search.setBackgroundResource(R.drawable.edit_search);
		edit_text_search.setFocusable(true);
	}

	private void setShowSoftInputOnFocus(Object o, boolean value) {
		try {
			Method setShowSoftInputOnFocus = o.getClass().getMethod("setShowSoftInputOnFocus", boolean.class);
			setShowSoftInputOnFocus.invoke(o,value);
		} catch (Exception ex) {

		}
	}

	private void hideDefaultKeyboard() {
//		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(edit_text_search.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
//		imm.hideSoftInputFromInputMethod(edit_text_search.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
//		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		setShowSoftInputOnFocus(edit_text_search, false);
	}

	public void onFragmentBackState() {
		View v = getView();
		if (v != null)
			((LinearLayout)v.findViewById(R.id.rootview)).setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
	}

	public void onFragmentTopFromBackState() {
		View v = getView();
		if (v != null) {
			((LinearLayout) v.findViewById(R.id.rootview)).setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

			adapterSearchTvShowRecycler.setFlag(2);
			adapterSearchTvShowRecycler.notifyDataSetChanged();

		}
	}
}
