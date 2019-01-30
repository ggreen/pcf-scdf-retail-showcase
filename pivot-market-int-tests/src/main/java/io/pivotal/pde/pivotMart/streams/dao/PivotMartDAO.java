package io.pivotal.pde.pivotMart.streams.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.pivotal.gemfire.domain.Beacon;
import io.pivotal.gemfire.domain.Customer;
import io.pivotal.gemfire.domain.CustomerFavorites;
import io.pivotal.gemfire.domain.Product;
import io.pivotal.gemfire.domain.ProductQuantity;
import io.pivotal.gemfire.domain.Promotion;

public class PivotMartDAO
{
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}
	public Collection<CustomerFavorites> selectCustomerFavorites(Customer customer)
	{
		String sql = "select * from pivotalmarkets.customer_favorites where customerid = ?";
		
		RowMapper<CustomerFavorites> rm = (rs,rowNum) -> 
		{ 
			CustomerFavorites cp = new CustomerFavorites();
			cp.setCustomerId(customer.getCustomerId());
			
			Collection<ProductQuantity> productQuatities = new ArrayList<>();
			
			while(rs.next())
			{
				ProductQuantity productQuantity = new ProductQuantity();
				Product product = new Product();
				product.setProductName(rs.getString("productname"));
				productQuantity.setProduct(product);
				productQuantity.setQuantity(rs.getInt("count"));
				productQuatities.add(productQuantity);
			}
		
			return cp; 
		};
		Integer[] args = {customer.getCustomerId()};
		
		return  jdbcTemplate.query(sql,args,rm);
	}//------------------------------------------------
	public Collection<Product> selectProductsByBeacon(Beacon beacon)
	{
	
		String sql = "select distinct c.categoryid, c.categoryname,c.subcategoryname,p.productid,p.productname, p.unit,p.cost,p.price \n" + 
		"from pivotalmarkets.beacon b, \n" + 
		"pivotalmarkets.category c,\n" + 
		"pivotalmarkets.product p\n" + 
		"where b.category = c.categoryname \n" + 
		"and \n" + 
		"( p.categoryid = c.categoryid or\n" + 
		"  p.subcategoryid = c.categoryid)\n" + 
		"  and b.uuid = ? and b.major = ? and b.minor = ?";
		
		RowMapper<Product> rm = (rs,rowNbr) -> 
		{
			Product p = new Product();
			p.setProductName(rs.getString("productname"));
			p.setPrice(rs.getBigDecimal("price"));
			p.setProductId(rs.getInt("productid"));
			p.setUnit(rs.getBigDecimal("unit"));
			return p;
		};
		
		Object[] args  = { beacon.getUuid(), beacon.getMajor(), beacon.getMinor()};
		return  jdbcTemplate.query(sql,args,rm);
	}//------------------------------------------------
	public Collection<Promotion> selectPromotionsByProduct(Product product)
	{
		if(product == null)
			return null;
		
		/*
		 * startdate
			enddate
			marketingmessage
			marketingimageurl

		 */
		String sql  = "select * from pivotalmarkets.promotion where productid = ?";
		RowMapper<Promotion> rm = (rs,rowNbr) -> 
		{ 
			Promotion p = new Promotion();
			p.setPromotionId(rs.getInt("promotionid"));
			p.setStartDate(new Date(rs.getDate("startdate").getTime()));
			p.setEndDate(new Date(rs.getDate("enddate").getTime()));
			p.setMarketingMessage(rs.getString("marketingmessage"));
			p.setMarketingUrl(rs.getString("marketingimageurl"));
			p.setProductId(rs.getInt("productid"));
			return p;
		};
		
		Object[] args  = {product.getProductId()};
		return  jdbcTemplate.query(sql,args,rm);
	}

}
