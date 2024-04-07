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
    private final CurrencyService currencyService;

    public DebtService(EventRepository eventRepository, DebtRepository debtRepository,
                       CurrencyService currencyService) {
        this.eventRepository = eventRepository;
        this.debtRepository = debtRepository;
        this.currencyService = currencyService;
    }

    /**
     * Here we use d as a container, we don't care about d.getId().
     * We only use it to know which debt we update.(Of course, it the debt doesn't
     * exist, then we create it).
     * @param eventId id of the event
     * @param d the debt
     */
    public void saveDebtToEvent(long eventId,Debt d,String date)
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
                //convert from our d currency to the debt currency that is already there
                double rate=currencyService.getExchangeRateAndUpdateCacheFile(date,
                        d.getCurrency(),debt.getCurrency());
                money=money*rate;
                debt.setAmount(debt.getAmount() + money);
                debtRepository.save(debt);
                eventRepository.save(event);
                return;
            }
            else
            if(d.getDebtor()==debt.getCreditor() && d.getCreditor()==debt.getDebtor())
            {
                //convert from our d currency to the debt currency that is already there
                double rate=currencyService.getExchangeRateAndUpdateCacheFile(date,
                        d.getCurrency(),debt.getCurrency());
                money=money*rate;
                debt.setAmount(debt.getAmount() - money);
                if(debt.getAmount()==0)
                {
                    //settle the debt->delete it
                    event.settleDebt(debt);
                    //first we delete it from here (somewhere we have a FK)
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
                //I chose 0.005 because I think it is a good limit for a debt.
                //Don't forget that we are only using EUR,USD,RON and CHF in this app.
                //(The greatest rate is around 5.2 from CHF to RON so the smallest is around 1/5.2)
                if(debt.getAmount()<0.005)
                {
                    //this is an error from double operations, so we can delete the debt
                    //in reality you cannot pay less than 0.01 of something.
                    //settle the debt->delete it
                    event.settleDebt(debt);
                    //first we delete it from here (somewhere we have a FK)
                    eventRepository.save(event);
                    debtRepository.delete(debt);
                    return;
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

        // unlink the debt from the event
        event.settleDebt(debt);
        eventRepository.save(event);

        // verify debt was unlinked
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
