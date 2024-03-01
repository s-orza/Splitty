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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


import commons.Participant;


import commons.Expense;
import commons.ParticipantEventDto;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

	private static final String SERVER = "http://localhost:8080/";

	public void getQuotesTheHardWay() throws IOException, URISyntaxException {
		var url = new URI("http://localhost:8080/api/quotes").toURL();
		var is = url.openConnection().getInputStream();
		var br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
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


	public void addParticipant(Participant participant){
		ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/participant") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(participant, APPLICATION_JSON), Participant.class);
	}

	public Participant getParticipant(long participantId){
        return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/api/participant/" + participantId) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.get(Participant.class);
	}

	/**
	 * This will go and invoke the ParticipantEvent controller
	 * It will create an entry to the participant page, but also an entry
	 * in the participantEvent page connecting the participant and the event
	 * @param participant the participant - instance
	 * @param eventId the id of the event the participant is connected to
	 */
	public void addParticipantEvent(Participant participant, int eventId){
		System.out.println("In server");
		ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/participantEvent/" + eventId) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(participant, APPLICATION_JSON), Participant.class);
		System.out.println("Out server");
	}

	/**
	 * This method will add just an entry to the participant_event table
	 * @param participantEventDTO an object that contains particpantId and eventId
	 */
	public void addParticipantEvent(ParticipantEventDto participantEventDTO) {
		System.out.println("In server");
		ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("/api/participant/event/") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(participantEventDTO, APPLICATION_JSON), Participant.class);
		System.out.println("Out server");
	}
	public Expense getExpenseById(long id)
	{
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/?id="+id)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON).get()
				.readEntity(Expense.class);
		//System.out.println(response.getStatus());
		//System.out.println(response.readEntity(Expense.class));
				//.get(new GenericType<Expense>(){});
		//return response.readEntity(Expense.class);
		//System.out.println(response);
		//return null;
	}

	public boolean addExpense(Expense expense)
	{
		System.out.println("In server");
		//return;
		Response response=ClientBuilder.newClient(new ClientConfig())
				.target(SERVER+"api/expenses/s")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(expense,APPLICATION_JSON));
		System.out.println(response.readEntity(String.class));
		System.out.println(response);
		//if(response.getStatus()<300)
			return true;
		//return false;
				//.post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
		//return null;*/

	}
}