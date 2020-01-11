package israel13.androidtv.Setter_Getter;

import java.util.ArrayList;

/**
 * Created by krishanu on 17/05/17.
 */
public class SetgetAllChannels {


    String channelsid="",gname="",egname="",logo="",odid="",isradio="";

    ArrayList<SetgetSubchannels> subchannelsList = new ArrayList<>();

    public ArrayList<SetgetSubchannels> getSubchannelsList() {
        return subchannelsList;
    }

    public void setSubchannelsList(ArrayList<SetgetSubchannels> subchannelsList) {
        this.subchannelsList = subchannelsList;
    }

    public String getChannelsid() {
        return channelsid;
    }

    public void setChannelsid(String channelsid) {
        this.channelsid = channelsid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getEgname() {
        return egname;
    }

    public void setEgname(String egname) {
        this.egname = egname;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getOdid() {
        return odid;
    }

    public void setOdid(String odid) {
        this.odid = odid;
    }

    public String getIsradio() {
        return isradio;
    }

    public void setIsradio(String isradio) {
        this.isradio = isradio;
    }
}
