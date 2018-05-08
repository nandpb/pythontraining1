package com.ge.treasury.pfi.controller;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import com.ge.treasury.pfi.paincontroller.GetAlbanyWCAccountID;
import com.ge.treasury.pfi.paincontroller.TransformXML;

public class AllStgs {
	
	final static String inputFile = "TR_6200_VP_S_XML_1200.xml";
	final static String xlsFileStg1 = "pain001v3ToIBAN.xsl";
	final static String outputFileStg1 = "gecc_acctsId_IBAN_TEST.xml";
	final static String inputFileBckUp = "modifiedFileName_TEST.xml";
	
	final static String xlsFileStg2 = "AlbanyPain001v3ToWCxml.xsl";
	final static String outputFileStg2 = "webcashAlbanyPain001XML_TEST.xml";
	
	final static String finalOutputFile = "webcashAlbanyXML_TEST.xml";
	final static String fileType = "Pain";
	
	final static String optionid = "PFIALPWAPACXML";
	
	public static void main(String[] args) {
		System.out.println("JAVA JRE " + System.getProperty("java.version"));
		try {
			System.out.println("===========================================");
			System.out.println("Cleaning tmp files");
			deleteFile(outputFileStg1);
			deleteFile(inputFileBckUp);
			deleteFile(outputFileStg2);
			deleteFile(finalOutputFile);
			System.out.println("===========================================");
			System.out.println("Starting Stgs...");
			System.out.println("--------------------------------");
			System.out.println("\tSTG 1");
			System.out.println("--------------------------------");
			stage1();
			System.out.println("--------------------------------");
			System.out.println("\tSTG 2");
			System.out.println("--------------------------------");
			stage2();
			System.out.println("--------------------------------");
			System.out.println("\tSTG 3");
			System.out.println("--------------------------------");
			stage3();
			System.out.println("===========================================");
			System.out.println("Stgs Completed Succesfully");
			System.out.println("===========================================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		if(file.delete()){
			System.out.println(file.getName() + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}
	}

	public static void stage1() throws Exception {
		String[] stgArgs = new String[5];
		stgArgs [0] = inputFile; 
		stgArgs [1] = xlsFileStg1;
		stgArgs [2] = outputFileStg1;
		stgArgs [3] = inputFileBckUp;
		stgArgs [4] = optionid;
		for(String args: stgArgs){
			System.out.println(args);
		}
		GetAlbanyWCAccountID.main(stgArgs);
	}

	public static void stage2() throws IOException, TransformerException {
		String[] stgArgs = new String[5];
		stgArgs [0] = inputFile; 
		stgArgs [1] = xlsFileStg2;
		stgArgs [2] = outputFileStg2;
		stgArgs [3] = outputFileStg1;
		stgArgs [4] = inputFileBckUp;
		for(String args: stgArgs){
			System.out.println(args);
		}
		TransformXML.main(stgArgs);
	}

	public static void stage3() {
		String[] stgArgs = new String[5];
		stgArgs [0] = outputFileStg2; 
		stgArgs [1] = finalOutputFile;
		stgArgs [2] = fileType;
		stgArgs [3] = optionid;
		stgArgs [4] = outputFileStg1;
		System.out.println("--------------------------------");
		for(String args: stgArgs){
			System.out.println(args);
		}
		GetAlbanyWCBankEMap.main(stgArgs);
	}
}
