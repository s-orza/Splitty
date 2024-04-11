package server.service;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Optional<Participant> findById(Long id) {
        return participantRepository.findById(id);
    }
    public List<Participant> findAllParticipants() {
        return participantRepository.findAll();
    }

    public void deleteById(Long id) {
        participantRepository.deleteById(id);
    }
}
