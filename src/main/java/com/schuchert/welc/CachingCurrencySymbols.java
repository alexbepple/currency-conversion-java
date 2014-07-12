package com.schuchert.welc;

import java.util.Map;

public class CachingCurrencySymbols implements CurrencySymbols {

	private CurrencySymbols realProvider;
	private Map<String, String> allCurrenciesCache;
	private long lastCacheRead = 0;
	private Clock clock;

	public CachingCurrencySymbols(CurrencySymbols realProvider, Clock clock) {
		this.realProvider = realProvider;
		this.clock = clock;
	}

	@Override
	public Map<String, String> currencySymbols() {
		long currentTime = clock.currentTimeMillis();
		if (allCurrenciesCache == null
				|| currentTime - lastCacheRead > 5 * 60 * 1000)
		{
			allCurrenciesCache = realProvider.currencySymbols();
			lastCacheRead = currentTime;
		}
		return allCurrenciesCache;
	}

}
