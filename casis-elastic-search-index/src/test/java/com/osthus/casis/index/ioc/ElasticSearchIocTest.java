package com.osthus.casis.index.ioc;

import org.junit.Test;

import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;

@TestFrameworkModule({ ElasticSearchIocModule.class })
public class ElasticSearchIocTest extends AbstractIocTest
{
	@Test
	public void test()
	{
		System.out.println();
	}
}
