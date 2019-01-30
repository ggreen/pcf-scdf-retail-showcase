package io.pivotal.pde.pivotMart.streams;

import java.util.Collection;
import java.util.Queue;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.geode.cache.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import gedi.solutions.geode.client.GeodeClient;
import io.pivotal.gemfire.domain.CustomerFavorites;
import io.pivotal.gemfire.domain.Product;
import io.pivotal.gemfire.domain.Promotion;
import io.pivotal.pde.pivotMart.streams.dao.PivotMartDAO;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import nyla.solutions.core.util.Config;
import solutions.nyla.apacheKafka.ApacheKafka;

@Configuration
public class PivotMartConf
{
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
	 * @return
	 */
	
	@Bean
	public DataSource dataSource()
	{
		  // Construct BasicDataSource
		  BasicDataSource bds = new BasicDataSource();
		  bds.setDriverClassName("org.postgresql.Driver");
		  bds.setUrl("jdbc:postgresql://18.213.48.32:6432/retail");
		  bds.setUsername("retail");
		  bds.setPassword("");
		  
		  return bds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource ds)
	{
		return new JdbcTemplate(ds);
	}
	
	@Bean
	public Region<String, Collection<Product>> beaconProductsRegion()
	{
		return GeodeClient.connect().getRegion("beaconProducts");
	}

	@Bean
	public Region<String, Collection<CustomerFavorites>> customerFavoritesRegion()
	{
		return GeodeClient.connect().getRegion("customerFavorites");
	}
	
	@Bean
	public Region<String, Collection<Promotion>> customerPromotionsRegion()
	{
		return GeodeClient.connect().getRegion("customerPromotions");
	}

	@Bean
	public PivotMartDAO dao()
	{
		PivotMartDAO pivotMartDAO = new PivotMartDAO();
		pivotMartDAO.setJdbcTemplate(this.jdbcTemplate(this.dataSource()));
		
		return pivotMartDAO;
	}

	@Bean
	public ExecutorBoss boss()
	{
		return new ExecutorBoss(Config.getPropertyInteger("bossThreads",10));
	}
	@Bean
	public Queue<String> kafkaQueue()
	{
		return ApacheKafka.connect().queue("beacon");
	}
}
