package Team_4;

import java.awt.Dimension;
import java.awt.Polygon;
import java.util.StringTokenizer;

public class polygon extends Polygon implements Comparable<polygon>
{

	private static final long serialVersionUID = -5713107373149025434L;

	public polygon(Polygon poly)
	{
		this.xpoints = poly.xpoints;
		this.ypoints = poly.ypoints;
		this.npoints = poly.npoints;
	}

	public polygon(String strPoints)
	{
		StringTokenizer st = new StringTokenizer(strPoints, "()");
		String points = "";
		while (st.hasMoreTokens())
			points += st.nextToken().trim();
		String[] arrPoints = points.split(",");
		this.xpoints = new int[arrPoints.length / 2];
		this.ypoints = new int[arrPoints.length / 2];
		for (int i = 0, j = 0; i < arrPoints.length; i++)
		{
			this.xpoints[j] = Integer.parseInt(arrPoints[i++]);
			this.ypoints[j++] = Integer.parseInt(arrPoints[i]);
		}
		this.npoints = xpoints.length;
	}

	@Override
	public int compareTo(polygon poly2)
	{
		Dimension d1 = this.getBounds().getSize();
		Dimension d2 = poly2.getBounds().getSize();
		int a1 = d1.height * d1.width;
		int a2 = d2.height * d2.width;
		return ((Integer) a1).compareTo((Integer) a2);
	}

	@Override
	public String toString()
	{
		Dimension d = this.getBounds().getSize();
		return (d.height * d.width) + " ";
	}

}
