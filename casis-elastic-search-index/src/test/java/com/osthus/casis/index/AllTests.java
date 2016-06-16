package com.osthus.casis.index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ElastichSearchImporterTest.class ,EscapseUtilTest.class,XmlUtilTest.class ,
	JdbcDaoTest.class})
public class AllTests {
}
