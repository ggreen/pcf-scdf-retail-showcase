package io.pivotal.gemfire;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.Struct;

import io.pivotal.gemfire.domain.Trace;

public class GfRetriever {

	public GfRetriever() {
	}

	public static void main(String[] args) {
		String sql = "select id, name, income from /Trace";
		if (args.length > 0) {
			sql = args[0].toString();
		}
		
		ClientCacheFactory ccf = new ClientCacheFactory();
		ccf.set("cache-xml-file", "client.xml");
		ClientCache cache = ccf.create();
		//Region<String, Customer> region = cache.getRegion("Customer");

		try {
			QueryService service = cache.getQueryService();
			Query query = service.newQuery(sql);
			SelectResults results = (SelectResults) query.execute();
			//System.out.println("results: " + results.toString());

			int size = results.size();
			for (Object o : results) {
				Struct s = (Struct) o;
				Object[] fields = s.getFieldValues();
				//System.out.println(fields.length);
				
				StringBuffer sb = new StringBuffer();
				sb.append("Customer[");
				for (int i=0; i<fields.length; i++) {
					sb.append(fields[i] + ((i==fields.length-1)?"":","));
				}
				sb.append("]");
				System.out.println(sb.toString());
			}
			System.out.println("### " + size + " data retrieved in Gemfire.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}