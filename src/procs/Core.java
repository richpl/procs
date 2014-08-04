package procs;

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
	public static final String EMPTY = "";
	
	/**
	 * Constructs a new Core of the specified size
	 * 
	 * @param size The number of addresses to be contained
	 * within the core
	 */
	public Core(final int size)
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
	 * Adds a list of instructions to the Core, with its initial 
	 * instruction at the specified address
	 * 
	 * @param instructions The instructions to be added, as an ordered
	 * list of strings
	 * @param address The address at which to add the Process
	 * 
	 * @throws IndexOutOfBoundsException Signals that the process
	 * contained too many instructions to be accommodated within
	 * the Core
	 */
	public void addProcess(final String[] instructions, final int address)
		throws IndexOutOfBoundsException
	{
		if (address < 0 || address >= core.length)
		{
			throw new IndexOutOfBoundsException("Invalid core address specified");
		}
		
		// Add instructions to core, modulo the core 
		// size to ensure wrap around from end to start.
		// Only add instructions if there is sufficient
		// empty space however, either an empty location 
		// or a NOP sled
		
		// First check that there is sufficient space
		boolean isSpace = true;
		for (int index=0;index<instructions.length;index++)
		{
			int location = (index+address) % core.length;
			
			if (!core[location].equals(Core.EMPTY)
				&&
				!core[location].equals(Instructions.NOP))
			{
				isSpace = false;
			}

		}
		
		// Only add instructions if room
		if (isSpace)
		{
			for (int index=0;index<instructions.length;index++)
			{
				int location = (index+address) % core.length;

				core[location] = instructions[index];
			}
		}
	}
	
	/**
	 * Returns the list of instructions that make up the
	 * specified process as an array of strings.
	 * 
	 * @param process The process whose instructions are to be 
	 * returned
	 * 
	 * @return The ordered list of Process instructions. Returns an
	 * empty list if either the Process address or its length 
	 * are not valid.
	 */
	public String[] getInstructions(final Process process)
	{	
		// Get start address of Process
		int address = process.address();
		
		// Return empty list for invalid addresses
		if (address < 0 || address >= core.length ||
			process.length() > core.length)
		{
			return (new String[0]);
		}
		
		String[] instructions = new String[process.length()];
		
		for (int index=0;index<process.length();index++)
		{
			int location = (index+address) % core.length;
			
			instructions[index] = core[location];
		}
		
		return (instructions);
	}
	
	/**
	 * Deletes instructions corresponding to the specified
	 * Process from the Core, replacing them with empty
	 * values.
	 * 
	 * @param process The process whose instructions are
	 * to be removed
	 */
	public void removeProcess(final Process process)
	{
		// Get start address of Process
		int address = process.address();
		
		// Do nothing for invalid processes
		if (address >= 0 && address < core.length &&
			process.length() <= core.length)
		{
			for (int index=0;index<process.length();index++)
			{
				int location = (index+address) % core.length;
			
				core[location] = Core.EMPTY;
			}
		}
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
	public String getInstruction(final int address)
		throws IndexOutOfBoundsException
	{
		if (address < 0 || address >= core.length)
		{
			throw new IndexOutOfBoundsException("Invalid core address specified");
		}
		
		return (core[address]);
	}
	
	/**
	 * Sets the instruction to be stored at the specified address,
	 * expressed as a string
	 * 
	 * @param address The address of the instruction
	 * @param instruction The instruction to be inserted
	 * 
	 * @throws IndexOutOfBoundsException Signals that the specified
	 * address was outside the bounds of the Core
	 */
	public void setInstruction(final String instruction, final int address)
		throws IndexOutOfBoundsException
	{
		if (address < 0 || address >= core.length)
		{
			throw new IndexOutOfBoundsException("Invalid core address specified");
		}
		
		core[address] = instruction;
	}
	
	/**
	 * Returns a string representation of the process, as a bracketed
	 * list of instructions.
	 * 
	 * @param process The process to be represented as a string.
	 * 
	 * @return The string representation of the process.
	 */
	public String processToString(Process process)
	{
		String[] instructions = getInstructions(process);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		
		boolean isFirst = true;
		for (String instruction: instructions)
		{
			if (!isFirst)
			{
				stringBuilder.append(";");
			}
			else
			{
				isFirst = false;
			}
			
			stringBuilder.append(instruction);
		}
		
		stringBuilder.append("]");
		
		return (stringBuilder.toString());
	}
	
}

