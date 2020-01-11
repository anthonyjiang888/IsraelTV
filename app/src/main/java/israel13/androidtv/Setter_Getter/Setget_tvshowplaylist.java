package israel13.androidtv.Setter_Getter;

import com.google.android.exoplayer2.source.MediaSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by puspak on 30/8/17.
 */

public class Setget_tvshowplaylist {

public List<MediaSource> playlist = new ArrayList<>();
    String index="";

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List<MediaSource> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<MediaSource> playlist) {
        this.playlist = playlist;
    }
}
