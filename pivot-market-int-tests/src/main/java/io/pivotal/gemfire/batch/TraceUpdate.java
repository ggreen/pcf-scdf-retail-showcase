package io.pivotal.gemfire.batch;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import io.pivotal.gemfire.functions.FunctionInvoker;

public class TraceUpdate {
	private ClientCache cache;
	private Region<?, ?> traces;
	private Region<?, ?> masters;
	private Region<?, ?> transformed;
	
	public TraceUpdate() {
	}

	public static void main(String[] args) {
		TraceUpdate update = new TraceUpdate();
		update.getCache();
		update.getRegions();
		update.printInfo();
		update.closeCache();
	}
	
	public void closeCache() {
		cache.close();
	}

	public void getCache() {
		this.cache = new ClientCacheFactory().set("name", "ClientWorker").set("cache-xml-file", "client.xml")
				.create();
	}

	public void getRegions() {
		masters = cache.getRegion("MASTER");
		System.out.println("Got the MASTER Region: " + masters);
		traces = cache.getRegion("TRACE");
		System.out.println("Got the TRACE Region: " + traces);
		transformed = cache.getRegion("TRACE_TRANSFORMED");
		System.out.println("Got the TRACE_TRANSFORMED Region: " + transformed);
	}
	
	public void printInfo() {
		FunctionInvoker.executeMakeArray(traces);
		FunctionInvoker.executeGetMasterRegionData(traces);
	}
}
