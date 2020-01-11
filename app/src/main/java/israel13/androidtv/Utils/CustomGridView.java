package israel13.androidtv.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.print.PrintAttributes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;

import israel13.androidtv.Activity.HomeActivity;

public class CustomGridView extends GridView {
        public Context mContext;
        public int nSelectedIndex = 0;
        public int nSelectedTop = 0;
        public int nRowCellCount = 5;

        public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                init(attrs, context);
        }

        public CustomGridView(Context context, AttributeSet attrs) {
                super(context, attrs);
                init(attrs, context);
        }

        public CustomGridView(Context context) {
                super(context);
                init(null, context);
        }

        @Override
        public void onGlobalLayout() {
                super.onGlobalLayout();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public  void setSelectedIndex(int index) {
                nSelectedIndex = index;
        }

        public int getSelectedIndex() {
                return nSelectedIndex;
        }

        public void setSelectedTop(int top) {
                nSelectedTop = top;
        }

        public int getSelectedTop() {
                return nSelectedTop;
        }

        private void init(AttributeSet attrs, final Context context) {
                mContext = context;

                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                                Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
                                DisplayMetrics outMetrics = new DisplayMetrics ();
                                display.getMetrics(outMetrics);
                                float density  = outMetrics.density;
                                int width = getWidth();
                                nRowCellCount = (int)(width/355);

                                if (Build.VERSION.SDK_INT < 21) {
                                        int horizonSpacing = (int) (10 * density);
                                        setNumColumns(nRowCellCount);
                                        setHorizontalSpacing(horizonSpacing * (-1));
                                        setStretchMode(GridView.NO_STRETCH);
                                        setColumnWidth((int) ((width - (nRowCellCount) * horizonSpacing) * 1.0 / nRowCellCount));
                                } else {
                                        int horizonSpacing = (int) (10 * density);
                                        setNumColumns(nRowCellCount);
                                        setHorizontalSpacing(horizonSpacing);
                                        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                                }
                                requestLayout();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        //setSelectionFromTop(0, nSelectedTop);
                                        setSelection(nSelectedIndex);
                                } else {
                                        setSelection(nSelectedIndex);
                                }
                                //smoothScrollToPositionFromTop(nSelectedIndex, nSelectedTop, 0);
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                });
        }


        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (android.os.Build.VERSION.SDK_INT < 23) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                                return super.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event);
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                return super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, event);
                        }
                }
                return super.onKeyDown(keyCode, event);
        }
}
