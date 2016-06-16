package com.osthus.casis.web.app;

import de.osthus.ambeth.start.TomcatApplication;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		System.setProperty("property.file", "application.properties");
		System.setProperty(TomcatApplication.APP_CONTEXT_ROOT, "/casis");
		System.setProperty(TomcatApplication.WEBSERVER_PORT, "9191");
		TomcatApplication.run();
	}
}
