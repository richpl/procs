package procs;

import java.util.HashMap;
import java.util.Map;

/**
 * Models a simple computer memory capable of hosting multiple
 * instances of the Process type. The memory consists of a circular
 * list of addresses, with each address capable of holding a single
 * Process instruction.
 * 
 * @author richpl
 */
public class Core 
{
	/**
	 * Default core size in terms of number of available
	 * addresses
	 */
	public static final int CORE_SIZE = 10000;
	
	// The core itself. Processes are lists of instructions
	// represented as strings
	private String [] core;
	
	public Core()
	{
		// Initialise the core with null strings representing
		// empty addresses
		core = new String[CORE_SIZE];
		for (int index=0; index<core.length; index++)
		{
			core[index] = "";
		}
	}

}

