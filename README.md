# Database Management System

## Introduction

In this project we were asked to build a Database Management System (DBMS) using Java. Everything was built from scratch. Only we used the [BPTree](https://github.com/AhmadElsagheer/DBMS/tree/master/SagSolheeriman-A1/src/BPTree) package from [Ahmad Elsagheer](https://github.com/AhmadElsagheer/DBMS) because we did not have much time to implement it from scratch.

## Usage

In the `DBAppTest.java` file you will find in the main method several examples that were used for testing.

### Supported Data Types

- java.lang.Integer
- java.lang.String
- java.lang.double
- java.lang.Boolean
- java.util.Date
- java.awt.polygon

### Creating table with some columns

```java
DBApp dbApp = new DBApp(); // Create an instance
dbApp.init(); // Initialize that instance which initialize the files and everything

// Set the columns names and data types
Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
htblColNameType.put("id", "java.lang.Integer");
htblColNameType.put("name", "java.lang.String");
htblColNameType.put("gpa", "java.lang.double");
htblColNameType.put("male", "java.lang.Boolean");
htblColNameType.put("birthday", "java.util.Date");
htblColNameType.put("poly", "java.awt.polygon");

// Create the table with the "name" as the primary key
String strTableName = "students";
dbApp.createTable(strTableName, "name", htblColNameType);

```

### Making a simple query

```java
// SELECT * FROM students WHERE id <= "60-2513"
arrSQLTerms = new SQLTerm[1];
arrSQLTerms[0] = new SQLTerm();
arrSQLTerms[0]._strTableName = "students";
arrSQLTerms[0]._strColumnName = "id";
arrSQLTerms[0]._strOperator = "<=";
arrSQLTerms[0]._objValue = "60-2513";

Iterator iterator = dbApp.selectFromTable(arrSQLTerms, null); // returns an iterator with all the rows that match the query

```

You can also find more in the `Project Description` file
