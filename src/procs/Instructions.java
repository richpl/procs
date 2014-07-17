package procs;

import java.util.Random;

/**
 * Provides static methods that implement instructions.
 * 
 * @author richpl
 *
 */
public class Instructions 
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
	
	// Number of attempts to find random locations in the
	// core in which to spawn a process or drop a NOP bomb
	private static final int ATTEMPTS = 10;
	
	/**
	 * Modifies the instruction pointer in response to a JMP
	 * instruction
	 * 
	 * @param core The core containing the process
	 * @param process The process whose instruction pointer is
	 * to be modified
	 * @param instruction The JMP instruction
	 */
	public static void movePtr(Core core, Process process, String instruction)
	{
		//TODO
	}
	
	/**
	 * Copies a NOP to a random location in the core that is not
	 * empty and not part of the process executing the copy (i.e.
	 * the NOP should land within another executing process, possibly
	 * disrupting its operation).
	 * 
	 * @param core The core containing the process
	 * @param process The process launching the NOP bomb
	 */
	public static void copyNOP(Core core, Process process)
	{
		//TODO
	}
	
	/**
	 * Spawns a new copy of the specified process in a free portion
	 * of the core (either can empty area or a NOP sled within a 
	 * process that can accommodate the instructions). A process within
	 * an empty portion of the core gets its own execution thread, a
	 * process within a host's NOP sled executes using the execution 
	 * thread of the host.
	 * 
	 * Returns the address of the spawned process, or -1 if
	 * either spawning failed or the process successfully
	 * parasitised another process.
	 * 
	 * @param process The process to be spawned
	 * @param core The core containing the process
	 * 
	 * @return The starting address of the spawned process
	 */
	public static int spawnProcess(Core core, Process process)
	{
		Random random = new Random();
		
		int newAddress = -1;
		
		// Have ten attempts at spawning the process
		for (int attempts=0; attempts<ATTEMPTS; attempts++)
		{
			// Get a random core address
			int address = random.nextInt(core.size());
			
			// See if there is sufficient empty space for a 
			// copy of the process
			boolean allEmpty = true;
			for (int index=0; index<process.length(); index++)
			{
				int location = (address+index) % core.size();
				
				if (core.getInstruction(location) != Core.EMPTY)
				{
					allEmpty = false;
				}
			}
			
			// See if there is a NOP sled within another process
			// that could accommodate this instruction list
			boolean nopSled = true;
			for (int index=0; index<process.length(); index++)
			{
				int location = (address+index) % core.size();
				
				if (core.getInstruction(location) != Instructions.NOP)
				{
					nopSled = false;
				}
			}
			
			// If there is empty space, make a copy
			// of the process and spawn a new execution
			// thread
			if (allEmpty || nopSled)
			{
				// Get a copy of the instructions for
				// the process
				String[] instructions = core.getInstructions(process);
				
				// Make a new copy in the core
				core.addProcess(instructions, address);
				
				// Returned address if process successfully
				// spawned
				if (allEmpty)
				{
					newAddress = address;
				}
				
				// Exit the loop
				break;
			}

		}
		
		return (newAddress);
	}
}
