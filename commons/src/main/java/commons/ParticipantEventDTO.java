package commons;


/**
 * This is a data transfer object. It is used to pass on both an event
 * and an event to the server to be modelled with in the database.
 */
public class ParticipantEventDTO {
    private long participantId;
    private long eventId;

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participant) {
        this.participantId = participant;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long event) {
        this.eventId = event;
    }

    public ParticipantEventDTO(long participantId, long eventId) {
        this.participantId = participantId;
        this.eventId = eventId;
    }

    public ParticipantEventDTO() {
    }
}
