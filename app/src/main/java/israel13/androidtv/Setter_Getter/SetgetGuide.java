package israel13.androidtv.Setter_Getter;

import java.util.Date;

public class SetgetGuide {
        private String name;
        private String  description;
        private String startTime;
        private int timeLength;
        private String genre;
        private String year;
        private int star;
        private long rDateTime;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public String getStartTime() {
                return startTime;
        }

        public void setStartTime(String startTime) {
                this.startTime = startTime;
        }

        public int getTimeLength() {
                return timeLength;
        }

        public void setTimeLength(int timeLength) {
                this.timeLength = timeLength;
        }

        public String getGenre() {
                return genre;
        }

        public void setGenre(String genre) {
                if (genre == null || genre.isEmpty()) {
                        this.genre = "מידע אינו זמין";
                } else {
                        this.genre = genre;
                }
        }

        public String getYear() {
                return year;
        }

        public void setYear(String year) {
                this.year = year;
        }

        public int getStar() {
                return star;
        }

        public void setStar(int star) {
                this.star = star;
        }

        public long getRDateTime() {
                return rDateTime;
        }

        public void setRDateTime(long rDateTime) {
                this.rDateTime = rDateTime;
        }
}
