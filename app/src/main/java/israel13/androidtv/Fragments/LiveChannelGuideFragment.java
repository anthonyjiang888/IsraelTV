package israel13.androidtv.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetAllChannels;
import israel13.androidtv.Setter_Getter.SetgetGuide;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomDateComparator;

public class LiveChannelGuideFragment extends Fragment implements IJSONParseListener {
    int sign = -1;
    final int LOAD_GUIDE = 101;
    final int GET_ALLCHANNEL = 102;
    SharedPreferences loginDetails = null;
    ArrayList<SetgetSubchannels> arrChannels;
    ArrayList<SetgetGuide> arrGuides;

    int selectedChannelPos = 0;
    ListView listViewChannels;
    ListView listViewGuides;
    ChannelAdapter channelAdapter;
    GuideAdapter guideAdapter;

    TextView txtCurrentDate;
    ImageView imgChannel;
    TextView txtGuideTime;
    TextView txtGuideName;
    TextView txtGuideDescription;
    TextView txtGuideTopic;
    TextView txtGuideTopicTitle;
    RatingBar ratingBarStars;
    TextView txtGuideTimeLength;
    TextView txtGuideTimeLengthTitle;
    TextView txtGuideRatingBarTitle;
    TextView txtGuideTimeANameSeparator;
    ImageView imgChannelMark;
    ImageView imgButChannelListUp;
    ImageView imgButChannelListDown;
    ImageView imgButGuideListUp;
    ImageView imgButGuideListDown;

    View viewSelectedActivatedChannel;
    View viewLastSelectedChannel = null;
    View viewLastSelectedGuide = null;

    ProgressDialog progressDlg;
    ProgressBar progressBarGuideLoad;

    TextView txtNoChannel;
    TextView txtNoGuide;

    float fChannelListRowCount = 0;
    float fGuideListRowCount = 0;

    Handler mAutoSelectHandler = new Handler();
    Runnable mAutoSelectRunnable = new Runnable() {
        @Override
        public void run() {
            if (listViewChannels != null &&
                    listViewChannels.hasFocus() &&
                    listViewChannels.getSelectedItemPosition() != AdapterView.INVALID_POSITION &&
                    listViewChannels.getSelectedItemPosition() != selectedChannelPos) {
                onChannelListItemClicked(listViewChannels.getSelectedItemPosition());
                listViewChannels.setItemChecked(listViewChannels.getSelectedItemPosition(), true);
            }
        }
    };

    /*
        Method invocation 'getWindowManager' may produce NullPointerException.
        So we use a default value saved at init step of this fragment.
    */
    DisplayMetrics defaultMetrics = new DisplayMetrics();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loginDetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        arrChannels = new ArrayList<>();
        arrGuides = new ArrayList<>();
        GetAllChannelsList("0");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.activity_livechannel_guide, null);

        listViewChannels = (ListView)layout.findViewById(R.id.listview_channels);
        listViewGuides = (ListView)layout.findViewById(R.id.listview_schedules);
        listViewChannels.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listViewGuides.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        listViewChannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAutoSelectHandler.removeCallbacks(mAutoSelectRunnable);
                onChannelListItemClicked(position);
                view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
                if (viewLastSelectedChannel != null && viewLastSelectedChannel != view) {
                    viewLastSelectedChannel.animate().scaleY(1).scaleX(1).setDuration(200);
                }

                viewLastSelectedChannel = view;
            }
        });
        channelAdapter = new ChannelAdapter(getActivity(), arrChannels, R.layout.item_listview_guidepage_channel);
        listViewChannels.setAdapter(channelAdapter);
        //listViewChannels.setSelection(0);
        guideAdapter = new GuideAdapter(getActivity(), arrGuides, R.layout.item_listview_guidepage_guide);
        listViewGuides.setAdapter(guideAdapter);
        imgChannel = (ImageView)layout.findViewById(R.id.img_channel);
        txtGuideTime = (TextView)layout.findViewById(R.id.guidepage_text_guide_time);
        txtGuideName = (TextView)layout.findViewById(R.id.guidepage_text_guide_name);
        txtGuideDescription = (TextView)layout.findViewById(R.id.guidepage_text_guide_description);
        txtGuideTopic = (TextView)layout.findViewById(R.id.guidepage_text_guide_topic);
        txtGuideTopicTitle = (TextView)layout.findViewById(R.id.guidepage_text_guide_topic_title);
        ratingBarStars = (RatingBar) layout.findViewById(R.id.guidepage_ratingbar_guide_rating);
        txtGuideTimeLength = (TextView)layout.findViewById(R.id.guidepage_text_guide_timelength);
        txtGuideTimeLengthTitle = (TextView)layout.findViewById(R.id.guidepage_text_guide_timelength_title);
        txtCurrentDate = (TextView)layout.findViewById(R.id.text_current_date);
        txtGuideRatingBarTitle = (TextView)layout.findViewById(R.id.guidepage_text_ratingbar_title);
        txtGuideTimeANameSeparator = (TextView)layout.findViewById(R.id.guidepage_text_guide_timeAname_separator);
        imgButChannelListDown = (ImageView) layout.findViewById(R.id.img_downarrow_channels);
        imgButChannelListUp = (ImageView) layout.findViewById(R.id.img_uparrow_channels);
        imgButGuideListDown = (ImageView) layout.findViewById(R.id.img_downarrow_schedules);
        imgButGuideListUp = (ImageView) layout.findViewById(R.id.img_uparrow_schedules);
        imgChannelMark = (ImageView) layout.findViewById(R.id.img_channel_mark);

        progressBarGuideLoad = (ProgressBar)layout.findViewById(R.id.guidepage_guidelist_load_progressbar);
        txtNoChannel = (TextView)layout.findViewById(R.id.guidpage_text_no_channel);
        txtNoGuide = (TextView)layout.findViewById(R.id.guidpage_text_no_guide);

        listViewGuides.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //listViewGuides.setItemChecked(position, true);
                listViewGuides.clearChoices();
                showGuideDetail(position);
                if (sign != -1) {
                    view.animate().scaleX(1.05f).scaleY(1f).setDuration(200);
                    if (viewLastSelectedGuide != null && viewLastSelectedGuide != view) {
                        viewLastSelectedGuide.animate().scaleY(1).scaleX(1).setDuration(200);
                    }
                    viewLastSelectedGuide = view;
                }
                sign = 0;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setGuideDetailVisiblity(View.INVISIBLE);
            }
        });
        listViewGuides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showGuideDetail(position);
                //listViewGuides.setSelection(position);
            }
        });
        listViewGuides.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    listViewGuides.clearChoices();
                    if (viewLastSelectedGuide != null){
                        viewLastSelectedGuide.animate().scaleX(1).scaleY(1).setDuration(200);
                        viewLastSelectedGuide = null;
                    }
                }else{
                    if (listViewGuides.getSelectedView() != null) {
                        listViewGuides.getSelectedView().animate().scaleX(1.05f).scaleY(1f).setDuration(200);
                        viewLastSelectedGuide = listViewGuides.getSelectedView();
                    }
                }
                sign = 0;
            }
        });
        listViewChannels.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
                if (totalItemCount == 0 || fChannelListRowCount == 0) {
                    return;
                }
                //Log.e("aaa", "totalItemCount: " + totalItemCount + " firstVisibleItem: " + firstVisibleItem + " visible item: " + visibleItemCount);
                //int k = ((int)fChannelListRowCount == fChannelListRowCount)?(int)fChannelListRowCount:(int)fChannelListRowCount+1;
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (listViewChannels.getLastVisiblePosition() == totalItemCount - 1) {
                                imgButChannelListDown.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, 100);
                } else {
                    imgButChannelListDown.setVisibility(View.VISIBLE);
                }
                if (firstVisibleItem != 0) {
                    imgButChannelListUp.setVisibility(View.VISIBLE);
                } else {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (listViewChannels.getFirstVisiblePosition() == 0) {
                                imgButChannelListUp.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, 100);
                }
            }
        });
        listViewChannels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
                if (viewLastSelectedChannel != null && viewLastSelectedChannel != view) {
                    viewLastSelectedChannel.animate().scaleY(1).scaleX(1).setDuration(200);
                }

                viewLastSelectedChannel = view;
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
                    if (viewLastSelectedChannel != null) {
                        ((ViewGroup) viewLastSelectedChannel).dispatchSetSelected(false);
                    }
                    ((ViewGroup) view).dispatchSetSelected(true);
                    viewLastSelectedChannel = view;
                }

                mAutoSelectHandler.removeCallbacks(mAutoSelectRunnable);
                mAutoSelectHandler.postDelayed(mAutoSelectRunnable, 1000);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAutoSelectHandler.removeCallbacks(mAutoSelectRunnable);
            }
        });
        //Log.e("aaa","scroll listener set.");
        listViewGuides.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
                if (totalItemCount == 0 || fGuideListRowCount == 0) {
                    return;
                }
                int myFirstVisibleItem = firstVisibleItem;
                if (view.getChildCount() != 0) {
                    myFirstVisibleItem += -(view.getChildAt(0).getTop() - 10) / view.getChildAt(0).getHeight();
                    if (myFirstVisibleItem != 0) {
                        imgButGuideListUp.setVisibility(View.VISIBLE);
                    } else {
                        imgButGuideListUp.setVisibility(View.INVISIBLE);
                    }

                    if (view.getLastVisiblePosition() == totalItemCount - 1 &&
                            view.getChildAt(view.getChildCount() - 1).getBottom() < view.getHeight() + 10) {
                        imgButGuideListDown.setVisibility(View.INVISIBLE);//
                    }else{
                        imgButGuideListDown.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        listViewChannels.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onChannelListFocusChanged(hasFocus);
                if (!hasFocus){
                    if (viewLastSelectedChannel != null){
                        viewLastSelectedChannel.animate().scaleX(1).scaleY(1).setDuration(200);
                        viewLastSelectedChannel = null;
                        mAutoSelectHandler.removeCallbacks(mAutoSelectRunnable);
                    }
                }else{
                    if (listViewChannels.getSelectedView() != null) {
                        listViewChannels.getSelectedView().animate().scaleX(1.05f).scaleY(1.05f).setDuration(200);
                        viewLastSelectedChannel = listViewChannels.getSelectedView();
                        mAutoSelectHandler.removeCallbacks(mAutoSelectRunnable);
                        mAutoSelectHandler.postDelayed(mAutoSelectRunnable, 1000);
                    }
                }
            }
        });

        imgButChannelListUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = listViewChannels.getSelectedItemPosition();
                if (pos > 0) {
                    listViewChannels.setSelection(pos-1);
                }
            }
        });
        imgButChannelListDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = listViewChannels.getSelectedItemPosition();
                if (pos < listViewChannels.getCount() - 1) {
                    listViewChannels.setSelection(pos+1);
                }
            }
        });

        imgButChannelListDown.setVisibility(View.INVISIBLE);
        imgButChannelListUp.setVisibility(View.INVISIBLE);
        setChannelDetailVisiblity(View.INVISIBLE);
        setGuideDetailVisiblity(View.INVISIBLE);
        progressBarGuideLoad.setVisibility(View.GONE);

        saveDefaultDisplayParamOnInit();
        return layout;
    }

    @Override
    public void onResume() {
        getView().setFocusableInTouchMode(false);
        getView().requestFocus();
        super.onResume();
    }

    private void showGuideDetail(int position) {
        setGuideDetailVisiblity(View.VISIBLE);
        Glide.with(getActivity())
                .load(Constant.BASE_URL_IMAGE + arrChannels.get(selectedChannelPos).getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .override(200, 200).skipMemoryCache(true)
                .placeholder(R.drawable.channel_placeholder)
                .into(imgChannel);
        String endTime = Constant.getTimeByAdding((arrGuides.get(position).getTimeLength()), arrGuides.get(position).getStartTime());
        txtGuideTime.setText(arrGuides.get(position).getStartTime() + " - " + endTime);

        String strName = arrGuides.get(position).getName();
        String year = arrGuides.get(position).getYear();
        if (year != null && !year.trim().isEmpty()) {
            strName += " (" + year + ")";
        }
        txtGuideName.setText(strName);

        txtGuideDescription.setText(arrGuides.get(position).getDescription().trim());
        txtGuideTopic.setText(arrGuides.get(position).getGenre());
        ratingBarStars.setRating(arrGuides.get(position).getStar());
        txtGuideTimeLength.setText(Constant.hour_min_format(arrGuides.get(position).getTimeLength()));
    }

    private void setChannelDetailVisiblity(int visiblity) {
        txtCurrentDate.setVisibility(visiblity);
        listViewGuides.setVisibility(visiblity);
        imgButGuideListDown.setVisibility(visiblity);
        imgButGuideListUp.setVisibility(visiblity);
    }

    private void setGuideDetailVisiblity(int visiblity) {
        imgChannel.setVisibility(visiblity);
        txtGuideTime.setVisibility(visiblity);
        txtGuideName.setVisibility(visiblity);
        txtGuideDescription.setVisibility(visiblity);
        txtGuideTopic.setVisibility(visiblity);
        txtGuideTopicTitle.setVisibility(visiblity);
        ratingBarStars.setVisibility(visiblity);
        txtGuideTimeLength.setVisibility(visiblity);
        txtGuideTimeLengthTitle.setVisibility(visiblity);
        txtGuideRatingBarTitle.setVisibility(visiblity);
        txtGuideTimeANameSeparator.setVisibility(visiblity);
    }

    private void onChannelListFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            if (listViewChannels != null) {
                View selectedView = listViewChannels.getSelectedView();
                if (selectedView != null) {
                    viewSelectedActivatedChannel = selectedView.findViewById(R.id.item_listview_guidepage_channel_container);
                    viewSelectedActivatedChannel.setSelected(false);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                if (listViewChannels != null && viewSelectedActivatedChannel != null) {
                    viewSelectedActivatedChannel.setSelected(true);
                    viewSelectedActivatedChannel = null;
                }
            }
        }
    }

    private void onChannelListItemClicked(int position) {
        selectedChannelPos = position;
        String channelId = arrChannels.get(position).getSub_channelsid();
        txtCurrentDate.setText(Constant.getDatesWithDaysName(arrChannels.get(position).getTime()));

        Glide.with(this)
                .load(Constant.BASE_URL_IMAGE + arrChannels.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                .override(200, 200).skipMemoryCache(true)
                .placeholder(R.drawable.channel_placeholder)
                .into(imgChannelMark);

        //listViewChannels.setItemChecked(position, true);
        LoadGuide(channelId);
    }

    void GetAllChannelsList(String bIsRadio) {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        parms.putString("sid",loginDetails.getString("sid",""));
        parms.putString("isradio", bIsRadio);
        MyVolley.init(getActivity());
        showProgressDialog();
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "channels.php", GET_ALLCHANNEL, this, parms, false);
    }

    void LoadGuide(String channelId) {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        parms.putString("cid", channelId);
        parms.putString("withdescription", "1");
        MyVolley.init(getActivity());
        progressBarGuideLoad.setVisibility(View.VISIBLE);
        txtNoGuide.setVisibility(View.GONE);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "schbydate.php", LOAD_GUIDE, this, parms, false);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
        if (requestCode == GET_ALLCHANNEL) {
            dismissProgressDialog();
        } else if (requestCode == LOAD_GUIDE) {
            progressBarGuideLoad.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == GET_ALLCHANNEL) {
            JSONArray results = response.optJSONArray("results");
            ArrayList<SetgetAllChannels> channels = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                JSONArray subChannelsJObj;
                SetgetAllChannels channel = new SetgetAllChannels();
                JSONObject resultobj = results.optJSONObject(i);
                channel.setOdid(resultobj.optString("odid"));
                subChannelsJObj = resultobj.optJSONArray("channels");
                ArrayList<SetgetSubchannels> subChannels = new ArrayList<>();
                for (int j = 0; j < subChannelsJObj.length(); j++) {
                    SetgetSubchannels subChannel = new SetgetSubchannels();
                    JSONObject subChannelObj = subChannelsJObj.optJSONObject(j);
                    subChannel.setSub_channelsid(subChannelObj.optString("id"));
                    subChannel.setImage(subChannelObj.optString("image"));
                    subChannel.setName(subChannelObj.optString("name"));
                    JSONObject showObj = subChannelObj.optJSONObject("show");
                    subChannel.setRunning_epg_name(showObj.optString("name"));
                    subChannel.setTime(showObj.optString("rdate"));
                    subChannel.setEpg_time(showObj.optString("time"));
                    subChannel.setEpg_length(showObj.optString("lengthtime"));
                    subChannels.add(subChannel);
                }
                channel.setSubchannelsList(subChannels);
                channels.add(channel);
            }
            Collections.sort(channels, new CustomDateComparator.AllChannelsDateComparator());

            for (int i = 0; i < channels.size(); i ++) {
                arrChannels.addAll(channels.get(i).getSubchannelsList());
            }

            channelAdapter.notifyDataSetChanged();
            dismissProgressDialog();
            if (arrChannels.size() > 0) {
                listViewChannels.requestFocus();
                onChannelListItemClicked(0);
                listViewChannels.setItemChecked(0, true);
                setChannelDetailVisiblity(View.VISIBLE);
                txtNoChannel.setVisibility(View.GONE);
            } else {
                txtNoChannel.setVisibility(View.VISIBLE);
            }
            setChannelsUpDownArrowVisiblity();
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        if (requestCode == LOAD_GUIDE) {
            arrGuides.clear();
            JSONArray guideListJObj = response;
            if (guideListJObj != null && guideListJObj.length() != 0) {
                for (int i = 0; i < guideListJObj.length(); i++) {
                    JSONObject guideObj = guideListJObj.optJSONObject(i);
                    long curTime = new Date().getTime() / 1000;
                    long rdateTime = Long.valueOf(guideObj.optString("rdatetime"));
                    long lengthTime = Long.valueOf(guideObj.optString("lengthtime"));
                    if (rdateTime + lengthTime >= curTime) {
                        SetgetGuide guide = new SetgetGuide();
                        guide.setName(guideObj.optString("name"));
                        guide.setDescription(guideObj.optString("description"));
                        guide.setStar(guideObj.optInt("star"));
                        guide.setStartTime(guideObj.optString("time"));
                        guide.setRDateTime(rdateTime);
                        guide.setTimeLength(guideObj.optInt("lengthtime"));
                        guide.setGenre(guideObj.optString("genre", "מידע אינו זמין"));
                        guide.setYear(guideObj.optString("year", ""));
                        arrGuides.add(guide);
                    }
                }
            }

            guideAdapter.notifyDataSetChanged();
            sign = -1;
            listViewGuides.setSelection(0);


            progressBarGuideLoad.setVisibility(View.INVISIBLE);
            if (arrGuides.size() > 0) {
                // GGG
//                listViewGuides.setItemChecked(0, true);
//                listViewGuides.setSelection(0);
//                listViewGuides.requestFocus();
//                onChannelListFocusChanged(false);
                showGuideDetail(0);
                txtNoGuide.setVisibility(View.GONE);
            } else {
                txtNoGuide.setVisibility(View.VISIBLE);
            }
            setGuidesUpDownArrowVisiblity();
        }
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }

    private void showProgressDialog() {
        if (progressDlg == null) {
            progressDlg = new ProgressDialog(getActivity());
        }
        progressDlg.show();
        progressDlg.setCancelable(true);
        progressDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDlg.setContentView(R.layout.layout_progress_dilog);
    }

    private void dismissProgressDialog() {
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();
    }

    private void setChannelsUpDownArrowVisiblity() {
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = new DisplayMetrics();

                /*
                    Method invocation 'getWindowManager' may produce NullPointerException.
                    So we use a default value saved at init step of this fragment.
                 */
                try {
                    Display display = ((Activity) getActivity()).getWindowManager().getDefaultDisplay();
                    display.getMetrics(metrics);
                } catch (Exception e) {
                    metrics = defaultMetrics;
                    if (metrics == null) return;
                }

                fChannelListRowCount = (float)(listViewChannels.getHeight() * 1.0 / (metrics.density * 51));
                if ((int) fChannelListRowCount < arrChannels.size()) {
                    imgButChannelListDown.setVisibility(View.VISIBLE);
                } else {
                    imgButChannelListDown.setVisibility(View.INVISIBLE);
                }
                imgButChannelListUp.setVisibility(View.INVISIBLE);
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setGuidesUpDownArrowVisiblity() {
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = new DisplayMetrics();

                /*
                    Method invocation 'getWindowManager' may produce NullPointerException.
                    So we use a default value saved at init step of this fragment.
                 */
                try {
                    Display display = ((Activity) getActivity()).getWindowManager().getDefaultDisplay();
                    display.getMetrics(metrics);
                } catch (Exception e) {
                    metrics = defaultMetrics;
                    if (metrics == null) return;
                }
                fGuideListRowCount = (float)(listViewGuides.getHeight() * 1.0 / (metrics.density * (35 + 5)));
                int k = ((int)fGuideListRowCount == fGuideListRowCount || (fGuideListRowCount - (int)fGuideListRowCount) < 0.06)?(int)fGuideListRowCount:(int)fGuideListRowCount+1;
                if ((int) k <= arrGuides.size()) {
                    imgButGuideListDown.setVisibility(View.VISIBLE);
                } else {
                    imgButGuideListDown.setVisibility(View.INVISIBLE);
                }
                imgButGuideListUp.setVisibility(View.INVISIBLE);
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private class ChannelAdapter extends BaseAdapter {
        private ArrayList<SetgetSubchannels> channels;
        private Context context;
        private int childLayoutId;

        public ChannelAdapter(Context c, ArrayList<SetgetSubchannels> a, int l) {
            context = c;
            channels = a;
            childLayoutId = l;
        }

        public class ViewHolder
        {
            public TextView txtChannelName;
            public ImageView imgChannelAvatar;

            public ViewHolder(View itemView) {
                txtChannelName=(TextView)itemView.findViewById(R.id.text_guide_channel_name);
                imgChannelAvatar = (ImageView) itemView.findViewById(R.id.img_guide_channel_avatar);
            }
        }

        @Override
        public int getCount() {
            return channels.size();
        }

        @Override
        public Object getItem(int position) {
            return channels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_listview_guidepage_channel, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }
            holder.txtChannelName.setText(channels.get(position).getName());
            Glide.with(context)
                    .load(Constant.BASE_URL_IMAGE + channels.get(position).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                    .override(200, 200).skipMemoryCache(true)
                    .placeholder(R.drawable.channel_placeholder)
                    .into(holder.imgChannelAvatar);
            view.setPadding(25,2,25,2);
            return view;
        }
    }

    private class GuideAdapter extends BaseAdapter {
        private ArrayList<SetgetGuide> guides;
        private Context context;
        private int childLayoutId;

        public GuideAdapter(Context c, ArrayList<SetgetGuide> a, int l) {
            context = c;
            guides = a;
            childLayoutId = l;
        }

        public class ViewHolder
        {
            public TextView txtGuideTime;
            public TextView txtGuideName;
            public ImageView imgGuideLive;
            public FrameLayout itemContainerLayout;

            public ViewHolder(View itemView) {
                txtGuideTime=(TextView)itemView.findViewById(R.id.guidepage_guidelist_item_text_guide_time);
                txtGuideName = (TextView) itemView.findViewById(R.id.guidepage_guidelist_item_text_guide_name);
                itemContainerLayout = (FrameLayout)itemView.findViewById(R.id.guidepage_guidelist_item_layout_frame_container);
                imgGuideLive = (ImageView)itemView.findViewById(R.id.guidepage_guidelist_item_img_live);
            }
        }

        @Override
        public int getCount() {
            return guides.size();
        }

        @Override
        public Object getItem(int position) {
            return guides.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_listview_guidepage_guide, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }
            String endTime = Constant.getTimeByAdding((guides.get(position).getTimeLength()), guides.get(position).getStartTime());
            holder.txtGuideTime.setText(guides.get(position).getStartTime() + " - " + endTime);

            String strName = guides.get(position).getName();
            String year = guides.get(position).getYear();
            if (year != null && !year.trim().isEmpty()) {
                strName += " (" + year + ")";
            }
            holder.txtGuideName.setText(strName);

            if (position % 2 == 1) {
                holder.itemContainerLayout.setBackgroundColor(context.getResources().getColor(R.color.guide_even));
            } else {
                holder.itemContainerLayout.setBackgroundColor(context.getResources().getColor(R.color.guide_odd));
            }
            if (guides.get(position).getRDateTime() < (new Date().getTime() / 1000)) {
                //Log.e("aaa", "live position:" + position);
                holder.imgGuideLive.setVisibility(View.VISIBLE);
            } else {
                holder.imgGuideLive.setVisibility(View.GONE);
            }

            return view;
        }
    }

    /*
        Method invocation 'getWindowManager' may produce NullPointerException.
        So we use a default value saved at init step of this fragment.
    */
    private void saveDefaultDisplayParamOnInit() {

        WindowManager wmService = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager wmNormal = ((Activity)getActivity()).getWindowManager();
        WindowManager wm = wmNormal != null ? wmNormal : wmService;
        Display display = wm.getDefaultDisplay();
        display.getMetrics(defaultMetrics);
    }
}
