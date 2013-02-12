/*
 *	Created by Ranjit Kumar Parvathaneni
 *	02/01/2013
 * 
 */

import java.io.*;
import java.util.*;

public class DecisionTree {

	int numAttributes;
	int[] attributePossibleValues;
	Vector<DataPoint>[] X;
	Vector<Integer> Y;
	static final int classValues = 2;

	TreeNode root = new TreeNode();

	public int readFile(String filename, int size) throws Exception {
		FileInputStream in = null;
		try {
			File inputFile = new File(filename);
			in = new FileInputStream(inputFile);
		} catch ( Exception e) {
			System.err.println( "Unable to open data file: " + filename + "n" + e);
			return 0;
		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in) );
		String input;

		//get the no of attributes and their possible values
		while(true) {
			input = bin.readLine();
			if (input == null) {
				System.err.println( "No data found in the data file: " + filename + "n");
				return 0;
			}

			if (input.startsWith("//")) continue;
			if (input.equals("")) continue;
			break;
		}

		StringTokenizer tokenizer = new StringTokenizer(input);
		numAttributes = tokenizer.countTokens() / 2; //because we have attribute name and possible values in the first line. Two values for each attribute

		if (numAttributes <= 1) {
			System.err.println( "Expecting at least one input attribute with attribute values");
			return 0;
		}

		X = new Vector[numAttributes];

		attributePossibleValues = new int[numAttributes];

		int k = 0;
		while(tokenizer.hasMoreTokens()) {
			String name = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			attributePossibleValues[k] = Integer.parseInt(value);
			k++;
		}

		//read the datapoints
		while(true) {
			input = bin.readLine();
			if (input == null) break;
			if (input.startsWith("//")) continue;
			if (input.equals("")) continue;
			tokenizer = new StringTokenizer(input);
			int numtokens = tokenizer.countTokens();
			if (numtokens != numAttributes + 1) {
				System.err.println( "Read " + root.X.size() + " data");
				System.err.println( "Last line read: " + input);
				System.err.println( "Expecting " + numAttributes + " attributes");
				return 0;
			}

			DataPoint point = new DataPoint(numAttributes);

			for (int i = 0; i < numAttributes; i++) {
				root.unProcessedAttributes.add(i);
				int nextToken = Integer.parseInt(tokenizer.nextToken());
				point.attributes[i] = filename.matches(".*(-)[0-9].*") ? nextToken - 1 : nextToken;
			}

			root.X.addElement(point);
			root.Y.addElement(Integer.parseInt(tokenizer.nextToken()));
			
			if(size > 0 && root.X.size() == size) break;
		}
		bin.close();
		return 1;
	}

	//get the count of labels in Y
	public int getCount(Vector<Integer> Y, int y) {
		int count = 0;
		for(int i : Y) {
			if(i == y)
				count++;
		}
		return count;
	}

	//Calculate Entropy which is sigma(-plog(p))
	public double calculateEntropy(TreeNode node) {
		int total = node.Y.size();
		double entropy = 0;
		for(int i = 0; i < classValues; i++) {
			double probability = (double) getCount(node.Y, i) / total;
			entropy += -probability*(Math.log(probability)/Math.log(2));
		}
		return entropy;
	}

	//Calculate the attribute which has Maximum Information Gain I(Y|C) = E(C) - E(Y|X)
	//which is equivalent calculating Minimum of Relative Entropy E(Y|X) because E(C) is same for all the attributes
	public int calculateSelectedAttribute(TreeNode node) {
		int selectedAttribute = -1;
		double minRelativeEntropy = 999999;
		for(int i : node.unProcessedAttributes) {
			int total = node.Y.size();
			int count[][] = new int[attributePossibleValues[i]][classValues];
			for(int j = 0; j < total; j++) {
				count[node.X.get(j).attributes[i]][node.Y.get(j)]++;
			}

			double relativeEntropy = 0;
			for(int k = 0; k < attributePossibleValues[i]; k++) {
				double localEntropy = 0;
				int subTotal = 0;
				for(int l = 0; l < classValues; l++) {
					subTotal += count[k][l];
				}
				for(int l = 0; l < classValues; l++) {
					if(subTotal != 0 && count[k][l] != 0) {
						double probability = (double) count[k][l]/subTotal;
						localEntropy += -probability*(Math.log(probability)/Math.log(2));
					}
				}
				relativeEntropy += ((double)subTotal/total)*localEntropy;
				
			}
			if(minRelativeEntropy > relativeEntropy) {
				minRelativeEntropy = relativeEntropy;
				selectedAttribute = i;
			}
		}
		return selectedAttribute;
	}

	public void learnTree(TreeNode node) {
		//If X set has same class
		int value = -1;
		for(int i = 0; i < classValues; i++) {
			if(node.Y.size() == getCount(node.Y, i)) {
				value = i;
				break;
			}
		}
		if(value >= 0) {
			node.value =  value;
			node.children = null;
			return;
		}

		//If datapoints are all equal in X or there are no more attributes to be processed, then we must return the class that dominates
		boolean same = true;
		for(int i = 0; i < node.X.size() - 1; i++) {
			if(node.X.get(i) != node.X.get(i + 1)) {
				same = false;
				break;
			}
		}
		if(same || node.unProcessedAttributes.isEmpty()) {
			int best = 0;
			int bestValue = 0;
			for(int i = 0; i < classValues; i++) {
				int count = getCount(node.Y, i);
				if(best < count) {
					best = count;
					bestValue = i;
				}
			}
			node.value = bestValue;
			node.children = null;
			return;
		}

		int selectedAttribute = calculateSelectedAttribute(node);
		int numvalues = attributePossibleValues[selectedAttribute];
		node.selectedAttribute = selectedAttribute;
		node.children = new TreeNode [numvalues];
		for (int j=0; j< numvalues; j++) {
			node.children[j] = new TreeNode();
			node.children[j].parent = node;
			node.children[j].depth = node.depth + 1;
			node.children[j].selectedAttributeValue = j;
			node.children[j].unProcessedAttributes = new HashSet<Integer>(node.unProcessedAttributes);
			node.children[j].unProcessedAttributes.remove(selectedAttribute);
			node.children[j].X = getSubsetX(node.X, selectedAttribute, j);
			node.children[j].Y = getSubsetY(node.X, node.Y, selectedAttribute, j);
		}
		for (int j=0; j< numvalues; j++) {
			learnTree(node.children[j]);
		}
		node.X = null;
	}

	//Print the tree
	public void printTree(TreeNode node) {
		if(node == null) return;
		//for root we have all data with out any division
		if(node.parent != null) {
			for(int i = 0; i < node.depth - 1; i++)
				System.out.print("| ");
			System.out.print("attr" + "" + node.parent.selectedAttribute + " = " + node.selectedAttributeValue + " : ");
		}
		//procedure further if it has children. We are performing DFS on this tree.
		if(node.children != null) {
			//do not print a line for root
			if(node.parent != null)
				System.out.println();
			for(TreeNode t : node.children) 
				printTree(t);
		}
		else 
			System.out.println(node.value);
	}

	//Distribute X of a parent to its children
	public Vector<DataPoint> getSubsetX(Vector<DataPoint> data, int attribute, int value) {
		Vector<DataPoint> subset = new Vector<DataPoint>();
		int num = data.size();
		for (int i=0; i< num; i++) {
			DataPoint point = (DataPoint)data.elementAt(i);
			if (point.attributes[attribute] == value) subset.addElement(point);
		}
		return subset;
	}

	//Distribute Y of a parent to its children
	public Vector<Integer> getSubsetY(Vector<DataPoint> data, Vector<Integer> Y, int attribute, int value) {
		Vector<Integer> subset = new Vector<Integer>();
		int num = data.size();
		for (int i=0; i< num; i++) {
			DataPoint point = (DataPoint)data.elementAt(i);
			if (point.attributes[attribute] == value) subset.addElement(Y.get(i));
		}
		return subset;
	}
	
	//check accurancy
	

}
