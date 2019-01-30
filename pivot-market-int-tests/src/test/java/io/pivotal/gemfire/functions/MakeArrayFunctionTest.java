package io.pivotal.gemfire.functions;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import io.pivotal.gemfire.domain.TraceKey;

public class MakeArrayFunctionTest
{

	@Test
	public void testSqueeze()
	{
		MakeArrayFunction function = new MakeArrayFunction();
		Assert.assertNull(function.squeeze(null));
		
		Map<TraceKey, PdxInstance> map = new HashMap<TraceKey,PdxInstance>();
		map.put(new TraceKey(1), null);
		map.put(new TraceKey(2), null);
		
		Set<Map.Entry<TraceKey, PdxInstance>> set = new HashSet<Map.Entry<TraceKey,PdxInstance>>(map.entrySet());

		Assert.assertTrue(!set.isEmpty());
		PdxInstance pdx = function.squeeze(set);
		Assert.assertNull(pdx);
		
		PdxInstance pi = Mockito.mock(PdxInstance.class);
		
		WritablePdxInstance writablePdxInstance = Mockito.mock(WritablePdxInstance.class);
		Mockito.when(pi.createWriter()).thenReturn(writablePdxInstance);
		
		map.put(new TraceKey(1), pi);
		map.put(new TraceKey(2), pi);
		
		Assert.assertNotNull(function.squeeze(set));
	}

}
