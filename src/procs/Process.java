package procs;

/**
 * Holds details of each Process executing in the core
 * 
 * @author richpl
 */
public class Process 
{
	/**
	 *  Representation of the jump instruction which
	 *  is used to move the instruction pointer a 
	 *  specified number of instructions backwards 
	 *  or forwards (or to the first or last instruction
	 *  in the Process if that is closer to the jump 
	 *  address)
	 */
	public static final String JMP = "JMP";
	
	/**
	 * Representation of the no operation instruction
	 */
	public static final String NOP = "NOP";
	
	/**
	 * Copy NOP instruction (NOP bomb) that copies
	 * a NOP to a random place in the core, as long as
	 * the target address is not empty (i.e. can only
	 * be copied into the instruction list of a Process)
	 */
	public static final String CPN = "CPN";
	
	/**
	 * Spawn instruction, which creates a copy of this
	 * Process starting at a random location within the core.
	 * However, there must be sufficient empty addresses 
	 * at and after the target location for the Process to be
	 * copied. In this case, a new Process is created which will
	 * begin executing the copied instructions.
	 * 
	 * Alternatively, the Process may copy itself to the inside
	 * of another Process if there are sufficient NOPs at and 
	 * after the target location for the Process to be copied.
	 * In this case, no new Process is created, but the instructions
	 * may be executed by the infected Process.
	 */
	public static final String SPW = "SPW";
	
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
	 * 
	 * @throws IndexOutOfBoundsException Signals that the
	 * specified address is not within the core bounds.
	 */
	public Process(int address, int length)
		throws IndexOutOfBoundsException
	{
		// Check core address
		if (address < 0 || address > Core.CORE_SIZE)
		{
			throw new IndexOutOfBoundsException("Invalid Core address specified");
		}
		
		// Check for silly lengths
		if (length < 1 || length > Core.CORE_SIZE)
		{
			throw new IndexOutOfBoundsException("Invalid Process length specified");
		}
		
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
	 * @return The absolute address of the first instruction of this Process
	 * in the core
	 */
	public int address()
	{
		return (address);
	}
}
