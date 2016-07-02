package com.osthus.casis.index;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
@TestFrameworkModule(value = ElasticSearchIocModule.class)
public class DBManagerTest extends AbstractIocTest {

	@LogInstance
	private static ILogger log;
	@Test
	public void getConnTest() {
		 long begin=System.currentTimeMillis();  
		  for(int i=0;i<10;i++){  
		   Connection conn=DBManager.getConn();  
//		   log.info(i+"   ");  
		   DBManager.closeConn(conn);  
		  }  
		  long end=System.currentTimeMillis();  
		  
		  Assert.assertTrue((end-begin)>0);
	}

}
