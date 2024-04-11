package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantEventTest {

    @Test
    void testNotNull()
    {
        ParticipantEvent participantEvent =new ParticipantEvent();
        assertNotNull(participantEvent);
    }
    @Test
    void getTest() {
        ParticipantEvent participantEvent=new ParticipantEvent(1,2);
        participantEvent.setId(0);
        assertEquals(0,participantEvent.getId());
        assertEquals(1,participantEvent.getEventId());
        assertEquals(2,participantEvent.getParticipantId());
    }

    @Test
    void setTests() {
        ParticipantEvent participantEvent=new ParticipantEvent(1,2);
        participantEvent.setId(1);
        participantEvent.setParticipantId(3);
        participantEvent.setEventId(4);
        assertEquals(1,participantEvent.getId());
        assertEquals(3,participantEvent.getParticipantId());
        assertEquals(4,participantEvent.getEventId());
    }
}