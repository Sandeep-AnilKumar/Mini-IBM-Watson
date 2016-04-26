package com.NLPProject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.trees.Tree;

public class QueryBuilderPrint {

	static Set<?> POSTagSet = new HashSet<String>(); //A set of all POS Tags.
	static Map<String, String> semantic = new LinkedHashMap<String, String>(); //A map for storing the semantics of a rule.

	static String currentQuestionParseTree = ""; //This will hold the current questions's parse tree.

	public static void getAnswer(String question) {
		try{
			String rule = "";
			String currentLine = question;
			Map<String, String> rules = new LinkedHashMap<String, String>();
			semantic = new LinkedHashMap<String, String>();

			List<Tree> t = ParsingQuestions.parse(currentLine);
			String parseTree = "";
			for(Tree tree : t) {
				parseTree = parseTree + tree.toString();
			}

			// extracting and printing grammar rules. A regex pattern is used to extract grammar like (Aux Did) from  the tree.
			rule = parseTree;
			List<String> ruleList = new ArrayList<String>();
			StringBuffer ruleParts = new StringBuffer();

			Pattern pattern = Pattern.compile("\\([.a-zA-Z0-9\\:\\`\\'\\-]*(\\s[.\\`\\'a-zA-Z\\?0-9\\:\\-]*)+\\)");
			String parts[];
			String org = "";
			String old = "";
			int j = 0;
			boolean treeStored = false;
			while(rule.contains("(")) {
				ruleList = new ArrayList<String>();
				Matcher matcher = pattern.matcher(rule);
				while(matcher.find()) {
					String temp = matcher.group();
					org = temp.replace("(", "");
					org = org.replace(")", "");
					parts = org.split("\\s");
					old = parts[0];
					if(!old.equals(".")) {
						parts[0] = parts[0] + ++j;
						rule = rule.replace(temp, parts[0]);
						temp = temp.replaceFirst(old, parts[0]);
						if(!ruleList.contains(temp))
							ruleList.add(temp);
					}
					else {
						rule = rule.replace(temp, "");
					}
				}
				if(!treeStored) { //This holds the information after removing the leaves of the tree.
					currentQuestionParseTree = rule;
					treeStored = true;
				}
				for(String ruleProduction: ruleList) {
					ruleParts = new StringBuffer();
					ruleProduction = ruleProduction.replace("(", "");
					ruleProduction = ruleProduction.replace(")", "");
					parts = ruleProduction.split("\\s");

					for(int i = 1; i < parts.length; ++i) {
						ruleParts.append(parts[i] + " ");
					}

					String production = ruleParts.toString();
					rules.put(parts[0], production);
				}
			}

			String value = "";

			for(String s : rules.keySet()) { //Finding out the Start of the rule and labelling it as "Start"
				if(!s.startsWith("S")) {
					continue;
				}
				value = rules.get(s);
			}

			rules.put("Start", value);

			//This ends the grammar extraction from the parse tree phase. Please uncomment the below for loop to see the grammar.


			/*System.out.println("\n\nGRAMMAR RULES ARE: - ");
				for(Entry<String, String> entry : rules.entrySet()) {
					String productions = entry.getValue();
					System.out.println(entry.getKey() + " -> " + productions);
				}*/

			//System.out.println(currentQuestionParseTree);

			//Now that the grammar is ready, let us first extract the semantics for those rules and put it into a Map.

			attachSemantic(rules);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void attachSemantic(Map<String, String> rules) throws IOException, SQLException {
		if(rules == null) {
			return;
		}

		Properties prop = new Properties();
		InputStream ir = ParsingQuestions.class.getResourceAsStream("/com/resources/POSTags.properties");
		prop.load(ir);
		ir.close();

		POSTagSet = prop.keySet(); //contains all the POS tags.

		Set<Entry<String, String>> rulesSet = rules.entrySet();
		String productions = "";
		StringBuffer semanticBuffer = new StringBuffer();
		String preTerminal = "";
		String temp = "";

		for(Iterator<Entry<String, String>> i = rulesSet.iterator(); i.hasNext();) {
			Entry<String, String> entry = i.next();

			productions = entry.getValue();
			temp = entry.getKey();
			temp = temp.replaceAll("[0-9]*","");

			String prod = productions;
			semanticBuffer = new StringBuffer();
			prod = prod.trim();
			String prodParts[] = prod.split("\\s");

			if(POSTagSet.contains(temp)) {
				prod = prod.replaceAll("[0-9]*", "");
				semanticBuffer.append(prod);
			}

			else {
				for(int j = prodParts.length - 1; j >= 0; --j) {
					temp = prodParts[j];
					temp = temp.replaceAll("[0-9]*", "");
					if(!POSTagSet.contains(temp)) {
						semanticBuffer.append(prodParts[j] + ".sem ");
					}
					else {
						preTerminal = prodParts[j];
						temp = preTerminal + ".sem " + semanticBuffer.toString();
						semanticBuffer = new StringBuffer(temp);
					}
				}
			}
			semantic.put(prod, semanticBuffer.toString());
		}

		//Semantic attachments are done here, uncomment this for loop, to see them.

		/*System.out.println("\n\nSEMANTIC RULES ARE: -");
		for(Entry<String, String> entry : semantic.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
		}*/


		//Now that we are done with semantic rules. We will be sending the question to MovieDomainLambda functions,
		//which will use the appropriate lambda functions to generate the SQL queries.
		MovieDomainLambda.movieLambdas(semantic, currentQuestionParseTree, rules);
	}
}
