package io.pivotal.gemfire.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.pdx.PdxSerializable;

import io.pivotal.gemfire.domain.TraceKey;
import io.pivotal.gemfire.functions.FunctionInvoker;

/**
 * Usage java PutPrintingStatsClient 
 * egionName, bulkCount threadCount clientXml booleanPreallocate booleanRemoveAll
 * @author Gregory Green
 *
 */
public class PutPrintingStatsClient
{
	static boolean isTest = false;
	
	public static AtomicLong globalCounter = new AtomicLong ( );

	public PutPrintingStatsClient()
	{
	}

	public static void main(String[] args) throws InterruptedException
	{
		String regionName = null;
		long bulkCount = -1;
		long samplePeriodSecs = 10;
		int threadCount = 1;
		String clientXml = "client.xml";
		int durationInMins = 60;
		boolean preAllocateMap = false;
		boolean removeAll = true;
		String clientName = "ClientWorker";

		if (args.length == 8)
		{
			try
			{
				regionName = args[0];
				bulkCount = Long.parseLong(args[1]);
				threadCount = Integer.parseInt(args[2]);
				clientXml = args[3];
				preAllocateMap = Boolean.parseBoolean(args[4]);
				removeAll = Boolean.parseBoolean(args[5]);
				samplePeriodSecs = Long.parseLong(args[6]);
				clientName = args[7];
			}
			catch (NumberFormatException e)
			{
				System.err.println("bulkCount & threadCount & samplePeriodSecs must be an integer.");
				System.exit(1);
			}
		}
		else
		{
			System.err.println("Expected arguments: regionName, bulkCount threadCount clientXml booleanPreallocate booleanRemoveAll samplePeriodSecs clientName");
			System.exit(1);
		}

		ClientCache cache = new ClientCacheFactory().set("name", clientName).set("cache-xml-file", clientXml)
				.create();
		Region<TraceKey, PdxSerializable> region = cache.getRegion(regionName);

		if (region == null)
		{
			throw new RuntimeException("region:" + regionName + " not found");
		}
		
		if(removeAll)
		{
			FunctionInvoker.executeClearRegionRemoveAll(region);
			Set<TraceKey> keys = region.keySetOnServer();
			if(keys != null && !keys.isEmpty())
			{
				throw new RuntimeException("keys not removed from region "+regionName);
			}
		}

		List<Thread> threads = new ArrayList<Thread>(threadCount);
		PrintTimeCountsRunnable printer = new PrintTimeCountsRunnable();
		
		for (int i = 0; i < threadCount; i++)
		{
			threads.add(new Thread(
					new PutPrintStatWorker(printer,region, durationInMins, bulkCount, preAllocateMap,samplePeriodSecs)));
		}
		
		long start = System.currentTimeMillis();
		
		for (Thread thread : threads)
		{
			thread.start();
		}
		
		
		//run on current thread
		//printer.run();

		for (Thread thread : threads)
		{
			thread.join();
		}
		
		long periodEnd = 0;
		long elapsed = 0;
		long rate;
		long lastGlobalCounter=0;

		while ( true ) {
			periodEnd = System.currentTimeMillis();
			
			elapsed = ( periodEnd - start ) * 1000;
			
			if ( elapsed >= samplePeriodSecs ) { 
				long currentCounterVal = globalCounter.get();
				rate = ( currentCounterVal - lastGlobalCounter ) / elapsed;
				System.out.println("Next measured rate is: " + rate );
				lastGlobalCounter = currentCounterVal;
				start = periodEnd;
			}
			
			Thread.sleep(2);
			
			
		}

	}
}