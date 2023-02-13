package com.seefusion;

//import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.StrictExpectations;
import mockit.VerificationsInOrder;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class SmtpSenderTest {

	protected static final String CRLF = "\r\n";

	@Test
	public void testAuth(@Injectable final BufferedReader in, @Injectable final PrintWriter out) throws Exception {
		new StrictExpectations() {{
			in.readLine(); result = "220 smtp.server.com Simple Mail Transfer Service Ready";
			in.readLine(); result = "250-whatever" + CRLF;
			in.readLine(); result = "250 whatever" + CRLF;
			in.readLine(); result = "334 VXNlcm5hbWU6" + CRLF;
			in.readLine(); result = "334 UGFzc3dvcmQ6" + CRLF;
			in.readLine(); result = "235 OK whatever" + CRLF;
			in.readLine(); result = "250 OK" + CRLF;
			in.readLine(); result = "250 OK" + CRLF;
			in.readLine(); result = "354 OK" + CRLF;
			in.readLine(); result = "250 OK" + CRLF;
		}};
		SmtpSender test = new SmtpSender("localhost", "username", "password");
		SmtpMessage message = new SmtpMessage();
		message.setSmtpFrom("me@my.com");
		message.setSmtpTo("me@my.com");
		message.setSmtpSubject("Hello, cruel world!");
		message.setSmtpBody("Bah!");
		test.send(in, out, message);
		new VerificationsInOrder() {{
			out.print("EHLO SeeFusion" + CRLF);
			out.flush();
			out.print("AUTH LOGIN" + CRLF);
			out.flush();
			out.print("dXNlcm5hbWU=" + CRLF);
			out.flush();
			out.print("cGFzc3dvcmQ=" + CRLF);
			out.flush();
			unverifiedInvocations();
		}};
		
	}

}
