package trailsync.shared;

import java.io.Serializable;
import java.util.List;

public class ServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MessageType {
        ACTIVITY_UPDATE,
        SESSION_SUMMARY,
        SESSION_ENDED
    }

    private MessageType type;
    private ActivityData activityData;
    private List<ActivityData> allSessions;
    private String info;

    public ServerMessage(MessageType type) {
        this.type = type;
    }

    public MessageType getType()                         { return type; }
    public ActivityData getActivityData()                { return activityData; }
    public void setActivityData(ActivityData d)          { this.activityData = d; }
    public List<ActivityData> getAllSessions()            { return allSessions; }
    public void setAllSessions(List<ActivityData> list)  { this.allSessions = list; }
    public String getInfo()                              { return info; }
    public void setInfo(String info)                     { this.info = info; }
}