/**
 * Copyright (C) 2014 Turn, Inc.  All Rights Reserved.
 * Proprietary and confidential.
 */
package com.turn.api.campaign_metrics;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class AuthorizationCodeReceiver {

	private static final String CALLBACK_PATH = "/oauth2callback";
	private static final String LOCALHOST = "127.0.0.1";
	private static final int PORT = 1237;

	/** Server or {@code null} before {@link #getRedirectUri()}. */
	private Server server;

	/** Verification code or {@code null} before received. */
	volatile String code;

	public String getRedirectUri() throws Exception {
		server = new Server(PORT);
		for (Connector c : server.getConnectors()) {
			c.setHost(LOCALHOST);
		}
		server.addHandler(new CallbackHandler());
		server.start();
		return "http://" + LOCALHOST + ":" + PORT + CALLBACK_PATH;
	}

	public synchronized String waitForCode() {
		try {
			this.wait();
		} catch (InterruptedException exception) {
			// should not happen
		}
		return code;
	}

	public void stop() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	/**
	 * Jetty handler that takes the verifier token passed over from the OAuth
	 * provider and stashes it where {@link #waitForCode} will find it.
	 */
	class CallbackHandler extends AbstractHandler {

		public void handle(String target, HttpServletRequest request,
				HttpServletResponse response, int dispatch) throws IOException {
			if (!CALLBACK_PATH.equals(target)) {
				return;
			}

			response.flushBuffer();
			((Request) request).setHandled(true);
			String error = request.getParameter("error");
			if (error != null) {
				System.out.println("Authorization failed. Error=" + error);
				System.out.println("Quitting.");
				System.exit(1);
			}
			code = request.getParameter("code");
			writeLandingHtml(response, code);
			System.out.println("code: " + code);
			synchronized (AuthorizationCodeReceiver.this) {
				AuthorizationCodeReceiver.this.notify();
			}
		}

		private void writeLandingHtml(HttpServletResponse response, String code)
				throws IOException {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");

			PrintWriter doc = response.getWriter();
			doc.println("<html>");
			doc.println("<head><title>OAuth 2.0 Authentication Token Recieved</title></head>");
			doc.println("<body>");
			doc.println("Received verification code. Code:" + code);
			doc.println("</HTML>");
			doc.flush();
		}
	}
}
