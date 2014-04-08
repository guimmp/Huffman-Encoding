import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Creates Huffman Tree based on input file; tree will have a character at its node and will place 
 * more frequent characters at the top of the tree. Less frequent numbers will be put at the bottom. 
 * This tree can be used to compress a file and decompress the compressed file.
 * 
 * Dartmouth CS 10, Winter 2014
 * @author Paul Champeau
 *
 */

public class HuffmanTree extends BinaryTree<TreeElement> implements Comparable<HuffmanTree> {
	public HuffmanTree (TreeElement node){
		super(node);
		this.data = node;
		this.left = null;
		this.right = null;
	}

	//physically creates the tree by reading a txt file
	public static HuffmanTree createTree(String pathName){

		//creates the frequency table of characters and then gets the map
		FrequencyTable table = new FrequencyTable(pathName);
		Map<Character, Integer> frequencyMap = table.getFrequencyMap();
		Set<Character> letters = frequencyMap.keySet();

		//Create a priority queue of TreeElements
		PriorityQueue<HuffmanTree> queue = new PriorityQueue<HuffmanTree>();

		//loop over the entire map for each key and create singleton trees to add to the queue
		for (Character key: letters) {
			TreeElement element = new TreeElement(frequencyMap.get(key), key);
			HuffmanTree tree = new HuffmanTree(element);
			queue.add(tree);
		}

		//growing tree algorithm. for each loop, pop two trees and make a parent tree from them. add this tree back to queue.
		//when only 1 tree is left, return this tree. 
		while (queue.peek() != null) {
			HuffmanTree left = queue.poll();
			if (queue.peek() != null) {
				HuffmanTree right = queue.poll();

				//create Parent tree...
				Character c = new Character((char)(left.data.letter + right.data.letter));
				TreeElement parent = new TreeElement((left.data.getFrequency()+ right.data.getFrequency()), c);
				HuffmanTree parentTree = new HuffmanTree(parent);
				parentTree.left = left;
				parentTree.right = right;
				queue.add(parentTree);
			}
			else return left;
		}

		return null;	//will only happen if there is nothing in queue to begin with
	}

	//creates Map matching each character to its respective binary string.
	public static Map<Character, String> codeRetrieval(HuffmanTree tree){
		Map<Character, String> codeMap = new TreeMap<Character, String>();
		String currSequence = "";
		codeMap = codeRetrievalHelper(tree,currSequence, codeMap);
		return codeMap;
	}

	//helps implement codeRetrieval method. keeps track of previous binary string (path) up to this point
	//and adds a "0" to the string if left node and a "1" if right node.
	public static Map<Character, String> codeRetrievalHelper(HuffmanTree tree, String currSequence, Map<Character, String> codeMap){
		if(tree.isLeaf()){
			codeMap.put(tree.data.letter, currSequence);
		}
		else{
			codeRetrievalHelper((HuffmanTree)tree.left, currSequence + "0", codeMap);
			codeRetrievalHelper((HuffmanTree)tree.right, currSequence + "1", codeMap);
		}
		return codeMap;
	}

	//print method useful for debugging
	public static void print(HuffmanTree tree){
		if(!tree.hasLeft() && !tree.hasRight()) {
			System.out.println(tree.data.letter + ":" + tree.data.frequency);
		}
		else{
			print((HuffmanTree)tree.left);
			print((HuffmanTree)tree.right);
		}
	}

	//compareTo method is implemented by PriorityQueue
	public int compareTo(HuffmanTree o) {
		return (int) Math.signum(this.data.frequency - o.data.frequency);
	}


	//allows user to chose which file to compress/read
	public static String setFilePath() {
		JFileChooser fc = new JFileChooser("."); // start at current directory

		int returnVal = fc.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String pathName = file.getAbsolutePath();
			return pathName;
		}
		else
			return "";
	}

	//creates new compressed file composed of strings of bits
	public static void compress(String pathName, Map<Character, String> codeMap) {
		BufferedReader input = null;
		BufferedBitWriter bitOutput = null;
		try {
			input = new BufferedReader(new FileReader(pathName));
			String substring = (String) pathName.subSequence(0, pathName.lastIndexOf(".txt"));
			bitOutput = new BufferedBitWriter(substring + "_compressed.txt");	//new file will be originalfile_compressed.txt

			//reads each character in file and sets/writes appropriate bits.
			int temp;
			while ((temp = input.read()) != -1) {
				Character letter = new Character((char)temp);
				String path = codeMap.get(letter);
				for (int i = 0; i < path.length(); i++) {
					String pathPiece = path.substring(i, i+1);
					int bit;
					if (pathPiece.equals("0")){
						bit = 0; }
					else{
						bit = 1;
					}
					bitOutput.writeBit(bit);
				}
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//finally, try to close files you are reading and writing
		finally {
			try {
				input.close();
				bitOutput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//decompresses the compressed file we wrote previously
	public static void decompress(String pathName, HuffmanTree tree) {
		BufferedBitReader bitIn = null;
		BufferedWriter out = null;
		try {
			bitIn = new BufferedBitReader(pathName);
			String substring = (String) pathName.subSequence(0, pathName.lastIndexOf("_compressed.txt"));
			out = new BufferedWriter(new FileWriter(substring + "_decompressed.txt"));

			//go through strings of bits and use HuffmanTree previously created to determine what character is coded.
			int temp;
			HuffmanTree node = tree;
			while ((temp = bitIn.readBit()) != -1) {
				String msg = String.valueOf(temp);

				//iterates over string and moves down tree to the left if bit is a 0 and right if bit is a 1.
				for (int i = 0; i < msg.length(); i++) {
					String msgPiece = msg.substring(i, i+1);
					if (msgPiece.equals("0") && node.hasLeft()) {
						node = (HuffmanTree) node.left; 
					}
					else if (msgPiece.equals("1") && node.hasRight()) {
						node = (HuffmanTree) node.right;
					}

					if (!node.hasLeft() && !node.hasRight()) {	//if node is a leaf...
						char letter = node.data.letter;
						out.write(letter);
						node = tree;
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//close files if possible...
		finally {
			try {
				bitIn.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		String pathName = setFilePath();
		HuffmanTree tree = createTree(pathName);
		Map<Character, String> codeMap = codeRetrieval(tree);

		compress(pathName, codeMap);

		//set file to be compressed file
		String substring = (String) pathName.subSequence(0, pathName.lastIndexOf(".txt"));
		pathName = substring + "_compressed.txt";

		decompress(pathName, tree);
	} 
}
