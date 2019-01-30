package io.pivotal.gemfire.domain;

import java.util.Set;

public class TraceGpdb {
	
	private String equipId;
	private String lotId;
	private Set<String> steps;
	private Set<Double> vals;
	private Set<String> params;
	
	public TraceGpdb() {
	}
	
	public TraceGpdb(String equipId, String lotId, Set<String> steps, Set<Double> vals, Set<String> params) {
		this.equipId = equipId;
		this.lotId = lotId;
		this.steps = steps;
		this.vals = vals;
		this.params = params;
	}
	
	public String getEquipId() {
		return equipId;
	}

	public void setEquipId(String equipId) {
		this.equipId = equipId;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public Set<String> getSteps() {
		return steps;
	}

	public void setSteps(Set<String> steps) {
		this.steps = steps;
	}

	public Set<Double> getVals() {
		return vals;
	}

	public void setVals(Set<Double> vals) {
		this.vals = vals;
	}

	public Set<String> getParams() {
		return params;
	}

	public void setParams(Set<String> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "TraceGpdb [equipId=" + equipId + ", lotId=" + lotId + "steps=" + steps.toString()
				+ ", vals=" + vals.toString() + ", params=" + params.toString() + "]";
	}
}
