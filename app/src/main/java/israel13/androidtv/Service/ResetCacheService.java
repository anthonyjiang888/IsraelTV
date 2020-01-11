package israel13.androidtv.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.StatFs;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;

import israel13.androidtv.Utils.Constant;

public class ResetCacheService extends Service {

	private SharedPreferences prefs;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		checkSizeAndResetCacheDatas();
		stopSelf();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	private void checkSizeAndResetCacheDatas() {
		prefs = this.getSharedPreferences("logindetails", this.MODE_PRIVATE);
		boolean bForce = prefs.getBoolean("forceClearCache", false);
		if (bForce) {
			//ResetCacheDatas();
			SharedPreferences.Editor edit = prefs.edit();
			edit.remove("forceClearCache");
			edit.commit();

			return;
		}

		File internalCache = this.getCacheDir();
		List<File> fileList = getListFiles(internalCache);

		long size = 0;
		if (fileList != null) {
			try {
				for (File f : fileList) {
					size += f.length();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.e("cacheService", "Cache size:  " + size  + " (" + size/1024/1024 + " mb)");
		if(size >= 50 * 1024 * 1024) // 50mb
			ResetCacheDatas();
	}

	private List<File> getListFiles(File parentDir) {
		try {
			ArrayList<File> inFiles = new ArrayList<File>();
			File[] files = parentDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					inFiles.addAll(getListFiles(file));
				} else {
					inFiles.add(file);
				}
			}
			return inFiles;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void ResetCacheDatas() {
		String str_id, str_name, str_password, str_expires;
		String str_package_id, str_package_name, str_package_price, str_package_pricestr, str_package_pgname, str_package_description;
		String str_regtime, str_phone, str_carousel, str_isactive, str_activekey, str_email, str_status, str_ptime, str_sid;
		String str_deviceId;

		int exoplaybackbuffer, exominbuffer;

		prefs = this.getSharedPreferences("logindetails", this.MODE_PRIVATE);
		if (prefs != null) {
			str_id = prefs.getString("id", "");
			str_name = prefs.getString("name", "");
			str_password = prefs.getString("password", "");
			str_expires = prefs.getString("expires", "");

			str_package_id = prefs.getString("package_id", "");
			str_package_name = prefs.getString("package_name", "");
			str_package_price = prefs.getString("package_price", "");
			str_package_pricestr = prefs.getString("package_pricestr", "");
			str_package_pgname = prefs.getString("package_pgname", "");
			str_package_description = prefs.getString("package_description", "");

			str_regtime = prefs.getString("regtime", "");
			str_phone = prefs.getString("phone", "");
			str_carousel = prefs.getString("carousel", "");
			str_isactive = prefs.getString("isactive", "");
			str_activekey = prefs.getString("activekey", "");
			str_email = prefs.getString("email", "");
			str_status = prefs.getString("status", "");
			str_ptime = prefs.getString("ptime", "");
			str_sid = prefs.getString("sid", "");

			str_deviceId = prefs.getString("deviceID", "");

			exoplaybackbuffer = prefs.getInt("exoplaybackbuffer", DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS);
			exominbuffer = prefs.getInt("exominbuffer", DefaultLoadControl.DEFAULT_MIN_BUFFER_MS);

			deleteCacheWithoutEXOMedia(this);

			SharedPreferences.Editor edit = prefs.edit();

			edit.putString("id", str_id);
			edit.putString("name", str_name);
			edit.putString("password", str_password);
			edit.putString("expires", str_expires);

			edit.putString("package_id", str_package_id);
			edit.putString("package_name", str_package_name);
			edit.putString("package_price", str_package_price);
			edit.putString("package_pricestr", str_package_pricestr);
			edit.putString("package_pgname", str_package_pgname);
			edit.putString("package_description", str_package_description);

			edit.putString("regtime", str_regtime);
			edit.putString("phone", str_phone);
			edit.putString("carousel", str_carousel);
			edit.putString("isactive", str_isactive);
			edit.putString("activekey", str_activekey);
			edit.putString("email", str_email);
			edit.putString("status", str_status);
			edit.putString("ptime", str_ptime);
			edit.putString("sid", str_sid);

			edit.putString("deviceID", str_deviceId);

			edit.putInt("exoplaybackbuffer", exoplaybackbuffer);
			edit.putInt("exominbuffer", exominbuffer);

			edit.commit();
		} else
			deleteCacheWithoutEXOMedia(this);
	}

	private static void deleteCacheWithoutEXOMedia(Context context) {
		try {
			File dir = context.getCacheDir();
			deleteDir(dir, Constant.CACHE_EXO_DEFAULT_NAME);
		} catch (Exception e) {}

		try {
			File dir = context.getExternalCacheDir();
			deleteDir(dir, Constant.CACHE_EXO_DEFAULT_NAME);
		} catch (Exception e) {}
	}

	private static boolean deleteDir(File dir, String withoutFolderName) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				if (withoutFolderName != null && withoutFolderName.equalsIgnoreCase(children[i]))
					continue;
				boolean success = deleteDir(new File(dir, children[i]), withoutFolderName);
				if (!success) {
					return false;
				}
			}
			return dir.delete();
		} else if(dir!= null && dir.isFile()) {
			return dir.delete();
		} else {
			return false;
		}
	}

	/* Delete EXO Media Cache folder in cache */
	public static boolean deleteEXOMediaCache(Context context) {
		boolean success = false;
		try {
			File exoMediaCacheFolder = new File(context.getCacheDir(), Constant.CACHE_EXO_DEFAULT_NAME);
			success = deleteDir(exoMediaCacheFolder, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public static long getCacheAvailableSize(Context context) {
		File file = context.getCacheDir();
		StatFs stat = new StatFs(file.getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		return bytesAvailable;
	}
}