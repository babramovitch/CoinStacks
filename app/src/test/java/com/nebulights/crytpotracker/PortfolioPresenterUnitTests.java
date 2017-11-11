package com.nebulights.crytpotracker;

import com.nebulights.crytpotracker.Network.ExchangeProvider;
import com.nebulights.crytpotracker.Network.Exchanges;
import com.nebulights.crytpotracker.Network.exchanges.TradingInfo;
import com.nebulights.crytpotracker.Portfolio.PortfolioFragment;
import com.nebulights.crytpotracker.Portfolio.PortfolioPresenter;
import com.nebulights.crytpotracker.mock.FakeCryptoAssetRepository;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PortfolioPresenterUnitTests {

    @Test
    public void netValueRoundsTwoDecimalsUp() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        //2.418025
        BigDecimal result = presenter.netValue(new BigDecimal("1.555"), new BigDecimal("1.555"));
        assertEquals("2.42", result.toString());
    }

    @Test
    public void netValueRoundsTwoDecimalsDown() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        //2.4025
        BigDecimal result = presenter.netValue(new BigDecimal("1.55"), new BigDecimal("1.55"));
        assertEquals("2.40", result.toString());
    }

    //******

    @Test
    public void tickerCount() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BCH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BTC_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_ETH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "10.00");

        assertEquals(4, presenter.tickerCount());
    }

    @Test
    public void tickerCountAfterUpdatingItem() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BCH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BCH_USD, "15.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_ETH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "10.00");

        assertEquals(3, presenter.tickerCount());
    }


    //******

    @Test
    public void tickerPositionFoundLastIndex() throws Exception {
        PortfolioPresenter presenter = createPresenter();


        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BCH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BTC_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_ETH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "10.00");

        CryptoPairs result = presenter.getOrderedTicker(3);

        assertEquals(CryptoPairs.QUADRIGA_BTC_CAD, result);
    }

    @Test
    public void tickerPositionZeroIndex() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BCH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BTC_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_ETH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "10.00");

        CryptoPairs result = presenter.getOrderedTicker(0);

        assertEquals(CryptoPairs.BITFINEX_BCH_USD, result);
    }

    @Test
    public void tickerIndexFound() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BCH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_BTC_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.BITFINEX_ETH_USD, "10.00", "10.00");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "10.00");

        int result = presenter.getOrderedTickerIndex(CryptoPairs.BITFINEX_BTC_USD);
        assertEquals(1, result);
    }

    //******

    @Test
    public void currentHoldingsForPositionZeroBTC() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");
        BigDecimal result = presenter.tickerQuantityForIndex(0);

        assertEquals("50.50974687", result.toString());
    }

    //******

    @Test
    public void netWorthOneAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter();
        TradingInfo currentTradingInfo = new TradingInfo("100.50", "");
        presenter.addTickerData(currentTradingInfo, CryptoPairs.QUADRIGA_BTC_CAD);

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$5,076.23 CAD", networth);
    }

    @Test
    public void netWorthTwoAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        TradingInfo currentTradingInfoBTC = new TradingInfo("100.50", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoPairs.QUADRIGA_BTC_CAD);

        TradingInfo currentTradingInfoBCH = new TradingInfo("50.55", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoPairs.QUADRIGA_BCH_CAD);

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BCH_CAD, "20.55", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$6,115.03 CAD", networth);
    }

    @Test
    public void netWorthTwoAssetOneTicker() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        TradingInfo currentTradingInfoBTC = new TradingInfo("100.50", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoPairs.QUADRIGA_BTC_CAD);

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BCH_CAD, "20.55", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$5,076.23 CAD", networth);
    }

    @Test
    public void netWorthTwoAssetNoTicker() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BCH_CAD, "20.55", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthNoMatchingAssetTwoTickers() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        TradingInfo currentTradingInfoBTC = new TradingInfo("100.50", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoPairs.QUADRIGA_BTC_CAD);

        TradingInfo currentTradingInfoBCH = new TradingInfo("50.55", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoPairs.QUADRIGA_BCH_CAD);

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_LTC_CAD, "50.50974687", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthNoAssetTwoTickers() throws Exception {

        PortfolioPresenter presenter = createPresenter();

        TradingInfo currentTradingInfoBTC = new TradingInfo("100.50", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoPairs.QUADRIGA_BTC_CAD);

        TradingInfo currentTradingInfoBCH = new TradingInfo("50.55", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoPairs.QUADRIGA_BCH_CAD);

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthEmptyLastAmount() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        TradingInfo currentTradingInfo = new TradingInfo("", "");
        presenter.addTickerData(currentTradingInfo, CryptoPairs.QUADRIGA_BTC_CAD);
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthTwoCurrencies() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        TradingInfo currentTradingInfoBTC = new TradingInfo("100.50", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoPairs.QUADRIGA_BTC_CAD);

        TradingInfo currentTradingInfoBCH = new TradingInfo("50.55", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoPairs.GDAX_BTC_USD);

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "500", "1756.87");
        presenter.createOrUpdateAsset(CryptoPairs.GDAX_BTC_USD, "100", "1756.87");

        String networth = presenter.getNetWorthDisplayString();
        assertEquals("$50,250.00 CAD\n$5,055.00 USD", networth);
    }

    //****** Helper Functions ******

    private PortfolioPresenter createPresenter() {

        Exchanges exchange = Exchanges.INSTANCE;
        exchange.clearData();
        exchange.loadRepositories(ExchangeProvider.INSTANCE);

        return new PortfolioPresenter(exchange,
                PortfolioFragment.Companion.newInstance(),
                new FakeCryptoAssetRepository());
    }
}