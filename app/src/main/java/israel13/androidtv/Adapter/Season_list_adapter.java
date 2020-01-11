package israel13.androidtv.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import israel13.androidtv.R;

/**
 * Created by puspakghosh on 11/1/17.
 */
public class Season_list_adapter extends ArrayAdapter {

	ArrayList<String> list;
	Context mContext;
	int resourceID;
	private int selectedIndex;
	private boolean showDivider = false;

	public Season_list_adapter(Context mContext, int resourceID, ArrayList<String> list, boolean showDivider) {
		super(mContext, resourceID, list);

		this.mContext = mContext;
		this.resourceID = resourceID;
		this.list = list;
		selectedIndex = -1;
		this.showDivider = showDivider;
	}

	public void setSelectedIndex(int ind) {
		selectedIndex = ind;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resourceID, null);

			holder = new ViewHolder();
			holder.season_name = (TextView) view.findViewById(R.id.season_name);
			holder.layout_main = (LinearLayout) view.findViewById(R.id.layout_main);
			holder.season_divider = view.findViewById(R.id.season_divider);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}

		if ((position + 1) % 7 == 0 && position != list.size() - 1 && showDivider) {
			holder.season_divider.setVisibility(View.VISIBLE);
		}else{
			holder.season_divider.setVisibility(View.GONE);
		}
		holder.season_name.setText(list.get(position));

		view.setPadding(20,9,20,9);
		return view;
	}

	//********* Create a holder Class to contain inflated xml file elements *********//*
	public static class ViewHolder {
		public TextView season_name;
		public LinearLayout layout_main;
		public View season_divider;
	}
}
