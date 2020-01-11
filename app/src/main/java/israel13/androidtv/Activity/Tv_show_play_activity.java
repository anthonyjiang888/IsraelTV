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
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
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
import org.json.JSONException;
import org.json.JSONObject;

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
import israel13.androidtv.Fragments.VodSubcategory_Fragment;
import israel13.androidtv.Fragments.VodTvShowVideo_Fragment;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.Others.ConnectivityReceiver;
import israel13.androidtv.Others.CustomTimeBar_ForPlayer;
import israel13.androidtv.R;
import israel13.androidtv.Service.ResetCacheService;
import israel13.androidtv.Setter_Getter.SetgetVodTvShowVideoPlay;
import israel13.androidtv.Setter_Getter.Setget_tvshowplaylist;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomCacheDataSourceFactory;
import israel13.androidtv.Utils.ImageCacheUtil;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.UiUtils;

/**
 * Created by puspak on 23/6/17.
 */

public class Tv_show_play_activity extends AppCompatActivity implements IJSONParseListener, VideoRendererEventListener, ConnectionChange {

	private TextView tvshows_episode_name, tvshows_seasonname, tvshows_tv_show_name, tv_description_of_show_tvshows, add_to_fav_tvshow_;
	private TextView genre_tvshows, noof_watches_tvshows, time_tvshows, year_tvshows, upload_year_tvshows;
	private ImageView Iv_season_played_tvshows;
	private RatingBar ratingbars_tvshows;
	private ViewGroup jwPlayerViewContainer;

	//JWPlayerView playerView_vod_tvshow_videoplay;
	// EMVideoView playerView_vod_tvshow_videoplay;
	private SimpleExoPlayerView playerView_vod_tvshow_videoplay;
	private SimpleExoPlayer player;
	public static boolean flag_delete;
	public static boolean isSortDown;
	private SharedPreferences logindetails;
	private ProgressDialog pDialog = null;
	private int TVSHOW_PLAY = 211;
	private int ADD_FAVORITES = 213;
	private int REMOVE_FAVORITES = 214;
	private StringBuilder videoplayurl;
	private String fav_flag = "", cat_name = "";
	private Thread myThread1 = null;
	private RelativeLayout container_modified_livechannels;

	// small offline/online message
	private Timer mTimerForSmallConnection;
	private Handler mHandlerForSmallConnection;
	private LinearLayout layConnectionState;
	private TextView txtConnectionState;
    private boolean mLastConnectionStatus = true;

	private long pressed_time = 0;
	private long opened_time = 0;
	private ArrayList<String> videoBackupLists;
	private int flag_glob = 0;
	private int m_last_video_index = 0;
	private AlertDialog m_transitionDialog = null;
	private static final String TAG = "Tv_show_play";
	private ArrayList<SetgetVodTvShowVideoPlay> playlisttvshowlist;
	private CustomTimeBar_ForPlayer exo_progress;
	private LinearLayout tv_connectionstatus_tv_show;

	public String flag_pressed = "";
	public ArrayList<Setget_tvshowplaylist> full_links_list = new ArrayList<>();
	public List<MediaSource> tvshowPlaylist;
	public static int Pausestate_Window_Index = C.INDEX_UNSET;
	public static long Pausestate_lastposition = C.POSITION_UNSET;
	public static int flag_glob_at_connection_change = 0;
	private boolean mActivityAlive;
	public boolean isBackClose;
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
		setContentView(R.layout.play_tvshows_activity);

		layConnectionState = (LinearLayout)findViewById(R.id.layout_connection_state);
		txtConnectionState = (TextView)findViewById(R.id.text_connection_state);

		logindetails = this.getSharedPreferences("logindetails", MODE_PRIVATE);

		// jwPlayerViewContainer = (ViewGroup)findViewById(R.id.container_tvshows_);
		container_modified_livechannels = (RelativeLayout) findViewById(R.id.container_modified_tvshows_channels);
		tvshows_episode_name = (TextView) findViewById(R.id.tvshows_episode_name);
		tvshows_seasonname = (TextView) findViewById(R.id.tvshows_seasonname);
		tvshows_tv_show_name = (TextView) findViewById(R.id.tvshows_tv_show_name);
		tv_description_of_show_tvshows = (TextView) findViewById(R.id.tv_description_of_show_tvshows);
		genre_tvshows = (TextView) findViewById(R.id.genre_tvshows);
		noof_watches_tvshows = (TextView) findViewById(R.id.noof_watches_tvshows);
		time_tvshows = (TextView) findViewById(R.id.time_tvshows);
		year_tvshows = (TextView) findViewById(R.id.year_tvshows);
		upload_year_tvshows = (TextView) findViewById(R.id.upload_year_tvshows);
		// add_to_fav_tvshow_=(TextView)findViewById(R.id.add_to_fav_tvshow_);
		ratingbars_tvshows = (RatingBar) findViewById(R.id.ratingbars_tvshows);

		Iv_season_played_tvshows = (ImageView) findViewById(R.id.Iv_season_played_tvshows);
		exo_progress = (CustomTimeBar_ForPlayer) findViewById(R.id.exo_progress);
		tv_connectionstatus_tv_show = (LinearLayout) findViewById(R.id.tv_connectionstatus_tv_show);
		playerView_vod_tvshow_videoplay = (SimpleExoPlayerView) findViewById(R.id.playerView_vod_tvshow_videoplay);
		//  add_to_fav_tvshow_.requestFocus();
		//  add_to_fav_tvshow_.setSelected(true);


		SharedPreferences rememberPos = this.getSharedPreferences("position", MODE_PRIVATE);

		Pausestate_lastposition = rememberPos.getLong("Pausestate_lastposition",C.POSITION_UNSET);
		Pausestate_Window_Index = rememberPos.getInt("Pausestate_WindowIndex_", C.INDEX_UNSET);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		SharedPreferences rememberState = getSharedPreferences("remember", MODE_PRIVATE);
		if (!rememberState.getString("selecte_epi_id", "").isEmpty()) {
				VodTvShowVideo_Fragment.selecte_epi_id = rememberState.getString("selecte_epi_id", "");
		}

		requestTVShowVideo(VodTvShowVideo_Fragment.selecte_epi_id);
//		nextFullBackHandler.postDelayed(nextFullBackRunnable, Constant.RECONNECT_NEXT_TIME_OUT);



	}

	public void initial_player() {

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
		playerView_vod_tvshow_videoplay.setUseController(true);

		//playerView_vod_tvshow_videoplay.requestFocus();

		// Bind the player to the view.
		playerView_vod_tvshow_videoplay.setPlayer(player);
	}

	void showTVShowDataByEpisode() {
		tvshows_episode_name.setText(" - " + playlisttvshowlist.get(m_last_video_index).getName());
		tv_description_of_show_tvshows.setText(playlisttvshowlist.get(m_last_video_index).getDescription());

		noof_watches_tvshows.setText("צפיות : " + playlisttvshowlist.get(m_last_video_index).getViews());
		time_tvshows.setText("זמן" + ": " + Constant.hour_min_format(Constant.parseInt(playlisttvshowlist.get(m_last_video_index).getLength())));
		year_tvshows.setText(playlisttvshowlist.get(m_last_video_index).getYear());
		try {
			upload_year_tvshows.setText("הועלה בתאריך" + ": " + Constant.getUnixDate(playlisttvshowlist.get(m_last_video_index).getCreated()));
		} catch (Exception e) {
			upload_year_tvshows.setText("הועלה בתאריך" + ": " + "מידע אינו זמין");
		}

		int duration = (Constant.parseInt(playlisttvshowlist.get(m_last_video_index).getLength()));

		if (playlisttvshowlist.get(m_last_video_index).getShowpic().equalsIgnoreCase("no data")) {
			//no changes
		} else {
			String imageLink = "http://images.peer5.net/images/b/" + playlisttvshowlist.get(m_last_video_index).getShowpic();

			ImageCacheUtil.with(Tv_show_play_activity.this)
					.load(imageLink)
					.resize(200,200)
					.cacheUsage(false, true)
					.into(Iv_season_played_tvshows);
		}

		container_modified_livechannels.setVisibility(View.VISIBLE);
		flag_pressed = "pressed";
		pressed_time = System.currentTimeMillis();
		playerView_vod_tvshow_videoplay.setControllerShowTimeoutMs(8000);
	}

	void showTVShowData() {
		tvshows_episode_name.setText(" - " + VodTvShowVideo_Fragment.selecte_epi_name);
		tvshows_seasonname.setText("- " + VodTvShowVideo_Fragment.season_name);
		tvshows_tv_show_name.setText(VodSubcategory_Fragment.vod_subcategory_name);
		tv_description_of_show_tvshows.setText(VodTvShowVideo_Fragment.selecte_epi_description);
		//genre_tvshows.setText("ז'אנר" + ": " + VodTvShowVideo_Fragment.selecte_epi_genre);

		if (VodTvShowVideo_Fragment.selecte_epi_genre.length() > 11)
			genre_tvshows.setText("ז'אנר" + ": " + VodTvShowVideo_Fragment.selecte_epi_genre.substring(0, 11) + "...");
		else
			genre_tvshows.setText("ז'אנר" + ": " + VodTvShowVideo_Fragment.selecte_epi_genre);

		noof_watches_tvshows.setText("צפיות : " + VodTvShowVideo_Fragment.selecte_epi_views);
		time_tvshows.setText("זמן" + ": " + Constant.hour_min_format(Constant.parseInt(VodTvShowVideo_Fragment.selecte_epi_length)));
		year_tvshows.setText(VodTvShowVideo_Fragment.selecte_epi_year);
		try {
			upload_year_tvshows.setText("הועלה בתאריך" + ": " + Constant.getUnixDate(VodTvShowVideo_Fragment.selecte_epi_created));
		} catch (Exception e) {
			upload_year_tvshows.setText("הועלה בתאריך" + ": " + "מידע אינו זמין");
		}

		ratingbars_tvshows.setRating(Constant.parseFloat(VodTvShowVideo_Fragment.selecte_epi_stars));
		if(VodTvShowVideo_Fragment.selecte_epi_showpic!=null) {
			ImageCacheUtil.with(this)
					.load("http:"+VodTvShowVideo_Fragment.selecte_epi_showpic)
					.resize(200,200)
					.cacheUsage(false, true)
					.into(Iv_season_played_tvshows);
		}else{
			Iv_season_played_tvshows.setImageResource(R.drawable.channel_placeholder);
		}



	}

	void requestTVShowVideo(String id) {
		//test
		/*testPlayEndState();
		return;*/
		container_modified_livechannels.setVisibility(View.GONE);
		VodTvShowVideo_Fragment.selecte_epi_id = id;

		JSONRequestResponse mResponse = new JSONRequestResponse(this);
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		parms.putString("vodid", id);
		parms.putString("cookiekey", Constant.getDeviceUUID(this));
		if (isSortDown == true)
			parms.putString("append", "previous");
		MyVolley.init(this);
		ShowProgressDilog(this);
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "loadvod2.php", TVSHOW_PLAY, this, parms, false);
	}

	public void Set_player_video(List<MediaSource> playlist, int duration) {
//		ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(
//				playlist.toArray(new MediaSource[playlist.size()]));

		// Prepare the player with the source.
		if (playlist.size() <= m_last_video_index)
			return;

		releasePlayer();
		if (!mActivityAlive) return;
		initial_player();

		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}

		player.prepare(playlist.get(m_last_video_index));

		player.setPlayWhenReady(true); //run file/link when ready to play.
		player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution

		playerView_vod_tvshow_videoplay.setControllerShowTimeoutMs(8000);
	}

	public void Set_player_video_fallback(List<MediaSource> playlist, int current_index, long last_position) {
		System.out.println("Vod Tvshow pathinfo response ----------------" + current_index + "ttttttt" + last_position);
		releasePlayer();
		if (!mActivityAlive) return;
		initial_player();

		// Prepare the player with the source.
		player.prepare(playlist.get(m_last_video_index));
		try {
			if (current_index >= 0 && last_position >= 0)
				player.seekTo(current_index, last_position);
        } catch (Exception ex) {
		    ex.printStackTrace();
        }
		player.setPlayWhenReady(true); //run file/link when ready to play.
		player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
		player.setRepeatMode(Player.REPEAT_MODE_OFF);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		DismissProgress(this);
		Constant.ShowErrorToast(this);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		if (requestCode == TVSHOW_PLAY) {
			DismissProgress(this);
			try {
                JSONArray pathinfo = response.getJSONArray("pathinfo");
                JSONObject object1 = pathinfo.getJSONObject(0);
                JSONObject object3 = pathinfo.getJSONObject(1);
				VodSubcategory_Fragment.vod_subcategory_name = object3.optString("name","");
				VodTvShowVideo_Fragment.season_name = object1.optString("name","");
				VodTvShowVideo_Fragment.selecte_epi_stars = object1.optString("stars", "0.0");

			} catch (JSONException e) {
				e.printStackTrace();
			}




			videoplayurl = new StringBuilder();

			System.out.println("Vod Tvshow Videoplay response ----------------" + response.toString());

			try {
                JSONArray pathinfo = response.getJSONArray("pathinfo");
                JSONObject object1 = pathinfo.getJSONObject(0);
				System.out.println("Vod Tvshow pathinfo response ----------------" + pathinfo.toString());
				System.out.println("Vod Tvshow object1 response ----------------" + object1.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String errorcode = "";
			try {
				full_links_list = new ArrayList<>();

				try {
					errorcode = response.getString("error");
				} catch (Exception e) {
					errorcode = response.getString("errorcode");
				}
				JSONArray fulllinks = response.getJSONArray("fulllinks");

				for (int i = 0; i < fulllinks.length(); i++) {
					try {
						playlisttvshowlist = new ArrayList<>();
						tvshowPlaylist = new ArrayList<>();

						Setget_tvshowplaylist setgetPlayList = new Setget_tvshowplaylist();
						setgetPlayList.setIndex(String.valueOf(i));

						JSONArray arr = fulllinks.getJSONArray(i);

						for (int j = 0; j < arr.length(); j++) {

							JSONObject object = arr.getJSONObject(j);
							if (i == 0 && j ==0) {
								VodTvShowVideo_Fragment.selecte_epi_name = object.getString("name");
								VodTvShowVideo_Fragment.selecte_epi_year = object.getString("year");
								VodTvShowVideo_Fragment.selecte_epi_genre = object.getString("genre");
								VodTvShowVideo_Fragment.selecte_epi_vodlist = object.getString("vodlist");
								VodTvShowVideo_Fragment.selecte_epi_description = object.getString("description");
								if (VodTvShowVideo_Fragment.selecte_epi_description.equalsIgnoreCase("null"))
									VodTvShowVideo_Fragment.selecte_epi_description = "מידע אינו זמין";
								VodTvShowVideo_Fragment.selecte_epi_created = object.getString("created");
								VodTvShowVideo_Fragment.selecte_epi_updated = object.getString("updated");
								VodTvShowVideo_Fragment.selecte_epi_views = object.getString("views");
								VodTvShowVideo_Fragment.selecte_epi_length = object.getString("length");
//								VodTvShowVideo_Fragment.selecte_epi_stars = object.optString("stars", "0.0");
								try {

									JSONArray jsonPictureList = object.getJSONArray("picture");
									if (jsonPictureList != null && jsonPictureList.length() > 0) {
										JSONObject item = jsonPictureList.getJSONObject(0);
										if (item != null) {
											VodTvShowVideo_Fragment.selecte_epi_showpic = "//images.peer5.net/images/b/"+item.optString("big", "no data");
										}
									}
								} catch (Exception ex) {
									ex.printStackTrace();
									VodTvShowVideo_Fragment.selecte_epi_showpic = "no data";
								}
//											VodTvShowVideo_Fragment.selecte_epi_showpic = object.getString("temppicture");
//								VodTvShowVideo_Fragment.season_name = list.get(position).getSeason_name();
								//VodSubcategory_Fragment.vod_subcategory_name = list.get(position).getTvshow_name();
							}
							SetgetVodTvShowVideoPlay videoPlay = new SetgetVodTvShowVideoPlay();
							videoPlay.setTvshow_id(object.getString("id"));
							videoPlay.setYear(object.getString("year"));
							videoPlay.setGenre(object.optString("genre", "מידע אינו זמין"));

							videoPlay.setVodlist(object.getString("vodlist"));
							videoPlay.setName(object.getString("name"));
							videoPlay.setDescription(object.optString("description", "מידע אינו זמין"));
							videoPlay.setCreated(object.optString("created", "מידע אינו זמין"));
							videoPlay.setUpdated(object.getString("updated"));
							videoPlay.setViews(object.optString("views", ""));
							videoPlay.setLength(object.optString("length", "0"));
							videoPlay.setStars(object.optString("stars", "0.0"));

							try {
								JSONArray jsonPictureList = object.getJSONArray("picture");
								if (jsonPictureList != null && jsonPictureList.length() > 0) {
									JSONObject item = jsonPictureList.getJSONObject(0);
									if (item != null) {
										videoPlay.setShowpic(item.optString("big", "no data"));
									}
								}
							} catch (Exception ex) {
								ex.printStackTrace();
								videoPlay.setShowpic("no data");
							}

							videoPlay.setIsplaying("false");
							playlisttvshowlist.add(videoPlay);

							CustomCacheDataSourceFactory dataSourceFactory = new CustomCacheDataSourceFactory(this);
							MediaSource videoSource = new HlsMediaSource(Uri.parse(object.getString("playurl").toString()), dataSourceFactory, 1, null, null);
							tvshowPlaylist.add(videoSource);
						}
						setgetPlayList.setPlaylist(tvshowPlaylist);
						full_links_list.add(setgetPlayList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// tv_description_of_show_tvshows.setText(playlisttvshowlist.get());
			if (errorcode.equalsIgnoreCase("101")) {
				Constant.showPopup_Error_basic_package(Tv_show_play_activity.this);

			} else if (errorcode.equalsIgnoreCase("999")) {
				Constant.showPopup_Error_expire_package(Tv_show_play_activity.this);
			} else if (errorcode.equalsIgnoreCase("58403")) {
				Constant.showPopup_VPN_Block(Tv_show_play_activity.this);
			} else if (errorcode.equalsIgnoreCase("998")) {
				try {
					Constant.showPopup_Error_freeze_package(Tv_show_play_activity.this, Constant.getUnixDate(response.getString("endtime")));
				} catch (Exception e) {
					Constant.showPopup_Error_freeze_package(Tv_show_play_activity.this, "");
				}
			} else if (errorcode.equalsIgnoreCase("99")) {
				UiUtils.logout(this);
				return;
			} else if (errorcode.equalsIgnoreCase("997")) {
				Constant.showPopup_Error_suspend_package(Tv_show_play_activity.this);
			} else {
				m_last_video_index = 0;
				Log.i("hereweare", ""+Pausestate_lastposition);
				if (Pausestate_lastposition != C.POSITION_UNSET)
					Set_player_video_fallback(full_links_list.get(0).getPlaylist(), Pausestate_Window_Index, Pausestate_lastposition);
				else
					Set_player_video(full_links_list.get(0).getPlaylist(), Constant.parseInt(playlisttvshowlist.get(0).getLength()));

				opened_time = System.currentTimeMillis();
			}
			showTVShowData();
			container_modified_livechannels.setVisibility(View.VISIBLE);
			exo_progress.setVisibility(View.GONE);
			playerView_vod_tvshow_videoplay.hideController();
		} else if (requestCode == ADD_FAVORITES) {
			DismissProgress(this);
			System.out.println("Response for add Live Channels --------" + response.toString());

			Toast.makeText(this, "נוסף בהצלחה למועדפים", Toast.LENGTH_SHORT).show();
			add_to_fav_tvshow_.setText("הסר ממועדפים");
			fav_flag = "remove_fav";
			//LoadScheduleRecord(date);
		} else if (requestCode == REMOVE_FAVORITES) {

			DismissProgress(this);
			System.out.println("Response for Remove Live Channels --------" + response.toString());

			Toast.makeText(this, "הוצא בהצלחה מהמועדפים", Toast.LENGTH_SHORT).show();
			// LoadScheduleRecord(date);
			add_to_fav_tvshow_.setText("הוסף למועדפיn");
			fav_flag = "add_fav";
		}

	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {

	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {

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
		} else {
			//Show reconnecting message
			DismissProgress(Tv_show_play_activity.this);
			flag_glob_at_connection_change = flag_glob;
			showOfflineMessage(true);
		}
	}

	void ShowProgressDilog(Context c) {

		if (m_transitionDialog != null
				&& m_transitionDialog.isShowing()) {
			return;
		}

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
			Set_player_video_fallback(full_links_list.get(flag_glob).getPlaylist(), Pausestate_Window_Index, Pausestate_lastposition);
		} catch (Exception e) {
			flag_glob = 0;
			showTVShowDataByEpisode();
			requestTVShowVideo(VodTvShowVideo_Fragment.selecte_epi_id);
		}
	}

	private boolean isLastEpisode() {
		if ( !full_links_list.isEmpty() && m_last_video_index == full_links_list.get(flag_glob).getPlaylist().size() - 1 )
			return true;
		else
			return false;
	}

	private void showTransitionDialog() {
		if (m_transitionDialog != null && m_transitionDialog.isShowing())
			return;

		View alertDlgView = getLayoutInflater().inflate(R.layout.alert_dialog_endof_episode, null);
		ImageView imgNextEpisode = (ImageView)alertDlgView.findViewById(R.id.img_next_episode);
		final TextView txtEpisodeName = (TextView)alertDlgView.findViewById(R.id.txt_next_episode_name);
		Button butTransitionCancel = (Button)alertDlgView.findViewById(R.id.but_transition_cancel);
		final FrameLayout frameLayoutEpisode = (FrameLayout)alertDlgView.findViewById(R.id.framelayout_next_episode);

		int nextIndex = m_last_video_index + 1;
		String name = null, imageLink = null;
		if (playlisttvshowlist != null && playlisttvshowlist.size() > nextIndex) {
			name = playlisttvshowlist.get(nextIndex).getName();
			imageLink = "http://images.peer5.net/images/b/" + playlisttvshowlist.get(nextIndex).getShowpic();
		}
		txtEpisodeName.setText(name);

		ImageCacheUtil.with(this)
				.load(imageLink)
				.resize(200,200)
				.cacheUsage(false, true)
				.into(imgNextEpisode);

		m_transitionDialog = new AlertDialog.Builder(this)
				.setView(alertDlgView)
				.create();
		m_transitionDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent));

		frameLayoutEpisode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_transitionDialog.dismiss();
				reqNextVideo();
			}
		});

		frameLayoutEpisode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, final boolean hasFocus) {
				if (hasFocus)
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							txtEpisodeName.setSelected(hasFocus);
						}
					}, 2000);
				else
					txtEpisodeName.setSelected(hasFocus);
			}
		});

		butTransitionCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_transitionDialog.dismiss();
				isBackClose = true;
				clearSavedData();
				Tv_show_play_activity.this.finish();

			}
		});
		m_transitionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
				}
				return false;
			}
		});

		m_transitionDialog.show();
	}

	private void reqNextVideo() {
		m_last_video_index++;
		showTVShowDataByEpisode();
		Set_player_video(full_links_list.get(flag_glob).getPlaylist(), 0);
		requestTVShowVideo(playlisttvshowlist.get(m_last_video_index).getTvshow_id());
	}

	@Override
	public void onResume() {


        mActivityAlive = true;

		showOfflineMessage(false);

		ConnectivityReceiver.mcontext = this;
		initial_player();
		if (Pausestate_lastposition != C.POSITION_UNSET && flag_delete != true) {
			container_modified_livechannels.setVisibility(View.VISIBLE);
			flag_pressed = "pressed";
			pressed_time = System.currentTimeMillis();
			if (flag_glob > 0) {
				try {
					Set_player_video_fallback(full_links_list.get(flag_glob).getPlaylist(), Pausestate_Window_Index, Pausestate_lastposition);
				} catch (Exception e) {
					Set_player_video_fallback(full_links_list.get(0).getPlaylist(), Pausestate_Window_Index, Pausestate_lastposition);
				}
			} else {
				if (full_links_list.isEmpty()) {
					finish();
				} else {
					requestTVShowVideo(VodTvShowVideo_Fragment.selecte_epi_id);
				}
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
		SharedPreferences remember = getSharedPreferences("remember", MODE_PRIVATE);
		SharedPreferences.Editor editor = remember.edit();
		editor.putString("selecte_epi_id", VodTvShowVideo_Fragment.selecte_epi_id);
		editor.putString("Where", "TvShowPlay");
		editor.putBoolean("isSortDown",isSortDown);
		editor.putBoolean("isHome", true);
		editor.commit();
	}
	@Override
	public void onPause() {



		if (myThread1 != null) {
			myThread1.interrupt();
			myThread1 = null;
		}
		ConnectivityReceiver.mcontext = null;

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
		///playerView_vod_tvshow_videoplay.onPause();
		ConnectivityReceiver.mcontext = null;

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
		//releasePlayer();

		//dialog dismiss
		DismissProgress(this);

		nextFullBackHandler.removeCallbacks(nextFullBackRunnable);

		super.onStop();
	}

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
		//  playerView_vod_tvshow_videoplay.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			flag_pressed = "pressed";
			pressed_time = System.currentTimeMillis();
			if (container_modified_livechannels.getVisibility() == View.GONE) {
				container_modified_livechannels.setVisibility(View.VISIBLE);

			}
			exo_progress.setVisibility(View.GONE);
			playerView_vod_tvshow_videoplay.hideController();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			flag_pressed = "pressed";

			pressed_time = System.currentTimeMillis();
			if (container_modified_livechannels.getVisibility() == View.GONE) {
				container_modified_livechannels.setVisibility(View.VISIBLE);

			}
			exo_progress.setVisibility(View.GONE);
			playerView_vod_tvshow_videoplay.hideController();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			playerView_vod_tvshow_videoplay.showController();
		   /* long currentposition=player.getCurrentPosition();
			long seekto=(currentposition-30000);
            if (seekto<0)
            {
                player.seekTo(C.TIME_UNSET);
            }
            else player.seekTo(seekto);*/
			exo_progress.requestFocus();
			exo_progress.setVisibility(View.VISIBLE);
			if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
				container_modified_livechannels.setVisibility(View.GONE);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			playerView_vod_tvshow_videoplay.showController();
          /*  long currentposition=player.getCurrentPosition();
            long seekto=(currentposition+30000);
            if (seekto>player.getDuration())
            {
                player.seekTo(player.getDuration());
            }
            else player.seekTo(seekto);*/
			exo_progress.requestFocus();
			exo_progress.setVisibility(View.VISIBLE);
			if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
				container_modified_livechannels.setVisibility(View.GONE);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == KeyEvent.KEYCODE_ENTER) {
			flag_pressed = "pressed";

			pressed_time = System.currentTimeMillis();
			if (container_modified_livechannels.getVisibility() == View.GONE) {
				container_modified_livechannels.setVisibility(View.VISIBLE);
			}
			if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
				if (player != null) {
					if (player.getPlayWhenReady()) {
						player.setPlayWhenReady(false);
					} else player.setPlayWhenReady(true);
				}
			}
			exo_progress.setVisibility(View.GONE);
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
			playerView_vod_tvshow_videoplay.showController();
			flag_pressed = "pressed";
			pressed_time = System.currentTimeMillis();
			if (player != null) {
				long currentposition = player.getCurrentPosition();
//			long seekto = (currentposition - 30000);
				long seekto = (currentposition - 180000);
				if (seekto < 0) {
					player.seekTo(C.TIME_UNSET);
				} else player.seekTo(seekto);
			}
			playerView_vod_tvshow_videoplay.setControllerShowTimeoutMs(8000);//
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
			playerView_vod_tvshow_videoplay.showController();
			flag_pressed = "pressed";
			pressed_time = System.currentTimeMillis();
			if (player != null) {
				long currentposition = player.getCurrentPosition();
//			long seekto = (currentposition + 30000);
				long seekto = (currentposition + 180000);
				if (seekto > player.getDuration()) {
					player.seekTo(player.getDuration());
				} else player.seekTo(seekto);
			}
			playerView_vod_tvshow_videoplay.setControllerShowTimeoutMs(8000);//
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
			playerView_vod_tvshow_videoplay.showController();
			flag_pressed = "pressed";
			pressed_time = System.currentTimeMillis();
			if (player != null) {
				if (player.getPlayWhenReady()) {
					player.setPlayWhenReady(false);
				} else player.setPlayWhenReady(true);
			}
		}

		return super.onKeyDown(keyCode, event);
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
								playerView_vod_tvshow_videoplay.hideController();
								//exo_progress.requestFocus();
							}
						}
					} else {
						if (opened_time != 0) {
							if (current_time - opened_time >= 8000) {
								if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
									container_modified_livechannels.setVisibility(View.GONE);
									playerView_vod_tvshow_videoplay.hideController();
									//exo_progress.requestFocus();
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

		if (container_modified_livechannels.getVisibility() == View.VISIBLE) {
			container_modified_livechannels.setVisibility(View.GONE);
			//exo_progress.requestFocus();
		} else {
			isBackClose = true;
			clearSavedData();
			super.onBackPressed();
		}
	}

	Player.EventListener playerEventListener = new Player.EventListener() {

		@Override
		public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

		}

		@Override
		public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

		}

		@Override
		public void onLoadingChanged(boolean isLoading) {
			saveData();

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
			if (playbackState == Player.STATE_ENDED) {
				player.setPlayWhenReady(false);
				player.stop();
				if (Tv_show_play_activity.this != null && !Tv_show_play_activity.this.isFinishing() && mActivityAlive) {
					if (Tv_show_play_activity.this.isLastEpisode() == false) {
						Tv_show_play_activity.this.showTransitionDialog();
					} else {
						isBackClose = true;
						clearSavedData();
						finish();

					}
				}
				DismissProgress(Tv_show_play_activity.this);
			}
			if (playbackState == Player.STATE_READY) {
				showOfflineMessage(false);
				DismissProgress(Tv_show_play_activity.this);
				nextFullBackHandler.removeCallbacks(nextFullBackRunnable);
			}
			if (playbackState == Player.STATE_BUFFERING) {
				if (NetworkUtil.checkNetworkAvailable(Tv_show_play_activity.this)) {
					//Remove the conneting message and reconnect the player

					ShowProgressDilog(Tv_show_play_activity.this);
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (NetworkUtil.hasActiveInternetConnection(Tv_show_play_activity.this)) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {

									}
								});
							} else {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										DismissProgress(Tv_show_play_activity.this);
										flag_glob_at_connection_change = flag_glob;
									}
								});
							}
						}
					}).start();

					// if(!player.getPlayWhenReady())
					//    Set_player_video_fallback(full_links_list.get(0).getPlaylist(), Pausestate_Window_Index, Pausestate_lastposition);
				} else {
					//Show reconnecting message
					DismissProgress(Tv_show_play_activity.this);
					flag_glob_at_connection_change = flag_glob;
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
			DismissProgress(Tv_show_play_activity.this);

			// should wait for 3 seconds before reconnecting
			nextFullBackHandler.postDelayed(nextFullBackRunnable, Constant.RECONNECT_NEXT_TIME_OUT);
		}

		@Override
		public void onPositionDiscontinuity(int reason) {
			int latestWindowIndex = player.getCurrentWindowIndex();
			if (latestWindowIndex != m_last_video_index) {
				//m_last_video_index = latestWindowIndex;
				//showTVShowDataByEpisode();
			}
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

		tv_connectionstatus_tv_show.setVisibility(View.GONE);
		layConnectionState.setVisibility(View.GONE);

		if (player.getCurrentPosition() >= player.getBufferedPosition()) {
			if (bShow) {
				// hide small offline message
				if (mTimerForSmallConnection != null) {
					mTimerForSmallConnection.cancel();
					mTimerForSmallConnection = null;
				}
				if (!Constant.isInternetAvailable())
					tv_connectionstatus_tv_show.setVisibility(View.VISIBLE);
			} else {
				tv_connectionstatus_tv_show.setVisibility(View.GONE);
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
