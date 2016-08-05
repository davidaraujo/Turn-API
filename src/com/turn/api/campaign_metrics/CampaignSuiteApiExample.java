/**
 * Copyright (C) 2014 Turn, Inc.  All Rights Reserved.
 * Proprietary and confidential.
 */
package com.turn.api.campaign_metrics;

/**
 * An example code of Turn Developer API. We take Campaign Suite API as an example.
 * You might need to apply for an API Key that is assigned to Campaign Suite API
 * package plan in order to run this example code.
 * 
 * @author jchen
 */

import com.turn.apis.metadata.v1.Metadata;
import com.turn.apis.metadata.v1.Metadata.Advertisers;
import com.turn.apis.metadata.v1.Metadata.Advertisers.Get;
import com.turn.apis.metadata.v1.Metadata.Lineitems;
import com.turn.apis.metadata.v1.Metadata.Lineitems.List;
import com.turn.apis.metadata.v1.MetadataScopes;
import com.turn.apis.metadata.v1.model.Advertiser;
import com.turn.apis.metadata.v1.model.AdvertiserSummaryList;

import com.turn.apis.metrics.v1.Metrics;
import com.turn.apis.metrics.v1.MetricsScopes;
import com.turn.apis.metrics.v1.model.AdvertiserReportingDataList;
import com.turn.apis.metrics.v1.model.IdList;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class CampaignSuiteApiExample {
	public final String apiKey;

	/** Campaign Suite API constants 
	 *	You should always use these constants for all Turn developers API 
	 **/
	private static final String AUTHORIZATION_SERVER_URL = "https://console.turn.com/app/advertiser/include/consent.htm";
	
	/**
	 * API scope constants
	 * Different Turn API services will have different API scopes and API request token URL.
	 * Turn customers can get these information from Java SDK and Turn Developer API documentation.
	 * */
	/** Campaign Suite Management API constants */
	private final String MANAGEMENT_API_SCOPE = MetadataScopes.MPAIGN_METADATA;
	private final String MANAGEMENT_API_TOKEN_SERVER_URL = "https://www.turnapis.com/metadata/oauth/token";
	/** Campaign Suite Metrics API constants */
	private final String METRICS_API_SCOPE = MetricsScopes.MPAIGN_METRICS;
	private final String METRICS_API_TOKEN_SERVER_URL = "https://www.turnapis.com/metrics/oauth/token";
	
	/** Third party library to deal with HTTP protocol and JSON object.*/
	/** Global instance of the HTTP transport. */
	private final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
	/** Global instance of the JSON factory. */
	private final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private Metadata metadataApi = null;
	private Metrics metricsApi = null;

	public CampaignSuiteApiExample(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public Metadata getMetadataApi() throws Exception {
		if (this.metadataApi != null) {
			return this.metadataApi;
		}
		
		/** Authorize Campaign Suite Management API **/
		Credential credential = turnDeveloperApiOAuth(this.apiKey,
				MANAGEMENT_API_SCOPE,
				MANAGEMENT_API_TOKEN_SERVER_URL);
		
		//Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).build().setAccessToken("mww9r3qhetwjw8kvktct35u4");
		
		this.metadataApi = new Metadata(HTTP_TRANSPORT, JSON_FACTORY,
				credential);
		return this.metadataApi;
	}
	
	public Metrics getMetricsApi() throws Exception {
		if (this.metricsApi != null) {
			return this.metricsApi;
		}
		
		
		/** Authorize Campaign Suite Metrics API **/
		Credential credential = turnDeveloperApiOAuth(apiKey,
				METRICS_API_SCOPE,
				METRICS_API_TOKEN_SERVER_URL);
		this.metricsApi = new Metrics(HTTP_TRANSPORT, JSON_FACTORY,
				credential);
		return this.metricsApi;
	}
	
	/**
	 * Turn API authentication process.
	 * Turn customers can get API Scope and Token
	 * Given your API key, 
	 */
	protected Credential turnDeveloperApiOAuth(String apiKey, String scope,
			String requestToken) throws Exception {
		AuthorizationCodeReceiver receiver = new AuthorizationCodeReceiver();
		try {
			String redirectUri = receiver.getRedirectUri();
			System.out.println(redirectUri);
			launchInBrowser("google-chrome", redirectUri,
					apiKey, scope);
			Credential credential = authorize(receiver, redirectUri, apiKey,
					scope, requestToken); 
			System.out.println("access token:" + credential.getAccessToken());
					
			return credential;
		} catch (Exception e){
			return null;
		} finally {
			receiver.stop();
		}
	}

	private void launchInBrowser(String browser, String redirectUrl,
			String clientId, String scope) throws IOException {
		String authorizationUrl = new AuthorizationCodeRequestUrl(
				AUTHORIZATION_SERVER_URL, clientId).setRedirectUri(redirectUrl)
				.setScopes(Arrays.asList(scope)).build();
		System.out.println("authorizationUrl uri: " + authorizationUrl);
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Action.BROWSE)) {
				desktop.browse(URI.create(authorizationUrl));
				return;
			}
		}
		if (browser != null) {
			Runtime.getRuntime().exec(
					new String[] { browser, authorizationUrl });
		} else {
			System.out.println(
					"Open the following address in your favorite browser:");
			System.out.println("  " + authorizationUrl);
		}
	}
	
	private Credential authorize(AuthorizationCodeReceiver receiver,
			String redirectUri, String api_key, String scope, String request_token) throws IOException {
		String code = receiver.waitForCode();
		AuthorizationCodeFlow codeFlow = new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(), HTTP_TRANSPORT,
				JSON_FACTORY, new GenericUrl(request_token),
				new ClientParametersAuthentication(api_key,	""),
				api_key, AUTHORIZATION_SERVER_URL)
				.setScopes(Arrays.asList(scope)).build();

		TokenResponse response = codeFlow.newTokenRequest(code)
				.setRedirectUri(redirectUri).setScopes(Arrays.asList(scope))
				.execute();
		
		// hardcode token
		//TokenResponse response = new TokenResponse().setAccessToken("gr8y4tm7canh34we6ug39c9z");
		
		return codeFlow.createAndStoreCredential(response, null);
	}

	public static void main(String[] args) throws Exception {
		/** TODO : You should fill your API KEY here. **/
		//CampaignSuiteApiExample example = new CampaignSuiteApiExample("dtsnwwknjr6zktmydhfyegc2");
		//CampaignSuiteApiExample example = new CampaignSuiteApiExample("9fn5jv5s8rbg9snwfknzr5nz"); // Willy key for 
		//CampaignSuiteApiExample example = new CampaignSuiteApiExample("a5fu9vzzcevtskngzjvu432w"); // AudienceTV key 
		CampaignSuiteApiExample example = new CampaignSuiteApiExample("672nwqwcv7k3xazb4z3czfpn"); // Dentsu Japan key 
		
		//private Metadata metadataApi = null;
		//private Metrics metricsApi = null;
		
		/**
		 * TODO : Please answer these two questions here. If you don't know the answers here,
		 * please feel free to contact us :
		 * 		api-support@turn.com
		 */
		boolean DO_YOU_HAVE_CAMPAIGN_MANAGEMENT_API_ACCESS = true;
		boolean DO_YOU_HAVE_CAMPAIGN_METRICS_API_ACCESS = false;
			
		
		if (DO_YOU_HAVE_CAMPAIGN_MANAGEMENT_API_ACCESS) {
			AdvertiserSummaryList metadatalist = example.getMetadataApi().advertisers().list()
					.setLimit(50)
					.setSortColum("advertiserId")
					.setStatus("playing")
					.setStart(0)
					.setSortOrder("asc")
					.execute();
			System.out.println("List of advertisers under market:");
			System.out.println(metadatalist.toPrettyString());
			
			Advertiser metadata1602693118 = example.getMetadataApi().advertisers().get(new Long(1602693118)).execute();
					
			System.out.println("Details for advertiser 1602693118:");
			System.out.println(metadata1602693118.toPrettyString());
			
			
		}
		
		
		
			/*
			
			// *** start Japan project
			// Advertiser: Japan Sports Council   ID: 1602693118
			// Campaign Product Name : TOTO Big   IO ID : 1602699332
	
			AdvertiserSummaryList metadatalist = example.getMetadataApi().advertisers().get(new Long(1602693118)).
			
			
			JAPANSPORTCOUNCIL.
			
			Insertionorders insertionOrders = metadata.insertionorders();                                               ©
	    	List<IOSummary> ioorders = insertionOrders.list().setStart(0).setLimit(10).execute().getItems();            ⓓ
	    	for(IOSummary order: ioorders) {                                                                            ⓔ
	    		System.out.println("IO Name :" + order.getName());
	    		System.out.println("IO Account Manager :" + order.getAccountManager());
	    	} 
			
			
			System.out.println("Size:" + JAPANSPORTCOUNCIL. getClassInfo());
			
			
			// *** end Japan project
			
		}
		*/
		
		
		if (DO_YOU_HAVE_CAMPAIGN_METRICS_API_ACCESS) {
			
			// get metrics for all advertisers
			//AdvertiserReportingDataList metricslist = example.getMetricsApi().advertisers().list("today")
			//		.execute();
			//System.out.println(metricslist.toPrettyString());
			
			// get metrics for Japan Sports Council
			IdList idList = new IdList();
			ArrayList<Long> list = new ArrayList<Long>();
			list.add(new Long(1602693118));
			idList.setId(list);
			
			AdvertiserReportingDataList metricsJapanSportsCouncil = example.getMetricsApi().advertisers().get("today", idList).execute();
			System.out.println("Campaign Metrics for Japan Sports Council");
			System.out.println(metricsJapanSportsCouncil.toPrettyString());
			
		}
	}
}