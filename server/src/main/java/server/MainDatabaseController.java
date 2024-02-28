package server;


import commons.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.database.EventRepository;
import server.database.ParticipantEventRepository;
import server.database.ParticipantRepository;

@Controller
@RequestMapping("/")
public class MainDatabaseController {
    EventRepository eventRepository;
    ParticipantRepository participantRepository;

    ParticipantEventRepository participantEventRepository;
    public MainDatabaseController(EventRepository eventRepository,
                                  ParticipantRepository participantRepository,
                                  ParticipantEventRepository participantEventRepository){
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.participantEventRepository = participantEventRepository;
    }
//    @GetMapping("/")
//    @ResponseBody
//    public String index() {
//        Expense expense = new Expense();
//        System.out.println(expense.toString());
//        return "Hello world!";
//    }

    @GetMapping("/")
    @ResponseBody
    public String init() {
        // Use the autowired Expense bean
//        var event = new Event(123);
//        Participant a = new Participant("a", "b", "c", "d");
//        participantRepository.save(a);
//        event.getParticipants().add(a);
//        eventRepository.save(event);

        var event1 = new Event();
        var event2 = new Event();
        var participant1 = new Participant("Ivan", "nove@gamil.com", "revo", "bici");
        var participant2 = new Participant("Boro", "bara@gamil.com", "rnrn", "fdcv");
        var participant3 = new Participant("ta", "faddf@gamil.com", "ghs", "jh");
        var participant4 = new Participant("Boyaro", "dasf@avb.com", "dfas", "ghgh");

        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        eventRepository.save(event1);
        eventRepository.save(event2);


        var participantEvent1 = new ParticipantEvent(event1.getEventID(),
                participant1.getParticipantID());
        var participantEvent2 = new ParticipantEvent(event2.getEventID(),
                participant2.getParticipantID());
        var participantEvent3 = new ParticipantEvent(event1.getEventID(),
                participant3.getParticipantID());
        var participantEvent4 = new ParticipantEvent(event1.getEventID(),
                participant4.getParticipantID());

        System.out.println(participant1.getParticipantID());





        participantEventRepository.save(participantEvent1);
        participantEventRepository.save(participantEvent2);
        participantEventRepository.save(participantEvent3);
        participantEventRepository.save(participantEvent4);
        return "in show expense";
    }
}