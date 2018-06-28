/**
 * This class provides a vertex object for the graph in the 
 * collaboration graph analyzer.
 * 
 * @author Adam Warner
 * @version 3/27/16
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CGVertex implements Comparable<CGVertex>
{
	int id;
	int degree = 0;
	int distance = -1;
	int CCid = -1;			//The connected component this belongs to
	boolean visited = false;
	float closeness = 0;
	CGVertex previous = null;
	int mode = 0;
	
	List<Integer> connections = new ArrayList<Integer>();
	
	/**
	 * Constructor for the CGVertex object.
	 * 
	 * @param id		The id of the vertex
	 */
	public CGVertex (int id)
	{
		this.id = id;
	}
	
	/**
	 * 
	 */
	public void checkDegree(int c)
	{
		if (!connections.contains(c))
		{
			connections.add(c);
			degree++;
		}
	}
	
	/**
	 * Set comparison to degree (0) or closeness (1) for sorting.
	 * 
	 * @param m		The new setting
	 */
	public void setSort(int m)
	{
		mode = m;
	}
	
	/**
	 * Get the list of vertices connected to this vertex.
	 * 
	 * @return connections		The list of connected vertices
	 */
	public List<Integer> getConnections()
	{
		return connections; 
	}
	
	/**
	 * Display connected vertices on the console.
	 */
	public void displayConnections()
	{
		Iterator<Integer> connIterator = connections.iterator();
		while (connIterator.hasNext()) 
		{
			System.out.print(" " + connIterator.next() + " ");
		}
		System.out.println();
	}
	
	/**
	 * Display vertex ID and degree on the console.
	 * 
	 * @param mode		Display either degree (0) or closeness (1)
	 */
	public void display(int mode)
	{
		if (mode == 0)
			System.out.println("id: " + id + " degree: " + degree);
		else
			System.out.println("id: " + id + " closeness " + closeness);
	}

	/**
	 * Sorts the list in order of decreasing degree centrality.
	 * 
	 * @param 	vert		The vertex this is being compared to
	 * @return	int			-1 for lower centrality, 0 for equal, 1 for greater
	 */
	@Override
	public int compareTo(CGVertex vert)
	{
		if (mode == 0)
		{
			if (this.degree > vert.degree)
				return -1;
			else if (this.degree < vert.degree)
				return 1;
		} else
		{
			if (this.closeness < vert.closeness)
				return -1;
			else if (this.closeness > vert.closeness)
				return 1;
		}
		
		return 0;
	}

}
