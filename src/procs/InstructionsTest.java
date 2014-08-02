package procs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstructionsTest 
{
	private Core core;
	
	private Process process;
	
	private String[] ancestor = 
		{Instructions.NOP, Instructions.NOP, Instructions.SPW, 
		 Instructions.NOP, Instructions.NOP};

	// Address at which to place process
	private final int ADDRESS = 0;
	
	private final int CORE_SIZE = 10;
	
	@Before
	public void setUp() throws Exception 
	{
		// Create a core and add a process to it
		core = new Core(CORE_SIZE);
		process = new Process(ADDRESS, ancestor.length);
		
		core.addProcess(ancestor, ADDRESS);
	}

	@After
	public void tearDown() throws Exception 
	{
	}

	@Test
	public final void testMovePtr() 
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCopyNOP() 
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testSpawnProcess() 
	{
		int address = Instructions.spawnProcess(core, process);
		
		// Check that the spawned process is at the 
		// returned address
		if (address != -1)
		{
			for (int index=0; index<process.length(); index++)
			{
				int location = (address+index) % core.size();
				
				assertTrue("Empty location found", 
						   !core.getInstruction(location).equals(Core.EMPTY));
			}
		}
		else
		{
			fail("Invalid core address returned");
		}
	}

}
