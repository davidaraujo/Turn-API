package com.turn.api.projects;

import java.util.Hashtable;

import java.util.Scanner;
import com.turn.api.authentication.Authentication;
import com.turn.api.campaign_management.*;
import com.turn.api.config.JapanSportsConfig;
import com.turn.api.config.ReadConfigFile;
import com.turn.apis.metadata.v1.Metadata;
import com.turn.apis.metadata.v1.model.IdList;


public class JapanSportsCouncil {

	// metadata for all the APIs the market has access to
	Metadata campaignManagementMetadata;

	// classes that run API methods
	CampaignManagement campaignManagement;

	Hashtable<String, Hashtable<String, String>> tokens;
	Hashtable<String, IdList> lineItems;

	// 1.
	public JapanSportsCouncil(String configPath) throws Exception {

		// read authnetication config file
		ReadConfigFile configFile = new ReadConfigFile(configPath);
		tokens = configFile.getProperties();

		// read lineitems config file
		JapanSportsConfig japanSportsConfig = new JapanSportsConfig(configPath);
		lineItems = japanSportsConfig.getProperties();
	}

	// 2.
	// Get authentication to access CAMPAIGN MANAGEMENT APIs using user tokens
	void runAuthentication(int marketId) throws Exception {

		if (tokens.containsKey(marketId + "")) {

			Hashtable<String, String> marketConfig = tokens.get(marketId + "");

			if (marketConfig.containsKey("campaignManagementToken")) {
				String token = (String) marketConfig.get("campaignManagementToken");
				Authentication campaignManagementAuthentication = new Authentication(token);
				campaignManagementMetadata = campaignManagementAuthentication.getMetadataApi();
				//System.out.println("\nSetting up authentication for market " + marketId
				//		+ " to access Campaign management API ...\n");
			}
		}
	}

	// 4.
	public void runTask(int task) {

		try {
			if (task == 1) {
				pauseAll();
			} else if (task == 2) {
				CarryOver();
			} else if (task == 3) {
				NONCarryOver();
			}
		} catch (Exception e)

		{
			System.err.println("Error on runTask: " + e);
		}

	}

	private void pauseAll() {
		System.out.println("\nPausing all carry&non carry lineitems: " + lineItems.get("NON-Carry-Over") + lineItems.get("Carry-Over"));
		//campaignManagement.pauseMultipleLineItems(1602699332,lineItems.get("NON-Carry-Over"));
		//campaignManagement.pauseMultipleLineItems(1602699332,lineItems.get("Carry-Over"));
		System.out.println("\nAll done, thank you and see you next week!!! \n");
	}
	
	private void NONCarryOver() {
		System.out.println("\nPlaying NONCarryOver LineItems " + lineItems.get("NON-Carry-Over"));
		// campaignManagement.playMultipleLineItems(1602699332,
		// lineItems.get("NON-Carry-Over"));
		System.out.println("\nAll done, thank you and see you next week!!! \n");
	}

	// 5.
	private void CarryOver() {
		System.out.println("\nPlaying CarryOver LineItems " + lineItems.get("Carry-Over"));
		// campaignManagement.playMultipleLineItems(1602699332,
		// lineItems.get("Carry-Over"));
		System.out.println("\nAll done, thank you and see you next week!!! \n");
	}

	public static void main(String[] args) throws Exception {

		Scanner input = new Scanner(System.in);
		int option = 0;
		String confirm;
		JapanSportsCouncil jp = new JapanSportsCouncil(args[0]);

		System.out.print("\n\n --- Japan Sports Lottery Automation Project --- \n\n\n");

		System.out.print("Please choose from the following options:\n\n");
		System.out.print("1) Pause all line items\n");
		System.out.print("2) Play carry over line items\n");
		System.out.print("3) Play non carry over line items\n");
		System.out.print("\n>");

		option = input.nextInt();
		
		if (option != 1 & option != 2 & option != 3) {
			System.out.println("Invalid option, bye bye ...");
			System.exit(0);
		}
		
		System.out.println("\nAre you sure you want to run option " + option + " ? (y/n)\n");
		System.out.println(">");

		confirm = input.next();
		
		input.close();
		
		if (confirm.equals("y")) {
			jp.runAuthentication(328);
			jp.runTask(option);
		} else {
			System.out.println("Bye bye!!!");
		}
		System.exit(1);
	}
}