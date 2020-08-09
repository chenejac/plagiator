package com.ftn.plagiator.validation;

import java.util.regex.Pattern;

public class StaticData {
	
	public static final Pattern VALID_PASSWORD_NUMBERS = Pattern.compile("[0-9]+");
	public static final Pattern VALID_PASSWORD_SPECIAL_CHARACTERS = Pattern.compile("[+-/*/.,!?+_#%^]+");
	public static final Pattern VALID_PASSWORD_UPPER_CASE = Pattern.compile("[A-Z]+");
	public static final Pattern VALID_PASSWORD_LOWER_CASE = Pattern.compile("[a-z]+");
	
	//konfiguracija za radove
	public static final String PUTANJA_DO_FAJLA = "/opt/plagiator/files"; //relativna putanja na kojoj cuva radove u fajl sistemu
	public static final String PUTANJA_DO_ELASTICSEARCHDATA = "/opt/plagiator/elastic";
	public static final int NUMBERS_OF_FILES = 7; //broj slicnih fajlova za svaki deo dokumenta
	public static final int NUMBERS_OF_WORDS_SPLITER = 850; //na koliko reci ce da podeli dokument
	public static final int NUMBERS_OF_WORDS_TO_SHOW = 5; // koliko ce eci da prikazu korisniku da bi mogao da ctr finduje u dokumentu
	
}
