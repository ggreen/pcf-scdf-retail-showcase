package io.pivotal.gemfire.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxSerializable;

import io.pivotal.gemfire.domain.Master;
import io.pivotal.gemfire.domain.Trace;
import io.pivotal.gemfire.domain.TraceKey;

public class PutOptimizedWorker implements Runnable {
	
	private final long offset;
	private Region<TraceKey,PdxSerializable> region;
	private Master value = new Master();
	private int durationInMins;
	private Statistics statististics;
	private final long bulkSize;
	private Map<TraceKey,Trace> map;
	private final boolean preallocateMap;
	
	
	public PutOptimizedWorker(Region<TraceKey, PdxSerializable> region, int durationInMins, Statistics statististics, long bulkSize,long offset,boolean preallocateMap) {
		this.region = region;
		this.durationInMins = durationInMins;
		this.statististics = statististics;
		this.bulkSize = bulkSize;
		this.offset = offset;
		this.map = makeMap((int)bulkSize);
		this.preallocateMap = preallocateMap;
	}
	
	public void run() {
		long endTime = System.currentTimeMillis() + durationInMins * 60 * 1000;
		if (region == null) {
			while (endTime > System.currentTimeMillis()) {

				statististics.incr();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
		
			
			while (System.currentTimeMillis() < endTime ) {
				
				if (bulkSize > 0) {
					region.putAll(makeMap((int)bulkSize));
					//region.putAll(makeMap(bulkSize));
					statististics.incr(bulkSize);
				} else if (bulkSize == 0) {
					region.put(this.makeTraceKey(UUID.randomUUID().toString(), (int)bulkSize), value);
					statististics.incr();
				} else { // bulkSize == -1
					region.create(this.makeTraceKey(UUID.randomUUID().toString(), (int)bulkSize), value);
					statististics.incr();
				}
			}
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
		
		i = i +offset;

		String text = new StringBuilder(uuid).append(i).toString();
		
		return new TraceKey(text, text, text, text, text, text,
				text, text, text, text, Long.valueOf(i).intValue(), uuid + i,
				(i % 2 == 0)?true:false, i);
	}
	
	public Trace makeTrace(TraceKey key, int i) {
		Trace trace = new Trace(key);
		return trace;
	}
	
	
}