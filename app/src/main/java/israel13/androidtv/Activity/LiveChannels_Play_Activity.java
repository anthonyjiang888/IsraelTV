package israel13.androidtv.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import israel13.androidtv.Adapter.AllRadioRecyclerviewAdapter;
import israel13.androidtv.Adapter.AllchannelRecyclerviewAdapter;
import israel13.androidtv.CallBacks.ConnectionChange;
import israel13.androidtv.Fragments.AllChannels_Fragment;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.Others.ConnectivityReceiver;
import israel13.androidtv.R;
import israel13.androidtv.Service.ResetCacheService;
import israel13.androidtv.Setter_Getter.SetgetLoadScheduleRecord;
import israel13.androidtv.Setter_Getter.SetgetSubchannels;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.UiUtils;

/**
 * Created by puspak on 06/06/17.
 */
public class LiveChannels_Play_Activity extends AppCompatActivity implements IJSONParseListener, VideoRendererEventListener, ConnectionChange {
    private boolean isBackClose;
    private LinearLayout linearlayout_modified_livechannels_close, epg_live_time, epg_add_to_fav, epg_move_to_record_mode, total_lay_with_bar_add_fav;
    private int LOAD_VIDEO = 109;
    private int ADD_FAVORITES = 150;
    private int LOAD_RECORD_SCHEDULE = 160;
    private int REMOVE_FAVORITES = 170;
    private int LOAD_ALL_CHANNEL = 110;
    final static public String CHANNEL_ID = "CHANNEL_ID";
    final static public String MAIN_CH_ID = "MAIN_CH_ID";
    final static public String CH_ICON = "CH_ICON";
    private ProgressDialog pDialog;
    private ProgressDialog player_pDialog;
    private SharedPreferences logindetails;
    private SharedPreferences rememberState;
    private StringBuilder builder;
    private ArrayList<SetgetLoadScheduleRecord> LoadScheduleRecordsList = new ArrayList<>();
    private RelativeLayout progressbar_layout_live_channels;
    private TextView tv_channel_name, start_time, end_time, tv_description_of_show, tv_title_of_show, tv_no_of_channel_remote;
    private TextView genre, tv_dash;
    private ImageView Iv_next_show, Iv_channel_played, Iv_channel_not_playing, Iv_up_channele,  Iv_down_channel, rec_imag, Iv_previous_show;
    private ProgressBar pgbar_livetime;
    private int running_show_time;
    private Thread myThread1;
    private ArrayList<SetgetSubchannels> array_subchannels_live_tv = new ArrayList<>();
    private RelativeLayout llay_channel_img;
    private RelativeLayout container_modified_livechannels;
    private LinearLayout tv_connectionstatus;
    private RelativeLayout container;

    //channel number
    private LinearLayout layChannelNumber;
    private TextView txtChannelNumber;
    private int nChannelCurElapsedTime = 0;
    private boolean bChannelNumShowing = false;
    private final int CHANNEL_SELECT_TIMEOUT = 4;
    private Handler mHandler = new Handler();

    // small offline/online message
    private Timer mTimerForSmallConnection;
    private Handler mHandlerForSmallConnection;
    private LinearLayout layConnectionState;
    private TextView txtConnectionState;
    private boolean mLastConnectionStatus = true;

    public static String start_show_time;
    public static int left_pressed = 1;
    public static int right_pressed = 1;

    public int selected_channel_position = 0;
    public String channel_id, channel_icon, main_channel_id;
    public int channel_up_flag = 1;
    public int channel_down_flag = 1;
    public String no_load_video = "", fav_flag = "";
    public static int channel_epg_position = 0;
    public String flag_pressed = "";
    public static String flag_channle_id_played = "";
    public static int selected_channel_position_default = 0;

    private String date;
    private long pressed_time = 0;
    private long opened_time = 0;
    private ArrayList<String> videoBackupLists = new ArrayList<String>();
    private int flag_glob = 0;
    private ImageView add_To_fav_img;
    private TextView add_fav_text;
    private SimpleExoPlayerView playerView_live;
    private SimpleExoPlayer player;
    private int press_count_for_add_to_fav = 0;
    private Toast toast;

    private int flag = 0;//first state
    private static int Iv_up_channeleOnClick = 1;
    private static int Iv_down_channelOnClick = 2;
    private static int onKeyDown_UP_GONE = 3;
    private static int onKeyDown_UP_array_subchannels_live_tv_size = 4;
    private static int onKeyDown_Down_GONE = 5;
    private static int onKeyDown_Down_noGONE_array_subchannels_live_tv_size = 6;
    private static int onKeyDown_ENTER_GONE = 7;
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
        Log.i("test test ", "test test ");
        super.onCreate(savedInstanceState);

        if (!Constant.hasFreeDiskSpace(this, true)) {
            isBackClose = true;
            ClearState();
            finish();
            return;
        }
        Log.i("isDead", "isLive");
        mLastConnectionStatus = NetworkUtil.checkNetworkAvailable(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.play_livechannels_activity);

        progressbar_layout_live_channels = (RelativeLayout) findViewById(R.id.progressbar_layout_live_channels);
        container_modified_livechannels = (RelativeLayout) findViewById(R.id.container_modified_livechannels);
        container = (RelativeLayout) findViewById(R.id.container);
        container.setFocusable(false);
        container.setClickable(false);
        tv_channel_name = (TextView) findViewById(R.id.tv_channel_name);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        tv_description_of_show = (TextView) findViewById(R.id.tv_description_of_show);
        tv_title_of_show = (TextView) findViewById(R.id.tv_title_of_show);
        tv_no_of_channel_remote = (TextView) findViewById(R.id.tv_no_of_channel_remote);
        tv_dash = (TextView) findViewById(R.id.tv_dash);
        add_fav_text = (TextView) findViewById(R.id.add_fav_text);

        epg_add_to_fav = (LinearLayout) findViewById(R.id.epg_add_to_fav);
        genre = (TextView) findViewById(R.id.genre);
        epg_move_to_record_mode = (LinearLayout) findViewById(R.id.epg_move_to_record_mode);
        total_lay_with_bar_add_fav = (LinearLayout) findViewById(R.id.total_lay_with_bar_add_fav);

        Iv_next_show = (ImageView) findViewById(R.id.Iv_next_show);
        Iv_up_channele = (ImageView) findViewById(R.id.Iv_up_channele);
        Iv_channel_played = (ImageView) findViewById(R.id.Iv_channel_played);
        Iv_channel_not_playing = (ImageView) findViewById(R.id.Iv_channel_not_playing);
        Iv_down_channel = (ImageView) findViewById(R.id.Iv_down_channel);
        Iv_previous_show = (ImageView) findViewById(R.id.Iv_previous_show);
        rec_imag = (ImageView) findViewById(R.id.rec_imag);
        add_To_fav_img = (ImageView) findViewById(R.id.add_To_fav_img);

        epg_live_time = (LinearLayout) findViewById(R.id.epg_live_time);
        pgbar_livetime = (ProgressBar) findViewById(R.id.pgbar_livetime);
        // pgbar_livetime.setRotation(180);

        llay_channel_img = (RelativeLayout) findViewById(R.id.llay_channel_img);
        tv_connectionstatus = (LinearLayout) findViewById(R.id.tv_connectionstatus);

        layChannelNumber = (LinearLayout)findViewById(R.id.layout_channel_number);
        txtChannelNumber = (TextView) findViewById(R.id.text_channel_number);

        layConnectionState = (LinearLayout)findViewById(R.id.layout_connection_state);
        txtConnectionState = (TextView)findViewById(R.id.text_connection_state);

        playerView_live = (SimpleExoPlayerView) findViewById(R.id.playerView_live);

        logindetails = this.getSharedPreferences("logindetails", MODE_PRIVATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        array_subchannels_live_tv = new ArrayList<>();
//        if (AllChannels_Fragment.is_radio.equalsIgnoreCase("0")) {
//            array_subchannels_live_tv = AllchannelRecyclerviewAdapter.array_subchannels;
//            selected_channel_position = AllchannelRecyclerviewAdapter.selected_position;
//        } else {
//            array_subchannels_live_tv = AllRadioRecyclerviewAdapter.array_subchannels;
//            selected_channel_position = AllRadioRecyclerviewAdapter.selected_position;
//        }

        channel_id = getIntent().getStringExtra(CHANNEL_ID);
        main_channel_id = getIntent().getStringExtra(MAIN_CH_ID);
        channel_icon = getIntent().getStringExtra(CH_ICON);
//        channel_id = array_subchannels_live_tv.get(selected_channel_position).getSub_channelsid();
//        main_channel_id = array_subchannels_live_tv.get(selected_channel_position).getChid();
//        channel_icon = array_subchannels_live_tv.get(selected_channel_position).getImage();

        AllChannels_Fragment.flag_back = 1;

        pgbar_livetime.setVisibility(View.VISIBLE);

        epg_move_to_record_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn = new Intent(LiveChannels_Play_Activity.this, HomeActivity.class);
                intn.putExtra("go_to", "record");
                intn.putExtra("channel_id", channel_id);
                intn.putExtra("channel_icon", channel_icon);
                startActivity(intn);
            }
        });

        epg_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fav_flag.equalsIgnoreCase("add_fav")) {
                    no_load_video = "no_need";
                    AddFavorites();
                } else if (fav_flag.equalsIgnoreCase("remove_fav")) {
                    no_load_video = "no_need";
                    RemoveFavorites();
                }
            }
        });

        Iv_up_channele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = Iv_up_channeleOnClick;//here

                pressed_time = System.currentTimeMillis();
                if (array_subchannels_live_tv.size() > 0) {
                    if (selected_channel_position <= 0) {
                        selected_channel_position = array_subchannels_live_tv.size();
                    }
                    if (selected_channel_position - channel_down_flag !=  selected_channel_position_default) {
                        llay_channel_img.setBackgroundResource(R.drawable.button_bg1);
                        Iv_channel_not_playing.setVisibility(View.VISIBLE);
                        epg_add_to_fav.setVisibility(View.INVISIBLE);
                    } else {
                        llay_channel_img.setBackground(null);
                        Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                        epg_add_to_fav.setVisibility(View.VISIBLE);
                    }

                    channel_id = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getSub_channelsid();
                    main_channel_id = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getChid();
                    int position = selected_channel_position - channel_down_flag;
                    tv_channel_name.setText(array_subchannels_live_tv.get(position).getName());

                    ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                            .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(position).getImage())
                            .resize(200,200)
                            .cacheUsage(false, true)
                            .into(Iv_channel_played);

                    tv_no_of_channel_remote.setText(main_channel_id);

                    channel_icon = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getImage();
                    AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getIsinfav();
                    if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                        add_fav_text.setText("הסר ממועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                        fav_flag = "remove_fav";
                    } else {
                        add_fav_text.setText("הוסף למועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                        fav_flag = "add_fav";
                    }
                    selected_channel_position = selected_channel_position - channel_down_flag;
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד ערוץ זמין", Toast.LENGTH_SHORT).show();

                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                emptyField();
                LoadScheduleRecord(date);
                pgbar_livetime.setVisibility(View.VISIBLE);
            }
        });

        Iv_down_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = Iv_down_channelOnClick;
                pressed_time = System.currentTimeMillis();
                if (array_subchannels_live_tv.size() > 0) {
                    if (selected_channel_position >= array_subchannels_live_tv.size() - 1) {
                        selected_channel_position = -1;
                    }
                    if (selected_channel_position + channel_down_flag !=  selected_channel_position_default) {
                        llay_channel_img.setBackgroundResource(R.drawable.button_bg1);
                        Iv_channel_not_playing.setVisibility(View.VISIBLE);
                        epg_add_to_fav.setVisibility(View.INVISIBLE);
                    } else {
                        llay_channel_img.setBackground(null);
                        Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                        epg_add_to_fav.setVisibility(View.VISIBLE);
                    }
                    int position = selected_channel_position + channel_up_flag;
                    channel_id = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getSub_channelsid();
                    main_channel_id = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getChid();
                    tv_channel_name.setText(array_subchannels_live_tv.get(position).getName());

                    ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                            .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(position).getImage())
                            .resize(200,200)
                            .cacheUsage(false, true)
                            .into(Iv_channel_played);

                    tv_no_of_channel_remote.setText(main_channel_id);
                    channel_icon = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getImage();
                    AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getIsinfav();
                    if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                        add_fav_text.setText("הסר ממועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                        fav_flag = "remove_fav";
                    } else {
                        add_fav_text.setText("הוסף למועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                        fav_flag = "add_fav";
                    }
                    selected_channel_position = selected_channel_position + channel_up_flag;
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד ערוץ זמין", Toast.LENGTH_SHORT).show();

                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                emptyField();
                LoadScheduleRecord(date);
                pgbar_livetime.setVisibility(View.VISIBLE);
            }
        });

        llay_channel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                if (progressbar_layout_live_channels.getVisibility() == View.VISIBLE)
                    return;
                // check player null value
                if (player != null) {
                    player.stop();
                }
                LoadVideo();
            }
        });

        Iv_next_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgbar_livetime.setVisibility(View.INVISIBLE);

                pressed_time = System.currentTimeMillis();
                if (LoadScheduleRecordsList.size() > 0 && (left_pressed + running_show_time) < LoadScheduleRecordsList.size()) {
                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time + left_pressed).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time + left_pressed).getTime());

                    // end_time.setText(time);
                    // tv_next_show_name.setText(LoadScheduleRecordsList.get(running_show_time+left_pressed).getName());
                    Iv_previous_show.setVisibility(View.VISIBLE);
                    start_time.setText(LoadScheduleRecordsList.get(running_show_time + left_pressed).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time + left_pressed).getName().trim();
                    String year = LoadScheduleRecordsList.get(running_show_time + left_pressed).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time + left_pressed).getDescription().trim());
                    if (LoadScheduleRecordsList.get(running_show_time + left_pressed).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time + left_pressed).getGenre().trim());

                    pgbar_livetime.setMax((int) Constant.time_diff_in_millisec(LoadScheduleRecordsList.get(channel_epg_position).getTime(), time));

                    // left_pressed++;
                    running_show_time = running_show_time + left_pressed;
                    if (running_show_time + left_pressed >= LoadScheduleRecordsList.size()) {
                        Iv_next_show.setVisibility(View.INVISIBLE);
                        // Iv_previous_show.requestFocus();
                    }
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();
            }
        });

        Iv_previous_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iv_next_show.setVisibility(View.VISIBLE);
                pressed_time = System.currentTimeMillis();
                if (LoadScheduleRecordsList.size() > 0 &&
                        (running_show_time - right_pressed) >= 0 &&
                        (running_show_time - right_pressed) < LoadScheduleRecordsList.size() &&
                        channel_epg_position <= running_show_time) {
                    if (channel_epg_position == running_show_time) {
                        right_pressed = 0;
                        Iv_previous_show.setVisibility(View.INVISIBLE);
                    } else right_pressed = 1;

                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time - right_pressed).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time - right_pressed).getTime());

                    // end_time.setText(time);
                    // tv_next_show_name.setText(LoadScheduleRecordsList.get(running_show_time+left_pressed).getName());
                    start_time.setText(LoadScheduleRecordsList.get(running_show_time - right_pressed).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time - right_pressed).getName();
                    String year = LoadScheduleRecordsList.get(running_show_time - right_pressed).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time - right_pressed).getDescription());
                    if (LoadScheduleRecordsList.get(running_show_time - right_pressed).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time - right_pressed).getGenre());

                    pgbar_livetime.setMax((int) Constant.time_diff_in_millisec(LoadScheduleRecordsList.get(channel_epg_position).getTime(), time));

                    // left_pressed++;
                    running_show_time = running_show_time - right_pressed;
                    if ((running_show_time - right_pressed) < channel_epg_position) {
                        Iv_previous_show.setVisibility(View.INVISIBLE);
                        //Iv_next_show.requestFocus();
                        pgbar_livetime.setVisibility(View.VISIBLE);
                    }
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();
            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (container_modified_livechannels.getVisibility() == View.GONE) {
                    opened_time = System.currentTimeMillis();
                    container_modified_livechannels.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void initPlayerUI() {
        playerView_live.findViewById(R.id.bar_layout).setVisibility(View.GONE);

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
        playerView_live.setUseController(true);
        playerView_live.setPlayer(player);
    }

    public void Set_player_video(Uri path) {
        releasePlayer();
        if (!mActivityAlive) return;
        initPlayerUI();

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //FOR LIVESTREAM LINK:
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "IsraelTV"), bandwidthMeterA);
        MediaSource videoSource = new HlsMediaSource(path, dataSourceFactory, 1, null, null);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        playerView_live.setFocusable(false);
        long duration = player == null ? 0 : player.getDuration();
        // playerView_live.setControllerShowTimeoutMs(8000);
    }

    public void Call_api() {
        if (NetworkUtil.checkNetworkAvailable(this)) {
            LoadAllChannel();
        } else {
            ShowErrorAlert(this, "Please check your network connection..");
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

    void LoadAllChannel() {
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("allchannel", "1");
//        parms.putString("sid", logindetails.getString("sid", ""));
        //parms.putString("isradio", AllChannels_Fragment.is_radio);
        MyVolley.init(LiveChannels_Play_Activity.this);
        //ShowProgressDilog(getActivity());
        // progressbar_layout_live_channels.setVisibility(View.VISIBLE);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "channels.php", LOAD_ALL_CHANNEL, LiveChannels_Play_Activity.this, parms, false);
    }

    void LoadScheduleRecord(String date) {
        JSONRequestResponse mResponse = new JSONRequestResponse(LiveChannels_Play_Activity.this);
        Bundle parms = new Bundle();
        // parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("cid", channel_id);
        //  parms.putString("date", date);
        parms.putString("record", "0");
        parms.putString("withdescription", "1");
        MyVolley.init(LiveChannels_Play_Activity.this);
        //ShowProgressDilog(getActivity());
        progressbar_layout_live_channels.setVisibility(View.VISIBLE);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "schbydate.php", LOAD_RECORD_SCHEDULE, LiveChannels_Play_Activity.this, parms, false);
    }

    void LoadVideo() {
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("cid", channel_id);
        parms.putString("cookiekey", Constant.getDeviceUUID(this));
        MyVolley.init(LiveChannels_Play_Activity.this);
        ShowProgressDilog(this);
        // progressbar_layout_live_channels.setVisibility(View.VISIBLE);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "chls.php", LOAD_VIDEO, LiveChannels_Play_Activity.this, parms, false);
    }

    void AddFavorites() {
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("stfor", "0");
        parms.putString("act", "1");
        parms.putString("ch", channel_id);
        parms.putString("datetime", "");
        parms.putString("vodid", "");
        MyVolley.init(LiveChannels_Play_Activity.this);
        ShowProgressDilog(this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", ADD_FAVORITES, this, parms, false);
    }

    void RemoveFavorites() {
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("stfor", "0");
        parms.putString("act", "11");
        parms.putString("ch", channel_id);
        parms.putString("datetime", "");
        parms.putString("vodid", "");
        MyVolley.init(this);
        ShowProgressDilog(this);
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "myfav.php", REMOVE_FAVORITES, this, parms, false);
    }

    private void nextFullBackPlay() {
        try {
            flag_glob++;
            Set_player_video(Uri.parse(videoBackupLists.get(flag_glob)));
        } catch (Exception e) {
            flag_glob = 0;

            if (NetworkUtil.isServer401(this, videoBackupLists.get(flag_glob))) {
                // this case means, streamServer is Online.
                String message = String.format(this.getResources().getString(R.string.reconnect_continue_watching));
                showReconnectErrorAlert(this, message);
            } else {
                Set_player_video(Uri.parse(videoBackupLists.get(flag_glob)));
            }

        }
    }

    private void showReconnectErrorAlert(final Context c, String text) {
        android.app.AlertDialog.Builder alertDialogBuilder;
        ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
        alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);

        alertDialogBuilder.setTitle("הודעה");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call_api();
            }
        });
        alertDialogBuilder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isBackClose = true;
                ClearState();
                finish();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @Override
    public void onResume() {

        mActivityAlive = true;

        showOfflineMessage(false);

        ConnectivityReceiver.mcontext = this;

        //call api for loadschedule
        Call_api();

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        super.onResume();
        Runnable r = new CountDownRunner1();
        myThread1 = new Thread(r);
        myThread1.start();
    }
    void saveState() {
        rememberState = this.getSharedPreferences("remember", MODE_PRIVATE);
        SharedPreferences.Editor editor = rememberState.edit();
        editor.putString("Where","LiveChannelPlay");
        editor.putString("ChannelId",channel_id);
        editor.putString("MainChId", main_channel_id);
        editor.putString("ChIcon",channel_icon);
        editor.putBoolean("isHome",true);
        editor.commit();
        editor.apply();
    }
    @Override
    public void onPause() {
        if (isBackClose != true)
            saveState();
        if (myThread1 != null)
        {
            myThread1.interrupt();
            myThread1 = null;
        }
        // Let JW Player know that the app is going to the background
        ConnectivityReceiver.mcontext = null;

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
        ConnectivityReceiver.mcontext = null;

        mActivityAlive = false;
        releasePlayer();

        //dialog dismiss
        DismissProgress(this);

        nextFullBackHandler.removeCallbacks(nextFullBackRunnable);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("isDead","onDestroy");

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

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
        DismissProgress(this);
        Constant.ShowErrorToast(this);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == LOAD_VIDEO) {
            flag_channle_id_played = channel_id;
            builder = new StringBuilder();

            // progressbar_layout_live_channels.setVisibility(View.GONE);
            DismissProgress(this);

            String errorcode = "";
            String End_Time = "";
            String isinfav = "";
            try {

                try {
                    errorcode = response.getString("error");
                } catch (Exception e) {
                    errorcode = response.getString("errorcode");
                }

                try {
                    End_Time = response.getString("endtime");
                } catch (Exception e) {
                    End_Time = "";
                }

                JSONObject results = response.getJSONObject("results");

                String sip = results.getString("sip");
                String port = results.getString("port");
                String path = results.getString("path");
                isinfav = results.getString("isinfav");

                JSONArray fulllinks = results.getJSONArray("fulllinks");

                videoBackupLists = new ArrayList<>();
                for (int i = 0; i < fulllinks.length(); i++) {
                    videoBackupLists.add(fulllinks.get(i).toString());
                }

                builder.append("http://").append(sip).append(":").append(port).append(path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (errorcode.equalsIgnoreCase("101")) {
                Constant.showPopup_Error_basic_package(LiveChannels_Play_Activity.this);

            } else if (errorcode.equalsIgnoreCase("999")) {
                Constant.showPopup_Error_expire_package(LiveChannels_Play_Activity.this);
            } else if (errorcode.equalsIgnoreCase("58403")) {
                Constant.showPopup_VPN_Block(LiveChannels_Play_Activity.this);
            } else if (errorcode.equalsIgnoreCase("998")) {
                try {
                    Constant.showPopup_Error_freeze_package(LiveChannels_Play_Activity.this, Constant.getUnixDate(response.getString("endtime")));
                } catch (Exception e) {
                    Constant.showPopup_Error_freeze_package(LiveChannels_Play_Activity.this, "");
                }
            } else if (errorcode.equalsIgnoreCase("99")) {
                UiUtils.logout(this);
                return;
            } else if (errorcode.equalsIgnoreCase("997")) {
                Constant.showPopup_Error_suspend_package(LiveChannels_Play_Activity.this);
            } else {
                total_lay_with_bar_add_fav.setVisibility(View.VISIBLE);
                if (isinfav.equalsIgnoreCase("1")) {
                    add_fav_text.setText("הסר ממועדפים");
                    add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                    fav_flag = "remove_fav";
                } else {
                    add_fav_text.setText("הוסף למועדפים");
                    add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                    fav_flag = "add_fav";
                }
                Set_player_video(Uri.parse(videoBackupLists.get(0)));
                showCurChannelTextView(main_channel_id);
            }

            selected_channel_position_default = selected_channel_position;
            llay_channel_img.setBackground(null);
            Iv_channel_not_playing.setVisibility(View.INVISIBLE);
            epg_add_to_fav.setVisibility(View.VISIBLE);
        } else if (requestCode == ADD_FAVORITES) {

            DismissProgress(this);
            System.out.println("Response for add Live Channels --------" + response.toString());

            Toast.makeText(LiveChannels_Play_Activity.this, "הוסף בהצלחה למועדפים", Toast.LENGTH_SHORT).show();

            add_fav_text.setText("הסר ממועדפים");
            add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
            fav_flag = "remove_fav";
            // AllChannels_Fragment.is_infav="1";
            array_subchannels_live_tv.get(selected_channel_position).setIsinfav("1");
            //LoadScheduleRecord(date);
        } else if (requestCode == REMOVE_FAVORITES) {
            DismissProgress(LiveChannels_Play_Activity.this);
            System.out.println("Response for Remove Live Channels --------" + response.toString());

            Toast.makeText(LiveChannels_Play_Activity.this, "הוסר בהצלחה מהמועדפים", Toast.LENGTH_SHORT).show();

            // LoadScheduleRecord(date);
            add_fav_text.setText("הוסף למועדפים");
            add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
            fav_flag = "add_fav";
            //AllChannels_Fragment.is_infav="0";
            array_subchannels_live_tv.get(selected_channel_position).setIsinfav("0");
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        //DismissProgress(getActivity());
        if (requestCode == LOAD_RECORD_SCHEDULE) {
            progressbar_layout_live_channels.setVisibility(View.GONE);
            System.out.println("Response for load record list --------------------" + response.toString());

            if (LoadScheduleRecordsList == null) {
                LoadScheduleRecordsList = new ArrayList<>();
            } else {
                LoadScheduleRecordsList.clear();
            }

            for (int i = 0; i < response.length(); i++) {

                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    SetgetLoadScheduleRecord setgetLoadScheduleRecord = new SetgetLoadScheduleRecord();
                    setgetLoadScheduleRecord.setChannel(jsonObject.getString("channel"));
                    setgetLoadScheduleRecord.setRdatetime(jsonObject.getString("rdatetime"));
                    setgetLoadScheduleRecord.setTime(jsonObject.getString("time"));
                    setgetLoadScheduleRecord.setName(jsonObject.getString("name"));
                    setgetLoadScheduleRecord.setWday(jsonObject.getString("wday"));
                    setgetLoadScheduleRecord.setGenre(jsonObject.optString("genre", "מידע אינו זמין"));
                    setgetLoadScheduleRecord.setRdate(jsonObject.getString("rdate"));
                    setgetLoadScheduleRecord.setLengthtime(jsonObject.getString("lengthtime"));
                    setgetLoadScheduleRecord.setWeekno(jsonObject.getString("weekno"));
                    setgetLoadScheduleRecord.setIsinfav(jsonObject.getString("isinfav"));
                    setgetLoadScheduleRecord.setStar(jsonObject.getString("star"));
                    setgetLoadScheduleRecord.setStartotal(jsonObject.getString("startotal"));
                    setgetLoadScheduleRecord.setIsradio(jsonObject.getString("isradio"));
                    setgetLoadScheduleRecord.setIshd(jsonObject.getString("ishd"));
                    setgetLoadScheduleRecord.setLogo(jsonObject.getString("logo"));
                    setgetLoadScheduleRecord.setDescription(jsonObject.optString("description", ""));
                    setgetLoadScheduleRecord.setYear(jsonObject.optString("year", ""));
                    String s = Constant.getUnixDateMyprofile(setgetLoadScheduleRecord.getRdatetime());
                    String endtime = Constant.getDateTimeByAdding((Constant.parseInt(setgetLoadScheduleRecord.getLengthtime()))
                            , s);

                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                    timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                    String currtime = timeFormat.format(new Date());
                    //Toast.makeText(getApplicationContext(), endtime, Toast.LENGTH_SHORT).show();
                    if (!checkdatetimes(s, endtime, currtime)) {

                    } else {
                        LoadScheduleRecordsList.add(setgetLoadScheduleRecord);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


//            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
//            timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
//            String curr_time = timeFormat.format(new Date());
//
//            for (int i = 0; i < LoadScheduleRecordsList.size(); i++) {
//                String end_time = LoadScheduleRecordsList.get(i).getTime();
//                if (checktimings(end_time, curr_time)) {
//                    try {
//                        String end_time_nextshow = LoadScheduleRecordsList.get(i + 1).getTime();
//                        if (checktimings(curr_time, end_time_nextshow)) {
//
//                            System.out.println("the running show is ----" + LoadScheduleRecordsList.get(i).getTime());
//                            running_show_time = i;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            running_show_time = 0;
            tv_channel_name.setText(array_subchannels_live_tv.get(selected_channel_position).getName());

            ImageCacheUtil.with(this)
                    .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(selected_channel_position).getImage())
                    .resize(200,200)
                    .cacheUsage(false, true)
                    .into(Iv_channel_played);

            tv_no_of_channel_remote.setText(main_channel_id);
//            Iv_previous_show.setVisibility(View.INVISIBLE);
            Iv_next_show.setVisibility(View.VISIBLE);

            if (LoadScheduleRecordsList.size() > 0) {
                String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time).getLengthtime()))
                        , LoadScheduleRecordsList.get(running_show_time).getTime());
                start_time.setText(LoadScheduleRecordsList.get(running_show_time).getTime());
                end_time.setText(time);
                tv_dash.setVisibility(View.VISIBLE);
                String strName = LoadScheduleRecordsList.get(running_show_time).getName();
                String year = LoadScheduleRecordsList.get(running_show_time).getYear();
                if (year != null && !year.trim().isEmpty()) {
                    strName += " (" + year + ")";
                }
                tv_title_of_show.setText(strName);
                tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time).getDescription());
                if (LoadScheduleRecordsList.get(running_show_time).getGenre().equalsIgnoreCase("")) {
                    genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                } else {
                    genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time).getGenre());
                }

                start_show_time = LoadScheduleRecordsList.get(running_show_time).getTime();
                pgbar_livetime.setMax((int) Constant.time_diff_in_millisec(LoadScheduleRecordsList.get(running_show_time).getTime(), time));
                channel_epg_position = running_show_time;
            } else {
                // start_time.setVisibility(View.INVISIBLE);
                end_time.setText("מידע אינו זמין");
                start_time.setText("");

                tv_title_of_show.setVisibility(View.VISIBLE);
                tv_title_of_show.setText("מידע אינו זמין");
                tv_description_of_show.setText("מידע אינו זמין");
                genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                //genre.setVisibility(View.INVISIBLE);
                tv_dash.setVisibility(View.GONE);
                //epg_live_time.setVisibility(View.INVISIBLE);
            }

            if (AllChannels_Fragment.is_radio.equalsIgnoreCase("1")) {
                epg_move_to_record_mode.setVisibility(View.INVISIBLE);
                rec_imag.setVisibility(View.INVISIBLE);
//                Iv_next_show.setVisibility(View.INVISIBLE);
                Iv_previous_show.setVisibility((View.VISIBLE));
                epg_add_to_fav.requestFocus();
            } else {
                //Iv_next_show.requestFocus();
            }

           /* if (no_load_video.equalsIgnoreCase("no_need")) {

            } else LoadVideo();*/
        } else if (requestCode == LOAD_ALL_CHANNEL) {

            System.out.println("Response for load all channel --------------------" + response.toString());

            ArrayList<SetgetSubchannels> subchannelsList = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {

                try {
                    JSONObject channelobj = response.getJSONObject(i);
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
                    setgetSubchannels.setYear(channelobj.optString("year", ""));
                    if (setgetSubchannels.getChid().equals(""))
                        setgetSubchannels.setChid("0");
                    subchannelsList.add(setgetSubchannels);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(subchannelsList, new DateComparator());
            array_subchannels_live_tv.clear();
            array_subchannels_live_tv.addAll(subchannelsList);
            // selected_channel_position=AllchannelRecyclerviewAdapter.selected_position;
            for (int i = 0; i < array_subchannels_live_tv.size(); i++) {
                if (array_subchannels_live_tv.get(i).getSub_channelsid().equalsIgnoreCase(channel_id)) {
                    selected_channel_position = i;
                    selected_channel_position_default = selected_channel_position;
                    break;
                }
            }

            LoadScheduleRecord("");
            LoadVideo();
        }
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }

    void ShowProgressDilog(Context c) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(c);
        }
        pDialog.show();
        pDialog.setCancelable(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setContentView(R.layout.layout_progress_dilog);
    }

    void ShowProgressDilog_Player(Context c) {
        if (player_pDialog == null) {
            player_pDialog = new ProgressDialog(c);
        }
        try {
            player_pDialog.show();
            player_pDialog.setCancelable(true);
            player_pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            player_pDialog.setContentView(R.layout.layout_progress_dilog);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void DismissProgress(Context c) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    void DismissProgress_Player(Context c) {
        if (player_pDialog != null && player_pDialog.isShowing())
            player_pDialog.dismiss();
    }

    private boolean checktimings(String current_time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(current_time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean checkdatetimes(String starttime, String endtime, String current_time) {

        String pattern = "HH:mm:ss dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(starttime);
            date1.setTime(date1.getTime() - 6 * 3600 * 1000);
            Date date2 = sdf.parse(endtime);
            date2.setTime(date2.getTime() - 6 * 3600 * 1000);
            Date date3 = sdf.parse(current_time);
            date3.setTime(date3.getTime() - 6 * 3600 * 1000);

            if (date3.before(date2) && date1.getDate() == date3.getDate())
                return true;
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void OnNetworkChange() {
        if (NetworkUtil.checkNetworkAvailable(this)) {
            //Remove the conneting message and reconnect the player
            showOfflineMessage(false);
        } else {
            //Show reconnecting message
            showOfflineMessage(true);
            DismissProgress_Player(LiveChannels_Play_Activity.this);
            DismissProgress(LiveChannels_Play_Activity.this);
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

                    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                    String curTime1 = timeFormat.format(new Date());

                    if (LoadScheduleRecordsList.size() > 0) {
                        pgbar_livetime.setProgress((int) Constant.time_diff_in_millisec(start_show_time, curTime1));
                    } else pgbar_livetime.setVisibility(View.INVISIBLE);

                    long current_time = 0;
                    current_time = System.currentTimeMillis();
                    if (pressed_time != 0) {
                        if (current_time - pressed_time >= 8000) {
                            if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                                press_count_for_add_to_fav = 0;
                                container_modified_livechannels.setVisibility(View.GONE);
                            }
                        }
                        if (current_time - pressed_time >= 500) {
                            if (Iv_next_show.isPressed() == true) {
                                Iv_next_show.setPressed(false);
                            }
                            if (Iv_previous_show.isPressed() == true) {
                                Iv_previous_show.setPressed(false);
                            }
                            if (Iv_up_channele.isPressed()) {
                                Iv_up_channele.setPressed(false);
                            }
                            if (Iv_down_channel.isPressed()) {
                                Iv_down_channel.setPressed(false);
                            }
                        }
                    } else {
                        if (opened_time != 0) {
                            if (current_time - opened_time >= 8000) {
                                if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
                                    press_count_for_add_to_fav = 0;
                                    container_modified_livechannels.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    public class DateComparator implements Comparator<SetgetSubchannels> {
        @Override
        public int compare(SetgetSubchannels lhs, SetgetSubchannels rhs) {
            Double distance = Double.valueOf(lhs.getChid());
            Double distance1 = Double.valueOf(rhs.getChid());
            if (distance.compareTo(distance1) < 0) {
                return -1;
            } else if (distance.compareTo(distance1) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    void ClearState() {
        rememberState = this.getSharedPreferences("remember", MODE_PRIVATE);
        SharedPreferences.Editor editor = rememberState.edit();
        editor.clear();
        editor.commit();
    }
    @Override
    public void onBackPressed() {
        Log.i("isDead","onBack");
        isBackClose = false;
        if (isChannelNumDowning()) {
            mHandler.removeCallbacks(runnableChannelNumDownTimer);
            showCurChannelTextView(main_channel_id);
            return;
        }
        if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
            container_modified_livechannels.setVisibility(View.GONE);
        } else {
            ClearState();
            isBackClose = true;
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
//
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            playerView_live.hideController();

            if (container_modified_livechannels.getVisibility() == View.GONE) {
                flag = onKeyDown_UP_GONE;
                flag_pressed = "pressed";
                pressed_time = System.currentTimeMillis();
                if (array_subchannels_live_tv.isEmpty()) {

                } else {
                    channel_id = array_subchannels_live_tv.get(selected_channel_position_default).getSub_channelsid();
                    main_channel_id = array_subchannels_live_tv.get(selected_channel_position_default).getChid();
                    tv_channel_name.setText(array_subchannels_live_tv.get(selected_channel_position_default).getName());

                    ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                            .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(selected_channel_position_default).getImage())
                            .resize(200, 200)
                            .cacheUsage(false, true)
                            .into(Iv_channel_played);

                    tv_no_of_channel_remote.setText(main_channel_id);
                    channel_icon = array_subchannels_live_tv.get(selected_channel_position_default).getImage();
                    AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position_default).getIsinfav();
                    if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                        add_fav_text.setText("הסר ממועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                        fav_flag = "remove_fav";
                    } else {
                        add_fav_text.setText("הוסף למועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                        fav_flag = "add_fav";
                    }
                    emptyField();
                    LoadScheduleRecord(date);
                    pgbar_livetime.setVisibility(View.VISIBLE);

                    selected_channel_position = selected_channel_position_default;

                    llay_channel_img.setBackground(null);
                    Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                    epg_add_to_fav.setVisibility(View.VISIBLE);

                    if (LoadScheduleRecordsList.size() > 0 && LoadScheduleRecordsList.size() > running_show_time) {
                        running_show_time = channel_epg_position;
                        Iv_previous_show.setVisibility(View.INVISIBLE);

                        String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time).getLengthtime()))
                                , LoadScheduleRecordsList.get(running_show_time).getTime());

                        start_time.setText(LoadScheduleRecordsList.get(running_show_time).getTime());
                        end_time.setText(time);
                        tv_dash.setVisibility(View.VISIBLE);
                        String strName = LoadScheduleRecordsList.get(running_show_time).getName();
                        String year = LoadScheduleRecordsList.get(running_show_time).getYear();
                        if (year != null && !year.trim().isEmpty()) {
                            strName += " (" + year + ")";
                        }
                        tv_title_of_show.setText(strName);
                        tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time).getDescription());
                        if (LoadScheduleRecordsList.get(running_show_time).getGenre().equalsIgnoreCase("")) {
                            genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                        } else
                            genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time).getGenre());

                        pgbar_livetime.setVisibility(View.VISIBLE);
                    } else
                        Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();

                    total_lay_with_bar_add_fav.setVisibility(View.VISIBLE);
                    container_modified_livechannels.setVisibility(View.VISIBLE);
                }
            } else {
                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });

                // player.stop();
                flag_pressed = "pressed";
                Iv_up_channele.setPressed(true);
                Iv_down_channel.setPressed(false);
                pressed_time = System.currentTimeMillis();
                container_modified_livechannels.setVisibility(View.VISIBLE);

                if (array_subchannels_live_tv.size() > 0) {
                    flag = onKeyDown_UP_array_subchannels_live_tv_size;
                    if (selected_channel_position >= array_subchannels_live_tv.size() - 1) {
                        selected_channel_position = -1;
                    }

                    if (selected_channel_position + channel_down_flag != selected_channel_position_default) {
                        llay_channel_img.setBackgroundResource(R.drawable.button_bg1);
                        Iv_channel_not_playing.setVisibility(View.VISIBLE);
                        epg_add_to_fav.setVisibility(View.INVISIBLE);
                    } else {
                        llay_channel_img.setBackground(null);
                        Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                        epg_add_to_fav.setVisibility(View.VISIBLE);
                    }

                    int position = selected_channel_position + channel_up_flag;
                    channel_id = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getSub_channelsid();
                    main_channel_id = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getChid();
                    tv_channel_name.setText(array_subchannels_live_tv.get(position).getName());

                    ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                            .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(position).getImage())
                            .resize(200,200)
                            .cacheUsage(false, true)
                            .into(Iv_channel_played);

                    tv_no_of_channel_remote.setText(main_channel_id);
                    channel_icon = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getImage();
                    AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position + channel_up_flag).getIsinfav();
                    if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                        add_fav_text.setText("הסר ממועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                        fav_flag = "remove_fav";
                    } else {
                        add_fav_text.setText("הוסף למועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                        fav_flag = "add_fav";
                    }
                    selected_channel_position = selected_channel_position + channel_up_flag;

                    //total_lay_with_bar_add_fav.setVisibility(View.INVISIBLE); // ???
                    emptyField();
                    LoadScheduleRecord(date);
                    pgbar_livetime.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד ערוץ זמין", Toast.LENGTH_SHORT).show();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            playerView_live.hideController();
            if (container_modified_livechannels.getVisibility() == View.GONE) {
                flag = onKeyDown_Down_GONE;
                flag_pressed = "pressed";
                pressed_time = System.currentTimeMillis();
                if (array_subchannels_live_tv.isEmpty()) {

                } else {
                    channel_id = array_subchannels_live_tv.get(selected_channel_position_default).getSub_channelsid();
                    main_channel_id = array_subchannels_live_tv.get(selected_channel_position_default).getChid();
                    tv_channel_name.setText(array_subchannels_live_tv.get(selected_channel_position_default).getName());

                    ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                            .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(selected_channel_position_default).getImage())
                            .resize(200, 200)
                            .cacheUsage(false, true)
                            .into(Iv_channel_played);

                    tv_no_of_channel_remote.setText(main_channel_id);
                    channel_icon = array_subchannels_live_tv.get(selected_channel_position_default).getImage();
                    AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position_default).getIsinfav();
                    if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                        add_fav_text.setText("הסר ממועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                        fav_flag = "remove_fav";
                    } else {
                        add_fav_text.setText("הוסף למועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                        fav_flag = "add_fav";
                    }
                    emptyField();
                    LoadScheduleRecord(date);
                    pgbar_livetime.setVisibility(View.VISIBLE);

                    selected_channel_position = selected_channel_position_default;

                    llay_channel_img.setBackground(null);
                    Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                    epg_add_to_fav.setVisibility(View.VISIBLE);

                    if (LoadScheduleRecordsList.size() > 0 && LoadScheduleRecordsList.size() > running_show_time) {
                        running_show_time = channel_epg_position;
                        Iv_previous_show.setVisibility(View.INVISIBLE);

                        String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time).getLengthtime()))
                                , LoadScheduleRecordsList.get(running_show_time).getTime());

                        start_time.setText(LoadScheduleRecordsList.get(running_show_time).getTime());
                        end_time.setText(time);
                        tv_dash.setVisibility(View.VISIBLE);
                        String strName = LoadScheduleRecordsList.get(running_show_time).getName();
                        String year = LoadScheduleRecordsList.get(running_show_time).getYear();
                        if (year != null && !year.trim().isEmpty()) {
                            strName += " (" + year + ")";
                        }
                        tv_title_of_show.setText(strName);
                        tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time).getDescription());
                        if (LoadScheduleRecordsList.get(running_show_time).getGenre().equalsIgnoreCase("")) {
                            genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                        } else
                            genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time).getGenre());

                        pgbar_livetime.setVisibility(View.VISIBLE);
                    } else
                        Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();

                    total_lay_with_bar_add_fav.setVisibility(View.VISIBLE);
                    container_modified_livechannels.setVisibility(View.VISIBLE);
                }
            } else {
                MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });

                // player.stop();
                flag_pressed = "pressed";
                pressed_time = System.currentTimeMillis();
                Iv_down_channel.setPressed(true);
                Iv_up_channele.setPressed(false);
                container_modified_livechannels.setVisibility(View.VISIBLE);

                if (array_subchannels_live_tv.size() > 0) {
                    flag = onKeyDown_Down_noGONE_array_subchannels_live_tv_size;
                    if (selected_channel_position <= 0) {
                        selected_channel_position = array_subchannels_live_tv.size();
                    }
                    if (selected_channel_position - channel_down_flag != selected_channel_position_default) {
                        llay_channel_img.setBackgroundResource(R.drawable.button_bg1);
                        Iv_channel_not_playing.setVisibility(View.VISIBLE);
                        epg_add_to_fav.setVisibility(View.INVISIBLE);
                    } else {
                        llay_channel_img.setBackground(null);
                        Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                        epg_add_to_fav.setVisibility(View.VISIBLE);
                    }

                    channel_id = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getSub_channelsid();
                    main_channel_id = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getChid();
                    int position = selected_channel_position - channel_down_flag;
                    tv_channel_name.setText(array_subchannels_live_tv.get(position).getName());

                    ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                            .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(position).getImage())
                            .resize(200,200)
                            .cacheUsage(false, true)
                            .into(Iv_channel_played);

                    tv_no_of_channel_remote.setText(main_channel_id);

                    channel_icon = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getImage();
                    AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position - channel_down_flag).getIsinfav();
                    if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                        add_fav_text.setText("הסר ממועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                        fav_flag = "remove_fav";
                    } else {
                        add_fav_text.setText("הוסף למועדפים");
                        add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                        fav_flag = "add_fav";
                    }
                    selected_channel_position = selected_channel_position - channel_down_flag;

                    //total_lay_with_bar_add_fav.setVisibility(View.INVISIBLE);     // ???
                    emptyField();
                    LoadScheduleRecord(date);
                    pgbar_livetime.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד ערוץ זמין", Toast.LENGTH_SHORT).show();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            playerView_live.hideController();

            flag_pressed = "pressed";
            pressed_time = System.currentTimeMillis();
            if (container_modified_livechannels.getVisibility() == View.GONE) {
                if (LoadScheduleRecordsList.size() > 0 && LoadScheduleRecordsList.size() > running_show_time) {
                    running_show_time = channel_epg_position;
                    Iv_previous_show.setVisibility(View.INVISIBLE);

                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time).getTime());

                    start_time.setText(LoadScheduleRecordsList.get(running_show_time).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time).getName();
                    String year = LoadScheduleRecordsList.get(running_show_time).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time).getDescription());
                    if (LoadScheduleRecordsList.get(running_show_time).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time).getGenre());

                    pgbar_livetime.setVisibility(View.VISIBLE);
                } else showAToast("אין עוד לוח זמנים זמין");
                //Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();
                container_modified_livechannels.setVisibility(View.VISIBLE);
            } else {
                Iv_next_show.setPressed(true);
                Iv_previous_show.setPressed(false);
                pgbar_livetime.setVisibility(View.INVISIBLE);
                pressed_time = System.currentTimeMillis();
                if (LoadScheduleRecordsList.size() > 0 && (left_pressed + running_show_time) < LoadScheduleRecordsList.size()) {
                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time + left_pressed).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time + left_pressed).getTime());

                    // end_time.setText(time);
                    // tv_next_show_name.setText(LoadScheduleRecordsList.get(running_show_time+left_pressed).getName());
                    Iv_previous_show.setVisibility(View.VISIBLE);
                    start_time.setText(LoadScheduleRecordsList.get(running_show_time + left_pressed).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time + left_pressed).getName();
                    String year = LoadScheduleRecordsList.get(running_show_time + left_pressed).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time + left_pressed).getDescription());
                    if (LoadScheduleRecordsList.get(running_show_time + left_pressed).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time + left_pressed).getGenre());

                    pgbar_livetime.setMax((int) Constant.time_diff_in_millisec(LoadScheduleRecordsList.get(channel_epg_position).getTime(), time));

                    // left_pressed++;
                    running_show_time = running_show_time + left_pressed;
                    if (running_show_time + left_pressed >= LoadScheduleRecordsList.size()) {
                        Iv_next_show.setVisibility(View.INVISIBLE);
                        // Iv_previous_show.requestFocus();
                    }
                } else
                    // Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();
                    showAToast("אין עוד לוח זמנים זמין");
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            playerView_live.hideController();
            flag_pressed = "pressed";
            Iv_next_show.setVisibility(View.VISIBLE);
            pressed_time = System.currentTimeMillis();
            if (container_modified_livechannels.getVisibility() == View.GONE) {
                if (LoadScheduleRecordsList.size() > 0 && LoadScheduleRecordsList.size() > running_show_time) {
                    running_show_time = channel_epg_position;
                    Iv_previous_show.setVisibility(View.INVISIBLE);

                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time).getTime());

                    start_time.setText(LoadScheduleRecordsList.get(running_show_time).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time).getName();
                    String year = LoadScheduleRecordsList.get(running_show_time).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time).getDescription());
                    if (LoadScheduleRecordsList.get(running_show_time).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time).getGenre());

                    pgbar_livetime.setVisibility(View.VISIBLE);
                } else
                    //Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();
                    showAToast("אין עוד לוח זמנים זמין");
                container_modified_livechannels.setVisibility(View.VISIBLE);
            } else {
                Iv_previous_show.setPressed(true);
                Iv_next_show.setPressed(false);
                pressed_time = System.currentTimeMillis();
                if (LoadScheduleRecordsList.size() > 0 &&
                        (running_show_time - right_pressed) >= 0 &&
                        (running_show_time - right_pressed) < LoadScheduleRecordsList.size() &&
                        channel_epg_position <= running_show_time) {
                    if (channel_epg_position == running_show_time) {
                        right_pressed = 0;
                        Iv_previous_show.setVisibility(View.INVISIBLE);
                    } else right_pressed = 1;

                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time - right_pressed).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time - right_pressed).getTime());

                    start_time.setText(LoadScheduleRecordsList.get(running_show_time - right_pressed).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time - right_pressed).getName();
                    String year = LoadScheduleRecordsList.get(running_show_time - right_pressed).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time - right_pressed).getDescription());
                    if (LoadScheduleRecordsList.get(running_show_time - right_pressed).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time - right_pressed).getGenre());

                    pgbar_livetime.setMax((int) Constant.time_diff_in_millisec(LoadScheduleRecordsList.get(channel_epg_position).getTime(), time));

                    // left_pressed++;
                    running_show_time = running_show_time - right_pressed;
                    if ((running_show_time - right_pressed) < channel_epg_position) {
                        Iv_previous_show.setVisibility(View.INVISIBLE);
                        //Iv_next_show.requestFocus();
                        pgbar_livetime.setVisibility(View.VISIBLE);
                    }
                } else showAToast("אין עוד לוח זמנים זמין");
                //Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == KeyEvent.KEYCODE_ENTER) {
            MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
            flag_pressed = "pressed";
            pressed_time = System.currentTimeMillis();
            if (container_modified_livechannels.getVisibility() == View.GONE) {
                Iv_channel_not_playing.setVisibility(View.INVISIBLE);
                epg_add_to_fav.setVisibility(View.VISIBLE);
                flag = onKeyDown_ENTER_GONE;
                channel_id = array_subchannels_live_tv.get(selected_channel_position_default).getSub_channelsid();
                main_channel_id = array_subchannels_live_tv.get(selected_channel_position_default).getChid();
                tv_channel_name.setText(array_subchannels_live_tv.get(selected_channel_position_default).getName());

                ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                        .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(selected_channel_position_default).getImage())
                        .resize(200,200)
                        .cacheUsage(false, true)
                        .into(Iv_channel_played);

                tv_no_of_channel_remote.setText(main_channel_id);
                channel_icon = array_subchannels_live_tv.get(selected_channel_position_default).getImage();
                AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(selected_channel_position_default).getIsinfav();
                if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
                    add_fav_text.setText("הסר ממועדפים");
                    add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
                    fav_flag = "remove_fav";
                } else {
                    add_fav_text.setText("הוסף למועדפים");
                    add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
                    fav_flag = "add_fav";
                }
                emptyField();
                LoadScheduleRecord(date);
                pgbar_livetime.setVisibility(View.VISIBLE);

                selected_channel_position = selected_channel_position_default;

                if (LoadScheduleRecordsList.size() > 0 && LoadScheduleRecordsList.size() > running_show_time) {
                    running_show_time = channel_epg_position;
                    Iv_previous_show.setVisibility(View.INVISIBLE);

                    String time = Constant.getTimeByAdding((Constant.parseInt(LoadScheduleRecordsList.get(running_show_time).getLengthtime()))
                            , LoadScheduleRecordsList.get(running_show_time).getTime());

                    start_time.setText(LoadScheduleRecordsList.get(running_show_time).getTime());
                    end_time.setText(time);
                    tv_dash.setVisibility(View.VISIBLE);
                    String strName = LoadScheduleRecordsList.get(running_show_time).getName();
                    String year = LoadScheduleRecordsList.get(running_show_time).getYear();
                    if (year != null && !year.trim().isEmpty()) {
                        strName += " (" + year + ")";
                    }
                    tv_title_of_show.setText(strName);
                    tv_description_of_show.setText(LoadScheduleRecordsList.get(running_show_time).getDescription());
                    if (LoadScheduleRecordsList.get(running_show_time).getGenre().equalsIgnoreCase("")) {
                        genre.setText("ז'אנר" + ": " + "מידע אינו זמין");
                    } else
                        genre.setText("ז'אנר" + ": " + LoadScheduleRecordsList.get(running_show_time).getGenre());

                    pgbar_livetime.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(LiveChannels_Play_Activity.this, "אין עוד לוח זמנים זמין", Toast.LENGTH_SHORT).show();

                container_modified_livechannels.setVisibility(View.VISIBLE);
            } else if (container_modified_livechannels.getVisibility() == View.VISIBLE) {

                if (!flag_channle_id_played.equalsIgnoreCase(channel_id)) {
//                    if (progressbar_layout_live_channels.getVisibility() != View.VISIBLE)
                    {
                        if (player != null) {
                            player.stop();
                        }
                        LoadVideo();
                        LoadScheduleRecord("");
                    }
                } else {
                    if (total_lay_with_bar_add_fav.getVisibility() == View.VISIBLE) {
                        press_count_for_add_to_fav++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (press_count_for_add_to_fav == 2) {
                                    if (fav_flag.equalsIgnoreCase("add_fav")) {
                                        no_load_video = "no_need";
                                        AddFavorites();
                                    } else if (fav_flag.equalsIgnoreCase("remove_fav")) {
                                        no_load_video = "no_need";
                                        RemoveFavorites();
                                    }
                                    press_count_for_add_to_fav = 0;
                                }else if(press_count_for_add_to_fav == 1){
                                    if (player != null) {
                                        boolean isPlaying = player.getPlayWhenReady();
                                        player.setPlayWhenReady(!isPlaying);
                                    }
                                    press_count_for_add_to_fav = 0;
                                }
                            }
                        }, 500);

                    }
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {

        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            //The case of processing searchChannel function in threadChannelSelectTimer thread should be avoided.
            //test
            /*int num = 0;
            if (keyCode == KeyEvent.KEYCODE_TAB) num = 1;
            if (keyCode == KeyEvent.KEYCODE_DEL) num = 2;*/

            int num = keyCode - KeyEvent.KEYCODE_0;

            mHandler.removeCallbacks(runnableChannelNumShowTimer);
            if (isChannelNumShowing()) {
                txtChannelNumber.setText("");
                txtChannelNumber.setTextColor(getResources().getColor(R.color.white));
            }

            layChannelNumber.setVisibility(View.VISIBLE);
            String channelStr = txtChannelNumber.getText().toString();
            if (channelStr.length() == 3) {
                channelStr = "";
            } else {
                channelStr = channelStr + num;
            }

            if (channelStr.equals("")) {
                showCurChannelTextView(main_channel_id);
            } else {
                txtChannelNumber.setText(channelStr);
            }

            nChannelCurElapsedTime = 0;
            mHandler.removeCallbacks(runnableChannelNumDownTimer);
            mHandler.postDelayed(runnableChannelNumDownTimer, 4000);
        }
        //else if (keyCode == KeyEvent.KEYCODE_0) {
        // Toast.makeText(LiveChannels_Play_Activity.this,"press 0",Toast.LENGTH_SHORT).show();

           /* if(fav_flag.equalsIgnoreCase("add_fav"))
            {
                no_load_video="no_need";
                AddFavorites();

            }
            else if (fav_flag.equalsIgnoreCase("remove_fav"))
            {
                no_load_video="no_need";
                RemoveFavorites();
            }*/
        return super.onKeyDown(keyCode, event);
    }

    private Runnable runnableChannelNumDownTimer = new Runnable() {
        @Override
        public void run() {
            if (txtChannelNumber.getText() != "")
                searchChannel(Integer.valueOf(txtChannelNumber.getText().toString()));
        }
    };

    private Runnable runnableChannelNumShowTimer = new Runnable() {
        @Override
        public void run() {
            layChannelNumber.setVisibility(View.INVISIBLE);
            txtChannelNumber.setTextColor(getResources().getColor(R.color.white));
            txtChannelNumber.setText("");
        }
    };

    private void showCurChannelTextView(final String channel) {
        layChannelNumber.setVisibility(View.VISIBLE);
        txtChannelNumber.setTextColor(getResources().getColor(R.color.red));
        txtChannelNumber.setText(channel);
        mHandler.postDelayed(runnableChannelNumShowTimer, 8000);

        container_modified_livechannels.setVisibility(View.VISIBLE);
        opened_time = System.currentTimeMillis();
        pressed_time = 0;
    }

    private void searchChannel(int channel) {
        int i;
        for (i = 0; i < array_subchannels_live_tv.size(); i ++) {
            int ch = Integer.valueOf(array_subchannels_live_tv.get(i).getChid());
            if (ch == channel) {
                if (ch == Integer.valueOf(main_channel_id) && selected_channel_position_default == selected_channel_position) {
                    //current channel
                    showCurChannelTextView(main_channel_id);
                } else {
                    //should switch channel
                    switchChannel(i);
                }
                break;
            }
        }
        //cannot find channel
        if (i == array_subchannels_live_tv.size()) {
            //Toast.makeText(LiveChannels_Play_Activity.this, "Cannot find that channel.", Toast.LENGTH_SHORT).show();
            layChannelNumber.setVisibility(View.INVISIBLE);
            txtChannelNumber.setText("");
        }
    }

    private void switchChannel(final int index) {
        selected_channel_position_default = index;
        selected_channel_position = index;
        llay_channel_img.setBackground(null);
        Iv_channel_not_playing.setVisibility(View.INVISIBLE);
        epg_add_to_fav.setVisibility(View.VISIBLE);
        MyVolley.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        setChannelInfoToIndex(index);
        Call_api();
        showCurChannelTextView(main_channel_id);
    }

    private void setChannelInfoToIndex(int index) {
        channel_id = array_subchannels_live_tv.get(index).getSub_channelsid();
        main_channel_id = array_subchannels_live_tv.get(index).getChid();
        tv_channel_name.setText(array_subchannels_live_tv.get(index).getName());

        ImageCacheUtil.with(LiveChannels_Play_Activity.this)
                .load(Constant.BASE_URL_IMAGE + array_subchannels_live_tv.get(index).getImage())
                .resize(200,200)
                .cacheUsage(false, true)
                .into(Iv_channel_played);

        tv_no_of_channel_remote.setText(main_channel_id);

        channel_icon = array_subchannels_live_tv.get(index).getImage();
        AllChannels_Fragment.is_infav = array_subchannels_live_tv.get(index).getIsinfav();
        if (AllChannels_Fragment.is_infav.equalsIgnoreCase("1")) {
            add_fav_text.setText("הסר ממועדפים");
            add_To_fav_img.setImageResource(R.drawable.ok_remove_fav_icon);
            fav_flag = "remove_fav";
        } else {
            add_fav_text.setText("הוסף למועדפים");
            add_To_fav_img.setImageResource(R.drawable.ok_add_fav_icon);
            fav_flag = "add_fav";
        }
        selected_channel_position = index;

        //total_lay_with_bar_add_fav.setVisibility(View.INVISIBLE);     // ???
        emptyField();
        LoadScheduleRecord(date);
        pgbar_livetime.setVisibility(View.VISIBLE);
    }

    private boolean isChannelNumDowning() {
        if (layChannelNumber.getVisibility() == View.VISIBLE && txtChannelNumber.getTextColors().getColorForState(null, Color.RED) == getResources().getColor(R.color.white))
            return true;
        return false;
    }

    private boolean isChannelNumShowing() {
        if (layChannelNumber.getVisibility() == View.VISIBLE && txtChannelNumber.getTextColors().getColorForState(null, Color.WHITE) == getResources().getColor(R.color.red))
            return true;
        return false;
    }

    public void showAToast(String st) { //"Toast toast" is declared in the class
        try {
            toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        }
        toast.show();  //finally display it
    }

    private void emptyField() {
        end_time.setText("");
        start_time.setText("");
        //start_time.setVisibility(View.GONE);
        tv_title_of_show.setVisibility(View.VISIBLE);
        tv_title_of_show.setText("");
        tv_description_of_show.setText("");
        genre.setText("");
        tv_dash.setVisibility(View.GONE);

        Iv_next_show.setVisibility(View.INVISIBLE);
        Iv_previous_show.setVisibility(View.INVISIBLE);
    }

    private Player.EventListener playerEventListener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

            if (!isLoading) {
                DismissProgress_Player(LiveChannels_Play_Activity.this);
            }

            saveState();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                player.setPlayWhenReady(false);
                player.stop();
                DismissProgress_Player(LiveChannels_Play_Activity.this);
            }else if (playbackState == Player.STATE_READY) {
                showOfflineMessage(false);
                DismissProgress_Player(LiveChannels_Play_Activity.this);
                nextFullBackHandler.removeCallbacks(nextFullBackRunnable);
            }else if (playbackState == Player.STATE_BUFFERING) {
                if (NetworkUtil.checkNetworkAvailable(LiveChannels_Play_Activity.this)) {
                    //Remove the conneting message and reconnect the player
                    ShowProgressDilog_Player(LiveChannels_Play_Activity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkUtil.hasActiveInternetConnection(LiveChannels_Play_Activity.this)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DismissProgress_Player(LiveChannels_Play_Activity.this);
                                        DismissProgress(LiveChannels_Play_Activity.this);
                                    }
                                });
                            }
                        }
                    }).start();

                } else {
                    DismissProgress_Player(LiveChannels_Play_Activity.this);
                    DismissProgress(LiveChannels_Play_Activity.this);
                }
            }else{

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
            //Show reconnecting message
//            if (NetworkUtil.isServerAlive(LiveChannels_Play_Activity.this, videoBackupLists.get(0)))
            showOfflineMessage(true);
            DismissProgress_Player(LiveChannels_Play_Activity.this);

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

        tv_connectionstatus.setVisibility(View.GONE);
        layConnectionState.setVisibility(View.GONE);

        if (player.getCurrentPosition() >= player.getBufferedPosition()) {
            if (bShow) {
                // hide small offline message
                if (mTimerForSmallConnection != null) {
                    mTimerForSmallConnection.cancel();
                    mTimerForSmallConnection = null;
                }
                if (!Constant.isInternetAvailable())
                    tv_connectionstatus.setVisibility(View.VISIBLE);
            } else {
                tv_connectionstatus.setVisibility(View.GONE);
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
