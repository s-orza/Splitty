# OOPP Template Project

This repository contains the template for the OOPP project. Please extend this README.md with instructions on how to run your project.

For adding a tag:
-
In the add/edit page you can add tags with a name and a color. After you click the "+" button,
the tag will appear in the list. You can edit tags in the page with statistic.
**You cannot delete** the tag with the name **"other"** or to change its name, but you can change its color.

For foreign currencies:
-
- We created our fake exchange rates API converter that uses the hashcode of a date as a seed for a Random to create random exchange rates. (In this way we make sure that our API gives the same rate if you call it twice with the same parameters). It also ensures that rate(A, B)=1/rate(B, A). The rates are based on real exchange rates from 04-04-2024 but with a  +/-10% adjustment.
- Our application interacts with this API as if it is a real one taken from the internet. We created a local cache file in the server (stored in the server/build/resources) that caches the exchange rates that it gets from the API. When the rate from A/B is asked for a specific date, after we obtain it we cache it together with the rate B/A for that day. In this way, we increase the CACHE HIT ratio and we reduce the calls for this API.
- Fun fact: our API can give exchange rates for any date, so it can "predict" rates from the future.


For keyboard shortcuts:
-
- All functions are accessible with keyboard shortcuts. You move around all pages with left, right, up and down.
- In a table you go up and down through the values and right and left to exit it.
- You can even use tab to go through the page. If you click enter it fires the function of what you are focused on.
- Escape key on all pages goes back to the previous page.

Undo button:
-
- The undo button is kept on the add expense page, it works for all its fields. We decided to not have it for the checkboxes as it feels 
counter-intuitive for the user. You can use control z, to also make it work

Main page:
-
- Double-clicking on events allow you to join them.

Admin page:
-
- You need a password that is output onto the terminal when you run the server to open it.
- You can generate a new random password in bottom left.
- Double-clicking on an event makes you open it.
- Exporting an event creates a json in a default repository.
- Importing an event won't be possible if event with the same name and id exists.
- Importing doesn't keep the same id since we generate it automatically when creating an event.

Config:
- 
- One config is shared among server and client just for an easy use.
- Server runs on the ip and port provided in the config, if nothing is provided it will use localhost:8080.
- Client opens on the server page with ip and port from config file, but those values can be adjusted.
- Config is a json file.
- If config contains empty values we use a basic value for that field (email and password are an exception to that).
- When closing the app, information of the current language, currency, ip, port, email, password and recent events are fetched and saved.
- We store recent events in the config (the ones you see on the main page), as those are the events you interacted with (created, joined, imported or edited with admin page).

Email:
-
- Email is stored in the config and can be changed there.
- There is a button on the main page to test the email.
- All buttons using email will be greyed out if the email is incorrectly set. But **can** be pressed which will check the current email in the config and possibly let you use them if you corrected an email.
- Email **can** be adjusted during runtime of the app.
- The app checks the email that is in the config file in the moment of pressing the button and uses that mail to send the message.
- We have set up an email for the app 33splitty@gmail.com, that is normally used, but it can be changed.

Long polling was used for:
-
- Creating a new event
- Creating an expense

Websockets were used for:
-
- Deleting an expense
- Adding tags
- Adding participants
- Editing the event name
- Deleting an event

Dependency Injection is used for:
-
- Service - controller - repository communication using Spring annotations in the server
- Constructor injection for the client controllers


Icons link:
- <a href="https://www.flaticon.com/free-icons/search" title="search icons">Search icons created by Kiranshastry - Flaticon</a>
- <a href="https://www.flaticon.com/free-icons/add" title="add icons">Add icons created by Becris - Flaticon</a>
- <a href="https://www.flaticon.com/free-icons/recycle-bin" title="recycle bin icons">Recycle bin icons created by Uniconlabs - Flaticon</a>
- <a href="https://www.flaticon.com/free-icons/write" title="write icons">Write icons created by Arkinasi - Flaticon</a>
- And other icons from https://www.flaticon.com/free-icons/