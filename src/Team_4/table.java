package Team_4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import BPTree.BPTree;
import BPTree.Ref;

public class table implements Serializable
{

	private static final long serialVersionUID = -3716893639279061448L;
	private String strTableName;
	private String strClusteringKeyColumn;
	private Hashtable<String, String> tableHeaders;
	private int nodeSize;
	@SuppressWarnings("rawtypes")
	public Hashtable<String, BPTree> colNameIndex;
	public transient Vector<page> pages;

	@SuppressWarnings("rawtypes")
	public table(String strTableName, String strClusteringKeyColumn, Hashtable<String, String> htblColNameType)
	{
		this.strTableName = strTableName;
		this.strClusteringKeyColumn = strClusteringKeyColumn.toLowerCase();
		this.tableHeaders = htblColNameType;
		this.colNameIndex = new Hashtable<String, BPTree>();
		this.pages = new Vector<page>();
		configFileReader cfr = new configFileReader();
		this.nodeSize = cfr.getPropValues("NodeSize");
	}

	public void insertIntoTable(Hashtable<String, Object> newRow) throws DBAppException
	{
		// If the table is empty, create a new page, insert the newRow, add it to the
		// pages (table)
		if (pages.size() == 0)
		{
			page tempPage = new page(this.strClusteringKeyColumn);
			tempPage.insertRow(newRow);
			pages.add(tempPage);
		}

		// If the table is not empty and contains pages, get the last page
		else
		{
			if (this.pages.get(this.pages.size() - 1).isFull())
			{
				page newPage = new page(this.strClusteringKeyColumn);
				this.pages.add(newPage);
			}

			if (this.pages.size() == 1 && !this.pages.get(0).isFull())
			{
				this.pages.get(0).insertRow(newRow);
			} else
			{
				page lastPage = pages.get(pages.size() - 1);
				page preLastPage = pages.get(pages.size() - 2);

				// If the last page is not full, just insert the row
				if (!lastPage.isFull())
				{
					if (common.compareTo(newRow.get(strClusteringKeyColumn),
							preLastPage.getLastRow().get(strClusteringKeyColumn)) >= 0)
					{
						lastPage.insertRow(newRow);
					} else
					{
						this.shiftAndInsert(newRow);
					}
				}

				// If the last page is full, create a new page, insert the row, add it to the
				// pages (table)
				else
				{
					page tempPage = new page(this.strClusteringKeyColumn);
					pages.add(tempPage);
					this.insertIntoTable(newRow);
				}
			}
		}

		ArrayList<String> indexedCols = this.getIndices();
		for (int i = 0; i < indexedCols.size(); i++)
		{
			colNameIndex.remove(indexedCols.get(i));
			this.fixTree(indexedCols.get(i));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fixTree(String strColName)
	{
		String colType = common.getColType(this.strTableName, strColName);
		this.colNameIndex.remove(strColName);
		BPTree tree = null;
		if (colType.equals("String"))
			tree = new BPTree<String>(nodeSize);

		if (colType.equals("Integer"))
			tree = new BPTree<Integer>(nodeSize);

		if (colType.equals("Double"))
			tree = new BPTree<Double>(nodeSize);

		if (colType.equals("Boolean"))
			tree = new BPTree<Boolean>(nodeSize);

		if (colType.equals("Date"))
			tree = new BPTree<Date>(nodeSize);

		if (colType.equals("Polygon"))
			tree = new BPTree<polygon>(nodeSize);

		// Inserting the nodes
		for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++)
		{
			for (int recordIndex = 0; recordIndex < this.pages.get(pageNumber).getPageSize(); recordIndex++)
			{
				Hashtable<String, Object> record = this.pages.get(pageNumber).getRecord(recordIndex);
				Ref recordReference = new Ref(pageNumber, recordIndex);
				tree.insert((Comparable) (common.convertPolygonToComparable(record.get(strColName))), recordReference);
			}
		}
		this.colNameIndex.put(strColName, tree);
//		System.out.println("Nodes updated successfully to the tree => column: " + strColName);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void updateTable(Hashtable<String, Object> targetRow) throws DBAppException
	{
		Hashtable<String, Object> oldRow = new Hashtable<String, Object>();
		if (pages.size() == 0)
		{
			throw new DBAppException(
					"There is no element with this primary key: " + targetRow.get(strClusteringKeyColumn));
		} else
		{
			int editPageIndex = this.binarySearch(0, this.pages.size() - 1, targetRow);
			if (editPageIndex == -1)
			{
				throw new DBAppException(
						"There is no element with this primary key: " + targetRow.get(strClusteringKeyColumn));
			} else
			{
				boolean goUp = false;

				for (int i = editPageIndex; goUp ? i >= 0 : i < this.pages.size(); i = goUp ? i - 1 : i + 1)
				{
					if (this.pages.get(i).existInPage(targetRow))
					{
						oldRow = (Hashtable<String, Object>) this.pages.get(i).getRow(targetRow).clone();
						this.pages.get(i).editRow(targetRow);
					} else
					{
						if (goUp)
							break;
						else
						{
							goUp = true;
							i = editPageIndex;
						}
					}

					if (i + 1 == this.pages.size())
					{
						goUp = true;
						i = editPageIndex;
					}
//					if(oldRow != null)
//					{
//						Object[] updatedRows = targetRow.keySet().toArray();
//						ArrayList<String> indexedCols = this.getIndices();
//						for (int ii = 0; ii < updatedRows.length; ii++)
//						{
//							for (int j = 0; j < indexedCols.size(); j++)
//							{
//								String colName = indexedCols.get(j);
//								if (((String) updatedRows[ii]).toLowerCase().equals(colName.toLowerCase())
//										&& !colName.toLowerCase().equals(strClusteringKeyColumn.toLowerCase()))
//								{
//									Ref recordReference = this.colNameIndex.get(indexedCols.get(j)).search((Comparable) common.convertPolygonToComparable(oldRow.get(colName)));
//									this.colNameIndex.get(indexedCols.get(j)).delete((Comparable) (common.convertPolygonToComparable(oldRow.get(colName))));
//									this.colNameIndex.get(indexedCols.get(j)).insert((Comparable) (common.convertPolygonToComparable(targetRow.get(colName))), recordReference);
//								}
//							}
//						}
//					}
				}
			}
		}
		Object[] updatedRows = targetRow.keySet().toArray();
		ArrayList<String> indexedCols = this.getIndices();
		for (int ii = 0; ii < updatedRows.length; ii++)
		{
			for (int j = 0; j < indexedCols.size(); j++)
			{
				String colName = indexedCols.get(j);
				if (((String) updatedRows[ii]).toLowerCase().equals(colName.toLowerCase())
						&& !colName.toLowerCase().equals(strClusteringKeyColumn.toLowerCase()))
				{
					this.fixTree(colName);
				}
			}
		}
	}

	public void deleteFromTable(Hashtable<String, Object> targetRow) throws DBAppException
	{
		if (pages.size() == 0)
		{
			// Throw an error that a record with the this "key" doesn't exist exist
			throw new DBAppException("There is no element matches the requested columns");
		} else
		{
			if (targetRow.containsKey(this.strClusteringKeyColumn))
			{

				int deletePageIndex = this.binarySearch(0, this.pages.size() - 1, targetRow);
				if (deletePageIndex == -1)
				{
					// Throw an error that a record with the this "key" doesn't exist exist
					throw new DBAppException("There is no element matches the requested columns");
				}

				else
				{
					// binarySearch & delete
					while (deletePageIndex != -1)
					{
						this.pages.get(deletePageIndex).removeRow(targetRow);
						if (this.pages.get(deletePageIndex).isEmpty())
						{
							this.pages.remove(deletePageIndex);
							deletePageIndex = this.binarySearch(0, this.pages.size() - 1, targetRow);
						} else
							deletePageIndex = this.binarySearch(0, this.pages.size() - 1, targetRow);
					}
					this.removePagesShiftRows();
				}
			}

			// The column does not contain the key, check for indices
			else
			{
				boolean hasIndex = false;
				Object[] rowCols = targetRow.keySet().toArray();
				for (int i = 0; i < rowCols.length; i++)
				{
					if (this.hasIndex(((String) rowCols[i])))
					{
						hasIndex = true;
//						System.out.println((String) rowCols[i]);
//						System.out.println("Has index");
						ArrayList<Ref> recordRefs = this.getAllRecordRefs(this.cloneTree((String) rowCols[i]),
								targetRow, (String) rowCols[i]);
						if (recordRefs != null)
						{
							Hashtable<Integer, ArrayList<Integer>> refs = new Hashtable<Integer, ArrayList<Integer>>();
							for (int j = 0; j < recordRefs.size(); j++)
							{
								int pageNumber = recordRefs.get(j).getPage();
								int indexInPage = recordRefs.get(j).getIndexInPage();
								if (refs.containsKey(pageNumber))
								{
									refs.get(pageNumber).add(indexInPage);
								}

								else
								{
									refs.put(pageNumber, new ArrayList<Integer>());
									refs.get(pageNumber).add(indexInPage);
								}
							}

							Object[] refsKeys = refs.keySet().toArray();
							for (int j = 0; j < refsKeys.length; j++)
							{
								int pageNumber = (int) refsKeys[j];
								ArrayList<Integer> indexInPage = refs.get(pageNumber);
								Collections.sort(indexInPage);
								this.pages.get(pageNumber).removeRow(targetRow, indexInPage);
							}
							this.removePagesShiftRows();
							break;
						}
					}

				}
				if (!hasIndex)
				{
//					System.out.println("No Index Linear Search...");
					for (int i = 0; i < this.pages.size(); i++)
						if (this.pages.get(i).existInPage(targetRow))
							this.pages.get(i).removeRow(targetRow);
					this.removePagesShiftRows();
				}

			}
		}

		Object[] updatedRows = targetRow.keySet().toArray();
		ArrayList<String> indexedCols = this.getIndices();
		for (int ii = 0; ii < updatedRows.length; ii++)
		{
			for (int j = 0; j < indexedCols.size(); j++)
			{
				String colName = indexedCols.get(j);
				if (((String) updatedRows[ii]).toLowerCase().equals(colName.toLowerCase()))
				{
					this.fixTree(colName);
				}
			}
		}
	}

	private void removePagesShiftRows() throws DBAppException
	{
		for (; !this.allPagesAreFull();)
		{
			// Remove empty pages
			for (int k = 0; k < this.pages.size(); k++)
				if (this.pages.get(k).isEmpty())
					this.pages.remove(k);

			// Shift rows
			for (int k = 0; k < this.pages.size() - 1; k++)
			{
				if (!this.pages.get(k).isFull())
					for (; !this.pages.get(k).isFull();)
					{
						this.pages.get(k).insertRow(this.pages.get(k + 1).getFirstRow());
						this.pages.get(k + 1).removeThisRowOnly(this.pages.get(k + 1).getFirstRow());
						if (this.pages.get(k + 1).isEmpty())
							break;
					}
			}
			// Remove empty pages
			for (int k = 0; k < this.pages.size(); k++)
				if (this.pages.get(k).isEmpty())
					this.pages.remove(k);
		}

	}

	private boolean allPagesAreFull()
	{
		boolean allPagesAreFull = true;
		for (int i = 0; i < this.pages.size() - 1; i++)
		{
			if (!this.pages.get(i).isFull())
			{
				allPagesAreFull = false;
				break;
			}
		}
		return allPagesAreFull;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BPTree cloneTree(String strColName)
	{
		// Creating the tree
		String colType = common.getColType(this.strTableName, strColName);
		BPTree tree = null;
		if (colType.equals("String"))
			tree = new BPTree<String>(nodeSize);

		if (colType.equals("Integer"))
			tree = new BPTree<Integer>(nodeSize);

		if (colType.equals("Double"))
			tree = new BPTree<Double>(nodeSize);

		if (colType.equals("Boolean"))
			tree = new BPTree<Boolean>(nodeSize);

		if (colType.equals("Date"))
			tree = new BPTree<Date>(nodeSize);

		if (colType.equals("Polygon"))
			tree = new BPTree<polygon>(nodeSize);

		// Inserting the nodes
		for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++)
		{
			for (int recordIndex = 0; recordIndex < this.pages.get(pageNumber).getPageSize(); recordIndex++)
			{
				Hashtable<String, Object> record = this.pages.get(pageNumber).getRecord(recordIndex);
				Ref recordReference = new Ref(pageNumber, recordIndex);
				tree.insert((Comparable) (common.convertPolygonToComparable(record.get(strColName))), recordReference);
			}
		}

		return tree;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Ref> getAllRecordRefs(BPTree tree, Hashtable<String, Object> targetRow, String key)
	{
		ArrayList<Ref> refs = new ArrayList<Ref>();
		Ref tempRef = tree.search((Comparable) common.convertPolygonToComparable(targetRow.get(key)));
		while (tempRef != null)
		{
			refs.add(tempRef);
			tree.delete((Comparable) common.convertPolygonToComparable(targetRow.get(key)));
			tempRef = tree.search((Comparable) common.convertPolygonToComparable(targetRow.get(key)));
		}
		return refs.size() == 0 ? null : refs;
	}

	public boolean hasIndex(String colName)
	{
		return this.colNameIndex.containsKey(colName);
	}
//	if (this.pages.get(deletePageIndex).isEmpty())
//		{
//			this.pages.remove(deletePageIndex);
//		}
//		
//		else if (this.pages.get(deletePageIndex) != this.getLastPage())
//		{
//			boolean goBack = false;
//			for(int j = deletePageIndex; j < this.pages.size() - 1; j++)
//			{
//				for (int i = j; !this.pages.get(j).isFull() && !this.pages.get(j + 1).isEmpty();)
//				{
//					this.pages.get(i).insertRow(this.pages.get(i + 1).getFirstRow());
//					this.pages.get(i + 1).removeRow(this.pages.get(i + 1).getFirstRow());
//				}
//				if(this.pages.get(j + 1).isEmpty())
//					this.pages.remove(j);
//			}
//
//		}

//		for(int i = deletePageIndex; i != -1; i = this.binarySearch(0, this.pages.size() - 1, targetRow))
//		{
//			this.pages.get(deletePageIndex).removeRow(targetRow);
//			if(this.pages.get(deletePageIndex).isEmpty())
//				this.pages.remove(deletePageIndex);
//		}
//		
//		
//		for (int i = deletePageIndex; i < this.pages.size() - 1; i++)
//		{
//			this.pages.get(i).insertRow(this.pages.get(i + 1).getFirstRow());
//			this.pages.get(i + 1).removeRow(this.pages.get(i + 1).getFirstRow());
//		}
//		
//		if(this.pages.get(this.pages.size() - 1).isEmpty())
//			this.pages.remove(this.pages.get(this.pages.size() - 1));
	public String getClusteringKey()
	{
		return strClusteringKeyColumn;
	}

	public boolean existInTable(Hashtable<String, Object> targetRow)
	{
		return this.binarySearch(0, this.pages.size() - 1, targetRow) != -1;
	}

	private int binarySearch(int left, int right, Hashtable<String, Object> newRow)
	{
		if (right >= left)
		{
			int mid = left + (right - left) / 2;
			Hashtable<String, Object> firstRow = this.pages.get(mid).getFirstRow();

			if (this.pages.get(mid).existInPage(newRow))
				return mid;

			if (common.compareTo(newRow.get(this.strClusteringKeyColumn),
					firstRow.get(this.strClusteringKeyColumn)) == 0)
				return this.linearSearch(newRow);

			if (common.compareTo(newRow.get(this.strClusteringKeyColumn),
					firstRow.get(this.strClusteringKeyColumn)) < 0)
				return binarySearch(left, mid - 1, newRow);

			return binarySearch(mid + 1, right, newRow);
		}
		return -1;
	}

	private int linearSearch(Hashtable<String, Object> newRow)
	{
		for (int i = 0; i < this.pages.size(); i++)
			if (this.pages.get(i).existInPage(newRow))
				return i;
		return -1;

	}

	private int getInsertionPage(Hashtable<String, Object> newRow)
	{
		for (int i = pages.size() - 1; i >= 0; i--)
		{
			page currentPage = this.pages.get(i);
			if (!currentPage.isEmpty())
				if (common.compareTo(newRow.get(strClusteringKeyColumn),
						currentPage.getFirstRow().get(strClusteringKeyColumn)) >= 0
						&& common.compareTo(newRow.get(strClusteringKeyColumn),
								currentPage.getLastRow().get(strClusteringKeyColumn)) < 0)
					return i;
				else if (i != 0)
				{
					page preCurrentPage = this.pages.get(i - 1);
					if (common.compareTo(newRow.get(strClusteringKeyColumn),
							currentPage.getFirstRow().get(strClusteringKeyColumn)) < 0
							&& common.compareTo(newRow.get(strClusteringKeyColumn),
									preCurrentPage.getLastRow().get(strClusteringKeyColumn)) >= 0)
						return i;
				}
		}
		return -1;
	}

	private void shiftAndInsert(Hashtable<String, Object> newRow) throws DBAppException
	{
		int insertionIndex = getInsertionPage(newRow);
		insertionIndex = insertionIndex == -1 ? 0 : insertionIndex;
		for (int i = this.pages.size() - 1; i > insertionIndex; i--)
		{
			this.pages.get(i).insertRow(this.pages.get(i - 1).getLastRow());
			this.pages.get(i - 1).removeThisRowOnly(this.pages.get(i - 1).getLastRow());
		}

		this.pages.get(insertionIndex).insertRow(newRow);
	}

	public page getFirstPage()
	{
		return this.pages.get(0);
	}

	public page getLastPage()
	{
		return this.pages.get(this.pages.size() - 1);
	}

	public String toString()
	{
		System.err.println("Printing Table: " + this.strTableName);
		System.err.println("Total Pages: " + this.pages.size());
		System.err.println("Node Size: " + this.nodeSize);
		System.err.print("The Table has indices on columns: ");
		ArrayList<String> index = this.getIndices();
		for (int i = 0; i < index.size(); i++)
			System.out.print(index.get(i) + " ");
		System.out.println();

		for (int i = 0; i < this.pages.size(); i++)
		{
			System.err.println("Page: " + i);
			System.out.println(this.pages.get(i));
		}
		return "";
	}

	public void writeTable()
	{
		String basePath = "data/tables/" + this.strTableName + "/";
		File tableFile = new File(basePath + this.strTableName + ".class");
		ObjectOutputStream OS;
		try
		{
			tableFile.createNewFile();
			OS = new ObjectOutputStream(new FileOutputStream(tableFile));
			OS.writeObject(this);
			OS.close();

			for (int i = 0; i < this.pages.size(); i++)
				this.pages.get(i).writePage(strTableName, i);

			int fileLength = new File(basePath).listFiles().length;
			for (int i = fileLength - 2; i >= this.pages.size(); i--)
			{
				File toBeDeleted = new File(basePath + "Page " + i + ".class");
				toBeDeleted.delete();
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public Hashtable<String, String> getTableHeaders()
	{
		return this.tableHeaders;
	}

	public static void main(String[] args)
	{
//		// Creating rows
//		Hashtable<String, Object> row1 = new Hashtable<String, Object>();
//		row1.put("strClusteringKeyColumn", "Ahmed");
//		row1.put("ss", 10000);
//
//		Hashtable<String, Object> row2 = new Hashtable<String, Object>();
//		row2.put("strClusteringKeyColumn", "Farida");
//		row2.put("ss", 10000);
//
//		Hashtable<String, Object> row3 = new Hashtable<String, Object>();
//		row3.put("strClusteringKeyColumn", "Hossam");
//		row3.put("ss", 10000);
//
//		Hashtable<String, Object> row4 = new Hashtable<String, Object>();
//		row4.put("strClusteringKeyColumn", "Ashraf");
//		row4.put("ss", 10000);
//
//		Hashtable<String, Object> row5 = new Hashtable<String, Object>();
//		row5.put("strClusteringKeyColumn", "Abdallah");
//		row5.put("ss", 10000);
//
//		Hashtable<String, Object> row6 = new Hashtable<String, Object>();
//		row6.put("strClusteringKeyColumn", "Zeyad");
//		row6.put("ss", 10000);
//
//		Hashtable<String, Object> row7 = new Hashtable<String, Object>();
//		row7.put("strClusteringKeyColumn", "Rowan");
//		row7.put("ss", 10000);
//
//		Hashtable<String, Object> row8 = new Hashtable<String, Object>();
//		row8.put("strClusteringKeyColumn", "Rawan");
//		row8.put("ss", 10000);
//
//		Hashtable<String, Object> row9 = new Hashtable<String, Object>();
//		row9.put("strClusteringKeyColumn", "Arwa");
//		row9.put("ss", 10000);
//
//		Hashtable<String, Object> row10 = new Hashtable<String, Object>();
//		row10.put("strClusteringKeyColumn", "Donia");
//		row10.put("ss", 10000);
//
//		Hashtable<String, Object> row11 = new Hashtable<String, Object>();
//		row11.put("strClusteringKeyColumn", "Mariam");
//		row11.put("Medo", 10000);
//		row11.put("Hamada", 10000);
//		row11.put("No", 19191919);
//
//		Hashtable<String, Object> row12 = new Hashtable<String, Object>();
//		row12.put("strClusteringKeyColumn", "Aya");
//		row12.put("ss", 10000);
//
//		Hashtable<String, Object> row13 = new Hashtable<String, Object>();
//		row13.put("strClusteringKeyColumn", "Nada");
//		row13.put("ss", 10000);
//
//		table testTable = new table("Student", "strClusteringKeyColumn");
//		testTable.insertIntoTable(row1);
//		testTable.insertIntoTable(row2);
//		testTable.insertIntoTable(row3);
//		testTable.insertIntoTable(row4);
//		testTable.insertIntoTable(row5);
//		testTable.insertIntoTable(row6);
//		testTable.insertIntoTable(row7);
//		testTable.insertIntoTable(row8);
//		testTable.insertIntoTable(row9);
//		testTable.insertIntoTable(row10);
//		testTable.insertIntoTable(row11);
//		testTable.insertIntoTable(row12);
//		testTable.insertIntoTable(row13);
//
//		Hashtable<String, Object> rowEdit = new Hashtable<String, Object>();
//		rowEdit.put("strClusteringKeyColumn", "Mariam");
//		rowEdit.put("ss", 11);
//		rowEdit.put("Medo", 1611);
//		rowEdit.put("Hamada", 1104);
//		testTable.updateTable(rowEdit);

//		Hashtable<String, Object> rowDelete = new Hashtable<String, Object>();
//		rowDelete.put("strClusteringKeyColumn", 100);
//		testTable.deleteFromTable(rowDelete);

//		Hashtable<String, Object> rowInsertAfterDelete = new Hashtable<String, Object>();
//		rowInsertAfterDelete.put("strClusteringKeyColumn", 11);
//		rowInsertAfterDelete.put("ss", 10000);
//		testTable.insertIntoTable(rowInsertAfterDelete);

//		System.out.println(testTable.toString());
	}

	public ArrayList<String> getIndices()
	{
		ArrayList<String> indices = new ArrayList<String>();
		Object[] temp = this.colNameIndex.keySet().toArray();
		for (int i = 0; i < temp.length; i++)
			indices.add((String) temp[i]);
		return indices;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createTreeIndex(String strColName)
	{
		// Creating the tree
		String colType = common.getColType(this.strTableName, strColName);
		BPTree tree = null;
		if (colType.equals("String"))
			tree = new BPTree<String>(nodeSize);

		if (colType.equals("Integer"))
			tree = new BPTree<Integer>(nodeSize);

		if (colType.equals("Double"))
			tree = new BPTree<Double>(nodeSize);

		if (colType.equals("Boolean"))
			tree = new BPTree<Boolean>(nodeSize);

		if (colType.equals("Date"))
			tree = new BPTree<Date>(nodeSize);

		if (colType.equals("Polygon"))
			tree = new BPTree<polygon>(nodeSize);

		// Inserting the nodes
		for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++)
		{
			for (int recordIndex = 0; recordIndex < this.pages.get(pageNumber).getPageSize(); recordIndex++)
			{
				Hashtable<String, Object> record = this.pages.get(pageNumber).getRecord(recordIndex);
				Ref recordReference = new Ref(pageNumber, recordIndex);
				tree.insert((Comparable) (common.convertPolygonToComparable(record.get(strColName))), recordReference);
			}
		}
		this.colNameIndex.put(strColName, tree);
		this.writeTable();

		// Update logs
		ArrayList<String[]> originalMetadata = common.getMetadata();
		common.modifyIndexInMetadata(originalMetadata, this.strTableName, strColName);
//		System.out.println("Nodes inserted successfully to the tree => column: " + strColName);
//		System.out.println(tree);
	}

	public Iterator<Hashtable<String, Object>> selectFromTable(SQLTerm[] arrSQLTerms, String[] strArrOperators)
			throws DBAppException
	{
		if (pages.size() == 0)
			return null;
		ArrayList<ArrayList<Hashtable<String, Object>>> queriesResults = new ArrayList<ArrayList<Hashtable<String, Object>>>();
		for (int queryNumber = 0; queryNumber < arrSQLTerms.length; queryNumber++)
		{

			String colName = arrSQLTerms[queryNumber]._strColumnName;
			Object colValue = arrSQLTerms[queryNumber]._objValue;
			Hashtable<String, Object> targetRow = new Hashtable<String, Object>();
			targetRow.put(colName, colValue);
			boolean colIsKey = colName.toLowerCase().equals(this.strClusteringKeyColumn.toLowerCase());
			boolean colIndexed = this.colNameIndex.containsKey(colName);
			ArrayList<Hashtable<String, Object>> queryRecords = new ArrayList<Hashtable<String, Object>>();

			if (colIndexed && arrSQLTerms[queryNumber]._strOperator.equals("="))
			{
//				System.out.println("Indexed Column Search");
				ArrayList<Ref> recordsInTree = this.getAllRecordRefs(this.cloneTree(colName), targetRow, colName);
				Hashtable<Integer, ArrayList<Integer>> refs = new Hashtable<Integer, ArrayList<Integer>>();
				if (refs != null)
				{
					for (int j = 0; j < recordsInTree.size(); j++)
					{
						int pageNumber = recordsInTree.get(j).getPage();
						int indexInPage = recordsInTree.get(j).getIndexInPage();
						if (refs.containsKey(pageNumber))
						{
							refs.get(pageNumber).add(indexInPage);
						}

						else
						{
							refs.put(pageNumber, new ArrayList<Integer>());
							refs.get(pageNumber).add(indexInPage);
						}
					}
					Object[] refsKeys = refs.keySet().toArray();
					for (int j = 0; j < refsKeys.length; j++)
					{
						int pageNumber = (int) refsKeys[j];
						ArrayList<Integer> indexInPage = refs.get(pageNumber);
						for (int k = 0; k < indexInPage.size(); k++)
							queryRecords.add(this.pages.get(pageNumber).getRecord(indexInPage.get(k)));
					}
				}
			} else
			{
				if (!colIsKey)
				{
//					System.out.println("Binary Column Search");
					for (int i = 0; i < this.pages.size(); i++)
					{
						ArrayList<Hashtable<String, Object>> temp;
						try
						{
							temp = this.pages.get(i).selectBinary(targetRow, arrSQLTerms[queryNumber]._strOperator);
							if (temp != null)
								for (int j = 0; j < temp.size(); j++)
									queryRecords.add(temp.get(j));
						} catch (CloneNotSupportedException e)
						{
							e.printStackTrace();
						}

					}
				} else
				{
//					System.out.println("Linear Column Search");
					for (int i = 0; i < this.pages.size(); i++)
					{
						ArrayList<Hashtable<String, Object>> temp = this.pages.get(i).selectLinear(targetRow,
								arrSQLTerms[queryNumber]._strOperator);
						if (temp != null)
							for (int j = 0; j < temp.size(); j++)
								queryRecords.add(temp.get(j));
					}
				}
			}
			queriesResults.add(queryRecords);
		}

		ArrayList<Hashtable<String, Object>> finalResults = new ArrayList<Hashtable<String, Object>>();
		if (strArrOperators.length == 0)
			return queriesResults.get(0).iterator();

		switch (strArrOperators[0].toLowerCase())
		{
			case "or":
				finalResults = this.getTheUnion(queriesResults);
				break;

			case "and":
				finalResults = this.getTheIntersection(queriesResults);
				break;

			case "xor":
				finalResults = this.getTheXOR(queriesResults);
				break;

			default:
				throw new DBAppException("This operator is not supported ==> " + strArrOperators[0]);

		}

		return finalResults.iterator();

	}

	private ArrayList<Hashtable<String, Object>> getTheXOR(
			ArrayList<ArrayList<Hashtable<String, Object>>> queriesResults)
	{
		if (queriesResults.size() > 1)
		{
			ArrayList<Hashtable<String, Object>> baseResult = queriesResults.get(0);
			for (int i = 1; i < queriesResults.size(); i++)
			{
				ArrayList<Hashtable<String, Object>> anotherResult = queriesResults.get(i);
				baseResult = getXORHelper(baseResult, anotherResult);
			}
			return baseResult;
		} else
		{
			return queriesResults.size() == 0 ? null : queriesResults.get(0);
		}
	}

	private ArrayList<Hashtable<String, Object>> getXORHelper(ArrayList<Hashtable<String, Object>> baseResult,
			ArrayList<Hashtable<String, Object>> anotherResult)
	{

		if (baseResult == null && anotherResult != null)
			return anotherResult;

		if (baseResult != null && anotherResult == null)
			return baseResult;

		if (baseResult == null && anotherResult == null)
			return null;

		ArrayList<Hashtable<String, Object>> xorResult = new ArrayList<Hashtable<String, Object>>();
		for (int i = 0; i < baseResult.size(); i++)
			if (!anotherResult.contains(baseResult.get(i)))
				xorResult.add(baseResult.get(i));

		for (int i = 0; i < anotherResult.size(); i++)
			if (!baseResult.contains(anotherResult.get(i)))
				xorResult.add(anotherResult.get(i));
		return xorResult;

	}

	private ArrayList<Hashtable<String, Object>> getTheIntersection(
			ArrayList<ArrayList<Hashtable<String, Object>>> queriesResults)
	{
		if (queriesResults.size() > 1)
		{
			ArrayList<Hashtable<String, Object>> baseResult = queriesResults.get(0);
			for (int i = 1; i < queriesResults.size(); i++)
			{
				ArrayList<Hashtable<String, Object>> anotherResult = queriesResults.get(i);
				baseResult = getIntersectionHelper(baseResult, anotherResult);
			}
			return baseResult;
		} else
		{
			return queriesResults.size() == 0 ? null : queriesResults.get(0);
		}
	}

	private ArrayList<Hashtable<String, Object>> getIntersectionHelper(ArrayList<Hashtable<String, Object>> baseResult,
			ArrayList<Hashtable<String, Object>> anotherResult)
	{
		if (baseResult == null)
			return null;

		ArrayList<Hashtable<String, Object>> intersectionResult = new ArrayList<Hashtable<String, Object>>();
		for (int i = 0; i < baseResult.size(); i++)
		{
			boolean exist = false;
			for (int j = 0; j < anotherResult.size(); j++)
			{
				if (baseResult.get(i) == anotherResult.get(j))
				{
					exist = true;
					break;
				}
			}
			if (exist)
				intersectionResult.add(baseResult.get(i));
		}
		return intersectionResult;
	}

	private ArrayList<Hashtable<String, Object>> getTheUnion(
			ArrayList<ArrayList<Hashtable<String, Object>>> queriesResults)
	{
		if (queriesResults.size() > 1)
		{
			ArrayList<Hashtable<String, Object>> baseResult = queriesResults.get(0);
			for (int i = 1; i < queriesResults.size(); i++)
			{
				ArrayList<Hashtable<String, Object>> anotherResult = queriesResults.get(i);
				baseResult = getUnionHelper(baseResult, anotherResult);
			}
			return baseResult;
		} else
		{
			return queriesResults.size() == 0 ? null : queriesResults.get(0);
		}
	}

	private ArrayList<Hashtable<String, Object>> getUnionHelper(ArrayList<Hashtable<String, Object>> baseResult,
			ArrayList<Hashtable<String, Object>> anotherResult)
	{
		if (baseResult == null && anotherResult != null)
			return anotherResult;

		if (baseResult != null && anotherResult == null)
			return baseResult;

		if (baseResult == null && anotherResult == null)
			return null;

		ArrayList<Hashtable<String, Object>> unionResult = new ArrayList<Hashtable<String, Object>>();
		for (int i = 0; i < baseResult.size(); i++)
			unionResult.add(baseResult.get(i));

		for (int i = 0; i < anotherResult.size(); i++)
			if (!unionResult.contains(anotherResult.get(i)))
				unionResult.add(anotherResult.get(i));
		return unionResult;
	}

}
