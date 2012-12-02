import java.util.ArrayList;


public class Parser {
		
	//Takes a sentence and some common words and returns the sentence without those useless words and without punctuation in an arraylist separated by spaces
	public static ArrayList<String> lineParser(String line, String[] uselessWords)
	{
		line = line.toLowerCase().replace("?", "").replace("!", "").replace(".", "").replace(",", "").replace("/", "").replace("@", "").replace("#", "").replace("$", "").replace("%", "").replace("^", "").replace("&", "").replace("*", "").replace("(", "").replace(")", "").replace("`", "").replace("~", "").replace(":", "").replace(";", "").replace("'", "").replace("\"", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace("\\", "").replace("|", "").replace("-", "").replace("+", "").replace("_", "").replace("=", "");
		String word;
		String[] splitLine = line.split(" ");
		ArrayList<String> parLine = new ArrayList<String>();
		
		for (int i = 0; i < splitLine.length; i++)
		{
			if (compare(uselessWords, splitLine[i]) == -1)
			{
				word = splitLine[i].replace(" ", "");
				if (!word.equals(""))
					parLine.add(word);
			}
		}
						
		return parLine;
	}
	
	//Takes a sentence and returns a sentence without punctuation in an arraylist separated by spaces
	public static ArrayList<String> lineParser(String line)
	{
		String[] uselessWords = {""};
		return lineParser(line, uselessWords);
	}
	
	//Compares two strings for ~70% correctness and returns: -1 if nothing is found or 0 if found
	//str1 should be the correctly spelled word and str2 should be the typed
	public static int compare(String str1, String str2)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(str1);
		return compare(arrayList, str2);
	}
	
	
	//Finds the most accurate word from a list (str1) given a typed word (str2) and returns: -1 if nothing is found or the index of the word found
	public static int compare(ArrayList<String> str1, String str2)
	{
		int accWord = -1;
		double mostAcc = -1;
		int numFound;
		boolean found;
		//To prevent double counting of the same letter
		boolean[] used = new boolean[str2.length()];
		
		for (int j = 0; j < str1.size(); j++)
		{
			if (str1.get(j).length() <= 4)
			{
				if (str1.get(j).equalsIgnoreCase(str2))
					return j;
			}
			else
			{
				numFound = 0;
				
				for (int x = 0; x < used.length; x++)
					used[x] = false;
				
				for (int i = 0; i < str1.get(j).length(); i++)
				{
					found = false;
										
					try{
						if (str1.get(j).charAt(i) == str2.charAt(i) & !used[i])
						{
							used[i] = true;
							numFound++;
							found = true;
						}
					}catch (StringIndexOutOfBoundsException e){}
					
					try{
						if (str1.get(j).charAt(i) == str2.charAt(i+1) & found == false & !used[i+1])
						{
							used[i+1] = true;
							numFound++;
							found = true;
						}
					}catch (StringIndexOutOfBoundsException e){}
						
					try{
						if (str1.get(j).charAt(i) == str2.charAt(i-1) & found == false & !used[i-1])
						{
							used[i-1] = true;
							numFound++;
							found = true;
						}
					}catch (StringIndexOutOfBoundsException e){}	
				}
				
				//double acc = (double)numFound/(((double)str1.length()+(double)str2.length())*0.5);
				double acc;
				if (str1.get(j).length() > str2.length())
					acc = (double)numFound/(double)str1.get(j).length();
				else
					acc = (double)numFound/(double)str2.length();
				
				
				//System.out.println("Found: " + numFound);
				//System.out.println(j + ": " + acc + " Acc");
				
				if (acc == 1)
					return j;
				
				if (acc >= 0.7 & mostAcc < acc)
				{
					accWord = j;
					mostAcc = acc;
				}
			}	
		}
		return accWord;
	}
	
	//Does above function but with array input
	public static int compare(String[] str1, String str2)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < str1.length; i++)
			arrayList.add(str1[i]);
		return compare(arrayList, str2);
	}
	
	//Compares 2 Arrays of words and returns the 'closeness' of them (1 = exact, 0 = completely different)
	//A missing word is heavily penalized and a word in the wrong order is slightly penalized
	//str1 should be the correctly spelled phrase, str2 is the one typed by the user
	public static double comparePhrases(String[] str1, String[] str2)
	{
		double penalty = 0, tempPenalty;
		for (int i = 0; i < str1.length; i++)
		{
			tempPenalty = 3;
			for (int j = 0; j < str2.length; j++)
			{
				if (compare(str1[i], str2[j]) == 0)
				{
					if (tempPenalty > Math.abs(i-j))
						tempPenalty = Math.abs(i-j)*0.5;	
				}
			}
			penalty += tempPenalty;
		}
		if (str2.length > str1.length)
			penalty += Math.abs(str1.length - str2.length);
		
		double acc = 1-(penalty/str1.length);
		if (acc < 0)
			acc = 0;
		return acc;
	}
	
	//Does above method but with arraylist input
	public static double comparePhrases(ArrayList<String> str1, String[] str2)
	{
		String[] array = new String[str1.size()];
		for (int i = 0; i < str1.size(); i++)
			array[i] = str1.get(i);
		return comparePhrases(array, str2);
	}

	//Returns null if not a question or the type of question if it is one
	public static String isQuestion(String line)
	{
		ArrayList<String> parLine = lineParser(line);
		String[] questionWords = {"what", "how", "why", "where", "when", "are", "is"};	

		int index = compare((questionWords), parLine.get(0));
		if (index != -1)
			return questionWords[index];

		return null;
	}
}
