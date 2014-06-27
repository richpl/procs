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
	// The core itself. Processes are lists of instructions
	// represented as strings
	private String [] core;
	
	/**
	 * Denotes and empty address in the Core
	 */
	public static final String EMPTY = "Empty";
	
	/**
	 * Constructs a new Core of the specified size
	 * 
	 * @param size The number of addresses to be contained
	 * within the core
	 */
	public Core(int size)
	{
		// Initialise the core with null strings representing
		// empty addresses
		core = new String[size];
		for (int index=0; index<core.length; index++)
		{
			core[index] = Core.EMPTY;
		}
	}
	
	/** 
	 * Returns the size of the Core.
	 * 
	 * @return The number of possible addresses in the Core
	 */
	public int size()
	{
		return (core.length);
	}

	/**
	 * Adds a Process to the Core, with its initial instruction
	 * at the specified address
	 * 
	 * @param proc The process to be added
	 * @param address The address at which to add the Process
	 * 
	 * @throws IndexOutOfBoundsException Signals that the process
	 * contained too many instructions to be accommodated within
	 * the Core
	 */
	public void addProcess(Process proc, int address)
		throws IndexOutOfBoundsException
	{
		//TODO
	}
	
	/**
	 * Returns the list of instructions that make up the
	 * specified process as an array of strings.
	 * 
	 * @param proc The process whose instructions are to be 
	 * returned
	 * 
	 * @return The ordered list of Process instructions
	 */
	public String[] getInstructions(Process proc)
	{
		String[] instructions = {"", ""};
		
		//TODO
		
		return (instructions);
	}
	
	/**
	 * Returns the instruction stored at the specified address,
	 * expressed as a string
	 * 
	 * @param address The address of the instruction
	 * 
	 * @return The string representing the instruction
	 * 
	 * @throws IndexOutOfBoundsException Signals that the specified
	 * address was outside the bounds of the Core
	 */
	public String getInstruction(int address)
		throws IndexOutOfBoundsException
	{
		if (address < 0 || address >= core.length)
		{
			throw new IndexOutOfBoundsException("Invalid core address specified");
		}
		
		return (core[address]);
	}
	
}

