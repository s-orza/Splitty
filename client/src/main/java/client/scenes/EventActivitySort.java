package client.scenes;

import commons.Event;

import java.util.Comparator;

public class EventActivitySort implements Comparator<Event> {

  @Override
  public int compare(Event e1, Event e2) {
    if(e1.getActivityDate().before(e2.getActivityDate())){
      return 1;
    }
    if(e1.getActivityDate().after(e2.getActivityDate())){
      return -1;
    }
    return 0;
  }
}
