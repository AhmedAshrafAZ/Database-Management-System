package Team_4;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class configFileReader
{

	public int getPropValues(String property)
	{
		InputStream inputStream;
		Properties prop = new Properties();
		try
		{
			String propFilePath = "config/DBApp.properties";
			inputStream = new FileInputStream(propFilePath);
			prop.load(inputStream);
			inputStream.close();
		} catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
		return Integer.parseInt(prop.getProperty(property));

	}

	public static void main(String[] args) throws IOException
	{
		configFileReader cfr = new configFileReader();
		System.out.println(cfr.getPropValues("MaximumRowsCountinPage"));
	}

}
