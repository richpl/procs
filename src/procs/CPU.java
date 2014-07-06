package procs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
	private Map<byte[], String[]> genomes;
	
	// Map to hold details of the range of lifetimes of 
	// currently executing Processes. Keys are lifetimes and values
	// are number of processes with that lifetime.
	private Map<byte[], Integer> lifetimes;
	
	// Core in which to execute Processes
	private Core core;
	
	// The digest algorithm to be used to hash lists of 
	// instructions
	private MessageDigest md;
	
	// Ancestral process with which to initially 
	// innoculate the Core
	private String[] ancestor = 
		{Process.NOP, Process.NOP, Process.SPW, 
		 Process.NOP, Process.NOP};
	
	/**
	 * Initialises the system, innoculating the Core
	 * with a predefined ancestor Process.
	 */
	public CPU()
	{
		processList = new Vector<Process>();
		
		genomes = new HashMap<byte[], String[]>();
		
		lifetimes = new HashMap<byte[], Integer>();
		
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
			Random random = new Random();
			int address = random.nextInt(CORE_SIZE);
		
			// Add the process at that location
			core.addProcess(ancestor, address);
		
			// Create a corresponding Process and add it to the list
			// of current processes
			Process process = new Process(address, ancestor.length);
			processList.add(process);
		
			// Register in the table of unique processes and process 
			// lifetimes
			byte[] procDigest = digest(ancestor);
		
			genomes.put(procDigest, ancestor);
			lifetimes.put(procDigest, 0);
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
	 * Generates a digest for the specified list
	 * of instructions.
	 * 
	 * @param instructions The list of instructions
	 * 
	 * @return The digest generated from the instructions
	 */
	private byte[] digest(final String[] instructions)
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
		
		return (digest);
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
		processList.remove(process);		
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
						killProcess(process);
						
						break;
						
					case Process.NOP:
						// Do nothing
						
						break;
					
					case Process.JMP:
						
						break;
						
					case Process.SPW:
						
						break;
						
					case Process.CPN:
						
						break;
						
					default:
						// Something is screwy here,
						// kill the process
						killProcess(process);
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
				// Kill the process
				killProcess(process); // *** Can i remove within loop? ***
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
	
	public static void main(String[] args)
	{
		//TODO
	}
}
