package Team_4;
import java.awt.Polygon;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

public class DBAppTest
{

	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static void main(String[] args)
			throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException
	{
		String strTableName = "Student";

//		DBApp dbApp = new DBApp();
//		dbApp.init();

//		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
//		htblColNameType.put("id", "java.lang.Integer");
//		htblColNameType.put("name", "java.lang.String");
//		htblColNameType.put("gpa", "java.lang.double");
//		htblColNameType.put("male", "java.lang.Boolean");
//		htblColNameType.put("birthday", "java.util.Date");
//		htblColNameType.put("poly", "java.awt.polygon");
//		
//		// Cheak key name exist
//		dbApp.createTable(strTableName, "name", htblColNameType);
//		dbApp.createBTreeIndex(strTableName, "gpa");
//		dbApp.createBTreeIndex(strTableName, "male");
//		dbApp.createBTreeIndex(strTableName, "birthday");
//		dbApp.createRTreeIndex(strTableName, "poly");
//		
////		 Inserting 200 random generated rows
//		for(int i = 0; i < 20; i++)
//		{
//			Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
//			
//			htblColNameValue.put("id", new Integer((int) (Math.random() * ((200 - 1) +1)) + 1));
////			htblColNameValue.put("id", new Integer(10));
//			
//			if(i < 5)
//				htblColNameValue.put("name", new String("Ahmed Basha Ashruf Beh"));
//			else
//				htblColNameValue.put("name", new String(generateString(new Random(), "abcdefghijklmnopqrstuvwxyz", 10)));
//
//			htblColNameValue.put("gpa", new Double((Math.random() * ((200 - 1) +1)) + 1));
//	
//			htblColNameValue.put("male", i % 2 == 0 ? new Boolean(true) : new Boolean(false));
//			
//			htblColNameValue.put("birthday", new Date(Date.parse(createRandomDate(1850, 2020))));
//			
////			int random1 = (int) (Math.random() * ((200 - 1) +1)) + 1;
////			int random2 = (int) (Math.random() * ((200 - 1) +1)) + 1;
////			int random3 = (int) (Math.random() * ((200 - 1) +1)) + 1;
////			int random4 = (int) (Math.random() * ((200 - 1) +1)) + 1;
////			int[] xpoints = {random1, random2 * 2, random3 * 3, random4 * 4};
////			int[] ypoints = {random1, random2 * 2, random3 * 3, random4 * 4};
////			htblColNameValue.put("poly", new Polygon(xpoints, ypoints, xpoints.length));
//
//			int[] xpoints = {10, 30, 40, 50};
//			int[] ypoints = {20, 30, 40, 60};
//			htblColNameValue.put("poly", new Polygon(xpoints, ypoints, 4));
//		
//			dbApp.insertIntoTable(strTableName, htblColNameValue);
//		}

//		Hashtable<String, Object> testColUpdate = new Hashtable<String, Object>();
//		testColUpdate.put("id", new Integer(321));
//		testColUpdate.put("male", new Boolean(false));
//		testColUpdate.put("gpa", new Double(0.7));
//		int[] xpoints = {20, 30, 40, 60, 12};
//		int[] ypoints = {20, 30, 40, 60, 12};
//		testColUpdate.put("poly", new Polygon(xpoints, ypoints, 5));
//		testColUpdate.put("birthday", new Date(Date.parse(createRandomDate(1850, 2020))));
//		testColUpdate.put("name", new String("Ahmed Basha Ashruf Beh"));
//		dbApp.updateTable(strTableName, "Ahmed Basha Ashruf Beh", testColUpdate);

//		Hashtable<String, Object> testColDelete = new Hashtable<String, Object>();
//		testColDelete.put("id", new Integer(158));
//		testColDelete.put("male", new Boolean(true));
//		testColDelete.put("name", new String("Ahmed Basha Ashruf Beh"));
//		dbApp.deleteFromTable(strTableName, testColDelete);

//		SQLTerm[] arrSQLTerms = new SQLTerm[3];
//		
//		arrSQLTerms[0] = new SQLTerm();
//		arrSQLTerms[0]._strTableName = "Student";
//		arrSQLTerms[0]._strColumnName = "name";
//		arrSQLTerms[0]._strOperator = "=";
//		arrSQLTerms[0]._objValue = "Ahmed Basha Ashruf Beh";
//		
////		arrSQLTerms[1] = new SQLTerm();
////		arrSQLTerms[1]._strTableName = "Student";
////		arrSQLTerms[1]._strColumnName = "id";
////		arrSQLTerms[1]._strOperator = "=";
////		arrSQLTerms[1]._objValue = new Integer(8);
//		
////		arrSQLTerms[2] = new SQLTerm();
////		arrSQLTerms[2]._strTableName = "Student";
////		arrSQLTerms[2]._strColumnName = "male";
////		arrSQLTerms[2]._strOperator = "=";
////		arrSQLTerms[2]._objValue = new Boolean(true);
//		
//		String[]strarrOperators = new String[1];
//		strarrOperators[0] = "AND";
//
//		Iterator resultSet = dbApp.selectFromTable(arrSQLTerms , strarrOperators);
//		
//		while (resultSet.hasNext())
//			System.out.println(resultSet.next());
//		
//		
////		System.out.println(common.getTable(strTableName));
////		System.out.println(common.getTable(strTableName).colNameIndex.get("id"));
////		System.out.println(common.getTable(strTableName).colNameIndex.get("male"));
////		System.out.println(common.getTable(strTableName).colNameIndex.get("gpa"));
////		System.out.println(common.getTable(strTableName).colNameIndex.get("poly"));
////		System.out.println(common.getTable(strTableName).colNameIndex.get("birthday"));
//		
//		
////		dbApp.createBTreeIndex(strTableName, "gpa");
////		dbApp.createBTreeIndex(strTableName, "name");
////		dbApp.createBTreeIndex(strTableName, "male");
////		dbApp.createBTreeIndex(strTableName, "id");
////		dbApp.createBTreeIndex(strTableName, "gpa");
////		dbApp.createRTreeIndex(strTableName, "poly");

		DBApp dbApp = new DBApp();
//		System.out.println(common.getTable("students"));
		SQLTerm[] arrSQLTerms;
		arrSQLTerms = new SQLTerm[1];
		arrSQLTerms[0] = new SQLTerm();
		arrSQLTerms[0]._strTableName = "students";
		arrSQLTerms[0]._strColumnName = "id";
		arrSQLTerms[0]._strOperator = "<=";
		arrSQLTerms[0]._objValue = "60-2513";

		String[] strarrOperators = new String[0];

// select * from Student where n

		Iterator iterator = dbApp.selectFromTable(arrSQLTerms, strarrOperators);

		while (iterator.hasNext())
		{
			System.out.println(iterator.next());
		}
	}

	public static String generateString(Random random, String characters, int length)
	{
		char[] text = new char[length];
		for (int i = 0; i < length; i++)
		{
			text[i] = characters.charAt(random.nextInt(characters.length()));
		}
		return new String(text);
	}

	public static int createRandomIntBetween(int start, int end)
	{
		return start + (int) Math.round(Math.random() * (end - start));
	}

	@SuppressWarnings("deprecation")
	public static String createRandomDate(int startYear, int endYear)
	{
		int day = createRandomIntBetween(1, 28);
		int month = createRandomIntBetween(1, 12);
		int year = createRandomIntBetween(startYear, endYear);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(new java.util.Date(year - 1900, month - 1, day));
	}
}

// For intial testing only

//int[] xpoints = {i * 10, (i + 1) * 10, (i + 2) * 10};
//int[] ypoints = {i * 10, (i + 1) * 10, (i + 2) * 10};
//
//for(int j = 0; j < xpoints.length; j++)
//	System.out.print(xpoints[j] + " ");
//
//System.out.println();
//
//for(int j = 0; j < ypoints.length; j++)
//	System.out.print(ypoints[j] + " ");
//
//System.out.println();
//System.out.println("=======");

//Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
//htblColNameType.put("id", "java.lang.Integer");
//htblColNameType.put("name", "java.lang.String");
//htblColNameType.put("gpa", "java.lang.double");
//
//Hashtable<String, Object> htblColNameValue1 = new Hashtable<String, Object>( );
//htblColNameValue1.put("id", new Integer( 454355 ));
//htblColNameValue1.put("name", new String("Student 1"));
//htblColNameValue1.put("gpa", new Double( 0.95 ) );
//
//Hashtable<String, Object> htblColNameValue2 = new Hashtable<String, Object>( );
//htblColNameValue2.put("id", new Integer( 456355 ));
//htblColNameValue2.put("name", new String("Student 3"));
//htblColNameValue2.put("gpa", new Double( 0.95 ) );
//
//Hashtable<String, Object> htblColNameValue3 = new Hashtable<String, Object>( );
//htblColNameValue3.put("id", new Integer( 453855 ));
//htblColNameValue3.put("name", new String("Student 1"));
//htblColNameValue3.put("gpa", new Double( 0.75 ) );
//
//Hashtable<String, Object> htblColNameValue4 = new Hashtable<String, Object>( );
//htblColNameValue4.put("id", new Integer( 453155 ));
//htblColNameValue4.put("name", new String("Student 7" ) );
//htblColNameValue4.put("gpa", new Double( 0.85 ) );
//
//// Create and insert
////dbApp.createTable(strTableName, "gpa", htblColNameType);
////dbApp.insertIntoTable(strTableName, htblColNameValue1);
////dbApp.insertIntoTable(strTableName, htblColNameValue2);
////dbApp.insertIntoTable(strTableName, htblColNameValue3);
////dbApp.insertIntoTable(strTableName, htblColNameValue4);
////System.out.println("Done 1");
//
////dbApp.insertIntoTable(strTableName, htblColNameValue1);
////dbApp.insertIntoTable(strTableName, htblColNameValue2);
////dbApp.insertIntoTable(strTableName, htblColNameValue3);
////dbApp.insertIntoTable(strTableName, htblColNameValue4);
////System.out.println("Done 2");
//
////dbApp.insertIntoTable(strTableName, htblColNameValue1);
////dbApp.insertIntoTable(strTableName, htblColNameValue2);
////System.out.println("Done 3");
////
//// Update a row
////htblColNameValue2.put("id", new Integer(1234));
////dbApp.updateTable(strTableName, "Student 3", htblColNameValue2);
//
//// Delete a row
////htblColNameValue1.put("id", new Integer(1234));
////dbApp.deleteFromTable(strTableName, htblColNameValue1);
//
//// Insert after deleting the row
////dbApp.insertIntoTable(strTableName, htblColNameValue3);

//Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
//htblColNameType.put("id", "java.lang.Integer");
//htblColNameType.put("name", "java.lang.String");
//htblColNameType.put("gpa", "java.lang.double");
//htblColNameType.put("male", "java.lang.boolean");
////htblColNameType.put("ploy", "java.awt.Polygon");
//dbApp.createTable(strTableName, "id", htblColNameType);
//
//Hashtable<String, Object> htblColNameValue1 = new Hashtable<String, Object>( );
//htblColNameValue1.put("id", new Integer(0));
//htblColNameValue1.put("name", new String("Student 1"));
//htblColNameValue1.put("gpa", new Double( 0.95 ));
//htblColNameValue1.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue2 = new Hashtable<String, Object>( );
//htblColNameValue2.put("id", new Integer(1));
//htblColNameValue2.put("name", new String("Student 2"));
//htblColNameValue2.put("gpa", new Double( 0.95 ));
//htblColNameValue2.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue3 = new Hashtable<String, Object>( );
//htblColNameValue3.put("id", new Integer(195));
//htblColNameValue3.put("name", new String("Student 3"));
//htblColNameValue3.put("gpa", new Double( 0.95 ));
//htblColNameValue3.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue4 = new Hashtable<String, Object>( );
//htblColNameValue4.put("id", new Integer(3));
//htblColNameValue4.put("name", new String("Student 4"));
//htblColNameValue4.put("gpa", new Double( 0.95 ));
//htblColNameValue4.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue5 = new Hashtable<String, Object>( );
//htblColNameValue5.put("id", new Integer(5));
//htblColNameValue5.put("name", new String("Student 4"));
//htblColNameValue5.put("gpa", new Double( 0.95 ));
//htblColNameValue5.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue6 = new Hashtable<String, Object>( );
//htblColNameValue6.put("id", new Integer(199));
//htblColNameValue6.put("name", new String("Student 4"));
//htblColNameValue6.put("gpa", new Double( 0.95 ));
//htblColNameValue6.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue7 = new Hashtable<String, Object>( );
//htblColNameValue7.put("id", new Integer(7));
//htblColNameValue7.put("name", new String("Student 4"));
//htblColNameValue7.put("gpa", new Double( 0.95 ));
//htblColNameValue7.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue8 = new Hashtable<String, Object>( );
//htblColNameValue8.put("id", new Integer(8));
//htblColNameValue8.put("name", new String("Student 4"));
//htblColNameValue8.put("gpa", new Double( 0.95 ));
//htblColNameValue8.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue9 = new Hashtable<String, Object>( );
//htblColNameValue9.put("id", new Integer(9));
//htblColNameValue9.put("name", new String("Student 4"));
//htblColNameValue9.put("gpa", new Double( 0.95 ));
//htblColNameValue9.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue10 = new Hashtable<String, Object>( );
//htblColNameValue10.put("id", new Integer(9));
//htblColNameValue10.put("name", new String("Student 4"));
//htblColNameValue10.put("gpa", new Double( 0.95 ));
//htblColNameValue10.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue11 = new Hashtable<String, Object>( );
//htblColNameValue11.put("id", new Integer(10));
//htblColNameValue11.put("name", new String("Student 4"));
//htblColNameValue11.put("gpa", new Double( 0.95 ));
//htblColNameValue11.put("male", new Boolean(true));
//
//Hashtable<String, Object> htblColNameValue12 = new Hashtable<String, Object>( );
//htblColNameValue12.put("id", new Integer(195));
//htblColNameValue12.put("name", new String("Student 4"));
//htblColNameValue12.put("gpa", new Double( 0.95 ));
//htblColNameValue12.put("male", new Boolean(true));
//
//dbApp.insertIntoTable(strTableName, htblColNameValue1);
//dbApp.insertIntoTable(strTableName, htblColNameValue2);
//dbApp.insertIntoTable(strTableName, htblColNameValue3);
//dbApp.insertIntoTable(strTableName, htblColNameValue4);
//dbApp.insertIntoTable(strTableName, htblColNameValue5);
//dbApp.insertIntoTable(strTableName, htblColNameValue6);
//dbApp.insertIntoTable(strTableName, htblColNameValue7);
//dbApp.insertIntoTable(strTableName, htblColNameValue8);
//dbApp.insertIntoTable(strTableName, htblColNameValue9);
//dbApp.insertIntoTable(strTableName, htblColNameValue10);
//dbApp.insertIntoTable(strTableName, htblColNameValue11);
//dbApp.insertIntoTable(strTableName, htblColNameValue12);