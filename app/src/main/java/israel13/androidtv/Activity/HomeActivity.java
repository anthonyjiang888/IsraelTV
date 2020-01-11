package israel13.androidtv.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.DefaultLoadControl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import israel13.androidtv.CallBacks.Keydown;
import israel13.androidtv.CallBacks.TVShowPlayDataUpdate;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.All_record_Fragment;
import israel13.androidtv.Fragments.Favorites_Fragment;
import israel13.androidtv.Fragments.LoadRecordList_Fragment;
import israel13.androidtv.Fragments.MyProfile_Fragment;
import israel13.androidtv.Fragments.Search_Fragment;
import israel13.androidtv.Fragments.VodMainPage_Fragment;
import israel13.androidtv.Fragments.VodSub_Subcategory_Fragment;
import israel13.androidtv.Fragments.VodSubcategoryMovies_Fragment;
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Service.ResetCacheService;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.NetworkUtil;

/**
 * Created by puspak on 16/6/17.
 */

public class HomeActivity extends AppCompatActivity implements IJSONParseListener {

    public static int VodGridRecycler = 0;
    public static int VodEpisodes = 1;
    public static int VodMovies = 2;
    public static int VodSubCategoryGridRecycler = 3;
    public static int VodSub_SubCategoryGridRecycler = 4;
    public static int VodTvshowPlayRecycler = 5;
    public static int VodSubMovieGridRecycler = 6;
    public static int VodSub_Sub_SubCategoryGridRecycler = 7;
    public static int AllchannelRecording = 8;
    public static int AllchannelRecyclerview = 9;
    public static int AllRadioRecyclerview = 10;
    public static int FavoritesLiveChannel = 11;
    public static int FavoritesEpisodes = 12;
    public static int FavoritesMovies = 13;
    public static int FavoritesLiveRadio = 14;
    public static int VodSearchEpisodes = 15;

    private int availableAdapterId = 0;

    private int selectedPos_GridRecycler = 0;
    private int selectedPos_Episodes = 0;
    private int selectedPos_Movies = 0;
    private int selectedPos_VodSubCategoryGridRecycler = 0;
    private int selectedPos_VodSub_SubCategoryGridRecycler = 0;
    private int selectedPos_VodTvshowPlayRecycler = 0;
    private int selectedPos_VodSubMovieGridRecycler = 0;
    private int selectedPos_VodSub_Sub_SubCategoryGridRecycler = 0;
    private int selectedPos_AllchannelRecording = 0;
    private int selectedPos_AllchannelRecyclerview = 0;
    private int selectedPos_AllRadioRecyclerview = 0;
    private int selectedPos_FavoritesLiveChannel = 0;
    private int selectedPos_FavoritesEpisodes = 0;
    private int selectedPos_FavoritesMovies = 0;
    private int selectedPos_FavoritesLiveRadio = 0;
    private int selectedPos_VodSearchEpisodes = 0;

    private TextView tv_profile_,tv_search_,tv_vod_,tv_record_,tv_live_tv_,tv_fav_,device_details;
    private LinearLayout tv_profile,tv_search,tv_radio, tv_vod, tv_record,tv_live_tv,tv_fav;
    private LinearLayout menu_layout,im_israeltv,rootview;
    private ImageView logo_icon,iv_pro,iv_search,iv_vod,Iv_records,iv_live,iv_fav;
    public static boolean FlagVodTVShow;
    public static LoadRecordList_Fragment mContext;
    private SharedPreferences mPrefs;
    public static boolean isLiveChannels;
    public static boolean isFavoritesFragment;
    public static boolean flagReady;
    private static boolean isApplicationStart = false;
    public static boolean isRadioFragment = false;
    int GET_USER_DETAILS = 101;
    public interface BackInterface{
        void onBackKeyPress();
    }
    public BackInterface onFavoriteFragmentBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isApplicationStart = false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_homepage);
        mPrefs = this.getSharedPreferences("logindetails", this.MODE_PRIVATE);

        tv_profile=(LinearLayout) findViewById(R.id.tv_profile);
        tv_search=(LinearLayout) findViewById(R.id.tv_search);
        // tv_radio=(LinearLayout) findViewById(R.id.tv_radio);
        tv_vod=(LinearLayout) findViewById(R.id.tv_vod);
        tv_live_tv=(LinearLayout)findViewById(R.id.tv_live_tv);
        tv_fav=(LinearLayout) findViewById(R.id.tv_fav);
        tv_record=(LinearLayout)findViewById(R.id.tv_record);
        menu_layout=(LinearLayout) findViewById(R.id.menu_layout);
        im_israeltv=(LinearLayout) findViewById(R.id.im_israeltv);
        rootview=(LinearLayout) findViewById(R.id.rootview);

        tv_profile_=(TextView)findViewById(R.id.tv_profile_);
        tv_search_=(TextView)findViewById(R.id.tv_search_);
        tv_vod_=(TextView)findViewById(R.id.tv_vod_);
        tv_record_=(TextView)findViewById(R.id.tv_record_);
        tv_live_tv_=(TextView)findViewById(R.id.tv_live_tv_);
        tv_fav_=(TextView)findViewById(R.id.tv_fav_);

        logo_icon=(ImageView)findViewById(R.id.logo_icon);
        iv_pro=(ImageView)findViewById(R.id.iv_pro);
        iv_search=(ImageView)findViewById(R.id.iv_search);
        iv_vod=(ImageView)findViewById(R.id.iv_vod);
        Iv_records=(ImageView)findViewById(R.id.Iv_records);
        iv_live=(ImageView)findViewById(R.id.iv_live);
        iv_fav=(ImageView)findViewById(R.id.iv_fav);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getSupportFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
        //Toast.makeText(this, "width and height--"+width+" "+height, Toast.LENGTH_LONG).show();

        if(width<=1280)
        {
            tv_profile_.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv_search_.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv_vod_.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv_record_.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv_live_tv_.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv_fav_.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

            logo_icon.getLayoutParams().width=130;
            iv_pro.getLayoutParams().width=30;
            iv_search.getLayoutParams().width=30;
            iv_vod.getLayoutParams().width=30;
            Iv_records.getLayoutParams().width=30;
            iv_live.getLayoutParams().width=30;
            iv_fav.getLayoutParams().width=30;

            logo_icon.requestLayout();
            iv_pro.requestLayout();
            iv_search.requestLayout();
            iv_vod.requestLayout();
            Iv_records.requestLayout();
            iv_live.requestLayout();
            iv_fav.requestLayout();
        }
        else
        {
            tv_profile_.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            tv_search_.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            tv_vod_.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            tv_record_.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            tv_live_tv_.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            tv_fav_.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        }

        tv_fav.requestFocus();

        String fragment,channel_id,channel_icon;
        Intent intent = getIntent();
        if (intent!=null)
        {
            fragment=intent.getStringExtra("go_to");
            channel_id=intent.getStringExtra("channel_id");
            channel_icon=intent.getStringExtra("channel_icon");

            if(fragment!=null&&channel_id!=null&&channel_icon!=null) {
                if (fragment.equalsIgnoreCase("record")) {
                    AllChannels_Fragment.channel_id = channel_id;
                    AllChannels_Fragment.channel_icon=channel_icon;
                    LoadRecordList_Fragment loadRecordList_Fragment = new LoadRecordList_Fragment();
                    FragmentTransaction ft1 = HomeActivity.this.getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.fram_container, loadRecordList_Fragment, "layout_tvshow_screen");
                    ft1.addToBackStack(null);
                    ft1.commit();
                    isselected(tv_record.getId());
                }
                else
                {
                    AllChannels_Fragment allchannel_frag= new AllChannels_Fragment();
                    AllChannels_Fragment.is_radio="0";
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.fram_container, allchannel_frag, "layout_All_live_channel");
                    ft3.commit();
                    isselected(tv_live_tv.getId());
                }
            }
            else
            {
                AllChannels_Fragment allchannel_frag= new AllChannels_Fragment();
                AllChannels_Fragment.is_radio="0";
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.fram_container, allchannel_frag, "layout_All_live_channel");
                ft3.commit();
                isselected(tv_live_tv.getId());
            }
        }
        else
        {
            AllChannels_Fragment allchannel_frag= new AllChannels_Fragment();
            AllChannels_Fragment.is_radio="0";
            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
            ft3.replace(R.id.fram_container, allchannel_frag, "layout_All_live_channel");
            ft3.commit();
            isselected(tv_live_tv.getId());
        }

        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAdapterId_Position();
                cleanFragments();
                MyProfile_Fragment profile_fragment= new MyProfile_Fragment();
                FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                // ft6.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft6.replace(R.id.fram_container, profile_fragment, "layout_profile");
                ft6.commit();
                isselected(v.getId());
                hideIME(v);
            }
        });

        tv_profile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    tv_profile.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
                } else {
                    if (AllChannels_Fragment.itemPosition >= 0 && AllChannels_Fragment.itemPosition <= 2) {
                        AllChannels_Fragment.itemPosition = -1;
                        requestTabFocus(Constant.TAB_FAVOURITE);
                    }else if (Favorites_Fragment.is_editfav)
                    {
                        requestTabFocus(Constant.TAB_FAVOURITE);

                        Favorites_Fragment.is_editfav = false;
                    }
                    else tv_profile.animate().scaleX(1.04f).scaleY(1.08f).setDuration(200).x(tv_profile.getWidth() * 0.019f);
                    AllChannels_Fragment.itemPosition = -1;
                }
                isRadioFragment = false;
            }
        });
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAdapterId_Position();
                Search_Fragment search_main= new Search_Fragment();
                FragmentTransaction ft7 = getSupportFragmentManager().beginTransaction();
                // ft7.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft7.replace(R.id.fram_container, search_main, "layout_search");
                ft7.commit();
                isselected(v.getId());
            }
        });
        tv_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    tv_search.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
                } else {
                    tv_search.animate().scaleX(1.04f).scaleY(1.08f).setDuration(200).x(tv_search.getWidth() * 0.019f);
                }
                isRadioFragment = false;
            }
        });
        tv_vod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAdapterId_Position();
                cleanFragments();
                VodMainPage_Fragment Vod_main= new VodMainPage_Fragment();
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                //  ft2.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft2.replace(R.id.fram_container, Vod_main, "layout_VOD");
                ft2.commit();
                isselected(v.getId());
                hideIME(v);
            }
        });
        tv_vod.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    tv_vod.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
                } else {
                    tv_vod.animate().scaleX(1.04f).scaleY(1.08f).setDuration(200).x(tv_vod.getWidth() * 0.019f);
                }
                isRadioFragment = false;
            }
        });

        tv_live_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAdapterId_Position();
                cleanFragments();
                // Toast.makeText(HomeActivity.this,"Click Tv_line_tv",Toast.LENGTH_SHORT).show();
                AllChannels_Fragment allchannel_frag= new AllChannels_Fragment();
                AllChannels_Fragment.is_radio="0";
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                // ft3.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft3.replace(R.id.fram_container, allchannel_frag, "layout_All_live_channel");
                ft3.commit();
                isselected(v.getId());
                hideIME(v);
            }
        });

        tv_live_tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                    if (!b) {
                        tv_live_tv.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
                    } else {
                        if (isApplicationStart == true)
                            tv_live_tv.animate().scaleX(1.04f).scaleY(1.08f).setDuration(200).x(view.getWidth() * 0.019f);
                    }
                Favorites_Fragment.is_editfav = false;
                isRadioFragment = true;
                isApplicationStart = true;
            }
        });

        tv_fav.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    tv_fav.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
                } else {
                    tv_fav.animate().scaleX(1.04f).scaleY(1.08f).setDuration(200).x(tv_fav.getWidth() * 0.019f);
                }
                isRadioFragment = true;
            }
        });

        tv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAdapterId_Position();
                cleanFragments();
                Favorites_Fragment favorites_fragment= new Favorites_Fragment();
                FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                //  ft5.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft5.replace(R.id.fram_container, favorites_fragment, "layout_Favorites");
                ft5.commit();
                isselected(v.getId());
                hideIME(v);
            }
        });

        tv_record.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    tv_record.animate().scaleX(1).scaleY(1).setDuration(200).x(0);
                } else {
                    tv_record.animate().scaleX(1.04f).scaleY(1.08f).setDuration(200).x(tv_record.getWidth() * 0.019f);
                }
                isRadioFragment = false;
            }
        });

        tv_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAdapterId_Position();
                cleanFragments();
                All_record_Fragment Record_main= new All_record_Fragment();
                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                //  ft4.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft4.replace(R.id.fram_container, Record_main, "layout_Record");
                ft4.commit();
                isselected(v.getId());
                hideIME(v);
            }
        });

        if (LoginActivity.logAct != null) {
            LoginActivity.logAct.finish();
            LoginActivity.logAct = null;
        }

        if (mPrefs != null) {
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putBoolean("forceClearCache", false);
            edit.commit();
        }
        Records_Play_Activity.flag_delete = false;
        Tv_show_play_activity.flag_delete = false;
        VodMovieVideoPlayActivity.flag_delete = false;

        CheckStateAndPlay();
    }

    void CheckStateAndPlay() {
        SharedPreferences rememberState;
        rememberState = this.getSharedPreferences("remember", LoginActivity.MODE_PRIVATE);
        if (rememberState != null && rememberState.getBoolean("isHome", false) && rememberState.getString("Where", "").equals("LiveChannelPlay")){

            Intent in = new Intent(this, LiveChannels_Play_Activity.class);
            in.putExtra(LiveChannels_Play_Activity.CHANNEL_ID, rememberState.getString("ChannelId",""));
            in.putExtra(LiveChannels_Play_Activity.MAIN_CH_ID, rememberState.getString("MainChId",""));
            in.putExtra(LiveChannels_Play_Activity.CH_ICON, rememberState.getString("ChIcon",""));

            tv_live_tv.callOnClick();
            startActivity(in);

        }

        if (rememberState != null && rememberState.getBoolean("isHome", false) && rememberState.getString("Where", "").equals("RecordPlay")) {
            Records_Play_Activity.flag_delete = true;

            AllChannels_Fragment.channel_id = rememberState.getString("ChannelId", "");
            AllChannels_Fragment.channel_name = rememberState.getString("ChannelName", "");
            AllChannels_Fragment.channel_icon = rememberState.getString("ChIcon", "");
            Records_Play_Activity.current_position = rememberState.getInt("position", 0);
            Records_Play_Activity.rdatetime = rememberState.getString("rdatetime", "");
            Records_Play_Activity.show_pic = rememberState.getString("show_pic", "");
            Records_Play_Activity.rating_record = rememberState.getString("rating_record","");

            Records_Play_Activity.isSearch = false;
            LoadRecordList_Fragment.date = rememberState.getString("date", "");

            tv_record.callOnClick();
            Intent in = new Intent(this, Records_Play_Activity.class);
            startActivity(in);
        }

        if (rememberState != null && rememberState.getBoolean("isHome", false) && rememberState.getString("Where", "").equals("TvShowPlay")) {
            Tv_show_play_activity.flag_delete = true;
            VodTvShowVideo_Fragment.selecte_epi_id = rememberState.getString("selecte_epi_id", "");
            Tv_show_play_activity.isSortDown = rememberState.getBoolean("isSortDown", false);
            tv_vod.callOnClick();
            Intent intent2 = new Intent(this, Tv_show_play_activity.class);
            startActivity(intent2);
        }
        if (rememberState != null && rememberState.getBoolean("isHome", false) && rememberState.getString("Where", "").equals("MoviePlay")) {
            VodMovieVideoPlayActivity.flag_delete = true;
            VodMovieVideoPlayActivity.vodid = rememberState.getString("vodid", "");
            tv_vod.callOnClick();
            Intent in = new Intent(this, VodMovieVideoPlayActivity.class);
            startActivity(in);
        }
    }
    Handler backPressHandler = new Handler();
    int backPressCount = 0;
    Runnable backPressRunnable = new Runnable() {
        @Override
        public void run() {
            backPressCount--;
            if (backPressCount > 0)
                backPressHandler.postDelayed(backPressRunnable, 500);
        }
    };

    @Override
    public void onBackPressed() {
        if (isFavoritesFragment) {
            if (flagReady == false) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    super.onBackPressed();
                    return;
                }
                cleanAdapterId_Position();
                cleanFragments();
                // Toast.makeText(HomeActivity.this,"Click Tv_line_tv",Toast.LENGTH_SHORT).show();
                AllChannels_Fragment allchannel_frag= new AllChannels_Fragment();
                AllChannels_Fragment.is_radio="0";
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                // ft3.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                ft3.replace(R.id.fram_container, allchannel_frag, "layout_All_live_channel");
                ft3.commit();
                isselected(tv_live_tv.getId());
                hideIME(tv_live_tv);
            } else if (onFavoriteFragmentBack != null)
                onFavoriteFragmentBack.onBackKeyPress();
        } else if (!isLiveChannels) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
                return;
            }
            cleanAdapterId_Position();
            cleanFragments();
            // Toast.makeText(HomeActivity.this,"Click Tv_line_tv",Toast.LENGTH_SHORT).show();
            AllChannels_Fragment allchannel_frag= new AllChannels_Fragment();
            AllChannels_Fragment.is_radio="0";
            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
            // ft3.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            ft3.replace(R.id.fram_container, allchannel_frag, "layout_All_live_channel");
            ft3.commit();
            isselected(tv_live_tv.getId());
            hideIME(tv_live_tv);
        }else{
            backPressCount++;
            if (backPressCount == 3) {
                super.onBackPressed();
            }else{
                backPressHandler.removeCallbacks(backPressRunnable);
                backPressHandler.postDelayed(backPressRunnable, 500);
            }
        }

    }

    private void cleanFragments() {
        int nCount = getSupportFragmentManager().getBackStackEntryCount();
        while (nCount > 0) {
            getSupportFragmentManager().popBackStack();
            nCount --;
        }
    }

    private void hideIME(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        System.gc();
    }

    public void isselected(int viewid)
    {
        if(tv_profile.getId()==viewid)
        {
            tv_profile.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else if(tv_search.getId()==viewid)
        {
            tv_search.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else if(tv_vod.getId()==viewid)
        {
            tv_vod.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else if(tv_live_tv.getId()==viewid)
        {
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else if(tv_fav.getId()==viewid)
        {
            tv_fav.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else if(tv_record.getId()==viewid)
        {
            tv_record.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else if(im_israeltv.getId()==viewid)
        {
            im_israeltv.setBackground(getResources().getDrawable(R.drawable.selected_background));
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
        else
        {
            tv_profile.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_search.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_vod.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_live_tv.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_fav.setBackground(getResources().getDrawable(R.drawable.textview_background));
            tv_record.setBackground(getResources().getDrawable(R.drawable.textview_background));
        }
    }

    @Override
    protected void onPause() {
//        if (mPrefs != null) {
//            SharedPreferences.Editor edit = mPrefs.edit();
//            edit.putBoolean("forceClearCache", true);
//            edit.commit();
//        }
//        checkService();
        super.onPause();
    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        super.onResume();
//        Call_api();
    }

    private long mLastKeyDownTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long current = System. currentTimeMillis();
        boolean res = false;
        // By 12-19 request, remove onKeyDown.
//        if (current - mLastKeyDownTime < 300 ) {
//            res = true;
//        }
//        else
//        {
//            res = super.onKeyDown(keyCode, event);
//            mLastKeyDownTime = current;
//        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            if(mContext != null) {
                Keydown down = (Keydown) mContext;
                down.OnKeydown();
            }
        }
        res = super.onKeyDown(keyCode, event);

        return res;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            List<Fragment> frms  = getSupportFragmentManager().getFragments();
            int nCount = frms.size();
            for (int i = 0; i < nCount; i ++) {
                Fragment frm = frms.get(i);
                if (frm instanceof Search_Fragment) {
                    Search_Fragment sFrm = (Search_Fragment) frm;
                    if (sFrm.hideIMEAndEnableFocus())
                        return true;
                }
            }

        }
        if(keyCode != KeyEvent.KEYCODE_DPAD_UP && keyCode != KeyEvent.KEYCODE_DPAD_DOWN && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT && keyCode != KeyEvent.KEYCODE_DPAD_LEFT){
            return super.onKeyUp(keyCode, event);
        }

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fram_container);
        View v = frameLayout.getChildAt(0);
        ScrollView scrollView = getScrollView(v);

        if(scrollView != null){
//            if(scrollView != null && scrollView.getScrollY() < 100){
//                scrollView.smoothScrollTo(0, 0);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 300);
//            }
        }

        return super.onKeyUp(keyCode, event);
    }

    private ScrollView getScrollView(View v){
        if(v != null && v instanceof ScrollView){
            ScrollView sv = (ScrollView) v;
            return sv;
        }else{
            if(v == null){
                return null;
            }
            if(v instanceof LinearLayout){
                View vv = ((LinearLayout) v).getChildAt(0);
                return getScrollView(vv);
            }
            return null;
        }
    }

    public void setAvialableAdapterId(int adapterId){
        availableAdapterId = adapterId;
    }

    public int getAvailableAdapterId(){
        return availableAdapterId;
    }

    private void cleanAdapterId_Position(){
        availableAdapterId = 0;

        selectedPos_GridRecycler = 0;
        selectedPos_Episodes = 0;
        selectedPos_Movies = 0;
        selectedPos_VodSubCategoryGridRecycler = 0;
        selectedPos_VodSub_SubCategoryGridRecycler = 0;
        selectedPos_VodTvshowPlayRecycler = 0;
        selectedPos_VodSubMovieGridRecycler = 0;
        selectedPos_VodSub_Sub_SubCategoryGridRecycler = 0;
        selectedPos_AllchannelRecording = 0;
        selectedPos_AllchannelRecyclerview = 0;
        selectedPos_AllRadioRecyclerview = 0;
        selectedPos_FavoritesLiveChannel = 0;
        selectedPos_FavoritesEpisodes = 0;
        selectedPos_FavoritesMovies = 0;
        selectedPos_FavoritesLiveRadio = 0;
    }

    public void setSelectedPosition(int adapterId, int position){
        if(adapterId == VodGridRecycler){
            selectedPos_GridRecycler = position;
        }
        if(adapterId == VodMovies){
            selectedPos_Movies = position;
        }
        if(adapterId == VodEpisodes){
            selectedPos_Episodes = position;
        }
        if(adapterId == VodSubCategoryGridRecycler){
            selectedPos_VodSubCategoryGridRecycler = position;
        }
        if(adapterId == VodSub_SubCategoryGridRecycler){
            selectedPos_VodSub_SubCategoryGridRecycler = position;
        }
        if(adapterId == VodTvshowPlayRecycler){
            selectedPos_VodTvshowPlayRecycler = position;
        }
        if(adapterId == VodSubMovieGridRecycler){
            selectedPos_VodSubMovieGridRecycler = position;
        }
        if(adapterId == VodSub_Sub_SubCategoryGridRecycler){
            selectedPos_VodSub_Sub_SubCategoryGridRecycler = position;
        }
        if(adapterId == AllchannelRecording){
            selectedPos_AllchannelRecording = position;
        }
        if(adapterId == AllchannelRecyclerview){
            selectedPos_AllchannelRecyclerview = position;
        }
        if(adapterId == AllRadioRecyclerview){
            selectedPos_AllRadioRecyclerview = position;
        }
        if(adapterId == FavoritesLiveChannel){
            selectedPos_FavoritesLiveChannel = position;
        }
        if(adapterId == FavoritesEpisodes){
            selectedPos_FavoritesEpisodes = position;
        }
        if(adapterId == FavoritesMovies){
            selectedPos_FavoritesMovies = position;
        }
        if(adapterId == FavoritesLiveRadio){
            selectedPos_FavoritesLiveRadio = position;
        }
        if(adapterId == VodSearchEpisodes){
            selectedPos_VodSearchEpisodes = position;
        }
    }

    public int getSelectedPosition(int adapterId){
        if(adapterId == VodGridRecycler){
            return selectedPos_GridRecycler;
        }
        if(adapterId == VodEpisodes){
            return selectedPos_Episodes;
        }
        if(adapterId == VodMovies){
            return selectedPos_Movies;
        }
        if(adapterId == VodSubCategoryGridRecycler){
            return selectedPos_VodSubCategoryGridRecycler;
        }
        if(adapterId == VodSub_SubCategoryGridRecycler){
            return selectedPos_VodSub_SubCategoryGridRecycler;
        }
        if(adapterId == VodTvshowPlayRecycler){
            return selectedPos_VodTvshowPlayRecycler;
        }
        if(adapterId == VodSubMovieGridRecycler){
            return selectedPos_VodSubMovieGridRecycler;
        }
        if(adapterId == VodSub_Sub_SubCategoryGridRecycler){
            return selectedPos_VodSub_Sub_SubCategoryGridRecycler;
        }
        if(adapterId == AllchannelRecording){
            return selectedPos_AllchannelRecording;
        }
        if(adapterId == AllchannelRecyclerview){
            return selectedPos_AllchannelRecyclerview;
        }
        if(adapterId == AllRadioRecyclerview){
            return selectedPos_AllRadioRecyclerview;
        }
        if(adapterId == FavoritesLiveChannel){
            return selectedPos_FavoritesLiveChannel;
        }
        if(adapterId == FavoritesEpisodes){
            return selectedPos_FavoritesEpisodes;
        }
        if(adapterId == FavoritesMovies){
            return selectedPos_FavoritesMovies;
        }
        if(adapterId == FavoritesLiveRadio){
            return selectedPos_FavoritesLiveRadio;
        }
        if(adapterId == VodSearchEpisodes){
            return selectedPos_VodSearchEpisodes;
        }
        return 0;
    }

    public void setFocusableState(boolean bFocusable) {
        tv_profile.setFocusable(bFocusable);
        tv_search.setFocusable(bFocusable);
        tv_vod.setFocusable(bFocusable);
        tv_record.setFocusable(bFocusable);
        tv_live_tv.setFocusable(bFocusable);
        tv_fav.setFocusable(bFocusable);
    }

    public void requestTabFocus(int tab) {
        switch (tab) {
            case Constant.TAB_PROFILE:
                tv_profile.requestFocus();
                break;
            case Constant.TAB_VOD:
                tv_vod.requestFocus();
                break;
            case Constant.TAB_RECORD:
                tv_record.requestFocus();
                break;
            case Constant.TAB_LIVECHANNEL:
                tv_live_tv.requestFocus();
                break;
            case Constant.TAB_FAVOURITE:
                tv_fav.requestFocus();
                break;
        }
    }

    private FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            Fragment tvShowfragment = getSupportFragmentManager().findFragmentByTag("layout_tvshow_screen");
            Fragment searchFragment= getSupportFragmentManager().findFragmentByTag("layout_search");
            Fragment recordFragment = getSupportFragmentManager().findFragmentByTag("layout_recording_frag");
            Fragment allRecordFragment = getSupportFragmentManager().findFragmentByTag("layout_Record");
            Fragment channelGuideFragment= getSupportFragmentManager().findFragmentByTag("layout_livechannel_guide");
            Fragment allChannelsFragment = getSupportFragmentManager().findFragmentByTag("layout_All_live_channel");
            Fragment vodMainFragment = getSupportFragmentManager().findFragmentByTag("layout_VOD");
            Fragment vodSubFragment = getSupportFragmentManager().findFragmentByTag("layout_Vodsubcategory");
            Fragment favFragment = getSupportFragmentManager().findFragmentByTag("layout_Favorites");
            Fragment subsubFragment = getSupportFragmentManager().findFragmentByTag("layout_sub_subcat");
            Fragment movieFragment = getSupportFragmentManager().findFragmentByTag("layout_VodMoviesubcategory");
            if (tvShowfragment != null && searchFragment != null) {
                ((Search_Fragment)searchFragment).onFragmentBackState();
            } else if (searchFragment != null) {
                ((Search_Fragment)searchFragment).onFragmentTopFromBackState();
            }
            if (recordFragment != null && allRecordFragment != null) {
                ((All_record_Fragment)allRecordFragment).onFragmentBackState();
            } else if (allRecordFragment != null) {
                ((All_record_Fragment)allRecordFragment).onFragmentTopFromBackState();
            }
            if (channelGuideFragment != null && allChannelsFragment != null) {
                ((AllChannels_Fragment)allChannelsFragment).onFragmentBackState();
            } else if (allChannelsFragment != null) {
                ((AllChannels_Fragment)allChannelsFragment).onFragmentTopFromBackState();
            }
            //vod page
            if (vodMainFragment != null && vodSubFragment == null) {
//                Log.e("test", "a");
                ((VodMainPage_Fragment)vodMainFragment).onFragmentTopFromBackState();
            } else if (vodMainFragment != null && vodSubFragment != null && subsubFragment == null && movieFragment == null && tvShowfragment == null) {
//                Log.e("test", "b");
                ((VodMainPage_Fragment)vodMainFragment).onFragmentBackState();
                ((VodSubcategory_Fragment)vodSubFragment).onFragmentTopFromBackState();
            } else if (vodMainFragment != null && vodSubFragment != null && subsubFragment != null && tvShowfragment != null) {
//                Log.e("test", "c");
                ((VodSub_Subcategory_Fragment)subsubFragment).onFragmentBackState();
            } else if (vodMainFragment != null && vodSubFragment != null && subsubFragment != null) {
//                Log.e("test", "d");
                ((VodSubcategory_Fragment)vodSubFragment).onFragmentBackState();
                ((VodSub_Subcategory_Fragment)subsubFragment).onFragmentTopFromBackState();
            } else if (vodMainFragment != null && vodSubFragment != null && (movieFragment != null || tvShowfragment != null)) {
//                Log.e("test", "e");
                ((VodSubcategory_Fragment)vodSubFragment).onFragmentBackState();
            }
            //fav page
            if (favFragment != null && tvShowfragment == null) {
                ((Favorites_Fragment)favFragment).onFragmentTopFromBackState();
            }
        }
    };

    public void Call_api() {
        if (NetworkUtil.checkNetworkAvailable(HomeActivity.this)) {
            GetUserDetails();
        } else {
            ShowErrorAlert(HomeActivity.this, "Please check your network connection..");
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

    void GetUserDetails() {
        JSONRequestResponse mResponse = new JSONRequestResponse(HomeActivity.this);
        Bundle parms = new Bundle();
        parms.putString("sid", mPrefs.getString("sid", ""));
        MyVolley.init(HomeActivity.this);
//        ShowProgressDilog(HomeActivity.this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "/loaduser.php", GET_USER_DETAILS, this, parms, false);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
//        DismissProgress(HomeActivity.this);
        Constant.ShowErrorToast(HomeActivity.this);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

//        DismissProgress(HomeActivity.this);
        if (requestCode == GET_USER_DETAILS) {
            String errorcode = "";
            try {
                errorcode = response.getString("error");
            } catch (Exception e) {
                try {
                    errorcode = response.getString("errorcode");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            try {
                int exoplaybackbuffer = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
                int exominbuffer = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS;
                if (errorcode.equalsIgnoreCase("0")) {
                    exoplaybackbuffer = response.optInt("exoplaybackbuffer", DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS);
                    exominbuffer = response.optInt("exominbuffer", DefaultLoadControl.DEFAULT_MIN_BUFFER_MS);
                }
                if (mPrefs != null) {
                    Constant.setEXOPlaybackBufferMS(mPrefs, exoplaybackbuffer);
                    Constant.setEXOMinBufferMS(mPrefs, exominbuffer);
                }
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
}
