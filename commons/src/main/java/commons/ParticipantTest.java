package commons;

/**
 * This is just a Dummy Test class, because i needed to populate the event page with some data
 * Later it will be discarded and the proper participant class will be used
 */
public class ParticipantTest {
    String name;

    public ParticipantTest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
