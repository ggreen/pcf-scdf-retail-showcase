package io.pivotal.pde.demo.cloudNativeData.customer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import org.apache.geode.cache.Region;
import org.junit.Before;
import org.junit.Test;
import io.pivotal.gemfire.domain.Promotion;
import io.pivotal.pde.demo.cloudNativeData.customer.CustomerMgr;

public class CustomerMgrTest
{
	private CustomerMgr mgr = new CustomerMgr();
	
	@Before
	@SuppressWarnings("unchecked")
	public void setup()
	{
		String expectedBeacon = "beaconId";
		
		mgr.beaconPromotionsRegion = mock(Region.class);
		mgr.customerLocationRegion = mock(Region.class);

		Collection<Promotion> expectedPromotions = Collections.singleton(new Promotion());
		when(mgr.customerLocationRegion.get("imani")).thenReturn(expectedBeacon);
		when(mgr.beaconPromotionsRegion.get(expectedBeacon)).thenReturn(expectedPromotions);

	}

	
	@Test
	public void testGetCustomerLocation()
	{
				
		String userName = "imani";
		
		String beacon = mgr.whereIsCustomer(userName);
		assertNotNull(beacon);
		
		Collection<Promotion> promotions =  mgr.whatArePromotions(beacon);
		
		assertNotNull(promotions);
		
	}//------------------------------------------------
	@Test
	public void testByPromotionsByUser()
	{
		   assertNull(this.mgr.findPromotions("ggreen"));
		   
		   assertNotNull(this.mgr.findPromotions("imani"));
	}//------------------------------------------------
	
	
	@Test
	public void testSaveCustomerAtBeaconId()
	{
		String name = "ggreen";
		String beaconId = "1212";
		
		this.mgr.saveCustomerAtBeaconId(name,beaconId);
	
	}

}
