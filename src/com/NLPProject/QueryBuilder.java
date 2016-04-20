package com.NLPProject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javafx.collections.MappingChange.Map;

public class QueryBuilder 
{
	
	static HashMap<String, HashSet<String>> grammar = new HashMap<String, HashSet<String>>();
	public static void main(String[] args) throws Exception 
	{
		InputStream is = new FileInputStream("C:\\Users\\kpava\\workspace\\NLP_Proejct_GitHub_Sync\\trunk\\ParseTree_Part2.txt");
	    String s; 
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while((s= br.readLine())!=null)
		{
			buildGrammar(s);
		}
		System.out.println("____________________________________");
		for(String rule : grammar.keySet())
		{
			
			for(String production : grammar.get(rule))
			{
				System.out.println( rule + " -> " + production);
			}
		}
	}
	
	public static void addtoGrammar(String rule, String production)
	{
		//HashSet<String> alreadyAddedRule =  grammar.keySet();
		if(!grammar.keySet().contains(rule))
		{
			HashSet<String> productions = new HashSet<>();
			productions.add(production);
			grammar.put(rule, productions);
		}
		else
		{
			HashSet<String> productions = grammar.get(rule);
			productions.add(production);
			grammar.put(rule, productions);
		}
	}
	public static void buildGrammar(String tree)
	{
		//String rule = "(ROOT (SQ (VBZ Is) (NP (NNP Rome)) (NP (NP (DT the) (NN capital)) (PP (IN of) (NP (NNP Italy)))) (. ?)))";
		String rule = tree;
		List<String> rules = new ArrayList<String>();

		Pattern pattern = Pattern.compile("\\([.a-zA-Z]*(\\s[.'a-zA-Z\\?0-9]*)+\\)");
		String parts[];
		String org = "";
		System.out.println(rule);
		System.out.println(" ");
		while(!rule.equals("ROOT")) {
			//System.out.println("-----------------------------------------------------------------------------------");
			//System.out.println(rule);
			rules = new ArrayList<String>();
			Matcher matcher = pattern.matcher(rule);
			while(matcher.find()) {
				String temp = matcher.group();
				org = temp.replace("(", "");
				org = org.replace(")", "");
				parts = org.split("\\s");
				addtoGrammar(parts[0], parts[1]);
				rule = rule.replace(temp, parts[0]);
				rules.add(temp);
			}
			for(String ruleProduction: rules) {
				ruleProduction = ruleProduction.replace("(", "");
				ruleProduction = ruleProduction.replace(")", "");
				parts = ruleProduction.split("\\s");
				System.out.print(parts[0] + " -> ");
				for(int i = 1; i < parts.length; ++i) {
					System.out.print(parts[i] + " ");
				}
				System.out.println("");
			}
		}
	}
}
