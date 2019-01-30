package io.pivotal.gemfire;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;

import io.pivotal.gemfire.functions.PRBResultCollector;

public class PRBFunctionExecutor {
	private ClientCache cache;
	private Region<?, ?> traces;
	private Region<?, ?> masters;
	private Region<?, ?> transformed;

	public static void main(String[] args) {
		PRBFunctionExecutor executor = new PRBFunctionExecutor();
		executor.getCache();
		executor.getRegions();
		executor.printBuckets();
		executor.closeCache();
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
	
	public void printBuckets() {
		System.out.println("\nTrace buckets");
		executePRB(traces);
		System.out.println("\nMaster buckets");
		executePRB(masters);
		System.out.println("\nTransformed buckets");
		executePRB(transformed);
	}

	public void executePRB(Region r) {
		Execution execution = FunctionService.onRegion(r).withCollector(new PRBResultCollector());
		ResultCollector collector = execution.execute("PRBFunction");
		String result = (String) collector.getResult();
		System.out.println(result);
	}

}