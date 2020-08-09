package com.ftn.plagiator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.ftn.plagiator.elasticsearch.handler.PDFHandler;
import com.ftn.plagiator.elasticsearch.handler.Word2007Handler;
import com.ftn.plagiator.elasticsearch.handler.WordHandler;

public class FileClass {
	
	private static PDFHandler pdfHandler = new PDFHandler();
	private static WordHandler wordHandler = new WordHandler();
	private static Word2007Handler word2007Handler = new Word2007Handler();
		
	public static String saveFile(MultipartFile file, String folderPath) throws IOException {
	   	String retVal = null;
        if (! file.isEmpty()) {
	           byte[] bytes = file.getBytes();
	           String filePrefix = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
	           Path path = Paths.get(getResourceFilePath(folderPath).getPath() + File.separator + filePrefix 
	        		   + "-" + file.getOriginalFilename());
	           Files.write(path, bytes);
	           retVal = path.toString();
        }
        return retVal;
    }
	
	public static File getResourceFilePath(String path) {
	    return new File(path);
	}
	
	public static String getTextFromFile(String filename, File file) {
		String text = "";
		
		if(filename.contains(".pdf")) {
			text = pdfHandler.getText(file);
		}
		else if(filename.contains(".docx")){
			text = word2007Handler.getText(file);
		}
		else if(filename.contains(".doc")){
			text = wordHandler.getText(file);
		}
		
		return text;
	}
	
}