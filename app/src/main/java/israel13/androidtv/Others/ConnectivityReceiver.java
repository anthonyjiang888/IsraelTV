package israel13.androidtv.Others;

/**
 * Created by Puspak on 14/09/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import israel13.androidtv.CallBacks.ConnectionChange;

public class ConnectivityReceiver extends BroadcastReceiver {

    public  static  Context mcontext;

    ConnectionChange callbackobj;


    @Override
    public void onReceive(Context context, Intent arg1) {

        if(mcontext!=null) {
            callbackobj = (ConnectionChange) mcontext;
            callbackobj.OnNetworkChange();
        }



    }



}