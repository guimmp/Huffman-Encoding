import java.io.*;
import java.util.*;

import javax.swing.*;
/**
 * This class creates a frequency map from a text document
 * @author Paul Champeau and Matthew Ginsberg
 *
 */

public class FrequencyTable {
	private Map<Character, Integer> frequencyMap;

	public FrequencyTable(String pathName){
		this.frequencyMap = new TreeMap<Character, Integer>();
		//String pathName = getFilePath();
		try {
			frequencyMap = MakeTable(pathName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("File Not Found");
		}
	}
	/**
	 * This method constructs a frequency table from the .txt document selected in getFilePath()
	 * @param pathName
	 * @return
	 * @throws Exception
	 */
	public Map<Character, Integer> MakeTable(String pathName) throws Exception{
		//Create temporary storage variables
		Integer frequency = new Integer(0);
		Integer one = new Integer(1);
		int temp;
		
		BufferedReader input = new BufferedReader(new FileReader(pathName));

		while((temp = input.read()) != -1){ //while the file still has characters to read...

			Character letter = new Character((char)temp);
			
			//if the letter is already in the map then increment the frequency
			if(frequencyMap.containsKey(letter)) {
				frequency = frequencyMap.get(letter);
				frequency = frequency.intValue() + 1;
				frequencyMap.put(letter, frequency);
			}
			//otherwise add it to the character to the map
			else{
				frequencyMap.put(letter, one);

			}
		}
		//Print out all the entries in the map
		//Set<Character> letters = frequencyMap.keySet();
		//for (Character key: letters) {
		//	System.out.println(key + ": " + frequencyMap.get(key));}
		input.close();
		return frequencyMap;
	}

	public Map<Character, Integer> getFrequencyMap() {
		return frequencyMap;
	}
	
	public void setFrequencyMap(Map<Character, Integer> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}
	
	public static void main(String[] args, String pathName) {
		// TODO Auto-generated method stub
		FrequencyTable test = new FrequencyTable(pathName);
		Character tester = new Character('a');
		System.out.println(test.frequencyMap.get(tester));

	}

}
