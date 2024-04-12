package commons;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TagId implements Serializable {
    private String name;
    private long eventId;

    public TagId(String name, long eventId) {
        this.name = name;
        this.eventId = eventId;
    }

    public TagId() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "TagId{" +
                "name='"+name+"', eventId="+eventId +'}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagId tagId = (TagId) o;
        return getEventId() == tagId.getEventId() && Objects.equals(getName(), tagId.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEventId());
    }
}
