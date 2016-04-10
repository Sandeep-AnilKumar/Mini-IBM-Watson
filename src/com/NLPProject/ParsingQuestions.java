package com.NLPProject;
//test
/*************Revision History**************************************
Version 	Author    Date    			ChangesDone
---------------------------------------------------
1.0			Sandeep   Apr-10-2016		Initial Version
1.1			Pavan	  Apr-10-2016       Modified for takig file from command line args

********************************************************************* */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
public class ParsingQuestions {

	public static void main(String[] args) throws IOException {

		List<String> questionsList = new ArrayList<String>();
		InputStream ir = null;
		BufferedReader br = null;
		String filePath = args[0];

		try {

			ir  = ParsingQuestions.class.getResourceAsStream(filePath);
			br = new BufferedReader(new InputStreamReader(ir));

			String currentQuestion = "";

			while((currentQuestion = br.readLine()) != null) {
				questionsList.add(currentQuestion);
			}

			buildParseTrees(questionsList);
		}

		catch(Exception e) {
			e.printStackTrace();
		}

		finally {
			try {
				if(br != null) {
					br.close();
				}
				if(ir != null) {
					ir.close();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void buildParseTrees(List<String> questions) throws IOException 
	{
		PrintWriter pw = new PrintWriter("ParseTrees.txt");
		PrintWriter pw2 = new PrintWriter("QuestionDomains.txt");
		if(questions == null || questions.size() == 0) {
			System.out.println("No questions to parse");
		}

		String currentQuestion = "";
		for(Iterator<String> i = questions.iterator(); i.hasNext();) {
			currentQuestion = i.next();
			List<Tree> result = parse(currentQuestion);
			List<String> ner = ner(currentQuestion);
			String tree = "";
			String namedEntities = "";

			for(Tree t : result) {
				tree= tree + t.toString();
				pw.write(t.toString()+"\n");
			}
			for (String n : ner) {
				namedEntities = namedEntities + n.toString();
			}
			String domain = getDomian(namedEntities, currentQuestion, tree);
			pw2.write(currentQuestion);
			pw2.write("\n"+domain);
			pw2.write("\n\n");
		}
		System.out.println("done");
		pw.close();
		pw2.close();
	}

	public static String getDomian(String ner, String currentQuestion, String t) throws IOException
	{
		if(ner == null || currentQuestion == null || t == null || ner.length() == 0 || currentQuestion.length() == 0 || t.length() == 0) {
			return "Question is empty";
		}
		InputStream ir = ParsingQuestions.class.getResourceAsStream("/com/resources/Keywords.properties");
		Properties prop = new Properties();
		prop.load(ir);
		ir.close();
		
		String patternGeo = ".*\\bJJS\\b.*\\bNN\\b.*";
		String questionParts[] = currentQuestion.split("\\s");
		int partsLength = questionParts.length;
		
		Set keywordsSets = prop.keySet();
		String domain = "";
		
		for(int i = 0; i < partsLength; i++) {
				if(! (keywordsSets.contains(questionParts[i]))) {
					continue;
				}
				else {
					domain = prop.get(questionParts[i]).toString();
					break;
				}
			}
		
		if(ner.contains("LOCATION"))
		{
			if(ner.contains("PERSON"))
			{
				return "MOVIE or MUSIC";
			}
			else
			{
				return "GEOGRAPHY";
			}
		}
		else if(ner.contains("PERSON"))
		{
			return "MOVIE or MUSIC";
		}
		else if(domain != null && domain.length() != 0)
		{
			return domain;
		}
		else if(t.toUpperCase().matches(patternGeo)) {
			return "GEOGRAPHY";
		}
		else if(t.contains("VBD"))
		{
			return "MOVIE or MUSIC";
		}
		else {
			return "Could not find";
		}
	}

	public static List<String> ner(String text) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);

		List<String> result = new ArrayList<String>();
		for (CoreLabel token : tokens) {
			// this is the text of the token
			String nerTag = token.get(NamedEntityTagAnnotation.class);
			result.add(nerTag);
		}

		return result;
	}
	public static List<Tree> parse(String text) throws FileNotFoundException {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(text);

		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		List<Tree> result = new ArrayList<Tree>();
		for (CoreMap sentence : sentences) {
			Tree tree = sentence.get(TreeAnnotation.class);
			result.add(tree);
		}
		return result;
	}
}
