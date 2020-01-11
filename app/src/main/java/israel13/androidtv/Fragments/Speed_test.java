package israel13.androidtv.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.R;

/**
 * Created by puspak on 7/11/17.
 */

public class Speed_test extends Fragment {

    WebView wv_speedtest;
    ProgressBar progressBar;
    SharedPreferences logindetails ;
    TextView ProgressTextView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.activity_speedtest,null);

        wv_speedtest=(WebView)layout.findViewById(R.id.wv_speedtest);

        wv_speedtest.getSettings().setJavaScriptEnabled(true);
        wv_speedtest.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        ProgressTextView = (TextView)layout.findViewById(R.id.progresstextview);
        logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressbar);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        //String html = "<iframe width="+width+";height="+height+";style=\"border: 1px solid #cccccc;\" src=\"http://israel.tv/he/speedtest_mobile?sid="+logindetails.getString("sid","")+"></iframe>";
       // String html = "<iframe width="+width+"; height="+height+"; style=border: 1px solid #cccccc;  src=http://israel.tv/he/speedtest_mobile?sid="+logindetails.getString("sid","")+"></iframe>";
        String html = "http://israel.tv/he/speedtest_tv?sid="+logindetails.getString("sid","");
       // wv_speedtest.loadData(html, "text/html", null);
        wv_speedtest.loadUrl(html);
        ProgressTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wv_speedtest.setWebViewClient(new WebViewClient() {

            // when finish loading page
            public void onPageFinished(WebView view, String url) {
                ProgressTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                wv_speedtest.requestFocus();
            }
        });
        return layout;
    }

    @Override
    public void onResume() {
        getView().setFocusableInTouchMode(false);
        getView().requestFocus();
        super.onResume();
    }
}
