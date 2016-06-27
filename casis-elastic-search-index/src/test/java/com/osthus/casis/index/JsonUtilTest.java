package com.osthus.casis.index;

import static org.junit.Assert.*;

import org.junit.Test;

import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
@TestFrameworkModule(value=ElasticSearchIocModule.class)
public class JsonUtilTest extends AbstractIocTest {

	@Test
	public void test() {
//		fail("Not yet implemented");
	}

}
