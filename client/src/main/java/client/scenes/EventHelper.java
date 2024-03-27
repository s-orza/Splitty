package client.scenes;

import java.util.Date;
import java.util.Objects;

public class EventHelper {
  long id;
  String title;
  Date creationDate;
  Date lastActivity;

  public EventHelper(long id, String title, Date creationDate, Date lastActivity) {
    this.id = id;
    this.title = title;
    this.creationDate = creationDate;
    this.lastActivity = lastActivity;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getLastActivity() {
    return lastActivity;
  }

  public void setLastActivity(Date lastActivity) {
    this.lastActivity = lastActivity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EventHelper that = (EventHelper) o;
    if (!Objects.equals(id, that.id)) return false;
    if (!Objects.equals(title, that.title)) return false;
    if (!Objects.equals(creationDate, that.creationDate)) return false;
    return Objects.equals(lastActivity, that.lastActivity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTitle(), getCreationDate(), getLastActivity());
  }

  @Override
  public String toString() {
    return id + " - " + title;
  }

}
