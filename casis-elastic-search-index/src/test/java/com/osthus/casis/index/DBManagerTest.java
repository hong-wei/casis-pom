package com.osthus.casis.index;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

public class DBManagerTest {

	@Test
	public void test() {
		 long begin=System.currentTimeMillis();  
		  for(int i=0;i<100;i++){  
		   Connection conn=DBManager.getConn();  
		   System.out.print(i+"   ");  
		   DBManager.closeConn(conn);  
		  }  
		  long end=System.currentTimeMillis();  
		  
		  Assert.assertTrue((end-begin)>0);
	}

}
