package com.osthus.casis.index.ioc;

import com.osthus.casis.index.*;
import de.osthus.ambeth.ioc.IInitializingModule;
import de.osthus.ambeth.ioc.annotation.FrameworkModule;
import de.osthus.ambeth.ioc.config.IBeanConfiguration;
import de.osthus.ambeth.ioc.factory.IBeanContextFactory;
import de.osthus.ambeth.job.IJobExtendable;
import de.osthus.ambeth.job.IJobScheduler;
import de.osthus.ambeth.job.cron4j.AmbethCron4jScheduler;
import de.osthus.ambeth.security.AuthenticatedUserHolder;
import de.osthus.ambeth.security.IAuthenticatedUserHolder;
import de.osthus.ambeth.security.ISecurityContextHolder;
import de.osthus.ambeth.security.SecurityContextHolder;

@FrameworkModule
public class ElasticSearchIocModule implements IInitializingModule
{
	@Override
	public void afterPropertiesSet(IBeanContextFactory beanContextFactory) throws Throwable
	{
		beanContextFactory.registerBean(SecurityContextHolder.class).autowireable(ISecurityContextHolder.class);
		beanContextFactory.registerBean(AuthenticatedUserHolder.class).autowireable(IAuthenticatedUserHolder.class);
		beanContextFactory.registerBean("jobScheduler", AmbethCron4jScheduler.class).autowireable(IJobScheduler.class, IJobExtendable.class);

		IBeanConfiguration myJobBean = beanContextFactory.registerBean(MyJobUpdateEachHour.class);
		beanContextFactory.link(myJobBean).to(IJobExtendable.class).with("myJobUpdateEachHour", "* * * * *"); // hour(First minute per hour) day month year
		
		beanContextFactory.registerBean(ElastichSearchImporter.class).autowireable(ElastichSearchImporter.class);;
		beanContextFactory.registerBean(ElasticSearchDao.class).autowireable(ElasticSearchDao.class);
		beanContextFactory.registerBean(ElasticSearchUtil.class).autowireable(ElasticSearchUtil.class);
		beanContextFactory.registerBean(JdbcDao.class).autowireable(JdbcDao.class);
		beanContextFactory.registerBean(JsonUtil.class).autowireable(JsonUtil.class);
		beanContextFactory.registerBean(LastHourState.class).autowireable(LastHourState.class);
		beanContextFactory.registerBean(XmlUtil.class).autowireable(XmlUtil.class);
		
	}

}
