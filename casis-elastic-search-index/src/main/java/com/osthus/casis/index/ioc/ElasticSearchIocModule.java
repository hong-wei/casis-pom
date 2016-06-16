package com.osthus.casis.index.ioc;

import java.util.Date;

import de.osthus.ambeth.ioc.IInitializingModule;
import de.osthus.ambeth.ioc.config.IBeanConfiguration;
import de.osthus.ambeth.ioc.factory.IBeanContextFactory;
import de.osthus.ambeth.job.IJob;
import de.osthus.ambeth.job.IJobContext;
import de.osthus.ambeth.job.IJobExtendable;
import de.osthus.ambeth.job.IJobScheduler;
import de.osthus.ambeth.job.cron4j.AmbethCron4jScheduler;
import de.osthus.ambeth.security.AuthenticatedUserHolder;
import de.osthus.ambeth.security.IAuthenticatedUserHolder;
import de.osthus.ambeth.security.ISecurityContextHolder;
import de.osthus.ambeth.security.SecurityContextHolder;

public class ElasticSearchIocModule implements IInitializingModule
{
	@Override
	public void afterPropertiesSet(IBeanContextFactory beanContextFactory) throws Throwable
	{
		beanContextFactory.registerBean(SecurityContextHolder.class).autowireable(ISecurityContextHolder.class);
		beanContextFactory.registerBean(AuthenticatedUserHolder.class).autowireable(IAuthenticatedUserHolder.class);
		beanContextFactory.registerBean("jobScheduler", AmbethCron4jScheduler.class).autowireable(IJobScheduler.class, IJobExtendable.class);

		IBeanConfiguration myJobBean = beanContextFactory.registerBean(MyJob.class);
		beanContextFactory.link(myJobBean).to(IJobExtendable.class).with("helloJob", "1 * * * *");
	}

	public static class MyJob implements IJob
	{
		@Override
		public boolean canBePaused()
		{
			return false;
		}

		@Override
		public boolean canBeStopped()
		{
			return false;
		}

		@Override
		public boolean supportsStatusTracking()
		{
			return false;
		}

		@Override
		public boolean supportsCompletenessTracking()
		{
			return false;
		}

		@Override
		public void execute(IJobContext context) throws Throwable
		{
			System.out.println(Thread.currentThread().getId() + " current time:" + new Date());
		}

	}
}
