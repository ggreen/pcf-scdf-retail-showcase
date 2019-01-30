package io.pivotal.gemfire.functions;

import java.util.Properties;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.internal.cache.PartitionedRegion;
/**
 * Function to allocate all Partitioned Region buckets for a Partitioned Region.  Assigning bucket partitions is a distributed operation 
 * for the whole Partitioned Region and all it's co-located PR's, so we make sure the invocation only results in a single server firing 
 * the function.  It is safe to invoke the Fn multiple times if the calling code receives an exception and isn't sure if the logic completed
 * successfully (after the First successful invocation, nothing happens).  Note that the first Bucket will already be assigned via the logic 
 * that routes the onFilter() key, but the net result is the same: All buckets will be assigned to specific servers once the Fn completes, 
 * and there will be equal numbers of Primary and Secondary buckets on each server (+/- 1 bucket if bucket-count cannot be evenly divided by
 * the server count).
 * <p>
 * This function may be used to initialize a ParallelDistributionPartitionedRegion by invoking on its "parent" PR.  When the buckets are 
 * assigned/allocated, PartitionListener afterBucketCreate() and afterPrimary() events will fire on the local server process for each 
 * bucket created.
 * <p>
 * Usage is:
 * <p>
 * <li> Invoke via onRegion().withFilter() Fn invocation pattern.  
 * <li> The filter should contain only One key.  The filter key is arbitrary--we use to assure that the Fn fires on only One server, and we do not 
 * 		use or access it in the body of the execute() method.
 * <li> If the target Partitioned Region has a custom Partition Resolver installed, make sure the (otherwise arbitrary) key is of a type handled 
 * 		by the resolver.
 * <p>
 * There is no return value.  However, if a problem occurs, the invoking client code will receive an Exception.  If there is no Exception, 
 * then the Fn completed successfully. 
 * <p>
 * @author glow
 *
 */
public class PRBAllocationFunction implements Function, Declarable {

	public PRBAllocationFunction() {
	}

	public String getId() {
		return getClass().getSimpleName();
	}

	public boolean optimizeForWrite() {
		return false;
	}

	public boolean hasResult() {
		return false;
	}

	public boolean isHA() {
		return false;
	}

	public void init(Properties properties) {
	}

	/**
	 * Get a handle on the invocation-target PartitionedRegion and use the PartitionRegionHelper API to allocate (AKA "assign") its 
	 * buckets to specific servers.  Any buckets already assigned will be ignored, and both Primary and Secondary bucket counts will
	 * be evenly allocated across the cluster once completed. 
	 */
	public void execute(FunctionContext context) {
		Cache c = CacheFactory.getAnyInstance();

		if (context instanceof RegionFunctionContext) {
			RegionFunctionContext rc = (RegionFunctionContext) context;
			Region targetRegion = rc.getDataSet();

			if (PartitionRegionHelper.isPartitionedRegion(targetRegion)) {
				//
				PartitionedRegion pr = (PartitionedRegion) rc.getDataSet();
				PartitionRegionHelper.assignBucketsToPartitions(pr);
				c.getLogger().config("Successfully assigned Buckets for Partitioned Region: " + pr.getName());
				return;
			} else {
				String errMsg = "Could not assigned Buckets! Target Region '" + targetRegion.getName()
						+ "' is NOT Partitioned!";
				c.getLogger().warning(errMsg);
				throw new RuntimeException(errMsg);
			}
		} else {
			String errMsg = "Could not assigned Buckets! No Target Region available (not an onRegion() Fn invocation)";
			c.getLogger().warning(errMsg);
			throw new RuntimeException(errMsg);
		}

	}
}