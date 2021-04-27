package Team_4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class DBApp
{
	private ArrayList<String> supportedTypes = new ArrayList<String>();

	public void init()
	{
		// Adding the supported types
		supportedTypes.add("java.lang.integer");
		supportedTypes.add("java.lang.string");
		supportedTypes.add("java.lang.double");
		supportedTypes.add("java.lang.boolean");
		supportedTypes.add("java.util.date");
		supportedTypes.add("java.awt.polygon");

		// Creating "data" directory if it doesn't exist
		if (!(new File("data").exists()))
		{
			File dataDirectory = new File("data");
			dataDirectory.mkdir();
		}

		// Creating "tables" directory if it doesn't exist
		if (!(new File("data/tables").exists()))
		{
			File dataDirectory = new File("data/tables");
			dataDirectory.mkdir();
		}

		// Creating the csv file if it doesn't exist
		if (!(new File("data/metadata.csv").exists()))
		{
			FileWriter csvWriter;
			try
			{
				csvWriter = new FileWriter("data/metadata.csv");
				ArrayList<String> metadataHeader = new ArrayList<String>();
				metadataHeader.add("Table Name");
				metadataHeader.add("Column Name");
				metadataHeader.add("Column Type");
				metadataHeader.add("ClusteringKey");
				metadataHeader.add("Indexed");
				csvWriter.append(String.join(",", metadataHeader));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException
	{
		// Check if keyName is correct
		Set<String> keySet = htblColNameType.keySet();
		Object[] keyArray = keySet.toArray();
		boolean clusterKeyExist = false;
		for (int i = 0; i < keyArray.length; i++)
			if (keyArray[i].equals(strClusteringKeyColumn))
				clusterKeyExist = true;

		// Check the Clustering key value does exist
		if (!clusterKeyExist)
			throw new DBAppException("The key you entered (" + strClusteringKeyColumn
					+ ") doesn't have a corresponding column in the table columns");

		// Check Supported NameType & Throw if not supported
		if (common.nameTypeSupported(htblColNameType, this.supportedTypes))
		{

			// Check Table Existence
			if (!common.existInMetadata(strTableName, common.getMetadata()))
			{
				// Add the new table to the metadata
				ArrayList<ArrayList<String>> metadata = common.prepareTheMetadata(strTableName, strClusteringKeyColumn,
						htblColNameType);
				common.setMetadata(metadata, true);

				// Create directory to store the table and pages in it
				File dataDirectory = new File("data/tables/" + strTableName);
				dataDirectory.mkdir();

				// Creating the table and Writing it to the folder
				table newTable = new table(strTableName, strClusteringKeyColumn, htblColNameType);
				newTable.writeTable();
//				System.out.println("The table (" + strTableName + ") was created successfully");
			}

			// Throw error that there is a table with this name already exists
			else
			{
				throw new DBAppException("There is a table already exists with this name: " + strTableName);
			}
		}
	}

	public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException
	{
		// Check The Table Existence
		if (common.existInMetadata(strTableName, common.getMetadata()))
		{
			table insertionTable;
			try
			{
				insertionTable = common.getTable(strTableName);
				// Check that all columns are present
				if (htblColNameValue.size() != insertionTable.getTableHeaders().size())
					throw new DBAppException("There are some missing attributes, Please re-check your record");

				// Check all input values are supported & if not it will throw inside
				if (common.nameValueSupported(insertionTable.getTableHeaders(), htblColNameValue))
				{

					// Add the touchDate
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					htblColNameValue.put("TouchDate", "" + sdf.format(new java.util.Date()));

					// Insert into the table then write it to the directory
					insertionTable.insertIntoTable(htblColNameValue);
					insertionTable.writeTable();

//					System.out.println("The record (" + htblColNameValue.get(insertionTable.getClusteringKey()) + ") was inserted to the table (" + strTableName + ") successfully");

				}
			} catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}

		}
		// Throw error that there is the table doesn't exist
		else
		{
			throw new DBAppException("There is no table exist with this name: " + strTableName);
		}

	}

	public void updateTable(String strTableName, String strClusteringKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException
	{
		// Check The Table Existence
		if (common.existInMetadata(strTableName, common.getMetadata()))
		{
			table editingTable;
			try
			{
				editingTable = common.getTable(strTableName);
				// Check all input values are supported & if not it will throw inside
				if (common.nameValueSupported(editingTable.getTableHeaders(), htblColNameValue))
				{
					// Add the touchDate
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					htblColNameValue.put("TouchDate", "" + sdf.format(new java.util.Date()));

					// Add the clustring key
					this.addTheClustringKey(editingTable, htblColNameValue, strClusteringKey);

					// Insert into the table then write it to the directory
					editingTable.updateTable(htblColNameValue);
					editingTable.writeTable();

//					System.out.println("The record (" + htblColNameValue.get(editingTable.getClusteringKey()) + ") has been updated successfully in the table (" + strTableName + ")");

				}
			} catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}

		}
		// Throw error that there is the table doesn't exist
		else
		{
			throw new DBAppException("There is no table exist with this name: " + strTableName);
		}
	}

	@SuppressWarnings("deprecation")
	private void addTheClustringKey(table editingTable, Hashtable<String, Object> htblColNameValue,
			String strClustringKeyValue)
	{
		String colType = editingTable.getTableHeaders().get(editingTable.getClusteringKey());

		// Integer
		if (colType.toLowerCase().equals("java.lang.integer"))
		{
			htblColNameValue.put(editingTable.getClusteringKey(), new Integer(Integer.parseInt(strClustringKeyValue)));
		}

		// String
		else if ((colType.toLowerCase().equals("java.lang.string")))
		{
			htblColNameValue.put(editingTable.getClusteringKey(), strClustringKeyValue);
		}

		// Double
		else if ((colType.toLowerCase().equals("java.lang.double")))
		{
			htblColNameValue.put(editingTable.getClusteringKey(), new Double(Double.parseDouble(strClustringKeyValue)));
		}

		// Boolean
		else if ((colType.toLowerCase().equals("java.lang.boolean")))
		{
			htblColNameValue.put(editingTable.getClusteringKey(),
					new Boolean(Boolean.parseBoolean(strClustringKeyValue)));
		}

		// Date ==> Format = YYYY-MM-DD
		else if ((colType.toLowerCase().equals("java.util.date")))
		{
			String[] strDate = strClustringKeyValue.split("-");
			int year = Integer.parseInt(strDate[0]);
			int month = Integer.parseInt(strDate[1]);
			int day = Integer.parseInt(strDate[2]);

			htblColNameValue.put(editingTable.getClusteringKey(), new Date(year - 1900, month - 1, day));
		}

		// Polygon ==> Format = (10,20),(30,30),(40,40),(50,60)
		else if ((colType.toLowerCase().equals("java.awt.polygon")))
		{
			htblColNameValue.put(editingTable.getClusteringKey(), new polygon(strClustringKeyValue));
		}

	}

	public void deleteFromTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException
	{
		// Check The Table Existence
		if (common.existInMetadata(strTableName, common.getMetadata()))
		{
			table deletionTable;
			try
			{
				deletionTable = common.getTable(strTableName);

				// Delete from the table
				deletionTable.deleteFromTable(htblColNameValue);
				deletionTable.writeTable();

//				System.out.println("The record (" + htblColNameValue.get(deletionTable.getClusteringKey()) + ") was deleted successfully in the table (" + strTableName + ")");

			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		// Throw error that there is the table doesn't exist
		else
		{
			throw new DBAppException("There is no table exist with this name: " + strTableName);
		}
	}

	public void createBTreeIndex(String strTableName, String strColName) throws DBAppException
	{
		if (common.suitableForCreatingIndex(strTableName, strColName, true))
		{
			table targetTable;
			try
			{
				targetTable = common.getTable(strTableName);
				targetTable.createTreeIndex(strColName);
			} catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}
		} else
			throw new DBAppException("There is something not suitable in BTreeIndex");
	}

	public void createRTreeIndex(String strTableName, String strColName) throws DBAppException
	{
		if (common.suitableForCreatingIndex(strTableName, strColName, false))
		{
			table targetTable;
			try
			{
				targetTable = common.getTable(strTableName);
				targetTable.createTreeIndex(strColName);
			} catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}
		} else
			throw new DBAppException("There is something not suitable in RTreeIndex");
	}

	@SuppressWarnings("rawtypes")
	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strArrOperators) throws DBAppException
	{
		table targetTable;
		Iterator<Hashtable<String, Object>> result = null;
		try
		{
			// Filter SQLTerms
			ArrayList<SQLTerm> temp = new ArrayList<SQLTerm>();
			for (int i = 0; i < arrSQLTerms.length; i++)
				if (arrSQLTerms[i] != null)
					temp.add(arrSQLTerms[i]);

			arrSQLTerms = new SQLTerm[temp.size()];

			for (int i = 0; i < arrSQLTerms.length; i++)
				arrSQLTerms[i] = (SQLTerm) temp.get(i);

			String strTableName = arrSQLTerms[0]._strTableName;
			ArrayList<String[]> metaData = common.getMetadata();

			// Check Table Existence
			if (common.existInMetadata(strTableName, metaData))
			{
				// Check Columns Exist
				for (int i = 0; i < arrSQLTerms.length; i++)
					common.columnExistInMetadata(arrSQLTerms[i], metaData);

				// Fetch The Table Then Select
				targetTable = common.getTable(strTableName);
				result = targetTable.selectFromTable(arrSQLTerms, strArrOperators);
			}

			// Throw error that there is no table with this name
			else
			{
				throw new DBAppException("There is no table exists with this name: " + strTableName);
			}
		} catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args)
	{
		DBApp dbApp = new DBApp();
		dbApp.init();

		common.printMetadata(common.getMetadata());
	}
}
