package io.pivotal.gemfire.domain;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TraceKeyTest
{

	@Test
	public void testToData()
	throws IOException
	{
		
		File dir = new File("target/runtime/");
		dir.mkdir();
		
		File file = new File("target/runtime/"+TraceKey.class.getSimpleName());
		file.delete();
		
		try( DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(file)))
		{
			long seq = 02;
		
			
			// writing string to a file encoded as modified UTF-8
		     

		      TraceKey traceKey = new TraceKey();	      
		      traceKey.setEqpIndex("eqpIndex");
		      
		      traceKey.setSeq(seq);
		      traceKey.setEqpIndex("String");
		      traceKey.setLotId("String");
		      traceKey.setLotType("String");
		      traceKey.setPairId("String");
		      traceKey.setParamIndex("String");
		      traceKey.setPpId("String");
		      traceKey.setProcessId("String");
		      traceKey.setRecipeId("String");

		      traceKey.setStatusTf(true);
		      traceKey.setStepSeq("String");
		      traceKey.setUnitIndex("String");
		      traceKey.setWaferId("String");
		      traceKey.setWaferNo(23);
		      
		      
		      traceKey.toData(dataOut);
		      dataOut.close();
		      
		      
		      DataInputStream dataIn = new DataInputStream(new FileInputStream(file));
		     
		      TraceKey fromFile = new TraceKey();
		      fromFile.fromData(dataIn);
		      
		      Assert.assertEquals(traceKey, fromFile);
		     
		}
		
	}//------------------------------------------------

	@Test
	public void testFromData()
	throws IOException
	{
	DataInput dataInput = Mockito.mock(DataInput.class);
		
		TraceKey traceKey = new TraceKey();
		
		
		Mockito.when(dataInput.readUTF())
		.thenReturn("eqpIndex")
		.thenReturn("unitIndex")
		.thenReturn("paramIndex")
		.thenReturn("lotId")
		.thenReturn("ppId")
		.thenReturn("recipeId")
		.thenReturn("stepSeq")
		.thenReturn("pairId")
		.thenReturn("processId")
		.thenReturn("waferId");
		
		long seq = 01;
		
		Mockito.when(dataInput.readBoolean()).thenReturn(true);
		Mockito.when(dataInput.readLong()).thenReturn(seq);
		
		
		traceKey.fromData(dataInput);
		
		Assert.assertEquals(traceKey.getLotId(), "lotId");
		Assert.assertEquals(traceKey.getWaferId(), "waferId");
		Assert.assertEquals(traceKey.getUnitIndex(), "unitIndex");
		Assert.assertEquals(traceKey.isStatusTf(), true);
		Assert.assertEquals(traceKey.getSeq(), seq);
	
	}

	@Test
	public void testGetRoutingObject()
	{
		TraceKey o1 = new TraceKey();
		TraceKey o2 = new TraceKey();
		
		Assert.assertEquals(o1.getRoutingObject(), o2.getRoutingObject());
		o1.setSeq(-1);
		o2.setSeq(-1212);
		Assert.assertEquals(o1.getRoutingObject(), o2.getRoutingObject());
		o2.setEqpIndex("eqpIndex");
		Assert.assertNotEquals(o1.getRoutingObject(), o2.getRoutingObject());
	}
	
	@Test
	public void testEquals()
	{
		TraceKey o1 = new TraceKey();
		TraceKey o2 = new TraceKey();
		
		Assert.assertEquals(o1, o2);
		o1.setStepSeq("stepSeq1");
		o2.setStepSeq("stepSeq2");
		Assert.assertNotEquals(o1, o2);
		o1.setSeq(001);
		o2.setSeq(02);
		Assert.assertNotEquals(o1, o2);
	}

	@Test
	public void testHashCode()
	{
		TraceKey o1 = new TraceKey();
		TraceKey o2 = new TraceKey();
		
		Assert.assertEquals(o1.hashCode(), o2.hashCode());
		o1.setStepSeq("stepSeq1");
		o2.setStepSeq("stepSeq2");
		Assert.assertNotEquals(o1.hashCode(), o2.hashCode());
		o1.setSeq(001);
		o2.setSeq(02);
		Assert.assertNotEquals(o1.hashCode(), o2.hashCode());
	}
}
