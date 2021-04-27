package Team_4;

public class SQLTerm
{
	String _strTableName;
	String _strColumnName;
	String _strOperator;
	Object _objValue;

	public SQLTerm()
	{

	}

	public SQLTerm(String tableName, String columnName, String operator, Object objValue)
	{
		this._strTableName = tableName;
		this._strColumnName = columnName;
		this._strOperator = operator;
		this._objValue = objValue;
	}

	public String toString()
	{
		return "TABLE " + this._strTableName + " ==> " + this._strColumnName + " " + this._strOperator + " "
				+ this._objValue;
	}

	// For testing
	public static void main(String[] args)
	{
		SQLTerm[] sqlTerms = new SQLTerm[2];
		SQLTerm sqlTerm = new SQLTerm();
		sqlTerm._strTableName = "Student";
		sqlTerm._strColumnName = "name";
		sqlTerm._strOperator = "=";
		sqlTerm._objValue = new Double(1.5);
		sqlTerms[0] = sqlTerm;
		System.out.println(sqlTerms[0]);
	}

}
