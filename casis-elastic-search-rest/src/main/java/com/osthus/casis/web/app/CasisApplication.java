//package com.osthus.casis.web.app;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import javax.ws.rs.ApplicationPath;
//import javax.ws.rs.core.Application;
//
//import org.glassfish.jersey.filter.LoggingFilter;
//
//import com.osthus.casis.web.CasisRestController;
//
//@ApplicationPath("/foo")
//public class CasisApplication extends Application
//{
//	@Override
//	public Set<Class<?>> getClasses()
//	{
//		final Set<Class<?>> classes = new HashSet<Class<?>>();
//		classes.add(CasisRestController.class);
//		classes.add(LoggingFilter.class);
//		return classes;
//	}
//
//}

//package com.osthus.casis.web.app;
//
//import java.util.Collections;
//import java.util.Set;
//import javax.ws.rs.ApplicationPath;
//import javax.ws.rs.core.Application;
//
//@ApplicationPath("/foo")
//public class CasisApplication extends Application {
//
//    @Override
//    public Set<Class<?>> getClasses() {
//        return Collections.emptySet();
//    }
//}