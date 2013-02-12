/*
 *	Created by Ranjit Kumar Parvathaneni
 *	02/01/2013 
 */

public class ProblemA {

	public static void main(String[] args) throws Exception {
		String inputfile = "";
		String testfile = "";
		if(args.length != 1) {
			System.out.println("Error in the arguments");
		}
		else {
			inputfile = args[0];
		}

		DecisionTree dt = new DecisionTree();
		int status =  dt.readFile(inputfile, -1);
		if(status > 0) {
			dt.root.depth = 0;
			dt.learnTree(dt.root);
			dt.printTree(dt.root);
		}
	}
}
