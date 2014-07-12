package com.schuchert.welc;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

public class CachingCurrencySymbolsTest {

	private CurrencySymbols realProvider = mock(CurrencySymbols.class);
	private Clock clock = mock(Clock.class);
	private CachingCurrencySymbols cachingProvider = new CachingCurrencySymbols(realProvider, clock);

	@Test
	public void getsSymbolsFromRealSymbolsProvider() throws Exception {
		Map<String, String> sentinel = Collections.emptyMap();
		
		when(realProvider.currencySymbols()).thenReturn(sentinel);
		
		assertThat(cachingProvider.currencySymbols(), is(sentinel));
	}
	
	@Test
	public void cachesSymbolsFromRealProvider() throws Exception {
		cachingProvider.currencySymbols();
		cachingProvider.currencySymbols();
		
		verify(realProvider, only()).currencySymbols();
	}

	@Test
	public void getsSymbolsAnewFromRealProviderAfter5Minutes() throws Exception {
		when(clock.currentTimeMillis())
			.thenReturn(0l)
			.thenReturn(5l*60*1000 + 1);
		
		cachingProvider.currencySymbols();
		cachingProvider.currencySymbols();
		
		verify(realProvider, times(2)).currencySymbols();
	}
}
