package io.pivotal.gemfire.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class Client {

	public Client() {
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 2) {
			System.err.println("Correct Usage: <num_threads> <duratios_in_mins>");
			System.exit(1);
		}
		
		int numThreads = Integer.parseInt(args[0]);
		int durationInMins = Integer.parseInt(args[1]);
		
		ClientCache cache = new ClientCacheFactory()
				.addPoolLocator("localhost", 10334)
				.create();
		
		Region<String, byte[]> region = cache.<String, byte[]>createClientRegionFactory(ClientRegionShortcut.PROXY).create("EQUIPMENT");
		Statistics statistics = new Statistics(durationInMins);
		List<Thread> threads = new ArrayList<Thread>();
		
		for (int i=0; i<numThreads; i++) {
			threads.add(new Thread(new Worker(region, durationInMins, statistics)));
		}
		
		for (Thread thread : threads) {
			thread.start();
		}
		
		for (Thread thread : threads) {
			thread.join();
		}
	}

}