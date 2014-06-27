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
	
	/**
	 * Number of addresses that the Core can store
	 */
	public static final int CORE_SIZE = 10000;
	
	// List of current Processes, defined as a list 
	// of core addresses that hold their first instruction
	private List<Process> processList;
	
	// Map to hold details of unique Processes. Keys
	// are created by hashing the string derived by
	// concatenating the ordered Process instructions together,
	// while the values are the instructions lists themselves
	private Map<Integer, String[]> typeRegister;
	
	// Map to hold details of the range of lifetimes of 
	// currently executing Processes. Keys are lifetimes and values
	// are number of processes with that lifetime.
	private Map<Integer, Integer> lifetimeRegister;
	
	// Core in which to execute Processes
	private Core core;
	
	public CPU()
	{
		processList = new Vector<Process>();
		
		typeRegister = new HashMap<Integer, String[]>();
		
		lifetimeRegister = new HashMap<Integer, Integer>();
		
		core = new Core(CORE_SIZE);
	}
	
	/**
	 * Adds a new process to the Core and starts executing it
	 * 
	 * @param instructions The ordered list of instructions making
	 * up the Process, expressed as an array of strings
	 * @param address The address in the Core where the first instruction
	 * should be stored
	 */
	public void createProcess(String[] instructions, int address)
	{
		
	}
	
	/**
	 * Executes the current set of Processes, allowing each
	 * one to execute and instruction in turn. Kills any Process
	 * which has reached the end of its lifetime.
	 */
	public void execute()
	{
		for (Process process: processList)
		{
			// Get core address of current instruction,
			// derived from relative value of instruction
			// pointer and known start address of Process
			int currentAddr = process.address() + process.ptr();
			
			// Execute this instruction
			try
			{
				String instruction = core.getInstruction(currentAddr);
				
				switch(instruction)
				{
					case Core.EMPTY:
						// Kill the process, rogue
						// instruction pointer
						
						// TODO
						
						break;
						
					case Process.NOP:
						// Do nothing
						break;
						
					default:
						// TODO
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				// TODO
			}
			
			// Increment the instruction pointer
			process.incrementPtr();
			
			// Check if the lifetime has been exceeded
			if (process.numExecutions() > CPU.LIFETIME)
			{
				// Remove the instructions from the Core
				// TODO
				
				// Delete the Process from the list
				processList.remove(process); // *** Can i remove within loop? ***
			}
		}
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
