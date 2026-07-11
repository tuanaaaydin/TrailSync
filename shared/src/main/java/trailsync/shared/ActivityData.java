package trailsync.shared;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityData implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ActivityType {
        RUNNING, HIKING, CYCLING
    }

    private String sessionId;
    private ActivityType type;
    private double distanceKm;
    private int steps;
    private int heartRate;
    private double elevationM;
    private String timestamp;
    private String clientName;
    private boolean sessionEnd;

    public ActivityData(String sessionId, String clientName, ActivityType type) {
        this.sessionId = sessionId;
        this.clientName = clientName;
        this.type = type;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.sessionEnd = false;
    }

    public String getSessionId()         { return sessionId; }
    public String getClientName()        { return clientName; }
    public ActivityType getType()        { return type; }
    public double getDistanceKm()        { return distanceKm; }
    public void setDistanceKm(double d)  { this.distanceKm = d; }
    public int getSteps()                { return steps; }
    public void setSteps(int s)          { this.steps = s; }
    public int getHeartRate()            { return heartRate; }
    public void setHeartRate(int hr)     { this.heartRate = hr; }
    public double getElevationM()        { return elevationM; }
    public void setElevationM(double e)  { this.elevationM = e; }
    public String getTimestamp()         { return timestamp; }
    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public boolean isSessionEnd()        { return sessionEnd; }
    public void setSessionEnd(boolean b) { this.sessionEnd = b; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %.2f km | %d steps | %d bpm | %.0f m",
                timestamp, clientName, type, distanceKm, steps, heartRate, elevationM);
    }
}