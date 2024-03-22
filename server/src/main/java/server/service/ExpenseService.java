package server.service;

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
  public ExpenseService(ExpenseRepository repo, ParticipantExpenseRepository repoPaExp, ParticipantRepository repoPa) {
    this.repo = repo;
    this.repoPaExp = repoPaExp;
    this.repoPa = repoPa;
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
    System.out.println("Participants of expense set to " + participantList);
  }

}
