# Overview

This module is a Apache Kafka based streaming application.
It was developed to demo Cloud Native Data capabilities based
on Spring, GemFire/Apache Geode, Greenplum and Messaging Driven Architecture.

## GemFire/Apache Geode Setup 

	create region --name=beaconProducts --type=PARTITION
	create region --name=customerFavorites --type=PARTITION
	create region --name=customerPromotions --type=PARTITION
	create region --name=alerts --type=PARTITION



## Testing

	POST http://localhost:6060/processBeaconRequest
	
	{"customerId":1003,"deviceId":"6a468e3e-631f-44e6-8620-cc83330ed994","uuid":"6a468e3e-631f-44e6-8620-cc83330ed994","major":23,"minor":1,"signalPower":0}
	
	http://localhost:8080/favorites/1003
	
	
	http://localhost:6060/loadProductsCache

or  pivotmarketstreams.apps.pcfone.io/loadProductsCache

## SCDF

	app import --uri http://bit.ly/Darwin-SR3-stream-applications-rabbit-maven
	
	app register --name kafka --type source --uri file:///Projects/solutions/nyla/integration/dev/nyla-integration/messaging/apacheKafka/nyla-kafka-spring-cloud-stream-source/target/nyla-kafka-spring-cloud-stream-source-0.0.1-SNAPSHOT.jar


	stream create --definition "kafka --boot-strap-servers-config=localhost:9092 --group-id=scdf | log" --name deleteLog

	stream deploy --name deleteLog --properties  app.kafka.spring.cloud.stream.defaultBinder=rabbit1


   **Remove **
   
	stream undeploy --name deleteLog
   
	   stream destroy --name deleteLog
	   
	   app unregister --name kafka --type source
   
   
   
# Greenplum Association Rules

	SELECT * FROM madlib.assoc_rules( .25,            -- Support
	                                  .5,             -- Confidence
	                                  'itemid',     -- Transaction id col
	                                  'productid',      -- Product col
	                                  'pivotalmarkets.order_items',    -- Input data
	                                  'pivotalmarkets',           -- Output schema
	                                  TRUE            -- Verbose output
	                                )
	select  *  from  madlib.assoc_rules  (.10,   -- Support
	.03,   -- Confidence
	  ‘orderid’, -- Transaction id column
	   ‘productname’, -- item column  
	   ‘order_items’,  -- Input table
	    NULL,  -- Output schema
	     TRUE, --verbose
	      2); --  max_itemset_size
	   
	                                
	select * from pivotalmarkets.assoc_rules
	 
	 psql -d retail
	 
## Rabbit MQ

	http://localhost:15672/#/