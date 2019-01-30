package io.pivotal.gemfire.functions;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;

import io.pivotal.gemfire.domain.TraceComparator;
import io.pivotal.gemfire.domain.TraceKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class MakeArrayFunction implements Function, Declarable {
	private static final long serialVersionUID = 1L;
	public static final String ID = "MakeArrayFunction";
	
	public void execute(FunctionContext context) {
		System.out.println(Thread.currentThread().getName() + ": Executing " + getId());
		RegionFunctionContext rfc = (RegionFunctionContext) context;
		Region<TraceKey,PdxInstance> localRegion = PartitionRegionHelper.getLocalDataForContext(rfc); // TRACE
		
		Map<String, Region<TraceKey, PdxInstance>> regions = (Map)PartitionRegionHelper.getLocalColocatedRegions(rfc);
		
		//Map buffer = new HashMap();
		Region<TraceKey,PdxInstance> transformeds = regions.get("/TRACE_TRANSFORMED");
		
		String res = "";
		//res = localRegion.keySet().toString();
		
		// Index Map
		Map<TraceKey, Set<TraceKey>> index = new HashMap<TraceKey, Set<TraceKey>>();
		//res = localRegion.keySet().toString();
		Iterator<TraceKey> iter = localRegion.keySet().iterator();
		while (iter.hasNext()) {
			TraceKey traceKey = new TraceKey(iter.next());
			/*
			WritablePdxInstance indexKey = traceKey.createWriter();
			indexKey.setField("seq", 0l);
			TODO: why???*/
			traceKey.setSeq(0);
			
			Set<TraceKey> set = index.get(traceKey);
			
			if (set == null) {
				set = new HashSet<TraceKey>();
			}
			set.add(traceKey);
			index.put(traceKey, set);
			//res += "|" + new Integer(set.size()).toString();
		}
		//res = new Integer(index.size()).toString();
		
		TraceComparator<TraceKey> comparator = new TraceComparator<TraceKey>();
		Iterator<TraceKey> it = index.keySet().iterator();
		while (it.hasNext()) {
			TraceKey indexKey = it.next();
			Set<TraceKey> keys = index.get(indexKey);
			Map<TraceKey, PdxInstance> traces = comparator.sort(localRegion.getAll(keys)); // ts, vl, sl
			PdxInstance value = squeeze(traces.entrySet());
			transformeds.put(indexKey, value);
		}
		context.getResultSender().lastResult(res);
	}
	
	public PdxInstance squeeze(Set<Entry<TraceKey, PdxInstance>> set) {
		WritablePdxInstance pdxI = null;
		
		if(set == null)
			return null;
		
		//Iterator<Entry<TraceKey, PdxInstance>> iter = set.iterator();
		for (Entry<TraceKey, PdxInstance> entry : set) {
			PdxInstance value = entry.getValue();
			if(value == null )
				continue;
			
			if (pdxI == null) {
				pdxI = value.createWriter();
				pdxI.setField("eqpIndex", value.getField("eqpIndex"));
				pdxI.setField("unitIndex", value.getField("unitIndex"));
				pdxI.setField("paramIndex", value.getField("paramIndex"));
				pdxI.setField("lotId", value.getField("lotId"));
				pdxI.setField("ppId", value.getField("ppId"));
				pdxI.setField("recipeId", value.getField("recipeId"));
				pdxI.setField("stepSeq", value.getField("stepSeq"));
				pdxI.setField("pairId", value.getField("pairId"));
				pdxI.setField("processId", value.getField("processId"));
				pdxI.setField("waferId", value.getField("waferId"));
				pdxI.setField("waferNo", value.getField("waferNo"));
				pdxI.setField("lotType", value.getField("lotType"));
				pdxI.setField("statusTf", value.getField("statusTf"));
				pdxI.setField("ts", value.getField("ts"));
				pdxI.setField("vl", value.getField("vl"));
				pdxI.setField("ls", value.getField("ls"));
				pdxI.setField("us", value.getField("us"));
				pdxI.setField("sl", String.valueOf(value.getField("sl")));
			} else {
				pdxI.setField("ts", String.valueOf(pdxI.getField("ts")) + "," + String.valueOf(value.getField("ts")));
				pdxI.setField("vl", String.valueOf(pdxI.getField("vl")) + "," + String.valueOf(value.getField("vl")));
				pdxI.setField("sl", String.valueOf(pdxI.getField("sl")) + "," + String.valueOf(value.getField("sl")));
			}
		}
		
		return pdxI;
	}

	public String getId() {
		return getClass().getSimpleName();
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
}
