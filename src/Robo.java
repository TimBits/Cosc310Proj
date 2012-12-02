import java.util.*;


public class Robo {

	//Answered Question and waiting for users next response: status = "answered"
	//Waiting on user to answer question: status = "waiting"
	private String status;
	//Stores the last question asked to it can determine the format of the answer
	private String questionAsked;
	//Stores where the conversation is, 1 = Greetings, 2 = Regular conversation
	private int topic;
	
	private static String[] commonWords = {"the", "is", "a", "an", "but", "this", "that", "are", "so", "what", "when", "why", "how", "where", "do", "does", "of", "same", "as", "was", "will", "were", "did", "don't", "not"};
	private static String[] pronouns = {"they", "it", "them", "she", "he", "we", "i", "its", "you", "your", "me"};
	private static String[] denyWords = {"no", "nope", "na", "negative", "i dont know", "im not sure", "no idea", "not sure"};
	private static String[] askPhrases = {"I do not know,$s?", "I am not sure,$s?", "Can you answer,$s?", "I have not learned the answer to,$s? Could you tell me?", "What is the answer to$s?"};
	private static String[] responses = {"Ok, I will make note of that.", "Cool, thanks!", "Right.", "OK.", "Alright.", "Thanks.", "I will remember that."};
	private static String[] noInput = {"O, ok then.", "Fine, dont tell me!", "I didnt need to know that anyways.", "It was useless information anyways!"};
	private static String[] alreadyKnow = {"I already knew that!", "You taught me that before.", "Thats not new information.", "I know that already.", "I memorized that before"};
	private static String[] associationPhrases = {"is a", "is an", "is the same as a", "is the same as an", "is the same as the", "is the", "is"};
	private static String[] dontUnderstand = {"I dont understand what you are saying.", "What?", "Huh?", "Im not sure what you mean.", "Please rephrase yourself.", "I can not understand you.", "Does not compute!"};
	private static String[] greet = {"We should greet eachother first.", "Your not gonna greet me?", "Lets greet first.", "Say hi first", "Humans greet eachother before starting a conversation", "Humans normally say hi to eachother first"};
	
	public Robo()
	{
		topic = 1;
		status = "answered";
		questionAsked = null;
	}
	
	public int getTopic()
	{
		return topic;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	//Branches based on whether the input is a question or not
	public String respond(String line)
	{
		String type = Parser.isQuestion(line);
		if (Parser.isQuestion(line) != null)
		{
			if (topic == 1)
			{
				Random rand = new Random();
				return greet[rand.nextInt(greet.length)];
			}
			return answerQuestion(line, type);
		}
		else if (status.equals("waiting"))
			return respondQuestion(line);
		else
			return respondPhrase(line);
	}
	
	//Responds back to the user give a phrase.
	//If it is association then it will store it and respond generically
	//If it is a phrase it will look it up and respond accordingly
	private String respondPhrase(String line)
	{
		for (int i = 0; i < associationPhrases.length; i++)
		{
			if (line.contains(associationPhrases[i]))
				return isAssociation(line);
		}
		
		ArrayList<String> parsedLine = Parser.lineParser(line);
		line = "";
		for (int i = 0; i < parsedLine.size(); i++)
		{
			if (i != 0)
				line += " ";
			line += parsedLine.get(i);
		}
		
		line = line.replace("you", "robotalk");
		
		String response = checkAssociation(line, 0);
		if (response != null)
		{
			return formatSentence(responseGenerator(response), false);
		}
		return dontUnderstand();
	}
	
	//Recursively searches through all the possible associations of the words in the given line to try and find a match
	private String checkAssociation(String line, int index)
	{
		String[] splitLine = line.split(" ");
		if (index == splitLine.length)
		{
			String[][] result = MemoryReader.searchPhrase(line, topic);	
			Random rand = new Random();
			if (result != null)
			{
				topic = Integer.parseInt(result[1][0]);
				String response = result[0][rand.nextInt(result[0].length)];
			
				return response;
			}
			return null;
		}
		String word, result = null;
		for (int k = 0; k < splitLine.length-index; k++)
		{
			word = "";
			for (int x = 0; x <= k; x++)
			{
				if (x != 0)
					word += " ";
				word += splitLine[index+x];
			}
			ArrayList<String> associations = MemoryReader.searchAssociation(word);
			if (associations == null)
			{
				return checkAssociation(line, index+1);
			}
			for (int i = 0; i < associations.size(); i++)
			{
				result = checkAssociation(line.replace(word,associations.get(i)), index+1);
				if (result != null)
					return result;
			}
		}
		return null;
	}
	
	//Takes the association and breaks it into its keywords and then calls MemoryReader to store it
	//Returns a generic phrase if it was successful and 'alreadyknow' phrase if it was already stored
	private String isAssociation(String line)
	{
		int index = 0;
		for (int i = 0; i < associationPhrases.length; i++)
		{
			index = line.indexOf(associationPhrases[i]);
			if (index != -1)
				break;
		}
		
		String start = line.substring(0, index);
		String end = line.substring(index);
		ArrayList<String> parsedStart = Parser.lineParser(start, commonWords);
		ArrayList<String> parsedEnd = Parser.lineParser(end, commonWords);
		
		if (parsedStart.get(0) == null || parsedEnd.get(0) == null)
			return dontUnderstand();
		
		String word1 = "", word2 = "";
		for (int i = 0; i < parsedStart.size(); i++)
		{
			if (i != 0)
					word1 += " ";
			word1 += parsedStart.get(i);
		}
		for (int i = 0; i < parsedEnd.size(); i++)
		{
			if (i != 0)
				word2 += " ";
			word2 += parsedEnd.get(i);
		}
		
		Random rand = new Random();
		if (MemoryReader.writeAssociation(word1, word2))
			return responses[rand.nextInt(responses.length)];
		else
			return alreadyKnow[rand.nextInt(alreadyKnow.length)];
		
	}
	
	//Answers questions and returns response based on what type of question it is
	//If it doesnt know the answer it will then ask for it
	private String answerQuestion(String line, String type)
	{
		ArrayList<String> parLine = Parser.lineParser(line, commonWords);
		if (parLine.size() == 0)
			return dontUnderstand();
		String word, secWord = "",response = "";
		ArrayList<String[][]> wordDefs = new ArrayList<String[][]>();
		String[][] wordDef;

		word = parLine.get(0);
		if (parLine.size() >= 2)
			secWord = parLine.get(1);
		
		boolean isAssoc = false;
		ArrayList<String> associations = MemoryReader.searchAssociation(word);
		if (associations != null)
		{	
			for (int i = 0; i < associations.size(); i++)
			{
				wordDef = MemoryReader.search(associations.get(i), secWord, type);
				if (wordDef != null)
					wordDefs.add(wordDef);
				isAssoc = true;
			}
		}
		else
		{
			wordDef = MemoryReader.search(word, secWord, type);
			if (wordDef != null)
				wordDefs.add(wordDef);
		}
	
		Random rand = new Random();
		if (wordDefs.isEmpty())
		{
			if (associations != null)
				if (associations.size() > 1 & secWord == "")
					return formatSentence("a " + associations.get(0) + " is a " + associations.get(rand.nextInt(associations.size()-1)+1), false);
			
			questionAsked = line;
			return askQuestion(line, word, secWord);
		}
		
		int index = rand.nextInt(wordDefs.size());
		
		String[][] result = new String[3][];
		if (isAssoc == true)
		{
			result[0] = new String[1];
			result[0][0] = associations.get(0);
			result[1] = wordDefs.get(0)[1];
			result[2] = wordDefs.get(index)[2];
		}
		else
		{
			result[0] = wordDefs.get(0)[0];
			result[1] = wordDefs.get(0)[1];
			result[2] = wordDefs.get(index)[2];
		}
		response = sentenceGenerator(result, type);
		
		return response;
	}
	
	//Asks what the word means and prepares to get an answer
	private String askQuestion(String line, String word, String secWord)
	{
		status = "waiting";
		Random rand = new Random();
		ArrayList<String> parLine = Parser.lineParser(line);
		line = "";
		for (int i = 0; i < parLine.size(); i++)
			line += " " + parLine.get(i);
		return askPhrases[rand.nextInt(askPhrases.length)].replace("$s", line);
	}
	
	//Determines the users response, responds to the given answer and calls the function to save it
	private String respondQuestion(String line)
	{
		ArrayList<String> parQuestion = Parser.lineParser(questionAsked, commonWords);
		if (parQuestion.size() == 0)
			return dontUnderstand();
		
		status = "answered";
		
		String type = Parser.isQuestion(questionAsked);
		Random rand = new Random();

		ArrayList<String> parLine = new ArrayList<String>();
		String word = "", secWord = "", def = "", foundWord;
		
		word = parQuestion.get(0);
		if (parQuestion.size() >= 2)
			secWord = parQuestion.get(1);
		
		String[ ] splitLine = line.split(" ");
		for (int i = 0; i < denyWords.length; i++)
		{
			if ((line.contains(denyWords[i]) & denyWords[i].length() > 4) | Parser.compare(splitLine, denyWords[i]) != -1)
				return noInput[rand.nextInt(noInput.length)];
		}
		
		parLine = Parser.lineParser(line);
		if (Parser.compare(commonWords, parLine.get(0)) >= 0)
			parLine.remove(0);
		
		if (Parser.compare(word, parLine.get(0)) == 0 || Parser.compare(pronouns, parLine.get(0)) >= 0)
			parLine.remove(0);
		
		if (Parser.compare(secWord, parLine.get(0)) == 0)
			parLine.remove(0);
		
		for (int i = 0; i < parLine.size(); i++)
		{
			def += parLine.get(i);
			if (i != parLine.size()-1) 
				def += " ";
		}
		foundWord = MemoryReader.search(word, type);
		if (foundWord != null)
			word = foundWord;
		MemoryReader.write(word, secWord, def, type);
		return responses[rand.nextInt(responses.length)];
	}
	
	//Simple method to pick a random phrase from dont understand and return it
	private String dontUnderstand()
	{
		Random rand = new Random();
		return dontUnderstand[rand.nextInt(dontUnderstand.length)];
	}
	
	//Generates a sentence when given a word and definitions based on what type of sentence it is
	private String sentenceGenerator(String[][] wordDef, String type)
	{
		String line = "";
		Random rand = new Random();
		
		status = "answered";
		
			int index  = rand.nextInt(wordDef[2].length);
			if (type.equals("what") | type.equals("where") | type.equals("why") | type.equals("when"))
			{
				String[] splitLine = wordDef[2][index].split(" ");
				if (Parser.compare("is", splitLine[0]) == 0 & Parser.compare("your", wordDef[0][0]) == -1 & Parser.compare("my", wordDef[0][0]) == -1)
				{
					if (wordDef[0][0].charAt(0) == 'a' | wordDef[0][0].charAt(0) == 'o' | wordDef[0][0].charAt(0) == 'u' | wordDef[0][0].charAt(0) == 'e' | wordDef[0][0].charAt(0) == 'i')
						line += "An ";
					else
						line += "A ";
					if (wordDef[0][0].charAt(wordDef[0][0].length()-1) == 's')
						wordDef[0][0] = wordDef[0][0].substring(0, wordDef[0][0].length()-1);
				}
				else if (Parser.compare("are", splitLine[0]) == 0 || Parser.compare("becuase", splitLine[0]) == 0)
				{
					if (wordDef[0][0].charAt(wordDef[0][0].length()-1) != 's')
						wordDef[0][0] = wordDef[0][0] + "s";
				}
				if (wordDef[1] == null)
				{	
					line += wordDef[0][0] + " " + responseGenerator(wordDef[2][index]);
				}
				else
				{
					line += wordDef[0][0] + " " + wordDef[1][0] + " " + responseGenerator(wordDef[2][index]); 
				}
			}
			else if (type.equals("are") | type.equals("is") | type.equals("how"))
			{
				line += wordDef[2][index];
			}
			
		return formatSentence(line, true);
	}
	
	//Formats the line to be a sentence. Capital at the front, period at the end
	//This also swaps you/i to the system can recognize you means i and vice-versa
	private static String formatSentence(String line, boolean swapPronouns)
	{
		if (swapPronouns)
		{
			String[] splitLine = line.split(" ");
			String[] result = new String[splitLine.length];
			for (int i = 0; i < splitLine.length; i++)
				result[i] = splitLine[i];
				
			int index = Parser.compare(splitLine, "you");
			if (index != -1)
				result[index] = "i";
			index = Parser.compare(splitLine, "your");
			if (index != -1)
				result[index] = "my";
			index = Parser.compare(splitLine, "i");
			if (index != -1)
				result[index] = "you";
			index = Parser.compare(splitLine, "me");
			if (index != -1)
				result[index] = "you";
			index = Parser.compare(splitLine, "my");
			if (index != -1)
				result[index] = "your";
		
			line = "";
			for (int i = 0; i < result.length; i++)
			{
				if (i != 0)
					line += " ";
				line += result[i];
			}
		}
		line = line.substring(0, 1).toUpperCase() + line.substring(1) + ".";
		
		return line;
	}
	
	//Given any line, it will replace any possible words with its association at random
	private String responseGenerator(String line)
	{
		String[] splitLine = line.split(" ");
		ArrayList<String> associationWords = new ArrayList<String>();
		Random rand = new Random();
		for (int i = 0; i < splitLine.length; i++)
		{
			associationWords = MemoryReader.searchAssociation(splitLine[i]);
			if (associationWords != null)
				splitLine[i] = associationWords.get(rand.nextInt(associationWords.size()));
		}
		
		String result = splitLine[0];
		for (int i = 1; i < splitLine.length; i++)
			result += " " + splitLine[i];

		return result;
	}
}
