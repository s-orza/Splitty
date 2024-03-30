package server.service;

import commons.Debt;
import commons.Event;
import org.springframework.stereotype.Service;
import server.database.DebtRepository;
import server.database.EventRepository;

import java.util.Optional;

@Service
public class DebtService {
    private final EventRepository eventRepository;

    private final DebtRepository debtRepository;

    public DebtService(EventRepository eventRepository, DebtRepository debtRepository) {
        this.eventRepository = eventRepository;
        this.debtRepository = debtRepository;
    }

    /**
     * Here we use d as a container, we don't care about d.getId().
     * We only use it to know which debt we update.(Of course, it the debt doesn't
     * exist, then we create it).
     * @param eventId id of the event
     * @param d the debt
     */
    public void saveDebtToEvent(long eventId,Debt d)
    {
        Optional<Event> eventOp=eventRepository.findById(eventId);
        //We need to update the debt between 2 persons in the event with values from debt.
        //maybe change the currency here?
        double money=d.getAmount();
        if(!eventOp.isPresent())
        {
            System.out.println("the event doesn't exist");
            return;
        }
        Event event=eventOp.get();
        //Thanks to Olav! He thought this logic.
        for(Debt debt:event.getDebts())
        {
            if(d.getDebtor()==debt.getDebtor() && d.getCreditor()==debt.getCreditor())
            {
                debt.setAmount(debt.getAmount() + money);
                debtRepository.save(debt);
                eventRepository.save(event);
                return;
            }
            else
            if(d.getDebtor()==debt.getCreditor() && d.getCreditor()==debt.getDebtor())
            {
                debt.setAmount(debt.getAmount() - money);
                if(debt.getAmount()==0)
                {
                    //settle the debt->delete it
                    event.settleDebt(debt);
                    //first we delete in from here (somewhere we have a FK)
                    eventRepository.save(event);
                    debtRepository.delete(debt);
                    return;
                }
                if(debt.getAmount()<0)
                {
                    //change persons
                    long oldDebtor = debt.getDebtor();
                    debt.setDebtor(debt.getCreditor());
                    debt.setCreditor(oldDebtor);
                    debt.setAmount(Math.abs(debt.getAmount()));
                }
                //update
                debtRepository.save(debt);
                eventRepository.save(event);
                return;
            }
        }
        //if you arrived here it means that there is no debt between these 2 persons.
        //to get a debt with Id
        Debt saved=debtRepository.save(d);
        event.getDebts().add(saved);
        eventRepository.save(event);
    }

    /**
     * To be used only with debts that are not connected to events!!!
     * (They were already deleted from lists.
     * @param eventId id of the event
     * @param debt the debt er try to delete
     * @return true if we deleted it
     */
    public boolean deleteDebt(long eventId,Debt debt)
    {
        if(debt==null)
            return false;
        if(!debtRepository.existsById(debt.getDebtID()))
            return false;
        if(!eventRepository.existsById(eventId))
            return false;
        Event event=eventRepository.findById(eventId).get();
        if(event.getDebts().contains(debt))
        {
            System.out.println("The debt is still connected to the event so we cannot delete it");
            return false;
        }
        //there should not be any event that still has this. Otherwise, there would be errors.
        debtRepository.delete(debt);
        return true;
    }

}