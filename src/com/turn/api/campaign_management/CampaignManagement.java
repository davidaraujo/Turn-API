package com.turn.api.campaign_management;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.turn.apis.metadata.v1.Metadata;
import com.turn.apis.metadata.v1.Metadata.Lineitems;
import com.turn.apis.metadata.v1.Metadata.Lineitems.Get;
import com.turn.apis.metadata.v1.Metadata.Lineitems.List;
import com.turn.apis.metadata.v1.Metadata.Packages.Play;
import com.turn.apis.metadata.v1.model.Advertiser;
import com.turn.apis.metadata.v1.model.AdvertiserSummaryList;
import com.turn.apis.metadata.v1.model.IOSummaryList;
import com.turn.apis.metadata.v1.model.IdList;
import com.turn.apis.metadata.v1.model.InsertionOrder;
import com.turn.apis.metadata.v1.model.LineItemSummaryList;
import com.turn.apis.metadata.v1.model.Lineitem;
import com.turn.apis.metadata.v1.model.MetadataPackage;
import com.turn.apis.metadata.v1.model.PackageSummaryList;

public class CampaignManagement {

	private Metadata metadataApi = null;

	public CampaignManagement(Metadata metadataApi)
	{
		this.metadataApi = metadataApi;
	}

	// [start] ADVERTISERS

	// Return data for all the advertisers under the market
	public void summaryDataAllAdvertisers() {

		AdvertiserSummaryList metadatalist = null;
		try {
			metadatalist = metadataApi.advertisers().list().setLimit(50).setSortColum("advertiserId")
					.setStatus("playing").setStart(0).setSortOrder("asc").execute();

			System.out.println("summaryDataAllAdvertisers :");
			System.out.println(metadatalist.toPrettyString());

		} catch (IOException e) {
			System.err.println("Error on summaryDataAllAdvertisers: " + e);
		}
	}

	// Return data for the specific advertiser id input under the market
	public void summaryDataSingleAdvertiser(long advertiserId) {

		Advertiser metadataAdvertiser = null;
		try {
			metadataAdvertiser = metadataApi.advertisers().get(new Long(advertiserId)).execute();

			System.out.println("summaryDataSingleAdvertiser for advertiser id " + advertiserId);
			System.out.println(metadataAdvertiser.toPrettyString());
		} catch (IOException e) {
			System.err.println("Error on summaryDataAllAdvertisers: " + e);
		}
	}

	private String editAdvertiserData() {
		return "";
	}
	// [end]

	// [start] INSERTION ORDERS

	// Return IOs data for a specfic advertiser
	public void getInsertionOrdersList(long advertiserId) {
		try {
			IOSummaryList metadatalist = metadataApi.insertionorders().list().execute(); //setAdvertiserId(new Long(advertiserId)).execute();
			System.out.println("getInsertionOrdersList for advertiser id " + advertiserId);
			System.out.println(metadatalist.toPrettyString());
		} catch (IOException e) {
			System.err.println("Error on getInsertionOrdersList: " + e);
		}
	}

	// Return IO by IO Id
	public void getInsertionOrder(long ioId) {
		try {
			InsertionOrder insertionOrder = metadataApi.insertionorders().get(new Long(ioId)).execute();
			System.out.println("getInsertionOrder for IO id " + ioId);
			System.out.println(insertionOrder.toPrettyString());
		} catch (IOException e) {
			System.err.println("Error on getInsertionOrder: " + e);
		}
	}

	// get IO Status by IO Id
	public void getInsertionOrderStatus(long ioId) {
		try {
			InsertionOrder insertionOrder = metadataApi.insertionorders().get(new Long(ioId)).execute();
			System.out.println("\ngetInsertionOrderStatus: IO id " + ioId + " is on status "
					+ insertionOrder.getClientStatusId() + "\n");
		} catch (IOException e) {
			System.err.println("Error on setInsertionOrderStatus: " + e);
		}
	}

	// set IO Status by IO Id and Status (play, pause, etc)
	public void setInsertionOrderStatus(long ioId, String status) {
		try {
			InsertionOrder insertionOrder = metadataApi.insertionorders().get(new Long(ioId)).execute();
			insertionOrder.setClientStatusId(status);
			System.out.println("\nsetInsertionOrderStatus: IO id " + ioId + " changed status to " + status);
		} catch (IOException e) {
			System.err.println("Error on setInsertionOrderStatus: " + e);
		}
	}

	// [end]

	// [start] PACKAGES

	// Return Package data by IO Id
	public PackageSummaryList getPackageList(long insertionorderId) {
		try {
			return metadataApi.packages().list(insertionorderId).execute();
			// System.out.println("getPackageList for IO id " +
			// insertionorderId);
			// System.out.println(metadatalist.toPrettyString());
		} catch (IOException e) {
			System.err.println("Error on getPackageList: " + e);
			return null;
		}
	}

	// Return IO by IO Id
	public MetadataPackage getPackage(long packageId) {
		try {
			return metadataApi.packages().get(new Long(packageId)).execute();
			// System.out.println("getPackage for Package id " + packageId);
			// System.out.println(pack.toPrettyString());
		} catch (IOException e) {
			System.err.println("Error on getPackage: " + e);
			return null;
		}
	}

	// get IO Status by IO Id
	public String getPackageStatus(long packageId) {
		try {
			MetadataPackage pack = metadataApi.packages().get(packageId).execute();

			System.out.println(
					"\ngetPackageStatus: Package id " + packageId + " is on status " + pack.getClientStatusId() + "\n");

			return pack.getClientStatusId();
		} catch (IOException e) {
			System.err.println("Error on getPackageStatus: " + e);
			return null;
		}
	}

	// play IO and Package
	public Boolean playPackage(long insertionOrderId, long packageId) {
		try {
			IdList idList = new IdList();
			java.util.List<Long> list = new ArrayList();
			list.add(packageId);
			idList.setIds(list);

			metadataApi.packages().play(insertionOrderId, idList).execute();

			System.out.println(
					"\nplayPackage: IO Id " + insertionOrderId + " and Package Id " + packageId + " set to play\n");
			return true;
		} catch (IOException e) {
			System.err.println("Error on playPackage: " + e);
			return false;
		}
	}

	// pause IO and Package
	public Boolean pausePackage(long insertionOrderId, long packageId) {
		try {
			IdList idList = new IdList();
			java.util.List<Long> list = new ArrayList();
			list.add(packageId);
			idList.setIds(list);

			metadataApi.packages().pause(insertionOrderId, idList).execute();

			System.out.println(
					"\npausePackage: IO Id " + insertionOrderId + " and Package Id " + packageId + " set to pause\n");
			return true;
		} catch (IOException e) {
			System.err.println("Error on pausePackage: " + e);
			return false;
		}
	}

	// [end]

	// [start] LINEITEMS
	public LineItemSummaryList getLineItemSummaryList(long packageId) {
		try {
			return metadataApi.lineitems().list(new Long(packageId)).execute();
		} catch (Exception e) {
			System.err.println("Error on getLineItemSummaryList: " + e);
		}
		return null;
	}

	public Lineitem getLineItem(long lineItemId) {
		try {
			return metadataApi.lineitems().get(new Long(lineItemId)).execute();
		} catch (Exception e) {
			System.err.println("Error on getLineItem: " + e);
			return null;
		}
	}

	// play lineitem
	public Boolean playLineItem(long insertionOrderId, long lineItemId) {
		try {
			IdList idList = new IdList();
			java.util.List<Long> list = new ArrayList();
			list.add(lineItemId);
			idList.setIds(list);

			metadataApi.lineitems().play(insertionOrderId, idList).execute();

			System.out.println(
					"\nplayLineItem: IO Id " + insertionOrderId + " and LineItem Id " + lineItemId + " set to play\n");

			return true;
		} catch (IOException e) {
			System.err.println("Error on playPackage: " + e);
			return false;
		}
	}

	// play lineitem
	public Boolean pauseLineItem(long insertionOrderId, long lineItemId) {
		try {
			IdList idList = new IdList();
			java.util.List<Long> list = new ArrayList();
			list.add(lineItemId);
			idList.setIds(list);

			metadataApi.lineitems().pause(insertionOrderId, idList).execute();

			System.out.println(
					"\npauseLineItem: IO Id " + insertionOrderId + " and LineItem Id " + lineItemId + " set to play\n");

			return true;
		} catch (IOException e) {
			System.err.println("Error on pauseLineItem: " + e);
			return false;
		}
	}

	// play multiple lineitems
	public Boolean playMultipleLineItems(long insertionOrderId, IdList lineItemsList) {
		try {
			metadataApi.lineitems().play(insertionOrderId, lineItemsList).execute();

			System.out.println("\nplayMultipleLineItems: IO Id " + insertionOrderId + " and LineItem Ids "
					+ lineItemsList.toPrettyString() + " set to play\n");

			return true;
		} catch (IOException e) {
			System.err.println("Error on playMultipleLineItems: " + e);
			return false;
		}
	}

	// pause multiple lineitems
	public Boolean pauseMultipleLineItems(long insertionOrderId, IdList lineItemsList) {
		try {
			metadataApi.lineitems().pause(insertionOrderId, lineItemsList).execute();

			System.out.println("\npauseMultipleLineItems: IO Id " + insertionOrderId + " and LineItem Ids "
					+ lineItemsList.toPrettyString() + " set to pause\n");

			return true;
		} catch (IOException e) {
			System.err.println("Error on pauseMultipleLineItems: " + e);
			return false;
		}
	}

	// [end]

	// *** BEACONS

	private String summaryDataAllBeacons() {
		return "";
	}

	private String summaryDataSingleBeacon() {
		return "";
	}

	private String updateSingleBeacon() {
		return "";
	}

	private String addNewBeacon() {
		return "";
	}

	// *** CONCEPTS

	private String summaryDataAllConcepts() {
		return "";
	}

	// *** CREATIVES

	private String summaryDataAllCreatives() {
		return "";
	}

	private String summaryDataSingleCreative() {
		return "";
	}

	private String updateSingleCreative() {
		return "";
	}

	private String addNewCreative() {
		return "";
	}

	// *** POWEREDITS
	private String exportPowerEdit() {
		return "";
	}

	private String importPowerEdit() {
		return "";
	}

	// *** SEGMENTS

	private String summaryDataAllSegments() {
		return "";
	}

}
