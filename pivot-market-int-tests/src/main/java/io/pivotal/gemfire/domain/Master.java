package io.pivotal.gemfire.domain;

import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;

public class Master extends TraceKey implements PdxSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6609002379168956681L;
	
	private String ls;
	private String us;
	
	public Master() {
	}
	
	public Master(PdxInstance key, String ls, String us) {
		setEqpIndex(key.getField("eqpIndex").toString());
		setUnitIndex(key.getField("unitIndex").toString());
		setParamIndex(key.getField("paramIndex").toString());
		setLotId(key.getField("lotId").toString());
		setPpId(key.getField("ppId").toString());
		setRecipeId(key.getField("recipeId").toString());
		setStepSeq(key.getField("stepSeq").toString());
		setPairId(key.getField("pairId").toString());
		setProcessId(key.getField("processId").toString());
		setWaferId(key.getField("waferId").toString());
		setWaferNo(Integer.parseInt(key.getField("waferNo").toString()));
		setLotType(key.getField("lotType").toString());
		setStatusTf(Boolean.getBoolean(key.getField("statusTf").toString()));
		this.ls = ls;
		this.us = us;
	}
	
	public String getLs() {
		return ls;
	}

	public void setLs(String ls) {
		this.ls = ls;
	}
	
	public String getUs() {
		return us;
	}

	public void setUs(String us) {
		this.us = us;
	}

	@Override
	public String toString() {
		return "Master [eqpIndex=" + getEqpIndex() + ", unitIndex=" + getUnitIndex() + ", paramIndex=" + getParamIndex()
				+ ", lotId=" + getLotId() + ", ppId=" + getPpId() + ", recipeId=" + getRecipeId() + ", stepSeq="
				+ getStepSeq() + ", pairId=" + getPairId() + ", processId=" + getProcessId() + ", waferId="
				+ getWaferId() + ", waferNo=" + getWaferNo() + ", lotType=" + getLotType() + ", statusTf="
				+ isStatusTf() + ", ls=" + ls + ", us=" + us + "]";
	}
	
	public void toData(PdxWriter writer) {
		writer.writeString("eqpIndex", getEqpIndex())
		.writeString("unitIndex", getUnitIndex())
		.writeString("paramIndex", getParamIndex())
		.writeString("lotId", getLotId())
		.writeString("ppId", getPpId())
		.writeString("recipeId", getRecipeId())
		.writeString("stepSeq", getStepSeq())
		.writeString("pairId", getPairId())
		.writeString("processId", getProcessId())
		.writeString("waferId", getWaferId())
		.writeInt("waferNo", getWaferNo())
		.writeString("lotType", getLotType())
		.writeBoolean("statusTf", isStatusTf())
		.writeString("ls", ls)
		.writeString("us", us);
	}

	public void fromData(PdxReader reader) {
		setEqpIndex(reader.readString("eqpIndex"));
		setUnitIndex(reader.readString("unitIndex"));
		setParamIndex(reader.readString("paramIndex"));
		setLotId(reader.readString("lotId"));
		setPpId(reader.readString("ppId"));
		setRecipeId(reader.readString("recipeId"));
		setStepSeq(reader.readString("stepSeq"));
		setPairId(reader.readString("pairId"));
		setProcessId(reader.readString("processId"));
		setWaferId(reader.readString("waferId"));
		setWaferNo(reader.readInt("waferNo"));
		setLotType(reader.readString("lotType"));
		setStatusTf(reader.readBoolean("statusTf"));
		this.ls = reader.readString("ls");
		this.us = reader.readString("us");
	}
}
