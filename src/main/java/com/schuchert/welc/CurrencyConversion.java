package com.schuchert.welc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class CurrencyConversion {
	private static CurrencyConversion instance;
	private CurrencySymbols currencySymbols;

	public CurrencyConversion(CurrencySymbols currencySymbols) {
		this.currencySymbols = currencySymbols;
	}

	public static BigDecimal getConversionRate(String fromCurrency,
			String toCurrency) {
		return getInstance().getConversionRateInstance(fromCurrency, toCurrency);
	}

	private static CurrencyConversion getInstance() {
		if (instance == null) {
			instance = new CurrencyConversion(
							new CachingCurrencySymbols(
									new CurrencySymbolsFromXE(), 
									new SystemClock()));
		}
		return instance;
	}
   
   public BigDecimal getConversionRateInstance(String fromCurrency,
	         String toCurrency) {
	      Map<String, String> symbolToName = currencySymbols.currencySymbols();
	      if (!symbolToName.containsKey(fromCurrency))
	         throw new IllegalArgumentException(String.format(
	               "Invalid from currency: %s", fromCurrency));
	      if (!symbolToName.containsKey(toCurrency))
	         throw new IllegalArgumentException(String.format(
	               "Invalid to currency: %s", toCurrency));
	      String url = String
	            .format("http://www.gocurrency.com/v2/dorate.php?inV=1&from=%s&to" +
	            		"=%s&Calculate=Convert",
	                  toCurrency, fromCurrency);
	      try {
	         HttpClient httpclient = new DefaultHttpClient();
	         HttpGet httpget = new HttpGet(url);
	         HttpResponse response = httpclient.execute(httpget);
	         HttpEntity entity = response.getEntity();
	         StringBuffer result = new StringBuffer();
	         if (entity != null) {
	            InputStream instream = entity.getContent();
	            InputStreamReader irs = new InputStreamReader(instream);
	            BufferedReader br = new BufferedReader(irs);
	            String l;
	            while ((l = br.readLine()) != null) {
	               result.append(l);
	            }
	         }
	         String theWholeThing = result.toString();
	         int start = theWholeThing
	               .lastIndexOf("<div id=\"converter_results\"><ul><li>");
	         String substring = result.substring(start);
	         int startOfInterestingStuff = substring.indexOf("<strong>") + 3;
	         int endOfIntererestingStuff = substring.indexOf("</strong>",
	               startOfInterestingStuff);
	         String interestingStuff = substring.substring(
	               startOfInterestingStuff, endOfIntererestingStuff);
	         String[] parts = interestingStuff.split("=");
	         String value = parts[1].trim().split(" ")[0];
	         BigDecimal bottom = new BigDecimal(value);
	         return bottom;
	      } catch (Exception e) {
	         throw new RuntimeException(e);
	      }
	   }

}
