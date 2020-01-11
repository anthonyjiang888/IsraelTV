package israel13.androidtv.Fragments;

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
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.C;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Records_Play_Activity;
import israel13.androidtv.Adapter.AllchannelRecordingAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.Setter_Getter.SetgetAllChannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.RtlGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import israel13.androidtv.R;

import israel13.androidtv.Setter_Getter.SetgetSubchannels;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by puspak on 29/6/17.
 */

public class All_record_Fragment extends Fragment implements IJSONParseListener {
    private int GET_ALLCHANNEL = 104;
    private ProgressDialog pDialog;
    private ArrayList<SetgetAllChannels> allChannelsArrayList;
    private ArrayList<CustomRecyclerView> arrChannelRecyclerViews;
    private ArrayList<TextView> arrChannelTextViews;
    private SharedPreferences logindetails;
    private int mWidth = 0;
    private int mSpanCnt = 0;

    public static String channel_id = "", channel_name = "", channel_icon = "";
    public static String is_infav = "";
    public LinearLayout livetv_container;
    private int mSpaceSize = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        logindetails = getActivity().getSharedPreferences("logindetails", MODE_PRIVATE);
        allChannelsArrayList = new ArrayList<>();

        Call_api();

        arrChannelRecyclerViews = new ArrayList<>();
        arrChannelTextViews = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View layout = layoutInflater.inflate(R.layout.activity_allchannels, null);
        livetv_container = (LinearLayout) layout.findViewById(R.id.livetv_container);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mWidth = metrics.widthPixels;

        setupUI();

        return layout;
    }


    @Override
    public void onResume() {
//        getView().setFocusableInTouchMode(false);
//        getView().requestFocus();

        ((HomeActivity)getActivity()).requestTabFocus(Constant.TAB_RECORD);
        super.onResume();
    }

    public void Call_api() {
        if (NetworkUtil.checkNetworkAvailable(getActivity())) {
            GetAllChannelsList("0");
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

    void GetAllChannelsList(String isradio) {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        parms.putString("sid",logindetails.getString("sid",""));
        parms.putString("isradio", isradio);
        MyVolley.init(getActivity());
        ShowProgressDilog(getActivity());
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "channels.php", GET_ALLCHANNEL, this, parms, false);
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
            System.out.println("Response for All channel list --------------------" + response.toString());

            try {
                JSONArray results = response.getJSONArray("results");

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

            //listview_activity_allchannels.setAdapter(new AllChannelsAdapter(getActivity(),R.layout.child_activity_allchannels,
            //    allChannelsArrayList));
            Collections.sort(allChannelsArrayList, new DateComparator());
            setupUI();
        }
    }

    private void setupUI() {
        for (int i = 0; i < allChannelsArrayList.size(); i++) {
            if (i < arrChannelRecyclerViews.size()) {
                arrChannelTextViews.get(i).setText(allChannelsArrayList.get(i).getGname());
                ((AllchannelRecordingAdapter)arrChannelRecyclerViews.get(i).getAdapter()).setList(allChannelsArrayList.get(i).getSubchannelsList());
                ((AllchannelRecordingAdapter)arrChannelRecyclerViews.get(i).getAdapter()).notifyAllData();
                //arrChannelRecyclerViews.get(i).getAdapter().notifyDataSetChanged();
            } else {
                TextView textView = new TextView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 20, 20, 5); //payel
                textView.setLayoutParams(params);
                textView.setText(allChannelsArrayList.get(i).getGname());
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER | Gravity.RIGHT);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                livetv_container.addView(textView);
                arrChannelTextViews.add(textView);

                final CustomRecyclerView recyclerView = new CustomRecyclerView(getActivity());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 0, 0, 10);
                params1.gravity = Gravity.CENTER | Gravity.RIGHT;
                recyclerView.setLayoutParams(params1);
                recyclerView.setItemViewCacheSize(18);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setScrollContainer(false);

                RtlGridLayoutManager gridLayoutManager = new RtlGridLayoutManager(getActivity(), mSpanCnt);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.left = mSpaceSize;
                    }
                });

                AllchannelRecordingAdapter adapter = new AllchannelRecordingAdapter(allChannelsArrayList.get(i).getSubchannelsList(), getActivity());
                adapter.setAllchannelRecordingAdapterId(i);
                recyclerView.setAdapter(adapter);
                arrChannelRecyclerViews.add(recyclerView);
                livetv_container.addView(arrChannelRecyclerViews.get(i));
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

    public class DateComparator implements Comparator<SetgetAllChannels> {
        @Override
        public int compare(SetgetAllChannels lhs, SetgetAllChannels rhs) {
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

    public void onFragmentBackState() {
        View v = getView();
        if (v != null)
            ((LinearLayout)(v.findViewById(R.id.layout_root_allchannels))).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    public void onFragmentTopFromBackState() {
        View v = getView();
        if (v != null) {
            ((LinearLayout) (v.findViewById(R.id.layout_root_allchannels))).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            for (int i = 0; i < allChannelsArrayList.size(); i++) {
                if (i < arrChannelRecyclerViews.size()) {
                    ((AllchannelRecordingAdapter) arrChannelRecyclerViews.get(i).getAdapter()).notifyAllData();
                }
            }
            Call_api();
        }
    }
}
