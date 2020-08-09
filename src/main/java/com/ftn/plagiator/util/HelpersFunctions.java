package com.ftn.plagiator.util;

public class HelpersFunctions {
	
	public static double calculateCoefficient(double number) {
		double truncateNumber = Double.valueOf(String.valueOf(number).substring(0,3));
		
		int intTruncateNumber = (int)(truncateNumber*10);
		double coefficient = 0;
		switch (intTruncateNumber) {
		case 0:
			coefficient = 0.5;
			break;
		case 1:
			coefficient = 0.6;
			break;
		case 2:
			coefficient = 0.7;
			break;
		case 3:
			coefficient = 0.8;
			break;
		case 4:
			coefficient = 0.9;
			break;
		case 5:
			coefficient = 1.0;
			break;
		case 6:
			coefficient = 1.1;
			break;
		case 7:
			coefficient = 1.2;
			break;
		case 8:
			coefficient = 1.3;
			break;
		case 9:
			coefficient = 1.4;
			break;
		case 10:
			coefficient = 1.5;
			break;
		default:
			coefficient = 1.0;
			break;
		}
		
		return coefficient*number;
	}

}
