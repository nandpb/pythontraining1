package com.ge.treasury.pfi.controller;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathExpression;
//import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Pankaj1.Tiwari
 *
 */
public class GetAlbanyWCBankEMap extends CreateDataBaseConnection {
	
	private static Logger logger = Logger.getLogger(GetAlbanyWCBankEMap.class);
	private static final String MY_PAYMENTS_OPTION_ID = "PFICORPMYPAY";
	private static final String ALSTOM_OPTION_ID_1 = "PFIALGRID";
	private static final String ALSTOM_OPTION_ID_2 = "PFIALPW";
	private static final String LMWIND_OPTION_ID = "LMWIND";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("--------------------------------");
		System.out.println("          STG 3");
		System.out.println("--------------------------------");
		String sourceFilePath = args[0];//"E:/IXPROCESS/CONVERT/webcashAlbanyPain001XML.xml";//
		logger.info("Input File Name....."+sourceFilePath);
		String outputFilePath = args[1];//"E:/IXPROCESS/CONVERT/webcashAlbanyPain001XMLxml.xml";//
		logger.info("Output File Name....."+outputFilePath);
		String processType = args[2];//"Pain";//
		logger.info("Processing File Type....."+processType);
		String optionId = args[3];
		logger.info("Option ID ["+ optionId + "]");
		String stg1File = args[4];
		logger.info("STG 1 File ["+ stg1File + "]");
		boolean myPayments = false;
		boolean LMWIND = false;
		boolean itsFlag = false;
		if(MY_PAYMENTS_OPTION_ID.equals(optionId)) {
			System.out.println("My Payments Logic");
			myPayments = true;
			itsFlag =true;
		}
		else if(optionId.startsWith(ALSTOM_OPTION_ID_1) || optionId.startsWith(ALSTOM_OPTION_ID_2)) {
			System.out.println("Alstom Logic");
			// Works with MyPayments Flag, if false then Alstom
			itsFlag = true;
		}
		else if(LMWIND_OPTION_ID.equals(optionId)) {
			System.out.println("LMWIND Logic");
			// Since will be using shared functionality with Alstom, create new flag
			LMWIND = true;
			itsFlag=true;
		}
		else  if (!itsFlag){
			System.out.println("Unkown Logic");
		}
		
		logger.info("Going into createXMLFromData method.....");
		String drBankId = new GetAlbanyWCBankEMap().getDrBankId(stg1File);
		logger.info("DR Bank ID " + drBankId);
		new GetAlbanyWCBankEMap().createXMLFormData(sourceFilePath, outputFilePath, processType, myPayments, LMWIND, drBankId);
		
	}

	/**
	 * 
	 * @param accountsXmlFilePath
	 * @param outputFilePath
	 */
	private void createXMLFormData(String accountsXmlFilePath, String outputFilePath,String processType, final boolean myPayments, final boolean LMWIND, final String drBankId) {
		logger.info("In createXMLFromData method.....");
		try {
			File fXmlFile = new File(accountsXmlFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			// Get properties
			logger.info("Reading dataProperties.properties files.....");
			// LOCAL
//			Properties dataProperties = getPropertiesFromFile("C:/IXPROCESS/CONVERT/dataProperties.properties");
			// DEV
			Properties dataProperties = getPropertiesFromFile("E:/IXPROCESS/CONVERT/dataProperties.properties");
			String creationModule = dataProperties.getProperty("creationModule");
			String active = dataProperties.getProperty("active");
			String status = dataProperties.getProperty("status");

			// create database connection
			logger.info("Creating connection.....");
			Connection conn = getDataBaseConnection();
			PreparedStatement preparedStmt = null;
			
			//get creditor bank address
			/*String sqlCreditorBankCode = "SELECT BANK_EMAP.*, LKCREATION_MODULE.CREATION_MODULE_ID AS [EXTERNAL MODEL ID], LKCREATION_MODULE.DESCRIPTION AS MODULE_DESC, count(*) over (partition by 1) total_rows "+
			"FROM [gewebcash].[dbo].[BANK_EMAP] INNER JOIN "+
			"LKCREATION_MODULE ON BANK_EMAP.CREATION_MODULE_INC = LKCREATION_MODULE.CREATION_MODULE_INC "+
			"where CREATION_MODULE_ID = ? AND bank_emap.ACTIVE = ? AND bank_emap.STATUS = ? ORDER BY WEIGHT ";*/
			          
			String sqlCreditorBankCode = "SELECT BANK_EMAP.*, MODEL_MAIN_2.MODELID AS [EXTERNAL MODEL ID],MODEL_MAIN_3.MODELID AS [INTERNAL MODEL ID],LKCREATION_MODULE.CREATION_MODULE_ID, LKCREATION_MODULE.DESCRIPTION AS MODULE_DESC, count(*) over (partition by 1) total_rows " +
					"FROM BANK_EMAP INNER JOIN LKCREATION_MODULE ON BANK_EMAP.CREATION_MODULE_INC = LKCREATION_MODULE.CREATION_MODULE_INC " +
					"LEFT JOIN MODEL_MAIN AS MODEL_MAIN_2 ON BANK_EMAP.EXTERNAL_MODELINC = MODEL_MAIN_2.MODELINC " +
					"LEFT JOIN MODEL_MAIN AS MODEL_MAIN_3 ON BANK_EMAP.INTERNAL_MODELINC = MODEL_MAIN_3.MODELINC " +
					"where CREATION_MODULE_ID=? AND  bank_emap.ACTIVE = ? AND bank_emap.STATUS = ? ORDER BY WEIGHT;";
			
			// create statement
			preparedStmt = conn.prepareStatement(sqlCreditorBankCode);
			preparedStmt.setString(1, creationModule);
			preparedStmt.setString(2, active);
			preparedStmt.setString(3, status);
			
			logger.info("SQL Query is....."+sqlCreditorBankCode);
			logger.info("Input paramteres for query..................");
			logger.info("CREATION_MODULE_ID :"+creationModule);
			logger.info("bank_emap.ACTIVE :"+active);
			logger.info("bank_emap.STATUS :"+status);
			
			//File/Transaction Data
			logger.info("Starting reading xml file.....");
			NodeList nList = doc.getElementsByTagName("Transaction");
			int txnLoopCount = 0;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				++txnLoopCount;
				logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				logger.info("Selecting "+ txnLoopCount +" Transation.....");
				
				Node transactionNode = nList.item(temp);
				transactionNode.getChildNodes();
				logger.info("Current Element :" + transactionNode.getNodeName());
				boolean internalFlag = false;
				// Validates if is INTERNAL or EXTERNAL before the logic overwrites the value
				if (transactionNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList txnNodeList = transactionNode.getChildNodes();
					for (int i = 0; i < txnNodeList.getLength(); i++) {
						Node txnNode = txnNodeList.item(i);
						if (txnNode.getNodeName().equals("#text")) {
							continue;
						}
						if(txnNode.getNodeName().equals("Model_Info")) {
							NodeList modelInfoNodeList = txnNode.getChildNodes();
							for (int modelInfoIndex = 0; modelInfoIndex < modelInfoNodeList.getLength(); modelInfoIndex++) {
								Node modelInfoNode = modelInfoNodeList.item(modelInfoIndex);
								if (modelInfoNode.getNodeName().equals("#text")) {
									continue;
								}
								if (modelInfoNode.getNodeName().equals("Model_ID")) {
									if(modelInfoNode.getTextContent().equals("INTERNAL")){
										internalFlag = true;
									}
								}
							}
						}
					}
				}
				//execute query
				logger.info("Executing Query.....");
				ResultSet rsCreditorBankCode = preparedStmt.executeQuery();
				//executing query one more time... 
				rsCreditorBankCode = preparedStmt.executeQuery();
				int ruleItrCount = 0;
				logger.info("Start to iterating loop.....");				
				endDBLoop: while (rsCreditorBankCode.next()) {
					logger.info("=======================================================================");
					String mapIDDB = rsCreditorBankCode.getString("MAP_ID");
					String transactionCurrencyCodeDB = rsCreditorBankCode.getString("TRN_CURRCODE");
					String urgentDB =  rsCreditorBankCode.getString("IS_URGENT");
					String operativeDB = rsCreditorBankCode.getString("OPERATIVE");
					String drBankCountryDB = rsCreditorBankCode.getString("DR_BANK_COUNTRY");
					String drAccountCurrencyCodeDB = rsCreditorBankCode.getString("DR_ACCT_CURRCODE");
					String crBankCountryDB = rsCreditorBankCode.getString("CR_BANK_COUNTRY");
					//String crAccountCurrencyCodeDB = rsCreditorBankCode.getString("CR_ACCT_CURRCODE");
					String validTransactionDB = rsCreditorBankCode.getString("TRANSFER_STATUS");
					String urgentIndDB = rsCreditorBankCode.getString("URGENT_IND");
					String drBankAddrDB = rsCreditorBankCode.getString("DR_BANK_ADDR");
					String crBankAddrDB = rsCreditorBankCode.getString("CR_BANK_ADDR");
					//String inBankAddrDB = "A";
					String externalModelIdDB = rsCreditorBankCode.getString("EXTERNAL MODEL ID");
					String internalModelIdDB = rsCreditorBankCode.getString("INTERNAL MODEL ID");
					String CodeType="";
					
					int rsCount = rsCreditorBankCode.getInt("total_rows");
					++ruleItrCount;
					
					//Initializing DB route code as S, if this is null/blank
					if(crBankAddrDB == null || crBankAddrDB.equals("")){
						logger.info("CR Address Type is null in DB/ So, setting as S");
						crBankAddrDB = "S";
					}
					//crBankAddrDB= "A";
					if (transactionNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) transactionNode;
						Node drCurrencyNode = null;
						Node drBankAddInfoChildNode = null;
						String dbtrCountry = "";
						String cdtrCountry = "";
					
						//creating rules.accountsXmlFilePath...............
						boolean ruleFlag = false;
						NodeList txnChildNodeList = transactionNode.getChildNodes();
						logger.info("Going to Check for "+ruleItrCount+" Rule for Map ID: "+mapIDDB);
						//column M & N
						logger.info("Check 1st for DR's Country & Currency.....");
						countryLoop: for (int i = 0; i < txnChildNodeList.getLength(); i++) {
							Node txnChildNode = txnChildNodeList.item(i);
							if (txnChildNode.getNodeName().equals("#text")) {
								continue;
							}
							if (txnChildNode.getNodeName().equals("Bank_Account")) {
								logger.info("ENH- Child Note its BANK_ACCOUNT.....");
								NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
								boolean checkDRFlag = false;
								for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
									Node bankAccChildNode = bankAccChildNodeList.item(j);
									if (bankAccChildNode.getNodeName().equals("#text")) {
										continue;
									}
									if (bankAccChildNode.getNodeName().equals("Bank_Account_Type") &&  bankAccChildNode.getTextContent().equals("DR")){
										logger.info("ENH- Bank Account Child Node its BANK_ACCOUNT_TYPE and value has DR.... CHECK DR FLAG = TRUE");
										checkDRFlag = true;
									}
									// DR Bank
									if (bankAccChildNode.getNodeName().equals("Bank") && checkDRFlag) {
										logger.info("ENH- Bank Child Node its BANK and DR FLAG its true.....");
										NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
										for (int k = 0; k < bankChildNodeList.getLength(); k++) {
											drBankAddInfoChildNode = bankChildNodeList.item(k);
											if (drBankAddInfoChildNode.getNodeName().equals("#text")) {
												continue;
											}
											if (drBankAddInfoChildNode.getNodeName().equals("Bank_Address_Info")) {
												logger.info("ENH- Bank Child Info node its BANK_ADDRESS_INFO.....");
												NodeList bankAddInfoNodeList = drBankAddInfoChildNode.getChildNodes();
												for (int l = 0; l < bankAddInfoNodeList.getLength(); l++) {
													Node bankAddInfoNode = bankAddInfoNodeList.item(l);
													if (bankAddInfoNode.getNodeName().equals("#text")) {
														continue;
													}
													if (bankAddInfoNode.getNodeName().equals("Country")) {
														logger.info("ENH- Bank Address Info node its COUNTRY.....");
														dbtrCountry = bankAddInfoNode.getTextContent();
														logger.info("DR Country in DataBase: "+drBankCountryDB+" DR Country in XML: "+dbtrCountry);
														if (drBankCountryDB == null) {
															ruleFlag = true;
															logger.info("DR Country in DataBase is null. So, check is pass");
														}else{
															if (dbtrCountry.equals(drBankCountryDB)) {
																ruleFlag = true;
																logger.info("ENH- Bank Node Info matches the value  IN DB.....");
																logger.info("1st check is done successfully.... ");
																logger.info("Go and check other one.... ");
															} else {
																ruleFlag = false;
																logger.info("ENH- Bank Node info does not match value in DB.....");
																logger.info("1st check fail.... ");
																logger.info("So I'll not check other one....... ");
															} 
														}
													}
													if (bankAddInfoNode.getNodeName().equals("Currency")) {
														drCurrencyNode = bankAddInfoNode;
														if (ruleFlag) {
															logger.info("ENH- Bank Node Info its CURRENCY and ruleFLAG enabled.....");
															logger.info("I am in DR currency.");
															logger.info("DR Currency in DataBase: "+drAccountCurrencyCodeDB+" DR Currency in XML: "+bankAddInfoNode.getTextContent());
															if (drAccountCurrencyCodeDB == null) {
																ruleFlag = true;
																logger.info("ENH- Curreny code in DB is NULL enable rule FLAG.....");
																logger.info("DR Currency in DataBase is null. So, check is pass. ");
															} else {
																if (drAccountCurrencyCodeDB.equals(bankAddInfoNode.getTextContent())) {
																	ruleFlag = true;
																	logger.info("ENH- Currency Code in DB matches the one in XML.....");
																	logger.info("DR Currency check is done successfully.... ");
																	logger.info("Go and check other one.... ");
																}
															}
														} else {
															//logger.info("1st Check was fail. But still I have to remove DR currency tag....");
														}
														break countryLoop;
													}
												}
											}
										}
									}
								}
							}
						}
					
						//breakIfBlock:
						// File/Transaction/Amounts/Transaction_Amount/Currency Data........
						//column G
						if (ruleFlag) {
							logger.info("ENH- Check for Transaction Curreny.....");
							logger.info("Check 2nd for Transaction Curency Code.....");
							String currency = eElement.getElementsByTagName("Currency").item(0).getTextContent();
							logger.info("Transaction Currency in DataBase: "+transactionCurrencyCodeDB+" Transaction Currency in XML: "+currency);
							if (transactionCurrencyCodeDB == null) {
								logger.info("Transaction Currency in DataBase is null. So, this is pass in check ");
								ruleFlag = true;
							} else {
								if (currency.equalsIgnoreCase(transactionCurrencyCodeDB)) {
									ruleFlag = true;
									logger.info("ENH- Currency code IN XML matches Currency code in DB, FLAG=TRUE.....");
									logger.info("2nd check is done successfully.... ");
									logger.info("Go and check other one.... ");
								} else {
									ruleFlag = false;
									logger.info("ENH- Currency code in XML does not matches in DB, FLAG=FALSE.....");
									logger.info("2nd check fail.... ");
									logger.info("So I'll not check other one....... ");
								}
							}
						}
						
						//File/Transaction/Transaction_Header/Urgent Data...........
						//column H
						if (ruleFlag) {
							logger.info("ENH- Check for URGENT TAG .....");
							logger.info("Check 3rd for Urgent.....");
							String urgent = eElement.getElementsByTagName("Urgent").item(0).getTextContent();
							logger.info("Urgent in DataBase: "+urgentDB+" Urgent in XML: "+urgent);
							if (urgentDB == null || urgentDB.equalsIgnoreCase("B")) {
								ruleFlag = true;
								logger.info("urgent in DB is null or B. So, check is pass");
							} else {
								if (urgent.equalsIgnoreCase(urgentDB)) {
									ruleFlag = true;
									logger.info("ENH- URGENT TAG in XML matches URGENT TAG in DB, FLAG=TRUE.....");
									logger.info("3rd check is done successfully.... ");
									logger.info("Go and check other one.... ");
								} else {
									ruleFlag = false;
									logger.info("ENH- URGENT TAG in XML NOT matches URGENT TAG in DB, FLAG=FALSE.....");
									logger.info("3rd check fail.... ");
									logger.info("So I'll not check other one....... ");
								}
							}
						}
					
						//File/Transaction/Bank_Account/Bank_Account_Type Data....
						String bankAccountTypeDR = eElement.getElementsByTagName("Bank_Account_Type").item(0).getTextContent();
						String bankAccountTypeCR = eElement.getElementsByTagName("Bank_Account_Type").item(1).getTextContent();
					
						/*	String bankAccountTypeIN="";
						if (eElement.getElementsByTagName("Bank_Account_Type").item(2) != null) {
					       	bankAccountTypeIN = eElement.getElementsByTagName("Bank_Account_Type").item(2).getTextContent();
						}
						*/	
						
						//Column I
						//for DR....checking Account_Id exist or not??
						//accountIdLoop:
						if (ruleFlag) {
							logger.info("ENH- Check for BANK_ACCOUNT_TYPE DR.....");
							logger.info("Check 4th for DR's Operative.....");
							if (bankAccountTypeDR.substring(0, 1).equals(operativeDB)) {
								ruleFlag = true;
								logger.info("ENH- BANK_ACCOUNT_TYPE in XML matches DB value, FLAG=TRUE.....");
								logger.info("4th check is done successfully.... ");
								logger.info("Go and check other one.... ");
							} else {
								ruleFlag = false;
								logger.info("ENH- BANK_ACCOUNT_TYPE in XML NOT matches DB value, FLAG=FALSE.....");
								logger.info("4th check fail.... ");
								logger.info("So I'll not check other one....... ");
							}
						}
						
						//column R
						if (ruleFlag) {
							logger.info("ENH- Check for BANK_ACCOUNT_TYPE CR.....");
							logger.info("Check 5th for CR's Country.....");
							crCountryLoop: for (int i = 0; i < txnChildNodeList.getLength(); i++) {
								Node txnChildNode = txnChildNodeList.item(i);
								if (txnChildNode.getNodeName().equals("#text")) {
									continue;
								}
								if (txnChildNode.getNodeName().equals("Bank_Account")) {
									NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
									boolean checkCRFlag = false;
									for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
										Node bankAccChildNode = bankAccChildNodeList.item(j);
										if (bankAccChildNode.getNodeName().equals("#text")) {
											continue;
										}
										if (bankAccChildNode.getNodeName().equals("Bank_Account_Type") && bankAccChildNode.getTextContent().equals("CR")) {
											logger.info("ENH- IF Node equals BANK_ACCOUNT_TYPE and Child Node value is CR set CheckCRFLAG =TRUE.....");
											checkCRFlag = true;
										}
										if (bankAccChildNode.getNodeName().equals("Bank") && checkCRFlag) {
											NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
											//removeChildLoop:
											for (int k = 0; k < bankChildNodeList.getLength(); k++) {
												Node bankChildNode = bankChildNodeList.item(k);
												if (bankChildNode.getNodeName().equals("#text")) {
													continue;	
												}
												if (bankChildNode.getNodeName().equals("Bank_Address_Info")) {
													NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
													for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
														Node bankRouterChildNode = bankRouterChildNodeList.item(l);
														if (bankRouterChildNode.getNodeName().equals("#text")) {
															continue;
														}
														if (bankRouterChildNode.getNodeName().equals("Country")) {
															cdtrCountry = bankRouterChildNode.getTextContent();
															logger.info("ENH- If Node matches BANK_ADDRESS_INFO and Node Value = Country.....");
															logger.info("CR Country in DataBase: "+crBankCountryDB+" CR Country in XML: "+cdtrCountry);
															if (crBankCountryDB == null) {
																ruleFlag = true;
																logger.info("ENH- Country CR is null, FLAG=TRUE.....");
																logger.info("CR Country in DataBase is null. So, check is pass.");
																break crCountryLoop; 
															} else {
																if (bankRouterChildNode.getTextContent().equals(crBankCountryDB)) {
																	ruleFlag = true;
																	logger.info("ENH- IF Country CR in XML matches Country CR in database, FLAG=TRUE.....");
																	logger.info("5th check is done successfully.... ");
																	logger.info("Go and check other one.... ");
																	break crCountryLoop;
																} else {
																	ruleFlag = false;
																	logger.info("ENH- IF Country CR in XML NOT matches  Country CR in database, FLAG=FALSE.....");
																	logger.info("5th check fail.... ");
																	logger.info("So I'll not check other one....... ");
																	break crCountryLoop;
																} 
															}
														 //break removeChildLoop;
														}
													}
												}
											}
										}
									}
								}	 
							}
						}
						
//						// TODO New Rule
						if(ruleFlag) {
							logger.info("Check 6th for Bank ID.....");
							if(mapIDDB.equalsIgnoreCase("ALSTOM ALL")) {
								logger.info("ALSTOM ALL rule skip validateMapId");
								ruleFlag = true;
							} else {
								ruleFlag = validateMapId(drBankId, mapIDDB);
							}
						}
//						//checking transfer status as V(valid)...
						boolean modelIdAssigned = false;
						if (ruleFlag) {
							logger.info("All checks match successfully........proccedding for value modification in xml");
							logger.info("Now, Checking Transaction status.....");
							if (validTransactionDB.equals("V")) {
								logger.info("ENH- TRANSFER_STATUS in DB equals V, FLAG=TRUE.....");
								logger.info("Transaction Status is Valid.....");
								//column X
								if (bankAccountTypeCR.equals("CR")) {
									logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
									logger.info("                     CR");
									logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
									removeChildLoop: for (int i = 0; i < txnChildNodeList.getLength(); i++) {
										Node txnChildNode = txnChildNodeList.item(i);
										if (txnChildNode.getNodeName().equals("#text")) {
											continue;
										}
										if (txnChildNode.getNodeName().equals("Bank_Account")) {
											NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
											boolean checkCRFlag = false;
											for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
												Node bankAccChildNode = bankAccChildNodeList.item(j);
												if(bankAccChildNode.getNodeName().equals("#text")){
													continue;
												}
												if(bankAccChildNode.getNodeName().equals("Bank_Account_Type") && bankAccChildNode.getTextContent().equals("CR") ){
													logger.info("ENH- Node matches BANK_ACCOUNT_TYPE and Child Node value CR, CR FLAG=TRUE.....");
													checkCRFlag = true;
												}
												if(bankAccChildNode.getNodeName().equals("Bank") && checkCRFlag) {
													NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
													Node swiftBankRouteCodeNode = null;
													// Determine how many bank routes for Alstom
													int bankRouteCodes = 0;
													if(!myPayments) {
														for(int bankIndex = 0; bankIndex < bankChildNodeList.getLength(); bankIndex++) {
															Node bankNode = bankChildNodeList.item(bankIndex);
															if(bankNode.getNodeName().equals("#text")){
																continue;
															} else if (bankNode.getNodeName().equals("Bank_Route_Code")){
																bankRouteCodes++;
															}
														}
													}
													for (int k = 0; k < bankChildNodeList.getLength(); k++) {
														Node bankChildNode = bankChildNodeList.item(k);
														if(bankChildNode.getNodeName().equals("#text")){
															continue;
														}
														if(bankChildNode.getNodeName().equals("Bank_Route_Code")){
															NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
															//for loop for bank route code.........
															//breakBankRouteCode:
															for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
																Node bankRouterChildNode = bankRouterChildNodeList.item(l);
																if(bankRouterChildNode.getNodeName().equals("#text")){
																	continue;
																}
																if(processType.equals("MT")){
																	logger.info("Processing MT file......");
																	logger.info("ENH- BANK_ROUTE_CODE is MT.....");
																	if(bankRouterChildNode.getNodeName().equals("Code_Type") && bankRouterChildNode.getTextContent().equals("S")){
																		logger.info("Found Code_Type as S. So, this will remain same as in input file");
																		logger.info("Going to set Model ID, DR Bank Address Type......as hardCoded");
																		changeTagValuesOnMTYes(eElement, bankAccountTypeDR, externalModelIdDB,internalModelIdDB, myPayments);
																		break removeChildLoop;
																	} else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
																			(bankRouterChildNode.getTextContent().equals("") || 
																					!bankRouterChildNode.getTextContent().equals("S"))) {
																		logger.info("setting Code_type as whatever in DB because Code_Type is not as S in input file....");
																		Node codtTypeNode = eElement.getElementsByTagName("Code_Type").item(1);
																		codtTypeNode.setTextContent(crBankAddrDB);
																		logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......");
																		changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																		break removeChildLoop;
																	}
																} else {
																	//FIXME CR Logic not working
																	if (myPayments) {
																		if(bankRouterChildNode.getNodeName().equals("#text")){
																			continue;
																		}
																		if (bankRouterChildNode.getNodeName().equals("Code_Type")) {
																			
																			if(!modelIdAssigned) {
																				changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																				modelIdAssigned = true;
																			}
																			logger.info("Mapping Rule [" + crBankAddrDB + "], Input [" + bankRouterChildNode.getTextContent() + "]");
//																			logger.info("Processing Pain file......");
																			// Remove nodes that doesn't match to Map Rule
																			if(!bankRouterChildNode.getTextContent().equals(crBankAddrDB)) {
																				logger.info("Removing the BANK_ROUTE_CODE that NOT match with Map Rule");
																				bankChildNode.getParentNode().removeChild(bankChildNode);
																			} else {
																				logger.info("Kepping the BANK_ROUTE_CODE that match with Map Rule");
																				continue;
																			}
																		}
																	 } else {
																		if(bankRouterChildNode.getNodeName().equals("#text")){
																			continue;
																		}
																		//logger.info("Processing Pain file......");
																		//if we have only one Bank_Route_Code, then no need to check with db
																		//FIXME - Condition reverted to use the 2016 logic.
																		//if (bankRouteCodes <= 1 && bankRouterChildNode.getNodeName().equals("Code_Type")) {
																		if(bankChildNodeList.getLength()<6){
																			//do nothing
																			logger.info("We have only one Bank_Route_Code.....");
																			logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......as hardCoded");
																			//changeTagValuesOnMTYes(eElement, bankAccountTypeDR, externalModelIdDB, internalModelIdDB, myPayments);
																			logger.info("if Local assing Map Rulte Value");
																			if(bankRouterChildNode.getTextContent().equals("")) {
																				bankRouterChildNode.setTextContent(crBankAddrDB);
																			}
																			changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																			break removeChildLoop;
																		} else {
																			logger.info("Mapping Rule [" + crBankAddrDB + "], Input [" + bankRouterChildNode.getTextContent() + "]");
																			if(bankRouterChildNode.getNodeName().equals("Code_Type") && bankRouterChildNode.getTextContent().equals("S") && crBankAddrDB.equals("S")){
																				logger.info("Found Code_Type as S in XML and also in DB. So, this will remain same...");
																				logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
																				changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																				continue;
																			} else if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
																					(bankRouterChildNode.getTextContent().equals("") || !bankRouterChildNode.getTextContent().equals("S")) &&
																					crBankAddrDB.equals("S")){
																				logger.info("Removing Bank_Route_Code for Code_Type which is not S....");
																				bankAccChildNode.removeChild(bankChildNode);
																				break removeChildLoop;
																			} else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
																						bankRouterChildNode.getTextContent().equals("S") &&
																						!crBankAddrDB.equals("S")) {
																				logger.info("Code_type is S in XML but not in DB....");
																				swiftBankRouteCodeNode = bankChildNode;
																			} else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
																					(bankRouterChildNode.getTextContent().equals("") || !bankRouterChildNode.getTextContent().equals("S"))
																					&&	!crBankAddrDB.equals("S")) {
																				Node bankRouterChildNode1 = bankRouterChildNodeList.item(l+2);
																				if(bankRouterChildNode1.getNodeName().equals("Code") &&  bankRouterChildNode1.getTextContent().equals("")){
																					logger.info("Code is blank....So, we'll keep Swift Bank_Route_Code.");
																					bankAccChildNode.removeChild(bankChildNode);
																					logger.info("Local Bank_Route_Code has deleted.....");
																					logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
																					changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																					break removeChildLoop;
																				} else {
																					logger.info("Code is not blank....So, we'll keep local Bank_Route_Code.");
																					logger.info("Setting local Bank_Route_Code from DB.");
																					bankRouterChildNode.setTextContent(crBankAddrDB);
																					bankAccChildNode.removeChild(swiftBankRouteCodeNode);
																					logger.info("Swift Bank_Route_Code has deleted.....");
																					logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
																					changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																					break removeChildLoop;
																				}
																			}
																		}
																	}// MyPayments / Alstom Logic
																}// else MT FILE
															}// for bankRouterChildNodeList
														}
													}
												}
											}
										}
									}
								}// if CR
								if(bankAccountTypeDR.equals("DR")){
									logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
									logger.info("                     DR");
									logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
									removeChildLoop: for (int i = 0; i < txnChildNodeList.getLength(); i++) {
										Node txnChildNode = txnChildNodeList.item(i);
										if(txnChildNode.getNodeName().equals("#text")){
											continue;
										}
										if(txnChildNode.getNodeName().equals("Bank_Account")){
											NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
											boolean checkDRFlag = false;
											for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
												Node bankAccChildNode = bankAccChildNodeList.item(j);
												if (bankAccChildNode.getNodeName().equals("#text")) {
													continue;
												}
												if (bankAccChildNode.getNodeName().equals("Bank_Account_Type") && bankAccChildNode.getTextContent().equals("DR")) {
													logger.info("ENH- Node matches BANK_ACCOUNT_TYPE and Child Node value DR, DR FLAG=TRUE.....");
													checkDRFlag = true;
												}
												if (bankAccChildNode.getNodeName().equals("Bank") && checkDRFlag) {
													NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
													Node swiftBankRouteCodeNode = null;
													int bankRouteCodes = 0;
													if(!myPayments) {
														for(int bankIndex = 0; bankIndex < bankChildNodeList.getLength(); bankIndex++) {
															Node bankNode = bankChildNodeList.item(bankIndex);
															if(bankNode.getNodeName().equals("#text")){
																continue;
															} else if (bankNode.getNodeName().equals("Bank_Route_Code")){
																bankRouteCodes++;
															}
														}
													}
													for (int k = 0; k < bankChildNodeList.getLength(); k++) {
														Node bankChildNode = bankChildNodeList.item(k);
														if(bankChildNode.getNodeName().equals("#text")){
															continue;
														}
														if(bankChildNode.getNodeName().equals("Bank_Route_Code")){
															NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
															for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
																Node bankRouterChildNode = bankRouterChildNodeList.item(l);
																if(bankRouterChildNode.getNodeName().equals("#text")){
																	continue;
																}
																if(myPayments) {
																	//FIXME CR Logic not working
																	if (bankRouterChildNode.getNodeName().equals("Code_Type")) {
																		logger.info("Mapping Rule [" + drBankAddrDB + "], Input [" + bankRouterChildNode.getTextContent() + "]");
																		if(!modelIdAssigned) {
																			changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
																			modelIdAssigned = true;
																		}
																		// Remove nodes that doesn't match to Map Rule
																		if(!bankRouterChildNode.getTextContent().equals(drBankAddrDB)) {
																			logger.info("Removing the BANK_ROUTE_CODE that NOT match with Map Rule");
																			bankChildNode.getParentNode().removeChild(bankChildNode);
																			modelIdAssigned = true;
																		} else {
																			logger.info("Kepping the BANK_ROUTE_CODE that match with Map Rule");
																			modelIdAssigned = true;
																			continue;
																		}
																	}
																} else {
//																	//logger.info("Processing Pain file......");
//																	//if we have only one Bank_Route_Code, then no need to check with db
//																	if (bankRouteCodes <= 1) {
//																		//do nothing
//																		logger.info("We have only one Bank_Route_Code.....");
//																		logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......as hardCoded");
//																		changeTagValuesOnMTYes(eElement, bankAccountTypeDR, externalModelIdDB, internalModelIdDB, myPayments);
//																		//changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB);
//																		break removeChildLoop;
//																	} else {
//																		logger.info("Mapping Rule [" + drBankAddrDB + "]");
//																		if(bankRouterChildNode.getNodeName().equals("Code_Type") && bankRouterChildNode.getTextContent().equals("S") && drBankAddrDB.equals("S")){
//																			logger.info("Found Code_Type as S in XML and also in DB. So, this will remain same...");
//																			logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
//																			changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
//																			continue;
//																		} else if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
//																				(bankRouterChildNode.getTextContent().equals("") || !bankRouterChildNode.getTextContent().equals("S")) &&
//																				drBankAddrDB.equals("S")){
//																			logger.info("Removing Bank_Route_Code for Code_Type which is not S....");
//																			bankAccChildNode.removeChild(bankChildNode);
//																			break removeChildLoop;
//																		} else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
//																					bankRouterChildNode.getTextContent().equals("S") &&
//																					!drBankAddrDB.equals("S")) {
//																			logger.info("Code_type is S in XML but not in DB....");
//																			swiftBankRouteCodeNode = bankChildNode;
//																		} else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
//																				(bankRouterChildNode.getTextContent().equals("") || !bankRouterChildNode.getTextContent().equals("S"))
//																				&&	!drBankAddrDB.equals("S")) {
//																			Node bankRouterChildNode1 = bankRouterChildNodeList.item(l+2);
//																			if(bankRouterChildNode1.getNodeName().equals("Code") &&  bankRouterChildNode1.getTextContent().equals("")){
//																				logger.info("Code is blank....So, we'll keep Swift Bank_Route_Code.");
//																				bankAccChildNode.removeChild(bankChildNode);
//																				logger.info("Local Bank_Route_Code has deleted.....");
//																				logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
//																				changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
//																				break removeChildLoop;
//																			} else {
//																				logger.info("Code is not blank....So, we'll keep local Bank_Route_Code.");
//																				logger.info("Setting local Bank_Route_Code from DB.");
//																				bankRouterChildNode.setTextContent(drBankAddrDB);
//																				bankAccChildNode.removeChild(swiftBankRouteCodeNode);
//																				logger.info("Swift Bank_Route_Code has deleted.....");
//																				logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
//																				changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB, myPayments);
//																				break removeChildLoop;
//																			}
//																		}
//																	}
																} // My Payments / Alstom Logic
															}// Bank Route Code Childs loop
														} // Bank Route Code loop
													}
												}// Bank and DRflag
											} 
										}// if Bank_Account
									}
								}//if DR
								logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
						/*	if(bankAccountTypeIN.equals("IN") ){
								removeChildLoop:
								for (int i = 0; i < txnChildNodeList.getLength(); i++) {
									 Node txnChildNode = txnChildNodeList.item(i);
									 if(txnChildNode.getNodeName().equals("#text")){
										 continue;
									 }
									 if(txnChildNode.getNodeName().equals("Bank_Account")){
										 NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
										 boolean checkINFlag = false;
										 for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
											 Node bankAccChildNode = bankAccChildNodeList.item(j);
											 if(bankAccChildNode.getNodeName().equals("#text")){
												 continue;
											 }
											 if(bankAccChildNode.getNodeName().equals("Bank_Account_Type") && 
													 bankAccChildNode.getTextContent().equals("IN") ){
												 //logger.info("IN-checkINFlag  = YES.....");
												 checkINFlag = true;
											 }
											 if(bankAccChildNode.getNodeName().equals("Bank") && 
													 checkINFlag){
												// logger.info("IN-Get bankChildNodes.....");
												 NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
												 Node swiftBankRouteCodeNode = null;
												 
												 for (int k = 0; k < bankChildNodeList.getLength(); k++) {
													 Node bankChildNode = bankChildNodeList.item(k);
													 //logger.info("IN-Get bankChildNodes Node name value....."+ bankChildNode.getNodeName() );
													 //logger.info("IN-Get bankChildNodes Node value....."+ bankChildNode.getNodeValue() );
													  if(bankChildNode.getNodeName().equals("#text")){
														 continue;
													 }
													 if(bankChildNode.getNodeName().equals("Bank_Route_Code")){
														 NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
														 //for loop for bank route code.........
														 //breakBankRouteCode:
														 for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
															 Node bankRouterChildNode = bankRouterChildNodeList.item(l);
															// logger.info("IN-Get bankRouterChildNodeList Node name value....."+ bankRouterChildNode.getNodeName() );
															// logger.info("IN-Get bankRouterChildNodeList Node value....."+ bankRouterChildNode.getNodeValue() );
															 if(bankRouterChildNode.getNodeName().equals("#text")){
																 continue;
															 }
														
																logger.info("IN-Processing Pain file with INTERMEDIARY BANK......");
																 if(bankChildNodeList.getLength()<6){
																	 //do nothing
																	 logger.info("IN-We have only one Bank_Route_Code.....");
																	 logger.info("IN-Going to set DR Bank Address Type, Urgent Indicator, Model ID......as hardCoded");
																	 changeTagValuesOnMTYes(eElement, bankAccountTypeDR, externalModelIdDB,internalModelIdDB);
																	 //changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB);
																	 break removeChildLoop;
																 }else{
																
																	 if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
																			 bankRouterChildNode.getTextContent().equals("S") &&
																			 inBankAddrDB.equals("S")){
																		// logger.info("IN-Found Code_Type as S in XML and also in DB. So, this will remain same...");
																		// logger.info("IN-Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
																		// changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB);
																		 continue;
																	 }else if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
																			 (bankRouterChildNode.getTextContent().equals("") || !bankRouterChildNode.getTextContent().equals("S")) &&
																			 inBankAddrDB.equals("S")){
																			 logger.info("IN-Removing Bank_Route_Code for Code_Type which is not S....");
																			 bankAccChildNode.removeChild(bankChildNode);
																			 break removeChildLoop;
																	 }else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
																			 	bankRouterChildNode.getTextContent().equals("S") &&
																			 	!inBankAddrDB.equals("S")) {
																		 logger.info("IN-Code_type is S in XML but not in DB....");
																		 swiftBankRouteCodeNode = bankChildNode;
																	 }else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
																			 (bankRouterChildNode.getTextContent().equals("") || !bankRouterChildNode.getTextContent().equals("S"))
																			 &&	!inBankAddrDB.equals("S")) {
																		 
																		 Node bankRouterChildNode1 = bankRouterChildNodeList.item(l+2);
																		 if(bankRouterChildNode1.getNodeName().equals("Code") && 
																				 bankRouterChildNode1.getTextContent().equals("")){
																			 logger.info("IN-Code is blank....So, we'll keep Swift Bank_Route_Code.");
																			 bankAccChildNode.removeChild(bankChildNode);
																			 logger.info("IN-Local Bank_Route_Code has deleted.....");
																		
																		//	 logger.info("Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
																		//	 changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB);
																			
																			 break removeChildLoop;
																		 }else{
																			 logger.info("IN-Code is not blank....So, we'll keep local Bank_Route_Code.");
																			 
																			
																				 bankRouterChildNode.setTextContent(inBankAddrDB);
																				 bankAccChildNode.removeChild(swiftBankRouteCodeNode);
																				 logger.info("IN-Swift Bank_Route_Code has deleted.....");
																			// logger.info("IN-Going to set DR Bank Address Type, Urgent Indicator, Model ID......from DB");
																			// changeTagValuesfromDB(eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB);
																			
																			 break removeChildLoop;
																		 }
																		 
																	 } 
																 }																 
															// }
														}
													 }
												}
											 }
										 }
									 }	 
								}
							} // IF IN type
							
							*/
							
							
							logger.info("Breaking rule loop. Beacuse selected "+mapIDDB+" rule check successfully.");
							//	removing DR Currency tag
							logger.info("I have to remove DR currency tag....");
							drBankAddInfoChildNode.removeChild(drCurrencyNode);
							logger.info("DR Currency tag is removed.... ");
							break endDBLoop;
							} else { 
								logger.info("Transation status is not valid........");						
								//removing DR Currency tag
								if(rsCount == ruleItrCount){
									logger.info("Still need to remove IN bank Route Code........");
									removeINBankRouteCode(eElement, bankAccountTypeCR, txnChildNodeList, processType);
									logger.info("Moving to next transaction; if available ........");
									
									logger.info("Still need to remove CR bank Route Code........");
									removeCRBankRouteCode(eElement, bankAccountTypeCR, txnChildNodeList, processType);
									logger.info("Moving to next transaction; if available ........");
									
									logger.info("I have to remove DR currency tag....");
									drBankAddInfoChildNode.removeChild(drCurrencyNode);
									logger.info("DR Currency tag is removed.... ");
								}
								break endDBLoop;
							}
						} else {
							logger.info("All Checks are fail........");
							//removing DR Currency tag
							if(rsCount == ruleItrCount){
								logger.info("But, Still need to remove CR bank Route Code........");
								removeCRBankRouteCode(eElement, bankAccountTypeCR, txnChildNodeList, processType);
								logger.info("Moving to next rule; if available ........");
								logger.info("I have to remove DR currency tag....");
								drBankAddInfoChildNode.removeChild(drCurrencyNode);
								logger.info("DR Currency tag is removed.... ");
							}
							continue;
						}	
					}	
				}//resultset itr loop end here
				logger.info("=======================================================================");
				if(internalFlag && myPayments) { 
					logger.info("INTERNAL - Removing the Adress info for Internal Payments");
					if (transactionNode.getNodeType() == Node.ELEMENT_NODE) {
						NodeList txnNodeList = transactionNode.getChildNodes();
						for (int i = 0; i < txnNodeList.getLength(); i++) {
							Node txnNode = txnNodeList.item(i);
							if (txnNode.getNodeName().equals("#text")) {
								continue;
							}
							if (txnNode.getNodeName().equals("Bank_Account")) {
//								logger.debug(transactionNode.getNodeName() + "/" + txnNode.getNodeName());
								NodeList bankAccNodeList = txnNode.getChildNodes();
								for (int bankAccIndex = 0; bankAccIndex < bankAccNodeList.getLength(); bankAccIndex++) {
									Node bankAccNode = bankAccNodeList.item(bankAccIndex);
									if (bankAccNode.getNodeName().equals("#text")) {
										continue;
									}
									if (bankAccNode.getNodeName().equals("Bank")) {
//										logger.debug(transactionNode.getNodeName() + "/" + txnNode.getNodeName() + "/" + bankAccNode.getNodeName());
										NodeList bankNodeList = bankAccNode.getChildNodes();
										for (int bankIndex = 0; bankIndex < bankNodeList.getLength(); bankIndex++) {
											Node bankNode = bankNodeList.item(bankIndex);
											if (bankNode.getNodeName().equals("#text")) {
												continue;
											}
											if (bankNode.getNodeName().equals("Bank_Address_Info")){
//												logger.debug(transactionNode.getNodeName() + "/" + txnNode.getNodeName() + "/" + bankAccNode.getNodeName() + "/[" + bankNode.getNodeName()+ "]Removed");
												bankAccNode.removeChild(bankNode);
											}
										}
									}
									if(bankAccNode.getNodeName().equals("Account")){
//										logger.debug(transactionNode.getNodeName() + "/" + txnNode.getNodeName() + "/" + bankAccNode.getNodeName());
										NodeList accountNodeList = bankAccNode.getChildNodes();
										for (int accountIndex = 0; accountIndex < accountNodeList.getLength(); accountIndex++) {
											Node accountNode = accountNodeList.item(accountIndex);
											if (accountNode.getNodeName().equals("#text")) {
												continue;
											}
											if (accountNode.getNodeName().equals("Account_Address_Info")){
//												logger.debug(transactionNode.getNodeName() + "/" + txnNode.getNodeName() + "/" + bankAccNode.getNodeName() + "/[" + accountNode.getNodeName()+ "]Removed");
												bankAccNode.removeChild(accountNode);
											}
										}
									}
								}
							}
						}
					}
				}
			}//txn loop end here
			
			
			logger.info("Closing Prepared Statement........");
			closePreparedStatement(preparedStmt);
			logger.info("Prepared Statement closed successfully........");
			// write the content into xml file
			doc.normalizeDocument();
		//Miguel - Remove blank spaces
	/*	XPathFactory xpathFactory = XPathFactory.newInstance();
		// XPath to find empty text nodes.
		XPathExpression xpathExp = xpathFactory.newXPath().compile(
		        "//text()[normalize-space(.) = '']");  
		NodeList emptyTextNodes = (NodeList) 
		        xpathExp.evaluate(doc, XPathConstants.NODESET);

		// Remove each empty text node from document.
		for (int i = 0; i < emptyTextNodes.getLength(); i++) {
		    Node emptyTextNode = emptyTextNodes.item(i);
		    emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}*/
		//Miguel - End Remove blank spaces
		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outputFilePath));
			transformer.transform(source, result);
			logger.info("XML generated after Step-2 successfully !!");
			
			logger.info("Going inside addingCodeBranchInCRBankRouteCode method; for adding the Code_Branch in CR Bank_Route_Code, if CR has local Bank_Route_Code");
			addingCodeBranchInCRBankRouteCode(outputFilePath, conn);

			logger.info("Closing connection........");
			closeDataBaseConnection();
			logger.info("Connection closed successfully........");		
		} catch (Exception e) {
			logger.error("Exception occurred........!!");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param outputFilePath, connection
	 */
	private void addingCodeBranchInCRBankRouteCode(String outputFilePath, Connection conn) {
		try {
			File fXmlFile = new File(outputFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			DOMConfiguration config = doc.getDomConfig();
//			config.setParameter("datatype-normalization", Boolean.FALSE);
//			config.setParameter("validate", Boolean.TRUE);
			//get creditor bank address
			String sqlCreditorBankCode = "select distinct bankroutecode,branchcode from lksystembank_route_code " +
					"where bankroutecode+ISNULL(branchcode,'') = ? and type= ?";
			logger.info("SQL Query is....."+sqlCreditorBankCode);
			
			//File/Transaction Data
			logger.info("Starting reading xml file.....");
			NodeList nList = doc.getElementsByTagName("Transaction");
			int txnLoopCount = 0;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				++txnLoopCount;
				logger.info("Selecting "+txnLoopCount+" Transation.....");
				Node transactionNode = nList.item(temp);
				transactionNode.getChildNodes();
				if (transactionNode.getNodeType() == Node.ELEMENT_NODE) {
					//Element eElement = (Element) transactionNode;
					NodeList txnChildNodeList = transactionNode.getChildNodes();
					endBankRouteLoop: for (int i = 0; i < txnChildNodeList.getLength(); i++) {
						 Node txnChildNode = txnChildNodeList.item(i);
						 if(txnChildNode.getNodeName().equals("#text")){
							 continue;
						 }
						 if(txnChildNode.getNodeName().equals("Bank_Account")){
							 NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
							 boolean checkCRFlag = false;
							 for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
								 Node bankAccChildNode = bankAccChildNodeList.item(j);
								 if(bankAccChildNode.getNodeName().equals("#text")){
									 continue;
								 }
								 if(bankAccChildNode.getNodeName().equals("Bank_Account_Type") && 
										 bankAccChildNode.getTextContent().equals("CR")){
									 checkCRFlag = true;
								 }
								 if(bankAccChildNode.getNodeName().equals("Bank") &&  checkCRFlag){
									 NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
									 logger.info("Current Element :" + bankAccChildNode.getNodeName() + " CR " + bankChildNodeList.getLength());
									 //removeChildLoop:
									 for (int k = 0; k < bankChildNodeList.getLength(); k++) {
										 Node bankChildNode = bankChildNodeList.item(k);
										 if(bankChildNode.getNodeName().equals("#text")){
											 continue;
										 }
										 if(bankChildNode.getNodeName().equals("Bank_Route_Code")){
											 NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
											 //for loop for bank route code.........
											 //breakBankRouteCode:
											 for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
												 Node bankRouterChildNode = bankRouterChildNodeList.item(l);
												 if(bankRouterChildNode.getNodeName().equals("#text")){
													 continue;
												 }
												 if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
														 bankRouterChildNode.getTextContent().equals("S")){
													 logger.info("Found Code_Type as S. So, we'll not add Code_Branch ");
													 //do nothing
													 //break removeChildLoop;
												 } else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
														 (bankRouterChildNode.getTextContent().equals("") || 
																 !bankRouterChildNode.getTextContent().equals("S"))) {
													logger.info("Found Code_type not as S. So, we'll add Code_Branch node and modify Code value ...");
													if(bankRouterChildNodeList.getLength() >= 4) {
														logger.info("Node can retrive Code");
														Node codeNode = bankRouterChildNodeList.item(3); // Had 3 causing NPE, changed to last element Miguel
													    String  bankRouteCode = codeNode.getTextContent();
														String branchCode = "";
														// create statement
														PreparedStatement preparedStmt = conn.prepareStatement(sqlCreditorBankCode);
														preparedStmt.setString(1, codeNode.getTextContent());
														preparedStmt.setString(2, bankRouterChildNode.getTextContent());
														
														logger.info("Code Branch SQL Query is....."+sqlCreditorBankCode);
														logger.info("Input paramteres for Code Branch query..................");
														logger.info("bankroutecode :"+codeNode.getTextContent());
														logger.info("type :"+bankRouterChildNode.getTextContent());
														
														//execute query
														logger.info("Executing Query.....");
														ResultSet rsCreditorBankCode = preparedStmt.executeQuery();
														
														while (rsCreditorBankCode.next()) {
															bankRouteCode = rsCreditorBankCode.getString("BANKROUTECODE");
															branchCode = rsCreditorBankCode.getString("BRANCHCODE");
															
															// append a new node to Bank_Route_Code
															Element codeBranch = doc.createElement("Code_Branch");
															codeBranch.appendChild(doc.createTextNode(branchCode));
															bankChildNode.appendChild(codeBranch);
															logger.info("adding Code_Branch");
														}
														
														codeNode.setTextContent(bankRouteCode);
														logger.info("Closing Prepared Statement........");
														closePreparedStatement(preparedStmt);
														logger.info("Prepared Statement closed successfully........");
														
														
													} else {
														logger.error("Unable to set Code Brachn, item 3 doesn't exists");
													}
													break endBankRouteLoop;
												 }
											}
										 }
									 }
								 }
							 }
						 }	 
					}
				}
				//}
			}
			doc.normalizeDocument();
			// write the content into xml file
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outputFilePath));
			transformer.transform(source, result);
			logger.info("XML generation happened successfully !!");
		} catch (Exception e) {
			logger.error("Exception occurred........!!", e);
			e.printStackTrace();
		}
		
	}

	/**
	 * Method used for removing one bank route code from xml..if rule condition fail
	 * @param eElement, bankAccountTypeCR, txnChildNodeList, processType
	 */
	private void removeCRBankRouteCode(Element eElement,String bankAccountTypeCR, 
			NodeList txnChildNodeList ,String processType){
		
		removeChildLoop:
		for (int i = 0; i < txnChildNodeList.getLength(); i++) {
			 Node txnChildNode = txnChildNodeList.item(i);
			 if(txnChildNode.getNodeName().equals("#text")){
				 continue;
			 }
			 if(txnChildNode.getNodeName().equals("Bank_Account")){
				 NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
				 boolean checkCRFlag = false;
				 for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
					 Node bankAccChildNode = bankAccChildNodeList.item(j);
					 if(bankAccChildNode.getNodeName().equals("#text")){
						 continue;
					 }
					 if(bankAccChildNode.getNodeName().equals("Bank_Account_Type") && 
							 bankAccChildNode.getTextContent().equals("CR")){
						 checkCRFlag = true;
					 }
					 if(bankAccChildNode.getNodeName().equals("Bank") && 
							 checkCRFlag){
						 NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
						 
						 for (int k = 0; k < bankChildNodeList.getLength(); k++) {
							 Node bankChildNode = bankChildNodeList.item(k);
							 if(bankChildNode.getNodeName().equals("#text")){
								 continue;
							 }
							 if(bankChildNode.getNodeName().equals("Bank_Route_Code")){
								 NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
								 for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
									 Node bankRouterChildNode = bankRouterChildNodeList.item(l);
									 if(bankRouterChildNode.getNodeName().equals("#text")){
										 continue;
									 }
									 
									 if(processType.equals("MT")){
										 //do nothing
									 }else{
										 //logger.info("Processing Pain file......");
										 if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
												 bankRouterChildNode.getTextContent().equals("S")){
											 logger.info("Found Code_Type as S. So, this will remain same as in input file");
											 continue;
										 }else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
												 (bankRouterChildNode.getTextContent().equals("") || 
														 !bankRouterChildNode.getTextContent().equals("S"))) {
											 logger.info("Removing Bank_Route_Code for Code_Type which is not S....");
											 bankAccChildNode.removeChild(bankChildNode);
											 break removeChildLoop;
										 }
									 }
								}
							 }
						}
					 }
				 }
			 }	 
		}
	}

	
	private void removeINBankRouteCode(Element eElement,String bankAccountTypeCR, 
			NodeList txnChildNodeList ,String processType){
		
		removeChildLoop:
		for (int i = 0; i < txnChildNodeList.getLength(); i++) {
			 Node txnChildNode = txnChildNodeList.item(i);
			 if(txnChildNode.getNodeName().equals("#text")){
				 continue;
			 }
			 if(txnChildNode.getNodeName().equals("Bank_Account")){
				 NodeList bankAccChildNodeList = txnChildNode.getChildNodes();
				 boolean checkINFlag = false;
				 for (int j = 0; j < bankAccChildNodeList.getLength(); j++) {
					 Node bankAccChildNode = bankAccChildNodeList.item(j);
					 if(bankAccChildNode.getNodeName().equals("#text")){
						 continue;
					 }
					 if(bankAccChildNode.getNodeName().equals("Bank_Account_Type") && 
							 bankAccChildNode.getTextContent().equals("IN")){
						 checkINFlag = true;
					 }
					 if(bankAccChildNode.getNodeName().equals("Bank") && 
							 checkINFlag){
						 NodeList bankChildNodeList = bankAccChildNode.getChildNodes();
						 
						 for (int k = 0; k < bankChildNodeList.getLength(); k++) {
							 Node bankChildNode = bankChildNodeList.item(k);
							 if(bankChildNode.getNodeName().equals("#text")){
								 continue;
							 }
							 if(bankChildNode.getNodeName().equals("Bank_Route_Code")){
								 NodeList bankRouterChildNodeList = bankChildNode.getChildNodes();
								 for (int l = 0; l < bankRouterChildNodeList.getLength(); l++) {
									 Node bankRouterChildNode = bankRouterChildNodeList.item(l);
									 if(bankRouterChildNode.getNodeName().equals("#text")){
										 continue;
									 }
									 
									 if(processType.equals("MT")){
										 //do nothing
									 }else{
										 //logger.info("Processing Pain file......");
										 if(bankRouterChildNode.getNodeName().equals("Code_Type") &&
												 bankRouterChildNode.getTextContent().equals("S")){
											 logger.info("Found Code_Type as S. So, this will remain same as in input file");
											 continue;
										 }else if(bankRouterChildNode.getNodeName().equals("Code_Type") & 
												 (bankRouterChildNode.getTextContent().equals("") || 
														 !bankRouterChildNode.getTextContent().equals("S"))) {
											 logger.info("Removing Bank_Route_Code for Code_Type which is not S....");
											 bankAccChildNode.removeChild(bankChildNode);
											 break removeChildLoop;
										 }
									 }
								}
							 }
						}
					 }
				 }
			 }	 
		}
	}	
	
	/**
	 * Method call for MT & Pain File(if pain file has only one Bank_Route_Code)
	 * @param eElement
	 * @param bankAccountTypeDR
	 */
	private void changeTagValuesOnMTYes(Element eElement, String bankAccountTypeDR, String externalModelIdDB, String internalModelIdDB, final boolean myPayments) {
		// Column V
//		logger.info("Transaction/Bank_Account/Bank/Bank_Route_Code/Code_Type.....");
		if(!myPayments){
			if (bankAccountTypeDR.equals("DR")) {
				logger.info("ENH- BANK_ACCOUNT_TYPE = DR, then CODE_TYPE set to S.....");
				Node codeTypeName = eElement.getElementsByTagName("Code_Type").item(0);
				codeTypeName.setTextContent("S");
			}
		}
		// Column AA
		logger.info("Setting Model_ID....");
		Node modeId = eElement.getElementsByTagName("Model_ID").item(0);
		if (modeId.getTextContent().equals("INTERNAL") || modeId.getTextContent().equals("EXTERNAL")) {
			if (internalModelIdDB != null) {
				String typeOfModelID = modeId.getTextContent();
				ChangeModelID(eElement, typeOfModelID, externalModelIdDB, internalModelIdDB);
			} else {
				logger.info("Internal Model from DB is null, hardcode as always");
				modeId.setTextContent("FF CHECK EXT PAY");
			}
		} else {
			logger.info("No match for INTERNAL/EXTERNAL, hardcode as always, check external modelId from DB");
			if (externalModelIdDB != null && modeId.getTextContent().equals("")) {
				logger.info(" Going to take external modelId from DB");
				modeId.setTextContent(externalModelIdDB);
			}
			if (modeId.getTextContent().equals("")) {
				logger.info(" No matching condition for External in DB, Uses Default hardcode model");
				modeId.setTextContent("FF CHECK EXT PAY");
			}
		}
	}
	 
	/**
	 * Method call for Pain File
	 * @param eElement, urgentIndDB, bankAccountTypeDR, drBankAddrDB, externalModelIdDB, internalModelIdDB
	 */
	 private void changeTagValuesfromDB(Element eElement, String urgentIndDB, 
				String bankAccountTypeDR, String drBankAddrDB, String externalModelIdDB, String internalModelIdDB , final boolean myPayments){
		//Column U
		/*
		logger.info("Modifying Transaction_Header/Urgent.....");
		Node urgentNode = eElement.getElementsByTagName("Urgent").item(0);
		urgentNode.setTextContent(urgentIndDB);
		*/
		
		//Column V (DR Address Type)
		 if(!myPayments){
		logger.info("Transaction/Bank_Account/Bank/Bank_Route_Code/Code_Type.....");
			if(bankAccountTypeDR.equals("DR")){
				Node codeTypeName = eElement.getElementsByTagName("Code_Type").item(0);
				codeTypeName.setTextContent(drBankAddrDB);
			}
		}
		//Column AA
		logger.info("Setting Model_ID....");
		Node modeId = eElement.getElementsByTagName("Model_ID").item(0);
		if (modeId.getTextContent().equals("INTERNAL") || modeId.getTextContent().equals("EXTERNAL") ){
			String typeOfModelID= modeId.getTextContent();
			ChangeModelID ( eElement, typeOfModelID, externalModelIdDB, internalModelIdDB);
		} else{
			logger.info("No INTERNAL/EXTERNAL validation, send external Model from DB value as before");
			modeId.setTextContent(externalModelIdDB);
		}
	}
	 
	 private void ChangeModelID (Element eElement, String typeOfModelID, String externalModelIdDB, String internalModelIdDB) {
		 Node modelId = eElement.getElementsByTagName("Model_ID").item(0);
		 if (typeOfModelID.equals("INTERNAL") && internalModelIdDB !=null  ){
			 logger.info("Model_ID Checked as Internal with DB value.... Update with DB");
			 modelId.setTextContent(internalModelIdDB);	 
		 }
		 if (typeOfModelID.equals("EXTERNAL") && externalModelIdDB != null ){
			 logger.info("Model_ID Checked as External  with DB value.... Update with DB");
			 modelId.setTextContent(externalModelIdDB);
		 }
	 }
	 
	 private String getDrBankId(String fileName) {
		 String retVal = "";
		 try {
			 logger.debug("[START] Get DR Bank Id");
			 File fXmlFile = new File(fileName);
			 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			 Document doc = dBuilder.parse(fXmlFile);
			 doc.getDocumentElement().normalize();
			 NodeList nList = doc.getElementsByTagName("BANKID");
			 for(int iAccounts = 0; iAccounts < nList.getLength(); iAccounts++) {
				 Node accountNode = nList.item(iAccounts);
				 retVal = accountNode.getTextContent();
				 logger.debug(retVal);
			 }
			 logger.debug("[END] Get DR Bank Id");
		 } catch (Exception e){
			 logger.error("Unable to get DR Bank ID", e);
		 }	
		return retVal;
	 }
	 
	 /**
	  * Validate if the bank id have a map id
	  * @param bankId
	  * @return <b>true</b> If the bank is in the DB<br>
	  * 		<b>false</b> otherwise
	  */
	 private boolean validateMapId(String bankId, String mapId){
		 boolean retVal = false;
		// create database connection
		logger.info("[START] Validate Map Id");
		try {
			Connection conn = getDataBaseConnection();
			PreparedStatement preparedStmt = null;
			StringBuilder sqlQuery = new StringBuilder();
//			sqlQuery.append("SELECT BANK.BANKID AS DR_BANKID ");
//			sqlQuery.append("  FROM BANK_EMAP  ");
//			sqlQuery.append(" LEFT JOIN BANK "); 
//			sqlQuery.append("    ON BANK_EMAP.DR_BANKINC = BANK.BANKINC ");
//			sqlQuery.append(" WHERE BANK_EMAP.MAP_ID = ?; ");
			
			sqlQuery.append("SELECT DISTINCT dbo.BANK.BANKID,  ");
			sqlQuery.append(" dbo.BANK.NAME,   ");
			sqlQuery.append(" BANK_1.BANKID AS PARENT_BANK ");
			sqlQuery.append(" FROM   dbo.BANK  ");
			sqlQuery.append(" INNER JOIN dbo.BANK AS BANK_1  ");
			sqlQuery.append(" ON dbo.BANK.BANK_OFFICE_INC = BANK_1.BANKINC ");
			sqlQuery.append(" WHERE BANK_1.BANKID IN (  ");
			sqlQuery.append(" SELECT BANK.BANKID AS DR_BANKID  ");
			sqlQuery.append(" FROM BANK_EMAP   ");
			sqlQuery.append(" LEFT JOIN BANK   ");
			sqlQuery.append(" ON BANK_EMAP.DR_BANKINC = BANK.BANKINC  ");
			sqlQuery.append(" WHERE BANK_EMAP.MAP_ID = ? ) ");
			sqlQuery.append(" AND dbo.BANK.BANKID = ? ");  
			sqlQuery.append(" ORDER BY PARENT_BANK; ");
			
			// Create Statement
			preparedStmt = conn.prepareStatement(sqlQuery.toString());
			preparedStmt.setString(1, mapId);
			preparedStmt.setString(2, bankId);
			
			logger.debug("SQL Query [ " + sqlQuery.toString() + " ]");
			logger.debug("Input paramteres for query");
			logger.debug("MAP_ID:" + mapId);
			logger.debug("BANK_ID:" + bankId);
			
			ResultSet resultSet = preparedStmt.executeQuery();
			 String drBankIdDB = "";
			 while (resultSet.next()) {
				 drBankIdDB = resultSet.getString("BANKID");
			 }
			 if(drBankIdDB == null || drBankIdDB.isEmpty() || drBankIdDB.equalsIgnoreCase(bankId) ){
				 retVal = true;
			 }
		} catch(Exception e) {
			logger.error("Unable to get Bank ID", e);
		}
		return retVal;	
	 }

}