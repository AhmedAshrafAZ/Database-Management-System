package Team_4;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class common
{

	public static int compareTo(Object first, Object second)
	{
		int returnValue = 0;
		// Integer
		if (first instanceof Integer && second instanceof Integer)
			returnValue = ((Integer) first).compareTo((Integer) second);

		// String
		else if (first instanceof String && second instanceof String)
			returnValue = ((String) first).toLowerCase().compareTo(((String) second).toLowerCase());

		// Double
		else if (first instanceof Double && second instanceof Double)
			returnValue = ((Double) first).compareTo((Double) second);

		// Boolean
		else if (first instanceof Boolean && second instanceof Boolean)
			returnValue = ((Boolean) first).compareTo((Boolean) second);

		// Date
		else if (first instanceof Date && second instanceof Date)
			returnValue = ((Date) first).compareTo((Date) second);

		// Polygon
		else if (first instanceof Polygon && second instanceof Polygon)
		{

			Polygon firstP = (Polygon) first;
			Polygon secondP = (Polygon) second;

			Dimension d1 = firstP.getBounds().getSize();
			Dimension d2 = secondP.getBounds().getSize();

			int a1 = d1.height * d1.width;
			int a2 = d2.height * d2.width;

			returnValue = ((Integer) a1).compareTo((Integer) a2);
		}

		// Other
		else
			returnValue = (new polygon((String) first)).compareTo(new polygon((String) second));

		return returnValue;
	}

	public static ArrayList<ArrayList<String>> prepareTheMetadata(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType)
	{
		Set<String> colNamesTemp = htblColNameType.keySet();
		Object[] colNames = colNamesTemp.toArray();
		ArrayList<ArrayList<String>> metadata = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < colNames.length; i++)
		{
			ArrayList<String> metadataLine = new ArrayList<String>();
			metadataLine.add(strTableName);
			metadataLine.add((String) colNames[i]);
			metadataLine.add(htblColNameType.get(colNames[i]));
			metadataLine.add(((String) colNames[i]).toLowerCase().equals(strClusteringKeyColumn.toLowerCase()) ? "True"
					: "False");
			metadataLine.add("False"); // To be checked again in milestone 2
			metadata.add(metadataLine);

		}
		ArrayList<String> metadataLine = new ArrayList<String>();
		metadataLine.add(strTableName);
		metadataLine.add("TouchDate"); // colName
		metadataLine.add("java.util.Date"); // colType
		metadataLine.add("False");
		metadataLine.add("False");
		metadata.add(metadataLine);

		return metadata;
	}

	public static void setMetadata(ArrayList<ArrayList<String>> metadata, boolean append)
	{
		FileWriter csvWriter;
		try
		{
			csvWriter = new FileWriter("data/metadata.csv", append);
			for (ArrayList<String> rowData : metadata)
			{
				csvWriter.append(String.join(",", rowData));
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static Boolean nameTypeSupported(Hashtable<String, String> htblColNameType, ArrayList<String> supportedTypes)
			throws DBAppException
	{
		Set<String> colNamesTemp = htblColNameType.keySet();
		Object[] colNames = colNamesTemp.toArray();
		for (int i = 0; i < colNames.length; i++)
		{
			Boolean exist = false;
			for (int j = 0; j < supportedTypes.size(); j++)
			{

				if (((String) (supportedTypes.get(j)).toLowerCase())
						.equals(((String) (htblColNameType.get(colNames[i]))).toLowerCase()))
				{
					exist = true;
					break;
				}
			}
			if (!exist)
				throw new DBAppException("The column (" + colNames[i] + ") has the type ("
						+ htblColNameType.get(colNames[i]) + ") which is not supported type");
		}
		return true;
	}

	public static ArrayList<String[]> getMetadata()
	{
		ArrayList<String[]> metadata = new ArrayList<String[]>();
		BufferedReader csvReader;
		try
		{
			csvReader = new BufferedReader(new FileReader("data/metadata.csv"));
			String row;
			while ((row = csvReader.readLine()) != null)
			{
				String[] data = row.split(",");
				metadata.add(data);
			}

			csvReader.close();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
		return metadata;

	}

	public static boolean existInMetadata(String strTableName, ArrayList<String[]> metadata)
	{
		for (int i = 0; i < metadata.size(); i++)
			if (metadata.get(i)[0].toLowerCase().equals(strTableName.toLowerCase()))
				return true;
		return false;
	}

	public static void printMetadata(ArrayList<String[]> metadata)
	{
		for (int i = 0; i < metadata.size(); i++)
		{
			for (int j = 0; j < metadata.get(i).length; j++)
				System.out.print(metadata.get(i)[j] + (j == metadata.get(i).length - 1 ? "" : ", "));
			System.out.println();
		}
	}

	public static table getTable(String strTableName) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		String basePath = "data/tables/" + strTableName + "/";
		File tableFile = new File(basePath + strTableName + ".class");
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(tableFile));
		table table = (table) is.readObject();
		table.pages = new Vector<page>();
		for (int i = 0; i < (new File(basePath).listFiles().length) - 1; i++)
			table.pages.add(getPage(strTableName, i));
		is.close();
		return table;
	}

	public static page getPage(String strTableName, int pageNumber)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		File pageFile = new File("data/tables/" + strTableName + "/Page " + pageNumber + ".class");
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(pageFile));
		page page = (page) is.readObject();
		is.close();
		return page;
	}

	public static boolean nameValueSupported(Hashtable<String, String> tableHeaders,
			Hashtable<String, Object> htblColNameValue) throws DBAppException
	{
		Set<String> colNamesTemp = htblColNameValue.keySet();
		Object[] colNames = colNamesTemp.toArray();
		for (int i = 0; i < colNames.length; i++)
		{
			String valueType = htblColNameValue.get(colNames[i]).getClass().getName().toLowerCase();
			String nameType = tableHeaders.get(colNames[i]).toLowerCase();
			if (!valueType.equals(nameType))
				throw new DBAppException("This data type (" + valueType + ") is not supported for the column ("
						+ colNames[i] + ")\n" + "Expected: " + nameType + "\nFound: " + valueType);
		}
		return true;
	}

	public static boolean columnExistInMetadata(SQLTerm sqlTerm, ArrayList<String[]> metadata) throws DBAppException
	{
		String strTableName = sqlTerm._strTableName;
		String strColumnName = sqlTerm._strColumnName;
		String strColumnClass = sqlTerm._objValue.getClass().getName().toLowerCase();
		int colIndexInMetadata = 0;
		boolean colExist = false;
		for (int i = 0; i < metadata.size(); i++)
			if (metadata.get(i)[0].toLowerCase().equals(strTableName.toLowerCase())
					&& metadata.get(i)[1].toLowerCase().equals(strColumnName.toLowerCase()))
			{
				colExist = true;
				colIndexInMetadata = i;
				break;
			}
		if (colExist)
		{
			if (strColumnClass.equals((metadata.get(colIndexInMetadata)[2]).toLowerCase()))
				return true;
			else
				throw new DBAppException("The column " + strColumnName + " has the datatype ("
						+ metadata.get(colIndexInMetadata)[2] + "). Not this datatype: " + strColumnClass);

		} else
			throw new DBAppException(
					"There is no column with the name (" + strColumnName + ") in the table (" + strTableName + ").");
	}

	public static boolean suitableForCreatingIndex(String strTableName, String strColName, boolean BTree)
			throws DBAppException
	{
		ArrayList<String[]> metadata = getMetadata();
		boolean tableExist = false;
		boolean columnExist = false;
		String columnType = "";
		boolean columnNotIndexed = false;

		for (int i = 0; i < metadata.size(); i++)
		{
			// The table does exist
			if (metadata.get(i)[0].toLowerCase().equals(strTableName.toLowerCase()))
			{
				tableExist = true;

				// The column does exist
				if (metadata.get(i)[1].toLowerCase().equals(strColName.toLowerCase()))
				{
					columnExist = true;
					columnType = metadata.get(i)[2].toLowerCase();
					columnNotIndexed = metadata.get(i)[4].toLowerCase().equals("false") ? true : false;
					break;
				}
			}
		}
		if (!tableExist)
			throw new DBAppException("The table " + strTableName + " does not exist in the database!");

		if (!columnExist)
			throw new DBAppException("The column " + strColName + " does not exist in the table " + strTableName + "!");

		if (BTree && columnType.equals("java.awt.polygon"))
			throw new DBAppException("You cannot create an (B+ Tree) index on a column of type (Polygon)");

		if (!BTree && !columnType.equals("java.awt.polygon"))
			throw new DBAppException("You can only create an (R Tree) index on a column of type (Polygon)");

		if (!columnNotIndexed)
			throw new DBAppException("The column " + strColName + " is already indexed!");

		return true;
	}

	public static String getColType(String strTableName, String strColName)
	{
		ArrayList<String[]> metadata = getMetadata();
		String originalType = "";
		String type = "";
		for (int i = 0; i < metadata.size(); i++)
			if (metadata.get(i)[0].toLowerCase().equals(strTableName.toLowerCase())
					&& metadata.get(i)[1].toLowerCase().equals(strColName.toLowerCase()))
			{
				originalType = metadata.get(i)[2].toLowerCase();
				break;
			}
		switch (originalType)
		{
			case "java.lang.string":
				type = "String";
				break;

			case "java.lang.integer":
				type = "Integer";
				break;

			case "java.lang.double":
				type = "Double";
				break;

			case "java.lang.boolean":
				type = "Boolean";
				break;

			case "java.util.date":
				type = "Date";
				break;

			case "java.awt.polygon":
				type = "Polygon";
				break;

			default:
				type = null;
				break;
		}
		return type;
	}

	public static void modifyIndexInMetadata(ArrayList<String[]> originalMetadata, String strTableName,
			String strColName)
	{
		ArrayList<ArrayList<String>> modifiedMetadata = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < originalMetadata.size(); i++)
		{
			ArrayList<String> metadataLine = new ArrayList<String>();
			metadataLine.add(originalMetadata.get(i)[0]);
			metadataLine.add(originalMetadata.get(i)[1]);
			metadataLine.add(originalMetadata.get(i)[2]);
			metadataLine.add(originalMetadata.get(i)[3]);

			if (originalMetadata.get(i)[0].toLowerCase().equals(strTableName.toLowerCase())
					&& originalMetadata.get(i)[1].toLowerCase().equals(strColName.toLowerCase()))
				metadataLine.add("True");
			else
				metadataLine.add(originalMetadata.get(i)[4]);

			modifiedMetadata.add(metadataLine);
		}

		common.setMetadata(modifiedMetadata, false);
	}

	public static Object convertPolygonToComparable(Object object)
	{
		if (object instanceof Polygon)
			return new polygon((Polygon) object);
		else
			return object;
	}

}
