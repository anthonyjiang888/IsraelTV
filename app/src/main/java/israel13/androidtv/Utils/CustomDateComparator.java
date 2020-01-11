package israel13.androidtv.Utils;

import java.util.Comparator;

import israel13.androidtv.Setter_Getter.SetgetAllChannels;

public class CustomDateComparator {
    public static class AllChannelsDateComparator implements Comparator<SetgetAllChannels> {
        @Override
        public int compare(SetgetAllChannels lhs, SetgetAllChannels rhs) {
            Double distance = Double.valueOf(lhs.getOdid());
            Double distance1 = Double.valueOf(rhs.getOdid());
            if (distance.compareTo(distance1) < 0) {
                return -1;
            } else if (distance.compareTo(distance1) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
