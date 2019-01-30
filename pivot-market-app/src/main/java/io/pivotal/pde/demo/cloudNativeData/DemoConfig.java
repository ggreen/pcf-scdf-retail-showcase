package io.pivotal.pde.demo.cloudNativeData;

import java.util.Collection;
import java.util.Queue;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import gedi.solutions.geode.client.GeodeClient;
import gedi.solutions.geode.lucene.GeodeLuceneSearch;
import gedi.solutions.geode.spring.security.GeodeUserDetailsService;
import gedi.solutions.geode.spring.security.SpringSecurityUserService;
import io.pivotal.gemfire.domain.CustomerFavorites;
import io.pivotal.gemfire.domain.Promotion;
import solutions.nyla.apacheKafka.ApacheKafka;

@Configuration
public class DemoConfig
{
	 //Region<Integer, Set<ProductAssociate>> productAssociationsRegion;
	 @Bean(name = "productAssociationsRegion")
	 public Region<String,PdxInstance> productAssociationsRegion(@Autowired GeodeClient geodeClient)
	 {
	         return geodeClient.getRegion("productAssociations");
	 }//------------------------------------------------

	 //Region<Integer,Product> productsRegion;
	 @Bean(name = "productsRegion")
	 public Region<String,PdxInstance> productsRegion(@Autowired GeodeClient geodeClient)
	 {
	         return geodeClient.getRegion("products");
	 }//------------------------------------------------
	@Bean
	GeodeClient getGeodeClient()
	{

		return GeodeClient.connect();
	}//------------------------------------------------
	@Bean(name = "gemfireCache")
    public ClientCache getGemfireClientCache(@Autowired GeodeClient geodeClient) throws Exception {		
		
		 return geodeClient.getClientCache();
    }//------------------------------------------------
	@Bean(name = "alerts")
	public Region<String,PdxInstance> getAlerts(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.getRegion("alerts");
	}//------------------------------------------------
	@Bean(name = "productRecommendationsRegion")
	public Region<String,PdxInstance> productRecommendationsRegion(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.getRegion("productRecommendations");
	}//------------------------------------------------
	@Bean(name = "beaconPromotionsRegion")
	public Region<String,Collection<Promotion>> beaconPromotionsRegion(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.getRegion("beaconPromotions");
	}//------------------------------------------------
	
	
	@Bean(name = "customerLocationRegion")
	public Region<String,String> customerLocationRegion(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.getRegion("customerLocation");
	}//------------------------------------------------
	
	@Bean(name = "customerPromotionsRegion")
	public Region<String,Collection<Promotion>> customerPromotions(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.getRegion("customerPromotions");
	}//------------------------------------------------	
	//
	@Bean(name = "customerFavoritesRegion")
	Region<String,Collection<CustomerFavorites>> getCustomerFavoritesRegion(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.getRegion("customerFavorites");
	}//------------------------------------------------
	
	@Bean(name = "liveAlertsQueue")
	public Queue<Collection<Promotion>> getAlertQueue(@Autowired GeodeClient geodeClient)
	{
		return geodeClient.registerCq("liveAlerts", "select * from /customerPromotions");
	}//------------------------------------------------
	/**
	 * 
	 * @param geodeClient the geode client
	 * @return the user details service
	 */
	@Bean
	public SpringSecurityUserService userDetailsService(@Autowired GeodeClient geodeClient)
	{
		return new GeodeUserDetailsService(geodeClient.getRegion("users"));
	}//------------------------------------------------
	@Bean
	ViewResolver viewResolver()
	{
		InternalResourceViewResolver i= new InternalResourceViewResolver();
		i.setViewClass(JstlView.class);
		i.setPrefix("WEB-INF/jsp/");
		i.setSuffix(".jsp");
		return i;
	}//------------------------------------------------

	@Bean
	public ApacheKafka apacheKafka()
	{
		return ApacheKafka.connect();
	}
	
	@Bean
	public GeodeLuceneSearch geodeLucentSearch()
	{
		return new GeodeLuceneSearch(GeodeClient.connect().getClientCache());
	}
	
}
