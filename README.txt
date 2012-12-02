Installation:
1. Run through Windows (other operating systems may work)
2. Have java installed
3. Install the latest version of Eclipse (http://www.eclipse.org/downloads/)
4. Create a new project and import the 'src' files into it (main.java, MemoryReader.java, Parser.java, Robo.java)
5. Make sure the 'memory' folder is in the project directory with the 'src' and 'bin' folders
6. Run the 'main.java' through Eclipse
7. Use the command line to interact with the system

System Overview:
-The system is an AI trying to learn many things about the human word
-You can ask it questions and it will try to respond
-Things is does not know can be learned from the user
-The system can associate words with other words to determine user questions and to randomize output

A+ Options:
-Only 1 of the 3 options were completed for the deadline
-The system can learn new words and concepts and associate words with others (complete)
-The system will detect emotions from the user and try to understand them (incomplete)
-The system will have two GUIs for desktop and Android/iPhone devices (incomplete)

Files:
*All files should be located in your Eclipse workspace directory

src/main.java
-Runs the program. Simply creates a robo class and continually grabs user input.

src/MemoryReader.java
-Class used to read and write to the various memory files.  This also includes the ability to search for specific information.

src/Parser.java
-Class used to manipulate string data.  This includes some things as trimming string of useless words and comparing two strings with spelling errors.

src/Robo.java
-Central class used for deciding what the system should do and for formating its output.  This will determine the users input and split into various actions based on it.

memory/are.txt, memory/how.txt, memory/is.txt, memory/what.txt, memory/when.txt, memory/where.txt, memory/why.txt
-All txt files with similar information structure.  they are used to store the answers to various questions. (what.txt is for 'what is...?' questions and so on)

memory/association.txt
-An information txt file used to store the associations of different words to eachother.  Acts as an adjacency list.

memory/phrases.txt
-An information txt file used to store all the posible non-question inputs and the response it should give.

Bugs/Missing features:
-The system will not recognize negatives (eg. not, doesnt, etc).  It will just treat it is a positive.
-The system can not read more than 2 key words for input into questions. Eg: 'What do humans eat in the morning' would be the same as 'What do humans eat'.
-The system does not determine output based on any context of the conversation, only what was just inputed. This would be a hard feature to implement correctly.
-The system does not always have correct grammar. Eg: 'What do humans eat' returns 'Human eat bread'.
-The system has no way to learn phrases from the user.  In the future, Id like the system to figure out phrases and responses from the user and store them.
-The system believes anything the user tells it.  In the future, Id like to have the system determine if an answer from the user is correct based on multiple users.
 So if a certain small percentage of the userbase all told the system the same answer to that question it would then store it as the correct answer.
-The system can respond with very odd answers/phrases if the user tought it that. No real way to prevent the user from inputing nonsensical answers, aside from doing the above feature.
-The system never initiates the conversation. In the future, Id like the system to start the conversation or even ask the user questions of things it does not know.
-The systems phrases are currently very limited. In the future, Id like to expand out the possible responses to a wide range of inputs.