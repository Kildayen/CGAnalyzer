/**
 * This program analyzes a graph representing a collaboration network.
 * Vertices represent authors, edges represent coauthor relationships.
 *
 * @author Adam Warner
 * @version 3/29/16
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class CGAnalyzer {

	public static void main(String[] args) throws Exception
	{
		String filename;
		CGGraph graph = null;
		int info = 0;
		int mode = 0;
		if (args.length != 3) usage();
		mode = Integer.parseInt(args[1]);
		info = Integer.parseInt(args[2]);
		filename = args[0];
		
		//Read file and setup graph
		try 
		{
			File graphFile = new File(filename);
			BufferedReader buffReader = new BufferedReader(new FileReader(graphFile));
		    String line = "";
		    String token = "";
		    Scanner lineReader = null;
		    
		    while ((line = buffReader.readLine()) != null) 
		    {
			    lineReader = new Scanner(line);
		    	
		    	if (lineReader.hasNext())
		    		token = lineReader.next();
		    	
		    	if (token.equals("g"))
		    		 graph = new CGGraph(Integer.parseInt(lineReader.next()));
		    	
		    	if (token.equals("e"))
		    	{
		    		int a = Integer.parseInt(lineReader.next());
		    		int b = Integer.parseInt(lineReader.next());

		    		graph.add(a, b);
		    	}
		    }

		    lineReader.close();
			buffReader.close();
		
		}  catch (FileNotFoundException e) 
		{
            System.out.println(e);
            e.printStackTrace();
        }
		
		//Perform analysis on graph
	    graph.findCCs();
		
		if (mode == 0 || mode == 2)
		{
			graph.getDegreeCentrality(40, info, 1);
			graph.printData(0);
		}
		
		if (mode > 0)
		{
			graph.getClosenessCentrality(40, 0, 1);
			//graph.printData(1);
		}
	}
	
	// Print a usage message and exit with errors
	private static void usage()
	{
		System.err.println("Usage: java CGAnalyzer <filename> <mode> <info>");
		System.err.println("<filename> is the name of the text file to analyze");
		System.err.println("<mode> = 0 for degree centrality, 1 for closeness, 2 for both");
		System.err.println("<info> = 1 to display extra information, 0 otherwise");
		System.exit(1);
	}
}
