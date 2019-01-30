package io.pivotal.gemfire.client;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class PrintTimeCountsRunnable implements Runnable
{

	@Override
	public void run()
	{
		long [] results = null;
		try
		{
			StringBuilder line = new StringBuilder();
			
			while(true)
			{
				 results = this.queue.take();
				 line.append("\"").append(results[0]).append("\",\"").append(results[1]).append("\",\"")
					.append("PUTALL-TIMING\"");	
				 System.out.println(line);
				 
				 line.setLength(0);
				 Thread.sleep(delay);
				 
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}//------------------------------------------------
	public synchronized void putAll(List<long[]> stats) 
	{
		if(stats == null || stats.isEmpty())
			return;
		
		for (long[] stat : stats)
		{
			try
			{
			 queue.put(stat);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}//------------------------------------------------
	/**
	 * @param e
	 * @return
	 * @throws InterruptedException 
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public synchronized void put(long timeMs,long bulkSize) 
	{
		try
		{
			long[] output =  { timeMs,bulkSize};
			 queue.put(output);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}//------------------------------------------------

	private LinkedBlockingQueue<long[]> queue = new LinkedBlockingQueue<long[]>();
	private long delay = 500;

}
