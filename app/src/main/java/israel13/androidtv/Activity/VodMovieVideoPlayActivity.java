package israel13.androidtv.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import israel13.androidtv.Adapter.VodMovieVideoPlayAdapter;
import israel13.androidtv.CallBacks.ConnectionChange;
import israel13.androidtv.Fragments.VodSubcategoryMovies_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.Others.ConnectivityReceiver;
import israel13.androidtv.Others.CustomTimeBar_ForPlayer;
import israel13.androidtv.R;
import israel13.androidtv.Service.ResetCacheService;
import israel13.androidtv.Setter_Getter.SetgetMovies;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomCacheDataSourceFactory;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.UiUtils;

/**
 * Created by Puspak on 01/06/17.
 */
public class VodMovieVideoPlayActivity extends AppCompatActivity implements IJSONParseListener,VideoRendererEventListener,ConnectionChange {
    public static String vodid = "";
    private ImageView favorites_vod_movie_videoplay, channel_icon_vod_movie_videoplay;
    private TextView channelname_vod_movie_videoplay, year_vod_movie_videoplay, duration_vod_movie_videoplay,tvcat_name,add_to_fav_movie_movie_text;
    private TextView views_vod_movie_videoplay, upload_date_vod_movie_videoplay;
    private TextView description_vod_movie_videoplay, genre_vod_movie_videoplay, whatsnext_vod_movie_videoplay;
    private RatingBar ratingBar_vod_movie_videoplay;
    private CustomRecyclerView recycler_view_vod_movie_videoplay;
    private LinearLayout linearlayout_vod_movie_videoplay_close;
    private ViewGroup container_movie_;
    private static final String TAG = "Movie_play";
    private int VOD_MOVIE_VIDEOPLAY = 200;
    private int ADD_FAVORITES = 213;
    private int REMOVE_FAVORITES = 214;
    private int VOD_MOVIE_VIDEOPLAY_PAGE = 211;
    private int MOVIE_PLAY = 210;
    private ProgressDialog pDialog = null;
    private SharedPreferences logindetails;
    private int totalpage = 0,count=1;
    private StringBuilder videoplayurl;

    private VodMovieVideoPlayAdapter vodMovieVideoPlayAdapter;
    public static boolean flag_delete;
    // small offline/online message
    private Timer mTimerForSmallConnection;
    private Handler mHandlerForSmallConnection;
    private LinearLayout layConnectionState;
    private TextView txtConnectionState;
    private boolean mLastConnectionStatus = true;
    public boolean isBackClose;
    public static int position1 = 0;
    public static String isinfav="";
    public  static String movie_play_cat="";
    public String flag_pressed = "";
    public static int Pausestate_Window_Index = C.INDEX_UNSET;
    public static long Pausestate_lastposition = C.POSITION_UNSET;

    private ArrayList<SetgetMovies> vodMovieVideoPlayList;
    private String category_movie_name="";
    private long pressed_time = 0;
    private long opened_time = 0;
    private Thread myThread1 = null;
    private RelativeLayout container_modified_livechannels;
    private ArrayList<String> videoBackupLists = new ArrayList<>();
    private int flag_glob=0;
    private LinearLayout add_fav_;
    private ImageView add_To_fav_img_movie;
    private SimpleExoPlayerView playerView_vod_movie_videoplay;
    private SimpleExoPlayer player;
    private CustomTimeBar_ForPlayer exo_progress;
    private RelativeLayout bar_layout;
    private FrameLayout framlay_image;
    private LinearLayout tv_connectionstatus_movies;
    private boolean mActivityAlive;

    // Variables for processing fullback urls
    Handler nextFullBackHandler = new Handler();
    Runnable nextFullBackRunnable = new Runnable() {
        @Override
        public void run() {
            nextFullBackPlay();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Constant.hasFreeDiskSpace(this, true)) {
            isBackClose = true;
            clearSavedData();
            finish();
            return;
        }

        mLastConnectionStatus = NetworkUtil.checkNetworkAvailable(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.play_movies_activity);

        layConnectionState = (LinearLayout)findViewById(R.id.layout_connection_state);
        txtConnectionState = (TextView)findViewById(R.id.text_connection_state);

        logindetails = this.getSharedPreferences("logindetails", MODE_PRIVATE);

        // playerView_vod_movie_videoplay = (JWPlayerView) findViewById(R.id.playerView_vod_movie_videoplay);
        container_movie_=(ViewGroup)findViewById(R.id.container_movie_);
        container_modified_livechannels = (RelativeLayout) findViewById(R.id.container_modified_movies);
        tvcat_name = (TextView) findViewById(R.id.tvcat_name);
        channelname_vod_movie_videoplay = (TextView) findViewById(R.id.tvmovie_name);
        year_vod_movie_videoplay = (TextView) findViewById(R.id.year_movie);
        duration_vod_movie_videoplay = (TextView) findViewById(R.id.time_movie);
        views_vod_movie_videoplay = (TextView) findViewById(R.id.noof_watches_movie);
        upload_date_vod_movie_videoplay = (TextView) findViewById(R.id.upload_year_movie);
        channel_icon_vod_movie_videoplay = (ImageView) findViewById(R.id.Iv_season_played_movie);
        description_vod_movie_videoplay = (TextView) findViewById(R.id.tv_description_of_show_movie);
        genre_vod_movie_videoplay = (TextView) findViewById(R.id.genre_movie);
        add_to_fav_movie_movie_text = (TextView) findViewById(R.id.add_to_fav_movie_movie_text);
        ratingBar_vod_movie_videoplay = (RatingBar) findViewById(R.id.ratingbars_movie);

        add_fav_=(LinearLayout)findViewById(R.id.add_fav_);
        add_To_fav_img_movie=(ImageView)findViewById(R.id.add_To_fav_img_movie);

        exo_progress=(CustomTimeBar_ForPlayer)findViewById(R.id.exo_progress);

        framlay_image=(FrameLayout) findViewById(R.id.framlay_image);
        tv_connectionstatus_movies=(LinearLayout) findViewById(R.id.tv_connectionstatus_movies);
        framlay_image.requestFocus();
        playerView_vod_movie_videoplay = (SimpleExoPlayerView) findViewById(R.id.playerView_vod_movie_videoplay);
        // bar_layout=(RelativeLayout)findViewById(R.id.bar_layout);

        //bar_layout.setVisibility(View.VISIBLE);
        //  exo_progress.requestFocus();
        SharedPreferences rememberPos = this.getSharedPreferences("position", MODE_PRIVATE);

        Pausestate_lastposition = rememberPos.getLong("Pausestate_lastposition",C.POSITION_UNSET);
        Pausestate_Window_Index = rememberPos.getInt("Pausestate_WindowIndex_", C.INDEX_UNSET);
        Log.i("inxas", ""+Pausestate_lastposition);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences rememberState = getSharedPreferences("remember", MODE_PRIVATE);
        if (!rememberState.getString("vodid", "").isEmpty()) {
            vodid = rememberState.getString("vodid", "");
        }
        // playerView_vod_movie_videoplay = new JWPlayerView(this, playerConfig);
        // container_movie_.addView(playerView_vod_movie_videoplay);
        vodMovieVideoPlayList = new ArrayList<>();

        //tvcat_name.setText(" | "+movie_play_cat);


        PlayMovie(vodid);


        if (isinfav.equalsIgnoreCase("1")) {
            add_to_fav_movie_movie_text.setText("הסר ממועדפים");
            add_To_fav_img_movie.setImageResource(R.drawable.remove_fav);

        } else {
            add_to_fav_movie_movie_text.setText("הוסף למועדפים");
            add_To_fav_img_movie.setImageResource(R.drawable.add_fav);
        }

        add_fav_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vodid.equalsIgnoreCase("")) {

                    if (isinfav.equalsIgnoreCase("0")) {
                        AddFavorites();
                    } else {
                        RemoveFavorites();
                    }

                } else {
                    Toast.makeText(VodMovieVideoPlayActivity.this, "בחר סרט", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void intui_player()
    {

        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
                Constant.getEXOMinBufferMS(logindetails), DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, Constant.getEXOPlaybackBufferMS(logindetails),
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES, DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS);

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        player.addListener(playerEventListener);
        //Set media controller
        playerView_vod_movie_videoplay.setUseController(true);

        // playerView_vod_movie_videoplay.requestFocus();

        // Bind the player to the view.
        playerView_vod_movie_videoplay.setPlayer(player);
    }

    public void Set_player_video(Uri path)
    {
        System.out.println("Set_player_video11 ----------------" );
        releasePlayer();
        if (!mActivityAlive) return;
        intui_player();
        System.out.println("Set_player_video222 ----------------");
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Set_player_video33 ----------------");
        //FOR LIVESTREAM LINK:
        CustomCacheDataSourceFactory dataSourceFactory = new CustomCacheDataSourceFactory(this);
        MediaSource videoSource = new HlsMediaSource(path, dataSourceFactory, 1, null, null);

        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
        //player.setRepeatMode(Player.REPEAT_MODE_OFF);

        playerView_vod_movie_videoplay.setFocusable(false);
        long duration = player == null ? 0 : player.getDuration();
        playerView_vod_movie_videoplay.setControllerShowTimeoutMs(8000);
    }

    public void Set_player_video_to_postion(Uri path,long lastposition)
    {
        System.out.println("Set_player_video_to_postion111 ----------------" );
        releasePlayer();
        System.out.println("Set_player_video_to_postion222 ----------------" );
        if (!mActivityAlive) return;
        intui_player();
        System.out.println("Set_player_video_to_postion333 ----------------" );

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //FOR LIVESTREAM LINK:
        CustomCacheDataSourceFactory dataSourceFactory = new CustomCacheDataSourceFactory(this);
        MediaSource videoSource = new HlsMediaSource(path, dataSourceFactory, 1, null, null);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        try {
            if (lastposition >= 0)
                player.seekTo(lastposition);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Set_player_video_to_postion444 ----------------" );

        playerView_vod_movie_videoplay.setFocusable(false);
        long duration = player == null ? 0 : player.getDuration();
        playerView_vod_movie_videoplay.setControllerShowTimeoutMs(8000);
    }

    void AddFavorites() {
        JSONRequestResponse mResponse = new JSONRequestResponse(VodMovieVideoPlayActivity.this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("stfor", "2");
        parms.putString("act", "1");
        parms.putString("ch", "");
        parms.putString("datetime", "");
        parms.putString("vodid", vodid);
        MyVolley.init(VodMovieVideoPlayActivity.this);
        ShowProgressDilog(VodMovieVideoPlayActivity.this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", ADD_FAVORITES, this, parms, false);
    }

    void RemoveFavorites() {
        JSONRequestResponse mResponse = new JSONRequestResponse(VodMovieVideoPlayActivity.this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("stfor", "2");
        parms.putString("act", "11");
        parms.putString("ch", "");
        parms.putString("datetime", "");
        parms.putString("vodid", vodid);
        MyVolley.init(VodMovieVideoPlayActivity.this);
        ShowProgressDilog(VodMovieVideoPlayActivity.this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", REMOVE_FAVORITES, this, parms, false);
    }

    void PlayMovie(String id) {
        container_modified_livechannels.setVisibility(View.GONE);
        Log.i("ididid", id);
        JSONRequestResponse mResponse = new JSONRequestResponse(VodMovieVideoPlayActivity.this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("vodid", id);
        parms.putString("cookiekey", Constant.getDeviceUUID(this));
        MyVolley.init(VodMovieVideoPlayActivity.this);
        ShowProgressDilog(this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "loadvod2.php", MOVIE_PLAY, this, parms, false);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
        DismissProgress(this);
        Constant.ShowErrorToast(this);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        if (requestCode == MOVIE_PLAY) {
            DismissProgress(VodMovieVideoPlayActivity.this);
            videoplayurl = new StringBuilder();

            System.out.println("Vod Movie Videoplay response ----------------" + response.toString());

            String errorcode = "";
            try {
                try {
                    errorcode = response.getString("error");
                } catch (Exception e) {
                    errorcode = response.getString("errorcode");
                }

                JSONArray results = response.getJSONArray("results");
//                for (int i = 0; i < results.length(); i++) {
//                    JSONObject object = results.getJSONObject(i);
//                    videoplayurl.append(object.getString("urlport")).append(object.getString("file"));
//                }
                JSONArray fulllinks = response.getJSONArray("fulllinks");
                videoBackupLists = new ArrayList<String>();
                JSONObject vodinfo = new JSONObject();// = response.getJSONObject("vodinfo");

                for (int i = 0; i < fulllinks.length(); i++) {

                    JSONObject arr = fulllinks.getJSONArray(i).getJSONObject(0);
                    videoBackupLists.add(arr.optString("playurl"));
                    if (i==0)
                        vodinfo = arr;
                    if (i==0)
                        videoplayurl.append(videoBackupLists.get(i));
                }
//                System.out.println("Vod Movie object toString ----------------" + vodinfo.toString());
                channelname_vod_movie_videoplay.setText(vodinfo.getString("name"));
                year_vod_movie_videoplay.setText(vodinfo.getString("year"));
                views_vod_movie_videoplay.setText("צפיות: " + vodinfo.getString("views"));
                upload_date_vod_movie_videoplay.setText("הועלה בתאריך: " + Constant.getUnixDate(vodinfo.getString("created")));
                description_vod_movie_videoplay.setText(vodinfo.getString("description"));

                try {
                    if (vodinfo.optString("genre").equalsIgnoreCase("")) {
                        genre_vod_movie_videoplay.setText("ז'אנר: " + "מידע אינו זמין");
                    } else {
                        if (vodinfo.getString("genre").length() > 11)
                            genre_vod_movie_videoplay.setText("ז'אנר: " + vodinfo.getString("genre").substring(0, 11) + "...");
                        else
                            genre_vod_movie_videoplay.setText("ז'אנר: " + vodinfo.getString("genre"));

                    }
                } catch (Exception e) {
                    genre_vod_movie_videoplay.setText("ז'אנר: " + "מידע אינו זמין");
                }
                try {
                    duration_vod_movie_videoplay.setText("זמן: " + Constant.hour_min_format(Constant.parseInt(vodinfo.getString("length"))));
                } catch (Exception e) {
                    duration_vod_movie_videoplay.setText("זמן: " + "מידע אינו זמין");
                }

                try {
                    if (Constant.parseFloat(vodinfo.getString("stars")) > 5)
                        ratingBar_vod_movie_videoplay.setRating(Constant.parseFloat(vodinfo.getString("stars")) / 2);
                    else
                        ratingBar_vod_movie_videoplay.setRating(Constant.parseFloat(vodinfo.getString("stars")));
                } catch (Exception e) {
                    ratingBar_vod_movie_videoplay.setRating(0.0f);
                }
                String temp = new String();
                try {
                    JSONArray jsonPictureList = vodinfo.getJSONArray("picture");
                    if (jsonPictureList != null && jsonPictureList.length() > 0) {
                        JSONObject item = jsonPictureList.getJSONObject(0);
                        if (item != null) {
                            temp = "//images.peer5.net/images/b/" + item.optString("big", "no data");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    temp = "no data";
                }
                ImageCacheUtil.with(VodMovieVideoPlayActivity.this)
                        .load("http:" + temp)
                        .resize(200, 200)
                        .cacheUsage(false, true)
                        .into(channel_icon_vod_movie_videoplay);

                JSONObject pathinfo = response.optJSONObject("pathinfo");

                JSONObject object = pathinfo.optJSONObject("0");

                movie_play_cat = object.optString("name");

            } catch (Exception e) {

                e.printStackTrace();

            }
            tvcat_name.setText(" | " + movie_play_cat);

            if (errorcode.equalsIgnoreCase("101")) {
                Constant.showPopup_Error_basic_package(VodMovieVideoPlayActivity.this);

            } else if (errorcode.equalsIgnoreCase("999")) {
                Constant.showPopup_Error_expire_package(VodMovieVideoPlayActivity.this);
            } else if (errorcode.equalsIgnoreCase("58403")) {
                Constant.showPopup_VPN_Block(VodMovieVideoPlayActivity.this);
            } else if (errorcode.equalsIgnoreCase("998")) {
                try {
                    Constant.showPopup_Error_freeze_package(VodMovieVideoPlayActivity.this, Constant.getUnixDate(response.getString("endtime")));
                } catch (Exception e) {
                    Constant.showPopup_Error_freeze_package(VodMovieVideoPlayActivity.this, "");
                }
            } else if (errorcode.equalsIgnoreCase("99")) {
                UiUtils.logout(this);
                return;
            } else if (errorcode.equalsIgnoreCase("997")) {
                Constant.showPopup_Error_suspend_package(VodMovieVideoPlayActivity.this);
            } else {
//                Set_player_video(Uri.parse(videoplayurl.toString()));
                if (Pausestate_lastposition != C.POSITION_UNSET)
                    Set_player_video_to_postion(Uri.parse(videoplayurl.toString()), Pausestate_lastposition);
                else
                    Set_player_video(Uri.parse(videoplayurl.toString()));
                opened_time = System.currentTimeMillis();
            }
            container_modified_livechannels.setVisibility(View.VISIBLE);
            exo_progress.setVisibility(View.INVISIBLE);
        } else if (requestCode == ADD_FAVORITES) {
            DismissProgress(VodMovieVideoPlayActivity.this);
            System.out.println("Response for add Live Channels --------" + response.toString());

            Toast.makeText(VodMovieVideoPlayActivity.this, "הוסף בהצלחה למועדפים", Toast.LENGTH_SHORT).show();

            add_to_fav_movie_movie_text.setText("הסר ממועדפים");
            add_To_fav_img_movie.setImageResource(R.drawable.remove_fav);
            isinfav = "1";
        } else if (requestCode == REMOVE_FAVORITES) {
            DismissProgress(VodMovieVideoPlayActivity.this);
            System.out.println("Response for Remove Live Channels --------" + response.toString());

            Toast.makeText(VodMovieVideoPlayActivity.this, "הוסר בהצלחה מהמועדפים", Toast.LENGTH_SHORT).show();

            add_to_fav_movie_movie_text.setText("הוסף למועדפים");
            add_To_fav_img_movie.setImageResource(R.drawable.add_fav);
            isinfav = "0";
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void DismissProgress(Context c) {
        if (pDialog != null)
            pDialog.dismiss();
    }

    private void nextFullBackPlay() {
        if (pDialog != null) {
            pDialog.dismiss();
        }

        if (player != null) {
            int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
            if (_Pausestate_WindowIndex_ >= 0) {
                Pausestate_Window_Index = _Pausestate_WindowIndex_;
            }
            long _Pausestate_lastposition = player.getCurrentPosition();
            if (_Pausestate_lastposition > 0) {
                Pausestate_lastposition = _Pausestate_lastposition;
            }
        }

        try {
            flag_glob++;
            Set_player_video_to_postion(Uri.parse(videoBackupLists.get(flag_glob)), Pausestate_lastposition);
        }
        catch (Exception e)
        {
            flag_glob = 0;
            PlayMovie(vodid);

        }
    }

    @Override
    public void onResume() {

        mActivityAlive = true;

        showOfflineMessage(false);

        ConnectivityReceiver.mcontext=this;
        intui_player();
        System.out.println("Vod MovievideoBackupLists ----------------" + videoBackupLists);

        if(Pausestate_lastposition != C.POSITION_UNSET && flag_delete != true) {

            container_modified_livechannels.setVisibility(View.VISIBLE);
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();
            if (flag_glob > 0) {
                try
                {
                    Set_player_video_to_postion(Uri.parse(videoBackupLists.get(flag_glob)),Pausestate_lastposition);
                }
                catch (Exception e)
                {
                    Set_player_video_to_postion(Uri.parse(videoBackupLists.get(0)),Pausestate_lastposition);
                }

            } else {
                if (videoBackupLists.isEmpty()) {
                    finish();
                } else
                    Set_player_video_to_postion(Uri.parse(videoBackupLists.get(0)),Pausestate_lastposition);
            }
        }
        player.addListener(playerEventListener);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        super.onResume();
        Runnable runnable = new CountDownRunner1();
        myThread1 = new Thread(runnable);
        myThread1.start();
    }

    void saveTimingPosition() {
        SharedPreferences rememberPos = this.getSharedPreferences("position", MODE_PRIVATE);
        SharedPreferences.Editor posEditor = rememberPos.edit();
        posEditor.putInt("Pausestate_WindowIndex_", Pausestate_Window_Index);
        posEditor.putLong("Pausestate_lastposition", Pausestate_lastposition);
        Log.i("Pausestate"+Pausestate_lastposition,"asdfasdf");
        posEditor.commit();
    }
    public void saveData() {
        SharedPreferences remember = getSharedPreferences("remember",MODE_PRIVATE);
        SharedPreferences.Editor editor = remember.edit();
        editor.putString("vodid", vodid);
        editor.putString("Where","MoviePlay");
        editor.putBoolean("isHome",true);
        editor.commit();
    }
    @Override
    public void onPause() {


        if (myThread1 != null) {
            myThread1.interrupt();
            myThread1 = null;
        }
        ConnectivityReceiver.mcontext=null;
        if (player != null) {
            int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
            if (_Pausestate_WindowIndex_ >= 0) {
                Pausestate_Window_Index = _Pausestate_WindowIndex_;
            }
            long _Pausestate_lastposition = player.getCurrentPosition();
            if (_Pausestate_lastposition > 0) {
                Pausestate_lastposition = _Pausestate_lastposition;
            }
        }
        
        if (isBackClose != true) {
			flag_delete = false;
            saveData();
            Log.i("vodidvodid", ""+vodid);
            saveTimingPosition();
        }
        mActivityAlive = false;
        releasePlayer();

        //dialog dismiss
        DismissProgress(this);
        pDialog = null;

        nextFullBackHandler.removeCallbacks(nextFullBackRunnable);

        super.onPause();
    }

    @Override
    public void onStop() {
        ConnectivityReceiver.mcontext=null;
        if (player != null) {
            int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
            if (_Pausestate_WindowIndex_ >= 0) {
                Pausestate_Window_Index = _Pausestate_WindowIndex_;
            }
            long _Pausestate_lastposition = player.getCurrentPosition();
            if (_Pausestate_lastposition > 0) {
                Pausestate_lastposition = _Pausestate_lastposition;
            }
        }

        mActivityAlive = false;
        releasePlayer();

        //dialog dismiss
        DismissProgress(this);
        pDialog = null;

        nextFullBackHandler.removeCallbacks(nextFullBackRunnable);

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mHandlerForSmallConnection != null) {
            mHandlerForSmallConnection.removeCallbacks(null);
            mHandlerForSmallConnection = null;
        }
        if (mTimerForSmallConnection != null) {
            mTimerForSmallConnection.cancel();
            mTimerForSmallConnection = null;
        }
        mActivityAlive = false;
        releasePlayer();

        nextFullBackHandler.removeCallbacks(nextFullBackRunnable);

        super.onDestroy();
    }

    private void releasePlayer() {
        // Let JW Player know that the app is being destroyed
        if (player != null) {
            player.removeListener(playerEventListener);
            player.stop();
//            player.setVideoSurfaceHolder(null);
            player.release();
            player = null;
        }

        // clear the EXOPlayer's Media Cache
        ResetCacheService.deleteEXOMediaCache(this);

        try { Thread.sleep(200); } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Set fullscreen when the device is rotated to landscape
        // playerView_vod_movie_videoplay.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT)
        {
            exo_progress.requestFocus();
           /* playerView_vod_movie_videoplay.showController();
            long currentposition=player.getCurrentPosition();
            long seekto=(currentposition-10800000);
            if (seekto<0)
            {
                player.seekTo(C.TIME_UNSET);
            }
            else player.seekTo(seekto);
*/
            if(container_modified_livechannels.getVisibility()==View.VISIBLE) {
                container_modified_livechannels.setVisibility(View.GONE);
            }
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            exo_progress.requestFocus();
           /* playerView_vod_movie_videoplay.showController();
            long currentposition=player.getCurrentPosition();
            long seekto=(currentposition+10800000);
            if (seekto>player.getDuration())
            {
                player.seekTo(player.getDuration());
            }
            else player.seekTo(seekto);*/
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_DPAD_UP)
        {
            add_fav_.requestFocus();
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();
            if(container_modified_livechannels.getVisibility()==View.GONE)
            {
                container_modified_livechannels.setVisibility(View.VISIBLE);
                framlay_image.requestFocus();
            }
            exo_progress.setVisibility(View.INVISIBLE);
            playerView_vod_movie_videoplay.hideController();
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
        {
            flag_pressed="pressed";

            pressed_time=System.currentTimeMillis();
            add_fav_.requestFocus();
            if(container_modified_livechannels.getVisibility()==View.GONE)
            {
                container_modified_livechannels.setVisibility(View.VISIBLE);
                framlay_image.requestFocus();
            }
            exo_progress.setVisibility(View.INVISIBLE);
            playerView_vod_movie_videoplay.hideController();
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT)
        {
            playerView_vod_movie_videoplay.showController();
           /* long currentposition=player.getCurrentPosition();
            long seekto=(currentposition-30000);
            if (seekto<0)
            {
                player.seekTo(C.TIME_UNSET);
            }
            else player.seekTo(seekto);*/
            exo_progress.requestFocus();
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
            exo_progress.setVisibility(View.VISIBLE);
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            playerView_vod_movie_videoplay.showController();
          /*  long currentposition=player.getCurrentPosition();
            long seekto=(currentposition+30000);
            if (seekto>player.getDuration())
            {
                player.seekTo(player.getDuration());
            }
            else player.seekTo(seekto);*/
            exo_progress.requestFocus();
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
            exo_progress.setVisibility(View.VISIBLE);
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == KeyEvent.KEYCODE_ENTER)
        {
            flag_pressed = "pressed";

            pressed_time = System.currentTimeMillis();

            if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                if (player != null) {
                    if (player.getPlayWhenReady()) {
                        player.setPlayWhenReady(false);
                    } else player.setPlayWhenReady(true);
                }
            }
            if (container_modified_livechannels.getVisibility() == View.GONE) {
                container_modified_livechannels.setVisibility(View.VISIBLE);
                framlay_image.requestFocus();
            }
            exo_progress.setVisibility(View.INVISIBLE);
        }
        else if(keyCode==KeyEvent.KEYCODE_MEDIA_REWIND)
        {
            playerView_vod_movie_videoplay.showController();
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();
            if (player != null) {
                long currentposition = player.getCurrentPosition();
//            long seekto=(currentposition-30000);
                long seekto = (currentposition - 180000);
                if (seekto < 0) {
                    player.seekTo(C.TIME_UNSET);
                } else player.seekTo(seekto);
            }
            playerView_vod_movie_videoplay.setControllerShowTimeoutMs(8000);//
        }
        else if(keyCode==KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        {
            playerView_vod_movie_videoplay.showController();
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();
            if (player != null) {
                long currentposition = player.getCurrentPosition();
//            long seekto=(currentposition+30000);
                long seekto = (currentposition + 180000);
                if (seekto > player.getDuration()) {
                    player.seekTo(player.getDuration());
                } else player.seekTo(seekto);
            }
            playerView_vod_movie_videoplay.setControllerShowTimeoutMs(8000);
        }
        else if(keyCode==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        {
            playerView_vod_movie_videoplay.showController();
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();
            if (player != null) {
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                } else player.setPlayWhenReady(true);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    @Override
    public void OnNetworkChange() {
        if (NetworkUtil.checkNetworkAvailable(this)) {
            //Remove the conneting message and reconnect the player
            showOfflineMessage(false);
        }
        else
        {
            //Show reconnecting message
            DismissProgress(VodMovieVideoPlayActivity.this);
            showOfflineMessage(true);
        }
    }

    class CountDownRunner1 implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork1();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doWork1() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    long current_time = 0;
                    current_time = System.currentTimeMillis();
                    if (pressed_time != 0) {
                        if (current_time - pressed_time >= 8000) {
                            if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                                container_modified_livechannels.setVisibility(View.GONE);
                                playerView_vod_movie_videoplay.hideController();
                                //exo_progress.requestFocus();
                            }
                        }
                    } else {
                        if (opened_time != 0) {
                            if (current_time - opened_time >= 8000) {
                                if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                                    container_modified_livechannels.setVisibility(View.GONE);
                                    //exo_progress.requestFocus();
                                    playerView_vod_movie_videoplay.hideController();
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    public void clearSavedData() {
        SharedPreferences rememberState = getSharedPreferences("remember", LoginActivity.MODE_PRIVATE);
        if (rememberState != null) {
            SharedPreferences.Editor editor = rememberState.edit();
            editor.clear();
            editor.commit();
        }
        SharedPreferences rememberPos = getSharedPreferences("position", LoginActivity.MODE_PRIVATE);

        if (rememberPos != null) {
            SharedPreferences.Editor editor = rememberPos.edit();
            editor.clear();
            editor.commit();
        }
    }
    @Override
    public void onBackPressed() {
        isBackClose = false;
        if(container_modified_livechannels.getVisibility()==View.VISIBLE)
        {
            container_modified_livechannels.setVisibility(View.GONE);
            //exo_progress.requestFocus();
        }
        else
        {
            position1 = 0;
            clearSavedData();
            isBackClose = true;
            super.onBackPressed();
        }
    }

    private Player.EventListener playerEventListener = new Player.EventListener(){

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            flag_delete = false;
            SharedPreferences remember = getSharedPreferences("remember",MODE_PRIVATE);
            SharedPreferences.Editor editor = remember.edit();
            editor.putString("vodid", vodid);

            editor.putString("Where","MoviePlay");
            editor.putBoolean("isHome",true);
            editor.commit();

            if (player != null) {
                int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
                if (_Pausestate_WindowIndex_ >= 0) {
                    Pausestate_Window_Index = _Pausestate_WindowIndex_;
                }
                long _Pausestate_lastposition = player.getCurrentPosition();
                if (_Pausestate_lastposition > 0) {
                    Pausestate_lastposition = _Pausestate_lastposition;
                }
            }

            saveTimingPosition();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED ) {
                player.setPlayWhenReady(false);
                player.stop();
                isBackClose = true;
                clearSavedData();
                finish();
            }
            if (playbackState == Player.STATE_READY ) {
                showOfflineMessage(false);
                DismissProgress(VodMovieVideoPlayActivity.this);
                nextFullBackHandler.removeCallbacks(nextFullBackRunnable);
            }
            if (playbackState==Player.STATE_BUFFERING)
            {
                if (NetworkUtil.checkNetworkAvailable(VodMovieVideoPlayActivity.this)) {
                    //Remove the conneting message and reconnect the player
                    ShowProgressDilog(VodMovieVideoPlayActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkUtil.hasActiveInternetConnection(VodMovieVideoPlayActivity.this)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DismissProgress(VodMovieVideoPlayActivity.this);
                                    }
                                });
                            }
                        }
                    }).start();
                }
                else
                {//Show reconnecting message
                    DismissProgress(VodMovieVideoPlayActivity.this);
                }
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            showOfflineMessage(true);
            DismissProgress(VodMovieVideoPlayActivity.this);

            // should wait for 3 seconds before reconnecting
            nextFullBackHandler.postDelayed(nextFullBackRunnable, Constant.RECONNECT_NEXT_TIME_OUT);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

    private void showOfflineMessage(boolean bShow) {
        if (player == null)
            return;

        tv_connectionstatus_movies.setVisibility(View.GONE);
        layConnectionState.setVisibility(View.GONE);

        if (player.getCurrentPosition() >= player.getBufferedPosition()) {
            if (bShow) {
                // hide small offline message
                if (mTimerForSmallConnection != null) {
                    mTimerForSmallConnection.cancel();
                    mTimerForSmallConnection = null;
                }
                if (!Constant.isInternetAvailable())
                    tv_connectionstatus_movies.setVisibility(View.VISIBLE);
            } else {
                tv_connectionstatus_movies.setVisibility(View.GONE);
            }
        } else {
            Log.d("Player", "Cache playing now");
            // bshow is the meaning of OFFLINE
            showSmallNetworkStatus(bShow);
        }
    }

    private void showSmallNetworkStatus(boolean isOffline){

        if (mLastConnectionStatus == !isOffline) {
            return;
        }
        mLastConnectionStatus = !isOffline;

        txtConnectionState.setText(getConnectionSmallMessage(isOffline));

        if(isOffline && mTimerForSmallConnection == null){
            // remove online handler
            if (mHandlerForSmallConnection != null) {
                mHandlerForSmallConnection.removeCallbacks(null);
                mHandlerForSmallConnection = null;
            }

            layConnectionState.setVisibility(View.VISIBLE);
            mTimerForSmallConnection = new Timer();
            mTimerForSmallConnection.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (layConnectionState.isShown()) {
                                layConnectionState.setVisibility(View.GONE);
                            } else {
                                layConnectionState.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }, 0, 1000);
        } else if (!isOffline && mHandlerForSmallConnection == null){
            // remove offline timer
            if (mTimerForSmallConnection != null) {
                mTimerForSmallConnection.cancel();
                mTimerForSmallConnection = null;
            }

            layConnectionState.setVisibility(View.VISIBLE);
            mHandlerForSmallConnection = new Handler();
            mHandlerForSmallConnection.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layConnectionState.setVisibility(View.GONE);
                        }
                    });
                }
            }, 5000);
        }
    }

    private SpannableString getConnectionSmallMessage(boolean isOffline) {
        String text = getResources().getString(R.string.internet_connection_on_small_message);
        SpannableString spanText = SettextColorNew("ON", text, this.getResources().getColor(R.color.green_full));
        if (isOffline) {
            text = getResources().getString(R.string.internet_connection_off_small_message);
            spanText = SettextColorNew("OFF", text, this.getResources().getColor(R.color.red_full));
        }
        return spanText;
    }

    private SpannableString SettextColorNew(String mySearchedString, String myString, int color) {
        SpannableString ss = new SpannableString(myString);
        Pattern pattern = Pattern.compile(mySearchedString);
        Matcher matcher = pattern.matcher(ss);
        while (matcher.find()) {
            ss.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ss;
    }
}
