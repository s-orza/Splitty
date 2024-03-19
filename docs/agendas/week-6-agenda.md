| Key | Value |
| --- | --- |
| Date: | 19.03.2024 |
| Time: | 13:45-14:45 |
| Chair | Shahar |
| Minute Taker | Olav |

Agenda Items:
- Opening by chair (1 min)
- Check -in: How is everyone doing? (1 min)
- Approval of the agenda - Does anyone have any additions? (1 min)
- Announcements by the TA (3-5 min)
- Recap on what we did last week. showing it all to ta. (6 min)
- did everyone finish buddy check and passed knock out criteria (5 min)

Negatives from feedback (15 min):

planning:
- VERY IMPORTANT: start using the "Time tracking" feature on gitlab for issues and after time spent
- Everyone should work on 2 issues per week so smaller issues
- Add more information to description of issue
- To template of issue add definition of done, for explicit acceptance criteria
- Clearer titles for issues and merges

Code Contributions and Code Reviews:
- VERY IMPORTANT: we only have 4 checkstyle rules and should have 10+, maybe if at end time we can consider(5 min)
- When we comment on someone's mr we dont point out what he should change before we fix it
- The amount of discussion doesnt correlate to what we are uploading
- For more important contrevirsal changes having 3 reviewers might be prefered
- Modify the pipeline structure and include a separate "checkstyle" stage instead of having it integrated into the test stage so we dont need to push to see it failed form checkstyle (guide given)


Functionality to be implemented, task splitting (10 min):
- how to use  long polling/ws
- implement statistic
- logic for spliting debts
- fix participants method
- Sending emails/invitations
- multi language extension

Ending:
- Feedback round: What went well and what can be improved next time? (6 min)
- Question round: Does anyone have anything to add before the meeting closes? (4 min)
- Closure (1 min)
--------
- Planned meeting duration (1 hour) actual duration? Where/why did you mis -estimate? (? min)

Notes 

-----------------------------Announcements from Miss Elena------------------------------
 - From now on add sprint review, planning and retrospective
 - Friday W9 = code freeze
 - HCI assignment to do (this week!)
 - App follows *most* of Heuristic requirements (this week!)
 - IMPLEMENT TESTS!
    - for API (using mock in manual/moquito)
    - Show that we have testable code (dependancy injection)
 - Use service annotations for dependancy injection
 - READ FEEDBACK!
 - W7 rubric on product pitch (feature feedback)
 - Read BuddyCheck

-----------------------------RECAP------------------------------
 - Ivan: Backend Functions
 - Olav: Frontend debts Page and functions
 - Serban: fixed a lot of issues, checkboxes -> ID, Tags
 - David: Particpants (class, API, Repo) and Add Expense 
 - Oliwier: Main page and Admin page
 - Shahar: Connected Event to API, Tests

-----------------------------Negatives from feedback------------------------------
 - Planning
    - Time tracking (before)
    - How much time was spent (after)
    - make issues smaller (2 per week)
    - more descriptive issues (and Titles for issues and MRs)
    - definition of done
 - Code Contributions and Code Reviews
    - MORE CHECKSTYLE RULES
    - comments need to be more useful (give changes to implement, and implement them!)
    - 3 reviewers for BIG and CONTREVERSIAL
    - Follow checkstyle pipeline tutorial

------------------------To be implemented-----------------------
 - Long polling + Websockets (Shahar + Serban)
 - implement statistics (Serban)
 - backend Debt splitting (Olav)
 - Fixes
    - fix Participants methods (David)
    - fix Event Code (Oliwier)
 - Multi-Language (basic REQ) (Ivan)
 - sending email invites (Olav)
 - Admin JSON  (WildCard)
 - MORE CHECKSTYLE RULES (All)
 - Use service annotations for dependancy injection (Oliwier)
 - HCI (David)
