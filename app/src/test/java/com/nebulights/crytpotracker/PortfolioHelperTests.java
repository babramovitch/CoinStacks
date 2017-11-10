package com.nebulights.crytpotracker;

import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class PortfolioHelperTests {

    @Test
    public void stringSafeBigDecimalNotANumber() throws Exception {
        BigDecimal result = PortfolioHelpers.Companion.stringSafeBigDecimal("abc");
        assertEquals(result, new BigDecimal("0"));
    }

    @Test
    public void stringSafeBigDecimalIsANumber() throws Exception {
        BigDecimal result = PortfolioHelpers.Companion.stringSafeBigDecimal("500");
        assertEquals(result, new BigDecimal("500"));
    }

    @Test
    public void CurrencyFormattedHundreds() throws Exception {
        BigDecimal result = PortfolioHelpers.Companion.stringSafeBigDecimal("500");
        assertEquals("$500.00", PortfolioHelpers.Companion.currencyFormatter().format(result));
    }

    @Test
    public void CurrencyFormattedThousands() throws Exception {
        BigDecimal result = PortfolioHelpers.Companion.stringSafeBigDecimal("5000");
        assertEquals("$5,000.00", PortfolioHelpers.Companion.currencyFormatter().format(result));
    }

    @Test
    public void CurrencyFormattedMillions() throws Exception {
        BigDecimal result = PortfolioHelpers.Companion.stringSafeBigDecimal("5000000");
        assertEquals("$5,000,000.00", PortfolioHelpers.Companion.currencyFormatter().format(result));
    }

}