/* TextGenerator.java
 * Sam Hage, Philip Chang
 * 
 * Generates text predictively based on a training set
 */

import java.util.*;
import java.math.*;
import java.io.*;

public class TextGenerator {

	public static void main(String[] args) throws IOException {

		Scanner scan = new Scanner(System.in);

		boolean words = true;
		System.out.println("Generate text based on [words] or [characters]? ");
		String response = scan.next();
		scan.nextLine();
		if (response.equalsIgnoreCase("words")) {;} // words = true;
		else if (response.equalsIgnoreCase("characters")) {
			words = false;
		}
		else {
			System.out.println("Invalid input");
		}
		
		boolean toFile = false;
		System.out.println("Set up files? (y/n) ");
		response = scan.next();
		scan.nextLine();
		if (response.equalsIgnoreCase("y")) {
			toFile = true;
		}
		
		if (toFile) {
			// write sample output to the text files
			// NOTE: this will append, not overwrite, so it's best to clear the files first
			// files should start out with text when submitted
			
			if (words) { // output word-based text to files ---------------------------
				for (int k = 1; k < 4; k++) {
					
					String inFile = "kimTwitter.txt";
					for (int i = 0; i < 3; i++) {
						if (i == 1) {
							inFile = "kantyeTwitter.txt";
						}
						if (i == 2) {
							inFile = "ronTwitter.txt";
						}

						HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
						try {
							map = train2(inFile, k);
						} catch (IOException e) {
							e.printStackTrace();
						}

						String writeFile = "out-word" + Integer.toString(k) + ".txt";
						for (int j = 0; j < 10; j++) {
							try {
								generateText2(map, 30, k, writeFile, true); // 30 words means about 4.5 characters per tweet assuming 140 characters
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} // ---------------------------------------------------------------------

			else { // output character-based text to files ---------------------------
				for (int k = 1; k < 8; k += 2) {
					if (k == 7) {
						k += 1;
					} 
					String inFile = "kimTwitter.txt";
					for (int i = 0; i < 3; i++) {
						if (i == 1) {
							inFile = "kantyeTwitter.txt";
						}
						if (i == 2) {
							inFile = "ronTwitter.txt";
						}

						HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
						try {
							map = train(inFile, k);
						} catch (IOException e) {
							e.printStackTrace();
						}

						String writeFile = "out-char" + Integer.toString(k) + ".txt";
						for (int j = 0; j < 10; j++) {
							try {
								generateText(map, 140, k, writeFile, true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} // ---------------------------------------------------------------------
		}
		
		else { // ask for user input and print sample output to the screen until user quits
			while(true){

				String file = "";
				System.out.print("Please enter a training file, q to quit: ");
				file = scan.nextLine();
				if (file.equalsIgnoreCase("q")) {
					break;
				}
				System.out.print("Please enter the k value: ");
				int k = Integer.parseInt(scan.nextLine());
				HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
				try {
					if (words) {
						map = train2(file, k);
					}
					else {
						map = train(file, k);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				int numGen = 0;
				while(numGen <= 0 ){
					if (words) {
						System.out.print("Enter number of words to generate: ");
					}
					else {
						System.out.print("Enter number of characters to generate: ");
					}
					numGen = Integer.parseInt(scan.nextLine());
				}
				try {
					if (words) {
						generateText2(map, numGen, k, "dummy.txt", false); // we won't be writing anyway, but need a valid file
					}
					else {
						generateText(map, numGen, k, "dummy.txt", false);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}

	//Trainer: sets up the transitional probabilities for the given k. You may modify this method header.
	public static HashMap<String,ArrayList<String>> train(String file, int k) throws IOException {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		Scanner fileReader = new Scanner(new File(file));
		String testText = "";
		while (fileReader.hasNextLine()) {
			testText += fileReader.nextLine();
		}
		fileReader.close();

		for (int i = 0; i <= testText.length() - k - 1; i++) {
			String kgramX = "", kgramS = "";
			// read a k-gram X
			kgramX = testText.substring(i, i + k);
			// create S by appending the next character in the text
			kgramS = kgramX.substring(1) + testText.substring(i + k, i + k + 1);
			// create a new entry in the hashmap if X is not in the table
			if (!map.containsKey(kgramX)) {
				ArrayList<String> gramList = new ArrayList<String>();
				gramList.add(kgramS);
				map.put(kgramX, gramList);
				// otherwise add S to X's arraylist
			} else {
				map.get(kgramX).add(kgramS);
			}
		}
		return map;
	}

	//Generator: Generates numGen characters based on the transitional probabilities estimated by the trainer 
	public static void generateText(HashMap<String, ArrayList<String>> map, int numGen, int k, String filename, boolean write) throws IOException {

		// start with a random k-gram X
		String kgramX = "";
		Random generator = new Random();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		String toWrite = "";
		Object[] keys = map.keySet().toArray();
		kgramX = (String) keys[generator.nextInt(keys.length)];
		System.out.print(kgramX);
		toWrite += kgramX;
		int numChars = k;

		while (numChars <= numGen) {
			// retrieve X's list of transitional k-grams
			ArrayList<String> gramList = map.get(kgramX);
			// randomly select an S from X's list
			String kgramS = gramList.get(generator.nextInt(gramList.size()));
			// create a new X by concatenating with c, the last character of S
			String c = kgramS.substring(kgramS.length() - 1);
			System.out.print(c);
			toWrite += c;
			numChars++;
			kgramX = kgramX.substring(1) + c;
		}
		if (write) { // whether we write to a file or not
			out.println(toWrite + "\n");
			out.close();
		}
		System.out.println("\n\n");
	}

	public static HashMap<String,ArrayList<String>> train2(String file, int k) throws IOException {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		Scanner fileReader = new Scanner(new File(file));
		ArrayList<String> testText = new ArrayList<String>();
		while (fileReader.hasNext()) {
			testText.add(fileReader.next());
		}
		fileReader.close();

		for (int i = 0; i <= testText.size(); i++) {
			String wordgramX = "", wordgramS = "";
			// read a k-word-gram X
			for (int l = i; l < i + k; l++) {
				if (l < testText.size()) { // need to make sure we don't exceed testText
					wordgramX += testText.get(l) + " ";
				}
				else {
					wordgramX += testText.get(i - (testText.size()-k)); // loop back to beginning of text
				}
			}
			// create S by appending the next word in the text
			String[] tempStrings = new String[k];
			String tempgramX = "";
			if (k > 1) { // if k > 1 we need to remove the first word from X, if k == 1, S just becomes the next word in the text
				tempStrings = wordgramX.split(" "); // make it an array to remove beginning
				tempStrings = Arrays.copyOfRange(tempStrings, 1, tempStrings.length); // remove first word
				for (int j = 0; j < tempStrings.length; j++) { 
					tempgramX += tempStrings[j] + " "; // convert back to string
				}
			}
			if (i + k < testText.size()) { // need to make sure we don't exceed testText
				wordgramS = tempgramX + testText.get(i+k); // add the next word in the text
			}
			else {
				wordgramS = tempgramX + testText.get(i - (testText.size()-k));
			}
			// create a new entry in the hashmap if X is not in the table
			if (!map.containsKey(wordgramX)) {
				ArrayList<String> gramList = new ArrayList<String>();
				gramList.add(wordgramS);
				map.put(wordgramX, gramList);
				// otherwise add S to X's arraylist
			} else {
				map.get(wordgramX).add(wordgramS);
			}
		}
		return map;
	}

	public static void generateText2(HashMap<String, ArrayList<String>> map, int numGen, int k, String filename, boolean write) throws IOException {

		// start with a random k-word-gram X
		String wordgramX = "";
		Random gen = new Random();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		String toWrite = "";
		Object[] keys = map.keySet().toArray();
		wordgramX = (String) keys[gen.nextInt(keys.length)];
		// capitalize first letter of output without altering actual word gram and remove extra space
		toWrite += wordgramX.substring(0, 1).toUpperCase() + wordgramX.substring(1, wordgramX.length() - 1);
		int numWords = k;

		while (numWords < numGen) {
			// retrieve X's list of transitional k-grams
			ArrayList<String> gramList = map.get(wordgramX);
			// randomly select an S from X's list
			String wordgramS = "";
			if (gramList.size() > 1) {
				int randomIndex = gen.nextInt(gramList.size());
				wordgramS = gramList.get(randomIndex);
			}
			else {
				wordgramS = gramList.get(0);
			}
			// create a new X by concatenating with c, the last word of S
			String[] tempStringsS = new String[k];
			String[] tempStringsX = new String[k];
			String tempgramX = "", tempgramS = "";
			if (k > 1) { // split strings into arrays as before to allow easy slicing
				tempStringsS = wordgramS.split(" ");
				tempStringsX = wordgramX.split(" ");
				tempStringsS = Arrays.copyOfRange(tempStringsS, tempStringsS.length - 1, tempStringsS.length);
				tempStringsX = Arrays.copyOfRange(tempStringsX, 1, tempStringsX.length);
				for (int j = 0; j < tempStringsX.length; j++) { 
					tempgramX += tempStringsX[j] + " "; // convert back to string
				}
				tempgramS += tempStringsS[0];
			}
			else { // if k == 1 simply use S for c
				tempgramS = wordgramS;
				tempgramX = "";
			}
			String c = tempgramS;
			toWrite += " " + c;
			numWords++;
			wordgramX = tempgramX + c + " ";
		}
		int len = toWrite.length();
		// add a period at the end if there's no punctuation already
		if (!(toWrite.substring(len-1,len).equals("?")) && !(toWrite.substring(len-1,len).equals("!"))) { 
			toWrite += ".";
		}
		if (toWrite.substring(len-1,len).equals(";") || toWrite.substring(len-1,len).equals(",")) {
			toWrite = toWrite.substring(0, len-1) + ".";
		}
		System.out.println(toWrite);
		if (write) { // whether we write to a file or not
			out.println(toWrite + "\n");
			out.close();
		}
		System.out.println("\n\n");
	}
}