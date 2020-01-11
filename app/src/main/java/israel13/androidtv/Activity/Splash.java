package israel13.androidtv.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import israel13.androidtv.R;

public class Splash extends Activity {

	private SharedPreferences logindetails;
	boolean isRestart( ) {
		SharedPreferences rememberState = getSharedPreferences("remember", LoginActivity.MODE_PRIVATE);
		if (rememberState.getString("Where", "").equals("")) {
			return false;
		}
		return true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		clearState();
		if (!isTaskRoot()
				&& getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
				&& getIntent().getAction() != null
				&& getIntent().getAction().equals(Intent.ACTION_MAIN)) {

			finish();
			return;
		}

		setContentView(R.layout.activity_splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		logindetails = this.getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
		if (!isRestart())
			new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
					if (logindetails.getString("sid", "").equalsIgnoreCase("")) {
						Intent i = new Intent(Splash.this, LoginActivity.class);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						startActivity(i);
					} else {
						Intent i = new Intent(Splash.this, HomeActivity.class);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						startActivity(i);
					}
					Splash.this.finish();
				}
			}, 3000);
		else{
			Intent i = new Intent(Splash.this, HomeActivity.class);
			startActivity(i);
		}

	}
}
