/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.database;

import commons.Expense;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT ex FROM Expense ex JOIN ExpenseEvent ev ON ev.expenseId=ex.expenseId " +
            "WHERE ev.eventId=:eventId AND ex.author = :author")
    List<Expense> findEventByAuthor(long eventId,String author);
    @Query("SELECT e FROM Expense e WHERE e.author.participantID =:authorId")
    List<Expense> findByAuthor(long authorId);
    @Query("SELECT DISTINCT ex FROM Expense ex JOIN ExpenseEvent ev ON ev.expenseId=ex.expenseId " +
            "JOIN ParticipantEvent pev ON pev.eventId=ev.eventId " +
            "JOIN Participant  p ON p.participantID=pev.participantId " +
            "WHERE ev.eventId=:eventId AND p.name=:name")
    List<Expense> findEventsThatInvolvesName(long eventId,String name);
    @Query("SELECT DISTINCT ex FROM Expense ex JOIN ExpenseEvent ev ON ev.expenseId=ex.expenseId " +
            "WHERE ev.eventId=:eventId")
    List<Expense> findAllExpOfAnEvent(long eventId);
    @Query("SELECT DISTINCT ex FROM Expense ex")
    List<Expense> findAllExp();
    @Transactional
    @Modifying
    @Query("DELETE FROM Expense ex WHERE ex.expenseId=:expenseId")
    Integer deleteWithId(long expenseId);


    @Transactional
    @Modifying
    @Query("UPDATE Expense ex SET ex.author=:authorId, ex.content=:content, ex.money=:money, " +
            "ex.currency=:currency, ex.date=:date, ex.type=:type WHERE ex.expenseId=:expenseId")
    Integer updateExpenseWithId(long expenseId, long authorId, String content, double money,
                                           String currency, String date, String type);


    @Transactional
    @Modifying
    @Query("DELETE FROM ExpenseEvent ev WHERE ev.eventId=:eventId AND ev.expenseId=:expenseId")
    Integer deleteExpenseEventCon(long eventId,long expenseId);
    @Transactional
    @Modifying
    @Query("DELETE FROM ExpenseEvent ev WHERE ev.eventId=:eventId")
    void deleteAllExpensesEventCon(long eventId);
}