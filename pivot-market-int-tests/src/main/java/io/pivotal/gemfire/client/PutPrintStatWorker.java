package io.pivotal.gemfire.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxSerializable;

import io.pivotal.gemfire.domain.Trace;
import io.pivotal.gemfire.domain.TraceKey;

public class PutPrintStatWorker implements Runnable {
	
	//Print every three seconds
	private final long samplePeriodSecs;
	
	
	private final Region<TraceKey,PdxSerializable> region;
	private int durationInMins;
	private final long bulkSize;
	private Map<TraceKey,Trace> map;
	private final boolean preallocateMap;
	private final PrintTimeCountsRunnable printer;

	public PutPrintStatWorker(PrintTimeCountsRunnable printer,Region<TraceKey, PdxSerializable> region, int durationInMins, long bulkSize,boolean preallocateMap,long samplePeriodSecs) {
		this.region = region;
		this.durationInMins = durationInMins;
		this.bulkSize = bulkSize;
		this.map = makeMap((int)bulkSize);
		this.preallocateMap = preallocateMap;
		this.printer = printer;
		this.samplePeriodSecs = samplePeriodSecs;
	}
	
	public void run() {
		long endTime = System.currentTimeMillis() + durationInMins * 60 * 1000;
			
		long currentTime = System.currentTimeMillis();
		long nextPrintTime = currentTime + samplePeriodSecs * 1000;
		
		ArrayList<long[]> batchTimings = new ArrayList<long[]>(1000);
		
			while ((currentTime = System.currentTimeMillis()) < endTime ) {
				
					region.putAll(makeMap((int)bulkSize));
					
					/*
					 * Alternative measuring
					 */
					PutPrintingStatsClient.globalCounter.addAndGet(bulkSize);
					
/*					long[] timeStats = { currentTime,bulkSize};
					batchTimings.add(timeStats);
					
					
					if(currentTime > nextPrintTime)
					{
						printer.putAll(batchTimings);	
						batchTimings.clear();
						nextPrintTime = currentTime + (samplePeriodSecs*1000);
					}*/
					
					
			}
		
	}
	
	public Map<TraceKey,Trace> makeMap(int bulkSize) {
		
		if(map == null)
		{
			map = new HashMap<TraceKey,Trace>(bulkSize);	
		}
		else if(preallocateMap)
		{
			return map;
		}
		else
		{
			map.clear();
		}
		
		TraceKey key;
		Trace val;
		for (int i=0; i<bulkSize; i++) {
			key = makeTraceKey(UUID.randomUUID().toString(), i);
			val = makeTrace(key, i);
			map.put(key, val);
		}
		
		return map;
	}
	
	/**
	 * TraceKey makeTraceKey(String uuid, int i)
	 * @param uuid
	 * @param i  must be unique across threads 
	 * @return
	 */
	public TraceKey makeTraceKey(String uuid, long i) {
		
		String text = new StringBuilder(uuid).append(i).toString();
		
		return new TraceKey(text, text, text, text, text, text,
				text, text, text, text, Long.valueOf(i).intValue(), text,
				(i % 2 == 0)?true:false, i);
	}
	
	public Trace makeTrace(TraceKey key, int i) {
		Trace trace = new Trace(key);
		return trace;
	}
	
	
}