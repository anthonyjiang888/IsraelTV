package israel13.androidtv.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import israel13.androidtv.R;

/**
 * Created by G on 2/2/2018.
 */

public class ImageCacheUtil {

    static private ImageCacheUtil mInstance = null;
    static private Picasso mPicasso = null;

    static public ImageCacheUtil with(Context context) {
        if (mInstance == null) {
            mInstance = new ImageCacheUtil(context);

            if (mPicasso == null) {
                mPicasso = new Picasso.Builder(context)
                        .downloader(new OkHttp3Downloader(context))
                        .build();
                mPicasso.setIndicatorsEnabled(true);
            }
        }
        return mInstance;
    }

    Context mContext;
    String mImageUrl;
    int mWidth = 0;
    int mHeight = 0;
    boolean mMemoryCacheUsage = true;
    boolean mDiskCacheUsage = true;
    int mPlaceholder = 0;
    // Class Members
    ImageCacheUtil(Context context) {
        mContext = context;
    }

    public ImageCacheUtil load(String url) {
        mImageUrl = url;
        return this;
    }

    public ImageCacheUtil cacheUsage(boolean memCache, boolean diskCache) {
        mMemoryCacheUsage = memCache;
        mDiskCacheUsage = diskCache;
        return this;
    }

    public ImageCacheUtil resize(int width, int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    public ImageCacheUtil placeholder(int resource) {
        mPlaceholder = resource;
        return this;
    }

    public boolean into(final ImageView target) {
        final String imageUrl = Uri.encode(mImageUrl, ":/=&?");

        mPlaceholder = (mPlaceholder == 0) ? R.drawable.channel_placeholder : mPlaceholder;
        mWidth = (mWidth == 0) ? 200 : mWidth;
        mHeight = (mHeight == 0) ? 200 : mHeight;

        mPicasso.load(imageUrl)
                .placeholder(mPlaceholder)
                .resize(mWidth, mHeight)
                .into(target);

        return true;
    }
}
