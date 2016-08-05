package com.turn.api.control;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import com.turn.api.authentication.Authentication;
import com.turn.api.campaign_management.*;
import com.turn.api.campaign_metrics.*;
import com.turn.api.config.ReadConfigFile;
import com.turn.apis.metadata.v1.Metadata;
import com.turn.apis.metrics.v1.Metrics;

public class ControlTower {

	// variable to hold all the market tokens in config file
	Hashtable<String, Hashtable<String, String>> tokens = new Hashtable<String, Hashtable<String, String>>();

	// metadata for all the APIs the market has access to
	Metadata campaignManagementMetadata;
	Metrics campaignMetricsMetadata;

	// classes that run API methods
	CampaignManagement campaignManagement;
	CampaignMetrics campaignMetrics;

	
	// pass config file path in class creation
	public ControlTower(String configPath) throws Exception {
	
		// read config file
		ReadConfigFile configFile = new ReadConfigFile(configPath);
		tokens = configFile.getProperties();
	}
	
	
	// *** Get authentication to access CAMPAIGN MANAGEMENT & CAMPAIGN METRICS APIs using user tokens
	void runAuthentication(int marketId) throws Exception {

		if (tokens.containsKey(marketId + "")) {

			Hashtable<String, String> marketConfig = tokens.get(marketId + "");

			if (marketConfig.containsKey("campaignManagementToken")) {
				String token = (String) marketConfig.get("campaignManagementToken");
				Authentication campaignManagementAuthentication = new Authentication(token);
				campaignManagementMetadata = campaignManagementAuthentication.getMetadataApi();
				System.out.println("\nSetting up authentication for market " + marketId
						+ " to access Campaign management API ...\n");
			}

			if (marketConfig.containsKey("campaignMetricsToken")) {
				String token = (String) marketConfig.get("campaignMetricsToken");
				Authentication campaignMetricsAuthentication = new Authentication(token);
				campaignMetricsMetadata = campaignMetricsAuthentication.getMetricsApi();
				System.out.println(
						"\nSetting up authentication for market " + marketId + " to access Campaign metrics API ...\n");
			}
		}
	}

	// [start] CAMPAIGN MANAGEMENT API
	private void runCampaignManagement() {
		campaignManagement = new CampaignManagement(campaignManagementMetadata);
	}

	private void summaryDataAllAdvertisers() {
		campaignManagement.summaryDataAllAdvertisers();
	}

	private void summaryDataSingleAdvertiser(long advertiserId) {
		campaignManagement.summaryDataSingleAdvertiser(advertiserId);
	}

	private void getInsertionOrdersList(long advertiserId) {
		campaignManagement.getInsertionOrdersList(advertiserId);
	}

	private void getInsertionOrder(long ioId) {
		campaignManagement.getInsertionOrder(ioId);
	}

	private void getInsertionOrderStatus(long ioId) {
		campaignManagement.getInsertionOrderStatus(ioId);
	}

	private void setInsertionOrderStatus(long ioId, String status) {
		campaignManagement.setInsertionOrderStatus(ioId, status);
	}
	// [end]
	
	// *** CAMPAIGN METRICS API

	private void runCampaignMetrics() throws Exception {
		campaignMetrics = new CampaignMetrics(campaignMetricsMetadata);
	}
	
	private void performanceDataByAdvertiserId(long advertiserId) {
		campaignMetrics.performanceDataByAdvertiserId(advertiserId);
	}
	
	private void summaryDataSingleInsertionOrder(long ioId) {
		campaignMetrics.performanceDataByIOId(ioId);
	}
	
	
	// [start] DISPLAY MENUS

	// 1 - Display all the markets from the config file for user to choose
	private void displayMarketsToChoose() {
		System.out.println("\n-- Markets --");

		System.out.println("Select market:");

		Enumeration marketNames = tokens.keys();
		int i = 1;

		while (marketNames.hasMoreElements())
			System.out.println(i++ + ") " + marketNames.nextElement());

	}
	
	// 2 - Display APIs for user to choose
	private void displayAPI() {
		System.out.println("-- API --");
		System.out.println(
				"Select an option: \n" + "  1) Campaign Management\n" + "  2) Campaign Metrics\n" + "  3) Exit\n");
		System.out.printf("$>");
	}
	
	// 3 - Display Campaign Management API options for user to choose
	
	private void displayCampaignManagement() {
		System.out.println("-- Campaign Management --");
		System.out.println("Select an option: \n" + "  1) Summary data for all advertisers\n"
				+ "  2) Summary data for single advertiser\n" 
				+ "  3) Insertion Order data by advertiser\n" 
				+ "  4) Insertion Order data by IO Id\n" 
				+ "  5) Insertion Order status by IO Id\n" 
				+ "  6) Update Insertion Order status by IO Id\n" 
				+ "  10) Exit\n");
		System.out.printf("$>");
	}
	
	// 3 - Display Campaign Metrics API options for user to choose
	
	private void displayCampaignMetrics() {
		System.out.println("-- Campaign Metrics --");
		System.out.println(
				"Select an option: \n" + "  1) Performance data by advertiser \n" + "  2) Performance data by IO \n" + "  10) Exit\n");
		System.out.printf("$>");
	}
	
	private void displayIdRequest(String desc) {
		System.out.println("Please enter "+ desc + ":\n");
		System.out.printf("$>");
	}

	private void exit() {
		System.out.println("Exiting ...");
		System.exit(1);
	}
	
	// [end]
	
	
	public static void main(String[] args) throws Exception {

		Scanner input = new Scanner(System.in);
		int selection = 0;
		int id = 0;
		String text = "";
		ControlTower control = new ControlTower(args[0]);

		try {

			// displace markets to choose from what's in config.properties
			control.displayMarketsToChoose();
			
			control.displayIdRequest("Market Id");
			
			// user inputs market id
			selection = input.nextInt();

			// create metadata for each API
			control.runAuthentication(selection);
			
			// display APIs available to choose
			control.displayAPI();

			// user inputs API to work on
			selection = input.nextInt();

			switch (selection) {
			case 1:
				control.runCampaignManagement();
				break;
			case 2:
				control.runCampaignMetrics();
				break;
			case 10:
				control.exit();
				break;	
			default:
				System.out.println("Invalid selection.");
				break;
			}

			// Campaign Management
			if (selection == 1) {

				while (true) {

					control.displayCampaignManagement();
					selection = input.nextInt();

					switch (selection) {
					case 1:
						control.summaryDataAllAdvertisers();
						break;
					case 2:
						control.displayIdRequest("Advertiser Id");
						id = input.nextInt();
						control.summaryDataSingleAdvertiser(id);
						break;
					case 3:
						control.displayIdRequest("Advertiser Id");	
						id = input.nextInt();
						control.getInsertionOrdersList(id);
						break;
					case 4:
						control.displayIdRequest("IO Id");	
						id = input.nextInt();
						control.getInsertionOrder(id);
						break;
					case 5:
						control.displayIdRequest("IO Id");	
						id = input.nextInt();
						control.getInsertionOrderStatus(id);
						break;
					case 6:
						control.displayIdRequest("IO Id");	
						id = input.nextInt();
						control.displayIdRequest("Status");	
						text = input.nextLine();
						control.setInsertionOrderStatus(id, text);
						break;
						
						
					case 10:
						control.exit();
						break;
					default:
						System.out.println("Invalid selection.");
						break;
					}
				}
			}

			// Campaign Metrics
			else if (selection == 2) {
				
				control.displayCampaignMetrics();
				selection = input.nextInt();

				switch (selection) {
				case 1:
					control.displayIdRequest("Advertiser Id");
					id = input.nextInt();
					control.performanceDataByAdvertiserId(id);
					break;
				case 2:
					control.displayIdRequest("IO Id");
					id = input.nextInt();
					control.summaryDataSingleInsertionOrder(id);
					break;
				case 10:
					control.exit();
					break;
				default:
					System.out.println("Invalid selection.");
					break;
				}
			}

		} catch (

		InputMismatchException e)

		{
			System.out.println("Please input number from the options above ...");
		} finally

		{
			input.close();
		}
	}
}