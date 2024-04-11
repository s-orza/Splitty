package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testNotNull()
    {
        Participant p =new Participant();
        assertNotNull(p);
    }
    @Test
    void testSet1()
    {
        Participant p=new Participant("a", "b", "c", "d");
        p.setParticipantID(1);
        p.setName("Matei");
        p.setEmail("a@ddd");
        p.setIban("1234");
        p.setBic("abcd");
        assertEquals(1,p.getParticipantID());
        assertEquals("Matei",p.getName());
        assertEquals("a@ddd",p.getEmail());
        assertEquals("1234",p.getIban());
        assertEquals("abcd",p.getBic());

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
        assertTrue(actual.contains("a"));
    }
}
