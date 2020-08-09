package com.ftn.plagiator.elasticsearch.handler;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;

public class Word2007Handler extends DocumentHandler {

	public PaperElastic getIndexUnit(File file) {
		PaperElastic retVal = new PaperElastic();

		try {
			XWPFDocument wordDoc = new XWPFDocument(new FileInputStream(file));
			XWPFWordExtractor we = new XWPFWordExtractor(wordDoc);

			String text = we.getText();
			retVal.setText(text);

			POIXMLProperties props = wordDoc.getProperties();

			String title = props.getCoreProperties().getTitle();
			retVal.setTitle(title);

//			String keywords = props.getCoreProperties()
//					.getUnderlyingProperties().getKeywordsProperty().getValue();
//			if(keywords != null){
//				String[] splittedKeywords = keywords.split(" ");
//				retVal.setKeywords(new ArrayList<String>(Arrays.asList(splittedKeywords)));
//			}
//			retVal.setFilename(file.getCanonicalPath());
//			
//			String modificationDate=DateTools.dateToString(new Date(file.lastModified()),DateTools.Resolution.DAY);
//			retVal.setFiledate(modificationDate);
			
			we.close();

		} catch (Exception e) {
			System.out.println("Problem pri parsiranju docx fajla");
		}

		return retVal;
	}

	@Override
	public String getText(File file) {
		String text = null;
		try {
			XWPFDocument wordDoc = new XWPFDocument(new FileInputStream(file));
			XWPFWordExtractor we = new XWPFWordExtractor(wordDoc);
			text = we.getText();
			we.close();
		}catch (Exception e) {
			System.out.println("Problem pri parsiranju docx fajla");
		}
		return text;
	}

}
