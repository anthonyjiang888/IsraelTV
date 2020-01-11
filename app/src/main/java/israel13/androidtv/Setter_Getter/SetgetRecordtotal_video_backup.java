package israel13.androidtv.Setter_Getter;

import com.google.android.exoplayer2.source.MediaSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by puspak on 14/8/17.
 */

public class SetgetRecordtotal_video_backup {

   String fall_back_index="";
    ArrayList<SetgetLoadScheduleRecord> fallback_details=new ArrayList<>();
    List<MediaSource> playlistItems=new ArrayList<>();


    public List<MediaSource> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(List<MediaSource> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public String getFall_back_index() {
        return fall_back_index;
    }

    public void setFall_back_index(String fall_back_index) {
        this.fall_back_index = fall_back_index;
    }

    public ArrayList<SetgetLoadScheduleRecord> getFallback_details() {
        return fallback_details;
    }

    public void setFallback_details(ArrayList<SetgetLoadScheduleRecord> fallback_details) {
        this.fallback_details = fallback_details;
    }
}
