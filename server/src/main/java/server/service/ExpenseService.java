package server.service;

import commons.Debt;
import commons.Expense;
import commons.Participant;
import org.springframework.stereotype.Service;
import server.database.ExpenseRepository;
import server.database.ParticipantExpenseRepository;
import server.database.ParticipantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

  private final ExpenseRepository repo;
  private final ParticipantExpenseRepository repoPaExp;
  private final ParticipantRepository repoPa;
  private final DebtService debtService;
  public ExpenseService(ExpenseRepository repo, ParticipantExpenseRepository repoPaExp,
                        ParticipantRepository repoPa, DebtService debtService) {
    this.repo = repo;
    this.repoPaExp = repoPaExp;
    this.repoPa = repoPa;
    this.debtService = debtService;
  }

  public void putParticipants(Expense expense)
  {
    List<Participant> participantList;
    //this line we need to update in future when we would have the APIs for participants
    List<Long> particpantsIds=repoPaExp.getAllParticipantsIdFromExpense(expense.getExpenseId());

    participantList=new ArrayList<>();
    for (Long id : particpantsIds){
      Optional<Participant> newPar = repoPa.findById(id);
      newPar.ifPresent(participantList::add);
    }

    expense.setParticipants(participantList);
    //System.out.println("Participants of expense set to " + participantList);
  }

  /**
   * This function restore the debts as they were before creating this expense.
   * @param expense the expense
   * @param eventId the id of the event
   */
  public void resetDebtsFromThisExpense(Expense expense,long eventId)
  {
    Participant author=expense.getAuthor();
    List<Participant> participants=expense.getParticipants();
    if(participants.isEmpty())
      return;
    double split=expense.getMoney()/participants.size();
    //if the author was a participant he needs to give (n-1)*split
    if(participants.contains(author))
    {
      for(Participant p:expense.getParticipants())
      {
        if(p.getParticipantID()!=expense.getAuthor().getParticipantID())
        {
          //we revert the amount that should have been given
          debtService.saveDebtToEvent(eventId,new Debt(split,
                  expense.getCurrency(),expense.getAuthor().getParticipantID(),p.getParticipantID()),expense.getDate());
        }
      }
    }
    //else: the author needs to give n*split (he gives the money back)
    else
    {
      for(Participant p:expense.getParticipants())
      {
          //we revert the amount that should have been given
          debtService.saveDebtToEvent(eventId,new Debt(split,
                  expense.getCurrency(),expense.getAuthor().getParticipantID(),p.getParticipantID()),expense.getDate());
      }
    }
  }

}
