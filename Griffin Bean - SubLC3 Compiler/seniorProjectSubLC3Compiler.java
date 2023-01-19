// -------------------------------------------------------------------------------------------------------------------------
// Griffin Bean
// CSCI 4950, Senior Project
// SubLC3 Compiler in JAVA
// April 28th, 2019
// -------------------------------------------------------------------------------------------------------------------------
package seniorProjectSubLC3Compiler;

import java.util.ArrayList;
import java.util.Scanner;

import seniorProjectSubLC3Compiler.seniorProjectSubLC3Compiler;

import java.io.*;

public class seniorProjectSubLC3Compiler {
	final static seniorProjectSubLC3Compiler SubLC3VM = new seniorProjectSubLC3Compiler();
	final static String FILENAME = "src\\seniorProjectSubLC3Compiler\\mySubLC3.txt";
	final static int MAX_MEMORY_SIZE = 500; // Sets the maximum number of lines the SubLC3 program can be
	final static int MAX_LEXEME_LEN = 100; // Sets the maximum length for an individual identifier name of integer length
	static ArrayList<Character> statement = new ArrayList<Character>();
	static ArrayList<Variable> variables = new ArrayList<Variable>(); // Data structure to keep track of declared variables
	static ArrayList<String> labels = new ArrayList<String>(); // Data structure to keep track of declared labels
	static ArrayList<String> semanticLine = new ArrayList<String>();
	static ArrayList<String> lines = new ArrayList<String>();
	static String [] instructions = new String [MAX_MEMORY_SIZE]; // Data structure where the instructions are loaded
	static char [] lexeme = new char [MAX_LEXEME_LEN];
	static String readLine = "";
	static String charClass;
	static String nextToken;
	static String line;
	static char nextChar;
	static int quoteCount;
	static int progCount;
	static int lexLen;
	static int i, j;
	static Scanner fileScan, lineScan;
	
	public static void main(String[] args) throws IOException {
		try {
			fileScan = new Scanner (new File(FILENAME));
			/*
			 * Scan through the input file and loads the instructions into the lines Arraylist 
			 * before sending to the Lexical Analyzer
			 */
			while (fileScan.hasNext()) {
				line = fileScan.nextLine();
				lineScan = new Scanner(line);
				readLine = lineScan.next() + " ";
				while (lineScan.hasNext())
					readLine += lineScan.next() + " "; // End lineScan
				readLine = readLine.substring(0, readLine.length()-1);
				if (readLine.charAt(0) != ';')
					lines.add(readLine);
				// Throws error if MAX_MEMORY_SIZE is exceeded
				if (lines.size() > MAX_MEMORY_SIZE) {
					System.out.println("--------------------------------------------------");
					error(15);
				} // End if
			} // End fileScan
			// Throws error if the file is empty
			if (lines.size() == 0) {
				System.out.println("--------------------------------------------------");
				error(17);
			} // End if
			// Runs Lexical Analyzer and Parser on each line of the lines Arraylist
			for (int x = 0; x < lines.size(); x++) {
				i = 0;
				getChar();
				do {
					lex();
					start();
				} while (nextToken != "END_OF_FILE"); // End do/while
				j++;
			} // End for
			i = 0;
			j = 0;
			/* 
			 * Populates the semanticLine Arraylist with individual items of a given line of code. 
			 * Clears and restarts semantic analysis for each line
			 */
			for (int x = 0; x < lines.size(); x++) {
				semanticLine.clear();
				line = lines.get(x);
				lineScan = new Scanner(line);
				while (lineScan.hasNext())
					semanticLine.add(lineScan.next()); // End lineScan
				semanticAnalyze(semanticLine);
				j++;
			} // End for
			i = 0;
			j = 0;
			Scanner fileScan, lineScan;
			String line;
			String readLine = "";
			fileScan = new Scanner (new File(FILENAME));
			int i = 0;
			/* 
			 * After the code has been verified to be lexical-, syntax-, and semantic-error free, 
			 * then each line is populated into the instructions array for decoding and execution
			 */
			while (fileScan.hasNext()) {
				line = fileScan.nextLine();
				lineScan = new Scanner(line);
				readLine = lineScan.next();
				while (lineScan.hasNext())
					readLine += " " + lineScan.next(); // End lineScan
				instructions[i] = readLine;
				i++;
			} // End fileScan
			fileScan.close(); // End local fileScan
			/*
			 * Initializes the program counter, sets the current instruction to the one
			 * pointed to by the program counter, increments the program counter,
			 * and send the instruction off to be decoded and executed
			 */
			String instruction = "";
			progCount = 0; 
			while (!instruction.equals("HALT")) {
				instruction = instructions[progCount];
				progCount++;
				decodeAndExecute(instruction);
				j++;
			} // End while
		} // End try block
		// Catches file-not-found exceptions
		catch (IOException e) {
			System.out.println("--------------------------------------------------");
			error(1);
		} // End catch block
		lineScan.close(); // End global lineScan
		fileScan.close(); // End global fileScan
	}
	
	// -------------------------------------------------------------------------------------------------------------------------
	// Lexical Analysis Methods
	// -------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Lookup method for non-alphanumeric characters, specifically '"', ' ', and '_'. 
	 * All other characters throw an error unless they have been surrounded by quotation marks
	 */
	private static String lookup (char ch) {
		if (ch == '"') {
			addChar();
			quoteCount++;
			if (quoteCount == 1 || quoteCount == 2)
				nextToken = "QUOTE";
			else
				nextToken = "QUOTED";
			if (quoteCount == 2)
				quoteCount = 0;
		} // End if
		else if (ch == ' ') {
			addChar();
			if (quoteCount != 1)
				nextToken = "SPACE";
			else
				nextToken = "QUOTED";
		} // End else if
		else if (ch == '_')
			addChar();
		else if (ch != '"' || ch != ' ' || ch != '_') {
			addChar();
			if (quoteCount != 1) {
				nextToken = "NON_ALPHANUM";
				System.out.println("--------------------------------------------------");
				System.out.println(lines.get(j));
				System.out.println(ch);
				error(2);
			} // End if
			else
				nextToken = "QUOTED";
		} // End else if
		else {
			addChar();
			nextToken = "END_OF_FILE";
			lexeme[0] = 'E';
			lexeme[0] = 'O';
			lexeme[0] = 'F';
		} // End else
		return nextToken;
	}

	/*
	 * Adds a character to the lexeme array, or prints an error if the lexeme is too long
	 */
	private static void addChar() {
		if (lexLen <= MAX_LEXEME_LEN - 2) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		} // End if
		else {
			System.out.println("--------------------------------------------------");
			System.out.println(lines.get(j));
			System.out.println(toString(lexeme));
			error(3);
		} // End else
	}
	
	/*
	 * Retrieves the characters of each line from the file incrementally, then assigns an appropriate charClass
	 */
	private static void getChar()
	{
		if (i != lines.get(j).length()) {
			nextChar = lines.get(j).charAt(i);
			if (isalpha(nextChar))
				charClass = "LETTER";
			else if (isdigit(nextChar))
				charClass = "DIGIT";
			else if (nextChar == '_')
				charClass = "UNDERSCORE";
			else
				charClass = "UNKNOWN";
			i++;
		} // End if
		else
			charClass = "EOF";
	}
	
	/*
	 * Lexical Analyzer, constructs the lexemes as wholes, and identifies the appropriate token to display
	 */
	private static String lex() {
		lexLen = 0;
		switch (charClass) {
		// Builds an identifier based on consecutive letters or digits
		case "LETTER":
			addChar();
			getChar();
			while (charClass == "LETTER" || charClass == "DIGIT" || charClass == "UNDERSCORE") {
				addChar();
				getChar();
			} // End while
			if (quoteCount != 1)
				nextToken = "IDENTIFIER";
			else
				nextToken = "QUOTED";
			break;
		// Builds a literal integer based on consecutive digits
		case "DIGIT":
			addChar();
			getChar();
			while (charClass == "DIGIT") {
				addChar();
				getChar();
			} // End while
			if (quoteCount != 1)
				nextToken = "INT_LIT";
			else
				nextToken = "QUOTED";
			break;
		// For characters that are not letters or digits, sends to lookup() for valid symbols
		case "UNKNOWN":
			lookup(nextChar);
			getChar();
			break;
		case "EOF":
			nextToken = "END_OF_FILE";
			lexeme[0] = 'E';
			lexeme[1] = 'O';
			lexeme[2] = 'F';
			break;
		} // End switch
		return nextToken;
	}
	
	/*
	 * Turns the lexeme array into a printable String
	 */
	private static String toString(char [] ch) {
		String newLexeme = "";
		for (int i = 0; i < lexLen; i++)
			newLexeme += lexeme[i];
		return newLexeme;
	}
	
	/*
	 * Checks if a character is an letter
	 */
	private static boolean isalpha (char ch) {
		if (ch >= 65 && ch <= 90 || ch >= 97 && ch <= 122)
			return true;
		else
			return false;
	}
	
	/*
	 * Checks if a character is a digit
	 */
	private static boolean isdigit (char ch) {
		if (ch >= 48 && ch <= 57)
			return true;
		else
			return false;
	}
	
	// -------------------------------------------------------------------------------------------------------------------------
	// Recursive-descent Parsing Methods
	// -------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Used as a starting symbol for syntax analysis, lines must start with an identifier 
	 * (or command in this case) and be followed by a space, or nothing in the case of labels
	 */
	private static void start() {
		if (nextToken.equals("IDENTIFIER")) {
			lex();
			if (nextToken.equals("SPACE")) {
				lex();
				if (nextToken.equals("IDENTIFIER") || nextToken.equals("INT_LIT"))
					command();
				else if (nextToken.equals("QUOTE"))
					quote();
				else {
					System.out.println("--------------------------------------------------");
					System.out.println(lines.get(j));
					error(4);
				} // End else
			} // End if
			else
				label();
		} // End if
		else {
			System.out.println("--------------------------------------------------");
			System.out.println(lines.get(j));
			error(5);
		} // End else
	}
	
	/*
	 * Placeholder for a label
	 */
	private static void label() {
		lex();
	}
	
	/*
	 * Counts as the next identifier item in the line of code, which can be an identifier, or integer based on the command
	 */
	private static void command() {
		lex();
		if (nextToken.equals("SPACE")) {
			lex();
			if (nextToken.equals("IDENTIFIER") || nextToken.equals("INT_LIT"))
				parameters();
			else {
				System.out.println("--------------------------------------------------");
				System.out.println(lines.get(j));
				error(6);
			} // End else
		} // End if
		else
			singleParam();
	}
	
	/*
	 * Placeholder for lines only containing one parameter
	 */
	private static void singleParam() {
		lex();
	}
	
	/*
	 * Method for analyzing quotations in the code. Throws error if quotations or quotation marks aren't closed
	 */
	private static void quote() {
		lex();
		if (nextToken.equals("QUOTED")) {
			while(nextToken.equals("QUOTED"))
				lex();
			if (nextToken.equals("QUOTE"))
				lex();
			else {
				System.out.println("--------------------------------------------------");
				System.out.println(lines.get(j));
				error(7);
			} // End else
		} // End if
		else {
			System.out.println("--------------------------------------------------");
			System.out.println(lines.get(j));
			error(8);
		} // End else
	}
	
	/*
	 * Counts as the next identifier item in the line of code, which can be an identifier, or integer based on the command
	 */
	private static void parameters() {
		lex();
		if (nextToken.equals("SPACE")) {
			lex();
			if (nextToken.equals("IDENTIFIER") || nextToken.equals("INT_LIT"))
				values();
			else {
				System.out.println("--------------------------------------------------");
				System.out.println(lines.get(j));
				error(9);
			} // End else
		} // End if
		else
			doubleParam();
	}
	
	/*
	 * Placeholder for lines with two parameters
	 */
	private static void doubleParam() {
		lex();
	}
	
	/*
	 * Placeholder for the last identifier in the line of code, there shouldn't be any more after the third parameter
	 */
	private static void values() {
		lex();
		tripleParam();
	}
	
	/*
	 * Placeholder for lines with three parameters.
	 */
	private static void tripleParam() {
		lex();
	}
	
	// -------------------------------------------------------------------------------------------------------------------------
	// Semantic Analysis Method
	// -------------------------------------------------------------------------------------------------------------------------

	/*
	 * Checks if the proper number of parameters or identifiers is correct based on the 
	 * command given. Checks if the proper parameters are not literal integers
	 */
	private static void semanticAnalyze(ArrayList<String> s) {
		switch (s.get(0)) {
			case "OUT":
				String temp1 = s.get(0);
				String temp2 = "";
				if (s.size() > 2) {
					for (int i = 1; i < s.size(); i++) {
						if (i == s.size()-1)
							temp2 += s.get(i);
						else
							temp2 += s.get(i) + " ";
					} // End for
					s.clear();
					s.add(temp1);
					s.add(temp2);
				} // End if
				break;
			case "IN":
			case "JMP":
				if (s.size() != 2) {
					System.out.println("--------------------------------------------------");
					System.out.println(lines.get(j));
					error(10);
				} // End if
				break;
			case "STO":
			case "BRn":
			case "BRp":
			case "BRz":
			case "BRzn":
			case "BRzp":
				if (s.size() != 3) {
					System.out.println("--------------------------------------------------");
					System.out.println(lines.get(j));
					error(10);
				} // End if
				break;
			case "MUL":
			case "DIV":
			case "ADD":
			case "SUB":
				if (s.size() != 4) {
					System.out.println("--------------------------------------------------");
					System.out.println(lines.get(j));
					error(10);
				} // End if
				break;
			case "HALT":
				break;
			default:
				if (s.size() > 1) {
					System.out.println("--------------------------------------------------");
					System.out.println(lines.get(j));
					error(0);
				} // End if
				break;
		} // End switch
		try {
			Integer.parseInt(s.get(0));
			System.out.println("--------------------------------------------------");
			System.out.println(lines.get(j));
			error(5);
		} // End try block
		catch (NumberFormatException e) {
			try {
				Integer.parseInt(s.get(0).charAt(0) + "");
				if (s.get(0).charAt(0) == '_') {
					System.out.println("--------------------------------------------------");
					System.out.println(lines.get(j));
					error(11);
				} // End if
				System.out.println("--------------------------------------------------");
				System.out.println(lines.get(j));
				error(12);
			} // End try block
			catch (NumberFormatException f) {
				switch (s.size()) {
					case 1:
						labels.add(s.get(0));
						break;
					case 2:
						try {
							Integer.parseInt(s.get(1));
							System.out.println("--------------------------------------------------");
							System.out.println(lines.get(j));
							error(4);
						} // End try block
						catch (NumberFormatException g) {
							try {
								Integer.parseInt(s.get(1).charAt(0) + "");
								if (s.get(1).charAt(0) == '_') {
									System.out.println("--------------------------------------------------");
									System.out.println(lines.get(j));
									error(11);
								} // End if
								System.out.println("--------------------------------------------------");
								System.out.println(lines.get(j));
								error(12);
							} // End try block
							catch (NumberFormatException h){
							}
						} // End catch block
						break;
					case 3:
						try {
							Integer.parseInt(s.get(1));
							System.out.println("--------------------------------------------------");
							System.out.println(lines.get(j));
							error(4);
						} // End try block
						catch (NumberFormatException g) {
							try {
								Integer.parseInt(s.get(1).charAt(0) + "");
								if (s.get(1).charAt(0) == '_') {
									System.out.println("--------------------------------------------------");
									System.out.println(lines.get(j));
									error(11);
								} // End if
								System.out.println("--------------------------------------------------");
								System.out.println(lines.get(j));
								error(12);
							} // End try block
							catch (NumberFormatException h) {
								if (!s.get(0).equals("STO")) {
									try {
										Integer.parseInt(s.get(2));
										System.out.println("--------------------------------------------------");
										System.out.println(lines.get(j));
										error(4);
									} // End try block
									catch (NumberFormatException i) {
										try {
											Integer.parseInt(s.get(2).charAt(0) + "");
											if (s.get(2).charAt(0) == '_') {
												System.out.println("--------------------------------------------------");
												System.out.println(lines.get(j));
												error(11);
											} // End if
											System.out.println("--------------------------------------------------");
											System.out.println(lines.get(j));
											error(12);
										} // End try block
										catch (NumberFormatException j) {
										}
									} // End catch block
								} // End if
								else {
									try {
										Integer.parseInt(s.get(2));
									}
									catch (NumberFormatException k) {
										try {
											Integer.parseInt(s.get(2).charAt(0) + "");
											if (s.get(2).charAt(0) == '_') {
												System.out.println("--------------------------------------------------");
												System.out.println(lines.get(j));
												error(11);
											} // End if
											System.out.println("--------------------------------------------------");
											System.out.println(lines.get(j));
											error(12);
										} // End try block
										catch (NumberFormatException j) {
										}
									} // End catch block
								} // End else
							} // End catch block
						} // End catch block
						break;
					case 4:
						try {
							Integer.parseInt(s.get(1));
							System.out.println("--------------------------------------------------");
							System.out.println(lines.get(j));
							error(4);
						} // End try block
						catch (NumberFormatException g) {
							try {
								Integer.parseInt(s.get(1).charAt(0) + "");
								if (s.get(1).charAt(0) == '_') {
									System.out.println("--------------------------------------------------");
									System.out.println(lines.get(j));
									error(11);
								} // End if
								System.out.println("--------------------------------------------------");
								System.out.println(lines.get(j));
								error(12);
							} // End try block
							catch (NumberFormatException h) {
								try {
									Integer.parseInt(s.get(2));
								}
								catch (NumberFormatException k) {
									try {
										Integer.parseInt(s.get(2).charAt(0) + "");
										if (s.get(2).charAt(0) == '_') {
											System.out.println("--------------------------------------------------");
											System.out.println(lines.get(j));
											error(11);
										} // End if
										System.out.println("--------------------------------------------------");
										System.out.println(lines.get(j));
										error(12);
									} // End try block
									catch (NumberFormatException j) {
									}
								} // End catch block
								try {
									Integer.parseInt(s.get(3));
								}
								catch (NumberFormatException l) {
									try {
										Integer.parseInt(s.get(3).charAt(0) + "");
										if (s.get(3).charAt(0) == '_') {
											System.out.println("--------------------------------------------------");
											System.out.println(lines.get(j));
											error(11);
										} // End if
										System.out.println("--------------------------------------------------");
										System.out.println(lines.get(j));
										error(12);
									} // End try block
									catch (NumberFormatException m) {
									}
								} // End catch block
							} // End catch block
						} // End catch block
				} // End switch
			} // End catch block
		}// End catch block
	}
	
	// -------------------------------------------------------------------------------------------------------------------------
	// Virtual Machine Methods
	// -------------------------------------------------------------------------------------------------------------------------

	/*
	 * Takes in a string and breaks it up into elements delimited by a " ", 
	 * inserting each item into and Arraylist (statement), which is processed through. 
	 * The value of statement.get(0) determines what form of execution takes place
	 */
	public static void decodeAndExecute(String instruction) {
		boolean created = false;
		int x = 0;
		ArrayList<String> statement = new ArrayList<String>();
		Scanner stringScan = new Scanner(instruction);
		String var1 = "", var2 = "";
		while (stringScan.hasNext())
			statement.add(stringScan.next()); // end stringScan
		switch (statement.get(0)) {
			/* 
			 * case for when an add operation needs to be performed, try/catches throughout 
			 * this switch govern whether an integer literal needs to be parsed, or if a 
			 * variable has to be searched for in the variables Arraylist
			 */
			case "ADD":
				created = false;
				x = 0;
				try {
					int addValue1 = Integer.parseInt(statement.get(2));
					try {
						int addValue2 = Integer.parseInt(statement.get(3));
						// If both values have been parsed, run ADD on two integer literals
						addition(statement.get(1), addValue1, addValue2);
					} // End try block
					catch (NumberFormatException e) {
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								// If only the first value can be parsed, run ADD on an integer literal and a variable
								created = true;
								addition(statement.get(1), addValue1, statement.get(3));
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End catch block
				} // End try block
				catch (NumberFormatException e) {
					created = false;
					try {
						int addValue2 = Integer.parseInt(statement.get(3));
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								/* 
								 * If the first value can't be parsed, but the second can, 
								 * run ADD on a variable and an int_lit (Same as running it on an 
								 * int_lit and a variable, since order doesn't matter in addition)
								 */
								created = true;
								addition(statement.get(1), addValue2, statement.get(2));
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End try block
					catch (NumberFormatException f) {
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								created = true;
								var1 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								var2 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						// If neither value can be parsed, run ADD on two variables
						addition(statement.get(1), var1, var2);
					}// End catch block
				} // End catch block
				break;
			/*
			 * The logic behind the SUB, MUL, and DIV operations is identical to 
			 * that of the ADD operation, so all try/catch blocks and conditionals are identical. 
			 * The only difference is the operation being performed in the end
			 */
			case "SUB":
				created = false;
				x = 0;
				try {
					int subValue1 = Integer.parseInt(statement.get(2));
					try {
						int subValue2 = Integer.parseInt(statement.get(3));
						subtraction(statement.get(1), subValue1, subValue2);
					} // End try block
					catch (NumberFormatException e) {
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								subtraction(statement.get(1), subValue1, statement.get(3));
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End catch
				} // End try
				catch (NumberFormatException e) {
					created = false;
					try {
						int subValue2 = Integer.parseInt(statement.get(3));
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								/*
								 * Both subtraction and division are order-specific, so using two 
								 * similar methods with different operator sequences is acceptable
								 */
								created = true;
								subtraction(statement.get(1), statement.get(2), subValue2);
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End try block
					catch (NumberFormatException f) {
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								created = true;
								var1 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								var2 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						subtraction(statement.get(1), var1, var2);
					} // End catch block
				} // End catch block
				break;
			case "MUL":
				created = false;
				x = 0;
				try {
					int mulValue1 = Integer.parseInt(statement.get(2));
					try {
						int mulValue2 = Integer.parseInt(statement.get(3));
						multiplication(statement.get(1), mulValue1, mulValue2);
					} // End try block
					catch (NumberFormatException e) {
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								multiplication(statement.get(1), mulValue1, statement.get(3));
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End catch block
				} // End try block
				catch (NumberFormatException e) {
					created = false;
					try {
						int mulValue2 = Integer.parseInt(statement.get(3));
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								/*
								 * Again, since order in multiplication doesn't matter using the same 
								 * method for and int_lit and a variable and a variable and an int_lit is acceptable
								 */
								created = true;
								multiplication(statement.get(1), mulValue2, statement.get(2));
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End try block
					catch (NumberFormatException f) {
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								created = true;
								var1 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								var2 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						multiplication(statement.get(1), var1, var2);
					} // End catch block
				} // End catch block
				break;
			case "DIV":
				created = false;
				x = 0;
				try {
					int divValue1 = Integer.parseInt(statement.get(2));
					try {
						int divValue2 = Integer.parseInt(statement.get(3));
						division(statement.get(1), divValue1, divValue2);
					} // End try block
					catch (NumberFormatException e) {
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								division(statement.get(1), divValue1, statement.get(3));
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End catch block
				} // End try block
				catch (NumberFormatException e) {
					created = false;
					try {
						int divValue2 = Integer.parseInt(statement.get(3));
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								/*
								 * Both subtraction and division are order-specific, so using 
								 * two similar methods with different operator sequences is acceptable
								 */
								created = true;
								division(statement.get(1), statement.get(2), divValue2);
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
					} // End try block
					catch (NumberFormatException f) {
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(2).equals(variables.get(x).getName())) {
								created = true;
								var1 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						created = false;
						for (x = 0; x < variables.size(); x++) {
							if (statement.get(3).equals(variables.get(x).getName())) {
								created = true;
								var2 = variables.get(x).getName();
							} // End if
						} // End for
						if (x == variables.size() && created == false) {
							System.out.println("--------------------------------------------------");
							System.out.println(instructions[j]);
							error(13);
						} // End if
						division(statement.get(1), var1, var2);
					} // End catch block
				} // End catch block
				break;
			/*
			 * Sets the program counter to the value returned by the jump() method, 
			 * with each BRx instruction simply checking for the proper conditions
			 * before executing the jump() command
			 */
			case "JMP":
				if (!labels.contains(statement.get(1))) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(14);
				} // End if
				else
					progCount = jump(statement.get(1));
				break;
			case "BRn":
				// Checks if the variable specified is negative
				created = false;
				x = 0;
				for (x = 0; x < variables.size(); x++) {
					if (variables.get(x).getName().equals(statement.get(1))) {
						created = true;
						if (variables.get(x).getIntValue() < 0) {
							if (!labels.contains(statement.get(2))) {
								System.out.println("--------------------------------------------------");
								System.out.println(instructions[j]);
								error(14);
							} // End if
							else
								progCount = jump(statement.get(2));
						} // End if
					} // End if
				} // End for
				if (x == variables.size() && created == false) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(13);
				} // End if
				break;
			case "BRp":
				// Checks if the variable specified is positive
				created = false;
				x = 0;
				for (x = 0; x < variables.size(); x++) {
					if (variables.get(x).getName().equals(statement.get(1))) {
						created = true;
						if (variables.get(x).getIntValue() > 0) {
							if (!labels.contains(statement.get(2))) {
								System.out.println("--------------------------------------------------");
								System.out.println(instructions[j]);
								error(14);
							} // End if
							else
								progCount = jump(statement.get(2));
						} // End if
					} // End if
				} // End for
				if (x == variables.size() && created == false) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(13);
				} // End if
				break;
			case "BRz":
				// Checks if the variable specified is zero
				created = false;
				x = 0;
				for (x = 0; x < variables.size(); x++) {
					if (variables.get(x).getName().equals(statement.get(1))) {
						created = true;
						if (variables.get(x).getIntValue() == 0) {
							if (!labels.contains(statement.get(2))) {
								System.out.println("--------------------------------------------------");
								System.out.println(instructions[j]);
								error(14);
							} // End if
							else
								progCount = jump(statement.get(2));
						} // End if
					} // End if
				} // End for
				if (x == variables.size() && created == false) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(13);
				} // End if
				break;
			case "BRzp":
				// Checks if the variable specified is non-negative
				created = false;
				x = 0;
				for (x = 0; x < variables.size(); x++) {
					if (variables.get(x).getName().equals(statement.get(1))) {
						created = true;
						if (variables.get(x).getIntValue() >= 0) {
							if (!labels.contains(statement.get(2))) {
								System.out.println("--------------------------------------------------");
								System.out.println(instructions[j]);
								error(14);
							} // End if
							else
								progCount = jump(statement.get(2));
						} // End if
					} // End if
				} // End for
				if (x == variables.size() && created == false) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(13);
				} // End if
				break;
			case "BRzn":
				// Checks if the variable specified is non-positive
				created = false;
				x = 0;
				for (int i = 0; i < variables.size(); i++) {
					if (variables.get(i).getName().equals(statement.get(1))) {
						created = true;
						if (variables.get(i).getIntValue() <= 0) {
							if (!labels.contains(statement.get(2))) {
								System.out.println("--------------------------------------------------");
								System.out.println(instructions[j]);
								error(14);
							} // End if
							else
								progCount = jump(statement.get(2));
						} // End if
					}  // End if
				} // End for
				if (x == variables.size() && created == false) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(13);
				} // End if
				break;
			case "IN":
				// Waits for an integer input before calling the store method to create a variable with the name given
				Scanner scan = new Scanner(System.in);
				int inValue = scan.nextInt();
				boolean overwrite = false;
				/*
				 * Checks if the variable already exists, if it does, then the 
				 * value is overwritten, if not, then a new variable is created
				 */
				for (int i = 0; i < variables.size(); i++) {
					if (variables.get(i).getName().equals(statement.get(1)))
						overwrite = true;
					if (overwrite == true)
						variables.get(i).setIntValue(inValue);
				} // End for
				if (overwrite == false)
					store(statement.get(1), inValue);
				break;
			case "OUT":
				// Checks if the desired output is a variable or a String surrounded by quotes
				created = false;
				x = 0;
				if (statement.get(1).charAt(0) != '"') {
					for (x = 0; x < variables.size(); x++) {
						if (statement.get(1).equals(variables.get(x).getName())) {
							created = true;
							output(variables.get(x).getIntValue());
						} // End if
					} // End for
					if (x == variables.size() && created == false) {
						System.out.println("--------------------------------------------------");
						System.out.println(instructions[j]);
						error(13);
					} // End if
				} // End if
				else
					output(instruction.substring(5, instruction.length()-1));
				break;
			/*
			 * Tries to parse the second value, if it can, runs store() on a 
			 * String and int, if not, runs store() on two Strings
			 */
			case "STO":
				created = false;
				x = 0;
				try {
					int stoValue = Integer.parseInt(statement.get(2));
					store(statement.get(1), stoValue);
				} // End try block
				catch (NumberFormatException e) {
					for (x = 0; x < variables.size(); x++) {
						if (statement.get(2).equals(variables.get(x).getName())) {
							created = true;
							store(statement.get(1), variables.get(x).getIntValue());
						} // End if
					} // End for
					if (x == variables.size() && created == false) {
						System.out.println("--------------------------------------------------");
						System.out.println(instructions[j]);
						error(13);
					} // End if
				} // End catch block
				break;
			case "HALT":
				// Breaks out of switch if the instruction is HALT
				break;
			default:
				break;
		} // End switch
		stringScan.close(); // Close stringScan
	}
	
	/*
	 * Mathematical operation methods are overloaded to allow for different 
	 * parameters between int_lits and variables. Each method checks to see if 
	 * the variable specified needs to be overwritten before calling the store() 
	 * method to create a new variable to store the result
	 */
	public static void addition(String desVar, int value1, int value2) {
		boolean overwrite = false;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar)) {
				overwrite = true;
				if (overwrite == true) {
					int sum = value1+value2;
					variables.get(i).setIntValue(sum);
				} // End if
			} // End if
		} // End for
		if (overwrite == false) {
			int sum = value1+value2;
			store(desVar, sum);
		} // End if
	}

	public static void addition(String desVar, int value, String name) {
		boolean overwrite = false;
		int value1 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name))
						value1 = variables.get(j).getIntValue();
				} // End for
				int sum = value1+value;
				variables.get(i).setIntValue(sum);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name))
					value1 = variables.get(j).getIntValue();
			} // End for
			int sum = value1+value;
			store(desVar, sum);
		} // End if
	}
	
	public static void addition(String desVar, String name1, String name2) {
		boolean overwrite = false;
		int value1 = 0;
		int value2 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name1))
						value1 = variables.get(j).getIntValue();
				} // End for
				for (int k = 0; k < variables.size(); k++) {
					if (variables.get(k).getName().equals(name2))
						value2 = variables.get(k).getIntValue();
				} // End for
				int sum = value1+value2;
				variables.get(i).setIntValue(sum);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name1))
					value1 = variables.get(j).getIntValue();
			} // End for
			for (int k = 0; k < variables.size(); k++) {
				if (variables.get(k).getName().equals(name2))
					value2 = variables.get(k).getIntValue();
			} // End for
			int sum = value1+value2;
			store(desVar, sum);
		} // End if
	}
	
	public static void subtraction(String desVar, int value1, int value2) {
		boolean overwrite = false;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar)) {
				overwrite = true;
				if (overwrite == true) {
					int dif = value1-value2;
					variables.get(i).setIntValue(dif);
				}
			} // End if
		} // End for
		if (overwrite == false) {
			int dif = value1-value2;
			store(desVar, dif);
		} // End if
	}
	
	public static void subtraction(String desVar, String name, int value) {
		boolean overwrite = false;
		int value1 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name))
						value1 = variables.get(j).getIntValue();
				} // End for
				int dif = value1-value;
				variables.get(i).setIntValue(dif);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name))
					value1 = variables.get(j).getIntValue();
			} // End for
			int dif = value1-value;
			store(desVar, dif);
		} // End if
	}
	
	public static void subtraction(String desVar, int value, String name) {
		boolean overwrite = false;
		int value1 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name))
						value1 = variables.get(j).getIntValue();
				} // End for
				int dif = value-value1;
				variables.get(i).setIntValue(dif);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name))
					value1 = variables.get(j).getIntValue();
			} // End for
			int dif = value-value1;
			store(desVar, dif);
		} // End if
	}
	
	public static void subtraction(String desVar, String name1, String name2) {
		boolean overwrite = false;
		int value1 = 0;
		int value2 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name1)) 
						value1 = variables.get(j).getIntValue(); 
				} // End for
				for (int k = 0; k < variables.size(); k++) {
					if (variables.get(k).getName().equals(name2)) 
						value2 = variables.get(k).getIntValue(); 
				} // End for
				int dif = value1-value2;
				variables.get(i).setIntValue(dif);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name1)) 
					value1 = variables.get(j).getIntValue(); 
			} // End for
			for (int k = 0; k < variables.size(); k++) {
				if (variables.get(k).getName().equals(name2)) 
					value2 = variables.get(k).getIntValue(); 
			} // End for
			int dif = value1-value2;
			store(desVar, dif);
		} // End if
	}
	
	public static void multiplication(String desVar, int value1, int value2) {
		boolean overwrite = false;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar)) {
				overwrite = true;
				if (overwrite == true) {
					int pro = value1*value2;
					variables.get(i).setIntValue(pro);
				} // End if
			}
		} // End for
		if (overwrite == false) {
			int pro = value1*value2;
			store(desVar, pro);
		} // End if
	}
	
	public static void multiplication(String desVar, int value, String name) {
		boolean overwrite = false;
		int value1 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name))
						value1 = variables.get(j).getIntValue();
				} // End for
				int pro = value*value1;
				variables.get(i).setIntValue(pro);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name))
					value1 = variables.get(j).getIntValue();
			} // End for
			int pro = value*value1;
			store(desVar, pro);
		} // End if
	}
	
	public static void multiplication(String desVar, String name1, String name2) {
		boolean overwrite = false;
		int value1 = 0;
		int value2 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name1))
						value1 = variables.get(j).getIntValue();
				} // End for
				for (int k = 0; k < variables.size(); k++){
					if (variables.get(k).getName().equals(name2))
						value2 = variables.get(k).getIntValue();
				} // End for
				int pro = value1*value2;
				variables.get(i).setIntValue(pro);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name1))
					value1 = variables.get(j).getIntValue();
			} // End for
			for (int k = 0; k < variables.size(); k++) {
				if (variables.get(k).getName().equals(name2))
					value2 = variables.get(k).getIntValue();
			} // End for
			int pro = value1*value2;
			store(desVar, pro);
		} // End if
	}
	
	/*
	 * All division methods check for division by 0 before attempting operation
	 */
	public static void division(String desVar, int value1, int value2) {
		if (value2 == 0) {
			System.out.println("--------------------------------------------------");
			System.out.println(instructions[j]);
			error(16);
		} // End if
		boolean overwrite = false;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar)) {
				overwrite = true;
				if (overwrite == true) {
					int quo = value1/value2;
					variables.get(i).setIntValue(quo);
				} // End if
			}
		} // End for
		if (overwrite == false) {
			int quo = value1/value2;
			store(desVar, quo);
		} // End if
	}
	
	public static void division(String desVar, String name, int value) {
		if (value == 0) {
			System.out.println("--------------------------------------------------");
			System.out.println(instructions[j]);
			error(16);
		} // End if
		boolean overwrite = false;
		int value1 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name))
						value1 = variables.get(j).getIntValue();
				} // End for
				int quo = value1/value;
				variables.get(i).setIntValue(quo);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name))
					value1 = variables.get(j).getIntValue();
			} // End for
			int quo = value1/value;
			store(desVar, quo);
		} // End if
	}
	
	public static void division(String desVar, int value, String name) {
		boolean overwrite = false;
		int value1 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name))
						value1 = variables.get(j).getIntValue();
				} // End for
				if (value1 == 0) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(16);
				} // End if
				int quo = value1/value;
				variables.get(i).setIntValue(quo);
			} // End if
		} // End for
		if (overwrite == false){
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name))
					value1 = variables.get(j).getIntValue();
			} // End for
			int quo = value1/value;
			store(desVar, quo);
		} // End if
	}
	
	public static void division(String desVar, String name1, String name2) {
		boolean overwrite = false;
		int value1 = 0;
		int value2 = 0;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true) {
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).getName().equals(name1))
						value1 = variables.get(j).getIntValue();
				} // End for
				for (int k = 0; k < variables.size(); k++) {
					if (variables.get(k).getName().equals(name2))
						value2 = variables.get(k).getIntValue();
				} // End for
				if (value2 == 0) {
					System.out.println("--------------------------------------------------");
					System.out.println(instructions[j]);
					error(16);
				} // End if
				int quo = value1/value2;
				variables.get(i).setIntValue(quo);
			} // End if
		} // End for
		if (overwrite == false) {
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).getName().equals(name1))
					value1 = variables.get(j).getIntValue();
			} // End for
			for (int k = 0; k < variables.size(); k++) {
				if (variables.get(k).getName().equals(name2))
					value2 = variables.get(k).getIntValue();
			} // End for
			if (value2 == 0) {
				System.out.println("--------------------------------------------------");
				System.out.println(instructions[j]);
				error(16);
			} // End if
			int quo = value1/value2;
			store(desVar, quo);
		} // End if
	}
	
	/*
	 * Searches through the instructions array until it finds the matching label, returns its index
	 */
	public static int jump(String label) {
		for (int i = 0; i < instructions.length; i++) {
			if (instructions[i].equals(label))
				return i;
		} // End for
		return -1;
	}
	
	/*
	 * Overloaded output method takes in either a String from the 
	 * substring without quotes, or an integer passed from a variable's value
	 */
	public static void output(String output) {
		System.out.println(output);
	}
	
	public static void output(int output) {
		System.out.println(output);
	}
	
	/*
	 * Checks if the variable name is already in use, if so, overwrites the value, 
	 * if not, creates a new Variable object and adds it to the variables Arraylist
	 */
	public static void store(String desVar, int value) {
		boolean overwrite = false;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(desVar))
				overwrite = true;
			if (overwrite == true)
				variables.get(i).setIntValue(value);
		} // End for
		if (overwrite == false) {
			Variable newVar = SubLC3VM.new Variable (desVar, value);
			newVar.setName(desVar);
			newVar.setIntValue(value);
			variables.add(newVar);
		} // End if
	}
	
	// -------------------------------------------------------------------------------------------------------------------------
	// General Methods used throughout the Compiler
	// -------------------------------------------------------------------------------------------------------------------------

	/*
	 * Table of error cases and associated messages
	 */
	private static void error(int code) {
		// Code passed to method is arbitrary, and almost based solely on when I created the error
		switch (code) {
		case 0:
			System.out.println("Lines must start with a command, semicolon(;), or a single label");
			break;
		case 1:
			System.out.println("Could not find file specified");
			break;
		case 2:
			System.out.println("Invalid symbol error");
			break;
		case 3:
			System.out.println("The name of the identifier is too long");
			break;
		case 4:
			System.out.println("Syntax error - Unexpected literal integer");
			break;
		case 5:
			System.out.println("Lines must start with either a command, comment, or label");
			break;
		case 6:
			System.out.println("Commands not followed by a quote must be followed by an identifier");
			break;
		case 7:
			System.out.println("Quotations for output must be closed");
			break;
		case 8:
			System.out.println("Single quotations must be closed");
			break;
		case 9:
			System.out.println("Destination variables must be followed by at least one source or label");
			break;
		case 10:
			System.out.println("Invalid number of arguments");
			break;
		case 11:
			System.out.println("Identifers, commands; and labels cannot start with an underscore ('_')");
			break;
		case 12:
			System.out.println("Identifiers, commands, and labels cannot start with a digit.");
			break;
		case 13:
			System.out.println("Undeclared variable used as a source");
			break;
		case 14:
			System.out.println("Undeclared label");
			break;
		case 15:
			System.out.println("Program exceeds designated memory capacity of 500");
			break;
		case 16:
			System.out.println("Cannot divide by 0");
			break;
		case 17:
			System.out.println("Input file has no executable contents");
			break;
		} // End switch
		System.exit(0);
	}
	
	/*
	 * Basic class to act as descriptor for variables throughout the compiler 
	 * class. Takes in a name and integer value for a given variable
	 */
	class Variable {
		String name;
		int intValue;
		
		// Constructor
		public Variable(String name, int value) {
			name = this.name;
			value = this.intValue;
		}

		// Getters and Setters
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIntValue() {
			return intValue;
		}

		public void setIntValue(int intValue) {
			this.intValue = intValue;
		}
		
		// Basic toString()
		@Override
		public String toString() {
			return "Variable [name=" + name + ", intValue=" + intValue + "]";
		}
	}
}