import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Event implements Serializable {

    private static final long serialVersionUID = 1L; // Use a fixed value
    private EventType eventType;
    private float value; // double
    private ZonedDateTime time; //zone date time

    public Event(EventType eventType, float value, ZonedDateTime time) {
        this.eventType = eventType;
        this.value = value;
        this.time = time;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventType=" + eventType +
                ", value='" + value + '\'' +
                ", time=" + time +
                '}';
    }
}
