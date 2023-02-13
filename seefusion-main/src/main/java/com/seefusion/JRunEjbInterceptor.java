/**
 * 
 */
package com.seefusion;

public class JRunEjbInterceptor {
	
}
/*
import jrun.ejb.interceptors.AbstractInterceptor;
import jrun.ejb.EJBContainer;
import jrun.ejb.EJBInvocation;

public class JRunEjbInterceptor extends AbstractInterceptor {

	SeeFusionConnector sfcInstance;

	private static final long serialVersionUID = -708800794376674427L;

	public JRunEjbInterceptor(EJBContainer container) {
		super(container);
	}

	public Object invokeObjectMethod(EJBInvocation invocation) throws Exception {
		if (sfcInstance == null || invocation==null) {
			return getNext().invokeObjectMethod(invocation);
		}
		else {
			java.lang.reflect.Method method = invocation.getMethod();
			// log("Begin Interceptor");
			String methodClass = method.getDeclaringClass().getName();
			// log("Server is " + server);

			String methodName = method.getName();
			// log("Method name is " + methodName);

			StringBuilder sig = new StringBuilder(200);
			sig.append("(");
			Object[] args = invocation.getArgs();
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

			// createRequest(servername, URI, querystring, remoteaddr)
			SeeFusionRequest sfRequest = sfcInstance.createRequest(methodClass + ".", methodName + "(", sig.toString()
					+ ")", caller);
			try {
				return getNext().invokeObjectMethod(invocation);
			}
			finally {
				sfRequest.close();
			}
		}
	}

	public void init() throws Exception {
		super.init();
		try {
			// no longer really a TO DO:
			// TO DO: Figure out how to get a useful config dir in JRun
			this.sfcInstance = SeeFusionConnector.getInstance("???");
		}
		catch (Exception e) {
			System.err.println("Unable to load SeeFusion EJB Interceptor: " + e.toString());
			e.printStackTrace();
			this.sfcInstance = null;
		}

	}

	public void destroy() throws Exception {
		super.destroy();
		if (this.sfcInstance != null) {
			this.sfcInstance.shutdown();
		}
	}
}

*/