package israel13.androidtv.Adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import israel13.androidtv.R;

/**
 * Created by puspakghosh on 11/1/17.
 */
public class CountryCodeAdapter extends ArrayAdapter {

	ArrayList<String> mCodes;
	Context mContext;
	int mResourceId;

	public CountryCodeAdapter(Context context, int resourceID) {
		super(context, resourceID);

		mContext = context;
		mResourceId = resourceID;
		mCodes = new ArrayList<String>();

		updateList("");
	}

	public void updateList(String search) {
		mCodes.clear();

		String[] infos = mContext.getResources().getStringArray(R.array.CountryCodes);
		for(int i = 0;i < infos.length; i ++) {
			String code = infos[i];
			if(search.isEmpty() || code.toLowerCase().indexOf(search.toLowerCase()) != -1) {
				mCodes.add(infos[i]);
			}
		}
		notifyDataSetChanged();
	}

	public int searchPosition(String name) {
		int pos = -1;
		for(int i = 0;i < mCodes.size(); i ++) {
			String code = mCodes.get(i);
			if(code.toLowerCase().indexOf(name.toLowerCase()) != -1) {
				pos= i;  break;
			}
		}
		return pos;
	}

	public Bitmap getFlagImage(int position) {
		String name = getCountryName(position);
		name = name.replace(" ", "_");
		if (name == null)
			return null;

		Bitmap flag = null;
		try {
			AssetManager assetManager = mContext.getAssets();
			InputStream is = assetManager.open("flags/flag_" + name.toLowerCase() + ".png");
			flag = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	public String getCountryId(int position) {
		if (position >= mCodes.size())
			return null;

		String id = null;
		String info = mCodes.get(position);
		String[] info_parts = info.split(",");
		if (info_parts.length > 0) {
			id = info_parts[0];
		}

		return id;
	}

	public String getCountryCode(int position) {
		if (position >= mCodes.size())
			return null;

		String code = null;
		String info = mCodes.get(position);
		String[] info_parts = info.split(",");
		if (info_parts.length > 1) {
			code = info_parts[1];
		}
		return code;
	}

	public String getCountryName(int position) {
		if (position >= mCodes.size())
			return null;

		String name = null;
		String info = mCodes.get(position);
		String[] info_parts = info.split(",");
		if (info_parts.length > 2) {
			name = info_parts[2];
		}
		return name;
	}

	@Override
	public int getCount() {
		return mCodes.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResourceId, null);

			holder = new ViewHolder();
			holder.tv_country_code = (TextView) view.findViewById(R.id.code);
			holder.img_country_flag = (ImageView) view.findViewById(R.id.flag);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Bitmap flag = getFlagImage(position);
		if (flag != null) {
			holder.img_country_flag.setImageBitmap(flag);
		}

		String code = getCountryCode(position);
		String name = getCountryName(position);
		if(code != null && name != null) {
			holder.tv_country_code.setText(name + "  " + code);
		}

		return view;
	}

	//********* Create a holder Class to contain inflated xml file elements *********//*
	public static class ViewHolder {
		public TextView tv_country_code;
		public ImageView img_country_flag;
	}
}
