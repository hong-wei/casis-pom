package com.osthus.casis.index;

import static org.junit.Assert.*;

import org.junit.Test;

import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.job.IJobContext;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
@TestFrameworkModule(value=ElasticSearchIocModule.class)
public class MyJobUpdateEachHourTest extends AbstractIocTest {
	@Autowired
	protected MyJobUpdateEachHour myJobUpdateEachHour;
	@Test
	public void executeTest() throws Throwable {
		Thread.sleep(100000000);
	}

}
