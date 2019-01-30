package io.pivotal.gemfire.client;

import java.util.UUID;

import org.apache.geode.cache.Region;

public class Worker implements Runnable {
	
	private Region region;
	private byte[] value = new byte[100];
	int durationInMins;
	Statistics statistics;
	
	public Worker(Region<String, byte[]> region, int durationInMins, Statistics statististics) {
		this.region = region;
		this.durationInMins = durationInMins;
		this.statistics = statistics;
	}
	
	public void run() {
		long endTime = System.currentTimeMillis() + durationInMins * 60 * 100;
		while (endTime > System.currentTimeMillis()) {
			region.create(UUID.randomUUID().toString(), value);
			statistics.incr();
		}
	}

}