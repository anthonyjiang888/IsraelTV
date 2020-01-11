package israel13.androidtv.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.LevelListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import israel13.androidtv.R;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

	Context 				mContext;
	TextView			mErrorView;
	ImageView		mCheckIcon;

	LevelListDrawable mLevels;

	Boolean mHasMark, mModified;

	int LEVEL_NORMAL, LEVEL_FOCUSED, LEVEL_ERROR;

	public CustomEditText(Context context) {
		super(context);
		init(context, null);
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mContext = context;

		LEVEL_NORMAL = mContext.getResources().getInteger(R.integer.edit_level_normal);
		LEVEL_FOCUSED = mContext.getResources().getInteger(R.integer.edit_level_focus);
		LEVEL_ERROR = mContext.getResources().getInteger(R.integer.edit_level_error);

		mLevels = (LevelListDrawable) getBackground();

		mModified = false;

		String attrHasMark = null;
		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);
			attrHasMark = a.getString(R.styleable.CustomEditText_hasMark);
			a.recycle();
		}

        mHasMark = (attrHasMark == null ? false : Boolean.valueOf(attrHasMark).booleanValue());
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

		if(mCheckIcon == null) {
			mCheckIcon = new ImageView(mContext);
			mCheckIcon.setBackground(getResources().getDrawable(R.drawable.check));
			mCheckIcon.setVisibility(INVISIBLE);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_TOP, getId());
			params.addRule(RelativeLayout.ALIGN_END, getId());
			params.setMargins(UiUtils.convertDpToPx(mContext, 15), UiUtils.convertDpToPx(mContext, 10), UiUtils.convertDpToPx(mContext, 15), UiUtils.convertDpToPx(mContext, 10));
			mCheckIcon.setLayoutParams(params);

			layout.addView(mCheckIcon);
		}
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);

		if(!focused) {
			mModified = true;
		}

		updateLevel(focused);
	}

	@Override
	public void setError(CharSequence error) {
		mLevels.setLevel(LEVEL_ERROR);

		if(error == null) {
			if(mErrorView != null) {
				mErrorView.setVisibility(GONE);
			}
		} else {
			if(mErrorView != null) {
				mErrorView.setText(error);
				mErrorView.setVisibility(VISIBLE);
			}
			if(mCheckIcon != null) {
				mCheckIcon.setVisibility(INVISIBLE);
			}
		}
	}

	public void setError(int resid) {
		setError(getContext().getResources().getString(resid));
	}

	public void setSuccess() {
		if(mErrorView != null) {
			mErrorView.setVisibility(GONE);
		}
		if(mCheckIcon != null && mHasMark) {
			mCheckIcon.setVisibility(VISIBLE);
		}
		mLevels.setLevel(hasFocus() ? LEVEL_FOCUSED : LEVEL_NORMAL);
	}

	public void updateLevel(boolean focused) {
		if (mLevels.getLevel() == LEVEL_ERROR)
			return;

		if (focused) {
			mLevels.setLevel(LEVEL_FOCUSED);
		} else {
			mLevels.setLevel(LEVEL_NORMAL);
		}
	}

	public boolean hasError() {
		return mLevels.getLevel() == LEVEL_ERROR;
	}

	public boolean isModified() {
		return mModified;
	}
}
