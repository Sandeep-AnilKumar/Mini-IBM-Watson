package com.NLPProject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryBuilderPrint {

	static Set POSTagSet = new HashSet();

	public static void main(String[] args) {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = new FileInputStream("C:\\Users\\kpava\\workspace\\NLP_Proejct_GitHub_Sync\\trunk\\src\\com\\resources\\SemanticQuestions.txt");
			br = new BufferedReader(new InputStreamReader(is));

			String rule = "";
			String currentLine = "";
			while((currentLine = br.readLine()) != null) {
				System.out.println("----------------------------------------------------------------------");
				Map<String, HashSet<String>> rules = new HashMap<String, HashSet<String>>();
				System.out.println(currentLine);
				currentLine = br.readLine();
				System.out.println(currentLine);
				rule = currentLine;
				List<String> ruleList = new ArrayList<String>();
				StringBuffer ruleParts = new StringBuffer();

				Pattern pattern = Pattern.compile("\\([.a-zA-Z0-9]*(\\s[.'a-zA-Z\\?0-9]*)+\\)");
				String parts[];
				String org = "";
				String old = "";
				int j =0;
				while(rule.contains("(")) {
					ruleList = new ArrayList<String>();
					Matcher matcher = pattern.matcher(rule);
					while(matcher.find()) {
						String temp = matcher.group();
						org = temp.replace("(", "");
						org = org.replace(")", "");
						parts = org.split("\\s");
						old = parts[0];
						parts[0] = parts[0] + ++j;
						rule = rule.replace(temp, parts[0]);
						temp = temp.replaceFirst(old, parts[0]);
						if(!ruleList.contains(temp))
							ruleList.add(temp);
					}
					for(String ruleProduction: ruleList) {
						ruleParts = new StringBuffer();
						ruleProduction = ruleProduction.replace("(", "");
						ruleProduction = ruleProduction.replace(")", "");
						parts = ruleProduction.split("\\s");

						for(int i = 1; i < parts.length; ++i) {
							ruleParts.append(parts[i] + " ");
						}

						HashSet<String> production = rules.get(parts[0]);
						if(production == null) {
							production = new HashSet<String>();
						}
						production.add(ruleParts.toString());
						rules.put(parts[0], production);
					}
				}

				System.out.println("\n\nGRAMMAR RULES ARE: - ");
				for(Entry<String, HashSet<String>> entry : rules.entrySet()) {
					HashSet<String> productions = entry.getValue();
					for(Iterator i = productions.iterator(); i.hasNext();) {
						System.out.println(entry.getKey() + " -> " + i.next());
					} 
				}
				attachSemantic(rules);
			}
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

	public static void attachSemantic(Map<String, HashSet<String>> rules) throws IOException {
		if(rules == null) {
			return;
		}

		Map<String, String> semantic = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream ir = new FileInputStream("C:\\Users\\kpava\\Downloads\\Desktop\\POSTags.properties");
		prop.load(ir);
		ir.close();

		POSTagSet = prop.keySet();

		Set<Entry<String,HashSet<String>>> rulesSet = rules.entrySet();
		String production = "";
		HashSet<String> productions = new HashSet<String>();
		StringBuffer semanticBuffer = new StringBuffer();
		String preTerminal = "";
		String temp = "";

		for(Iterator<Entry<String,HashSet<String>>> i = rulesSet.iterator(); i.hasNext();) {
			Entry<String,HashSet<String>> entry = i.next();

			productions = entry.getValue();

			for(String prod : productions) {
				semanticBuffer = new StringBuffer();
				prod = prod.trim();
				String prodParts[] = prod.split("\\s");

				if(POSTagSet.contains(entry.getKey())) {
					semanticBuffer.append(prod);
				}

				else {
					for(int j = prodParts.length - 1; j >= 0; --j) {
						if(!POSTagSet.contains(prodParts[j])) {
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
		}

		System.out.println("\n\nSEMANTIC RULES ARE: -");
		for(Entry entry : semantic.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
		}		
	}
}
