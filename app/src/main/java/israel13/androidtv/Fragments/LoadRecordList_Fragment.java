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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Records_Play_Activity;
import israel13.androidtv.Adapter.LoadRecordListAdapter;
import israel13.androidtv.Adapter.Season_list_adapter;
import israel13.androidtv.CallBacks.Keydown;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetLoadScheduleRecord;
import israel13.androidtv.Utils.Constant;

import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;

/**
 * Created by krishanu on 25/05/17.
 */
public class LoadRecordList_Fragment extends Fragment implements IJSONParseListener, Keydown {
	private boolean flag_buttonFocus = false;
	private TextView channelname_load_recordlist;
	private ImageView back_record;
	private ImageView channel_icon_load_recordlist, up_arrow_recording_date, down_arrow_recording_date, up_arrow_recording_show, down_arrow_recording_show;
	private ListView list_date_recording;
	private ListView list_view_schedulelist_load_recordlist;
	private LinearLayout llv_list_data;
	private TextView tv_nodata;
	private LoadRecordList_Fragment fragment;
	int sign = 0;
	int ProgressSign = 0;
	int ClickId = 0;
	private int LOAD_RECORD_SCHEDULE = 115;
	private int LOAD_RECORD_WEEKDAY = 116;

	private ProgressDialog pDialog;
	private SharedPreferences logindetails;
	public static ArrayList<SetgetLoadScheduleRecord> LoadScheduleRecordsList;
	private ArrayList<SetgetLoadScheduleRecord> arrListViewScheduleRecords;

	private ArrayList<String> datesList;
	private int check = 0, ADD_FAVORITES = 160;
	private String rdatetime = "";
	private Season_list_adapter adapter;
	private LoadRecordListAdapter show_list_adapter = null;
	private ArrayList<String> dateListwithDays;
	private int number_of_rows;
	public static String flag_postion = "";
	public static String date = "";
	public int lastpostion_show_list = 0, flag_down = 1;
	boolean isFirstEvent = false;
	int season_list_selected_Item_Id = 0;
	View tempDateRecordingView = null;
	View tempScheduleListView = null;
	final int PAGE_ROW_COUNT = 20;
	private boolean bScrollLoading = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.recording_details, null);

		HomeActivity.mContext = LoadRecordList_Fragment.this;

		channelname_load_recordlist = (TextView) layout.findViewById(R.id.channelname_load_recordlist);
		channel_icon_load_recordlist = (ImageView) layout.findViewById(R.id.channel_icon_load_recordlist);
		up_arrow_recording_date = (ImageView) layout.findViewById(R.id.up_arrow_recording_date);
		down_arrow_recording_date = (ImageView) layout.findViewById(R.id.down_arrow_recording_date);
		up_arrow_recording_show = (ImageView) layout.findViewById(R.id.up_arrow_recording_show);
		down_arrow_recording_show = (ImageView) layout.findViewById(R.id.down_arrow_recording_show);
		back_record = (ImageView) layout.findViewById(R.id.back_record);

		down_arrow_recording_date.setVisibility(View.VISIBLE);
		// down_arrow_recording_show.setVisibility(View.VISIBLE);

		list_date_recording = (ListView) layout.findViewById(R.id.list_date_recording);
		list_view_schedulelist_load_recordlist = (ListView) layout.findViewById(R.id.list_recording_show);

		LoadScheduleRecordsList = new ArrayList<SetgetLoadScheduleRecord>();
		arrListViewScheduleRecords = new ArrayList<SetgetLoadScheduleRecord>();
		show_list_adapter = new LoadRecordListAdapter(getActivity(), R.layout.child_load_recordlist, arrListViewScheduleRecords);
		list_view_schedulelist_load_recordlist.setAdapter(show_list_adapter);

		tv_nodata = (TextView) layout.findViewById(R.id.tv_nodata);
		llv_list_data = (LinearLayout) layout.findViewById(R.id.llv_list_data);

		fragment = new LoadRecordList_Fragment();

		dateListwithDays = new ArrayList<>();
		datesList = new ArrayList<>();

		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);

		ImageCacheUtil.with(getActivity())
				.load(Constant.BASE_URL_IMAGE + AllChannels_Fragment.channel_icon)
				.resize(200,200)
				.cacheUsage(false, true)
				.into(channel_icon_load_recordlist);

		channelname_load_recordlist.setText("הקלטות - " + AllChannels_Fragment.channel_name);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float density = getResources().getDisplayMetrics().density;
		// float dpHeight = outMetrics.heightPixels / density;
		float dpHeight = outMetrics.heightPixels;
		float dpWidth = outMetrics.widthPixels / density;
		float without_margin = dpHeight - (30);
		number_of_rows = (int) without_margin / 300;

		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));

		Call_api();

		back_record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity homeActivity = getActivity();
				homeActivity.getSupportFragmentManager().popBackStack();
			}
		});
		back_record.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					flag_buttonFocus = false;
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					flag_buttonFocus = true;
					view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);
				}

				channel_icon_load_recordlist.setFocusable(false);
			}
		});
		list_date_recording.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (list_date_recording.getChildCount() == 0)
					return;

				View first_item = list_date_recording.getChildAt(0);
				isFirstEvent = true;
				list_date_recording.setItemChecked(season_list_selected_Item_Id, true);
				if (list_date_recording.getSelectedView() != null)
					list_date_recording.getSelectedView().findViewById(R.id.season_name).setSelected(false);
				list_date_recording.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		list_date_recording.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					if (tempDateRecordingView != null) {
						tempDateRecordingView.animate().scaleX(1).scaleY(1).setDuration(200);
						tempDateRecordingView = null;
					}
				}else{
					if (list_date_recording.getSelectedView() != null){
						tempDateRecordingView = list_date_recording.getSelectedView();
						tempDateRecordingView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
					}
				}
				if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT)
					return;
				if (!hasFocus) {
					if (list_date_recording.getSelectedView() != null)
						list_date_recording.getSelectedView().findViewById(R.id.season_name).setSelected(false);
				}else {
					if (list_date_recording.getSelectedView() != null) {
						list_date_recording.getSelectedView().findViewById(R.id.season_name).setSelected(true);
					}
				}
			}
		});

		list_date_recording.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				Log.e("ListView", "ItemSelected" + position);
				list_date_recording.smoothScrollToPosition(position);
				view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
				if (tempDateRecordingView != null && tempDateRecordingView != view) {
					tempDateRecordingView.animate().scaleY(1).scaleX(1).setDuration(200);
				}

				tempDateRecordingView = view;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.e("ListView", "ItemNoSelected");
			}
		});

		list_date_recording.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				season_list_selected_Item_Id = position;
				lastpostion_show_list = 0;
				date = Constant.formateDateFromstring("dd/MM/yyyy", "dd-MM-yyyy", datesList.get(position));
				LoadScheduleRecord(date);
				view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
				ClickId = list_date_recording.getSelectedItemPosition();
				if (tempDateRecordingView != null && tempDateRecordingView != view) {
					tempDateRecordingView.animate().scaleY(1).scaleX(1).setDuration(200);
				}

				tempDateRecordingView = view;
			}
		});

		list_view_schedulelist_load_recordlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Records_Play_Activity.flag_delete = true;
				Records_Play_Activity.current_position = position;
				Records_Play_Activity.rdatetime = LoadScheduleRecordsList.get(position).getRdatetime();
				Records_Play_Activity.show_pic = LoadScheduleRecordsList.get(position).getShowpic();
				Records_Play_Activity.rating_record = LoadScheduleRecordsList.get(position).getStar();
				Records_Play_Activity.isSearch = false;
				startActivity(new Intent(getActivity(), Records_Play_Activity.class));
			}
		});

		list_view_schedulelist_load_recordlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
				if (tempScheduleListView != null && tempScheduleListView != view) {
					tempScheduleListView.animate().scaleY(1).scaleX(1).setDuration(200);
				}

				tempScheduleListView = view;
				sign = 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		list_view_schedulelist_load_recordlist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus){
					if (tempScheduleListView != null){
						tempScheduleListView.animate().scaleX(1).scaleY(1).setDuration(200);
						tempScheduleListView = null;
						sign = 0;
					}
					list_view_schedulelist_load_recordlist.clearChoices();
				} else{
					if (list_view_schedulelist_load_recordlist.getSelectedView() != null) {
						list_view_schedulelist_load_recordlist.getSelectedView().animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
						tempScheduleListView = list_view_schedulelist_load_recordlist.getSelectedView();
						sign = 1;
					}
				}
				flag_buttonFocus = false;
			}
		});

		list_view_schedulelist_load_recordlist.setOnScrollListener(new AbsListView.OnScrollListener() {
			private int mLastFirstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				// up_arrow_recording_show.setVisibility(View.VISIBLE);

				/*if (mLastFirstVisibleItem < firstVisibleItem) {
					Log.i("SCROLLING DOWN", "TRUE");
					up_arrow_recording_show.setVisibility(View.VISIBLE);
					down_arrow_recording_show.setVisibility(View.VISIBLE);
				}
				if (mLastFirstVisibleItem > firstVisibleItem) {
					Log.i("SCROLLING UP", "TRUE");
					up_arrow_recording_show.setVisibility(View.VISIBLE);
					down_arrow_recording_show.setVisibility(View.VISIBLE);
				}
				if (firstVisibleItem > 0) {
					up_arrow_recording_show.setVisibility(View.VISIBLE);
					down_arrow_recording_show.setVisibility(View.VISIBLE);
				}
				if (firstVisibleItem == 0) {
					up_arrow_recording_show.setVisibility(View.INVISIBLE);
					down_arrow_recording_show.setVisibility(View.VISIBLE);
				}

				if (firstVisibleItem == totalItemCount - number_of_rows && totalItemCount == LoadScheduleRecordsList.size()) {
					up_arrow_recording_show.setVisibility(View.VISIBLE);
					down_arrow_recording_show.setVisibility(View.INVISIBLE);
					if (firstVisibleItem == 0) {
						up_arrow_recording_show.setVisibility(View.INVISIBLE);
					}
					flag_postion = "last";
				}

				if (totalItemCount <= number_of_rows) {
					up_arrow_recording_show.setVisibility(View.INVISIBLE);
					down_arrow_recording_show.setVisibility(View.INVISIBLE);
					flag_postion = "last";
				}

				if (visibleItemCount > (number_of_rows + 1)) {
					up_arrow_recording_show.setVisibility(View.VISIBLE);
				}

				mLastFirstVisibleItem = firstVisibleItem;*/

				if (totalItemCount == 0)
					return;

				if (firstVisibleItem + visibleItemCount == totalItemCount &&
						view.getChildAt(view.getChildCount() - 1).getBottom() < view.getHeight() + 10) {
					if (list_view_schedulelist_load_recordlist.getLastVisiblePosition() == totalItemCount - 1) {
						down_arrow_recording_show.setVisibility(View.INVISIBLE);
					}
				} else {
					down_arrow_recording_show.setVisibility(View.VISIBLE);
				}
				if (firstVisibleItem != 0) {
					up_arrow_recording_show.setVisibility(View.VISIBLE);
				} else {
					if (list_view_schedulelist_load_recordlist.getFirstVisiblePosition() == 0) {
						if (list_view_schedulelist_load_recordlist.getChildAt(0).getTop() == 0) {
							up_arrow_recording_show.setVisibility(View.INVISIBLE);
						} else {
							up_arrow_recording_show.setVisibility(View.VISIBLE);
						}
					}
				}

				if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
					//Log.e("...", "Last Item Wow !");
					//Do pagination.. i.e. fetch new data
					if (loadPageData() && !bScrollLoading) {
						bScrollLoading = true;
						ShowProgressDilog(getActivity());
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								show_list_adapter.notifyDataSetChanged();
								DismissProgress(getActivity());
								bScrollLoading = false;
							}
						}, 1000);
					}
				}
			}
		});

		list_date_recording.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(final AbsListView view, int firstVisibleItem,
								 int visibleItemCount, final int totalItemCount) {
				if (totalItemCount == 0) {
					return;
				}
				//Log.e("aaa", "totalItemCount: " + totalItemCount + " firstVisibleItem: " + firstVisibleItem + " visible item: " + visibleItemCount);
				//int k = ((int)fChannelListRowCount == fChannelListRowCount)?(int)fChannelListRowCount:(int)fChannelListRowCount+1;
				if (firstVisibleItem + visibleItemCount == totalItemCount &&
						view.getChildAt(view.getChildCount() - 1).getBottom() < view.getHeight() + 10) {
					if (list_date_recording.getLastVisiblePosition() == totalItemCount - 1) {
						down_arrow_recording_date.setVisibility(View.INVISIBLE);
					}
				} else {
					down_arrow_recording_date.setVisibility(View.VISIBLE);
				}
				if (firstVisibleItem != 0) {
					up_arrow_recording_date.setVisibility(View.VISIBLE);
				} else {
					if (list_date_recording.getFirstVisiblePosition() == 0 &&
							list_date_recording.getChildAt(0).getTop() == 4) {
						up_arrow_recording_date.setVisibility(View.INVISIBLE);
					}
				}
			}
		});

		return layout;
	}

	@Override
	public void onResume() {
		getView().setFocusableInTouchMode(false);
		getView().requestFocus();

		list_view_schedulelist_load_recordlist.requestFocus();
		super.onResume();
	}

	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {
			LoadWeekDayByChannel();
		} else {
			back_record.setFocusable(true);
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

	void LoadWeekDayByChannel() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("channel", AllChannels_Fragment.channel_id);
		MyVolley.init(getActivity());
		MyVolley.getRequestQueue().getCache().clear();
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "weekday.php", LOAD_RECORD_WEEKDAY, this, parms, false);
	}

	void LoadScheduleRecord(String date) {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		//  parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("cid", AllChannels_Fragment.channel_id);
		if (!date.equalsIgnoreCase("")) {
			parms.putString("date", date);
		}
		// parms.putString("date", date);
		parms.putString("recordwithdate", "1");
		parms.putString("withdescription", "1");
		MyVolley.init(getActivity());
		MyVolley.getRequestQueue().getCache().clear();
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "schbydate.php", LOAD_RECORD_SCHEDULE, this, parms, false);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());
		back_record.setFocusable(true);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		DismissProgress(getActivity());
		back_record.setFocusable(true);

		if (requestCode == LOAD_RECORD_WEEKDAY) {
			System.out.println("Response for weekday --------------------" + response.toString());
			dateListwithDays.clear();
			try {
				JSONArray weekdays = response.getJSONArray("weekday");
				for (int i = 0; i < weekdays.length(); i++) {
					JSONObject object = weekdays.getJSONObject(i);

					String date = object.getString("date") + "/" + object.getString("year");
					String day = "יום " + object.getJSONObject("week").getString("he");
					dateListwithDays.add(day + " - " + date);
					datesList.add(date);
				}
				adapter = new Season_list_adapter(getActivity(), R.layout.record_date_day, dateListwithDays, true);
				list_date_recording.setAdapter(adapter);
				Constant.setListViewHeightBasedOnItems(list_date_recording, 7);

				season_list_selected_Item_Id = 0;
				date = Constant.formateDateFromstring("dd/MM/yyyy", "dd-MM-yyyy", datesList.get(season_list_selected_Item_Id));
				LoadScheduleRecord(date);
			} catch (JSONException e) {
				e.printStackTrace();
				llv_list_data.setVisibility(View.GONE);
				tv_nodata.setVisibility(View.VISIBLE);
				tv_nodata.requestFocus();
			}
		}else if (requestCode == LOAD_RECORD_SCHEDULE) {
			System.out.println("Response for load record list --------------------" + response.toString());
			if (LoadScheduleRecordsList.size() > 0) {
				LoadScheduleRecordsList.clear();
			}
			String wday = "";

			try {
				JSONArray scheds = response.getJSONArray("scheds");
				for (int i = 0; i < scheds.length(); i++) {
					try {
						JSONObject jsonObject = scheds.getJSONObject(i);

						SetgetLoadScheduleRecord setgetLoadScheduleRecord = new SetgetLoadScheduleRecord();
						setgetLoadScheduleRecord.setChannel(jsonObject.getString("channel"));
						setgetLoadScheduleRecord.setRdatetime(jsonObject.getString("rdatetime"));
						setgetLoadScheduleRecord.setTime(jsonObject.getString("time"));
						setgetLoadScheduleRecord.setName(jsonObject.getString("name"));
						setgetLoadScheduleRecord.setWday(jsonObject.getString("wday"));
						wday = jsonObject.getString("wday");
						if (jsonObject.getString("genre").equalsIgnoreCase("")) {
							setgetLoadScheduleRecord.setGenre("מידע אינו זמין");
						} else {
							setgetLoadScheduleRecord.setGenre(jsonObject.getString("genre"));
						}
						setgetLoadScheduleRecord.setRdate(jsonObject.getString("rdate"));
						setgetLoadScheduleRecord.setLengthtime(jsonObject.getString("lengthtime"));
						setgetLoadScheduleRecord.setWeekno(jsonObject.getString("weekno"));
						setgetLoadScheduleRecord.setIsinfav(jsonObject.getString("isinfav"));
						setgetLoadScheduleRecord.setStar(jsonObject.getString("star"));
						setgetLoadScheduleRecord.setStartotal(jsonObject.getString("startotal"));
						setgetLoadScheduleRecord.setIsradio(jsonObject.getString("isradio"));
						setgetLoadScheduleRecord.setIshd(jsonObject.getString("ishd"));
						setgetLoadScheduleRecord.setLogo(jsonObject.getString("logo"));
						setgetLoadScheduleRecord.setDescription(jsonObject.getString("description"));
						setgetLoadScheduleRecord.setShowpic(jsonObject.getString("showpic"));
						setgetLoadScheduleRecord.setYear(jsonObject.optString("year", ""));
						LoadScheduleRecordsList.add(setgetLoadScheduleRecord);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (LoadScheduleRecordsList.size() > 0) {
				llv_list_data.setVisibility(View.VISIBLE);
				tv_nodata.setVisibility(View.GONE);

				if (lastpostion_show_list == 0) {
					arrListViewScheduleRecords.clear();
					if (LoadScheduleRecordsList.size() > number_of_rows) {
						down_arrow_recording_show.setVisibility(View.VISIBLE);
					} else down_arrow_recording_show.setVisibility(View.INVISIBLE);
				} else {
					if (LoadScheduleRecordsList.size() > arrListViewScheduleRecords.size()) {
						down_arrow_recording_show.setVisibility(View.VISIBLE);
					} else down_arrow_recording_show.setVisibility(View.INVISIBLE);
				}
				if (loadPageData()) {
					show_list_adapter.notifyDataSetChanged();
				}

				list_view_schedulelist_load_recordlist.requestFocus();
				if (lastpostion_show_list != 0) {
					list_view_schedulelist_load_recordlist.setSelection(lastpostion_show_list);
				} else {
					list_view_schedulelist_load_recordlist.setSelection(0);
				}

				try {
					Constant.setListViewHeightBasedOnItems(list_view_schedulelist_load_recordlist, number_of_rows);
				} catch (Exception e) {
					Constant.setListViewHeightBasedOnItems(list_view_schedulelist_load_recordlist, LoadScheduleRecordsList.size());
				}
			} else {
				llv_list_data.setVisibility(View.GONE);
				tv_nodata.setVisibility(View.VISIBLE);
				tv_nodata.requestFocus();
			}
		}
	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {
	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {

	}

	private boolean loadPageData() {
		int loadCount = PAGE_ROW_COUNT;
		int origCnt = arrListViewScheduleRecords.size();
		if (LoadScheduleRecordsList.size() > 0 && LoadScheduleRecordsList.size() > arrListViewScheduleRecords.size()) {
			int start = arrListViewScheduleRecords.size();
			int end = start + loadCount;
			if (end > LoadScheduleRecordsList.size()) end = LoadScheduleRecordsList.size();
			arrListViewScheduleRecords.addAll(LoadScheduleRecordsList.subList(start, end));
		} else {
		}
		return !(origCnt == arrListViewScheduleRecords.size());
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


  /*  public static List<String> GetNextSevenDays(String dt) {

        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        // Start date

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format1.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        *//*calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);*//*

        List<String> days1 = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            days1.add(format1.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, -1);

        }


        return days1;
    }*/

	public ArrayList<String> GetNextSevenDays(String dt) {

		DateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

		// Start date

		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(format1.parse(dt));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/*calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);*/

		StringBuilder builder;

		ArrayList<String> days1 = new ArrayList<>();
		for (int i = 0; i < 14; i++) {

			builder = new StringBuilder();
			days1.add(format1.format(calendar.getTime()));

			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			if (dayOfWeek == 1) {
				builder.append("יום ").append("ראשון").append("  - ").append(format1.format(calendar.getTime()));
			} else if (dayOfWeek == 2) {
				builder.append("יום ").append("שני").append("      - ").append(format1.format(calendar.getTime()));
			} else if (dayOfWeek == 3) {
				builder.append("יום ").append("שלישי").append(" - ").append(format1.format(calendar.getTime()));
			} else if (dayOfWeek == 4) {
				builder.append("יום ").append("רביעי").append("  - ").append(format1.format(calendar.getTime()));
			} else if (dayOfWeek == 5) {
				builder.append("יום ").append("חמישי").append(" - ").append(format1.format(calendar.getTime()));
			} else if (dayOfWeek == 6) {
				builder.append("יום ").append("שישי").append("  - ").append(format1.format(calendar.getTime()));
			} else {
				builder.append("יום ").append("שבת").append("  - ").append(format1.format(calendar.getTime()));
			}

			calendar.add(Calendar.DAY_OF_MONTH, -1);

			dateListwithDays.add(builder.toString());

		}
		dateListwithDays.size();

		return days1;
	}

	@Override
	public void OnKeydown() {

//		if (flag_down == KeyEvent.KEYCODE_SOFT_RIGHT) {//KEYCODE_SOFT_RIGHT = 2 : why?
		if (flag_down == 1) {
			SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
			df2.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
			String curDate = df2.format(new Date());

			try {
				if (show_list_adapter != null) {
					//lastpostion_show_list = show_list_adapter.getCount()-1;
					lastpostion_show_list = list_view_schedulelist_load_recordlist.getSelectedItemPosition();
					//if (curDate.equalsIgnoreCase(date) || date.equalsIgnoreCase("")) {
					if (ClickId == 0 && sign == 1){
						if (list_view_schedulelist_load_recordlist.getSelectedItemPosition() == show_list_adapter.getCount() - 1) {
//							if (flag_postion.equalsIgnoreCase("last")) {
							LoadScheduleRecord(date);
							ProgressSign = 1;
//							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			flag_down = 0;
		}
		flag_down++;
		if (list_date_recording.hasFocus())
			return;
		if (((HomeActivity)getActivity()).isRadioFragment == true)
		{
			channel_icon_load_recordlist.setFocusable(true);
			channel_icon_load_recordlist.requestFocus();
			((HomeActivity)getActivity()).isRadioFragment = false;
			return;
		}
		if (flag_buttonFocus == true)
			return;


		if (list_view_schedulelist_load_recordlist.hasFocus() && list_view_schedulelist_load_recordlist.getSelectedItemPosition() == arrListViewScheduleRecords.size() - 1 && ClickId != 0)
			return;
		list_view_schedulelist_load_recordlist.requestFocus();
		if (list_view_schedulelist_load_recordlist.getChildCount() == 5)
			list_view_schedulelist_load_recordlist.setSelection(list_view_schedulelist_load_recordlist.getFirstVisiblePosition() + 1);
		else if (list_view_schedulelist_load_recordlist.getFirstVisiblePosition() == 0 && ProgressSign != 1)
			list_view_schedulelist_load_recordlist.setSelection(list_view_schedulelist_load_recordlist.getFirstVisiblePosition());
		else if (ProgressSign != 1)
			list_view_schedulelist_load_recordlist.setSelection(list_view_schedulelist_load_recordlist.getFirstVisiblePosition() + 1);
		channel_icon_load_recordlist.setFocusable(false);
		ProgressSign = 0;

	}
}
