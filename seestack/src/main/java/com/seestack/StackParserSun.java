/**
 * 
 */
package com.seestack;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Daryl
 *
 */
public class StackParserSun implements IStackParser {

	boolean isDebug=false;
	private BufferedReader in;
	static Pattern threadStartPattern = Pattern.compile("\"[^\"]+\"[^\"]*");
	static Pattern threadNamePattern = Pattern.compile("\"");
	static Pattern lineSplitPattern = Pattern.compile("[\\s()\"]+");
	
	public StackParserSun(BufferedReader in) {
		this.in = in;
	}

	/* (non-Javadoc)
	 * @see com.seestack.IStackParser#parseStack(java.io.BufferedReader)
	 */
	public Threads parseStack(SeeStackInfo info) {
		String[] tokens;
		String curLine, curLineTrimmed;
		
		Threads ret = new Threads();
		boolean inThread = false;
		SeeThread curThread = null;
		try {
			while( (curLine = in.readLine()) != null) {
				curLineTrimmed = curLine.trim();
				
				// thread start
				if(threadStartPattern.matcher(curLine).matches()) {
					tokens = threadNamePattern.split(curLine);
					debug(curLine);
					inThread = true;
					if(curThread != null) {
						ret.add(curThread);
					}
					String threadName = tokens[1]; 
					curThread = new SeeThread(threadName, curLine, info.findThreadInfo(threadName));
				}
				// thread line
				else if (inThread) {
					tokens = lineSplitPattern.split(curLineTrimmed);
					if(curLineTrimmed.length()==0) {
						inThread = false;
						ret.add(curThread);
						curThread = null;
					}
					else if (curLineTrimmed.startsWith("at")) {
						// at java.lang.Object.wait(Object.java:429)
						// first token is "at", second is full method name
						Method m = null;
						if(tokens.length < 2) {
							// just the word "at"?
							// ignore
						}
						else if (tokens.length == 2) {
							// no parenthetical (not seen in the real world, but coding defensively)
							// at java.lang.Object.wait
							m = new Method( curLine, tokens[1], "", info );
						}
						else {
							m = new Method( curLine, tokens[1], tokens[2], info );
						}
						if(m != null) {
							curThread.addMethod(m);
						}
						debug(curLine);
					}
					else if (curLineTrimmed.startsWith("-")) {
						// all:
						// - locked <233> (a jrun.servlet.io.ReusableBufferedInputStream)
						// - locked <0x457ef990> (a java.util.ArrayList)
						// -- Waiting for notification on: org/apache/tomcat/util/threads/ThreadPool$ControlRunnable@0x23b277f0[fat lock]
						curThread.addText(curLine);
						debug(curLine);
					}
					else if (curLineTrimmed.startsWith("^--")) {
						// jrockit:
						// ^-- Lock released while waiting: org/apache/tomcat/util/threads/ThreadPool$ControlRunnable@0x23b277f0[fat lock]
						curThread.addText(curLine);
						debug(curLine);
					}
					else {
						// this thread stack must be complete
						inThread = false;
					}
				}
				
				// else ignore line, not part of a stack
				
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	void debug(String s) {
		if(this.isDebug) System.out.println(s);
	}
}
