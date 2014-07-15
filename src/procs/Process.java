package procs;

/**
 * Holds details of each Process executing in the core
 * 
 * @author richpl
 */
public class Process 
{
	// Number of instructions comprising the Process
	private int length;
	
	// Instruction pointer, defined relative to the 
	// first instruction in the Process (zero)
	private int ptr;
	
	// Number of instructions executed during the
	// lifetime of this Process
	private int numExecutions;
	
	// Absolute address of first instruction of this
	// Process in the core
	private int address;
	
	/**
	 * Constructs a new Process, with 
	 * the first instruction
	 * located at the specified Core address.
	 * 
	 * @param address Absolute address in the Core of
	 * the first instruction of the Process
	 * @param length The total number of instructions
	 * making up this Process
	 */
	public Process(final int address, final int length)
	{		
		this.length = length;
		
		// Set instruction pointer to first address
		ptr = 0;
		
		// Set initial number of instruction executions
		numExecutions = 0;
		
		// Set Core address of first instruction
		this.address = address;
	}
	
	/** 
	 * @return The number of instructions executed during the lifetime
	 * of this process
	 */
	public int numExecutions()
	{
		return (numExecutions);
	}
	
	/**
	 * Returns the current value of the Process instruction pointer,
	 * which is expressed relative to the first instruction of the Process
	 * (at position zero)
	 * 
	 * @return The instruction pointer value
	 */
	public int ptr()
	{
		return (ptr);
	}
	
	/**
	 * @return The absolute address of the first instruction of this Process
	 * in the core
	 */
	public int address()
	{
		return (address);
	}
	
	/**
	 * @return The number of instructions in this Process
	 */
	public int length()
	{
		return (length);
	}
	
	/**
	 * Increments the instruction pointer, wrapping around
	 * to the first instruction if necessary.
	 */
	public void incrementPtr()
	{
		ptr++;
		
		if (ptr == length)
		{
			// We have gone beyond the last instruction
			ptr = 0;
		}
		
		numExecutions++;
	}
	
}
