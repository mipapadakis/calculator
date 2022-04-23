/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce325.hw2;

/**
 *
 * @author minas
 */
public class Node{
    private Node parent, left, right;
    private String label;
	
	//Extra fields used by our methods:
	private String expression; //Store the expression from this node's subtree
	private double value; //Store the result of the expression from this node's subtree
	private int height;
	private int priority; //priority=1 for {+,-}, priority=2 for {*,x,/}, priority=3 for {^}.
    
    public Node(String label, Node parent, Node leftChild, Node rightChild){
        this.parent = parent;
        left = leftChild;
        right = rightChild;
        this.label = label;
    }
    
    public Node(String label){
        this.parent = null;
        left = null;
        right = null;
        this.label = label;
		CreateTree(this, null, null);
    }
    
    public Node(){
        this.parent = null;
        left = null;
        right = null;
        this.label = null;
    }
    
    public void setLabel(String label){
        this.label = label;
    }
    public void setParent(Node p){
		parent = p;
    }
    public void setLeft(Node child){
		left = child;
    }
    public void setRight(Node child){
		right = child;
    }
	public void setPriority(int priority){
		this.priority = priority;
    }
    
    public String getLabel(){
        return label;
    }
    public Node getParent(){
        return parent;
    }
    public Node getLeft(){
        return left;
    }
    public Node getRight(){
        return right;
    }
	//Given any node of the tree, this method returns the head:
	public Node getHead(){
		Node temp = this;
		while(temp.parent != null){
			temp = temp.parent;
		}
		return(temp);
	}
    
    public String toDotString(){
        StringBuilder dot = new StringBuilder("digraph ArithmeticExpressionTree {\n	label=\"Arithmetic Expression\"\n	");
		Node head = this.getHead();
		dot.append(PreOrderDot(head));
        return(dot.toString().substring(0,dot.toString().length()-1)+ "}\n");
    }
	
	public String PreOrderDot(Node node){ //Uses pre-order algorithm to access the nodes
		StringBuilder dot = new StringBuilder("");
		
		if(node==null){
			return("");
		}
		dot.append( Integer.toString(node.hashCode()));
		dot.append( String.format(" [label = \"%s\", shape=circle, color=black]\n	", node.getLabel()));

		if(node.getLeft()!=null){
			dot.append( Integer.toString(node.hashCode()));
			dot.append(" -> ");
			dot.append(node.getLeft().hashCode());
			dot.append("\n	");
			dot.append(PreOrderDot(node.getLeft()));
		}
		if(node.getRight()!=null){
			dot.append( Integer.toString(node.hashCode()));
			dot.append(" -> ");
			dot.append(node.getRight().hashCode());
			dot.append("\n	");
			dot.append(PreOrderDot(node.getRight()));
		}
		return (dot.toString());
	}
	
	@Override
	public String toString(){
		Node head = this.getHead();
		initialiseHeight(head);
		Node maxNode = findMaxNode(head, head, findMaxHeight(head, 0)); //Find left-most node that has max height
		
		if (head.left==null && head.right==null){
			return (head.label);
		}
		while(true){
			maxNode.height = -1;
			maxNode.parent.right.height = -1;
			
			//initialise all the leafs' expression field
			if(maxNode.left==null && maxNode.right==null ){
				maxNode.expression = maxNode.label;
			}
			if(maxNode.parent.right.left==null || maxNode.parent.right.right==null){
				maxNode.parent.right.expression = maxNode.parent.right.label;
			}
			
			if (maxNode.parent.parent!=null && maxNode.parent.priority<=maxNode.parent.parent.priority){
				maxNode.parent.expression= "("+maxNode.expression + maxNode.parent.label + maxNode.parent.right.expression+")";
			}
			else{
				maxNode.parent.expression=maxNode.expression + maxNode.parent.label + maxNode.parent.right.expression;
			}
			
			maxNode = findMaxNode(head, head, findMaxHeight(head, 0));
			if (maxNode==head){
				break;
			}
		}
		
		return (head.expression);
	}
	
	public double calculate(Node head){
		double leftResult, rightResult;
		initialiseHeight(head);
		Node maxNode = findMaxNode(head, head, findMaxHeight(head, 0)); //Find left-most node that has max height
		
		if (head.left==null && head.right==null){
			return Double.parseDouble(head.label);
		}
		
		while(true){
			if(maxNode.left==null && maxNode.right==null){
				leftResult = Double.parseDouble(maxNode.label);
			}else{
				leftResult = maxNode.value;
			}
			if(maxNode.parent.right.left==null){
				rightResult = Double.parseDouble(maxNode.parent.right.label);
			}else{
				rightResult = maxNode.parent.right.value;
			}
			
			if(maxNode.parent.label.equals("+")){
				maxNode.parent.value = leftResult + rightResult;
				maxNode.height = -1;
				maxNode.parent.right.height = -1;
			}
			if(maxNode.parent.label.equals("-")){
				maxNode.parent.value = leftResult - rightResult;
				maxNode.height = -1;
				maxNode.parent.right.height = -1;
			}
			if(maxNode.parent.label.equals("*") || maxNode.parent.label.equals("x")){
				maxNode.parent.value = leftResult * rightResult;
				maxNode.height = -1;
				maxNode.parent.right.height = -1;
			}
			if(maxNode.parent.label.equals("/")){
				maxNode.parent.value = leftResult / rightResult;
				maxNode.height = -1;
				maxNode.parent.right.height = -1;
			}
			if(maxNode.parent.label.equals("^")){
				maxNode.parent.value = Math.pow( leftResult, rightResult);
				maxNode.height = -1;
				maxNode.parent.right.height = -1;
			}
			
			maxNode = findMaxNode(head, head, findMaxHeight(head, 0));
			if (maxNode==head){
				break;
			}
		}
		return(head.value);
	}
	
	//Creates the tree, starting from the head (provided that the head's "label" field contains the whole expression). Uses methods "IndexOfLastOccurence" and "EnclosedByParentheses".
    public void CreateTree(Node parent, Node left, Node right){
		int highPriority, middlePriority, lowPriority;
		String line = parent.getLabel();
		
		if(EnclosedByParentheses(line)){ //Enters if{} whenever the expression is enclosed by parentheses, for example:		( 4+3*(5+8)^3 + 1 )
			line = line.substring(1); //Remove the first parenthesis
			line = line.substring(0,line.length()-1); //Remove the last parenthesis
			if(line.charAt(0)=='-'){
				line = '0'+line; //If the expression starts with '-', add a zero ('0') character at the start (eg. -5*(8-3) => 0-5*(8-3) )
			}
			else if(line.charAt(0)=='+' && Character.isDigit(line.charAt(1))){
				line = line.substring(1); //If the expression starts with '+', remove it.
			}
			parent.setLabel(line);
			CreateTree(parent, left, right);
		}
		
        lowPriority = IndexOfLastOccurence('+', '-', ' ', line); //c3 = ' ' doesn't affect the result because all spaces have been removed from the string.
		middlePriority = IndexOfLastOccurence('*', 'x', '/', line);
		highPriority = IndexOfLastOccurence('^', ' ', ' ', line);
		
		if(lowPriority>0 || middlePriority>0 || highPriority>0 || EnclosedByParentheses(line)){ //This means that we have more than just a number. Else it simply returns.
			if(lowPriority>0){
				left = new Node();
				right = new Node();
				parent.setLeft(left);
				left.setParent(parent);
				parent.setRight(right);
				right.setParent(parent);
				left.setLabel(line.substring(0,lowPriority)); //Expression left of the lowPriority symbol
				parent.setLabel(line.substring(lowPriority,lowPriority+1)); //Label = the lowPriority symbol
				parent.setPriority(1);
				right.setLabel(line.substring(lowPriority+1)); //Expression right of the lowPriority symbol
				CreateTree(left, null, null);
				CreateTree(right, null, null);
			}
			else if(middlePriority>0){
				left = new Node();
				right = new Node();
				parent.setLeft(left);
				left.setParent(parent);
				parent.setRight(right);
				right.setParent(parent);
				left.setLabel(line.substring(0,middlePriority));
				parent.setLabel(line.substring(middlePriority,middlePriority+1));
				parent.setPriority(2);
				right.setLabel(line.substring(middlePriority+1));
				CreateTree(left, null, null);
				CreateTree(right, null, null);
			}
			else{
				left = new Node();
				right = new Node();
				parent.setLeft(left);
				left.setParent(parent);
				parent.setRight(right);
				right.setParent(parent);
				left.setLabel(line.substring(0,highPriority));
				parent.setLabel(line.substring(highPriority,highPriority+1));
				parent.setPriority(3);
				right.setLabel(line.substring(highPriority+1));
				CreateTree(left, null, null);
				CreateTree(right, null, null);
			}
		}
    }
	
	//This method returns the index of the first occurence of either c1, c2, or c3 characters in the string. If none of these characters exist, return -1.
	private int IndexOfLastOccurence(char c1, char c2, char c3, String line){
		char c;
		int i, avoidParenthesis=0;
		
		//Check each character of the string, individually
        for(i=line.length()-1; i>=0; i--){
            c = line.charAt(i);
            if(c == ')'){
				avoidParenthesis++;
            }
			else if (c == '('){
				avoidParenthesis--;
			}
			
			//This makes sure that we work outside of any parenthesis
			if(avoidParenthesis==0){
				if(c==c1 || c==c2 || c==c3){
					return(i); //Save the index of the first occurence of one of the characters c1 | c2 | c3
				}
			}
        }
		//If none of the characters c1|c2|c3 exist in the string, return -1
		return(-1);
	}
	
	//Gets as a parameter the expression. Returns true if the whole expression is enclosed inside parentheses, like (4+2) or (4+(7*3)*(2+1)^3), etc. Returns false otherwise.
	private boolean EnclosedByParentheses(String line){ 
		int enclosed=1;
		
		if(line.charAt(0)=='('){
			enclosed=0;
		}
		
		//Check each character of the string, individually
        for(int i=0; i<line.length(); i++){
			if(line.charAt(i)=='('){
				enclosed++;
			}
			
			if(line.charAt(i)==')'){
				enclosed--; //enclosed will turn to zero as soon as the first parenthesis (at position 0) closes.
				if(enclosed==0){
					if(i==line.length()-1) //This means that the first parenthesis closes at the last char of the expression => enclosed!
						return(true);
					else
						break; //If the first '(' closes BEFORE the last char of the expression, then the expression has the form of (...)+(...) => so it is not enclosed.
				}
			}
        }
		return(false);
	}
	
	//Initialises the "height" field of all the nodes that belong to the subtree of the parameter "node". So if parameter = head, the method initialises the heights of all the tree's nodes.
	public void initialiseHeight(Node node){
		
		if(node==null){
			return;
		}
		
		if(node.getLeft()!=null){
			node.getLeft().height = node.height+1;
			initialiseHeight(node.getLeft());
		}
		if(node.getRight()!=null){
			node.getRight().height = node.height+1;
			initialiseHeight(node.getRight());
		}
	}
	
	//Returns the maximum height from the nodes that belong to the subtree of the parameter "node".
	public int findMaxHeight(Node node, int max){
		int temp;
		
		if(node==null){
			return 0;
		}
		
		if((node.getLeft()==null && node.getRight()==null ) || node.left.height==-1 || node.right.height==-1){
			return(node.height);
		}
				
		if(node.getLeft()!=null){
			temp = findMaxHeight(node.getLeft(), max);
			if(temp>max){
				max=temp;
			}
		}
		if(node.getRight()!=null){
			temp = findMaxHeight(node.getRight(), max);
			if(temp>max){
				max=temp;
			}
		}
		return(max);
	}
	
	//Returns the left-most node that 1)belongs to the "node" parameter's subtree, and 2)has height equal to the "max" parameter.
	public Node findMaxNode(Node node, Node maxNode, int max){
		
		if(node==null){
			return null;
		}
		
		if( (node.getLeft()==null && node.getRight()==null)|| node.left.height==-1 || node.right.height==-1){
			if( node.height==max )
				return(node);
			else
				return maxNode;
		}
		
		if(node.getRight()!=null){
			maxNode = findMaxNode(node.getRight(), maxNode, max);
		}
		if(node.getLeft()!=null){
			maxNode = findMaxNode(node.getLeft(), maxNode, max);
		}
		
		return(maxNode);
	}
	
}
