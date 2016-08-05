package com.turn.api.authentication;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.turn.apis.metadata.v1.Metadata;
import com.turn.apis.metrics.v1.Metrics;

public class Authentication {

		// Token is hardcoded on the config file
		public String token;

		/** Third party library to deal with HTTP protocol and JSON object. */
		/** Global instance of the HTTP transport. */
		private final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
		/** Global instance of the JSON factory. */
		private final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();

		private Metadata metadataApi = null;
		private Metrics metricsApi = null;

		public Authentication(String token) throws Exception  {
			this.token = token;
			getMetadataApi();
		}

		public Metadata getMetadataApi() throws Exception {
			if (this.metadataApi != null) {
				return this.metadataApi;
			}

			Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).build()
					.setAccessToken(token);

			this.metadataApi = new Metadata(HTTP_TRANSPORT, JSON_FACTORY, credential);
			return this.metadataApi;
		}
		
		
		public Metrics getMetricsApi() throws Exception {
			if (this.metricsApi != null) {
				return this.metricsApi;
			}

			Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).build()
					.setAccessToken(token);

			this.metricsApi = new Metrics(HTTP_TRANSPORT, JSON_FACTORY, credential);
			return this.metricsApi;			
		}
}
