package io.pivotal.gemfire.domain;

import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;

public class Trace extends TraceKey implements PdxSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3271315804269481715L;
	private String ts;
	private String vl;
	private String ls;
	private String us;
	private String sl;

	public Trace() {
	}
	
	public Trace(TraceKey key) {
		setEqpIndex(key.getEqpIndex());
		setUnitIndex(key.getUnitIndex());
		setParamIndex(key.getParamIndex());
		setLotId(key.getLotId());
		setPpId(key.getPpId());
		setRecipeId(key.getRecipeId());
		setStepSeq(key.getStepSeq());
		setPairId(key.getPairId());
		setProcessId(key.getProcessId());
		setWaferId(key.getWaferId());
		setWaferNo(key.getWaferNo());
		setLotType(key.getLotType());
		setStatusTf(key.isStatusTf());
	}

	public Trace(PdxInstance key, String ts, String vl, String ls, String us, String sl) {
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
		//setSeq(Long.parseLong(key.getField("seq").toString()));
		this.ts = ts;
		this.vl = vl;
		this.ls = ls;
		this.us = us;
		this.sl = sl;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getVl() {
		return vl;
	}

	public void setVl(String vl) {
		this.vl = vl;
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

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public Trace(String eqpIndex, String unitIndex, String paramIndex, String lotId, String ppId, String recipeId,
			String stepSeq, String pairId, String processId, String waferId, int waferNo, String lotType,
			boolean statusTf, /*long seq,*/ String ts, String vl, String ls, String us, String sl) {
		setEqpIndex(eqpIndex);
		setUnitIndex(unitIndex);
		setParamIndex(paramIndex);
		setLotId(lotId);
		setPpId(ppId);
		setRecipeId(recipeId);
		setStepSeq(stepSeq);
		setPairId(pairId);
		setProcessId(processId);
		setWaferId(waferId);
		setWaferNo(waferNo);
		setLotType(lotType);
		setStatusTf(statusTf);
		//setSeq(seq);
		this.ts = ts;
		this.vl = vl;
		this.ls = ls;
		this.us = us;
		this.sl = sl;
	}

	@Override
	public String toString() {
		/*
		 * return "Trace [eqpIndex=" + getEqpIndex() + ", unitIndex=" +
		 * getUnitIndex() + ", paramIndex=" + getParamIndex() + ", lotId=" +
		 * getLotId() + ", ppId=" + getPpId() + ", recipeId=" + getRecipeId() +
		 * ", stepSeq=" + getStepSeq() + ", pairId=" + getPairId() +
		 * ", processId=" + getProcessId() + ", waferId=" + getWaferId() +
		 * ", waferNo=" + getWaferNo() + ", lotType=" + getLotType() +
		 * ", statusTf=" + isStatusTf() + ", seq=" + getSeq() + ", ts=" + ts +
		 * ", vl=" + vl + ", ls=" + ls + ", us=" + us + ", sl=" + sl + "]";
		 */
		return "Trace [eqpIndex=" + getEqpIndex() + ", stepSeq=" + getStepSeq() + ", seq=" + getSeq() + ", ts=" + ts
				+ ", vl=" + vl + ", ls=" + ls + ", us=" + us + ", sl=" + sl + "]";
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
			//.writeLong("seq", getSeq())
			.writeString("ts", ts)
			.writeString("vl", vl)
			.writeString("ls", ls)
			.writeString("us", us)
			.writeString("sl", sl);
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
		//setSeq(reader.readLong("seq"));
		this.ts = reader.readString("ts");
		this.vl = reader.readString("vl");
		this.ls = reader.readString("ls");
		this.us = reader.readString("us");
		this.sl = reader.readString("sl");
	}
}