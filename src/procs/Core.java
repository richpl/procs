package procs;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

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
	 * Probability, expressed as a percentage, that a single
	 * instruction will be randomly changed when a process
	 * is copied in the core.
	 */
	private static int mutProb;
	
	private Random random;
	
	/**
	 * Constructs a new Core of the specified size
	 * 
	 * @param size The number of addresses to be contained
	 * within the core
	 * @param mutProb Probability, expressed as a percentage, that an
	 * instruction will mutate during a copy
	 * 
	 * @throws NumberFormatException Signals that a non-percentage
	 * mutation probability was specified
	 */
	public Core(final int size, final int mutProb)
		throws NumberFormatException
	{
		// Add mutation probability as a parameter so that unit
		// testing is predictable
		if (mutProb < 0 || mutProb > 100)
		{
			throw new NumberFormatException
				("Invalid mutation probability specified");
		}
			
		Core.mutProb = mutProb;
		
		// Initialise the core with null strings representing
		// empty addresses
		core = new String[size];
		for (int index=0; index<core.length; index++)
		{
			core[index] = Core.EMPTY;
		}
		
		// Initialise the random number generator
		// that governs the mutation rate
		random = new Random(new Date().getTime());
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
	 * instruction at the specified address.
	 * 
	 * @param instructions The instructions to be added, as an ordered
	 * list of strings
	 * @param address The address at which to add the Process
	 * @param process Handle for the process which is being added
	 * 
	 * @throws IndexOutOfBoundsException Signals that the process
	 * contained too many instructions to be accommodated within
	 * the Core
	 */
	public void addProcess(final String[] instructions, final int address,
			               Process process)
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
		
		// Mutate the instruction list with the specified
		// probability
		String[] newInstructions = 
				mutateInstructions(instructions, process);
		
		// TODO
		// Is there room for an extra instruction?
		
		// Only add instructions if room
		if (isSpace)
		{
			for (int index=0;index<newInstructions.length;index++)
			{
				int location = (index+address) % core.length;

				core[location] = newInstructions[index];
			}
		}
	}
	
	/**
	 * Mutates a string of instructions either
	 * by modifying an instruction, deleting
	 * an instruction, or inserting an instruction, with
	 * the probability specified to the constructor method.
	 * 
	 * @param instructions The instruction list to be mutated
	 * @param process The process associated with the instruction
	 * list
	 * 
	 * @return The mutant instruction list
	 */
	private String[] mutateInstructions(final String[] instructions,
										Process process)
	{
		String[] newInstructions;
			
		// Determine whether to introduce a mutation 
		// which changes the instruction
		int mutateProbability = random.nextInt(100);

		if (mutateProbability < mutProb)
		{
			// Pick a random location within the instruction string
			int position = random.nextInt(instructions.length);
			
			// Use a further probability to determine
			// the type of mutation
			int mutTypeProbability = random.nextInt(100);
			
			if (mutTypeProbability < 33)
			{
				// Make a copy of the instruction list
				newInstructions = new String[instructions.length];
				for (int index=0; index<instructions.length; index++)
				{
					if (index != position)
					{
						newInstructions[index] = instructions[index];
					}
					else
					{
						newInstructions[index] = 
								mutateInstruction(instructions[index], 
										          position, 
										          instructions.length);
					}
				}
			}
			else if (mutTypeProbability >= 33 &&
					 mutTypeProbability < 66)
			{
				// Mutate by deleting an instruction
				
				//TODO - Fix this!!!!!!!! Bombs out when position is
				// last element of array
				String[] start = null;
				String[] end = null;
				try
				{
					start = 
						Arrays.copyOfRange(instructions, 
						                   0, position-1);
				
					end = 
						Arrays.copyOfRange(instructions, 
				                           position+1, 
				                           instructions.length-1);
				
				}
				catch (Exception e)
				{
					System.err.println("Help1: " + e.getMessage());
				}
				
				newInstructions = new String[instructions.length-1];
				
				try
				{
					System.arraycopy(start, 0, newInstructions, 0, start.length);
					System.arraycopy(end, 0, newInstructions, 
									 start.length, end.length);
				}
				catch (Exception e)
				{
					System.err.println("Help: " + e.getMessage());
				}
				
				// Reduce the corresponding process length
				process.setLength(process.length() - 1);
			}
			else
			{
				// Mutate by adding an additional instruction and 
				// modifying the corresponding process length
				// TODO
				newInstructions = new String[instructions.length];
				for (int index=0; index< instructions.length; index++)
				{
					newInstructions[index] = instructions[index];
				}
			} 
			
			return (newInstructions);
		}
		else
		{
			// Return the instruction sequence unmodified
			return (instructions);
		}
	}
	
	/**
	 * Returns a new instruction which replaces the
	 * specified instruction with a certain probability.
	 * 
	 * @param instruction The instruction to be mutated
	 * @param position The length of the instruction list from
	 * which the specified instruction comes, so that JMP
	 * instructions can be generate correctly
	 * @param length The length of the instruction list from
	 * which the specified instruction comes, so that JMP
	 * instructions can be generated correctly
	 * 
	 * @return The mutated instruction
	 */
	private String mutateInstruction(String instruction, int position, int length)
	{
		String newInstruction = instruction;
		
		// Set up some random numbers to govern the 
		// nature of the mutation and the new instruction
		// type
		int mutationType = random.nextInt(100);
			
		// If the instruction is already a jump, strip
		// off the value before the switch test
		if (instruction.startsWith(Instructions.JMP))
		{
			instruction = Instructions.JMP;
		}
			
		switch (instruction)
		{
			case Instructions.CPN:
					
				if (mutationType <= 33)
				{
					newInstruction = Instructions.JMP;
				}
				else if (mutationType > 33 &&
						 mutationType <= 66)
				{
					newInstruction = Instructions.NOP;
				}
				else
				{
					newInstruction = Instructions.SPW;
				}
					
				break;
					
			case Instructions.JMP:
					
				if (mutationType <= 33)
				{
					newInstruction = Instructions.CPN;
				}
				else if (mutationType > 33 &&
						 mutationType <= 66)
				{
					newInstruction = Instructions.NOP;
				}
				else
				{
					newInstruction = Instructions.SPW;
				}
					
				break;
					
			case Instructions.NOP:
					
				if (mutationType <= 33)
				{
					newInstruction = Instructions.JMP;
				}
				else if (mutationType > 33 &&
						 mutationType <= 66)
				{
					newInstruction = Instructions.CPN;
				}
				else
				{
					newInstruction = Instructions.SPW;
				}
					
				break;
					
			case Instructions.SPW:
					
				if (mutationType <= 33)
				{
					newInstruction = Instructions.JMP;
				}
				else if (mutationType > 33 &&
						 mutationType <= 66)
				{
					newInstruction = Instructions.NOP;
				}
				else
				{
					newInstruction = Instructions.CPN;
				}
					
				break;
					
			default:
					
				// Should not occur
				assert false;
					
				break;
		}

		// If we have converted to a jump, we have to add a 
		// jump value
		if (newInstruction.equals(Instructions.JMP))
		{
			// Variable to determine the range of the jump
			int range;
				
			// Decide whether to jump forward or back
			int makeNegative = random.nextInt(2);
				
			if (makeNegative == 0)
			{
				// Jump backward
				// Work out how many instructions before
				// this one until the start of the process
				int numInstructions = position;
					
				// Randomly pick a negative value in that range
				try
				{
					range = 0 - random.nextInt(numInstructions+1);
				}
				catch (IllegalArgumentException e)
				{
					System.err.println("Illegal jump range calculated");
					range = 0;
				}
			}
			else
			{
				// Jump forward
				// Work out how many instructions after
				// this one until the end of the process
				int numInstructions = length - position - 1;
					
				// Randomly pick a value in that range
				try
				{
					range = random.nextInt(numInstructions);
				}
				catch (IllegalArgumentException e)
				{
					System.err.println("Illegal jump range calculated");
					range = 0;
				}
			}

			newInstruction = newInstruction + " "
	                         + String.valueOf(range);
			
		}
		
		return (newInstruction);
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

