package io.pivotal.gemfire.client;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;


public class TraceUpdate {
	private ClientCache cache;
	private Region<?, ?> region;
	
	String clientXml = "client.xml";
	String regionName = "TRACE";
	String gpdbTable = "TRACE_GPDB";

	public TraceUpdate(String clientXml, String regionName, String gpdbTable) {
		this.clientXml = clientXml;
		this.regionName = regionName;
		this.gpdbTable = gpdbTable;
	}

	public static void main(String[] args) {
		TraceUpdate update = null;
		if (args.length == 3) {
			update = new TraceUpdate(args[0], args[1], args[2]);
		} else {
			System.err.println("# of arguments must be 3.");
			System.exit(1);
		}
		
		update.execute();
	}
	
	public void execute() {
		
		cache = new ClientCacheFactory().set("name", "ClientWorker").set("cache-xml-file", clientXml).create();
		region = cache.getRegion(regionName);
		System.out.println("Got the TRACE Region: " + region);
		
		long start = System.currentTimeMillis();
		ResultCollector<?, ?> rc = FunctionService.onRegion(region).execute("MoveDataFunction");
		Object result = rc.getResult();
		long end = System.currentTimeMillis();
		System.out.println("1. executeMoveDataFunction.execution time: " + (end - start)/1000 + " sec.");
		System.out.println("    result: " + result);
		
		end = start;
		region = cache.getRegion(gpdbTable);
		rc = FunctionService.onRegion(region).execute("ExportToGpdbFunction");
		result = rc.getResult();
		end = System.currentTimeMillis();
		System.out.println("2. executeExportToGpdbFunction: " + (end - start)/1000 + " sec.");
		System.out.println("    result: " + result);
		
		end = start;
		rc = FunctionService.onRegion(region).execute("ClearRegionRemoveAllFunction");
		result = rc.getResult();
		end = System.currentTimeMillis();
		System.out.println("3. executeClearRegionRemoveAllFunction.execution time: " + (end - start)/1000 + " sec.");
		System.out.println("    result: " + result);
		
		cache.close(); 
	}

}
