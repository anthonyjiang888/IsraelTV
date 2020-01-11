package israel13.androidtv.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
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
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import israel13.androidtv.CallBacks.ConnectionChange;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.Fragments.All_record_Fragment;
import israel13.androidtv.Fragments.LoadRecordList_Fragment;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.Others.ConnectivityReceiver;
import israel13.androidtv.Others.CustomTimeBar_ForPlayer;
import israel13.androidtv.R;
import israel13.androidtv.Service.ResetCacheService;
import israel13.androidtv.Setter_Getter.SetgetLoadScheduleRecord;
import israel13.androidtv.Setter_Getter.SetgetRecordtotal_video_backup;
import israel13.androidtv.Setter_Getter.SetgetSearchRecordDatesDetails;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomCacheDataSourceFactory;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.UiUtils;

/**
 * Created by krishanu on 06/06/17.
 */
public class Records_Play_Activity extends AppCompatActivity implements IJSONParseListener,ConnectionChange{
    private boolean isBackClose;
    private LinearLayout linearlayout_modified_livechannels_close;
    private int LOAD_VIDEO = 109;
    private int ADD_FAVORITES = 150;
    private int LOAD_RECORD_SCHEDULE = 115;
    private int REMOVE_FAVORITES = 170;
    private int LOAD_RECORD_SCHEDULE_SINGLE_VIDEO = 116;
    private ProgressDialog pDialog = null;
    private SharedPreferences logindetails;
    private StringBuilder builder;

    private   ArrayList<SetgetLoadScheduleRecord> LoadScheduleRecordsList;
    private RelativeLayout progressbar_layout_live_channels;
    private TextView tv_channel_name, start_time, end_time, tv_description_of_show, tv_title_of_show, tv_no_of_channel_remote, dday_records, ddate_records;
    private TextView epg_add_to_fav, genre, epg_move_to_record_mode;
    private ImageView Iv_next_show, Iv_up_records, Iv_down_records, Iv_channel_played, Iv_channel_records, Iv_records_not_playing;
    private ProgressBar pgbar_livetime;
    private int running_show_time;
    private Thread myThread1;
    private ViewGroup jwPlayerViewContainer;

    // small offline/online message
    private Timer mTimerForSmallConnection;
    private Handler mHandlerForSmallConnection;
    private LinearLayout layConnectionState;
    private TextView txtConnectionState;
    private boolean mLastConnectionStatus = true;
    public static boolean flag_delete;
    public static String start_show_time;
    public static int left_pressed = 1;
    public int selected_channel_records_position = 0;
    public static int selected_channel_records_position_default = 0;
    public String channel_id;
    public int channel_up_flag = 1;
    public int channel_down_flag = 1;
    public String no_load_video = "", fav_flag = "";
    public static String record_time;
    public static String rdatetime = "", show_pic = "";
    public static String rating_record = "";
    public String flag_pressed = "";
    public static int current_position = 0;
    public static boolean isSearch;
    private String date;
    private RelativeLayout llay_channel_img;
    private StringBuilder video_url;
    private ArrayList<String> videoLists;
    private RatingBar ratingbars_record;
    private RelativeLayout container_modified_livechannels;
    private long pressed_time = 0;
    private long opened_time = 0;
    private ArrayList<SetgetLoadScheduleRecord> videoBackupLists;
    private ArrayList<SetgetRecordtotal_video_backup> videoTotal_BackupLists;
    private int flag_glob=0;
    private ArrayList<SetgetLoadScheduleRecord> LoadScheduleRecords_;
    private List<MediaSource> jw_playlist;
    private int error_count=0;
    private SimpleExoPlayerView playerView_record_play;
    private SimpleExoPlayer player;
    private String TAG="Record";
    private int lastWindowIndex = 0;
    private CustomTimeBar_ForPlayer exo_progress;
    private LinearLayout tv_connectionstatus_records;

    private int Pausestate_WindowIndex_ = C.INDEX_UNSET;
    private long Pausestate_lastposition = C.POSITION_UNSET;
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
        setContentView(R.layout.play_records_activity);
        selected_channel_records_position_default = current_position;
        selected_channel_records_position = current_position;

        jwPlayerViewContainer = (ViewGroup) findViewById(R.id.container_records);
        container_modified_livechannels = (RelativeLayout) findViewById(R.id.container_modified_record);
        tv_channel_name = (TextView) findViewById(R.id.tv_channel_name_records);
        start_time = (TextView) findViewById(R.id.start_time_records);
        end_time = (TextView) findViewById(R.id.end_time_records);
        tv_description_of_show = (TextView) findViewById(R.id.tv_description_of_show_records);
        tv_title_of_show = (TextView) findViewById(R.id.tv_title_of_show_records);
        tv_no_of_channel_remote = (TextView) findViewById(R.id.tv_no_of_channel_remote);
        dday_records = (TextView) findViewById(R.id.dday_records);
        ddate_records = (TextView) findViewById(R.id.ddate_records);
        genre = (TextView) findViewById(R.id.genre_records);
        ratingbars_record = (RatingBar) findViewById(R.id.ratingbars_record);

        LoadScheduleRecordsList = new ArrayList<SetgetLoadScheduleRecord>();
        llay_channel_img = (RelativeLayout) findViewById(R.id.llay_channel_img);
        llay_channel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reload_selected_video(selected_channel_records_position);
            }
        });

        Iv_channel_records = (ImageView) findViewById(R.id.Iv_channel_records);
        Iv_records_not_playing = (ImageView) findViewById(R.id.Iv_records_not_playing);
        tv_connectionstatus_records = (LinearLayout) findViewById(R.id.tv_connectionstatus_records);
        Iv_up_records = (ImageView) findViewById(R.id.Iv_up_records);
        Iv_down_records = (ImageView) findViewById(R.id.Iv_down_records);
        if (isSearch == false) {
            LoadScheduleRecord(LoadRecordList_Fragment.date);
            Log.i("rdatetimedate2", rdatetime);
        }
        else {
            LoadScheduleRecordsList = new ArrayList<SetgetLoadScheduleRecord>();
            LoadScheduleRecordsList = LoadRecordList_Fragment.LoadScheduleRecordsList;
            Refresh_Start_End_Reached();
        }
        playerView_record_play = (SimpleExoPlayerView) findViewById(R.id.playerView_record_play);
        Iv_up_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed_time = System.currentTimeMillis();
                if (LoadScheduleRecordsList.size() > 0) {
                    if (selected_channel_records_position >= LoadScheduleRecordsList.size() - 1) {
//                        selected_channel_records_position = -1;
                        return;
                    }
                    if(!Iv_down_records.isShown())
                        Iv_down_records.setVisibility(View.VISIBLE);
                    int position = selected_channel_records_position + channel_up_flag;
                    Reload_selected_records(position);
                    selected_channel_records_position = position;
                    Start_End_Reached();
                } else
                    Toast.makeText(Records_Play_Activity.this, "אין עוד ערוץ זמין", Toast.LENGTH_SHORT).show();
                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
//                emptyField();
//                LoadScheduleRecord(date);
//                pgbar_livetime.setVisibility(View.VISIBLE);
            }
        });

        Iv_down_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed_time = System.currentTimeMillis();
                if (LoadScheduleRecordsList.size() > 0) {
                    if (selected_channel_records_position <= 0) {
//                        selected_channel_records_position = LoadScheduleRecordsList.size();
                        return;
                    }
                    if(!Iv_up_records.isShown())
                        Iv_up_records.setVisibility(View.VISIBLE);
                    int position = selected_channel_records_position - channel_down_flag;
                    Reload_selected_records(position);
                    selected_channel_records_position = position;
                    Start_End_Reached();
                } else

                    MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                        @Override
                        public boolean apply(Request<?> request) {
                            return true;
                        }
                    });
//                emptyField();
//                LoadScheduleRecord(date);
//                pgbar_livetime.setVisibility(View.VISIBLE);
            }
        });

        layConnectionState = (LinearLayout)findViewById(R.id.layout_connection_state);
        txtConnectionState = (TextView)findViewById(R.id.text_connection_state);

        exo_progress = (CustomTimeBar_ForPlayer) findViewById(R.id.exo_progress);
        logindetails = this.getSharedPreferences("logindetails", MODE_PRIVATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences rememberPos = this.getSharedPreferences("position", MODE_PRIVATE);

        Pausestate_lastposition = rememberPos.getLong("Pausestate_lastposition",C.POSITION_UNSET);
        Pausestate_WindowIndex_ = rememberPos.getInt("Pausestate_WindowIndex_", C.INDEX_UNSET);
        flag_glob = rememberPos.getInt("flag_glob", 0);
        LoadScheduleRecords_=new ArrayList<>();
        videoTotal_BackupLists=new ArrayList<>();

        if (LoadRecordList_Fragment.LoadScheduleRecordsList == null) {
            LoadRecordList_Fragment.LoadScheduleRecordsList = new ArrayList<SetgetLoadScheduleRecord>();
        }
        if(LoadRecordList_Fragment.LoadScheduleRecordsList.size()>0)
        {
            for(int i=0;i<LoadRecordList_Fragment.LoadScheduleRecordsList.size();i++)
            {
                LoadScheduleRecords_.add(LoadRecordList_Fragment.LoadScheduleRecordsList.get(i));
            }
        }

        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Call_api(rdatetime);
        tv_channel_name.setText(AllChannels_Fragment.channel_name);
        try {
            ImageCacheUtil.with(this).load("http:" + show_pic)
                    .resize(200,200)
                    .cacheUsage(false, true)
                    .into(Iv_channel_records);
        } catch (Exception e) {
            Picasso.with(this)
                    .load(R.drawable.channel_placeholder)
                    .into(Iv_channel_records);
        }
        if (show_pic.equalsIgnoreCase("")) {
            Picasso.with(this)
                    .load(R.drawable.channel_placeholder)
                    .into(Iv_channel_records);
        }

        try {
            ratingbars_record.setRating(Constant.parseFloat(rating_record));
        } catch (Exception e) {
            ratingbars_record.setRating(0);
        }
    }

    void Reload_selected_records(int position) {
        if (position < 0 &&
                position > LoadScheduleRecordsList.size() - 1) return;
        show_pic = LoadScheduleRecordsList.get(position).getShowpic();
        try {
            ImageCacheUtil.with(Records_Play_Activity.this)
                    .load("http:" + show_pic)
                    .resize(200,200)
                    .cacheUsage(false, true)
                    .into(Iv_channel_records);
        } catch (Exception e) {
            Picasso.with(Records_Play_Activity.this).load(R.drawable.channel_placeholder).into(Iv_channel_records);
        }
        if (show_pic.equalsIgnoreCase("")) {
            Picasso.with(Records_Play_Activity.this).load(R.drawable.channel_placeholder).into(Iv_channel_records);
        }
        rating_record = LoadScheduleRecordsList.get(position).getStar();
        try {
            ratingbars_record.setRating(Constant.parseFloat(rating_record));
        } catch (Exception e) {
            ratingbars_record.setRating(0);
        }
        start_time.setText("" + LoadScheduleRecordsList.get(position).getTime());
        tv_title_of_show.setText("" + LoadScheduleRecordsList.get(position).getName());
        tv_description_of_show.setText("" + LoadScheduleRecordsList.get(position).getDescription());

        dday_records.setText(Constant.dayByGivenDate("" + LoadScheduleRecordsList.get(position).getWday()));
        ddate_records.setText(Constant.getUnixDate(LoadScheduleRecordsList.get(position).getRdatetime()));
        if (LoadScheduleRecordsList.get(position).getGenre().equalsIgnoreCase("")) {
            genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
        } else genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(position).getGenre());
        String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(position).getLengthtime()))
                , LoadScheduleRecordsList.get(position).getTime());
        end_time.setText(time);

        if (position != selected_channel_records_position_default) {
            llay_channel_img.setBackgroundResource(R.drawable.button_bg1);
            Iv_records_not_playing.setVisibility(View.VISIBLE);
        } else {
            llay_channel_img.setBackground(null);
            Iv_records_not_playing.setVisibility(View.INVISIBLE);
        }
    }

    void Start_End_Reached(){
        if(selected_channel_records_position == LoadScheduleRecordsList.size() - 1){
            Iv_up_records.setVisibility(View.INVISIBLE);
        }
        if(selected_channel_records_position == 0){
            Iv_down_records.setVisibility(View.INVISIBLE);
        }
    }

    void Refresh_Start_End_Reached(){
        selected_channel_records_position = selected_channel_records_position_default;
        if(selected_channel_records_position == LoadScheduleRecordsList.size() - 1){
            Iv_up_records.setVisibility(View.INVISIBLE);
        }else{
            Iv_up_records.setVisibility(View.VISIBLE);
        }
        if(selected_channel_records_position == 0){
            Iv_down_records.setVisibility(View.INVISIBLE);
        }else{
            Iv_down_records.setVisibility(View.VISIBLE);
        }
    }

    void Reload_selected_video(int position) {
        Pausestate_lastposition = C.POSITION_UNSET;
        Pausestate_WindowIndex_ = C.INDEX_UNSET;
        current_position = position;
        rdatetime = LoadScheduleRecordsList.get(position).getRdatetime();
        show_pic = LoadScheduleRecordsList.get(position).getShowpic();
        rating_record = LoadScheduleRecordsList.get(position).getStar();
        selected_channel_records_position_default = current_position;
        llay_channel_img.setBackground(null);
        Iv_records_not_playing.setVisibility(View.INVISIBLE);
        if (player != null)
            player.stop();
        Call_api(rdatetime);
        Log.i("rdatetime3", rdatetime);
    }

    public void intui_player() {

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
        playerView_record_play.setUseController(true);
       // playerView_record_play.requestFocus();
        // Bind the player to the view.
        playerView_record_play.setPlayer(player);
    }

    public void Set_player_video(List<MediaSource> playlist)
    {
        releasePlayer();
        if (!mActivityAlive) return;
        intui_player();

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(
                playlist.toArray(new MediaSource[playlist.size()]));
        // Prepare the player with the source.
        player.prepare(concatenatedSource);
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        playerView_record_play.setControllerShowTimeoutMs(8000);
        //playerView_record_play.hideController();
    }

    public void Set_player_video_fallback(List<MediaSource> playlist,int current_index,long last_position)
    {
        releasePlayer();
        if (!mActivityAlive) return;
        intui_player();

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(
                playlist.toArray(new MediaSource[playlist.size()]));
        // Prepare the player with the source.
        player.prepare(concatenatedSource);
        try {
            if (current_index >= 0 && last_position >= 0)
                player.seekTo(current_index, last_position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        playerView_record_play.setControllerShowTimeoutMs(8000);
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
    }

    public void Call_api(String r_datetime)
    {
        if (NetworkUtil.checkNetworkAvailable(this)) {
            LoadScheduleRecordSingleVideo(r_datetime);
        }
        else
        {
            ShowErrorAlert(this,"Please check your network connection..");
        }
    }

    public void ShowErrorAlert(final Context c, String text)
    {
        android.app.AlertDialog.Builder alertDialogBuilder;
        ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
        alertDialogBuilder = new  android.app.AlertDialog.Builder(ctx);

        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call_api(rdatetime);
                Log.i("rdatetime4", rdatetime);
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

    void LoadScheduleRecordSingleVideo(String time) {
        JSONRequestResponse mResponse = new JSONRequestResponse(Records_Play_Activity.this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));

        parms.putString("channel", AllChannels_Fragment.channel_id);
        parms.putString("day", "");
        parms.putString("time", time);
        parms.putString("cookiekey", Constant.getDeviceUUID(this));
        MyVolley.init(Records_Play_Activity.this);
        ShowProgressDilog(Records_Play_Activity.this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "loadrecord.php", LOAD_RECORD_SCHEDULE_SINGLE_VIDEO, this, parms, false);
    }

    void LoadScheduleRecord(String date) {
        JSONRequestResponse mResponse = new JSONRequestResponse(Records_Play_Activity.this);
        Bundle parms = new Bundle();
        //  parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("cid", AllChannels_Fragment.channel_id);
        if (!date.equalsIgnoreCase("")) {
            parms.putString("date", date);
        }
        // parms.putString("date", date);
        parms.putString("recordwithdate", "1");
        parms.putString("withdescription", "1");
        MyVolley.init(Records_Play_Activity.this);
        MyVolley.getRequestQueue().getCache().clear();
        ShowProgressDilog(Records_Play_Activity.this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "schbydate.php", LOAD_RECORD_SCHEDULE, this, parms, false);
    }

    private void nextFullBackPlay() {
        if (player != null) {
            int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
            if (_Pausestate_WindowIndex_ >= 0) {
                Pausestate_WindowIndex_ = _Pausestate_WindowIndex_;
            }
            long _Pausestate_lastposition = player.getCurrentPosition();
            if (_Pausestate_lastposition > 0) {
                Pausestate_lastposition = _Pausestate_lastposition;
            }
        }

        try {
            flag_glob++;
            Set_player_video_fallback(videoTotal_BackupLists.get(flag_glob).getPlaylistItems(),Pausestate_WindowIndex_,Pausestate_lastposition);
        }
        catch (Exception e)
        {
            flag_glob = 0;
            Call_api(rdatetime);
            Log.i("rdatetime5", rdatetime);
        }
    }

    @Override
    public void onResume() {


        mActivityAlive = true;

        showOfflineMessage(false);

        ConnectivityReceiver.mcontext=this;
        intui_player();
        if(Pausestate_lastposition != C.POSITION_UNSET && flag_delete != true) {
            container_modified_livechannels.setVisibility(View.VISIBLE);
            Refresh_Start_End_Reached();
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();

            if (videoTotal_BackupLists.size() > 0) {
                try
                {
                    Set_player_video_fallback(videoTotal_BackupLists.get(flag_glob).getPlaylistItems(), Pausestate_WindowIndex_, Pausestate_lastposition);
                }
                catch (Exception e)
                {
                    Set_player_video_fallback(videoTotal_BackupLists.get(0).getPlaylistItems(), Pausestate_WindowIndex_, Pausestate_lastposition);
                }
            } else {
                if (videoTotal_BackupLists.isEmpty()) {
                    SharedPreferences rememberState = getSharedPreferences("remember", MODE_PRIVATE);

                    AllChannels_Fragment.channel_id = rememberState.getString("ChannelId", "");
                    AllChannels_Fragment.channel_name = rememberState.getString("ChannelName", "");
                    AllChannels_Fragment.channel_icon = rememberState.getString("ChIcon", "");
                    Records_Play_Activity.current_position = rememberState.getInt("position", 0);
                    Records_Play_Activity.rdatetime = rememberState.getString("rdatetime", "");
                    Records_Play_Activity.show_pic = rememberState.getString("show_pic", "");
                    Records_Play_Activity.rating_record = rememberState.getString("rating_record", "");
                    Log.i("It's start", Records_Play_Activity.rdatetime);
                    Records_Play_Activity.isSearch = false;
                    LoadRecordList_Fragment.date = rememberState.getString("date", "");
                    LoadScheduleRecord(LoadRecordList_Fragment.date);
                    Log.i("rdatetimedate2", rdatetime);
                    //Call_api(rdatetime);
                    finish();
                } else {
                    Call_api(rdatetime);
                    Log.i("rdatetime1", rdatetime);
                }
            }
        }

        player.addListener(playerEventListener);

       // startPlayer();
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        super.onResume();
        Runnable runnable = new CountDownRunner1();
        myThread1 = new Thread(runnable);
        myThread1.start();
    }
    void saveState(){
        SharedPreferences rememberState = this.getSharedPreferences("remember", MODE_PRIVATE);
        SharedPreferences.Editor editor = rememberState.edit();
        editor.putString("Where","RecordPlay");
        editor.putString("ChannelId",AllChannels_Fragment.channel_id);
        editor.putString("ChannelName", AllChannels_Fragment.channel_name);
        editor.putString("ChIcon",AllChannels_Fragment.channel_icon);
        editor.putBoolean("isHome",true);
        editor.putInt("position", Records_Play_Activity.current_position);
        editor.putString("rdatetime",Records_Play_Activity.rdatetime);
        editor.putString("show_pic", Records_Play_Activity.show_pic);
        editor.putString("rating_record", Records_Play_Activity.rating_record);
        editor.putString("date",LoadRecordList_Fragment.date);
        editor.putBoolean("isSearch",false);
        editor.commit();
        editor.apply();

    }

    void saveTimingPosition() {
        SharedPreferences rememberPos = this.getSharedPreferences("position", MODE_PRIVATE);
        SharedPreferences.Editor posEditor = rememberPos.edit();
        posEditor.putInt("Pausestate_WindowIndex_", Pausestate_WindowIndex_);
        posEditor.putLong("Pausestate_lastposition", Pausestate_lastposition);
        posEditor.putInt("flag_glob", flag_glob);
        Log.i("It's pause", ""+Pausestate_lastposition);
        posEditor.commit();
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
                Pausestate_WindowIndex_ = _Pausestate_WindowIndex_;
            }
            long _Pausestate_lastposition = player.getCurrentPosition();
            if (_Pausestate_lastposition > 0) {
                Pausestate_lastposition = _Pausestate_lastposition;
            }
        }
        
        if (isBackClose != true) {
			flag_delete = false;
            saveState();
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
        // Let JW Player know that the app is going to the background
        ConnectivityReceiver.mcontext=null;
        if (player != null) {
            int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
            if (_Pausestate_WindowIndex_ >= 0) {
                Pausestate_WindowIndex_ = _Pausestate_WindowIndex_;
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            Iv_up_records.setPressed(false);
            Iv_down_records.setPressed(false);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            Iv_up_records.setPressed(false);
            Iv_down_records.setPressed(false);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Set fullscreen when the device is rotated to landscape
       // playerView_load_recordlist.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
        DismissProgress(this);

            //Constant.ShowErrorToast(this);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        DismissProgress(Records_Play_Activity.this);

        if (requestCode == LOAD_RECORD_SCHEDULE_SINGLE_VIDEO) {
            System.out.println("Response for load record list Single Video --------------------" + response.toString());
            String errorcode = "";
            try {
                try {
                    errorcode = response.getString("error");
                } catch (Exception e) {
                    errorcode = response.getString("errorcode");
                }

                JSONObject results = response.getJSONObject("results");
                JSONObject channel = results.getJSONObject("channel");

                JSONArray fulllinks = results.getJSONArray("fulllinks");
                //video_url = new StringBuilder();

                videoTotal_BackupLists=new ArrayList<>();

                for (int i = 0; i < fulllinks.length(); i++) {
                    JSONArray array = fulllinks.getJSONArray(i);
                    SetgetRecordtotal_video_backup setgetRecordtotal_video_backup =new SetgetRecordtotal_video_backup();
                    setgetRecordtotal_video_backup.setFall_back_index(String.valueOf(i));
                    videoBackupLists=new ArrayList<>();
                    jw_playlist=new ArrayList<>();
                    for (int j=0;j<array.length();j++)
                    {
                        try {
                            JSONObject object = array.getJSONObject(j);

                            SetgetLoadScheduleRecord setgetLoadScheduleRecord = new SetgetLoadScheduleRecord();
                            setgetLoadScheduleRecord.setItem_no(String.valueOf(j));
                            setgetLoadScheduleRecord.setChannel(object.getString("channel"));
                            setgetLoadScheduleRecord.setRdatetime(object.getString("rdatetime"));
                            setgetLoadScheduleRecord.setTime(object.getString("time"));
                            setgetLoadScheduleRecord.setName(object.getString("name"));
                            setgetLoadScheduleRecord.setWday(object.getString("wday"));
                            setgetLoadScheduleRecord.setGenre(object.optString("genre", "מידע אינו זמין"));
                            setgetLoadScheduleRecord.setRdate(object.getString("rdate"));
                            setgetLoadScheduleRecord.setLengthtime(object.getString("lengthtime"));
                            setgetLoadScheduleRecord.setWeekno(object.getString("weekno"));
                            setgetLoadScheduleRecord.setDescription(object.getString("description"));
                            setgetLoadScheduleRecord.setShowpic(object.getString("showpic"));
                            setgetLoadScheduleRecord.setPlayurl(object.getString("playurl"));

                            videoBackupLists.add(setgetLoadScheduleRecord);
                            CustomCacheDataSourceFactory dataSourceFactory = new CustomCacheDataSourceFactory(this);
                            MediaSource videoSource = new HlsMediaSource(Uri.parse(object.getString("playurl").toString()), dataSourceFactory, 1, null, null);
                            jw_playlist.add(videoSource);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    setgetRecordtotal_video_backup.setFallback_details(videoBackupLists);
                    setgetRecordtotal_video_backup.setPlaylistItems(jw_playlist);
                    videoTotal_BackupLists.add(setgetRecordtotal_video_backup);
                }

                System.out.println("content----"+videoTotal_BackupLists.size());

                JSONObject show = results.getJSONObject("show");
                start_time.setText(show.getString("time"));
                tv_title_of_show.setText(show.getString("name"));
                tv_description_of_show.setText(show.getString("description"));

                dday_records.setText(Constant.dayByGivenDate(show.getString("wday")));
                rdatetime = show.getString("rdatetime");
                ddate_records.setText(Constant.getUnixDate(show.getString("rdatetime")));

                String time = Constant.getTimeByAdding((Constant.parseInt(show.getString("lengthtime")))
                        , show.getString("time"));

                end_time.setText(time);

                try {
                    if (show.getString("genre").equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else genre.setText("ז'אנר" + ": " + show.getString("genre"));
                } catch (JSONException e) {
                    genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                }

                JSONArray playlist = results.getJSONArray("playlist");

                videoLists = new ArrayList<>();
                video_url = new StringBuilder();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (errorcode.equalsIgnoreCase("101")) {
                Constant.showPopup_Error_basic_package(Records_Play_Activity.this);
            }
            else if (errorcode.equalsIgnoreCase("58403"))
            {
                Constant.showPopup_VPN_Block(Records_Play_Activity.this);
            }else if (errorcode.equalsIgnoreCase("999")) {
                Constant.showPopup_Error_expire_package(Records_Play_Activity.this);
            } else if (errorcode.equalsIgnoreCase("998"))
            {
                try {
                    Constant.showPopup_Error_freeze_package(Records_Play_Activity.this,Constant.getUnixDate(response.getString("endtime")));
                }
                catch (Exception e)
                {
                    Constant.showPopup_Error_freeze_package(Records_Play_Activity.this,"");
                }
            }
            else if (errorcode.equalsIgnoreCase("99"))
            {
                UiUtils.logout(this);
                return;
            }
            else if (errorcode.equalsIgnoreCase("997"))
            {
                Constant.showPopup_Error_suspend_package(Records_Play_Activity.this);
            }
            else {
               // video = new PlaylistItem(videoLists.get(0));
//                Set_player_video(videoTotal_BackupLists.get(0).getPlaylistItems());
                Set_player_video_fallback(videoTotal_BackupLists.get(0).getPlaylistItems(), Pausestate_WindowIndex_, Pausestate_lastposition);
                opened_time=System.currentTimeMillis();
            }
        } else if (requestCode == LOAD_RECORD_SCHEDULE) {
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
                Refresh_Start_End_Reached();
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
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
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
            DismissProgress(Records_Play_Activity.this);
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
                                //exo_progress.requestFocus();
                                //playerView_record_play.requestFocus();
                            }
                        }
                    } else {
                        if (opened_time != 0) {
                            if (current_time - opened_time >= 8000) {
                                if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                                    container_modified_livechannels.setVisibility(View.GONE);
                                    //exo_progress.requestFocus();
                                  //  playerView_record_play.requestFocus();
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


        if(container_modified_livechannels.getVisibility()==View.VISIBLE)
        {
            container_modified_livechannels.setVisibility(View.GONE);
            //exo_progress.requestFocus();
           // playerView_record_play.requestFocus();
        }
        else
        {
            isBackClose = true;
            clearSavedData();
            super.onBackPressed();
        }
    }

    private Player.EventListener playerEventListener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            Log.v(TAG, "Listener-onTimelineChanged...");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.v(TAG, "Listener-onTracksChanged...");

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.v(TAG, "Listener-onLoadingChanged...isLoading:" + isLoading);
            flag_delete = false;
            saveState();

            if (player != null) {
                int _Pausestate_WindowIndex_ = player.getCurrentWindowIndex();
                if (_Pausestate_WindowIndex_ >= 0) {
                    Pausestate_WindowIndex_ = _Pausestate_WindowIndex_;
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
            if (playbackState == Player.STATE_ENDED) {
                player.stop();
                DismissProgress(Records_Play_Activity.this);
                isBackClose = true;
                clearSavedData();
                finish();
            }
            if (playbackState == Player.STATE_READY) {
                showOfflineMessage(false);
                DismissProgress(Records_Play_Activity.this);
                nextFullBackHandler.removeCallbacks(nextFullBackRunnable);
            }
            if (playbackState == Player.STATE_BUFFERING) {
                if (NetworkUtil.checkNetworkAvailable(Records_Play_Activity.this)) {
                    //Remove the conneting message and reconnect the player
                    ShowProgressDilog(Records_Play_Activity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkUtil.hasActiveInternetConnection(Records_Play_Activity.this)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Show reconnecting message
                                        DismissProgress(Records_Play_Activity.this);
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    //Show reconnecting message
                    DismissProgress(Records_Play_Activity.this);
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
        public void onPlayerError(com.google.android.exoplayer2.ExoPlaybackException error) {
            showOfflineMessage(true);
            DismissProgress(Records_Play_Activity.this);

            // should wait for 3 seconds before reconnecting
            nextFullBackHandler.postDelayed(nextFullBackRunnable, Constant.RECONNECT_NEXT_TIME_OUT);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            if (player == null) return;
            int latestWindowIndex = player.getCurrentWindowIndex();
            if (latestWindowIndex != lastWindowIndex) {

                lastWindowIndex = latestWindowIndex;

                start_time.setText(videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getTime());
                tv_title_of_show.setText(videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getName());
                tv_description_of_show.setText(videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getDescription());

                try {
                    dday_records.setText(Constant.dayByGivenDate(videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getWday()));
                    rdatetime = videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getRdatetime();
                    ddate_records.setText(Constant.getUnixDate(rdatetime));

                    String time = Constant.getTimeByAdding((Constant.parseInt(videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getLengthtime()))
                            , videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getTime());

                    end_time.setText(time);

                    try {
                        ImageCacheUtil.with(Records_Play_Activity.this)
                                .load("http:" + videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getShowpic())
                                .resize(200,200)
                                .cacheUsage(false, true)
                                .into(Iv_channel_records);
                    } catch (Exception e) {
                        Picasso.with(Records_Play_Activity.this).load(R.drawable.club1).into(Iv_channel_records);
                    }
                    if (videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getShowpic().equalsIgnoreCase("")) {
                        Picasso.with(Records_Play_Activity.this).load(R.drawable.club1).into(Iv_channel_records);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + videoTotal_BackupLists.get(0).getFallback_details().get(lastWindowIndex).getGenre());
                } catch (Exception e) {
                    genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                }
                container_modified_livechannels.setVisibility(View.VISIBLE);
                flag_pressed = "pressed";
                pressed_time = System.currentTimeMillis();
            }
            // playerView_record_play.hideController();
            playerView_record_play.setControllerShowTimeoutMs(8000);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_DPAD_UP)
        {
            flag_pressed="pressed";
            pressed_time=System.currentTimeMillis();
            if(container_modified_livechannels.getVisibility()==View.GONE)
            {
                Reload_selected_records(selected_channel_records_position_default);
                container_modified_livechannels.setVisibility(View.VISIBLE);
                Refresh_Start_End_Reached();
            } else {
                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                llay_channel_img.requestFocus();
                Iv_up_records.setPressed(true);
                Iv_down_records.setPressed(false);
                Iv_up_records.callOnClick();
                container_modified_livechannels.setVisibility(View.VISIBLE);
            }
            playerView_record_play.hideController();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

            flag_pressed = "pressed";
            pressed_time = System.currentTimeMillis();
            if (container_modified_livechannels.getVisibility() == View.GONE) {
//                flag = onKeyDown_Down_GONE;
                Reload_selected_records(selected_channel_records_position_default);
                container_modified_livechannels.setVisibility(View.VISIBLE);
                Refresh_Start_End_Reached();
            } else {
                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                llay_channel_img.requestFocus();
                Iv_down_records.setPressed(true);
                Iv_up_records.setPressed(false);
                Iv_down_records.callOnClick();
                container_modified_livechannels.setVisibility(View.VISIBLE);
            }
            playerView_record_play.hideController();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            container_modified_livechannels.setVisibility(View.GONE);
            exo_progress.requestFocus();
            playerView_record_play.showController();
           /*  long currentposition=player.getCurrentPosition();
             long seekto=(currentposition-30000);
            if (seekto<0)
            {
                player.seekTo(C.TIME_UNSET);
            }
            else player.seekTo(seekto);*/
            playerView_record_play.setControllerShowTimeoutMs(8000);
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT && event.isLongPress())
        {
            container_modified_livechannels.setVisibility(View.GONE);
            exo_progress.requestFocus();
            playerView_record_play.showController();
           /* long currentposition=player.getCurrentPosition();
            long seekto=(currentposition-180000);
            if (seekto<0)
            {
                player.seekTo(C.TIME_UNSET);
            }
            else player.seekTo(seekto);*/
            playerView_record_play.setControllerShowTimeoutMs(8000);
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            container_modified_livechannels.setVisibility(View.GONE);
            exo_progress.requestFocus();
            playerView_record_play.showController();
           /* long currentposition=player.getCurrentPosition();
            long seekto=(currentposition+30000);
            if (seekto>player.getDuration())
            {
                player.seekTo(player.getDuration());
            }
            else player.seekTo(seekto);*/
            playerView_record_play.setControllerShowTimeoutMs(8000);
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
        }
        else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT && event.isLongPress())
        {
            container_modified_livechannels.setVisibility(View.GONE);
            playerView_record_play.showController();
            if (player != null) {
                long currentposition = player.getCurrentPosition();
                long seekto = (currentposition + 180000);
                if (seekto > player.getDuration()) {
                    player.seekTo(player.getDuration());
                } else player.seekTo(seekto);
            }
            playerView_record_play.setControllerShowTimeoutMs(8000);
            if(container_modified_livechannels.getVisibility()==View.VISIBLE)
            {
                container_modified_livechannels.setVisibility(View.GONE);
            }
        }
        else if(keyCode==KeyEvent.KEYCODE_MEDIA_REWIND)
        {
            playerView_record_play.showController();
            if (player != null) {
                long currentposition = player.getCurrentPosition();
                long seekto = (currentposition - 180000);
                if (seekto < 0) {
                    player.seekTo(C.TIME_UNSET);
                } else player.seekTo(seekto);
            }
            playerView_record_play.setControllerShowTimeoutMs(8000);
        }
        else if(keyCode==KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        {
            playerView_record_play.showController();
            if (player != null) {
                long currentposition = player.getCurrentPosition();
                long seekto = (currentposition + 180000);
                if (seekto > player.getDuration()) {
                    player.seekTo(player.getDuration());
                } else player.seekTo(seekto);
            }
            playerView_record_play.setControllerShowTimeoutMs(8000);
        }
        else if(keyCode==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        {
            if (player != null) {
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                } else player.setPlayWhenReady(true);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == KeyEvent.KEYCODE_ENTER) {
//            MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
//                @Override
//                public boolean apply(Request<?> request) {
//                    return true;
//                }
//            });
            flag_pressed = "pressed";
            pressed_time = System.currentTimeMillis();
            if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                if (selected_channel_records_position_default != selected_channel_records_position) {
                    Reload_selected_video(selected_channel_records_position);
                } else {
                    if (player != null) {
                        if (player.getPlayWhenReady()) {
                            player.setPlayWhenReady(false);
                        } else player.setPlayWhenReady(true);
                    }
                }
            }
            else if(container_modified_livechannels.getVisibility()==View.GONE)
            {
                    container_modified_livechannels.setVisibility(View.VISIBLE);

                    if (player != null) {
                        if (player.getPlayWhenReady()) {
                            player.setPlayWhenReady(false);
                        } else player.setPlayWhenReady(true);
                    }
                }

        }

        return super.onKeyDown(keyCode, event);
    }


    private void showOfflineMessage(boolean bShow) {
        if (player == null)
            return;

        tv_connectionstatus_records.setVisibility(View.GONE);
        layConnectionState.setVisibility(View.GONE);

        if (player.getCurrentPosition() >= player.getBufferedPosition()) {
            if (bShow) {
                // hide small offline message
                if (mTimerForSmallConnection != null) {
                    mTimerForSmallConnection.cancel();
                    mTimerForSmallConnection = null;
                }
                if (!Constant.isInternetAvailable())
                    tv_connectionstatus_records.setVisibility(View.VISIBLE);
            } else {
                tv_connectionstatus_records.setVisibility(View.GONE);
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