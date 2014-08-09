package procs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
	public static final int CORE_SIZE = 100000;
	
	/**
	 * Range of NOP bombs, in terms of number of core
	 * locations
	 */
	public static final int RANGE = 100;
	
	/**
	 * Probability, expressed as a percentage, that
	 * two nearby processes will swap instructions
	 * during a particular execution cycle 
	 */
	public static final int SWAP_PROB = 1;
	
	// List of current Processes, defined as a list 
	// of core addresses that hold their first instruction
	private List<Process> processes;
	
	// Map to hold details of unique Processes. Keys
	// are created by hashing the string derived by
	// concatenating the ordered Process instructions together,
	// while the values are the instructions lists themselves
	private Map<Integer, String[]> genomes;

	// Core in which to execute Processes
	private Core core;
	
	// The digest algorithm to be used to hash lists of 
	// instructions
	private MessageDigest md;
	
	// Ancestral process with which to initially 
	// innoculate the Core
	private String[] ancestor = 
		{Instructions.NOP, Instructions.NOP, Instructions.SPW, 
		 Instructions.NOP, Instructions.NOP};
	
	// Random number generator to be used in a number
	// of methods
	private Random random;
	
	/**
	 * Initialises the system, innoculating the Core
	 * with a predefined ancestor Process.
	 */
	public CPU()
	{
		processes = new Vector<Process>();
		
		genomes = new HashMap<Integer, String[]>();
		
		core = new Core(CORE_SIZE);
		
		// Define the message digest algorithm to use
		try 
		{
			md = MessageDigest.getInstance("SHA");
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// Should never happen, we specified SHA, a valid algorithm
			System.err.println("Invalid hash algorithm specified " +
				               "during initialisation");
			assert false;
		}
		
		// Innoculate the core with the ancestor
		// starting at a random address
		
		try
		{
			// Get a random location in the Core
			random = new Random();
			int address = random.nextInt(CORE_SIZE);
		
			// Add the process at that location
			core.addProcess(ancestor, address);
		
			// Create a corresponding Process and add it to the list
			// of current processes
			Process process = new Process(address, ancestor.length);
			processes.add(process);
		
			// Register in the table of unique processes and process 
			// lifetimes
			genomes.put(hash(ancestor), ancestor);
		}
		catch (IndexOutOfBoundsException e)
		{
			// Should not encounter an invalid core
			// address
			System.err.println("Invalid core address specified " +
						       "during initialisation");
			assert false;
		}
		
	}
	
	/**
	 * Generates a hashcode for the specified list
	 * of instructions.
	 * 
	 * @param instructions The list of instructions
	 * 
	 * @return The hashcode derived from the digest 
	 * of the instructions
	 */
	private int hash(final String[] instructions)
	{	
		byte[] digest = {};
		
		// Create a hash of this string array using the 
		// specified algorithm
		for (int index=0; index<instructions.length-1; index++)
		{
			md.update(instructions[index].getBytes());
		}
		// Complete the digest with the final block
		digest = md.digest(instructions[instructions.length-1].getBytes());
		
		return (Arrays.hashCode(digest));
	}
	
	/**
	 * Kills the specified Process
	 * 
	 * @param proc The Process to be killed
	 */
	private void killProcess(final Process process)
	{
		// Remove its instructions from the Core
		core.removeProcess(process);
		
		// Remove the Process from the execution list
		processes.remove(process);		
	}
	
	/**
	 * Executes the current set of Processes, allowing each
	 * one to execute and instruction in turn. Kills any Process
	 * which has reached the end of its lifetime.
	 * 
	 * With a random probability, will cause two nearby processes 
	 * to possibly swap instructions once per execution cycle.
	 * 
	 * @throws IndexOutOfBoundsException Signals that an invalid core
	 * address was specified
	 */
	public void execute() throws IndexOutOfBoundsException
	{
		// List to keep a note of processes that 
		// should be killed
		List<Process> deadProcesses = new Vector<Process>();
		
		// List to keep a note of new processes that
		// need to be added
		List<Process> newProcesses = new Vector<Process>();
		
		for (Process process: processes)
		{
			// Get core address of current instruction,
			// derived from relative value of instruction
			// pointer and known start address of Process
			int currentAddr = (process.address()+process.ptr()) % CORE_SIZE;
			
			String instruction = core.getInstruction(currentAddr);
				
			switch(instruction)
			{
				case Core.EMPTY:
					// Kill the process, rogue
					// instruction pointer
					deadProcesses.add(process);
						
					break;
						
				case Instructions.NOP:
					// Do nothing
						
					break;
					
				case Instructions.JMP:
					// Modify the instruction pointer
					Instructions.movePtr(process, instruction);
						
					break;
						
				case Instructions.SPW:
					// Spawn a copy of this process in
					// a random location in the core
					newProcesses.add(process);
						
					break;
						
				case Instructions.CPN:
					// Copy a NOP to a random location
					// in the core that is not empty and
					// not occupied by this process
					Instructions.copyNOP(core, process, RANGE);
						
					break;
						
				default:
					// Something is screwy here,
					// kill the process
					deadProcesses.add(process);
			}
			
			// Increment the instruction pointer
			process.incrementPtr();
			
			// Increment the number of executions
			process.incrementNumExecutions();
			
			// Check if the lifetime has been exceeded
			if (process.numExecutions() > CPU.LIFETIME)
			{
				// Kill the process
				deadProcesses.add(process);
			}
			
			// Swap instructions between nearby processes, with
			// a given probability
			int swapProb = random.nextInt(100);
			if (swapProb <= SWAP_PROB)
			{
				// Swap instructions, if possible, between
				// two processes that are less than 100
				// locations apart
				int range = random.nextInt(100);
				int location1 = random.nextInt(CORE_SIZE);
				int location2 = (location1 + range) % CORE_SIZE;
				
				// If neither location is empty, swap
				String instruction1 = core.getInstruction(location1);
				String instruction2 = core.getInstruction(location2);
				
				if (!instruction1.equals(Core.EMPTY) &&
					!instruction2.equals(Core.EMPTY))
				{
					core.setInstruction(instruction2, location1);
					core.setInstruction(instruction1, location2);
				}
				
			}
			
		}
	
		// Dispose of processes to be killed
		for (Process process: deadProcesses)
		{
			killProcess(process);
		}

		// Add newly created processes
		for (Process process: newProcesses)
		{
			int address = Instructions.spawnProcess(core, process);
			
			if (address != -1)
			{
				// Add to the process list
				Process newProcess = new Process(address, process.length());
				processes.add(newProcess);
			
				// Update the unique genomes repository
				String[] instructions = core.getInstructions(newProcess);
				genomes.put(hash(instructions), instructions);
			}
		}
	}
		
	/**
	 * Returns a string representation of the instruction list, as a bracketed
	 * list of instructions.
	 * 
	 * @param process The instruction list.
	 * 
	 * @return The string representation of the instructions.
	 */
	private String instructionsToString(String[] instructions)
	{	
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
	
	/**
	 * Pretty prints the list of unique genomes present in the core.
	 */
	public void prettyPrintGenomes()
	{
		Set<Map.Entry<Integer, String[]>> entrySet = genomes.entrySet();
		Iterator<Map.Entry<Integer, String[]>> iter = entrySet.iterator();
		
		while (iter.hasNext())
		{
			Map.Entry<Integer, String[]> entry = iter.next();
			
			System.out.print(entry.getKey());
			System.out.print(": ");
			System.out.println(instructionsToString(entry.getValue()));
		}
	}
	
	/**
	 * Pretty prints metrics on currently executing processes,
	 * including number of currently executing processes,
	 * unique genomes present in the core, and distribution of 
	 * current process lifetimes.
	 */
	public void prettyPrintMetrics()
	{
		System.out.println("Number of processes: " + processes.size());
		System.out.println();
		
		System.out.println("Genomes:");
		prettyPrintGenomes();
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		CPU cpu = new CPU();
		
		// Carry out ten executions
		for (int index=0; index<1000; index++)
		{
			cpu.execute();
			
			cpu.prettyPrintMetrics();
		}
	}
}
