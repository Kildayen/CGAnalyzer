/**
 * This class provides a graph object for the 
 * collaboration graph analyzer.
 * 
 * @author Adam Warner
 * @version 3/28/16
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CGGraph 
{
	int vertexCount = 0;
	int edgeCount = 0;
	int CCCount = 0; 		//The number of connected components in this graph
	int bigCC = 0;			//The id of the largest connected component in this graph
	int smallCC = 0;
	int bigCCSize = 0;
	int smallCCSize = 0;

	CGVertex vertices[];

	/**
	 * Constructor for the CGGraph object.
	 */
	public CGGraph(int v)
	{
		 vertices = new CGVertex[v];
	}
	
	/**
	 * Add an edge (and possibly vertices) to the graph.
	 */
	public void add(int a, int b)
	{
		CGVertex vertA = new CGVertex(a);
		CGVertex vertB = new CGVertex(b);
		
		if (vertices[a] == null)
			vertices[a] = vertA;
 
		if (vertices[b] == null)
			vertices[b] = vertB;
 
		vertices[a].checkDegree(b);
		vertices[b].checkDegree(a);
	}
	
	/**
	 * Displays all vertices in this graph.
	 */
	public void showVertices()
	{
		for (int i = 0; i < vertices.length; i++)
		{
			if (vertices[i] != null)
			{
				vertices[i].display(0);
				vertices[i].displayConnections();
			}
		}
	}
	
	/**
	 * Displays the highest vertices by degree centrality, 
	 * with an option to show all connections for each vertex.
	 * 
	 * @param 	num		Display up to this number of high-degree vertices
	 * @param	mode	1 for more information, 0 otherwise
	 * @param	cc		0 for smallest cc, 1 for largest
	 */
	public void getDegreeCentrality(int num, int mode, int cc)
	{
		CGVertex display;
		List<CGVertex> dcList = new ArrayList<CGVertex>();
		
		int testCC = bigCC;
		if (cc == 0)
		{
			num = smallCCSize;
			testCC = smallCC;
		}
			
		for (int i = 0; i < vertices.length; i++)
		{
			if (vertices[i].CCid == testCC)
				dcList.add(vertices[i]);
		}
		
		dcList.sort(null);
		
		System.out.println("Showing Degree Centrality for Connected Component with id: " + testCC);
		for (int i = 0; i < num; i++)
		{		
			display = dcList.get(i);
			System.out.print("#" + (i + 1) + " ");
			display.display(0);
			if (mode == 1)
				display.displayConnections();
		}
	}
	
	/**
	 * Display the highest vertices by closeness centrality,
	 * with an option to show all connections for each vertex.
	 * 
	 * @param 	num		Display up to this number of high-degree vertices
	 * @param	mode	1 for more information, 0 otherwise
	 * @param	cc		0 for smallest cc, 1 for largest
	 */
	public void getClosenessCentrality(int num, int mode, int cc)
	{
		CGVertex vertI, vertJ;
		int testID = bigCC;
		int testSize = bigCCSize;
		CGVertex display;
		List<CGVertex> dcList = new ArrayList<CGVertex>();
		if (cc == 0)
		{
			testSize = smallCCSize;
			num = smallCCSize;
			testID = smallCC;	
		}
		
		//calculating closeness for all vertices in cc
		for (int i = 0; i < vertices.length; i++)
		{
			vertI = vertices[i];
			
			if (vertI.CCid == testID)
				for (int j = 0; j < vertices.length; j++)
				{
					if (i != j)
					{
						vertJ = vertices[j];
						
						if (vertJ.CCid == testID)
							vertI.closeness += ((float)bfs(vertI, vertJ) / (float)(testSize - 1));
					}
				}
		}
		
		//add cc vertices to list
		for (int i = 0; i < vertices.length; i++)
		{
			if (vertices[i].CCid == testID)
			{
				vertices[i].setSort(1);
				dcList.add(vertices[i]);
			}
		}
		
		dcList.sort(null);
		
		System.out.println("Showing Closeness Centrality for Connected Component with id: " + testID);
		for (int i = 0; i < num; i++)
		{		
			display = dcList.get(i);
			System.out.print("#" + (i + 1) + " ");
			display.display(1);
			if (mode == 1)
				display.displayConnections();
		}
	}
	
	/**
	 * Resets tracking information (distance and visited)
	 * in all vertices to the default values (-1 and false).
	 */
	public void resetVertices()
	{
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i].distance = -1;
			vertices[i].visited = false;
			vertices[i].previous = null;
		}
	}
	
	/**
	 * Print degree or closeness data to file.
	 */
	public void printData(int mode)
	{
		int degrees[] = new int[vertices.length];
		Arrays.fill(degrees, 0);
		
		for (int j = 0; j < vertices.length; j++)
		{
			if (vertices[j].degree > 0)
			{
				degrees[vertices[j].degree]++;
			}
		}
		
		try 
		{
			PrintWriter pw = new PrintWriter("degree-data.txt", "UTF-8");
			for (int n = 0; n < 3; n++)
			{
				if (n == 0)
					pw.println("degree");
				else if (n == 1)
					pw.println("count");
				else
					pw.println("probability");
				
				for (int i = 0; i < degrees.length; i++)
				{
					if (mode == 0 && degrees[i] != 0)
					{
						if (n == 0)
							pw.println(i);
						else if (n == 1)
							pw.println(degrees[i]);
						else
							pw.println(((double)degrees[i] / bigCCSize));
					} else 
					{
						//pw.println(vertices[i].closeness);
					}
				}
			}
			pw.close();
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Analyze the graph for connected components, and update
	 * each vertex with the id of the CC that it belongs to.
	 */
	public void findCCs()
	{
		Queue<CGVertex> queue = new LinkedList<CGVertex>();
		List<Integer> conn;
		int nextID;
		CGVertex current;
		
		for (int i = 0; i < vertices.length; i++)
		{
			//ensure all vertices are initialized
			if (vertices[i] == null)
			{
				vertices[i] = new CGVertex(i);
			}
			
			if (!vertices[i].visited)
			{
				queue.add(vertices[i]);

				while (!queue.isEmpty())
				{
					current = queue.remove();
					
					current.visited = true;
					current.CCid = CCCount;

					conn = current.getConnections();
					for (int j = 0; j < conn.size(); j++)
					{
						nextID = conn.get(j);
						
						if(vertices[nextID].visited == false)
							queue.add(vertices[nextID]);
					}
				}
				
				CCCount++;
			}
		}
		
		//find biggest cc
		int curCCSize;
		for (int i = 0; i < CCCount; i++)
		{
			curCCSize = 0;
			
			for(int j = 0; j < vertices.length; j++)
			{
				if (vertices[j].CCid == i)
				{
					curCCSize++;
				}
			}
			if (curCCSize > bigCCSize)
			{
				bigCCSize = curCCSize;
				bigCC = i;
			}
		}
		
		//find smallest cc
		smallCCSize = bigCCSize;
		for (int i = 0; i < CCCount; i++)
		{
			curCCSize = 0;
			
			for(int j = 0; j < vertices.length; j++)
			{
				if (vertices[j].CCid == i)
				{
					curCCSize++;
				}
			}
			if (curCCSize < smallCCSize && curCCSize > 0)
			{
				smallCCSize = curCCSize;
				smallCC = i;
			}
		}
		
		resetVertices();
		
		System.out.println("CC Count: " + CCCount);
		System.out.println("Largest Connected Component id: " + bigCC + " with size: "+ bigCCSize);
		System.out.println("Smallest Connected Component id: " + smallCC + " with size: " + smallCCSize);
	}
	
	/**
	 * Finds distance between two given vertices and displays it on the console.
	 * 
	 * @param i		index of the start vertex
	 * @param j		index of the target vertex
	 */
	public void getDistance(int i, int j)
	{
		System.out.println("distance between vertices " + i + " and " + j + ": " + bfs(vertices[i], vertices[j]));
	}
	
	/**
	 * Perform a breadth-first search to find 
	 * the minimal path between two given vertices.
	 * 
	 * @param 	initial		The initial vertex
	 * @param 	target		The target vertex
	 * @return	distance	The distance between initial and final vertices
	 */
	public int bfs(CGVertex initial, CGVertex target)
	{
		int distance = 1;
		int nextID;
		CGVertex current;
		CGVertex previous = new CGVertex(-1);
		Queue<CGVertex> queue = new LinkedList<CGVertex>();
		List<Integer> conn;
		
		queue.add(initial);
		initial.distance = 0;

		while (!queue.isEmpty())
		{
			current = queue.remove();
			conn = current.getConnections();
			
			if (previous == null)
				previous = initial;
			
			if (current != target)
			{
				//check connected vertices
				for (int j = 0; j < conn.size(); j++)
				{
					nextID = conn.get(j);
					
					if (!vertices[nextID].visited)
					{
						queue.add(vertices[nextID]);
						vertices[nextID].visited = true;
						vertices[nextID].previous = current;	
					}
				}
				
			} else
			{
				queue.clear();
			}
			previous = current;
		}
		
		current = target;
		
		distance = 0;
		while (current != initial)
		{
			previous = current.previous;
			current = previous;		
			distance++;
		}
		resetVertices();
		return distance;
	}
}
