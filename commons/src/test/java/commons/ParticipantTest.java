package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
public class ParticipantTest {
    @Test
    public void checkConstructorAndGetters() {
        Participant p = new Participant("john", "john@gmail.com",
                "DE68500105178297336485", "AAAABBCCDD");
        assertEquals("john", p.getName());
        assertEquals("john@gmail.com", p.getEmail());
        assertEquals("DE68500105178297336485", p.getIban());
        assertEquals("AAAABBCCDD", p.getBic());
    }

    @Test
    public void equalsHashCode() {
        Participant a = new Participant("a", "b", "c", "d");
        Participant b = new Participant("a", "b", "c", "d");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        Participant a = new Participant("a", "b", "c", "b");
        Participant b = new Participant("a", "c", "b", "c");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        String actual = new Participant("a", "b", "c", "d").toString();
        assertTrue(actual.contains(Participant.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("name"));
    }
}
