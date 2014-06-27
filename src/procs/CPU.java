package procs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * The CPU class is responsible for keeping track of, 
 * and executing Processes in the core. This is done
 * using a round robin method, with each Process being
 * allowed to execute one instruction in turn.
 * 
 * The CPU is also responsible for killing and removing
 * Processes from the core that have executed instructions
 * a specified number of times (the lifetime of a Process).
 * 
 * @author richpl
 */
public class CPU 
{
	/**
	 * Process lifetime, defined as the number of 
	 * instructions it can execute before it is killed
	 * and removed from the core.
	 */
	public static final int LIFETIME = 100000;
	
	// List of current Processes, defined as a list 
	// of core addresses that hold their first instruction
	private List<Integer> processList = new Vector<Integer>();
	
	// Map to hold details of unique Processes. Keys
	// are created by hashing the string derived by
	// concatenating the ordered Process instructions together,
	// while the values are the instructions lists themselves
	private Map<Integer, String[]> typeRegister;
	
	// Map to hold details of the range of lifetimes of 
	// currently executing Processes. Keys are lifetimes and values
	// are number of processes with that lifetime.
	private Map<Integer, Integer> lifetimeRegister;
	
	public CPU()
	{
		typeRegister = new HashMap<Integer, String[]>();
		
		lifetimeRegister = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Returns metrics on currently executing processes,
	 * expressed as a String.
	 * 
	 * @return Newline separated pairs specifying Process lengths and 
	 * number of current Processes of that length
	 */
	public String getMetrics()
	{
		//TODO
		return ("");
	}
}
