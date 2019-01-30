package io.pivotal.gemfire.functions;

import org.apache.geode.cache.Cache;
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

import io.pivotal.gemfire.functions.MoveDataFunction.WorkerThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClearRegionRemoveAllFunction implements Function, Declarable {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ClearRegionRemoveAllFunction";
	private DistributedMember member;
	private int THREAD_COUNT = 30;
	
	public ClearRegionRemoveAllFunction() {
		this.member = CacheFactory.getAnyInstance().getDistributedSystem().getDistributedMember();
	}
	
	public void execute(FunctionContext context) {
		System.out.println(Thread.currentThread().getName() + ": Executing " + getId());
		RegionFunctionContext rfc = (RegionFunctionContext) context;
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
		Region localRegion = PartitionRegionHelper.getLocalDataForContext(rfc); // TRACE_GPDB
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
			br.removeAll(br.keySet());
		}
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
