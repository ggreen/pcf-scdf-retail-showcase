package io.pivotal.market.dao;

import org.springframework.data.repository.CrudRepository;

import io.pivotal.gemfire.domain.Product;

public interface ProductRepository extends CrudRepository<Product,Integer>
{

}
