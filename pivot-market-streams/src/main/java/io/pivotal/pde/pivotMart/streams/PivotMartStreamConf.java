package io.pivotal.pde.pivotMart.streams;

import java.beans.PropertyVetoException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.geode.cache.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import gedi.solutions.geode.client.GeodeClient;
import gedi.solutions.geode.io.QuerierService;
import io.pivotal.gemfire.domain.CustomerFavorites;
import io.pivotal.gemfire.domain.Product;
import io.pivotal.gemfire.domain.ProductAssociate;
import io.pivotal.gemfire.domain.Promotion;
import io.pivotal.market.dao.PivotMarketPostgreDAO;
import io.pivotal.pde.pivotMart.streams.dao.PivotMartDAO;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import nyla.solutions.core.util.Config;
import solutions.nyla.apacheKafka.ApacheKafka;

@Configuration
@EnableJpaRepositories(basePackages = "io.pivotal.market.dao")
@EnableTransactionManagement
public class PivotMartStreamConf
{
	@Bean
	public PivotMarketPostgreDAO postreDao()
	{
		
		return new PivotMarketPostgreDAO();
		
	}//------------------------------------------------
	

	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource) throws PropertyVetoException {
	    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	    vendorAdapter.setGenerateDdl(true);
	   
	    

	    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	    factory.setJpaVendorAdapter(vendorAdapter);
	    factory.setPackagesToScan("io.pivotal.gemfire.domain");
	    factory.setDataSource(dataSource);
	    factory.afterPropertiesSet();

	    return factory.getObject();
	}//------------------------------------------------
	/**
	 * String url = "jdbc:postgresql://localhost/test";
	Properties props = new Properties();
	props.setProperty("user","fred");
	props.setProperty("password","secret");
	props.setProperty("ssl","true");
	Connection conn = DriverManager.getConnection(url, props);
	String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
	Connection conn = DriverManager.getConnection(url);
	jdbc:postgresql://host:port/database
	 * @return the data source
	 */
	
	public DataSource dataSource()
	{
		return dataSource(Config.getProperty("jdbcUrl","jdbc:postgresql://18.213.48.32:6432/retail"));
	}//------------------------------------------------
	@Bean
	public DataSource dataSource(Environment env)
	{
		return dataSource(env.getRequiredProperty("jdbcUrl"));
	}//------------------------------------------------
	public DataSource dataSource(String jdbcUrl)
	{
		  // Construct BasicDataSource
		  BasicDataSource bds = new BasicDataSource();
		  bds.setDriverClassName("org.postgresql.Driver");
		  
		//"jdbc:postgresql://18.213.48.32:6432/retail"
		  bds.setUrl(jdbcUrl);
		  bds.setUsername("retail");
		  bds.setPassword("");
		  
		  return bds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource ds)
	{
		return new JdbcTemplate(ds);
	}
	
	@Bean("beaconProductsRegion")
	public Region<String, Set<Product>> beaconProductsRegion()
	{
		return GeodeClient.connect().getRegion("beaconProducts");
	}

	@Bean("customerFavoritesRegion")
	public Region<String, Set<CustomerFavorites>> customerFavoritesRegion()
	{
		return GeodeClient.connect().getRegion("customerFavorites");
	}//------------------------------------------------
	
	@Bean("beaconPromotionsRegion")
	public Region<String, Set<CustomerFavorites>> beaconPromotionsRegion()
	{
		return GeodeClient.connect().getRegion("beaconPromotions");
	}//------------------------------------------------
	@Bean("customerLocationRegion")
	Region<String,String> customerLocationRegion()
	{
		return GeodeClient.connect().getRegion("customerLocation");
	}
	
	//productsRegion
	@Bean("productsRegion")
	public Region<Integer,Product> productsRegion()
	{
		return GeodeClient.connect().getRegion("products");
	}
	
	@Bean("customerPromotionsRegion")
	public Region<String, Set<Promotion>> customerPromotionsRegion()
	{
		return GeodeClient.connect().getRegion("customerPromotions");
	}

	//Region<Integer, Set<ProductAssociate>> productAssociationsRegion;
	@Bean("productAssociationsRegion")
	public Region<Integer, Set<ProductAssociate>> productAssociationsRegion()
	{
		return GeodeClient.connect().getRegion("productAssociations");
	}
	
	@Bean
	public PivotMartDAO dao(DataSource dataSource)
	{
		PivotMartDAO pivotMartDAO = new PivotMartDAO();
		pivotMartDAO.setJdbcTemplate(this.jdbcTemplate(dataSource));
		
		return pivotMartDAO;
	}//------------------------------------------------
	//checkOrderQueue

	@Bean
	public Thread checkOrderQueueThread(PivotMartStreamService service,Environment e)
	{
		Thread t = new Thread(
		() ->{
			while(true)
			{
				try
				{
					Thread.sleep(e.getProperty("checkKafkaSleepMs",Long.class));
					service.checkOrderQueue();
				}
				catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch(Exception  exp)
				{
					exp.printStackTrace();
				}
			}
			
		});
		
		t.start();
		return t;
	}//------------------------------------------------
	@Bean
	public Thread checkBeaconRequestThread(PivotMartStreamService service,Environment e)
	{
		Thread t = new Thread(
		() ->{
			while(true)
			{
				try
				{
					Thread.sleep(e.getProperty("checkKafkaSleepMs",Long.class));
					service.checkBeaconRequestQueue();
				}
				catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch(Exception  exp)
				{
					exp.printStackTrace();
				}
			}
			
		});
		
		t.start();
		return t;
	}//------------------------------------------------

	@Bean
	public ExecutorBoss boss()
	{
		return new ExecutorBoss(Config.getPropertyInteger("bossThreads",10));
	}
	@Bean
	public ApacheKafka apacheKafka()
	{
		return ApacheKafka.connect();
	}
	@Bean("beaconRequestQueue")
	public BlockingQueue<String> beaconRequestQueue(ApacheKafka apacheKafka)
	{
		return apacheKafka.queue("beacon");
	}
	
	@Bean("orderQueue")
	public BlockingQueue<String> orderQueue(ApacheKafka apacheKafka)
	{
		return apacheKafka.queue("orders");
	}//------------------------------------------------
	
	
	@Bean
	QuerierService querierService()
	{
		return GeodeClient.connect().getQuerierService();
	}
}
