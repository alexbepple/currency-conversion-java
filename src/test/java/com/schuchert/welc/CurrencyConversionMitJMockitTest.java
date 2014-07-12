package com.schuchert.welc;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import mockit.NonStrictExpectations;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class CurrencyConversionMitJMockitTest {

	@Test
	public void findsOutConversionRate() throws Exception {
		allowCurrencies("x", "y");
		setUpHtml("<div id=\"converter_results\"><ul><li><strong>1 x = 2 y</strong>");
		
		assertThat(CurrencyConversion.getConversionRate("x", "y"), 
				is(BigDecimal.valueOf(2)));
		
	}

	private void allowCurrencies(String... currencies) {
		final Map<String, String> mapMitGueltigenWaehrungen 
			= new HashMap<String, String>();
		for (String currency: currencies)
			mapMitGueltigenWaehrungen.put(currency, "");
		new NonStrictExpectations(CurrencyConversion.class) {{
			CurrencyConversion.currencySymbols();
			result = mapMitGueltigenWaehrungen;
		}};
	}

	private void setUpHtml(String html) throws IOException {
		final InputStream htmlStream = new ByteArrayInputStream(html.getBytes());
		
		new NonStrictExpectations() {
			DefaultHttpClient httpClient;
			HttpResponse response;
			HttpEntity entity;
			{
				httpClient.execute((HttpUriRequest) any);
				result = response;
				response.getEntity();
				result = entity;
				entity.getContent();
				result = htmlStream;
			}
		};
	}
	
}
