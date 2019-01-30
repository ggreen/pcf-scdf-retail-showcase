package io.pivotal.gemfire.functions;

import io.pivotal.gemfire.gpdb.service.GpdbService;
import org.apache.geode.LogWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;

import java.util.Properties;


public class ExportToGpdbFunction implements Function, Declarable {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ExportToGPDBFunction";
	private transient Cache cache = CacheFactory.getAnyInstance();
	private transient LogWriter logger = CacheFactory.getAnyInstance().getDistributedSystem().getLogWriter();

	public void execute(FunctionContext context) {
		if (!(context instanceof RegionFunctionContext)) {
			throw new FunctionException(
					"This is a data aware function, and has to be called using FunctionService.onRegion.");
		}

		try {
			Region<?, ?> region = cache.getRegion("TRACE_GPDB");
			//Region<?, ?> region = cache.getRegion("TRACE_STRING");
			long numberOfResults = GpdbService.createOperation(region).exportRegion();
			/*
			RegionFunctionContext rfc = (RegionFunctionContext) context;
			Region localRegion = PartitionRegionHelper.getLocalDataForContext(rfc); // TRACE_GPDB
			long numberOfResults = GpdbService.createOperation(localRegion).exportRegion();
			*/
			//String result = "Successfully imported this many records : " + numberOfResults;
			//logger.info(result);
			//context.getResultSender().lastResult(result);
			context.getResultSender().lastResult(numberOfResults);
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			if (e.getMessage() != null) {
				sb.append(e.getMessage() + "\n");
			}
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append(ste.toString());
				sb.append("\n");
			}
			logger.error(sb.toString());
			context.getResultSender().lastResult(sb.toString());
		}
		logger.info("I am in the " + this.getClass().getName() + " function");
		context.getResultSender().lastResult("");
	}

	public String getId() {
		return getClass().getSimpleName();
	}

	public boolean optimizeForWrite() {
		return false;
	}

	public boolean isHA() {
		return true;
	}

	public boolean hasResult() {
		return true;
	}

	public void init(final Properties properties) {
	}
}
