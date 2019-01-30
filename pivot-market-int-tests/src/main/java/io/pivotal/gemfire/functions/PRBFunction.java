package io.pivotal.gemfire.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.PartitionedRegion;

public class PRBFunction implements Function, Declarable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2772415645306122852L;
	private DistributedMember member;

	public PRBFunction() {
		this.member = CacheFactory.getAnyInstance().getDistributedSystem().getDistributedMember();
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

	public void execute(FunctionContext context) {
		RegionFunctionContext rfc = (RegionFunctionContext) context;
		PartitionedRegion pr = (PartitionedRegion) rfc.getDataSet();
		List<Object> primaryBucketInfo = new ArrayList<Object>();
		List<Object> redundantBucketInfo = new ArrayList<Object>();
		
		for (BucketRegion br: pr.getDataStore().getAllLocalBucketRegions()) {
			Map<Object,Object> map = new HashMap<Object,Object>();
			map.put("BucketId", br.getId());
			map.put("Size", br.size());
			map.put("Bytes", br.getTotalBytes());
			if (pr.getBucketPrimary(br.getId()).equals(this.member)) {
				if (br.keys().size() > 0) {
					map.put("keys", br.keys().toString());
				}
				primaryBucketInfo.add(map);
//			} else {
//				redundantBucketInfo.add(map);
			}
			
		}
		context.getResultSender().lastResult(new Object[] {primaryBucketInfo, redundantBucketInfo,
				pr.getPartitionAttributes().getTotalNumBuckets() });
	}
}