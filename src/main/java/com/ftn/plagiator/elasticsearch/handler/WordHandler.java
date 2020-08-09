package com.ftn.plagiator.elasticsearch.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;


public class WordHandler extends DocumentHandler {

	public PaperElastic getIndexUnit(File file) {
		PaperElastic retVal = new PaperElastic();
		InputStream is;

		try {
			is = new FileInputStream(file);
			// pomocu WordExtractor objekta izvuci tekst
			WordExtractor we = new WordExtractor(is);
			String text = we.getText();
			retVal.setText(text);
			
			// pomocu SummaryInformation objekta izvuci ostale metapodatke
//			SummaryInformation si = we.getSummaryInformation();
//			String title = si.getTitle();
//			retVal.setTitle(title);
//
//			String keywords = si.getKeywords();
//			if(keywords != null){
//				String[] splittedKeywords = keywords.split(" ");
//				retVal.setKeywords(new ArrayList<String>(Arrays.asList(splittedKeywords)));
//			}
			
			//retVal.setFilename(file.getCanonicalPath());
			
			//String modificationDate=DateTools.dateToString(new Date(file.lastModified()),DateTools.Resolution.DAY);
			//retVal.setFiledate(modificationDate);
			
			we.close();
		} catch (FileNotFoundException e1) {
			System.out.println("Dokument ne postoji");
		} catch (Exception e) {
			System.out.println("Problem pri parsiranju doc fajla");
		}

		return retVal;
	}

	@Override
	public String getText(File file) {
		String text = null;
		try {
			WordExtractor we = new WordExtractor(new FileInputStream(file));
			text = we.getText();
			we.close();
		} catch (FileNotFoundException e1) {
			System.out.println("Dokument ne postoji");
		} catch (Exception e) {
			System.out.println("Problem pri parsiranju doc fajla");
		}
		return text;
	}

}
