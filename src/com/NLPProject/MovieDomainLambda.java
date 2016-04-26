package com.NLPProject;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MovieDomainLambda {

	//interfaces to build lambdas with different number of arguments.
	interface BuildLambdaWithOneArguments {
		String getSqlQuery(String X);
	}

	interface BuildLambdaWithTwoArguments {
		String getSqlQuery(String X, String Y);
	}

	interface BuildLambdaWithThreeArguments {
		String getSqlQuery(String X, String Y, String Z);
	}

	interface BuildLambdaWithFourArguments {
		String getSqlQuery(String X, String Y, String Z, String D);
	}

	final static String TYPE_1 = "(ROOT (SQ VBZ (NP NN) (ADVP IN) (NP NN) ))";
	final static String TYPE_2 = "(ROOT (SQ VBZ (NP NN) (NP DT NN) ))";
	final static String TYPE_3 = "(ROOT (SQ VBD (NP NN) (VP VBN (PP IN (NP NN))) ))";
	final static String TYPE_4 = "(ROOT (S (VP VBD (NP (NP NN) (VP VBN (PP IN (NP NN))))) ))";
	final static String TYPE_5 = "(ROOT (SQ VBD (NP NN) (VP VB (NP (NP DT NN) (PP IN (NP CD)))) ))";
	final static String TYPE_6 = "(ROOT (SQ VBD (NP NN) (PP (NP DT JJS NN) (PP IN (NP CD))) ))";
	final static String TYPE_7 = "(ROOT (SQ VBD (NP NN) (VP VB (NP NN)) ))"; 
	final static String TYPE_8 = "(ROOT (SQ VBD (NP NN) (VP VB (PP IN (NP NN))) ))";
	final static String TYPE_9 = "(ROOT (SQ VBD (NP DT JJ NN) (VP VB (NP (NP DT NN) (PP IN (NP CD)))) ))"; 
	final static String TYPE_10 = "(ROOT (SQ VBD (NP NN) (VP VB (PP IN (NP ";
	final static String TYPE_11 = "(ROOT (SINV (VP VBD (NP NN NN)) (NP (NP NN) (PP IN (FRAG (NP ";
	final static String TYPE_12 = "(ROOT (SINV (VP VBD) (NP NN NN) (NP NN NN) (PP IN (NP ";
	final static String TYPE_13 = "(ROOT (SQ VBD (NP NN) (VP VB (NP ";
	final static String TYPE_14 = "(ROOT (SBARQ (WHNP WP) (SQ (VP VBD (NP NN))) ))";
	final static String TYPE_15 = "(ROOT (SBARQ (WHNP WP) (SQ (VP VBD (NP (NP DT JJS NN) (PP IN (NP CD))))) ))";
	final static String TYPE_16 = "(ROOT (SBARQ (WHNP WDT NN) (SQ (VP VBD (NP DT NN) (PP IN (NP CD)))) ))";
	final static String TYPE_17 = "(ROOT (SBARQ (WHNP WDT (NP NN)) (SQ (VP VBD (NP DT NN) (PP IN (NP CD)))) ))";
	final static String TYPE_18 = "(ROOT (SBARQ (WHADVP WRB) (SQ VBD (NP NN) (VP VB (NP (NP DT NN) (PP IN (NP JJS NN))))) ))";
	final static String TYPE_19 = "(ROOT (SBARQ (WHNP WP) (SQ (VP VBD (NP DT NN) (PP IN (NP JJS NN)) (PP IN (NP CD)))) ))";
	final static String TYPE_20 = "(ROOT (S (S (VP VBD (NP DT NN) (PP IN (NP NN)))) (VP VBP (NP (NP DT NN) (PP IN (NP JJS NN)))) ))";
	final static String TYPE_21 = "(ROOT (SBARQ (WHNP WP) (SQ (VP VBD (NP ";

	//contains the info of all the parse tress associated with their lambda functions.
	static Map<String, Object> parseTreeToFunction = new HashMap<String, Object>();
	static Map<String, String> rulesMap = new HashMap<String, String>();
	static Map<String, String> semanticMap = new HashMap<String, String>();
	static StringBuilder sqlQuery = new StringBuilder();
	static StringBuilder selectQuery = new StringBuilder();

	public static void movieLambdas(Map<String, String> semantic, String currentQuestionParseTree, Map<String, String> rules) throws SQLException {

		sqlQuery = new StringBuilder();
		selectQuery = new StringBuilder();

		rulesMap = rules;
		semanticMap = semantic;

		//For questions like is Kubrick a director?
		BuildLambdaWithTwoArguments isPersonProperNounLambda = (X, Y) -> {

			if(X.toLowerCase().equals("actress") || X.toLowerCase().equals("actor")) {
				X = "actor";
			}
			else if(X.toLowerCase().equals("director")) {
				X = "director";
			}

			String query = "from person p inner join " + X + " s on p.id = s." + X +"_id where replace(p.name,' ','') like '%" + Y + "%';";
			return query;
		};

		//For questions like Is Shining by Kubrick?
		BuildLambdaWithTwoArguments isMovieByPersonLambda = (X, Y) -> {

			String query = "from person p inner join director d on p.id = d.director_id inner join movie m"
					+ " on m.id = d.movie_id where replace(p.name, ' ','') like '%" + Y +"%' and replace(m.name, ' ', '') like '%"
					+ X + "%';"; 
			return query;
		};

		//For questions like Did Neeson star in Schindler's List?
		BuildLambdaWithTwoArguments personAssociatedMovieLambda = (X, Y) -> {
			String query = "from person p inner join actor a on a.actor_id = p.id inner join movie m on m.id = "
					+ "a.movie_id where p.name like '%" + X + "%' and replace(m.name, ' ','') like '%" + Y + "%';"; 
			return query;
		};

		//For questions like Did Nolan direct Inception? 
		BuildLambdaWithTwoArguments personDirectedMovieLambda = (X, Y) -> {
			String query = "from person p inner join director d on d.director_id = p.id inner join movie m on m.id = "
					+ "d.movie_id where p.name like '%" + X + "%' and replace(m.name, ' ','') like '%" + Y + "%';"; 
			return query;
		};

		//For questions like Was Loen born in Italy?
		BuildLambdaWithTwoArguments didPersonBornHereLambda = (X, Y) -> {

			String query = "from person where name like '%" + X + "%' and pob like '%" + Y + "%';";
			return query;
		};

		//For questions like Did Swank win the oscar in 2000?
		BuildLambdaWithThreeArguments didEntityWinOscarLambda = (X, Y, Z) -> {
			String query = "from person where name like '%" + X + "%';";
			ResultSet rs = queryBuilder1(query);

			try {
				if(rs != null && rs.next()) {
					int sResult = rs.getInt(1);
					if(sResult >= 1) {
						query = "from person p inner join oscar o on o.person_id = p.id where o.year = '" 
								+ Z + "' and p.name like '%" + X + "%';";
					}
					else {
						query = "from movie m inner join oscar o on o.movie_id = m.id where o.year = '" 
								+ Z + "' and m.name like '%" + X + "%';";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return query;
		};

		//For questions like Did a movie with Nesson win the oscar for best film?
		BuildLambdaWithFourArguments didMovieWithOrByPersonWinOscarLambda = (X, D, Z, Y) -> {

			String type = "";
			String column = "";
			String query = "";

			for(String s : rulesMap.keySet()) {
				if(!s.startsWith("IN")) {
					continue;
				}
				Z = rulesMap.get(s);
				Z = Z.trim();
				break;
			}

			if(Y.toLowerCase().equals("actress")) {
				Y = "actor";
				type = "BEST-ACTRESS";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("actor")) {
				Y = "actor";
				type = "BEST-ACTOR";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("movie") || Y.toLowerCase().equals("film") || Y.toLowerCase().equals("picture")) {
				Y = "actor";
				type = "BEST-PICTURE";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("director")) {
				Y = "actor";
				type = "BEST-DIRECTOR";
				column = "movie_id";
			}
			if(Z.toLowerCase().equals("by")) {
				Y = "director";
			}
			query = "from oscar o inner join " + Y + " a on a.movie_id = o." + column 
					+ " inner join person p on p.id = a." + Y + "_id where p.name like '%" + D
					+ "%' and o.type like '%" + type + "%' ";
			return query;
		};

		//For questions like Was Birdman the best movie in 2015?
		BuildLambdaWithThreeArguments isEntityBestLambda = (X, Y, Z) -> {

			String type = "";
			String column = "";

			if(Y.toLowerCase().equals("actress")) {
				Y = "person";
				type = "BEST-ACTRESS";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("actor")) {
				Y = "person";
				type = "BEST-ACTOR";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("movie") || Y.toLowerCase().equals("film") || Y.toLowerCase().equals("picture")) {
				Y = "movie";
				type = "BEST-PICTURE";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("director")) {
				Y = "person";
				type = "BEST-DIRECTOR";
				column = "person_id";
			}

			String query = "from oscar o inner join " + Y + " m on m.id = o." + column + " where m.name like '%" 
					+ X + "%' and o.year = '" + Z + "' and o.type = '" + type + "';";
			return query;
		};

		//For questions like Who directed Schindler's List?
		BuildLambdaWithOneArguments whoDirectedMovie = (movie) ->
		{
			String query = "Select p.name from person p inner join director d on p.id = d.director_id inner join movie m"
					+ " on m.id = d.movie_id where replace(m.name, ' ', '') like '%"
					+ movie + "%';"; 
			return query;
		};

		//For questions like Who directed the best movie in 2010?
		BuildLambdaWithOneArguments whoDirectedBestMovieInYear = (year) ->
		{
			String query = "Select p.name from oscar o inner join director d on o.movie_id = d.movie_id inner join person p"
					+ " on p.id = d.director_id where o.type='BEST-PICTURE' and  o.year = " + year; 
			return query;


		};

		//For questions like Who won the Oscar for best actor in 2005?
		BuildLambdaWithThreeArguments WhoWonOscarForTypeInYear = (X, Y, Z) -> {

			String type = "";
			String column = "";

			if(Y.toLowerCase().equals("actress")) {
				Y = "person";
				type = "BEST-ACTRESS";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("actor")) {
				Y = "person";
				type = "BEST-ACTOR";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("movie") || Y.toLowerCase().equals("film") || Y.toLowerCase().equals("picture")) {
				Y = "movie";
				type = "BEST-PICTURE";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("director")) {
				Y = "person";
				type = "BEST-DIRECTOR";
				column = "person_id";
			}

			String query = "from oscar o inner join " + Y + " p on p.id = o." + column +  " and o.year = '" + Z + "' and o.type = '" + type + "';";
			return query;
		};


		//For questions like When did Blanchett win an oscar for best actress?
		BuildLambdaWithThreeArguments whenEntityWonForTypeLambda = (X, Y, Z) -> {

			String type = "";
			String column = "";

			if(Y.toLowerCase().equals("actress")) {
				Y = "person";
				type = "BEST-ACTRESS";
				column = "person_id";
			}

			else if (Y.toLowerCase().equals("actor")) {
				Y = "person";
				type = "BEST-ACTOR";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("movie") || Y.toLowerCase().equals("film") || Y.toLowerCase().equals("picture")) {
				Y = "movie";
				type = "BEST-PICTURE";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("director")) {
				Y = "person";
				type = "BEST-DIRECTOR";
				column = "person_id";
			}

			String query = "from oscar o inner join " + Y + " p on p.id = o." + column +  " and p.name like '%" + Z + "%' and o.type = '" + type + "';";
			return query;
		};

		//For questions like Who won the oscar for best actor in 2005?
		BuildLambdaWithThreeArguments isEntityBestInYearLambda = (X, Y, Z) -> {

			String type = "";
			String column = "";

			if(Y.toLowerCase().equals("actress")) {
				Y = "person";
				type = "BEST-ACTRESS";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("actor")) {
				Y = "person";
				type = "BEST-ACTOR";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("movie") || Y.toLowerCase().equals("film") || Y.toLowerCase().equals("picture")) {
				Y = "movie";
				type = "BEST-PICTURE";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("director")) {
				Y = "person";
				type = "BEST-DIRECTOR";
				column = "person_id";
			}

			String query = "from oscar o inner join " + Y + " p on p.id = o." + column +  " and o.year = '" + Z + "' and o.type = '" + type + "';";
			return query;
		};

		//For questions like Did a French actor win the oscar for 2012?
		BuildLambdaWithThreeArguments nationalEntityBestLambda = (Y, Z, D) -> {
			String X = "";
			for(String s : rulesMap.keySet()) {
				if(!s.startsWith("JJ")) {
					continue;
				}
				X = rules.get(s);
				break;
			}

			String type = "";
			String column = "";
			String query = "";
			X = X.trim();

			if(X.toLowerCase().equals("french")) {
				X = "France";
			}
			else if(X.toLowerCase().equals("american")) {
				X = "USA";
			}
			else if(X.toLowerCase().equals("german")) {
				X = "Germany";
			}
			else if(X.toLowerCase().equals("italian")) {
				X = "Italy";
			}
			else if(X.toLowerCase().equals("british")) {
				X = "UK";
			}

			if(Y.toLowerCase().equals("actress")) {
				Y = "person";
				type = "BEST-ACTRESS";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("actor")) {
				Y = "person";
				type = "BEST-ACTOR";
				column = "person_id";
			}
			else if (Y.toLowerCase().equals("movie") || Y.toLowerCase().equals("film") || Y.toLowerCase().equals("picture")) {
				Y = "movie";
				type = "BEST-PICTURE";
				column = "movie_id";
			}
			else if (Y.toLowerCase().equals("director")) {
				Y = "person";
				type = "BEST-DIRECTOR";
				column = "person_id";
			}
			if(Y.equals("person")) {
				query = "from oscar o inner join " + Y + " m on m.id = o." + column 
						+ " where m.pob like '%" + X + "%' and o.year = '" + D + "' and o.type = '" + type +"'";
			}
			else {
				query = "from oscar o inner join " + Y + " m on m.id = o." + column 
						+ " inner join director d on d.movie_id = m.id inner join person p on p.id = d.director_id "
						+ "where p.pob like '%" + X + "%' and o.year = '" + D + "' and o.type = '" + type +"'";
			}
			return query;
		};

		Object isPersonProperNounLambdaObject = isPersonProperNounLambda;
		Object isMovieByPersonLambdaObject = isMovieByPersonLambda;
		Object didPersonBornHereLambdaObject = didPersonBornHereLambda;
		Object isEntityBestLambdaObject = isEntityBestLambda;
		Object didEntityWinOscarLambdaObject = didEntityWinOscarLambda;
		Object personAssociatedMovieLambdaObject = personAssociatedMovieLambda;

		Object whoDirectedMovieObject = whoDirectedMovie;
		Object WhoDirectedBestMovieInYearObject = whoDirectedBestMovieInYear;
		Object WhichEntityWonOscarInYearObject = isEntityBestInYearLambda;
		Object whenEntityWonForTypeObject = whenEntityWonForTypeLambda;
		Object WhoWonOscarForTypeInYearObject = WhoWonOscarForTypeInYear;
		Object didMovieWithOrByPersonWinOscarLambdaObject = didMovieWithOrByPersonWinOscarLambda;
		Object personDirectedMovieLambdaObject = personDirectedMovieLambda;
		Object nationalEntityBestLambdaObject = nationalEntityBestLambda;

		parseTreeToFunction.put(TYPE_1, isMovieByPersonLambdaObject);
		parseTreeToFunction.put(TYPE_2, isPersonProperNounLambdaObject);
		parseTreeToFunction.put(TYPE_3, didPersonBornHereLambdaObject);
		parseTreeToFunction.put(TYPE_4, didPersonBornHereLambdaObject);
		parseTreeToFunction.put(TYPE_5, didEntityWinOscarLambdaObject);
		parseTreeToFunction.put(TYPE_6, isEntityBestLambdaObject);
		parseTreeToFunction.put(TYPE_7, personDirectedMovieLambdaObject);
		parseTreeToFunction.put(TYPE_8, personAssociatedMovieLambdaObject);
		parseTreeToFunction.put(TYPE_9 ,nationalEntityBestLambdaObject);

		parseTreeToFunction.put(TYPE_14, whoDirectedMovieObject);
		parseTreeToFunction.put(TYPE_15, WhoDirectedBestMovieInYearObject);
		parseTreeToFunction.put(TYPE_16, WhichEntityWonOscarInYearObject);
		parseTreeToFunction.put(TYPE_17, WhichEntityWonOscarInYearObject);
		parseTreeToFunction.put(TYPE_18, whenEntityWonForTypeObject);
		parseTreeToFunction.put(TYPE_19, WhoWonOscarForTypeInYearObject);
		parseTreeToFunction.put(TYPE_20,didMovieWithOrByPersonWinOscarLambdaObject);

		currentQuestionParseTree = currentQuestionParseTree.replaceAll("\\d", "");
		currentQuestionParseTree = currentQuestionParseTree.replaceAll("NN[A-Z]*", "NN");

		Object function = parseTreeToFunction.get(currentQuestionParseTree);

		if (function == null){
			if(currentQuestionParseTree.startsWith(TYPE_10)) {
				function = personAssociatedMovieLambdaObject;
				buildLambda(function, rulesMap);
			}
			else if(currentQuestionParseTree.startsWith(TYPE_11)){
				function = personAssociatedMovieLambdaObject;
				buildLambdaSecondAlternate(function, rulesMap);
			}
			else if(currentQuestionParseTree.startsWith(TYPE_12)) {
				function = personAssociatedMovieLambdaObject;
				buildLambdaThirdAlternate(function, rulesMap);
			}
			else if(currentQuestionParseTree.startsWith(TYPE_13)) {
				function = personDirectedMovieLambdaObject;
				buildLambda(function, rulesMap);
			}
			else if(currentQuestionParseTree.startsWith(TYPE_21)) {
				function = whoDirectedMovieObject;
				buildLambdaWithOneArg(function, rulesMap);
			}
			/*else if(currentQuestionParseTree.contains("(PP IN (NP NN))")) {
				function = isMovieByPersonLambdaObject;
				buildLambda(function, rulesMap);
			}*/
			else {
				CS421_Project2.sqlQuery = "I do not know";
				CS421_Project2.answer =  "I do not know";
				return;
			}
		}
		else if(function != null && function.equals(isPersonProperNounLambdaObject)) {
			buildLambdaAlternate(function, rulesMap);
		}
		else if(function != null && function.equals(isMovieByPersonLambdaObject)) {
			buildLambda(function, rulesMap);
		}
		else if(function != null && function.equals(didPersonBornHereLambdaObject)) {
			buildLambda(function, rulesMap);
		}
		else if(function != null && function.equals(isEntityBestLambdaObject)) {
			buildLambdaOscar(function, rulesMap);
		}
		else if(function != null && function.equals(didEntityWinOscarLambdaObject)) {
			buildLambdaOscar(function, rulesMap);
		}
		else if(function != null && function.equals(personAssociatedMovieLambdaObject)) {
			buildLambda(function, rulesMap);
		}
		else if(function != null && function.equals(personDirectedMovieLambdaObject)) {
			buildLambda(function, rulesMap);
		}
		else if(function != null && function.equals(nationalEntityBestLambdaObject)) {
			buildLambdaOscar(function, rulesMap);
		}

		else if(function != null && function.equals(whoDirectedMovieObject)) {
			buildLambdaWithOneArg(function, rulesMap);
		}
		else if(function != null && function.equals(whoDirectedBestMovieInYear)) {
			buildLambdaWithOneArgForYear(function, rulesMap);
		}
		else if(function != null && function.equals(WhichEntityWonOscarInYearObject)) {
			buildLambdaOscarForName(function, rulesMap);
		}
		else if(function != null && function.equals(whenEntityWonForTypeObject)) {
			buildLambdaOscarForYear(function, rulesMap);
		}
		else if(function != null && function.equals(WhoWonOscarForTypeInYearObject)) {
			buildLambdaForWhoWonOscarForTypeInYear(function, rulesMap);
		}
		else if(function != null && function.equals(didMovieWithOrByPersonWinOscarLambdaObject)) {
			buildLambdaFour(function, rulesMap);
		}
	}

	//Now we use helpers for Lambda functions to extract the semantics and send it to Lambda functions.
	//getNounSemantic goes inside every rule and collects the useful semantics.
	//As discussed in the report, it does not assemble the determiners like 'a, an', etc.
	public static void buildLambdaAlternate(Object function, Map<String, String> rulesMap) throws SQLException {

		//We start with the starting rule and do a DFS to extract the semantics and again build it bottom-up with the lambda function.
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(1);
		String noun2 = resultNouns.get(0);
		noun1 = noun1.trim();
		noun2 = noun2.trim();

		String lambdaQuery = ((BuildLambdaWithTwoArguments) function).getSqlQuery(noun1, noun2);

		queryBuilder(lambdaQuery);
	}

	public static void buildLambda(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		noun1 = noun1.trim();
		noun2 = noun2.trim();

		String lambdaQuery = ((BuildLambdaWithTwoArguments) function).getSqlQuery(noun1, noun2);

		queryBuilder(lambdaQuery);
	}

	public static void buildLambdaSecondAlternate(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(3);
		noun1 = noun1.trim();
		noun2 = noun2.trim();

		String lambdaQuery = ((BuildLambdaWithTwoArguments) function).getSqlQuery(noun1, noun2);

		queryBuilder(lambdaQuery);
	}

	public static void buildLambdaThirdAlternate(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(4);
		noun1 = noun1.trim();
		noun2 = noun2.trim();

		String lambdaQuery = ((BuildLambdaWithTwoArguments) function).getSqlQuery(noun1, noun2);

		queryBuilder(lambdaQuery);
	}


	public static void buildLambdaOscar(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		String noun3 = resultNouns.get(2);
		noun1 = noun1.trim();
		noun2 = noun2.trim();
		noun3 = noun3.trim();

		String lambdaQuery = ((BuildLambdaWithThreeArguments) function).getSqlQuery(noun1, noun2, noun3);

		queryBuilder(lambdaQuery);
	}

	public static void buildLambdaFour(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		String noun3 = resultNouns.get(2);
		String noun4 = resultNouns.get(3);
		noun1 = noun1.trim();
		noun2 = noun2.trim();
		noun3 = noun3.trim();
		noun4 = noun4.trim();

		String lambdaQuery = ((BuildLambdaWithFourArguments) function).getSqlQuery(noun1, noun2, noun3, noun4);

		queryBuilder(lambdaQuery);
	}

	public static void buildLambdaWithOneArg(Object function, Map<String, String> rulesMap)throws SQLException
	{
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		noun1 = noun1.trim();

		String lambdaQuery = ((BuildLambdaWithOneArguments) function).getSqlQuery(noun1);

		executeQuery(lambdaQuery);
	}

	public static void buildLambdaWithOneArgForYear(Object function, Map<String, String> rulesMap)throws SQLException
	{
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(1);
		noun1 = noun1.trim();

		String lambdaQuery = ((BuildLambdaWithOneArguments) function).getSqlQuery(noun1);

		executeQuery(lambdaQuery);
	}

	public static void buildLambdaOscarForName(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		String noun3 = resultNouns.get(2);
		noun1 = noun1.trim();
		noun2 = noun2.trim();
		noun3 = noun3.trim();

		String lambdaQuery = ((BuildLambdaWithThreeArguments) function).getSqlQuery(noun2, noun1, noun3);

		executeQueryForName(lambdaQuery);
	}

	public static void buildLambdaOscarForYear(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		String noun3 = resultNouns.get(2);
		noun1 = noun1.trim();
		noun2 = noun2.trim();
		noun3 = noun3.trim();

		String lambdaQuery = ((BuildLambdaWithThreeArguments) function).getSqlQuery(noun2, noun3, noun1);

		executeQueryForYear(lambdaQuery);
	}

	public static void buildLambdaForWhoWonOscarForTypeInYear(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		String noun3 = resultNouns.get(2);
		noun1 = noun1.trim();
		noun2 = noun2.trim();
		noun3 = noun3.trim();

		String lambdaQuery = ((BuildLambdaWithThreeArguments) function).getSqlQuery(noun1, noun2, noun3);

		executeQueryForName(lambdaQuery);
	}

	public static void buildLambdaForWhichEntityWonInYear(Object function, Map<String, String> rulesMap) throws SQLException {
		String mainSemantic = rulesMap.get("Start");
		mainSemantic = mainSemantic.trim();
		String parts[] = mainSemantic.split("\\s");
		LinkedList<String> resultNouns = new LinkedList<String>();
		getNounSemantic(rulesMap, parts, resultNouns, null);

		String noun1 = resultNouns.get(0);
		String noun2 = resultNouns.get(1);
		String noun3 = resultNouns.get(2);
		noun1 = noun1.trim();
		noun2 = noun2.trim();
		noun3 = noun3.trim();

		String lambdaQuery = ((BuildLambdaWithThreeArguments) function).getSqlQuery(noun2, noun1, noun3);

		executeQueryForYear(lambdaQuery);
	}

	//Starts from the first Rule i.e. 'Start -> . . .' and does a DFS to get all the nouns and dates and other useful things for lambda functions. 
	public static void getNounSemantic(Map<String, String> rulesMap, String parts[], LinkedList<String> resultNouns, String oldRule) {
		String rule = "";
		String currentParts[];
		for(int i = 0; i < parts.length; ++i) {
			rule = parts[i];
			rule = rule.trim();
			if(rulesMap.containsKey(rule)) {
				oldRule = rule;
				rule = rulesMap.get(rule);
				currentParts = rule.split("\\s");
				getNounSemantic(rulesMap, currentParts, resultNouns, oldRule);
			}
			else if(oldRule != null && oldRule.startsWith("NN")) {
				rule = rulesMap.get(oldRule);
				resultNouns.add(rule);
			}
			else if(oldRule != null && oldRule.startsWith("CD")) {
				rule = rulesMap.get(oldRule);
				resultNouns.add(rule);
			}
		}
		return;
	}

	public static void queryBuilder(String lambdaQuery) throws SQLException {
		selectQuery = new StringBuilder();
		selectQuery.append("Select count(*) ");
		sqlQuery = new StringBuilder();
		sqlQuery.append(selectQuery);
		sqlQuery.append(lambdaQuery);

		ResultSet rs = JdbcSQLiteConnection.executeQuery(sqlQuery.toString());
		CS421_Project2.sqlQuery = sqlQuery.toString();
		while(rs != null && rs.next()) {
			int sResult = rs.getInt(1);
			if (sResult >= 1)
				CS421_Project2.answer = "YES";
			if(sResult == 0)
				CS421_Project2.answer = "NO";
		}
	}

	public static ResultSet queryBuilder1(String query) {
		selectQuery = new StringBuilder();
		selectQuery.append("Select count(*) ");
		sqlQuery = new StringBuilder();
		sqlQuery.append(selectQuery);
		sqlQuery.append(query);

		ResultSet rs = JdbcSQLiteConnection.executeQuery(sqlQuery.toString());
		return rs;
	}

	public static void executeQuery(String lambdaQuery) throws SQLException {

		ResultSet rs = JdbcSQLiteConnection.executeQuery(lambdaQuery);
		CS421_Project2.sqlQuery = lambdaQuery;
		CS421_Project2.answer= "";
		while(rs != null && rs.next()) 
		{
			CS421_Project2.answer = rs.getString("name") + " " + CS421_Project2.answer;
		}
	}

	public static void executeQueryForName(String lambdaQuery) throws SQLException
	{
		lambdaQuery = "select p.name " + lambdaQuery;
		ResultSet rs = JdbcSQLiteConnection.executeQuery(lambdaQuery);
		CS421_Project2.sqlQuery = lambdaQuery;
		CS421_Project2.answer= "";
		while(rs != null && rs.next()) 
		{
			CS421_Project2.answer = rs.getString("name") + " " + CS421_Project2.answer;

		}
	}

	public static void executeQueryForYear(String lambdaQuery) throws SQLException
	{
		lambdaQuery = "select o.year " + lambdaQuery;
		ResultSet rs = JdbcSQLiteConnection.executeQuery(lambdaQuery);
		CS421_Project2.sqlQuery = lambdaQuery;
		CS421_Project2.answer= "";
		while(rs != null && rs.next()) 
		{
			CS421_Project2.answer = rs.getString("year") + " " + CS421_Project2.answer;
		}
	}
}
