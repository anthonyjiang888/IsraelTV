package israel13.androidtv.Others;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import israel13.androidtv.Utils.Constant;

/**
 * Created by Puspak on 14/09/16.
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

   /* public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }*/
}
