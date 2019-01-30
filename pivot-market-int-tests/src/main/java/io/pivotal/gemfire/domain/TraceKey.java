package io.pivotal.gemfire.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.geode.DataSerializable;
//import org.apache.geode.pdx.PdxInstance;

/**
 * 
 * Implementation notes from Gideon
 * 
 * B TraceKey partitioning: 
 * Split the class into a super/sub class pair.  Put only the compound key fields in the superclass, 
 * and put only the seq field in the sub-class.  Superclass implements hashCode() with all 
 * the key fields, and we use that hashCode() result as the "routingObject" 
 * (the same combination of compound key field values always results in the same 
 * hashCode/routing object, so we get co-location based on their rules).  
 * Subclass implements hashCode()/equals() with ONLY the seq field, which acts as 
 * the actual Region key (and behaves the same as if we just used "seq" as the key directly).   
 * We could also keep it a single class, use only seq in the hashcode/equals, and implement the 
 * compound hashCode  suggested for the superclass as just another method (to be used in the 
 * PartitionResolver.getRoutingObject() method).
 *
 */
public class TraceKey implements DataSerializable, RoutingKey {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String eqpIndex;
	private String unitIndex;
	private String paramIndex;
	private String lotId;
	private String ppId;
	private String recipeId;
	private String stepSeq;
	private String pairId;
	private String processId;
	private String waferId;
	private int waferNo;
	private String lotType;
	private boolean statusTf;
	private long seq;
	
	public TraceKey() {
	}
	
	public TraceKey(long seq)
	{
		this.seq = seq;
	}
	
	public TraceKey(TraceKey other)
	{
		super();
		this.eqpIndex = other.eqpIndex;
		this.unitIndex = other.unitIndex;
		this.paramIndex = other.paramIndex;
		this.lotId = other.lotId;
		this.ppId = other.ppId;
		this.recipeId = other.recipeId;
		this.stepSeq = other.stepSeq;
		this.pairId = other.pairId;
		this.processId = other.processId;
		this.waferId = other.waferId;
		this.waferNo = other.waferNo;
		this.lotType = other.lotType;
		this.statusTf = other.statusTf;
		this.seq = other.seq;
	}



	/*
	public TraceKey(DataInput pdxI) {
		this.eqpIndex = pdxI.readUTF()("eqpIndex").toString();
		this.unitIndex = pdxI.getField("unitIndex").toString();
		this.paramIndex = pdxI.getField("paramIndex").toString();
		this.lotId = pdxI.getField("lotId").toString();
		this.ppId = pdxI.getField("ppId").toString();
		this.recipeId = pdxI.getField("recipeId").toString();
		this.stepSeq = pdxI.getField("stepSeq").toString();
		this.pairId = pdxI.getField("pairId").toString();
		this.processId = pdxI.getField("processId").toString();
		this.waferId = pdxI.getField("waferId").toString();
		this.waferNo = Integer.parseInt(pdxI.getField("waferNo").toString());
		this.lotType = pdxI.getField("lotType").toString();
		this.statusTf = Boolean.getBoolean(pdxI.getField("statusTf").toString());
		this.seq = Long.parseLong(pdxI.getField("seq").toString());
	}*/
	
	public TraceKey(String eqpIndex, String unitIndex, String paramIndex, String lotId, String ppId, 
			String recipeId, String stepSeq, String pairId, String processId, String waferId, 
			int waferNo, String lotType, boolean statusTf, long seq) {
		this.eqpIndex = eqpIndex;
		this.unitIndex = unitIndex;
		this.paramIndex = paramIndex;
		this.lotId = lotId;
		this.ppId = ppId;
		this.recipeId = recipeId;
		this.stepSeq = stepSeq;
		this.pairId = pairId;
		this.processId = processId;
		this.waferId = waferId;
		this.waferNo = waferNo;
		this.lotType = lotType;
		this.statusTf = statusTf;
		this. seq =  seq;
	}
	
	public String getEqpIndex() {
		return eqpIndex;
	}

	public void setEqpIndex(String eqpIndex) {
		this.eqpIndex = eqpIndex;
	}

	public String getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(String unitIndex) {
		this.unitIndex = unitIndex;
	}

	public String getParamIndex() {
		return paramIndex;
	}

	public void setParamIndex(String paramIndex) {
		this.paramIndex = paramIndex;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getPpId() {
		return ppId;
	}

	public void setPpId(String ppId) {
		this.ppId = ppId;
	}

	public String getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}

	public String getStepSeq() {
		return stepSeq;
	}

	public void setStepSeq(String stepSeq) {
		this.stepSeq = stepSeq;
	}

	public String getPairId() {
		return pairId;
	}

	public void setPairId(String pairId) {
		this.pairId = pairId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getWaferId() {
		return waferId;
	}

	public void setWaferId(String waferId) {
		this.waferId = waferId;
	}

	public int getWaferNo() {
		return waferNo;
	}

	public void setWaferNo(int waferNo) {
		this.waferNo = waferNo;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public boolean isStatusTf() {
		return statusTf;
	}

	public void setStatusTf(boolean statusTf) {
		this.statusTf = statusTf;
	}

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	@Override
	public String toString() {
		return "TraceKey [eqpIndex=" + eqpIndex + ", unitIndex=" + unitIndex + ", paramIndex=" + paramIndex + ", lotId="
				+ lotId + ", ppId=" + ppId + ", recipeId=" + recipeId + ", stepSeq=" + stepSeq + ", pairId=" + pairId
				+ ", processId=" + processId + ", waferId=" + waferId + ", waferNo=" + waferNo + ", lotType=" + lotType
				+ ", statusTf=" + statusTf + ", seq=" + seq + "]";
	}

	public void toData(DataOutput writer) throws IOException {
		writer.writeUTF(eqpIndex);
		writer.writeUTF(unitIndex);
		writer.writeUTF(paramIndex);
		writer.writeUTF(lotId);
		writer.writeUTF( ppId);
		writer.writeUTF(recipeId);
		writer.writeUTF( stepSeq);
		writer.writeUTF(pairId);
		writer.writeUTF(processId);
		writer.writeUTF(waferId);
		writer.writeInt(waferNo);
		writer.writeUTF( lotType);
		writer.writeBoolean(statusTf);
		writer.writeLong(seq);
		
		/*writer.writeString("eqpIndex", eqpIndex)
		.writeString("unitIndex", unitIndex)
		.writeString("paramIndex", paramIndex)
		.writeString("lotId", lotId)
		.writeString("ppId", ppId)
		.writeString("recipeId", recipeId)
		.writeString("stepSeq", stepSeq)
		.writeString("pairId", pairId)
		.writeString("processId", processId)
		.writeString("waferId", waferId)
		.writeInt("waferNo", waferNo)
		.writeString("lotType", lotType)
		.writeBoolean("statusTf", statusTf)
		.writeLong("seq", seq);*/
	}

	public void fromData(DataInput reader) throws IOException {
		eqpIndex = reader.readUTF();
		unitIndex = reader.readUTF();
		paramIndex = reader.readUTF();
		lotId = reader.readUTF();
		ppId = reader.readUTF();
		recipeId = reader.readUTF();
		stepSeq = reader.readUTF();
		pairId = reader.readUTF();
		processId = reader.readUTF();
		waferId = reader.readUTF();
		waferNo = reader.readInt();
		lotType = reader.readUTF();
		statusTf = reader.readBoolean();
		seq = reader.readLong();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eqpIndex == null) ? 0 : eqpIndex.hashCode());
		result = prime * result + ((lotId == null) ? 0 : lotId.hashCode());
		result = prime * result + ((lotType == null) ? 0 : lotType.hashCode());
		result = prime * result + ((pairId == null) ? 0 : pairId.hashCode());
		result = prime * result + ((paramIndex == null) ? 0 : paramIndex.hashCode());
		result = prime * result + ((ppId == null) ? 0 : ppId.hashCode());
		result = prime * result + ((processId == null) ? 0 : processId.hashCode());
		result = prime * result + ((recipeId == null) ? 0 : recipeId.hashCode());
		result = prime * result + (int) (seq ^ (seq >>> 32));
		result = prime * result + (statusTf ? 1231 : 1237);
		result = prime * result + ((stepSeq == null) ? 0 : stepSeq.hashCode());
		result = prime * result + ((unitIndex == null) ? 0 : unitIndex.hashCode());
		result = prime * result + ((waferId == null) ? 0 : waferId.hashCode());
		result = prime * result + waferNo;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceKey other = (TraceKey) obj;
		if (eqpIndex == null)
		{
			if (other.eqpIndex != null)
				return false;
		}
		else if (!eqpIndex.equals(other.eqpIndex))
			return false;
		if (lotId == null)
		{
			if (other.lotId != null)
				return false;
		}
		else if (!lotId.equals(other.lotId))
			return false;
		if (lotType == null)
		{
			if (other.lotType != null)
				return false;
		}
		else if (!lotType.equals(other.lotType))
			return false;
		if (pairId == null)
		{
			if (other.pairId != null)
				return false;
		}
		else if (!pairId.equals(other.pairId))
			return false;
		if (paramIndex == null)
		{
			if (other.paramIndex != null)
				return false;
		}
		else if (!paramIndex.equals(other.paramIndex))
			return false;
		if (ppId == null)
		{
			if (other.ppId != null)
				return false;
		}
		else if (!ppId.equals(other.ppId))
			return false;
		if (processId == null)
		{
			if (other.processId != null)
				return false;
		}
		else if (!processId.equals(other.processId))
			return false;
		if (recipeId == null)
		{
			if (other.recipeId != null)
				return false;
		}
		else if (!recipeId.equals(other.recipeId))
			return false;
		if (seq != other.seq)
			return false;
		if (statusTf != other.statusTf)
			return false;
		if (stepSeq == null)
		{
			if (other.stepSeq != null)
				return false;
		}
		else if (!stepSeq.equals(other.stepSeq))
			return false;
		if (unitIndex == null)
		{
			if (other.unitIndex != null)
				return false;
		}
		else if (!unitIndex.equals(other.unitIndex))
			return false;
		if (waferId == null)
		{
			if (other.waferId != null)
				return false;
		}
		else if (!waferId.equals(other.waferId))
			return false;
		if (waferNo != other.waferNo)
			return false;
		return true;
	}

	/*
	  route object attributes except seq
	 */
	public int getRoutingObject() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eqpIndex == null) ? 0 : eqpIndex.hashCode());
        result = prime * result + ((lotId == null) ? 0 : lotId.hashCode());
        result = prime * result + ((lotType == null) ? 0 : lotType.hashCode());
        result = prime * result + ((pairId == null) ? 0 : pairId.hashCode());
        result = prime * result + ((paramIndex == null) ? 0 : paramIndex.hashCode());
        result = prime * result + ((ppId == null) ? 0 : ppId.hashCode());
        result = prime * result + ((processId == null) ? 0 : processId.hashCode());
        result = prime * result + ((recipeId == null) ? 0 : recipeId.hashCode());
        result = prime * result + (statusTf ? 1231 : 1237);
        result = prime * result + ((stepSeq == null) ? 0 : stepSeq.hashCode());
        result = prime * result + ((unitIndex == null) ? 0 : unitIndex.hashCode());
        result = prime * result + ((waferId == null) ? 0 : waferId.hashCode());
        result = prime * result + waferNo;
        return result;
    }
	
	
}
