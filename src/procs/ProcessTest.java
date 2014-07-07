package procs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessTest 
{
	private Process process;
	
	private final int PROCESS_LENGTH = 5;
	
	private final int PROCESS_ADDRESS = 10;
	
	@Before
	public void setUp() throws Exception 
	{
		process = new Process(PROCESS_ADDRESS, PROCESS_LENGTH);
	}

	@After
	public void tearDown() throws Exception 
	{
	}

	@Test
	public final void testNumExecutions() 
	{
		for (int index=0; index<200; index++)
		{
			process.incrementPtr();
		}
		
		assertEquals("Incorrect number of executions", 
				     process.numExecutions(), 200);
	}

	@Test
	public final void testPtr() 
	{
		for (int index=0; index<PROCESS_LENGTH+2; index++)
		{
			process.incrementPtr();
		}
		
		assertEquals("Incorrect pointer value", 
			         process.ptr(), 2);
	}
	
	@Test
	public final void testAddress() 
	{
		assertEquals("Incorrect process address",
				     PROCESS_ADDRESS, process.address());
	}

	@Test
	public final void testLength() 
	{
		assertEquals("Incorrect process length",
			         PROCESS_LENGTH, process.length());
	}

}
