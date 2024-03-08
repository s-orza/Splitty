package client.scenes;

import java.util.Comparator;

public class EventActivitySort implements Comparator<EventHelper> {

  @Override
  public int compare(EventHelper e1, EventHelper e2) {
    if(e1.getLastActivity().before(e2.getLastActivity())){
      return -1;
    }
    if(e1.getLastActivity().after(e2.getLastActivity())){
      return 1;
    }
    return 0;
  }
}
