package com.ftn.plagiator.validation;

import java.util.regex.Matcher;

import org.springframework.stereotype.Component;

@Component
public class CheckPassword {
		
	public boolean checkIsPasswordCorrect(String password) {
		//nije dobro popunjeno
		if(password == null) {
			return false;
		}
		
		//kraca od 8 karaktera
		if(password.length() < 8) {
			return false;
		}
		
		//nema nijedan broj
		Matcher matcher = StaticData.VALID_PASSWORD_NUMBERS.matcher(password);
        if(!matcher.find()) {
        	return false;
        }
        
        //nema ni jedan specijalan znak
        matcher = StaticData.VALID_PASSWORD_SPECIAL_CHARACTERS.matcher(password);
        if(!matcher.find()) {
        	return false;
        }
        
        //nema ni jedno veliko slovo
        matcher = StaticData.VALID_PASSWORD_UPPER_CASE.matcher(password);
        if(!matcher.find()) {
        	return false;
        }
        
        //nema ni jedno malo slovo
        matcher = StaticData.VALID_PASSWORD_LOWER_CASE.matcher(password);
        if(!matcher.find()) {
        	return false;
        }
        
        //ako je preziveo sve provere onda je dobra sifra
		return true;
	}
}