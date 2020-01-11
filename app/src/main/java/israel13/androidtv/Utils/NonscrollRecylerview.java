package israel13.androidtv.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by puspak on 12/7/17.
 */

public class NonscrollRecylerview extends RecyclerView {

	public NonscrollRecylerview(Context context) {
		super(context);
	}

	public NonscrollRecylerview(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public NonscrollRecylerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = getMeasuredHeight();
	}

	@Override
	public View focusSearch(View focused, int direction) {
		if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
			LayoutManager layoutManager = getLayoutManager();
			if (layoutManager instanceof LinearLayoutManager && ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL ||
					layoutManager instanceof RtlGridLayoutManager && ((RtlGridLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
				int nextPos = getNextPos(focused, direction);
				if (nextPos == -1) {
					int next_focus_id = (int) NO_ID;
					if(direction == View.FOCUS_LEFT) {
						next_focus_id = getNextFocusLeftId();
					}
					else if(direction == View.FOCUS_RIGHT) {
						next_focus_id = getNextFocusRightId();
					}
					if(next_focus_id != NO_ID) {
						return ((Activity)getContext()).findViewById(next_focus_id);
					}
					return null;
				}
			}
		}
		return super.focusSearch(focused, direction);
	}

	public int getNextPos(View focused, int direction) {

		int cur_pos = getChildLayoutPosition(focused), nextPos = -1, dx = 0;
		int child_count = getAdapter().getItemCount(), column_count, left_end_pos = 0, right_end_pos = 0;

		boolean isReverseLayout = false, isLayoutRTL = false;

		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager instanceof RtlGridLayoutManager) {
			RtlGridLayoutManager gridLayoutManager = (RtlGridLayoutManager) layoutManager;

			column_count = gridLayoutManager.getSpanCount();
			isReverseLayout = gridLayoutManager.getReverseLayout();
			isLayoutRTL = gridLayoutManager.isLayoutRTL();

			if (!isLayoutRTL) {
				if (!isReverseLayout) {  // 00
					for (int i = 0; i < child_count; i += column_count) {
						left_end_pos = i;
						right_end_pos = i + column_count - 1;
						if (left_end_pos <= cur_pos && cur_pos <= right_end_pos)
							break;
					}
					dx = 1;
					if (right_end_pos > child_count - 1) {
						right_end_pos = child_count - 1;
					}
				} else {  //01
					for (int i = child_count - 1; i >= 0; i -= column_count) {
						left_end_pos = i;
						right_end_pos = i - column_count + 1;
						if (left_end_pos >= cur_pos && cur_pos >= right_end_pos)
							break;
					}
					dx = -1;
					if (right_end_pos < 0) {
						right_end_pos = 0;
					}
				}
			} else {
				if (!isReverseLayout) { //10
					for (int i = 0; i < child_count; i += column_count) {
						left_end_pos = i + column_count - 1;
						right_end_pos = i;
						if (left_end_pos >= cur_pos && cur_pos >= right_end_pos)
							break;
					}
					dx = -1;
					if (left_end_pos > child_count - 1) {
						left_end_pos = child_count - 1;
					}
				} else { //11
					for (int i = child_count - 1; i >= 0; i -= column_count) {
						left_end_pos = i - column_count + 1;
						right_end_pos = i;
						if (left_end_pos <= cur_pos && cur_pos <= right_end_pos)
							break;
					}
					dx = 1;
					if (left_end_pos < 0) {
						left_end_pos = 0;
					}
				}
			}

			if (direction == View.FOCUS_LEFT) {
				nextPos = cur_pos - dx;
			} else if (direction == View.FOCUS_RIGHT) {
				nextPos = cur_pos + dx;
			}
		}
		else if (layoutManager instanceof LinearLayoutManager) {
			LinearLayoutManager linearlayoutManager = (LinearLayoutManager) layoutManager;

			isReverseLayout = linearlayoutManager.getReverseLayout();
			isLayoutRTL = (linearlayoutManager.getLayoutDirection() == LAYOUT_DIRECTION_RTL);

			if (!isLayoutRTL && !isReverseLayout || isLayoutRTL && isReverseLayout) {
				dx = 1;
				left_end_pos = 0;
				right_end_pos = child_count - 1;
			} else {
				dx = -1;
				left_end_pos = child_count - 1;
				right_end_pos = 0;
			}

			if (direction == View.FOCUS_LEFT) {
				nextPos = cur_pos - dx;
			} else if (direction == View.FOCUS_RIGHT) {
				nextPos = cur_pos + dx;
			}
		}

//		Log.d("nonscroll", String.format("%s, %s, %d, %d, %d, %d, %d, %d",
//				Boolean.valueOf(isLayoutRTL).toString(), Boolean.valueOf(isReverseLayout).toString(),
//				child_count, left_end_pos, right_end_pos, cur_pos, nextPos, dx));

		if (dx > 0) {
			if (nextPos < left_end_pos || right_end_pos < nextPos) {
				nextPos = -1;
			}
		} else {
			if (nextPos > left_end_pos || right_end_pos > nextPos) {
				nextPos = -1;
			}
		}

		return nextPos;
	}
}
