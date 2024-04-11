package server.service;

import commons.Debt;
import commons.Expense;
import commons.Participant;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExpenseService {

  private final DebtService debtService;
  public ExpenseService(DebtService debtService) {
    this.debtService = debtService;
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
    if(participants==null || participants.isEmpty())
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
