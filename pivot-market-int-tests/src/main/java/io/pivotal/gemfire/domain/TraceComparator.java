package io.pivotal.gemfire.domain;

import java.util.*;
import java.util.Map.Entry;

import org.apache.geode.pdx.PdxInstance;

//public class TraceComparator implements Comparator<Trace> {
public class TraceComparator<K> implements Comparator<Entry<K, PdxInstance>> {

	public TraceComparator() {
	}

	// sort by stepSeq, ts, vl, sl
	public int compare(Entry<K, PdxInstance> e1, Entry<K, PdxInstance> e2) {
		// ascending
		PdxInstance t1 = (PdxInstance) e1.getValue();
		PdxInstance t2 = (PdxInstance) e2.getValue();
		int comp = t1.getField("stepSeq").toString().compareTo(t2.getField("stepSeq").toString());
		if (comp == 0) {
			comp = t1.getField("ts").toString().compareTo(t2.getField("ts").toString());
			if (comp == 0) {
				comp = t1.getField("vl").toString().compareTo(t2.getField("vl").toString());
				if (comp == 0) {
					//return (Integer.parseInt(t1.getField("sl").toString()) - Integer.parseInt(t2.getField("sl").toString()));
					return t1.getField("sl").toString().compareTo(t2.getField("sl").toString());
				} else {
					return comp;
				}
			} else {
				return comp;
			}
		}
		
		return comp;
	}
	/*
	public int compare(Entry<K, V> e1, Entry<K, V> e2) {
		// ascending
		Trace t1 = (Trace) e1.getValue();
		Trace t2 = (Trace) e2.getValue();
		int comp = t1.getStepSeq().compareTo(t2.getStepSeq());
		if (comp == 0) {
			comp = t1.getTs().compareTo(t2.getTs());
			if (comp == 0) {
				comp = t1.getVl().compareTo(t2.getVl());
				if (comp == 0) {
					return (t1.getSl() - t2.getSl());
				} else {
					return comp;
				}
			} else {
				return comp;
			}
		}
		
		return comp;
	}
	*/
	public static void main(String[] args) {
		Trace t1 = new Trace("eqpIndex", "unitIndex", "paramIndex", "lotId", "ppId", "recipeId", "STEP4", "pairId",
				"processId", "waferId", 1, "lotType", true, String.valueOf(System.nanoTime()), "1212", null, null,
				"0");
		Trace t2 = new Trace("eqpIndex", "unitIndex", "paramIndex", "lotId", "ppId", "recipeId", "STEP2", "pairId",
				"processId", "waferId", 1, "lotType", true, String.valueOf(System.nanoTime()), "1218", null, null,
				"1");
		Trace t3 = new Trace("eqpIndex", "unitIndex", "paramIndex", "lotId", "ppId", "recipeId", "STEP1", "pairId",
				"processId", "waferId", 1, "lotType", true, String.valueOf(System.nanoTime()), "1444", null, null,
				"5");
		Trace t4 = new Trace("eqpIndex", "unitIndex", "paramIndex", "lotId", "ppId", "recipeId", "STEP1", "pairId",
				"processId", "waferId", 1, "lotType", true, String.valueOf(System.nanoTime()), "1129", null, null,
				"1");
		Trace t5 = new Trace("eqpIndex", "unitIndex", "paramIndex", "lotId", "ppId", "recipeId", "STEP1", "pairId",
				"processId", "waferId", 1, "lotType", true, String.valueOf(System.nanoTime()), "1218", null, null,
				"2");

		
		Map<Integer, Trace> map = new HashMap<Integer, Trace>();
		map.put(1, t1);
		map.put(2, t2);
		map.put(3, t3);
		map.put(4, t4);
		map.put(5, t5);
		
		TraceComparator comparator = new TraceComparator();
		System.out.println("BEFORE SORTING");
		comparator.printMap(map);
		
		System.out.println("AFTER SORTING");
		comparator.printMap(comparator.sort(map));
	}
	
	public Map<TraceKey, PdxInstance> sort(Map<TraceKey,PdxInstance> map) {
		List<Entry<TraceKey, PdxInstance>> list = new LinkedList<Entry<TraceKey, PdxInstance>>(map.entrySet());
		Collections.sort(list, new TraceComparator());
		Map<TraceKey, PdxInstance> sortedMap = new LinkedHashMap<TraceKey, PdxInstance>();
		for (Entry<TraceKey, PdxInstance> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public void printMap(Map map) {
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry pair = (Entry) iter.next();
			Trace trace = (Trace) pair.getValue();
			System.out.println(trace.toString());
		}
	}

	public void printList(List list) {
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Trace trace = (Trace) iter.next();
			System.out.println(trace.toString());
		}
	}
}
