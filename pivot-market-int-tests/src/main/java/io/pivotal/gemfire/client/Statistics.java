package io.pivotal.gemfire.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {

	AtomicLong counter = new AtomicLong(0);
	int durationInMins;
	long startTime;
	float max;
	long IN_MILLIS_1_SEC = 1000;
	private final long sleepTimeMS;

	public Statistics(int durationInMins) {
		this(durationInMins,Long.valueOf(System.getProperty("sleepTimeMS","100")).longValue());
	}
	public Statistics(int durationInMins,long sleepTimeMS) {
		this.durationInMins = durationInMins;
		new Thread(new Printer()).start();
		this.sleepTimeMS = sleepTimeMS;
	}

	public void incr() {
		counter.incrementAndGet();
	}
	
	public void incr(long delta) {
		counter.addAndGet(delta);
	}

	private class Printer implements Runnable {
		long endTime;
		long curTime;
		
		public Printer() {
		}
		
		public void run() {
			startTime = System.currentTimeMillis();
			endTime = System.currentTimeMillis() + durationInMins * 60 * 1000;
			
			try {
				Thread.sleep(IN_MILLIS_1_SEC);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			while (endTime >= (curTime = System.currentTimeMillis())) {
				if ((curTime - startTime) >= IN_MILLIS_1_SEC) {
					long rate = counter.get();
					if (rate > max) {
						max = rate;
					}

					System.out.println(new SimpleDateFormat("mm:ss").format(Calendar.getInstance().getTime()));
					System.out.println(String.format("Cur %d writes/sec", rate));
					System.out.println(String.format("Max %.0f writes/sec", max));

					counter.addAndGet(-1 * rate);
					startTime = System.currentTimeMillis();
				} else {
					try {
						Thread.sleep(sleepTimeMS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}