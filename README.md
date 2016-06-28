# TicketService
Ticket Service

Download the code from Git.
Execute mvn install

The JAR file will be present inside the TARGET folder.
Go to the jar path and execute 

java -jar "ticketservice-0.0.1-SNAPSHOT.jar"

Assumptions
-------------
1)Users will have a valid unique customer Email Id and Password.

2)The authentication in the application now allows only "password" as the password for all users.
3)The user after logging in to the application will be able to see the Seating Level details followed by the Menu choices as below.

	1)Check Availability
	2)Find And Hold Seats
	3)Reserve Seats
	4)Logout

4)The user will be able to hold and reserve tickets for himself.The hold out time is configured in a constant file.(60seconds).
5)The seating data is initialized once when the TicketServiceRepository(Singleton) object is created in the application.
6)The storage is handled using inMemoryData object.
7)Prints the seating arrangement per level on a row by row basis.[A]- Available,[H]-Held Seats and [R]-Reserved Seats.
8)After finding and holding seats ,a seat hold Id is generated and displayed to the user.
  User is prompted to give the seat hold Id to reserve the held seats.
