package io.pivotal.gemfire.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.PartitionedRegion;

public class MoveDataFunction implements Function, Declarable {
	public static final String ID = "MoveDataFunction";
	private DistributedMember member;
	private int THREAD_COUNT = 30;
	Region gpdb;
	
	public MoveDataFunction() {
		this.member = CacheFactory.getAnyInstance().getDistributedSystem().getDistributedMember();
	}
	
	@Override
	public void execute(FunctionContext context) {
		System.out.println(Thread.currentThread().getName() + ": Executing " + getId());
		RegionFunctionContext rfc = (RegionFunctionContext) context;
		
		Map<String, Region<?, ?>> regions = PartitionRegionHelper.getLocalColocatedRegions(rfc); // TRACE
		gpdb = regions.get("/TRACE_GPDB");
		//gpdb = regions.get("/TRACE_STRING");
		//String res = gpdb.toString();
		PartitionedRegion pr = (PartitionedRegion) rfc.getDataSet();
		//int size = pr.getDataStore().getAllLocalPrimaryBucketIds().size();
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
		
		long count = 0;
		for (BucketRegion br: pr.getDataStore().getAllLocalBucketRegions()) {
			if (pr.getBucketPrimary(br.getId()).equals(member)) {
				Runnable worker = new WorkerThread(br);
				executor.execute(worker);
			}
			count += br.keySet().size();
		}
		
		executor.shutdown();
		while (!executor.isTerminated()) {
			System.out.println("Finished all threads...");
		}
		
		/*
		Region localRegion = PartitionRegionHelper.getLocalDataForContext(rfc); // TRACE
		int numLocalEntries = localRegion.size();
		
		// Destroy each entry
		long start = 0, end = 0;
		start = System.currentTimeMillis();
		localRegion.removeAll(localRegion.keySet());
		end = System.currentTimeMillis();
		System.out.println(Thread.currentThread().getName() + ": Cleared " + numLocalEntries + " entries in "
				+ (end - start) + " ms");
		context.getResultSender().lastResult(true);
		*/
		//context.getResultSender().lastResult(res);
		context.getResultSender().lastResult(count);
	}
	
	public class WorkerThread implements Runnable {
		private BucketRegion br;
		
		public WorkerThread(BucketRegion br) {
			this.br = br;
		}
		
		@Override
		public void run() {
			//System.out.println(Thread.currentThread());
			Set keys = br.keySet();
			Map map = br.getAll(keys);
			gpdb.putAll(map);
			br.removeAll(keys);
		}
	}
	
	@Override
	public void init(Properties properties) {
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
}
