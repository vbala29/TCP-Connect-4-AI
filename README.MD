# Connect-4 AI with TCP Login-Server (Java Swing)

# Introduction  

  Contains files to play connect-4 via a Java Swing application against a minimax based AI (depth 6 currently). 
  Also contains files to run a multithreaded login server (standalone) which allows for multiple clients to login
  at the same time, and retrieve data such as number of wins/losses. The AI prints out valuation of board positions and moves 
  to the console for debugging, but this can be commented out without affecting performance. Note that user information will
  be displayed in the top right of the application after a successful login and retrival from the server. Error code may 
  be thrown and displayed in the form of pop up messages.
  
 Use: 
  1. Run the Game class, as well as the ServerStartup Class. This will startup the Java Swing client applicaiton and present the oporunity the login.
     In addition, the TCP LoginServer will begin running. **If the login server is not running, the client application will not allow the game to start.**

  2. Login with your credentials. If you don't have an account, create one. 
      Credentials are alphanumeric and no more than 15 characters

  3. Begin playing against a the AI. You are player 1, they are player 2. The status
      bar at the bottom will tell you whose turn it is. 

  4. Note that you can undo moves using the undo button, as well as reset the game if 
      you are about to lose. Some error codes may cause the game to close after acknowledging the error,
      as it was determined that the error would prevent proper play from occuring. 

# Screenshots
   
   *AI wins a game where it played black.* 
   
   <img width="702" alt="Screen Shot 2021-01-06 at 2 46 56 PM" src="https://user-images.githubusercontent.com/56012430/103817286-62bce480-5034-11eb-8e2a-c54e4d7a5b5a.png">
   
   *A SMALL snippet of the AIs evaluation of various moves it could make, before settling on the best move.* 

<img width="210" alt="Screen Shot 2021-01-06 at 2 45 51 PM" src="https://user-images.githubusercontent.com/56012430/103817322-7a946880-5034-11eb-87a0-ef6ece585642.png">
   
   *Login Window*
   
   <img width="265" alt="Screen Shot 2021-01-06 at 2 45 15 PM" src="https://user-images.githubusercontent.com/56012430/103817310-7405f100-5034-11eb-9854-3a2d2a7d14cf.png">
   
   *Window indicating the login server is running*
   
   <img width="81" alt="Screen Shot 2021-01-06 at 2 45 06 PM" src="https://user-images.githubusercontent.com/56012430/103817397-9d268180-5034-11eb-8f3e-e70cead36571.png">


# Overview of Code    
*Package: com.vikrambala.connectfour:*

      Class: ConnectFour -> Models game state, and has algorithms to check for a winner.
      Class: Game implements Runnable-> The entry point, where the swing application starts from.
      Class: GameBoard extends JPanel -> The viewer/controller in the MVC model. 
      Class: GameMove -> Stores row & column fields. Used to store moves made in a collection
          in the ConnectFour class. These moves are undone when the undo button is clicked.
      Class: GameAI -> Runs the minimax AI algorithmn to determine the best moves to make. Information about valuation of
          board position and strength of moves is printed to the console for debugging. Depth of the AI minimax tree can be 
          adjusted by adjusting the depth paramneter for the minimaxAlgo() method when it is called in the GameBoard class.
      Class: LoginServerConnection -> Allows for the user to connect via TCP protocol to the login server. Contains methods to parse 
          server response codes, as well as act on the responses recieved, whether it be a successful login, information retrieval, or error code. 
          Will cause pop up messages to appear if error codes from the server are recieved. See com.vikrmabla.connectfour.gameserver package for more details.

*Sub-Package: com.vikrambala.connectfour.accounts*

      Class: Login extends JFrame -> Creates window for user to login into their account
          or create a new account.
      Class: CreateAccount extends JFrame -> Pop-up when a user opts to create a new account.
      Class: LoggedIn -> Wrapper for the boolean value loggedIn. Allows for synchronized access
          in the Game class, to determine whether or not gameWindow should be displayed as yet.
          Essentially allows for the gameWindow to stay hidden till a user is logged in. 

*Sub-Package: com.vikrambala.connectfour.interlude*

      Class: InterludeWindow extends JPanel -> Displays a randomly selected cute meme
      for five seconds after the user has logged in and the "game loads."

*Sub-Package: com.vikrambala.connectfour.error*

      Class: ErrorCode -> ErrorCode enum used in LoginFileParser to alert users to errors.
      Class: ErrorHandler -> Handles all error codes besides ones in LoginFileParser.
              Errors in LoginFileParser must be handled by the class itself, in order
              to properly shut down JFrames.

*Sub-Package: com.vikrambala.connectfour.gameserver*

     Class: LoginServerBackend -> A file which contains the code for the multithreaded back-end server. Uses TCP protocol and Java Server Sockets to
             establish connections. The server uses an executor service which creates new instances of the inner class, ConnectionWorker, each time a new client                        
             connects. The file uses the methods in LoginFileParser (see below) to retrieve data about a specific user, as well as verify logins. 
             Sends appropriate error responde codes if there is an issue with connection or with retrieving information.
     Class: LoginFileParser -> A file I/O class with functionality to create a new account,
             lookup existing account details (win/losses), validate login credentials,
             update account information after a game is completed (win/losses). Mainly used by LoginServerBackend.
     Class: ServerStartup -> Starts up the login server, and displays a small window to indicate that the server is running (see screenshots).

*Testing:*

     Class: GameTest -> Used to test the game state functions in ConnectFour class. 

