# CSCE 4930 Project
## The app: Gymwya
Gymwya is an app targetted at AUC students who are searching for a sports buddy. A lot of people give up on the idea of going to the gym or playing a sport simply because they cannot find a partner or are intimidated to play alone. This is why gymwya has been created. 
## Features
The app broadcasts the list of sports offered by AUC which could be either individual or group sports. This brings us to the idea of matching which is the main purpose of this app.
### Individual Matching
When the user presses on any individual sport, they will get a list of other users who have shown interest in playing a specific sport at a specifc time; then the user could either match with them or view other people who have created individual meetings.
### Group Matching
Group matching follows a similar idea to individual matching where the user presses in a group sport and all the available groups wanting to play this sport show up with information about the group members, number of members and the time of match. The user could check the profiles of the group members and either choose to join the group or search for other groups.

The matching feature is the most important feature of the application; however, we do offer more features within the application including, visiting other user’s profiles where you can only message them if a meeting has been confirmed between the two parties, edit profile, view requests when someone matches with a meeting you have created,view the meetings you have created and your current matches, search for sports and groups, creating individual and group meetings.
## Front-end
The app consists of around 16 activities. All of them were implemented as a Linear Layout with TextViews, EditViews, ImageViews and buttons in them. Alot of the activities also implemented ListViews, GridViews and RecycleViews and some had a SearchView for the search bars. The app’s general theme is blue,black and white and it has been emphasised through the consisten design of the activities, borders and text colours. 
## Back-end
#### API:
We have implemented around 20 APIs to connect the application with the database in order to fetch information from it or add information to it using NodeJs. We used two methods to implement these APIs which are POST and GET. We have used 11 GET methods and 9 POST methods.

#### Database:

We used 2 databases in this project. The first one is firebase to store the user’s login credentials and use them for authentication and to also store and upload the images in the application. For the rest of the data we used mySQL. We worked on a database hosted online on db4free as we could all open it on our laptops rather than being restricted to a group member’s laptop.

#### Java:

Our application was written using Java on Android Studio. In the Java code, we have created intents to deal with the buttons created in the xml files for the activities .We have also created POST and GET requests to call the APIs created in the NodeJs file. The aim of these requests is to insert information about the User into the database or get their information from the database to be used in the app. The POST requests are called once the user creates an account, edits profile, matches with someone, creates a meeting (individual or goup), decline request or removes someone from a group. The GET requests are called at the beginning of the application to retrieve the user’s information and puts the information in a class called ‘data’ to store all the information needed. The information fetched from the database is later used to set texts and images to display the correct information about a specific user on each activity. It is also important to note that we stored the user’s login information in shared preferences so that the user does not have to Sign In to the application everytime they open it, it will instead take them to the homepage directly. We have also written several validations regarding the date to ensure that the user cannot select a date before today, regarding the time of the meetings, regarding the name length, age restrictions, group size and passwords matching. 


## Contributers 
- Nour Kasaby
- Youssef Elhagg
- Dana Elkhouri
- Ahmed Elbarbary
- Hussein Elazhary
