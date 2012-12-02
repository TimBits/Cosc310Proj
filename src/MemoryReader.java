import java.io.*;
import java.util.*;

public class MemoryReader {

	//Searches for a given word (str1) in the memory and returns an Array of ArrayLists that contains the most accurate definition word and definitions
	//Index = 0 is the word, Index = 1 are the secondary words, Index = 2 are the definitions
	//Returns null if nothing is found
	public static String[][] search(String str1, String str2, String memoryLoc)
	{
		File defFile = new File("memory/"+memoryLoc+".txt");
		ArrayList<String> word = new ArrayList<String>();
		ArrayList<String> def = new ArrayList<String>();
		String[][] result = new String[3][];
		String[] line;
		Scanner scan = null;
		try {
			scan = new Scanner(defFile);
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		while (scan.hasNext())
		{
			line = scan.nextLine().split(":");
			word.add(line[0]);
			def.add(line[1]);
		}
		scan.close();
		int foundWord = Parser.compare(word, str1);
		if (foundWord != -1)
		{	
			String[] temp = new String[1];
			temp[0] = word.get(foundWord);
			result[0] = temp;
			
			if (str2.equals(""))
			{
				String[] tempDef = def.get(foundWord).split("%");
				if (tempDef.length < 2)
					return null;
				else
				{
					result[1] = null;
					result[2] = tempDef[1].split("/");
					return result;
				}
			}
			else
			{
				String[] tempDef = def.get(foundWord).split("%")[0].split("#");
				String[] secDef = new String[2];
				for (int i = 0; i < tempDef.length; i++)
				{
					secDef = tempDef[i].split(";");
					if (Parser.compare(secDef[0], str2) != -1)
					{
						result[1] = secDef[0].split("/");
						result[2] = secDef[1].split("/");
						return result;
					}
				}
				return null;
			}
		}
		return null;
	}
	
	//Searches for a given word and returns the first found word if it meets the comparison requirements defined by Parser.compare();
	//Returns null if nothing is found
	public static String search(String str1, String memoryLoc)
	{
		File defFile = new File("memory/"+memoryLoc+".txt");
		Scanner scan = null;
		String word;
		try {
			scan = new Scanner(defFile);
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		while (scan.hasNext())
		{
			word = scan.next().split(":")[0];
			if (Parser.compare(word, str1) == 0)
			{
				scan.close();
				return word;
			}
		}
		scan.close();
		return null;
	}
	
	//Searches the phrases of statements given a statement and conversation location and returns a set of phrases based on the closest number of correct words from the given and looked up statement
	//array[0][] = the possible response phrases, array[1][0] is the number the topic conversation should switch to
	public static String[][] searchPhrase(String str1, int topic)
	{
		File defFile = new File("memory/phrases.txt");
		ArrayList<String> word = new ArrayList<String>();
		ArrayList<String> def = new ArrayList<String>();
		String line;
		String[] splitLine;
		boolean correctTopic = false;
		Scanner scan = null;
		double tempFound, bestFound = 0;
		int found = -1;
		try {
			scan = new Scanner(defFile);
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		while (scan.hasNext())
		{
			line = scan.nextLine();
			if (line.equals("#" + topic))
				correctTopic = true;
			else if (line.equals("#" + (topic+1)))
				correctTopic = false;
			else if (correctTopic)
			{
				splitLine = line.split(":");
				word.add(splitLine[0]);
				def.add(splitLine[1]);
			}
		}
		scan.close();
		for (int i = 0; i < word.size(); i++)
		{
			tempFound = Parser.comparePhrases(word.get(i).split(" "), str1.split(" "));
			if (tempFound > bestFound)
			{
				bestFound = tempFound;
				found = i;
			}
		}

		if (found != -1)
		{
			String[][] result = {null, null};
			String[] tempArray = new String[1];
			tempArray = def.get(found).split(";");
			result[0] = tempArray[0].split("/");
			result[1] = tempArray[1].split(" ");
			return result;
		}
		else
			return null;
	}
	
	//Given a word it will search and return all associated words to that word including its self
	//Returns null if the word is not found
	public static ArrayList<String> searchAssociation(String str1)
	{
		File defFile = new File("memory/association.txt");
		ArrayList<String> word = new ArrayList<String>();
		ArrayList<String[]> associations = new ArrayList<String[]>();
		
		String[] line;
		Scanner scan = null;
		try {
			scan = new Scanner(defFile);
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		while (scan.hasNext())
		{
			line = scan.nextLine().split(":");
			if (line.length != 1)
				associations.add(line[1].split(","));
			else
			{
				String[] blankArray = {};
				associations.add(blankArray);
			}
			word.add(line[0].split(";")[1]);
		}
		scan.close();
		int index = Parser.compare(word, str1);
		if (index == -1)
			return null;
		else
		{
			ArrayList<String> result = new ArrayList<String>();
			if (associations.get(index) == null)
			{
				result.add(word.get(index));
				return result;
			}
			else
			{
				//Breadth-first search algorithm to find all associations
				Boolean[] found = new Boolean[word.size()];
				for (int i = 0; i < found.length; i++)
					found[i] = false;
				ArrayList<Integer> queue = new ArrayList<Integer>();
				
				queue.add(index);
				found[index] = true;
				int current, temp;
				while (!queue.isEmpty())
				{
					current = queue.remove(0);
					result.add(word.get(current));
					for (int i = 0; i < associations.get(current).length; i++)
					{
						temp = Integer.parseInt(associations.get(current)[i]);
						if (!found[temp])
						{
							found[temp] = true;
							queue.add(temp);
						}
					}
				}
				return result;
			}
		}
	}
	
	//Given the word, secondary word(optional), definition, and type of question it will store it in the correct memory file with the correct formatting
	//Returns true if words are stored, returns false if word is already stored
	public static boolean write(String str1, String str2, String str3, String memoryLoc)
	{
		File defFile = new File("memory/"+memoryLoc+".txt");
		Scanner scan = null;
		FileWriter fstream = null;
		String line, toWrite = "";
		String[] temp1, temp2, temp3;
		boolean tempFound = false, firstRun = true;

		boolean found = false;
		try {
			scan = new Scanner(defFile);
		}catch (FileNotFoundException e) {e.printStackTrace();}
		
		while (scan.hasNextLine())
		{
			line = scan.nextLine();
			temp1 = line.split(":");
			if (!firstRun) 
				toWrite += "\n";
			else
				firstRun = false;
			if (str1.equals(temp1[0]) & !found)
			{
				found = true;
				if (str2.equals(""))
				{
					temp1 = line.split("%");
					if (temp1[1].equals(str3))
						return false;
					else
						toWrite += line.split("%")[0] + "%" + str3;
				}
				else
				{
					tempFound = false;
					toWrite += str1 + ":";
					temp1 = temp1[1].split("%")[0].split("#");
					for (int i = 0; i < temp1.length; i++)
					{
						temp2 = temp1[i].split(";");
						if (temp2.length < 2)
						{
							toWrite += str2 + ";" + str3;
							tempFound = true;
						}
						else
						{
							temp3 = temp2[1].split("/");
							toWrite += temp2[0] + ";";
							if (temp2[0].equals(str2))
							{
								toWrite += str3 + "/";
								tempFound = true;	
							}
							for (int j = 0; j < temp3.length; j++)
							{
								if (temp2[0].equals(str2) & temp3[j].equals(str3))
								{
									scan.close();
									return false;
								}
								if (j < temp3.length-1)
									toWrite += temp3[j] + "/";
								else
									toWrite += temp3[j];
							}
							if (i < temp1.length-1)
								toWrite += "#";
						}
					}
					if (!tempFound)
						toWrite += "#" + str2 + ";" + str3;
					if (line.indexOf('%') == line.length()-1)
						toWrite += "%";
					else
						toWrite += "%" + line.split("%")[1];
				}
			}
			else
				toWrite += line;
		}
		scan.close();
		if (found)
			try {
				fstream = new FileWriter("memory/"+memoryLoc+".txt");
				PrintWriter out = new PrintWriter(fstream);
				out.print(toWrite);
				out.close();
				return true;
			} catch (IOException e1) {e1.printStackTrace();}	
		else
		{
			if (str2.equals(""))
				toWrite = "\n" + str1 + ":%" + str3;
			else
				toWrite = "\n" + str1 + ":" + str2 + ";" + str3 + "%";
		
			try {
				fstream = new FileWriter("memory/"+memoryLoc+".txt", true);
				PrintWriter out = new PrintWriter(fstream);
				out.print(toWrite);
				out.close();
				return true;
			} catch (IOException e1) {e1.printStackTrace();}	
		}
		return false;
	}
	
	//Writes to the association list given a word and its association word(str1 and str2 respectively) (eg: Tree is a plant : tree => plant)
	//Returns true if the word was stores, false if it was already
	public static boolean writeAssociation (String str1, String str2)
	{
		File defFile = new File("memory/association.txt");
		Scanner scan = null;
		FileWriter fstream = null;
		String toWrite = "";
		String[] line;
		ArrayList<String> word = new ArrayList<String>();
		ArrayList<String[]> associations = new ArrayList<String[]>();
		String[] blankArray = {};
		
		try {
			scan = new Scanner(defFile);
		}catch (FileNotFoundException e) {e.printStackTrace();}
		
		while (scan.hasNext())
		{
			line = scan.nextLine().split(":");
			if (line.length != 1)
				associations.add(line[1].split(","));
			else
				associations.add(blankArray);

			word.add(line[0].split(";")[1]);
		}
		scan.close();
		
		int index2 = Parser.compare(word, str2);
		if (index2 == -1)
		{
			word.add(str2);
			associations.add(blankArray);
			index2 = word.indexOf(str2);
		}
		
		int index1 = Parser.compare(word, str1);
		if (index1 == -1)
		{
			word.add(str1);
			String[] temp = {""+word.indexOf(str2)};
			associations.add(temp);
		}
		else if (associations.get(index1).length != 0)
		{
			if (Parser.compare(associations.get(index1), ""+index2) != -1)
				return false;
			else
			{
				String[] associationArray = associations.get(index1);
				String[] temp = new String[associationArray.length+1];
				for (int i = 0; i < temp.length-1; i++)
					temp[i] = associationArray[i];
				temp[temp.length-1] = ""+index2;
				associations.set(index1, temp);
			}
		}
		else
		{
			String[] temp = {""+index2};
			associations.set(index1, temp);
		}
		
		for (int i = 0; i < word.size(); i++)
		{
			if (i != 0)
				toWrite += "\n";
			toWrite += i + ";"+word.get(i);

			for (int j = 0; j < associations.get(i).length; j++)
			{
				if (j == 0)
					toWrite += ":";
				toWrite += associations.get(i)[j];
				if (j != associations.get(i).length-1)
					toWrite += ",";
			}
		}
		
		try {
			fstream = new FileWriter("memory/association.txt");
			PrintWriter out = new PrintWriter(fstream);
			out.print(toWrite);
			out.close();
			return true;
		} catch (IOException e1) {e1.printStackTrace();}	
		
		return false;
	}
}
