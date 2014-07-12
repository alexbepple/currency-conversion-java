package com.schuchert.welc;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class CurrencyConversionIntegrationTest {
   @Test
   public void findsOutConversionRate() {
      BigDecimal result = CurrencyConversion.getConversionRate("USD", "EUR");
      assertThat(result, both(is(greaterThan(BigDecimal.ZERO)))
    		  .and(is(lessThan(BigDecimal.valueOf(5)))));
   }

   @Test(expected = IllegalArgumentException.class)
   public void doesNotAllowBadFromCurrency() {
      CurrencyConversion.getConversionRate("bogus", "EUR");
   }

   @Test(expected = IllegalArgumentException.class)
   public void doesNotAllowBadToCurrency() {
      CurrencyConversion.getConversionRate("USD", "Bogus");
   }
}
