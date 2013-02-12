import java.util.*;

/*
 *	Created by Ranjit Kumar Parvathaneni
 *	02/01/2013
 * 
 */

public class ProblemBnC {

	public static void main(String[] args) throws Exception {
		String inputfile = "";
		String testfile = "";
		if(args.length != 2) {
			System.out.println("Error in the arguments");
			System.exit(0);
		}
		else {
			inputfile = args[0];
			testfile = args[1];
		}

		DecisionTree dt = new DecisionTree();
		int status =  dt.readFile(inputfile, -1);
		if(status > 0) {
			dt.root.depth = 0;
			dt.learnTree(dt.root);
		}
		
		DecisionTree testDt = new DecisionTree();
		int testDtStatus = testDt.readFile(testfile, -1);
		if(testDtStatus > 0 && Arrays.equals(dt.attributePossibleValues, testDt.attributePossibleValues)) {
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
			
			if(inputfile.equals(testfile))
				System.out.println("Accurancy of training set (" + trainingSetSize + ") : " + (double)(count * 100)/trainingSetSize);
			else 
				System.out.println("Accurancy of test set (" + trainingSetSize + ") : " + (double)(count * 100)/trainingSetSize);
		}
	}
}
