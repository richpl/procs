package procs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoreTest 
{
	// Test rocess with which to  
	// innoculate the Core
	private String[] ancestor = 
		{Process.NOP, Process.NOP, Process.SPW, 
		 Process.NOP, Process.NOP};
	
	private Core core;
	
	private Process process;
	
	private final int CORE_SIZE = 10;
	
	// Address at which to place process
	private final int ADDRESS = 8;
	
	@Before
	public void setUp() throws Exception 
	{
		core = new Core(CORE_SIZE);
		process = new Process(ADDRESS, ancestor.length);
		
		core.addProcess(ancestor, ADDRESS);
	}

	@Test
	public final void testSize() 
	{
		assertEquals("Core size incorrect", core.size(), CORE_SIZE);
	}

	@Test
	public final void testAddProcess() 
	{	
		assertEquals("Incorrect instruction at address 0",
				     core.getInstruction(0), Process.SPW);
		assertEquals("Incorrect instruction at address 1",
				     core.getInstruction(1), Process.NOP);
		assertEquals("Incorrect instruction at address 2",
				     core.getInstruction(2), Process.NOP);
		assertEquals("Incorrect instruction at address 3",
				     core.getInstruction(3), Core.EMPTY);
		assertEquals("Incorrect instruction at address 4",
				     core.getInstruction(4), Core.EMPTY);
		assertEquals("Incorrect instruction at address 5",
				     core.getInstruction(5), Core.EMPTY);
		assertEquals("Incorrect instruction at address 6",
				     core.getInstruction(6), Core.EMPTY);
		assertEquals("Incorrect instruction at address 7",
				     core.getInstruction(7), Core.EMPTY);
		assertEquals("Incorrect instruction at address 8",
				     core.getInstruction(8), Process.NOP);
		assertEquals("Incorrect instruction at address 9",
				     core.getInstruction(9), Process.NOP);
	}

	@Test
	public final void testGetInstructions() 
	{
		String[] instructions = core.getInstructions(process);
		
		for (int index=0; index<instructions.length; index++)
		{
			assertEquals("Wrong instruction returned",
					     instructions[index], ancestor[index]);
		}
	}

	@Test
	public final void testRemoveProcess() 
	{
		core.removeProcess(process);
		
		for (int index=0; index<CORE_SIZE; index++)
		{
			assertEquals("Non-empty core address",
					     core.getInstruction(index), Core.EMPTY);
		}
	}

	@Test
	public final void testGetInstruction() 
	{
		for (int index=0; index<ancestor.length; index++)
		{
			int location = (index+ADDRESS) % CORE_SIZE;
			String instruction = core.getInstruction(location);
			
			assertEquals("Wrong instruction returned",
					     ancestor[index], instruction);
		}
	}

}