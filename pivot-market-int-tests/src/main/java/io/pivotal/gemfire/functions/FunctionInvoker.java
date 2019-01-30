package io.pivotal.gemfire.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;

public class FunctionInvoker {
	//ClientCache cache;
	
	public FunctionInvoker() {
	}

	public static void exportToGpdb(Region<?, ?> region) {
		ResultCollector<?, ?> rc = FunctionService.onRegion(region).execute("ExportToGpdbFunction");
		Object result = rc.getResult();

		System.out.println(result.toString());
	}
	
	public static boolean executeClearRegionRemoveAll(Region<?, ?> region) {
		ResultCollector<?, ?> rc = FunctionService.onRegion(region).execute("ClearRegionRemoveAllFunction");
		Object result = rc.getResult();
		// Array size seems to be server node count.
		//System.out.println("executeClearRegionRemoveAll.result: " + result);
		String str[] =result.toString().split(",");
		for (int i=0; i<str.length; i++) {
			if ("false".equals(str[i])) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void executeMakeArray(Region<?, ?> region) {
		ResultCollector<?, ?> rc = FunctionService.onRegion(region).execute("MakeArrayFunction");
		Object result = rc.getResult();
		// Array size seems to be node count.
		System.out.println("executeMakeArray.result: " + result);
	}
	
	public static void executeGetMasterRegionData(Region<?, ?> region) {
		ResultCollector<?, ?> rc = FunctionService.onRegion(region).execute("GetMasterRegionDataFunction");
		Object result = rc.getResult();
		// Array size seems to be node count.
		System.out.println("executeGetMasterRegionData.result: " + result);
	}
}
