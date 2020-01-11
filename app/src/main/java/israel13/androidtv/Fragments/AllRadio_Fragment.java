package israel13.androidtv.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Adapter.AllchannelRecyclerviewAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetAllChannels;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomRecyclerView;


/**
 * Created by puspak on 20/06/17.
 */
public class AllRadio_Fragment extends Fragment implements IJSONParseListener {

    int GET_ALLCHANNEL = 104;
    ProgressDialog pDialog;

    ArrayList<SetgetAllChannels> allChannelsArrayList;
    ArrayList<SetgetSubchannels> allsuSubchannelsArrayList;

    ListView listview_activity_allchannels;

    SharedPreferences logindetails;
    public static String channel_id = "",channel_name="",channel_icon="";
    public static String is_infav="",is_radio="0";
    public LinearLayout livetv_container;
    public static ArrayList<SetgetSubchannels> total_all_channel;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.activity_allchannels,null);


        logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);


        livetv_container = (LinearLayout)layout.findViewById(R.id.livetv_container);


        allChannelsArrayList = new ArrayList<>();
        total_all_channel = new ArrayList<>();

       if(is_radio.equalsIgnoreCase("1"))
       {
       GetAllChannelsList("1");
       }
       else GetAllChannelsList("0");




 return layout;
    }


    void GetAllChannelsList(String isradio) {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
//        parms.putString("sid",logindetails.getString("sid",""));
        parms.putString("isradio",isradio);
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
        if (requestCode == GET_ALLCHANNEL)
        {

            System.out.println("Response for All channel list --------------------" + response.toString());

            try {

                JSONArray results = response.getJSONArray("results");

                for (int i = 0 ; i < results.length() ; i++)
                {
                    SetgetAllChannels setgetAllChannels = new SetgetAllChannels();
                    JSONArray channels;
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
                    allsuSubchannelsArrayList = new ArrayList<>();

                    for (int j = 0 ; j < channels.length() ; j++ )
                    {
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
                            setgetSubchannels.setIsinfav(channelobj.getString("isinfav"));
                            // setgetSubchannels.setTemp(channelobj.getString("temp"));
                            allsuSubchannelsArrayList.add(setgetSubchannels);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    setgetAllChannels.setSubchannelsList(allsuSubchannelsArrayList);
                    allChannelsArrayList.add(setgetAllChannels);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //listview_activity_allchannels.setAdapter(new AllChannelsAdapter(getActivity(),R.layout.child_activity_allchannels,
                //    allChannelsArrayList));
            for (int i = 0 ; i < allChannelsArrayList.size(); i++)
            {
                TextView textView =new TextView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,20,13,5); //payel
                textView.setLayoutParams(params);
                textView.setText(allChannelsArrayList.get(i).getGname());
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setTextSize(20); //payel

                textView.setGravity(Gravity.CENTER | Gravity.RIGHT);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                livetv_container.addView(textView);

                CustomRecyclerView recyclerView=new CustomRecyclerView(getActivity());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(10,0,10,10);
                params1.gravity=Gravity.CENTER | Gravity.RIGHT; //payel
                recyclerView.setLayoutParams(params1);
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                livetv_container.addView(recyclerView);

                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                layoutManager.setReverseLayout(true);

                recyclerView.setLayoutManager(layoutManager);

                AllchannelRecyclerviewAdapter adapter = new AllchannelRecyclerviewAdapter(allChannelsArrayList.get(i).getSubchannelsList(), getActivity());
                recyclerView.setAdapter(adapter);
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
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setContentView(R.layout.layout_progress_dilog);
    }

    void DismissProgress(Context c) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
