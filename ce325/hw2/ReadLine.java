/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce325.hw2;



import java.io.*;


/**
 *
 * @author minas
 */
public class ReadLine {
    
	public static void main(String []args) {
		java.util.Scanner sc = new java.util.Scanner(System.in);
		System.out.print("Enter math expression: ");
		String line = sc.nextLine();
		line = checkInput(line); //Checks input, exits if any errors were found!
		Node head= new Node(line); //This constructor also generates the tree.
		
		/*The way to create the tree from the main method is:
		Node head = new Node();
		head.setLabel(line);
		head.CreateTree(head,null,null);*/
		
		System.out.println("Expression:\n" + head.toString());
		
		//createPNG(head);
		head.toDotString();
		
		System.out.println("Result: " + Double.toString(head.calculate(head)));
    }
	
	public static void createPNG(Node head){
		try {        
		  PrintWriter pfile = new PrintWriter("ArithmeticExpression.dot");
		  pfile.println(head.toDotString());
		  pfile.close();
		  System.out.println("PRINT DOT FILE OK!");

		  Process p = Runtime.getRuntime().exec("dot -Tpng ArithmeticExpression.dot -o ArithmeticExpression.png");
		  p.waitFor();
		  System.out.println("PRINT PNG FILE OK!");
		} catch(Exception ex) {
		  System.err.println("Unable to write dotString!!!");
		  ex.printStackTrace();
		  System.exit(1);
		}
	}
	
	//Checks the input. If it is valid, it returns the input without whitespace characters. Otherwise, it prints "Wrong input!" and exits the program.
	public static String checkInput(String line){
		int flag = 0, leftParenthesis = 0, rightParenthesis = 0, error = 0;
		char current;
		char prev='0';
		
		//Remove " ", "\t" and "\n" from the string:
		line = line.replaceAll("\\s", ""); //"\s" is any space character
		line = line.replaceAll("\\t", ""); //"\t" is any tab character
		line = line.replaceAll("\\n", ""); //"\n" is any newline character

		if(line.charAt(0)=='-'){
			line = '0'+line; //If the expression starts with '-', add a zero ('0') character at the start (eg. -5*(8-3) => 0-5*(8-3) )
		}
		else if(line.charAt(0)=='+' && Character.isDigit(line.charAt(1))){
			line = line.substring(1); //If the expression starts with '+', remove it.
		}
		else if(line.charAt(0)=='*' || line.charAt(0)=='x' || line.charAt(0)=='/' || line.charAt(0)=='^' || line.charAt(0)=='.'){
			System.out.println("Wrong input!\n");
			System.exit(0);
		}
		else if(line.charAt(line.length()-1)=='*'||line.charAt(line.length()-1)=='x'||line.charAt(line.length()-1)=='/'||line.charAt(line.length()-1)=='^'||line.charAt(line.length()-1)=='.'){
			System.out.println("Wrong input!\n");
			System.exit(0);
		}

		//Check each character of the string, individually
		for(int i=0; i<line.length(); i++){
			current = line.charAt(i);

			//Make sure that the string has only the allowed characters:
			if(current=='('||current==')'||current=='+'||current=='-'||current=='x'||current=='*'||current=='/'||current=='^'||current=='.'||Character.isDigit(current)){

				//Check if there are any adjacent symbols ( like "+-", or "/)", or "(*", etc ):
				if (current=='x' || current=='*' || current=='/' || current=='^' || current=='+' || current=='-' || current=='.'){
					flag++;
					if(flag==2){ //prev char is a symbol
						error=1;
						break;
					}
				}
				else{
				   flag=0; 
				}
				if((prev=='.' && current=='(') || (prev==')' && current=='.') || (prev==')' && current=='(') || (prev=='(' && current==')') ){
						error=1;
						break;
				}
				if (prev=='(' && (current=='x' || current=='*' || current=='/' || current=='^' || current=='.')){ //Note that "(-" and "(+" are allowed
						error=1;
						break;
				}
				if (current==')' && (prev=='x' || prev=='*' || prev=='/' || prev=='^' || prev=='.' || prev=='-' || prev=='+')){
						error=1;
						break;
				}
				
				//Count num of "(" and num of ")" characters:
				if(current=='(')
					leftParenthesis++;
				if(current==')')
					rightParenthesis++;
			}
			else{
				if(current==',')
					System.out.println("Use dot (.) instead of comma (,)\n");
				error=1;
				break;
			}
			prev=current;
		}

		if(error>0 || leftParenthesis != rightParenthesis){
			System.out.println("Wrong input!\n");
			System.exit(0);
		}
		return(line);
	}
}
