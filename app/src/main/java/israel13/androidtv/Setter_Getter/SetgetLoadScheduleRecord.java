package israel13.androidtv.Setter_Getter;

/**
 * Created by suvadip on 23/5/17.
 */
public class SetgetLoadScheduleRecord {

    String channel="",rdatetime="",time="",name="",wday="",genre="",rdate="",lengthtime="",
            weekno="",isinfav="",star="",startotal="",isradio="",ishd="",logo="",description="",showpic="",playurl="",item_no="",year="";


    public String getItem_no() {
        return item_no;
    }

    public void setItem_no(String item_no) {
        this.item_no = item_no;
    }

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    public String getShowpic() {
        return showpic;
    }

    public void setShowpic(String showpic) {
        this.showpic = showpic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && !description.isEmpty()) {
            description = description.trim();
        }
        this.description = description;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRdatetime() {
        return rdatetime;
    }

    public void setRdatetime(String rdatetime) {
        this.rdatetime = rdatetime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWday() {
        return wday;
    }

    public void setWday(String wday) {
        this.wday = wday;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }

    public String getLengthtime() {
        return lengthtime;
    }

    public void setLengthtime(String lengthtime) {
        this.lengthtime = lengthtime;
    }

    public String getWeekno() {
        return weekno;
    }

    public void setWeekno(String weekno) {
        this.weekno = weekno;
    }

    public String getIsinfav() {
        return isinfav;
    }

    public void setIsinfav(String isinfav) {
        this.isinfav = isinfav;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getStartotal() {
        return startotal;
    }

    public void setStartotal(String startotal) {
        this.startotal = startotal;
    }

    public String getIsradio() {
        return isradio;
    }

    public void setIsradio(String isradio) {
        this.isradio = isradio;
    }

    public String getIshd() {
        return ishd;
    }

    public void setIshd(String ishd) {
        this.ishd = ishd;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}
