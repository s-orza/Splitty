package commons;

import jakarta.persistence.*;

@Entity
@Table(name = "participant_event")
public class ParticipantEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "event_id")
    private long eventId;

    @Column(name = "participant_id")
    private long participantId;

    public ParticipantEvent(long eventId, long participantId) {
        this.eventId = eventId;
        this.participantId = participantId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }
}
