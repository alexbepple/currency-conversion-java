package com.schuchert.welc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class CurrencySymbolsFromXE implements CurrencySymbols {
	@Override
	public Map<String, String> currencySymbols() {
		Map<String, String> symbolToName = new ConcurrentHashMap<String, String>();
		String url = "http://www.xe.com/iso4217.php";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				InputStreamReader irs = new InputStreamReader(instream);
				BufferedReader br = new BufferedReader(irs);
				String l;
				boolean foundTable = false;
				while ((l = br.readLine()) != null) {
					if (foundTable) {
						Pattern symbol = Pattern.compile("href=\"/currency/"
								+ "[^>]+>(...)</a></td><td class=\"[^\"]+\">"
								+ "([A-Za-z ]+)");
						Matcher m = symbol.matcher(l);
						while (m.find()) {
							symbolToName.put(m.group(1), m.group(2));
						}
					}
					if (l.indexOf("currencyTable") >= 0)
						foundTable = true;
					else
						continue;
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return symbolToName;
	}

}
