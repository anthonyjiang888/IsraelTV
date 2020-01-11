package israel13.androidtv.Setter_Getter;

import java.util.ArrayList;

/**
 * Created by krishanu on 03/07/17.
 */
public class SetgetSearchRecordDates {

    String dates="";
    String channel_logo="";
    String channelname="";
    String channel_odid="";
    String isselected="false";


    public String getChannel_odid() {
        return channel_odid;
    }

    public void setChannel_odid(String channel_odid) {
        this.channel_odid = channel_odid;
    }

    public String getIsselected() {
        return isselected;
    }

    public void setIsselected(String isselected) {
        this.isselected = isselected;
    }

    public String getChannel_logo() {
        return channel_logo;
    }

    public void setChannel_logo(String channel_logo) {
        this.channel_logo = channel_logo;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    ArrayList<SetgetSearchRecordDatesDetails> setgetSearchRecordDatesDetailsArrayList = new ArrayList<>();

    public ArrayList<SetgetSearchRecordDatesDetails> getSetgetSearchRecordDatesDetailsArrayList() {
        return setgetSearchRecordDatesDetailsArrayList;
    }

    public void setSetgetSearchRecordDatesDetailsArrayList(ArrayList<SetgetSearchRecordDatesDetails> setgetSearchRecordDatesDetailsArrayList) {
        this.setgetSearchRecordDatesDetailsArrayList = setgetSearchRecordDatesDetailsArrayList;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }
}
