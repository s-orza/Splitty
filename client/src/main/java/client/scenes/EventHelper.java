package client.scenes;

import java.util.Date;
import java.util.Objects;

public class EventHelper {
  String title;
  Date creationDate;
  Date lastActivity;

  public EventHelper(String title, Date creationDate, Date lastActivity) {
    this.title = title;
    this.creationDate = creationDate;
    this.lastActivity = lastActivity;
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

    if (!Objects.equals(title, that.title)) return false;
    if (!Objects.equals(creationDate, that.creationDate)) return false;
    return Objects.equals(lastActivity, that.lastActivity);
  }

  @Override
  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
    result = 31 * result + (lastActivity != null ? lastActivity.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return title + " creationDate " + creationDate +
            ", lastActivity=" + lastActivity;
  }
}
