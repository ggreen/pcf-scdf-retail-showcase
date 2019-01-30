package io.pivotal.gemfire;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.pdx.PdxInstance;

import io.pivotal.gemfire.domain.TraceKey;
import io.pivotal.gemfire.functions.FunctionInvoker;

public class GfLoader {
	static int BULK_COUNT = 1000;
	static int UNIT = 8;
	
	public GfLoader() {
	}
	
	public static void main(String[] args) {
		int count = 20;
		if (args.length > 0) {
			count = Integer.parseInt(args[0]);
		}
		if (count <= BULK_COUNT) {
			BULK_COUNT = count;
		}
		
		ClientCacheFactory ccf = new ClientCacheFactory();
		ccf.set("cache-xml-file", "client.xml");
		ClientCache cache = ccf.create();
		Region<TraceKey, PdxInstance> masters = cache.getRegion("MASTER");
		Region<TraceKey, PdxInstance> traces = cache.getRegion("TRACE");
		Region<?, ?> transformed = cache.getRegion("TRACE_TRANSFORMED");
		
		Map<TraceKey,PdxInstance> traceBuffer = new HashMap<TraceKey,PdxInstance>();
		Map<TraceKey,PdxInstance> masterBuffer = new HashMap<TraceKey,PdxInstance>();
		FunctionInvoker.executeClearRegionRemoveAll(transformed);
		
		if (FunctionInvoker.executeClearRegionRemoveAll(traces) && 
				FunctionInvoker.executeClearRegionRemoveAll(masters)) {
			
			String EqpIndex,UnitIndex,ParamIndex,LotId,PpId,RecipeId,StepSeq,
			PairId,ProcessId,SaferId,intValue,timestamp;
			int waferNo = 0;
			
			for (int i = 0; i < count; i++) {
				timestamp = new Timestamp(System.nanoTime()).toString();
				//int waferNo = new Random().nextInt();
				
				
				intValue  = String.valueOf(i % UNIT);
				
				EqpIndex = new StringBuilder("EqpIndex").append(intValue).toString();
				UnitIndex = new StringBuilder("UnitIndex").append(intValue).toString();
				ParamIndex = new StringBuilder("ParamIndex").append(intValue).toString() ;
				LotId = new StringBuilder("LotId").append(intValue).toString();
				PpId = new StringBuilder("PpId").append(intValue).toString();
				RecipeId = new StringBuilder("RecipeId").append(intValue).toString();
				StepSeq = new StringBuilder("StepSeq").append(intValue).toString() ;
				PairId =  new StringBuilder("PairId").append(intValue).toString() ;
				ProcessId = new StringBuilder("ProcessId").append(intValue).toString();
				SaferId = new StringBuilder("SaferId").append(intValue).toString(); 
				LotId = new StringBuilder("LotId").append(intValue).toString();
				
				TraceKey key = new TraceKey(EqpIndex, 
						UnitIndex, 
						ParamIndex, 
						LotId, 
						PpId, 
						RecipeId, 
						StepSeq, 
						PairId, 
						ProcessId, 
						SaferId, 
						waferNo, 
						LotId, 
						true, 
						Long.valueOf(i));
									
						/*cache.createPdxInstanceFactory("io.pivotal.gemfire.domain.TraceKey")
						.writeString("eqpIndex", "EqpIndex" + (i % UNIT))
						.writeString("unitIndex", "UnitIndex" + (i % UNIT))
						.writeString("paramIndex", "ParamIndex" + (i % UNIT))
						.writeString("lotId", "LotId" + (i % UNIT))
						.writeString("ppId", "PpId" + (i % UNIT))
						.writeString("recipeId", "RecipeId" + (i % UNIT))
						.writeString("stepSeq", "StepSeq" + (i % UNIT))
						.writeString("pairId", "PairId" + (i % UNIT))
						.writeString("processId", "ProcessId" + (i % UNIT))
						.writeString("waferId", "WaferId" + (i % UNIT))
						.writeInt("waferNo", waferNo)
						.writeString("lotType", "LotId" + (i % UNIT))
						.writeBoolean("statusTf", true)
						.writeLong("seq", new Long(i))
						.create();
						*/
				PdxInstance trace = cache.createPdxInstanceFactory("io.pivotal.gemfire.domain.Trace")
						.writeString("eqpIndex", "EqpIndex" + (i % UNIT))
						.writeString("unitIndex", "UnitIndex" + (i % UNIT))
						.writeString("paramIndex", "ParamIndex" + (i % UNIT))
						.writeString("lotId", "LotId" + (i % UNIT))
						.writeString("ppId", "PpId" + (i % UNIT))
						.writeString("recipeId", "RecipeId" + (i % UNIT))
						.writeString("stepSeq", "StepSeq" + (i % UNIT))
						.writeString("pairId", "PairId" + (i % UNIT))
						.writeString("processId", "ProcessId" + (i % UNIT))
						.writeString("waferId", "WaferId" + (i % UNIT))
						.writeInt("waferNo", waferNo)
						.writeString("lotType", "LotId" + (i % UNIT))
						.writeBoolean("statusTf", true)
						.writeString("ts", timestamp)
						.writeString("vl", new Integer(new Random().nextInt()).toString())
						.writeString("ls", "")
						.writeString("us", "")
						.writeString("sl", "10")
						.create();
				traceBuffer.put(key, trace);
				
				if (i < UNIT) {
					TraceKey masterKey = new TraceKey(EqpIndex, 
							UnitIndex, 
							ParamIndex, 
							LotId, 
							PpId, 
							RecipeId, 
							StepSeq, 
							PairId, 
							ProcessId, 
							SaferId, 
							waferNo, 
							LotId, 
							true, 
							Long.valueOf(0l));
							
							/*cache.createPdxInstanceFactory("io.pivotal.gemfire.domain.TraceKey")
							.writeString("eqpIndex", "EqpIndex" + (i % UNIT))
							.writeString("unitIndex", "UnitIndex" + (i % UNIT))
							.writeString("paramIndex", "ParamIndex" + (i % UNIT))
							.writeString("lotId", "LotId" + (i % UNIT))
							.writeString("ppId", "PpId" + (i % UNIT))
							.writeString("recipeId", "RecipeId" + (i % UNIT))
							.writeString("stepSeq", "StepSeq" + (i % UNIT))
							.writeString("pairId", "PairId" + (i % UNIT))
							.writeString("processId", "ProcessId" + (i % UNIT))
							.writeString("waferId", "WaferId" + (i % UNIT))
							.writeInt("waferNo", waferNo)
							.writeString("lotType", "LotId" + (i % UNIT))
							.writeBoolean("statusTf", true)
							.writeLong("seq", 0l)
							.create();*/
							
					PdxInstance master = cache.createPdxInstanceFactory("io.pivotal.gemfire.domain.Master")
							.writeString("eqpIndex", "EqpIndex" + (i % UNIT))
							.writeString("unitIndex", "UnitIndex" + (i % UNIT))
							.writeString("paramIndex", "ParamIndex" + (i % UNIT))
							.writeString("lotId", "LotId" + (i % UNIT))
							.writeString("ppId", "PpId" + (i % UNIT))
							.writeString("recipeId", "RecipeId" + (i % UNIT))
							.writeString("stepSeq", "StepSeq" + (i % UNIT))
							.writeString("pairId", "PairId" + (i % UNIT))
							.writeString("processId", "ProcessId" + (i % UNIT))
							.writeString("waferId", "WaferId" + (i % UNIT))
							.writeInt("waferNo", waferNo)
							.writeString("lotType", "LotId" + (i % UNIT))
							.writeBoolean("statusTf", true)
							.writeString("ls", "MASTER_LS")
							.writeString("us", "MASTER_US")
							.create();
					masterBuffer.put(masterKey, master);
				}
				if ((i % BULK_COUNT == BULK_COUNT-1)) {
					traces.putAll(traceBuffer);
					traceBuffer.clear();
					masters.putAll(masterBuffer);
					masterBuffer.clear();
				}
			}
			
			if (!traceBuffer.isEmpty()) {
				traces.putAll(traceBuffer);
				//System.out.println("##### buffer is not empty. region.putAll buffer.size: " + buffer.size());
			}
			if (!masterBuffer.isEmpty()) {
				masters.putAll(masterBuffer);
				//System.out.println("##### buffer is not empty. region.putAll buffer.size: " + buffer.size());
			}
			
			System.out.println("### " + count + " data loaded in Trace region.");
		} else {
			System.out.println("Some Trace data are not removed.");
		}
	}
	
	public static double randomInRange(double min, double max) {
		double range = max - min;
		double scaled = new Random().nextInt() * range;
		return (scaled + min);
	}
}
