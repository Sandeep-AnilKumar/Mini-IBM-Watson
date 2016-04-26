Name: - Pavan Kumar Kothagorla
UIN : - 675007844

Name : - Sandeep Kumar Anil Kumar
UIN : - 650005255

About The code:
--------------
TheJar conatins the following Java files
1) CS421_Project2.java : This is the driver class. It takes the input question and invokes the methods I wrote.
   Global varibles for storing the query and answer are set. Those varibles are assigned corresponding values in my code.
   When printSQL() and printAnswer() aer called, sqlquery and the answer is printed.
   
2) ParsingQuestions.java :This contains code to extract the parse tree using Stanford Core NLP

3) QueryBuilderPrint.java : This contains code to extarct actual grammmar rules from tree and do semantic attachments, which on doing lambda reduction will give teh sql Query.

4) MovieDomainLambda.java : This contains the lambda function for the semantic rules. When a new question is asked, we perform lambda reduction and generate the sql query and get answer from database.

5) JdbcSQLiteConnection.java : This is a helper class for connectiong to sqlite database from java.

Just run the jar as "java -jar <jar-name>" and give the questions.

Thank you for your time and have a great day.



