
/**
 * This class creates the elements which we will use for our huffman encoding tree
 * @author Paul Champeau and Matthew Ginsberg
 *
 */
public class TreeElement {
	public int frequency; //integer to store the frequency of the variable
	public char letter;	//character to store the actual letter
	
	/**
	 * Basic constructor for a new TreeElement
	 * @param num
	 * @param character
	 */
	public TreeElement(int num, char character){
		this.frequency = num;
		this.letter = character;
	}

	//Setter and Getter methods for both instance variables
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	public String toString(){
		String blank = " ";
		System.out.println(this.letter +  ":" +  this.frequency);
		return blank;
	}
	
}

