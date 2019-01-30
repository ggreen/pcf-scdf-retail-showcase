package io.pivotal.gemfire.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.apache.geode.cache.Region;

public class MyThread implements Runnable {
	long threadNo;
	Region<String, String> region;
	
	public MyThread(long threadNo, Region<String, String> region) {
		this.threadNo = threadNo;
		this.region = region;
	}
	
	public void run() {
		long i = threadNo;
		//InputStream is = client.getClass().getResourceAsStream("/data.txt");
//		System.out.println(is.toString());
		try {
			BufferedReader in = new BufferedReader(new FileReader("/root/gemfire/client/data.txt"));
			while (true) {
				String s;
				while ((s = in.readLine()) != null) {
					//System.out.println(s);
					//region.put(s + "|" + i, s);
				}
				i++;
			}
			//in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}