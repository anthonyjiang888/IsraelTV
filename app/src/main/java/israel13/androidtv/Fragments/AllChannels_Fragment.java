package israel13.androidtv.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import android.util.DisplayMetrics;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Adapter.AllRadioRecyclerviewAdapter;
import israel13.androidtv.Adapter.AllchannelRecyclerviewAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetAllChannels;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomDateComparator;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.RtlGridLayoutManager;

/**
 * Created by puspak on 20/06/17.
 */
public class AllChannels_Fragment extends Fragment implements IJSONParseListener {

    public static int itemPosition = -1;
    private int GET_ALLCHANNEL = 104;
    private int GET_ALL_RADIO = 105;
    private int GET_SYSTEM_MSG = 106;

    private ProgressDialog pDialog;
    private ArrayList<SetgetAllChannels> allChannelsArrayList;
    private ArrayList<SetgetAllChannels> allRadioArrayList;
    private ArrayList<SetgetSubchannels> allsuSubchannelsArrayList;
    private SharedPreferences logindetails;
    private AllchannelRecyclerviewAdapter channel_adapter;
    private AllRadioRecyclerviewAdapter radio_adapter;
    private int recordAdapterId = 0;
    private TextView system_msg;
    private String system_msg_text = "";
    private String system_msg_title = "";
    private int mWidth = 0;
    private int mSpanCnt = 0;

    public static String channel_id = "", channel_name = "", channel_icon = "";
    public static String is_infav = "", is_radio = "0";
    public LinearLayout livetv_container, llay_system_msg;
    public static int flag_back = 0;
    private TextView system_msg_text_view;
    private TextView system_msg_title_view;
    private boolean bFragmentCreated = false;

    private ArrayList<CustomRecyclerView> arrChannelRecyclerViews;
    private ArrayList<CustomRecyclerView> arrRadioRecyclerViews;
    private ArrayList<TextView> arrChannelTextViews;
    private ArrayList<TextView> arrRadioTextViews;

    private boolean bFromActivity = false;
    private AlertDialog tryAgainDialog = null;
    private int mSpaceSize = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        allChannelsArrayList = new ArrayList<>();
        allRadioArrayList = new ArrayList<>();
        channel_adapter = null;
        radio_adapter = null;
        arrChannelRecyclerViews = new ArrayList<>();
        arrRadioRecyclerViews = new ArrayList<>();
        arrChannelTextViews = new ArrayList<>();
        arrRadioTextViews = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemPosition = -1;
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.activity_allchannels, null);
        final ScrollView scrollView = (ScrollView)layout.findViewById(R.id.allchannels_scroll);

//        scrollView.setVerticalFadingEdgeEnabled(true);

        flag_back = 0;

        livetv_container = (LinearLayout) layout.findViewById(R.id.livetv_container);
        llay_system_msg = (LinearLayout) layout.findViewById(R.id.llay_system_msg);
        system_msg_title_view = (TextView) layout.findViewById(R.id.system_msg_title);
        system_msg_text_view = (TextView) layout.findViewById(R.id.system_msg_text);
        // system_msg = (TextView) layout.findViewById(R.id.system_msg);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mWidth = metrics.widthPixels;

        TextView txtShowGuide = (TextView)layout.findViewById(R.id.text_show_guide);
        txtShowGuide.setVisibility(View.VISIBLE);

        txtShowGuide.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    view.animate().scaleX(1).scaleY(1).setDuration(200);
                }else{

                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, 0);
                        }
                    });
                    view.animate().scaleX(1.15f).scaleY(1.15f).setDuration(200);
                }
            }
        });
        txtShowGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveChannelGuideFragment fragmentGuide = new LiveChannelGuideFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.fram_container, fragmentGuide, "layout_livechannel_guide");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        HomeActivity.isLiveChannels = true;

        return layout;
    }

    @Override
    public void onResume() {
        recordAdapterId = 0;

        /*if (!bFragmentCreated) {
            getView().setFocusableInTouchMode(false);
            getView().requestFocus();
            bFragmentCreated = true;
        }*/

        if (allChannelsArrayList.isEmpty())
            ((HomeActivity)getActivity()).requestTabFocus(Constant.TAB_LIVECHANNEL);
        if (tryAgainDialog != null
                && tryAgainDialog.isShowing()) {
            tryAgainDialog.dismiss();
        }
        Call_api();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        HomeActivity.isLiveChannels = false;
        super.onDestroyView();
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

    void GetAllRadioList(String isradio) {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        parms.putString("sid",logindetails.getString("sid",""));
        parms.putString("isradio", isradio);
        MyVolley.init(getActivity());
        ShowProgressDilog(getActivity());
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "channels.php", GET_ALL_RADIO, this, parms, false);
    }

    void GetSystemMsg() {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        MyVolley.init(getActivity());
        //ShowProgressDilog(getActivity());
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "notice.php", GET_SYSTEM_MSG, this, parms, false);
    }

    public void Call_api() {
        if (NetworkUtil.checkNetworkAvailable(getActivity())) {
            GetSystemMsg();
            GetAllChannelsList("0");
        } else {
            ShowErrorAlert(getActivity(), "Please check your network connection..");
        }
    }

    public void ShowErrorAlert(final Context c, String text) {

        if (tryAgainDialog == null) {
            ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
            tryAgainDialog = new AlertDialog.Builder(ctx)
                    .setTitle("Alert!")
                    .setMessage(text)
                    .setCancelable(false)
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call_api();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            tryAgainDialog.setCanceledOnTouchOutside(false);
        }
        tryAgainDialog.setMessage(text);
        tryAgainDialog.show();
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
        DismissProgress(getActivity());
        Constant.ShowErrorToast(getActivity());
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        DismissProgress(getActivity());
        int itemSize = Constant.getChannelItemWidth(getActivity());
        mSpanCnt = mWidth/itemSize;
        mSpaceSize = (mWidth - itemSize * mSpanCnt) / mSpanCnt;
        if (requestCode == GET_ALLCHANNEL) {
            boolean bFocusSet = false;
            System.out.println("Response for All channel list --------------------" + response.toString());
            //livetv_container.removeAllViewsInLayout();

            try {
                JSONArray results = response.getJSONArray("results");
                int updateID = 0;
                for (int i = 0; i < results.length(); i++) {
                    JSONArray channels;
                    SetgetAllChannels setgetAllChannels;
                    ArrayList<SetgetSubchannels> subChannels;
                    if (i < allChannelsArrayList.size()) {
                        setgetAllChannels = allChannelsArrayList.get(i);
                        subChannels = setgetAllChannels.getSubchannelsList();
                        subChannels.clear();
                    }
                    else {
                        setgetAllChannels = new SetgetAllChannels();
                        subChannels =  new ArrayList<>();
                    }

                    try {
                        JSONObject resultobj = results.getJSONObject(i);
                        setgetAllChannels.setChannelsid(resultobj.getString("id"));
                        setgetAllChannels.setGname(resultobj.getString("gname"));
                        setgetAllChannels.setEgname(resultobj.getString("egname"));
                        //setgetAllChannels.setLogo(resultobj.getString("logo"));
                        setgetAllChannels.setOdid(resultobj.getString("odid"));
                        setgetAllChannels.setIsradio(resultobj.getString("isradio"));
                        channels = resultobj.getJSONArray("channels");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }

                    for (int j = 0; j < channels.length(); j++) {
                        try {
                            JSONObject channelobj = channels.getJSONObject(j);

                            SetgetSubchannels setgetSubchannels = new SetgetSubchannels();
                            setgetSubchannels.setSub_channelsid(channelobj.getString("id"));
                            setgetSubchannels.setName(channelobj.getString("name"));
                            setgetSubchannels.setEname(channelobj.getString("ename"));
                            // setgetSubchannels.setDescription(channelobj.getString("description"));
                            setgetSubchannels.setImage(channelobj.getString("image"));
                            // setgetSubchannels.setTxticon(channelobj.getString("txticon"));
                            setgetSubchannels.setExtra(channelobj.getString("extra"));
                            //setgetSubchannels.setImport_url(channelobj.getString("import_url"));
                            //setgetSubchannels.setOrder(channelobj.getString("order"));
                            //setgetSubchannels.setOd(channelobj.getString("od"));
                            setgetSubchannels.setOdid(channelobj.getString("odid"));
                            setgetSubchannels.setGid(channelobj.getString("gid"));
                            setgetSubchannels.setIfshow(channelobj.getString("ifshow"));
                            setgetSubchannels.setIshd(channelobj.getString("ishd"));
                            setgetSubchannels.setIsradio(channelobj.getString("isradio"));
                            //  setgetSubchannels.setTochannel(channelobj.getString("tochannel"));
                            setgetSubchannels.setTohdchannel(channelobj.getString("tohdchannel"));
                            setgetSubchannels.setIsinfav(channelobj.optString("isinfav", "0"));
                            setgetSubchannels.setChid(channelobj.optString("chid", "0"));


                            JSONObject show = channelobj.getJSONObject("show");
                            setgetSubchannels.setRunning_epg_name(show.getString("name"));
                            setgetSubchannels.setEpg_time(show.getString("time"));
                            setgetSubchannels.setEpg_length(show.getString("lengthtime"));
                            setgetSubchannels.setYear(show.optString("year", ""));
                            // setgetSubchannels.setTemp(channelobj.getString("temp"));
                            subChannels.add(setgetSubchannels);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (i >= allChannelsArrayList.size()) {
                        setgetAllChannels.setSubchannelsList(subChannels);
                        allChannelsArrayList.add(setgetAllChannels);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Collections.sort(allChannelsArrayList, new CustomDateComparator.AllChannelsDateComparator());
            int startIndex = 0;
            for (int i = 0; i < allChannelsArrayList.size(); i++) {
                if (i < arrChannelRecyclerViews.size()) {
                    arrChannelTextViews.get(i).setText(allChannelsArrayList.get(i).getGname());
                    ((AllchannelRecyclerviewAdapter)arrChannelRecyclerViews.get(i).getAdapter()).setList(allChannelsArrayList.get(i).getSubchannelsList());
                    ((AllchannelRecyclerviewAdapter)arrChannelRecyclerViews.get(i).getAdapter()).notifyAllData();

                    channel_adapter = (AllchannelRecyclerviewAdapter)arrChannelRecyclerViews.get(i).getAdapter();
                    startIndex += allChannelsArrayList.get(i).getSubchannelsList().size();
                } else {
                    TextView textView = new TextView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (i == 0) {
                        params.setMargins(10, 0, 18, 5);
                    } else params.setMargins(10, 20, 18, 5);

                    textView.setLayoutParams(params);
                    textView.setText(allChannelsArrayList.get(i).getGname());
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER | Gravity.RIGHT);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    textView.setFocusable(false);
                    livetv_container.addView(textView);
                    arrChannelTextViews.add(textView);

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0, 0, 0, 10);
                    params1.gravity = Gravity.CENTER | Gravity.RIGHT;
                    arrChannelRecyclerViews.add(new CustomRecyclerView(getActivity()));
                    arrChannelRecyclerViews.get(i).setLayoutParams(params1);
                    arrChannelRecyclerViews.get(i).setItemViewCacheSize(18);
                    arrChannelRecyclerViews.get(i).setDrawingCacheEnabled(true);
                    arrChannelRecyclerViews.get(i).setNestedScrollingEnabled(false);
                    arrChannelRecyclerViews.get(i).setScrollContainer(false);

                    RtlGridLayoutManager gridLayoutManager =
                            new RtlGridLayoutManager(getActivity(), mSpanCnt);
                    arrChannelRecyclerViews.get(i).addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            super.getItemOffsets(outRect, view, parent, state);
                            outRect.left = mSpaceSize;
                        }
                    });
                    arrChannelRecyclerViews.get(i).setLayoutManager(gridLayoutManager);
                    arrChannelRecyclerViews.get(i).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                    channel_adapter = new AllchannelRecyclerviewAdapter(allChannelsArrayList.get(i).getSubchannelsList(), getActivity());
                    channel_adapter.setAllchannelRecyclerviewAdapterId(recordAdapterId++);
                    arrChannelRecyclerViews.get(i).setAdapter(channel_adapter);

                    startIndex += allChannelsArrayList.get(i).getSubchannelsList().size();
//                    if (!bFocusSet && channel_adapter.getChildCnt() > 0) {
//                        arrChannelRecyclerViews.get(i).requestFocus();
//                        bFocusSet = true;
//                    }
                    livetv_container.addView(arrChannelRecyclerViews.get(i));
                }
            }
            GetAllRadioList("1");
        } else if (requestCode == GET_ALL_RADIO) {
            DismissProgress(getActivity());
            System.out.println("Response for All channel list --------------------" + response.toString());
            allRadioArrayList.clear();
            try {
                JSONArray results = response.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONArray channels;
                    SetgetAllChannels setgetAllChannels;
                    ArrayList<SetgetSubchannels> subChannels;
                    if (i < allRadioArrayList.size()) {
                        setgetAllChannels = allRadioArrayList.get(i);
                        subChannels = setgetAllChannels.getSubchannelsList();
                        subChannels.clear();
                    }
                    else {
                        setgetAllChannels = new SetgetAllChannels();
                        subChannels =  new ArrayList<>();
                    }
                    try {
                        JSONObject resultobj = results.getJSONObject(i);
                        setgetAllChannels.setChannelsid(resultobj.getString("id"));
                        setgetAllChannels.setGname(resultobj.getString("gname"));
                        setgetAllChannels.setEgname(resultobj.getString("egname"));
                        //setgetAllChannels.setLogo(resultobj.getString("logo"));
                        //setgetAllChannels.setOdid(resultobj.getString("odid"));
                        setgetAllChannels.setIsradio(resultobj.getString("isradio"));
                        channels = resultobj.getJSONArray("channels");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }

                    for (int j = 0; j < channels.length(); j++) {
                        try {
                            JSONObject channelobj = channels.getJSONObject(j);
                            SetgetSubchannels setgetSubchannels = new SetgetSubchannels();
                            setgetSubchannels.setSub_channelsid(channelobj.getString("id"));
                            setgetSubchannels.setName(channelobj.getString("name"));
                            setgetSubchannels.setEname(channelobj.getString("ename"));
                            // setgetSubchannels.setDescription(channelobj.getString("description"));
                            setgetSubchannels.setImage(channelobj.getString("image"));
                            // setgetSubchannels.setTxticon(channelobj.getString("txticon"));
                            setgetSubchannels.setExtra(channelobj.getString("extra"));
                            //setgetSubchannels.setImport_url(channelobj.getString("import_url"));
                            //setgetSubchannels.setOrder(channelobj.getString("order"));
                            //setgetSubchannels.setOd(channelobj.getString("od"));
                            setgetSubchannels.setOdid(channelobj.getString("odid"));
                            setgetSubchannels.setGid(channelobj.getString("gid"));
                            setgetSubchannels.setIfshow(channelobj.getString("ifshow"));
                            setgetSubchannels.setIshd(channelobj.getString("ishd"));
                            setgetSubchannels.setIsradio(channelobj.getString("isradio"));
                            //  setgetSubchannels.setTochannel(channelobj.getString("tochannel"));
                            setgetSubchannels.setTohdchannel(channelobj.getString("tohdchannel"));
                            setgetSubchannels.setIsinfav(channelobj.optString("isinfav", "0"));
                            setgetSubchannels.setChid(channelobj.optString("chid", "0"));
                            subChannels.add(setgetSubchannels);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (i >= allRadioArrayList.size()) {
                        setgetAllChannels.setSubchannelsList(subChannels);
                        allRadioArrayList.add(setgetAllChannels);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            DismissProgress(getActivity());
            int startIndex = 0;
            for (int i = 0; i < allRadioArrayList.size(); i++) {
                if (i < arrRadioRecyclerViews.size()) {
                    arrRadioTextViews.get(i).setText(allRadioArrayList.get(i).getGname());
//                    arrRadioRecyclerViews.get(i).getAdapter().notifyDataSetChanged();

                    radio_adapter = (AllRadioRecyclerviewAdapter)arrRadioRecyclerViews.get(i).getAdapter();
                    startIndex += allRadioArrayList.get(i).getSubchannelsList().size();
                } else {
                    TextView textView = new TextView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 20, 13, 5);
                    textView.setLayoutParams(params);
                    textView.setText("רדיו - " + allRadioArrayList.get(i).getGname());
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER | Gravity.RIGHT);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    textView.setFocusable(false);
                    livetv_container.addView(textView);
                    arrRadioTextViews.add(textView);

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (i == allRadioArrayList.size() - 1) {
                        params1.setMargins(0, 0, 0, 0);
                    } else {
                        params1.setMargins(0, 0, 0, 10);
                    }
                    params1.gravity = Gravity.CENTER | Gravity.RIGHT;
                    arrRadioRecyclerViews.add(new CustomRecyclerView(getActivity()));
                    arrRadioRecyclerViews.get(i).setLayoutParams(params1);
                    arrRadioRecyclerViews.get(i).setItemViewCacheSize(18);
                    arrRadioRecyclerViews.get(i).setDrawingCacheEnabled(true);
                    arrRadioRecyclerViews.get(i).setNestedScrollingEnabled(false);
                    arrRadioRecyclerViews.get(i).setScrollContainer(false);
                    arrRadioRecyclerViews.get(i).addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            super.getItemOffsets(outRect, view, parent, state);
                            outRect.left = mSpaceSize;
                        }
                    });

                    RtlGridLayoutManager gridLayoutManager =
                            new RtlGridLayoutManager(getActivity(), mSpanCnt);
                    arrRadioRecyclerViews.get(i).setLayoutManager(gridLayoutManager);
                    arrRadioRecyclerViews.get(i).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                    radio_adapter = new AllRadioRecyclerviewAdapter(allRadioArrayList.get(i).getSubchannelsList(), getActivity());
                    radio_adapter.setAllRadioRecyclerviewAdapterId(recordAdapterId++);
                    arrRadioRecyclerViews.get(i).setAdapter(radio_adapter);
                    livetv_container.addView(arrRadioRecyclerViews.get(i));

                    startIndex += allRadioArrayList.get(i).getSubchannelsList().size();
                }
            }
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        if (requestCode == GET_SYSTEM_MSG) {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);
                    system_msg_text = jsonObject.getString("contents");
                    system_msg_title = jsonObject.getString("title");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            // system_msg.setText(Html.fromHtml(system_msg_text));

            if (!system_msg_text.equalsIgnoreCase("")) {
                system_msg_text_view.setVisibility(View.VISIBLE);
                system_msg_title_view.setVisibility(View.VISIBLE);
                system_msg_title_view.setText(system_msg_title);
                String text_raw = String.valueOf(Html.fromHtml(system_msg_text));
                text_raw = text_raw.replace("\n\n", "\n");
                system_msg_text_view.setText(text_raw);
            }else{
                system_msg_text_view.setVisibility(View.GONE);
                system_msg_title_view.setVisibility(View.GONE);
            }
            // GetAllChannelsList("0");
        }
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
            Log.i("trytrytry","allChannel onFragmentBackState");
            ((LinearLayout) (v.findViewById(R.id.layout_root_allchannels))).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            HomeActivity.isLiveChannels = false;
        }
    }

    public void onFragmentTopFromBackState() {
        View v = getView();
        if (v != null) {
            Log.i("trytrytry","allChannel onFragmentTopFromBackState");
            ((LinearLayout) (v.findViewById(R.id.layout_root_allchannels))).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            ((LinearLayout) (v.findViewById(R.id.layout_root_allchannels))).requestFocus();
            HomeActivity.isLiveChannels = true;
        }
    }
}
