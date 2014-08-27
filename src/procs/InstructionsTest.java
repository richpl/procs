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
	
	// NOP bomb range
	private final int RANGE = 4;
	
	@Before
	public void setUp() throws Exception 
	{
		// Create a core and add a process to it
		core = new Core(CORE_SIZE, 0);
		process = new Process(ADDRESS, ancestor.length);
		
		core.addProcess(ancestor, ADDRESS, process);
	}

	@After
	public void tearDown() throws Exception 
	{
	}

	@Test
	public final void testMovePtr() 
	{		
		// Test a jump forward without wrap around
		Instructions.movePtr(process, "JMP 4");
		assertTrue("Invalid instruction pointer value",
				   process.ptr() == 3);
		
		// Test a jump forward with wrap around
		Instructions.movePtr(process, "JMP 4");
		assertTrue("Invalid instruction pointer value",
				   process.ptr() == 1);
		
		// Test a jump backward without wrap around
		Instructions.movePtr(process, "JMP -1");
		assertTrue("Invalid instruction pointer value",
				   process.ptr() == 0);
		
		// Test a jump backward with wrap around
		Instructions.movePtr(process, "JMP -4");
		assertTrue("Invalid instruction pointer value",
				   process.ptr() == 1);
	}

	@Test
	public final void testCopyNOP() 
	{
		// Fill remainder of core with other instructions
		// so that NOP bomb will work
		for (int index=process.address()+process.length();
			 index<core.size(); index++)
		{
			core.setInstruction(Instructions.CPN, index);
		}
		
		Instructions.copyNOP(core, process, RANGE);
		
		// One of the CPN instructions should have been
		// replaced by a NOP
		boolean foundNOP = false;
		
		for (int index=process.address()+process.length();
				 index<core.size(); index++)
		{
				if (core.getInstruction(index).equals(Instructions.NOP))
				{
					foundNOP = true;
				}
		}
		
		assertTrue("NOP bomb not launched", foundNOP);
	}

	@Test
	public final void testSpawnProcess() 
	{
		int address = Instructions.spawnProcess(core, process);
		
		// Check that the spawned process is at the 
		// returned address, if space was found
		if (address != -1)
		{
			for (int index=0; index<process.length(); index++)
			{
				int location = (address+index) % core.size();
				
				assertTrue("Empty location found", 
						   !core.getInstruction(location).equals(Core.EMPTY));
			}
		}
	}

}
