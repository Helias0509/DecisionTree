import java.util.Arrays;

/*
 *	Created by Ranjit Kumar Parvathaneni
 *	02/01/2013
 * 
 */

public class ProblemD {

	public static void main(String[] args) throws Exception {
		String inputfile = "";
		String testfile = "";
		if(args.length != 2) {
			System.out.println("Error in the arguments");
		}
		else {
			inputfile = args[0];
			testfile = args[1];
		}
		
		DecisionTree testDt = new DecisionTree();
		int testDtStatus = testDt.readFile(testfile, -1);

		DecisionTree countTree = new DecisionTree();
		int status =  countTree.readFile(inputfile, -1);
		int noOfRows = countTree.root.X.size();
		
		int j = 50;
		if(status > 0 && testDtStatus > 0 && Arrays.equals(countTree.attributePossibleValues, testDt.attributePossibleValues)) {
			while(j <= noOfRows) {
				DecisionTree dt = new DecisionTree();
				dt.readFile(inputfile, j);
				dt.root.depth = 0;
				dt.root.children = null;
				dt.root.selectedAttribute = -1;
				dt.learnTree(dt.root);
				int count = 0;
				int trainingSetSize = testDt.root.X.size();
				for (int i = 0; i < trainingSetSize; i++) {
					DataPoint dp = testDt.root.X.get(i);
					TreeNode node = dt.root;
					while(node.children != null) {
						node = node.children[dp.attributes[node.selectedAttribute]];
					}
					if(node.value == testDt.root.Y.get(i)) count++;
				}
				System.out.println("Accurancy of training set (" + j + ") : " + (double)(count * 100)/trainingSetSize);
				j += 50;
			}
		}
	}
}
