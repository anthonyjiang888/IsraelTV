package israel13.androidtv.Utils;

import android.content.Context;
import android.graphics.drawable.LevelListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import israel13.androidtv.R;

public class CustomTextView extends TextView {

	Context 				mContext;
	TextView			mErrorView;

	int 						mState;
	int LEVEL_NORMAL, LEVEL_FOCUSED, LEVEL_ERROR;

	public CustomTextView(Context context) {
		super(context);
		init(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		ViewGroup layout = (ViewGroup)getParent();
		if(!(layout instanceof RelativeLayout))
			return;

		if(mErrorView == null) {
			mErrorView = new TextView(mContext);
			mErrorView.setTextColor(mContext.getResources().getColor(R.color.edittext_error_font_color));
			mErrorView.setGravity(Gravity.RIGHT);
			mErrorView.setVisibility(GONE);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, getId());
			params.addRule(RelativeLayout.ALIGN_START, getId());
			params.addRule(RelativeLayout.ALIGN_END, getId());
			mErrorView.setLayoutParams(params);

			layout.addView(mErrorView);
		}
	}

	@Override
	public void setError(CharSequence error) {
		if(mErrorView == null)
			return;

		if(error == null) {
			mErrorView.setVisibility(GONE);
		} else {
			mErrorView.setText(error);
			mErrorView.setVisibility(VISIBLE);
		}
	}

	public void setError(int resid) {
		setError(getResources().getString(resid));
	}

	public void setSuccess() {
		mErrorView.setVisibility(GONE);
	}
}
