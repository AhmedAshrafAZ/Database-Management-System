package Team_4;

public class DBAppException extends Exception
{
	private static final long serialVersionUID = -445821990928049212L;

	public DBAppException(String message)
	{
		System.err.print("ERROR ==>{ ");
		System.out.print(message);
		System.err.println(" }<==");		
	}
}
