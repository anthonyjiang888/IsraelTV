package israel13.androidtv.Utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by puspak on 10/7/17.
 */

public class LinearlayoutManager extends LinearLayoutManager {


	public LinearlayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}


	public LinearlayoutManager(Context context) {
		super(context);
	}

	public LinearlayoutManager(Context context, int orientation, boolean
			reverseLayout) {
		super(context, orientation, reverseLayout);
	}


	@Override
	public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
		// Need to be called in order to layout new row/column
		View nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state);

		if (nextFocus == null) {
			return null;
		}

		int fromPos = getPosition(focused);
		int nextPos = fromPos;

		return findViewByPosition(nextPos);
	}

	/**
	 * Manually detect next view to focus.
	 *
	 * @param fromPos   from what position start to seek.
	 * @param direction in what direction start to seek. Your regular {@code View.FOCUS_*}.
	 * @return adapter position of next view to focus. May be equal to {@code fromPos}.
	 */
	protected int getNextViewPos(int fromPos, int direction) {
		int offset = calcOffsetToNextView(direction);

		if (hitBorder(fromPos, offset)) {
			return fromPos;
		}

		return fromPos + offset;
	}

	/**
	 * Calculates position offset.
	 *
	 * @param direction regular {@code View.FOCUS_*}.
	 * @return position offset according to {@code direction}.
	 */
	protected int calcOffsetToNextView(int direction) {
		// int spanCount = getSpanCount();
		int orientation = getOrientation();

		if (orientation == VERTICAL) {
			switch (direction) {

				case View.FOCUS_RIGHT:
					return 1;
				case View.FOCUS_LEFT:
					return -1;
			}
		} else if (orientation == HORIZONTAL) {
			switch (direction) {
				case View.FOCUS_RIGHT:
					return 1;
				case View.FOCUS_LEFT:
					return -1;
			}
		}

		return 0;
	}

	/**
	 * Checks if we hit borders.
	 *
	 * @param from   from what position.
	 * @param offset offset to new position.
	 * @return {@code true} if we hit border.
	 */
	private boolean hitBorder(int from, int offset) {
		// int spanCount = getSpanCount();
		int spanCount = 1;

		if (Math.abs(offset) == 1) {
			int spanIndex = from % spanCount;
			int newSpanIndex = spanIndex + offset;
			return newSpanIndex < 0 || newSpanIndex >= spanCount;
		} else {
			int newPos = from + offset;
			return newPos < 0 && newPos >= spanCount;
		}
	}
}


