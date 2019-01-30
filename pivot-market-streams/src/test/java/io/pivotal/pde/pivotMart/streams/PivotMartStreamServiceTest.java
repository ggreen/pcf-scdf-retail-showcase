package io.pivotal.pde.pivotMart.streams;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.BlockingQueue;

import org.apache.geode.cache.Region;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;

import gedi.solutions.geode.client.GeodeClient;
import io.pivotal.gemfire.domain.BeaconRequest;
import io.pivotal.gemfire.domain.CustomerIdentifier;
import io.pivotal.gemfire.domain.OrderDTO;
import solutions.nyla.apacheKafka.ApacheKafka;


public class PivotMartStreamServiceTest
{

	private static PivotMartStreamConf conf = new PivotMartStreamConf();
	private static PivotMartStreamService service;
	private static ApacheKafka kafka;
	private Gson gson = new Gson();
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp()
	throws Exception
	{

		service = new PivotMartStreamService();
		service.beaconProductsRegion = conf.beaconProductsRegion();
		service.customerFavoritesRegion = conf.customerFavoritesRegion();
		service.customerPromotionsRegion = conf.customerPromotionsRegion();
		service.orderQueue = mock(BlockingQueue.class);
		
		OrderDTO order = new OrderDTO();
		order.setCustomerIdentifier(new CustomerIdentifier());
		order.getCustomerIdentifier().setFirstName("nyla");
		order.getCustomerIdentifier().setLastName("nyla");
		
		Integer[] productIds = {1};
		
		order.setProductIds(productIds);
		
		String orderGson = new Gson().toJson(order);
		when(service.orderQueue.take()).thenReturn(orderGson);
		when(service.orderQueue.take()).thenReturn(orderGson);
		
		service.dao = conf.dao(conf.dataSource());
		service.boss = conf.boss();
		service.beaconRequestQueue = conf.beaconRequestQueue(ApacheKafka.connect());
		kafka = ApacheKafka.connect();
		
	}
	
	@Test
	@Ignore
	public void testFullFlow()
	{
		
		Region<String,Object> region = GeodeClient.connect().getRegion("customerPromotions");
		
		
		BeaconRequest br = new BeaconRequest();
		br.setCustomerId(new CustomerIdentifier());
		br.getCustomerId().setFirstName("John");
		br.getCustomerId().setLastName("Smith");
		br.setUuid("2");
		
		
		region.remove(br.getCustomerId()+"");
		
		service.processBeaconRequest(br);
		
		
		
		assertNotNull(region.get(String.valueOf(br.getCustomerId())));
		
	}
	
	@Test
	@Ignore
	public void testCheckBeaconRequestQueueKakfa() throws Exception
	{
		
		BeaconRequest br = new BeaconRequest();
		br.setCustomerId(new CustomerIdentifier());
		br.getCustomerId().setFirstName("John");
		br.getCustomerId().setLastName("Smith");

		br.setDeviceId("6a468e3e-631f-44e6-8620-cc83330ed994");
		br.setUuid(br.getDeviceId());
		br.setMajor(23);
		br.setMinor(1);
		
		
		String json = gson.toJson(br);
		System.out.println("json:"+json);
		
		kafka.push("beacon", br.getKey(), json);
		
		Thread.sleep(100);
		
		assertTrue(service.checkBeaconRequestQueue() > 0);
		
		Thread.sleep(5000);
		
		Region<String,Object> region = GeodeClient.connect().getRegion("customerPromotions");
		
		
		assertNotNull(region.get(String.valueOf(br.getCustomerId())));
		
	}//------------------------------------------------
	@Test
	@Ignore
	public void testOrders()
	throws Exception
	{
		
		int results = service.checkOrderQueue();
		
		assertTrue(results > 0);
		
	}//------------------------------------------------
}
