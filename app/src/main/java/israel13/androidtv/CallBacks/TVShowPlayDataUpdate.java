package israel13.androidtv.CallBacks;

public interface TVShowPlayDataUpdate {
    boolean update();
    boolean isLastEpisode();
    String[] nextEpisodeSummary();
}
