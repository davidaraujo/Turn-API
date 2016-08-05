package com.turn.api.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import com.turn.apis.metadata.v1.model.IdList;

public class JapanSportsConfig {

	String folderPath;
	

	public JapanSportsConfig(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public Hashtable<String, IdList> getProperties() {
		
		// Hashtable with key as market Id and values as APIs tokens
		Hashtable<String, IdList> lineItemsConfig = new Hashtable<String, IdList>();
		Properties prop = new Properties();
		String propFileName = "japanSports.properties";
		String filePath = folderPath + "/" + propFileName;

		try {

			InputStream inputStream;

			inputStream = new FileInputStream(filePath);

			prop.load(inputStream);

			int i = 1;

			String lineItemId;
			
			IdList nonCarryOrderIdList = new IdList();
			java.util.List<Long> nonCarryOrderList = new ArrayList();
			
			IdList carryOrderIdList = new IdList();
			java.util.List<Long> carryOrderList = new ArrayList();

			System.out.println("------------------- Japan Sports Config file load -------------------");
			
			int it = 1;
			while (prop.containsKey("NON-Carry-Over-" + it)) {
				lineItemId = (String) prop.get("NON-Carry-Over-" + it);
				//System.out.println("NON adding line item:" + lineItemId);
				nonCarryOrderList.add(new Long(lineItemId));
				it++;	
			}
			nonCarryOrderIdList.setIds(nonCarryOrderList);
			lineItemsConfig.put("NON-Carry-Over", nonCarryOrderIdList);
			
			it = 1;
			while (prop.containsKey("Carry-Over-" + it )) {
				lineItemId = (String) prop.get("Carry-Over-" + it);
				//System.out.println("CARRY adding line item:" + lineItemId);
				carryOrderList.add(new Long(lineItemId));
				it++;
			}
			carryOrderIdList.setIds(carryOrderList);
			lineItemsConfig.put("Carry-Over", carryOrderIdList);
			
			
			System.out.println("NON-Carry-Over: " + nonCarryOrderIdList);
			System.out.println("Carry-Over: " + carryOrderIdList);
			System.out.println("------------------- Config file load -------------------");
			
			return lineItemsConfig;
			
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
