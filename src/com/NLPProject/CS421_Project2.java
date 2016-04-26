package com.NLPProject;

/**
 * NLP Project Part 2
 * Use this as the main class for your project. 
 * Implement the logic to generate the SQL query and the query answer.
 * Create additional methods, variables, and classes as needed.
 *
 */

import java.util.Scanner;

public class CS421_Project2
{

	public static String currentQuery = new String();
	public static String sqlQuery = null;
	public static String answer  = "";

	public static void main(String[] args) 
	{
		System.out.println("Welcome! This is MiniWatson.");
		System.out.println("Please ask a question. Type 'q' when finished.");
		System.out.println();
		String input;
		Scanner keyboard = new Scanner(System.in);
		do{	
			input = keyboard.nextLine().trim();

			if(!input.equalsIgnoreCase("q")){
				sqlQuery = "";
				currentQuery = input;
				System.out.println("<QUERY>\n" + currentQuery + "\n");

				QueryBuilderPrint.getAnswer(currentQuery); //Question Processing.
				printSQL();
				printAnswer();
				System.out.println();
			}
		}while(!input.equalsIgnoreCase("q"));

		keyboard.close();
		System.out.println("Goodbye.");

	}

	public static void printSQL(){
		System.out.println("\n<SQL>\n" + sqlQuery);
	}
	public static void printAnswer(){
		System.out.println("\n<ANSWER>\n" + answer);
	}

}