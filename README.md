# CodeFuryMain
An android application created for Woman Safety as a part of a one day Hackathon - "CodeFury" conducted at University Visvesvaraya College Of Engineering. 

Team Members :
  a) Yash Kothari ( CaptainDaVinci ) - UVCE 5th Semester CSE 
  b) Soumya S Raju - UVCE 5th Semester CSE
  c) Anurag Pandey - UVCE 5th Semester CSE
  d) Abhishek Rai ( OldTraveller ) - UVCE 5th Semester CSE
  
Features : 
1) The app can be launched in two ways :  
    First, using the Volume Down Key twice which triggers a CountDownTimer of 5 seconds ( which can be cancelled ).
       If not cancelled the application sends a message to the registered ICE ( In Case of Emergency Contacts ) set by the User.
       The message includes an in danger message to the recipient along with a link to the real time location of the User.
    Second, The Normal way of launching an application from the app menu which does not trigger any timer nor the sending of the messages.
    This measure was taken so as to avoid wrong information sending in case of accidental clicks by the User.
    
2) The differentiation of the opening procedures is made using the Accessibility Settings. 
   Other option can be using a Third Party Software like Button Mapper.

3) The application main motivation was to integrate it with a Central Server which can be handled by the Police or any such body.
   Central Server will be sent the details of lattitude and longitude along with any other extra helpful information.

4) The http requests are made using Volley which is used for scheduling the http requests and network scheduling.

5) For now the application can be used with devices on the same network. But can be modified for the global.

6) Real time location sharing is handled using an API created by CaptainDaVinci.


    
