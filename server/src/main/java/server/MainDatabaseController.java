package server;


import commons.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.database.*;

@Controller
@RequestMapping("/")
public class MainDatabaseController {
    EventRepository eventRepository;
    ParticipantRepository participantRepository;

    ParticipantEventRepository participantEventRepository;
    ExpenseRepository expenseRepository;
    ParticipantExpenseRepository participantExpenseRepository;
    ExpenseEventRepository expenseEventRepository;
    public MainDatabaseController(EventRepository eventRepository,
                                  ParticipantRepository participantRepository,
                                  ParticipantEventRepository participantEventRepository,
                                  ExpenseRepository expenseRepository,
                                  ExpenseEventRepository expenseEventRepository,
                                  ParticipantExpenseRepository participantExpenseRepository){
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.participantEventRepository = participantEventRepository;
        this.expenseRepository = expenseRepository;
        this.participantExpenseRepository = participantExpenseRepository;
        this.expenseEventRepository = expenseEventRepository;

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
        var participant1 = new Participant("Ivan", "nove@gamil.com", "revo", "bici");
        participantRepository.save(participant1);
        eventRepository.save(event1);

        var participantEvent1 = new ParticipantEvent(event1.getEventID(),
                participant1.getParticipantID());

        System.out.println(participant1.getParticipantID());

        participantEventRepository.save(participantEvent1);

        return "in show expense";
    }
}