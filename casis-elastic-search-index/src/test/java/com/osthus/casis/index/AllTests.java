package com.osthus.casis.index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DBManagerTest.class,
	ElastichSearchImporterTest.class ,
	ElasticSearchDaoTest.class,
	ElasticSearchUtilTest.class,
	JdbcDaoTest.class,
	JsonUtilTest.class,
	XmlUtilTest.class
	})
public class AllTests {
}
