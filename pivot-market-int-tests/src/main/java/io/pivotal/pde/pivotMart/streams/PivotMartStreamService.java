package io.pivotal.pde.pivotMart.streams;

import java.util.Collection;
import java.util.Queue;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.pivotal.gemfire.domain.Beacon;
import io.pivotal.gemfire.domain.BeaconRequest;
import io.pivotal.gemfire.domain.Customer;
import io.pivotal.gemfire.domain.CustomerFavorites;
import io.pivotal.gemfire.domain.Product;
import io.pivotal.gemfire.domain.Promotion;
import io.pivotal.pde.pivotMart.streams.dao.PivotMartDAO;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import nyla.solutions.core.patterns.workthread.MemorizedQueue;

@RestController
public class PivotMartStreamService
{

	@Autowired
	PivotMartDAO dao;
	
	@Resource
	Region<String,Collection<Product>> beaconProductsRegion;
	
	@Resource
	Region<String,Collection<CustomerFavorites>> customerFavoritesRegion;
	
	@Resource
	Region<String,Collection<Promotion>> customerPromotionsRegion;
	
	@Autowired
	Queue<String> beaconRequestQueue;
	
	@Autowired
	ExecutorBoss boss;
	
	/**
	 * 
	 * @param br the beacon request
	 */
	@PostMapping("/processBeaconRequest")
	public void processBeaconRequest(@RequestBody BeaconRequest br)
	{
		System.out.println("processBeaconRequest:"+br);
		
		Beacon beacon = new Beacon();
		beacon.setUuid(br.getUuid());
		beacon.setMajor(br.getMajor());
		beacon.setMinor(br.getMinor());
		
		
		Customer customer = new Customer();
		customer.setCustomerId(br.getCustomerId());
		
		Collection<CustomerFavorites> cf = dao.selectCustomerFavorites(customer);
		this.customerFavoritesRegion.put(br.getKey(), cf);
		
		Collection<Product> products = dao.selectProductsByBeacon(beacon);
		
		if(products == null || products.isEmpty())
			return;
		
		beaconProductsRegion.put(beacon.getKey(), products);
		
		for (Product product : products)
		{
			Collection<Promotion> promotions = dao.selectPromotionsByProduct(product);
			
			customerPromotionsRegion.put(customer.getKey(),promotions);
		}
	}//------------------------------------------------

	@GetMapping("/checkKafka")
	public int checkKafka()
	{
		String msg = null;
		Gson gson = new Gson();
		
		MemorizedQueue q = new MemorizedQueue();
		int cnt = 0;
		while((msg=this.beaconRequestQueue.poll()) != null)
		{
			cnt++;
			final BeaconRequest br = gson.fromJson(msg, BeaconRequest.class);
			
			q.add(() -> 
			{
				processBeaconRequest(br);}
			);
			
		}
		
		if(cnt == 0)
			return 0;
		
		boolean background = true;
		boss.startWorking(q, background);

		return cnt;
	}

}
