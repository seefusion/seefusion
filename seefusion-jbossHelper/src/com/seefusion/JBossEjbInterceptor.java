/*
 * JBossEjbInterceptor.java
 *
 */

package com.seefusion;

import org.jboss.ejb.plugins.AbstractInterceptor;
import org.jboss.invocation.Invocation;

/**
 * 
 */
public class JBossEjbInterceptor extends AbstractInterceptor {

	SeeFusionConnector sfcInstance;

	/** Creates a new instance of JBossEjbInterceptor */
	public JBossEjbInterceptor() {
		super();
	}

	public Object invoke(Invocation invocation) throws java.lang.Exception {
		if (sfcInstance == null || invocation == null) {
			return super.invoke(invocation);
		}
		else {
			java.lang.reflect.Method method = invocation.getMethod();
			// log("Begin Interceptor");
			String methodClass = method.getDeclaringClass().getName();
			// log("Server is " + server);

			String methodName = method.getName();
			// log("Method name is " + methodName);

			StringBuffer sig = new StringBuffer(200);
			sig.append("(");
			Object[] args = invocation.getArguments();
			// log("Arg count: " + args.length);
			for (int i = 0; i < args.length; ++i) {
				if(args[i]==null) {
					sig.append("null,");
				}
				else {
					sig.append(args[i].toString()).append(",");
				}
			}
			sig.append(")");

			// String caller = invocation.getPrincipal().getName();
			String caller = "";

			// createRequest(servername, URI, querystring, remoteaddr, method, pathInfo, isSecure)
			SeeFusionRequest sfRequest = sfcInstance.createRequest(methodClass + ".", methodName + "(", sig.toString()
					+ ")", caller, "GET", "", false);
			try {
				return super.invoke(invocation);
			}
			finally {
				sfRequest.close();
			}
		}
	}

	void log(String s) {
		Logger.log(s);
	}

	public void create() throws Exception {
		super.create();
		try {
			this.sfcInstance = SeeFusionConnector.getInstance("ejb");
		}
		catch (Exception e) {
			System.out.println("Unable to load SeeFusion EJB Interceptor: " + e.toString());
			System.err.println("Unable to load SeeFusion EJB Interceptor: " + e.toString());
			e.printStackTrace();
			this.sfcInstance = null;
		}
	}

	public void destroy() {
		super.destroy();
		if (this.sfcInstance != null) {
			this.sfcInstance.shutdown();
		}
	}
}
