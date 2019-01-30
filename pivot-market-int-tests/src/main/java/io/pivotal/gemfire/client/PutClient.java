package io.pivotal.gemfire.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.pdx.PdxSerializable;

import io.pivotal.gemfire.domain.TraceKey;
import io.pivotal.gemfire.functions.FunctionInvoker;

public class PutClient
{
	static boolean isTest = false;

	public PutClient()
	{
	}

	public static void main(String[] args) throws InterruptedException
	{
		String regionName = "EQUIPMENT";
		long bulkCount = -1;
		int threadCount = 1;
		String clientXml = "client.xml";
		int durationInMins = 60;
		boolean preAllocateMap = false;
		boolean removeAll = true;

		if (args.length == 6)
		{
			try
			{
				regionName = args[0];
				bulkCount = Long.parseLong(args[1]);
				threadCount = Integer.parseInt(args[2]);
				clientXml = args[3];
				preAllocateMap = Boolean.valueOf(args[4]).booleanValue();
				removeAll = Boolean.valueOf(args[5]).booleanValue();
			}
			catch (NumberFormatException e)
			{
				System.err.println("bulkCount & threadCount must be an integer.");
				System.exit(1);
			}
		}
		else
		{
			System.err.println("Expected arguments: regionName, bulkCount threadCount clientXml booleanPreallocate booleanRemoveAll");
			System.exit(1);
		}

		ClientCache cache = new ClientCacheFactory().set("name", "ClientWorker").set("cache-xml-file", clientXml)
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

		Statistics statistics = new Statistics(durationInMins);
		List<Thread> threads = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++)
		{
			threads.add(new Thread(
					new PutOptimizedWorker(region, durationInMins, statistics, bulkCount, ((i) * bulkCount),preAllocateMap)));
		}

		for (Thread thread : threads)
		{
			thread.start();
		}

		for (Thread thread : threads)
		{
			thread.join();
		}

	}
}