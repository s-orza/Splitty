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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import commons.*;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Accessing parts of the page will happen as follows (please take keen eye on indentation):
 * 	/api - where everything happens for the front-end (for developer access check below)
 * 	(suggest to replace api with com - commons or just skip to events/{eventId}
 * 		/events/{eventId}
 * 				/participants/{participantId}
 * 					/{name}
 * 					/{email}
 * 					/{iban}
 * 					/{bic}
 * 				/expenses/{expenseId}
 * 					/expenseType
 * 						/author/{authorId} | participantsId
 * 						/content	| 'For what?' section
 * 						/participant/{participantId} | In case of splitting with whom
 * 						/date
 * 						/payment
 * 							/{currencyType}
 * 							/{amount}
 * 				/debts
 *					/{debtId}
 *						/{DebtorId} | participantId
 *						/{currencyType} | should also have a column for such
 *						/{Amount}
 * 	/dev
 * 		/events
 *			/{eventId}
 */

public class ServerUtils {

	private static final String SERVER = "http://localhost:8080/";
	public static long currentId = -1;
	private static long expenseIdToModify=-1;


	public long getCurrentId(){
		return currentId;
	}
	public void connect(long newEvent){
		currentId = newEvent;
		Event updated = getEvent(currentId);
		updated.activity();
		createEvent(updated);
		expenseIdToModify=-1;
		System.out.println("Äctivity on event " + updated.getName());
	}
	public void setExpenseToBeModified(long expenseId)
	{
		expenseIdToModify=expenseId;
	}
	public long getExIdToModify()
	{
		return expenseIdToModify;
	}
	public Expense getExpenseToBeModified()
	{
		Expense ex=getExpenseById(expenseIdToModify);
		return ex;
	}
	public double convertCurrency(String date,String from,String to,double amount)
	{
		return amount*getExchangeRate(date,from,to);
	}
	public double getExchangeRate(String date,String from,String to)
	{
		if(from.equals(to))
			return 1.0;
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/foreignCurrencies/"+date+"?from="+from+"&to="+to)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		if(response.getStatus()<300)
			return response.readEntity(Double.class);
		//there has been a problem
		return 1.0;
	}

	public void getQuotesTheHardWay() throws IOException, URISyntaxException {
		var url = new URI("http://localhost:8080/api/quotes").toURL();
		var is = url.openConnection().getInputStream();
		var br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
	//connects to the database through the endpoint to give all events
	public List<Event> getEvents() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/events")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Event>>() {});
	}
	public List<Quote> getQuotes() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
	}

	public Quote addQuote(Quote quote) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
	}

	//connects to the database through the endpoint to add event
	public void createEvent(Event event) {
		ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/events") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(event, APPLICATION_JSON), Event.class);
	}

	//connects to the database through the endpoint to delete an event
	public void deleteEvent (long id) throws Exception {
		Response response = ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/events/" + id) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.delete();
		deleteAllExpensesFromEvent(id);
		//WIll be added when we get the API
		//deleteAllParticipantEvent(id)
		if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
			System.out.println("Event removed successfully.");
		} else {
			throw new Exception("Failed to remove quote. Status code: "+ response.getStatus());
		}
		response.close();
	}

	//connects to the database through the endpoint to get participants from specific event
//	public List<Participant> getParticipants(long eventId) {
//		Response response=ClientBuilder.newClient(new ClientConfig())
//				.target(SERVER).path("api/events/" + eventId + "/participants")
//				.request(APPLICATION_JSON)
//				.accept(APPLICATION_JSON).get();
//		if(response.getStatus()<300)
//			return response.readEntity(List.class);
//		return null;
//	}


	//connects to the database through the endpoint to get an event with an id
	public Event getEvent(long id) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/events/" + id) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.get(new GenericType<Event>() {});
	}

	private static final ExecutorService EXEC2 = Executors.newSingleThreadExecutor();
	public void registerForUpdatesEvents(long eventId, Consumer<Event> consumer)
	{
		EXEC2.submit(() -> {
			while(!Thread.interrupted()) {
				var res = ClientBuilder.newClient(new ClientConfig())
						.target(SERVER+"api/events/updates")
						.request(APPLICATION_JSON)
						.accept(APPLICATION_JSON)
						.get(Response.class);
				if (res.getStatus()==204) {
					continue;
				}
				var e = res.readEntity(Event.class);
				consumer.accept(e);
			}
		});
	}

	public void stop2 () {
		EXEC2.shutdown();
	}

	//connects to the database through the endpoint to change name of an event with an id
	// needs a bit of tweaking
	public void changeEventName(long id, String newName) {
		Event event = new Event();
		event.setName(newName);

		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER)
				.path("api/events/" + id)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.put(Entity.entity(newName, APPLICATION_JSON));
	}
	 public void addDebtToEvent(long eventId,Debt debt,String date)
	 {
		 //this post is a "put" if the debt is already there
		 Response response=ClientBuilder.newClient(new ClientConfig())
				 .target(SERVER+"api/events/debts?eventId="+eventId+"&date="+date)
				 .request(APPLICATION_JSON)
				 .accept(APPLICATION_JSON)
				 .post(Entity.entity(debt, APPLICATION_JSON));

		 System.out.println(response.getStatus());
	 }

	public Participant getParticipant(long participantId){
        Response response =ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/api/participant/" + participantId) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.get();
		if(response.getStatus()<300)
		{
			GenericType<Participant> genericType = new GenericType<Participant>() {};
			return response.readEntity(genericType);
		}
		return null;
	}

	/**
	 * THIS IS CORRECT
	 * @param eventId event id
	 * @return the list of participants
	 */
	public List<Participant> getParticipantsOfEvent(long eventId){
		System.out.println("in server");
		Response response = ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("/api/participant/event/"+eventId+"/allParticipants")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		System.out.println(response);
		if(response.getStatus() < 300) {
			GenericType<List<Participant>> genericType = new GenericType<List<Participant>>() {};
			return response.readEntity(genericType);  // Use a specific type reference here
		}
		return null;
	}


	public List<Event> getEventsOfParticipant(long participantId){
		System.out.println("in servero");
		Response response = ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("/api/participant/event/getEvents/" + participantId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		System.out.println("out servero");
		if(response.getStatus() < 300) {
			GenericType<List<Event>> genericType = new GenericType<List<Event>>() {};
			return response.readEntity(genericType);  // Use a specific type reference here
		}

		return null;
	}

	public boolean deleteParticipantEvent(long eventId, long participantId) {
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/participant/event/" + eventId + "/" + participantId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).delete();

		if(response.getStatus() < 300)
			return true;
		return false;
	}


	/**
	 * This will go and invoke the ParticipantEvent controller
	 * It will create an entry to the participant page, but also an entry
	 * in the participantEvent page connecting the participant and the event
	 * @param participant the participant - instance
	 * @param eventId the id of the event the participant is connected to
	 */
	public void addParticipantEvent(Participant participant, long eventId){
		System.out.println("In server");
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("/api/participant/event/" + eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(participant,APPLICATION_JSON));
		System.out.println(response);
	}

	/**
	 * This method will add just an entry to the participant_event table
	 * @param participantEventDto an object that contains participantId and eventId
	 */
	public void addParticipantEvent(ParticipantEventDto participantEventDto) {
		System.out.println("In server");
		ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/api/participant/event/") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(participantEventDto, APPLICATION_JSON), ParticipantEventDto.class);
		System.out.println("Out server");
	}
	//EXPENSE functions
	//GET functions
	public Expense getExpenseById(long id)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/?expenseId="+id)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		if(response.getStatus()<300)
			return response.readEntity(Expense.class);
		return null;
	}
	public boolean resetDebtsFromExpense(long eventId,long expenseId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/deletedDebts?eventId="+eventId+"&expenseId="+expenseId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		System.out.println(response);
		if(response.getStatus()<300)
			return true;
		return false;
	}
	public List<Expense> getAllExpensesOfEvent(long eventId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/allFromEvent?eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		GenericType<List<Expense>> listType = new GenericType<List<Expense>>() {};
		if(response.getStatus()<300)
			return response.readEntity(listType);
		return new ArrayList<>();
	}
	public List<Expense> getAllExpensesFromXOfEvent(long eventId,long authorId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/author?eventId="+eventId+"&authorId="+authorId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		GenericType<List<Expense>> listType = new GenericType<List<Expense>>() {};
		if(response.getStatus()<300)
			return response.readEntity(listType);
		return new ArrayList<>();
	}
	public List<Expense> getAllExpensesIncludingXOfEvent(long eventId,long authorId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/participantIncluded?eventId="+eventId+
						"&authorId="+authorId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		GenericType<List<Expense>> listType = new GenericType<List<Expense>>() {};
		if(response.getStatus()<300)
			return response.readEntity(listType);
		return new ArrayList<>();
	}

	private static final int THREAD_POOL_SIZE = 10;
	private static final ExecutorService EXEC = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	public void registerForUpdatesExpenses(long eventId, Consumer<Expense> consumer)
	{
		EXEC.submit(() -> {
			while(!Thread.interrupted()) {
				var res = ClientBuilder.newClient(new ClientConfig())
						.target(SERVER+"api/expenses/allFromEvent/updates?eventId="+eventId)
						.request(APPLICATION_JSON)
						.accept(APPLICATION_JSON)
						.get(Response.class);
				if (res.getStatus()==204) {
					continue;
				}
				var e = res.readEntity(Expense.class);
				consumer.accept(e);
			}
		});
	}

	public void stop () {
		EXEC.shutdown();
	}

	public List<Expense> getAllExpensesFromDatabase()
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/all")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		GenericType<List<Expense>> listType = new GenericType<List<Expense>>() {};
		if(response.getStatus()<300)
			return response.readEntity(listType);
		return null;
	}
	public List<Tag> getAllTagsFromEvent(long eventId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/allTags?eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		List<Tag> tags=new ArrayList<>();
		GenericType<List<Tag>> listType = new GenericType<List<Tag>>() {};
		if(response.getStatus()==200)
			tags=response.readEntity(listType);
		return tags;
	}
	public Tag getTagByIdOfEvent(String tagName,long eventId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/tags?tag="+tagName.replace(" ","%20")+"&eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get();
		if(response.getStatus()==200)
		{
			Tag tag=response.readEntity(Tag.class);
			System.out.println(tag);
			return tag;
		}
		return null;
	}
	public boolean checkIfTagExists(String tagName,long eventId)
	{
		if(getTagByIdOfEvent(tagName,eventId)!=null)
			return true;
		return false;
	}
	//POST functions

	/**
	 * To use if you want to connect to an event
	 * @param eventId the id of the event
	 * @param expense the expense
	 * @return true if it was successful
	 */
	public boolean addExpenseToEvent(long eventId, Expense expense)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/saved?eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(expense,APPLICATION_JSON));
		System.out.println(response + "addExpensetoEvent response");
		if(response.getStatus()==200)
			return true;
		return false;
	}

	/**
	 * To use if you don't care about storing ExpenseEvent connection
	 * Not good for database, but good for testing
	 * @param expense the expense
	 * @return true if it was successful
	 */
	public boolean addExpense(Expense expense)
	{
		System.out.println("In server");
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/s")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(expense,APPLICATION_JSON));
		System.out.println(response.readEntity(String.class));
		System.out.println(response);
		if(response.getStatus()<300)
			return true;
		return false;
	}
	public boolean addTag(Tag tag)
	{
		System.out.println(tag);
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/tags")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(tag,APPLICATION_JSON));
		System.out.println(response.readEntity(String.class));
		System.out.println(response);
		if(response.getStatus()==200)
			return true;
		return false;
	}

	private StompSession session = connectWebSocket("ws://localhost:8080/websocket");

	/**
	 * Stomp session is the connection to the websocket with an ip and port. To get to this session
	 * a connectWebSocket method is done. Which makes a websocket client that can connect to the websocket.
	 * And also a websocket stomp client that can communicate in stomp which can convert the messages
	 * with jackson as the message converter that can serialise and deserialize by itself.
	 * There is also a stomp session handler that makes this connection possible.
	 * @param url
	 * @return
	 */
	private StompSession connectWebSocket(String url) {
		var client = new StandardWebSocketClient();
		var stomp = new WebSocketStompClient(client);
		stomp.setMessageConverter(new MappingJackson2MessageConverter());
		try {
			return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			throw new RuntimeException();
		}
		throw new IllegalStateException();
	}
		public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
			session.subscribe(dest, new StompFrameHandler() {
				@Override
				public Type getPayloadType(StompHeaders headers) {
					return type;
				}

				@Override
				public void handleFrame(StompHeaders headers, Object payload) {
					consumer.accept((T) payload);
				}
			});
		}

	public void sendTag(String dest, Object o) {
		session.send(dest, o);
	}

	public void sendExpense(String dest, Expense o) {
		session.send(dest, o);
	}

	public void sendRemoveExpense(String dest, Expense o) {
		session.send(dest, o);
	}

	public void sendParticipant(String dest, Participant o) {
		session.send(dest, o);
	}


	public void sendEventName(String dest, String o) {
		session.send(dest, o);
	}

	public void sendEvent(String dest, Long o) {
		session.send(dest, o);
	}

	//PUT functions (update)
	public Expense updateExpense(long expenseId,Expense expense)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/?expenseId="+expenseId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).put(Entity.entity(expense,APPLICATION_JSON));
		System.out.println(response + "Update expense response");
		if(response.getStatus()!=200)
			return null;
		return response.readEntity(Expense.class);

	}
	public boolean updateTag(String tagName,long eventId,Tag newtag)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/tags?tagName="+tagName.replace(" ","%20")
						+"&eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.put(Entity.entity(newtag,APPLICATION_JSON));
		System.out.println(response.readEntity(String.class));
		System.out.println(response);
		if(response.getStatus()==200)
			return true;
		return false;
	}
	//DELETE functions
	public boolean deleteExpenseFromEvent(long eventId, long expenseId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/?eventId="+eventId+"&expenseId="+expenseId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).delete();
		//200->successful
		//444->event-expense connection not found
		//417->expense not found
		if(response.getStatus()==200)
			return true;
		return false;
	}
	public Integer deleteAllExpensesFromEvent(long eventId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/allFromEvent?eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).delete();
		if(response.getStatus()==200)
			return response.readEntity(Integer.class);
		return -1;
	}

	/**
	 * This function deletes a tag from the event, but this is tricky because we need
	 * to change all expenses that contains that tag. I changed their tags to "other"
	 * @param tagName the name of the tag
	 * @param eventId the event
	 * @return true if the tag was successfully deleted
	 */
	public boolean deleteTagFromEvent(String tagName,long eventId)
	{
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/tags?tagName="+tagName.replace(" ","%20")
						+"&eventId="+eventId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).delete();
		if(response.getStatus()==200)
			return true;
		return false;
	}

	/**
	 * connects to the database through the endpoint to add password
	 * @param password password to be added
	 */
	public void addPassword(Password password) {
		ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/password") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(password, APPLICATION_JSON), Password.class);
		System.out.println("Password added successfully.");
	}

	/**
	 * connects to the database through the endpoint to get password
	 */
	public Password getPass() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/password") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.get(new GenericType<Password>() {});
	}

	/**
	 * connects to the database through the endpoint to delete an event
	 * @param id ID of the password
	 * @throws Exception If password could not be removed
	 */
	public void deletePass (long id) throws Exception {
		Response response = ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/password/" + id) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.delete();
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			System.out.println("Password removed successfully.");
		} else {
			throw new Exception("Failed to remove password. Status code: " + response.getStatus());
		}
		response.close();
	}

	/**
	 * connects to the database through the endpoint to delete an event
	 * @throws Exception If password could not be removed
	 */
	public void deleteAllPass () throws Exception {
		Response response = ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/password/") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.delete();
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			System.out.println("Password/s removed successfully.");
		} else {
			throw new Exception("Failed to remove password/s. Status code: " + response.getStatus());
		}
		response.close();
	}
}