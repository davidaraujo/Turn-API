package com.turn.api.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

public class ReadConfigFile {

	String folderPath;
	

	public ReadConfigFile(String folderPath) {
		this.folderPath = folderPath;
	}

	public Hashtable<String, Hashtable<String, String>> getProperties() {
		
		// Hashtable with key as market Id and values as APIs tokens
		Hashtable<String, Hashtable<String, String>> tokens = new Hashtable<String, Hashtable<String, String>>();
		Properties prop = new Properties();
		String propFileName = "config.properties";
		String filePath = folderPath + "/" + propFileName;

		try {

			InputStream inputStream;

			inputStream = new FileInputStream(filePath);

			prop.load(inputStream);

			int i = 1;

			String marketName, marketId;
			String campaignManagementToken = "";
			String campaignMetricsToken = "";
			System.out.println("------------------- Config file load -------------------");
			while (prop.containsKey("config." + i + ".marketId")) {

				marketName = (String) prop.get("config." + i + ".marketName");
				marketId = (String) prop.get("config." + i + ".marketId");
				campaignManagementToken = (String) prop.get("config." + i + ".campaignManagementToken");
				campaignMetricsToken = (String) prop.get("config." + i + ".campaignMetricsToken");
				Hashtable<String, String> marketTokens = new Hashtable<String, String>();

				System.out.println("Properties found for market " + marketName + " with id " + marketId + ":");
				System.out.println("	-Campaign Management Token = " + campaignManagementToken);
				System.out.println("	-Campaign Metrics Token = " + campaignMetricsToken);

				if (campaignManagementToken != null) {
					marketTokens.put("campaignManagementToken", campaignManagementToken);
					tokens.put(marketId, marketTokens);
				}

				if (campaignMetricsToken != null) {
					marketTokens.put("campaignMetricsToken", campaignMetricsToken);
					tokens.put(marketId, marketTokens);
				}

				// clean variables
				marketName = "";
				marketId = "";
				campaignManagementToken = "";
				campaignMetricsToken = "";
				i++;
			}

			System.out.println("------------------- Config file load -------------------");
			return tokens;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // getClass().getClassLoader().getResourceAsStream(filePath);
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
