package io.pivotal.gemfire.functions;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;

import io.pivotal.gemfire.domain.RoutingKey;
import io.pivotal.gemfire.domain.TraceKey;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class GetMasterRegionDataFunction implements Function, Declarable {
	private static final long serialVersionUID = 1L;
	public static final String ID = "GetMasterRegionDataFunction";
	
	// TRACE --> TRACE
	public void execute(FunctionContext context) {
		System.out.println(Thread.currentThread().getName() + ": Executing " + getId());
		RegionFunctionContext rfc = (RegionFunctionContext) context;
		Region<TraceKey, PdxInstance> localRegion = PartitionRegionHelper.getLocalDataForContext(rfc); // TRACE
		
		Map<String, Region<TraceKey, PdxInstance>> regions = (Map)PartitionRegionHelper.getLocalColocatedRegions(rfc);
		Region<TraceKey, PdxInstance> masters = regions.get("/MASTER");
		Region<TraceKey, PdxInstance> transformed = regions.get("/TRACE_TRANSFORMED");
		Iterator<TraceKey> iter = transformed.keySet().iterator();
		
		String res = "";
		//res = regions.keySet().toString();
		//res = localRegion.keySet().toString();
		
		while (iter.hasNext()) {
			TraceKey traceKey = iter.next();
			PdxInstance trace = (PdxInstance) transformed.get(traceKey);
			
			if (trace != null) {
				TraceKey key = new TraceKey(traceKey);
				
				/*WritablePdxInstance key = traceKey.createWriter();
				key.setField("seq", 0l); //TODO: ? why
				*/
				key.setSeq(01); //TODO: why
				
				PdxInstance master = (PdxInstance) masters.get(key);
				if (master != null) {
					String ls = master.getField("ls").toString();
					String us = master.getField("us").toString();
					WritablePdxInstance value = trace.createWriter();
					value.setField("ls", ls);
					value.setField("us", us);
					transformed.remove(key); //TODO; why remove and put back
					transformed.put(key, value);
				}
			}
		}
		context.getResultSender().lastResult(res);
	}

	public String getId() {
		return name;
	}
	public boolean optimizeForWrite() {
		return true;
	}

	public boolean hasResult() {
		return true;
	}

	public boolean isHA() {
		return true;
	}

	public void init(Properties properties) {
	}
	
	private static final String name = GetMasterRegionDataFunction.class.getSimpleName();
}
