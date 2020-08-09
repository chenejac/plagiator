package com.ftn.plagiator.elasticsearch.handler;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.text.PDFTextStripper;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;

public class PDFHandler extends DocumentHandler {

	@Override
	public PaperElastic getIndexUnit(File file) {

		PaperElastic retVal = new PaperElastic();
		try {
			PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
			parser.parse();

			String text = getText(parser);
			retVal.setContent(text);

		} catch (IOException e) {
			System.out.println("Greksa pri konvertovanju dokumenta u pdf");
			return null;
		}

		return retVal;
	}

	@Override
	public String getText(File file) {
		try {
			PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
			parser.parse();
			PDFTextStripper textStripper = new PDFTextStripper();
			String text = textStripper.getText(parser.getPDDocument());
			parser.getPDDocument().close();
			return text;
		} catch (IOException e) {
			System.out.println("Greksa pri konvertovanju dokumenta u pdf");
		}
		return null;
	}

	public String getText(PDFParser parser) {
		try {
			PDFTextStripper textStripper = new PDFTextStripper();
			String text = textStripper.getText(parser.getPDDocument());
			return text;
		} catch (IOException e) {
			System.out.println("Greksa pri konvertovanju dokumenta u pdf");
		}
		return null;
	}

}
