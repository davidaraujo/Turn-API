package com.turn.api.campaign_metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.turn.apis.metrics.v1.Metrics;
import com.turn.apis.metrics.v1.model.IdList;
import com.turn.apis.metrics.v1.model.InsertionOrderReportingDataList;

public class CampaignMetrics {

	private Metrics metricsApi = null;

	public CampaignMetrics(Metrics metricsApi) throws Exception  {
		this.metricsApi = metricsApi;
	}

	// *** ADVERTISERS


	// *** IOs
	// Return all the IOs performance for a specfic advertiser
		public void performanceDataByAdvertiserId(long advertiserId) {
			
			InsertionOrderReportingDataList metricsList = null;
			try {
				
				// choose the data range to use 
				metricsList = metricsApi.insertionorders().list("today", new Long(advertiserId)).execute();
				
				System.out.println("performanceDataByAdvertiserId :" + metricsList.toPrettyString());
				
			} catch (IOException e) {
				System.err.println("Error on performanceDataByAdvertiserId: " + e);
			}
		}

		
		// Return data for the specific advertiser id input under the market
		public void performanceDataByIOId(long ioId) {

			InsertionOrderReportingDataList insertionOrder = null;
			try {
				
				IdList idList = new IdList();
				List<Long> list = new ArrayList();
				list.add(ioId);
				idList.setId(list);
				
				// choose the data range to use 
				insertionOrder = metricsApi.insertionorders().get("today", idList).execute();
				
				System.out.println("performanceDataByIOId for IO id " + ioId + ":" + insertionOrder.toPrettyString());
			} catch (Exception e) {
				System.err.println("Error on performanceDataByIOId: " + e);
			}
		}
	

}
