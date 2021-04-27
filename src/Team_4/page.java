package Team_4;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class page implements Serializable
{
	private static final long serialVersionUID = 1250453514135606379L;
	private int maximumNumberOfRows;
	private String strClusteringKeyColumn;
	private Vector<Hashtable<String, Object>> tuples;

	public page(String strClusteringKeyColumn)
	{
		this.tuples = new Vector<Hashtable<String, Object>>();
		this.strClusteringKeyColumn = strClusteringKeyColumn;
		// For getting the number of rows per page from the config file
		configFileReader cfr = new configFileReader();
		this.maximumNumberOfRows = cfr.getPropValues("MaximumRowsCountinPage");
	}

	// Done
	public int insertRow(Hashtable<String, Object> newRow) throws DBAppException
	{
		// If the page is empty, just insert it
		if (tuples.size() == 0)
		{
			tuples.add(newRow);
			return 0;
		}

		// If the table is not empty check the existence of the element
		else
		{

			int insertionIndex = getInsertionIndex(newRow);
			if (insertionIndex == -1)
				tuples.add(newRow);
			else
				tuples.add(insertionIndex, newRow);

			return insertionIndex == -1 ? tuples.size() - 1 : insertionIndex;

//			// Check if there is a a record with the same "key". If no, insert it in place
//			if (!existInPage(newRow))
//			{
//				int insertionIndex = getInsertionIndex(newRow);
//				if (insertionIndex == -1)
//					tuples.add(newRow);
//				else
//					tuples.add(insertionIndex, newRow);
//			}
//
//			// If yes, just throw an error that the key already exists
//			else
//			{
//				// Throw an error that a record with the same "key" already exist
//				throw new DBAppException("There is an element with this primary key (" + newRow.get(this.strClusteringKeyColumn) + ") already exists");
//			}
		}
	}

	// Done, it compares the clustering keys. If equal, it compares the other keys'
	// values. If equal, it removes it.
	public void removeRow(Hashtable<String, Object> targetRow) throws DBAppException
	{
		// If the page is empty, just throw it
		if (tuples.size() == 0)
		{
			// Throw an error that a record with the this "key" doesn't exist exist
			throw new DBAppException(
					"There is no element with this primary key: " + targetRow.get(strClusteringKeyColumn));
		}

		// If the table is not empty check the existence of the element
		else
		{
			// Check if there is a a record with the same "key". If no, throw it
			if (!existInPage(targetRow))
			{
				// Throw an error that a record with the this "key" doesn't exist exist
				throw new DBAppException(
						"There is no element with this primary key: " + targetRow.get(strClusteringKeyColumn));
			}
			// If yes, just find and delete one by one
			else
			{
				if (targetRow.containsKey(this.strClusteringKeyColumn))
				{
					int removeIndex = binarySearch(0, tuples.size() - 1, targetRow);
					for (int i = removeIndex; i != -1; i = binarySearch(0, tuples.size() - 1, targetRow))
						if (almostTheSame(this.tuples.get(i), targetRow))
							this.tuples.remove(i);

				} else
				{
					boolean removed = false;
					for (int i = 0; i < this.tuples.size(); i = removed ? i : i + 1)
					{
						removed = false;
						if (almostTheSame(this.tuples.get(i), targetRow))
						{
							this.tuples.remove(i);
							removed = true;
						}
					}
				}

			}
		}
	}

	public void removeRow(Hashtable<String, Object> targetRow, ArrayList<Integer> indexInPage)
	{
		for (int i = indexInPage.size() - 1; i >= 0; i--)
		{
			int index = (int) indexInPage.get(i);
			Hashtable<String, Object> originalRow = this.tuples.get(index);
			if (almostTheSame(originalRow, targetRow))
				this.tuples.remove(index);
		}

	}

	public void removeThisRowOnly(Hashtable<String, Object> targetRow) throws DBAppException
	{
		int removeIndex = binarySearch(0, tuples.size() - 1, targetRow);
		this.tuples.remove(removeIndex);
	}

	// Done, it compares the clustering keys. If equal, it updates all the entries
	// of the row.
	public void editRow(Hashtable<String, Object> targetRow) throws DBAppException
	{
		// If the page is empty, throw it
		if (tuples.size() == 0)
		{
			// Throw an error that a record with the this "key" doesn't exist exist
			throw new DBAppException(
					"There is no element with this primary key: " + targetRow.get(strClusteringKeyColumn));
		}

		// If the table is not empty check the existence of the element
		else
		{
			// Check if there is a a record with the same "key". If no, throw it
			if (!existInPage(targetRow))
			{
				// Throw an error that a record with the this "key" doesn't exist exist
				System.err
						.println("There is no element with this primary key: " + targetRow.get(strClusteringKeyColumn));
			}

			// If yes, just find it then edit every key if it exists if the original row
			else
			{
				int editingIndex = binarySearch(0, tuples.size() - 1, targetRow);

				Set<String> keySet = targetRow.keySet();
				Object[] keyArray = keySet.toArray();

				boolean goUp = false;

				for (int i = editingIndex; goUp ? i >= 0 : i < this.tuples.size(); i = goUp ? i - 1 : i + 1)
				{
					if (common.compareTo(this.tuples.get(i).get(strClusteringKeyColumn),
							targetRow.get(strClusteringKeyColumn)) == 0)
					{
						for (int j = 0; j < keyArray.length; j++)
							if (!((String) keyArray[j]).equals(this.strClusteringKeyColumn))
								this.tuples.get(i).put((String) keyArray[j], targetRow.get((String) keyArray[j]));
					} else
					{
						if (goUp)
							break;
						else
						{
							goUp = true;
							i = editingIndex;
						}
					}

					if (i + 1 == this.tuples.size())
					{
						goUp = true;
						i = editingIndex;
					}
				}
			}
		}

	}

	private boolean almostTheSame(Hashtable<String, Object> originalRow, Hashtable<String, Object> targetRow)
	{
		Set<String> keySet = targetRow.keySet();
		Object[] keyArray = keySet.toArray();
		boolean almostTheSame = false;
		for (int i = 0; i < keyArray.length; i++)
			if (common.compareTo(originalRow.get(keyArray[i]), targetRow.get(keyArray[i])) == 0)
				almostTheSame = true;
			else
			{
				almostTheSame = false;
				break;
			}
		return almostTheSame;
	}

	public Hashtable<String, Object> getLastRow()
	{
		return this.tuples.get(this.tuples.size() - 1);
	}

	public Hashtable<String, Object> getFirstRow()
	{
		return this.tuples.get(0);
	}

	private int binarySearch(int left, int right, Hashtable<String, Object> newRow)
	{
		if (right >= left)
		{
			int mid = left + (right - left) / 2;

			if (almostTheSame(this.tuples.get(mid), newRow))
				return mid;

			if (common.compareTo(this.tuples.get(mid).get(strClusteringKeyColumn),
					newRow.get(strClusteringKeyColumn)) == 0)
				return this.linearSearch(newRow);

			if (common.compareTo(this.tuples.get(mid).get(strClusteringKeyColumn),
					newRow.get(strClusteringKeyColumn)) > 0)
				return binarySearch(left, mid - 1, newRow);

			return binarySearch(mid + 1, right, newRow);
		}
		return -1;
	}

	private int linearSearch(Hashtable<String, Object> newRow)
	{
		for (int i = 0; i < this.tuples.size(); i++)
			if (this.almostTheSame(this.tuples.get(i), newRow))
				return i;
		return -1;
	}

	private int getInsertionIndex(Hashtable<String, Object> newRow)
	{
		for (int i = 0; i < this.tuples.size(); i++)
			if (common.compareTo(newRow.get(strClusteringKeyColumn),
					this.tuples.get(i).get(strClusteringKeyColumn)) < 0)
				return i;

		// If this case is reached, then the element should be inserted at last
		return -1;
	}

	public Hashtable<String, Object> getRecord(int i)
	{
		return this.tuples.get(i);
	}

	public int getPageSize()
	{
		return this.tuples.size();
	}

	public boolean existInPage(Hashtable<String, Object> newRow)
	{
		if (newRow.containsKey(this.strClusteringKeyColumn))
			return binarySearch(0, tuples.size() - 1, newRow) != -1;
		else
		{
			for (int i = 0; i < this.tuples.size(); i++)
				if (almostTheSame(this.tuples.get(i), newRow))
					return true;
			return false;
		}
	}

	public boolean isEmpty()
	{
		return this.tuples.size() == 0;
	}

	public boolean isFull()
	{
		return this.tuples.size() == this.maximumNumberOfRows;
	}

	public void writePage(String strTableName, int pageNumber)
	{
		File pageFile = new File("data/tables/" + strTableName + "/Page " + pageNumber + ".class");
		ObjectOutputStream OS;
		try
		{
			pageFile.createNewFile();
			OS = new ObjectOutputStream(new FileOutputStream(pageFile));
			OS.writeObject(this);
			OS.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	// For printing the table
	@Override
	public String toString()
	{
		System.out.println("=================================");
		System.out.println("==> Start: Printing the page <==");
		System.out.println("=================================");

		for (int i = 0; i < this.tuples.size(); i++)
		{
			Set<String> keySet = this.tuples.get(i).keySet();
			Object[] keyArray = keySet.toArray();
			System.out.println("Row ==> " + i);
			for (int j = 0; j < keyArray.length; j++)
			{
				System.out.print(keyArray[j] + ": ");
				if (!this.tuples.get(i).get(keyArray[j]).getClass().getName().toLowerCase().equals("java.awt.polygon"))
					System.out.print(this.tuples.get(i).get(keyArray[j]));
				else
				{
					Polygon p = (Polygon) this.tuples.get(i).get(keyArray[j]);
					Dimension dim = p.getBounds().getSize();
					System.out.print(dim.height * dim.width);
				}
				System.out.println();
			}
			System.out.println("================================");
		}
		System.out.println("==> End: Printing the page <==");
		return "================================";
//		return "";
	}

	// For testing only
	public static void main(String[] args)
	{
//		// Creating rows
//		Hashtable<String, Object> row1 = new Hashtable<String, Object>();
//		row1.put("strClusteringKeyColumn", 1);
//		row1.put("ss", 10000);
//
//		Hashtable<String, Object> row2 = new Hashtable<String, Object>();
//		row2.put("strClusteringKeyColumn", 2);
//		row2.put("ss", 10000);
//
//		Hashtable<String, Object> row3 = new Hashtable<String, Object>();
//		row3.put("strClusteringKeyColumn", 5);
//		row3.put("ss", 10000);
//
//		Hashtable<String, Object> row4 = new Hashtable<String, Object>();
//		row4.put("strClusteringKeyColumn", 6);
//		row4.put("ss", 10000);
//
//		Hashtable<String, Object> row5 = new Hashtable<String, Object>();
//		row5.put("strClusteringKeyColumn", 0);
//		row5.put("ss", 10000);
//
//		Hashtable<String, Object> row6 = new Hashtable<String, Object>();
//		row6.put("strClusteringKeyColumn", 10);
//		row6.put("ss", 10000);
//
//		Hashtable<String, Object> row7 = new Hashtable<String, Object>();
//		row7.put("strClusteringKeyColumn", 100);
//		row7.put("ss", 10000);
//
//		Hashtable<String, Object> row8 = new Hashtable<String, Object>();
//		row8.put("strClusteringKeyColumn", 50);
//		row8.put("ss", 10000);
//
//		Hashtable<String, Object> row9 = new Hashtable<String, Object>();
//		row9.put("strClusteringKeyColumn", 20);
//		row9.put("ss", 10000);
//
//		Hashtable<String, Object> row10 = new Hashtable<String, Object>();
//		row10.put("strClusteringKeyColumn", 16);
//		row10.put("ss", 10000);
//
//		Hashtable<String, Object> row11 = new Hashtable<String, Object>();
//		row11.put("strClusteringKeyColumn", 11);
//		row11.put("Medo", 10000);
//		row11.put("Hamada", 10000);
//		row11.put("No", 19191919);
//
//		Hashtable<String, Object> row12 = new Hashtable<String, Object>();
//		row12.put("strClusteringKeyColumn", 4);
//		row12.put("ss", 10000);
//
//		Hashtable<String, Object> row13 = new Hashtable<String, Object>();
//		row13.put("strClusteringKeyColumn", 8);
//		row13.put("ss", 10000);
//
//		// Creating a page and inserting the rows
//		page tempPage = new page("strClusteringKeyColumn");
//		tempPage.insertRow(row1);
//		tempPage.insertRow(row2);
//		tempPage.insertRow(row3);
//		tempPage.insertRow(row4);
//		tempPage.insertRow(row5);
//		tempPage.insertRow(row6);
//		tempPage.insertRow(row7);
//		tempPage.insertRow(row8);
//		tempPage.insertRow(row9);
//		tempPage.insertRow(row10);
//		tempPage.insertRow(row11);
//		tempPage.insertRow(row12);
//
//		tempPage.removeRow(row5);
//		tempPage.removeRow(row7);
//		tempPage.removeRow(row13); // Testing removing element that doesn't exist
//		
//		Hashtable<String, Object> row14 = new Hashtable<String, Object>();
//		row14.put("strClusteringKeyColumn", 11);
//		row14.put("Medo", 1611);
//		row14.put("Hamada", 1104);
//		row14.put("Ashruf", 1999);
//
//		tempPage.editRow(row14);
//
//		// Printing the table
//		System.out.println(tempPage.toString());

	}

	public Hashtable<String, Object> getRow(Hashtable<String, Object> targetRow)
	{
		int rowIndex = binarySearch(0, tuples.size() - 1, targetRow);
		return this.tuples.get(rowIndex);
	}

	public ArrayList<Hashtable<String, Object>> selectLinear(Hashtable<String, Object> targetRow, String _strOperator)
	{
		String targetColName = (String) targetRow.keySet().toArray()[0];
		Object targetColValue = targetRow.get(targetColName);

		ArrayList<Hashtable<String, Object>> records = new ArrayList<Hashtable<String, Object>>();
		for (int i = 0; i < this.tuples.size(); i++)
		{
			Object currentColValue = this.tuples.get(i).get(targetColName);

			if (_strOperator.equals("=") ? almostTheSame(this.tuples.get(i), targetRow)
					: _strOperator.equals("!=") ? !almostTheSame(this.tuples.get(i), targetRow)
							: _strOperator.equals(">") ? common.compareTo(currentColValue, targetColValue) > 0
									: _strOperator.equals(">=") ? common.compareTo(currentColValue, targetColValue) >= 0
											: _strOperator.equals("<")
													? common.compareTo(currentColValue, targetColValue) < 0
													: common.compareTo(currentColValue, targetColValue) < 0
															|| common.compareTo(currentColValue, targetColValue) == 0)
				records.add(this.tuples.get(i));
		}
		return records.size() == 0 ? null : records;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Hashtable<String, Object>> selectBinary(Hashtable<String, Object> targetRow, String _strOperator)
			throws CloneNotSupportedException
	{
		String targetColName = (String) targetRow.keySet().toArray()[0];
		Object targetColValue = targetRow.get(targetColName);

		ArrayList<Hashtable<String, Object>> records = new ArrayList<Hashtable<String, Object>>();
		page temp = new page(targetColName);

		temp.tuples = (Vector<Hashtable<String, Object>>) this.tuples.clone();
		for (int i = temp.binarySearch(0, temp.tuples.size() - 1, targetRow); i != -1; i = temp.binarySearch(0,
				temp.tuples.size() - 1, targetRow))
		{
			Object currentColValue = temp.tuples.get(i).get(targetColName);

			if (_strOperator.equals("=") ? almostTheSame(this.tuples.get(i), targetRow)
					: _strOperator.equals("!=") ? !almostTheSame(this.tuples.get(i), targetRow)
							: _strOperator.equals(">") ? common.compareTo(currentColValue, targetColValue) > 0
									: _strOperator.equals(">=") ? common.compareTo(currentColValue, targetColValue) >= 0
											: _strOperator.equals("<")
													? common.compareTo(currentColValue, targetColValue) < 0
													: common.compareTo(currentColValue, targetColValue) < 0
															|| common.compareTo(currentColValue, targetColValue) == 0)
			{
				records.add(temp.tuples.get(i));
				temp.tuples.remove(i);
			}
		}
		return records.size() == 0 ? null : records;
	}

}
