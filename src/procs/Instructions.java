package procs;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;

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
	 * @param process The process whose instruction pointer is
	 * to be modified
	 * @param instruction The JMP instruction
	 */
	public static void movePtr(Process process, String instruction)
	{
		// Parse out the jump instruction to see how
		// far to move the instruction pointer
		try
		{
			StringTokenizer st = new StringTokenizer(instruction);
		
			try
			{
				String command = st.nextToken();
				if (command.equals(Instructions.JMP))
				{
					// Only process if we have a valid jump instruction,
					// now obtain jump value
					String jumpVal = st.nextToken();
					
					// Check that this is a valid numeral
					try
					{
						int value = new Integer(jumpVal);
						
						// Move the instruction pointer the 
						// specified number of steps, wrapping
						// around as necessary						
						if (value >= 0)
						{
							// Increment the instruction pointer
							for (int index=1; index<value; index++)
							{
								process.incrementPtr();
							}
						}
						else
						{
							// Decrement the instruction pointer
							for (int index=0; index>value; index--)
							{
								process.decrementPtr();
							}
						}
					}
					catch (NumberFormatException e)
					{
						System.err.println("Invalid jump value encountered");
					}
				}
			} 
			catch (NoSuchElementException e)
			{
				System.err.println("Incomplete jump instruction encountered");
			}
		}
		catch (NullPointerException e)
		{
			System.err.println("Null instruction found when jumping");
		}
	}
	
	/**
	 * Copies a NOP to a random location in the core that is not
	 * empty and not part of the process executing the copy (i.e.
	 * the NOP should land within another executing process, possibly
	 * disrupting its operation). Will try a number of times to achieve this
	 * if an empty location is selected, the number of tries being 
	 * governed by the ATTEMPTS constant.
	 * 
	 * NOP bombs can only be launched
	 * upon parts of the core near to the process which launches them. The
	 * effective range is decided by the specified parameter (which is
	 * applied modulo the core size). The range could be either before or
	 * after the process in the core (i.e. a range of 100 will cause 
	 * a NOP bomb to land anywhere within 100 locations either before
	 * the start or after the end location of the process).
	 * 
	 * @param core The core containing the process
	 * @param process The process launching the NOP bomb
	 * @param range Range over which to launch the NOP bomb
	 */
	public static void copyNOP(Core core, Process process, int range)
	{
		Random random = new Random();
		
		// Get a random value within the range, adding one to the
		// answer in case we get a zero
		int bombRange = random.nextInt(range) + 1;
		
		int location;
		
		// Randomly decide whether or not to make it negative
		int makeNegative = random.nextInt(1);
		
		// Have ten attempts at spawning the process
		for (int attempts=0; attempts<ATTEMPTS; attempts++)
		{
			if (makeNegative == 0)
			{			
				// Now obtain the value at the relevant location
				// in the core
			
				// Examine the potential NOP bomb location,
				// taking care to wrap around if we reach the start of the core
				if (process.address() < bombRange)
				{
					int partialRange = bombRange - process.address();
					
					location = core.size() - partialRange;
				}
				else
				{
					location = process.address() - bombRange;
				}
			}
			else
			{
				// Now obtain the value at the relevant location
				// in the core
				// Get location of last address of process
				int lastAddress = 
					(process.address() + process.length()-1) % core.size();
			
				// Examine the potential NOP bomb location
				location = (lastAddress + bombRange) % core.size();
			}
		
			if (!core.getInstruction(location).equals(Core.EMPTY))
			{
				core.setInstruction(Instructions.NOP, location);
				
				// Exit the loop
				break;
			}
		}
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
