package io.pivotal.pde.demo.cloudNativeData;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gedi.solutions.geode.client.GeodeClient;
import io.pivotal.gemfire.domain.Product;

@Ignore
public class ProductShoppingServiceTest
{

	static ProductShoppingService service;
	
	@BeforeClass
	public static void setUp()
	{
		GeodeClient client = GeodeClient.connect();
		
		
		service = new ProductShoppingService();
	}
	
	@Test
	public void testSearch()
	throws Exception
	{
		
		String text = "fruit";
		
		Product product = new  Product();
		product.setProductName("fruit");
		product.setCost(BigDecimal.valueOf(20.32));
		product.setPrice(BigDecimal.valueOf(23.232));
		product.setProductId(-1);
		product.setSubCategoryId("Apples");
		
		//service.storeProduct(product);
		
		Collection<Product> collection = service.searchProducts(text);
		assertNotNull(collection);
		
		assertTrue(!collection.isEmpty());
		
		assertTrue(collection.stream()
					.anyMatch(p -> (p.getProductName() != null) 
						? 	p.getProductName().contains("fruit") 
							: false));
	}

}
