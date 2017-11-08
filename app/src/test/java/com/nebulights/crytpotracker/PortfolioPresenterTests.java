package com.nebulights.crytpotracker;

import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo;
import com.nebulights.crytpotracker.Network.RepositoryProvider;
import com.nebulights.crytpotracker.Portfolio.PortfolioFragment;
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers;
import com.nebulights.crytpotracker.Portfolio.PortfolioPresenter;
import com.nebulights.crytpotracker.mock.FakeCryptoAssetRepository;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PortfolioPresenterTests {

    @Test
    public void netValueRoundsTwoDecimalsUp() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        //2.418025
        BigDecimal result = presenter.netValue(new BigDecimal("1.555"), new BigDecimal("1.555"));
        assertEquals("2.42", result.toString());
    }

    @Test
    public void netValueRoundsTwoDecimalsDown() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        //2.4025
        BigDecimal result = presenter.netValue(new BigDecimal("1.55"), new BigDecimal("1.55"));
        assertEquals("2.40", result.toString());
    }

    //******

    @Test
    public void tickerCount() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        assertEquals(4, presenter.tickerCount());
    }


    //******

    @Test
    public void tickerPositionFoundLastIndex() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        CryptoTypes result = presenter.getOrderedTicker(3);

        assertEquals(CryptoTypes.LTC, result);
    }

    @Test
    public void tickerPositionZeroIndex() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        CryptoTypes result = presenter.getOrderedTicker(0);

        assertEquals(CryptoTypes.BTC, result);
    }

    @Test
    public void tickerIndexFound() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        int result = presenter.getOrderedTickerIndex(CryptoTypes.BCH);
        assertEquals(1, result);
    }

    //******

    @Test
    public void currentHoldingsForPositionZeroBTC() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        BigDecimal result = presenter.tickerQuantityForIndex(0);

        assertEquals("50.50974687", result.toString());
    }

    //******

    @Test
    public void netWorthOneAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfo = new CurrentTradingInfo("", "", "100.50", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfo, CryptoTypes.BTC);

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$5,076.23", networth);
    }

    @Test
    public void netWorthTwoAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo("", "", "100.50", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo("", "", "50.55", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoTypes.BCH, "20.55", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$6,115.03", networth);
    }

    @Test
    public void netWorthTwoAssetOneTicker() throws Exception {
        PortfolioPresenter presenter = createPresenter(getSingleCryptoList());

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo("", "", "100.50", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoTypes.BCH, "20.55", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$5,076.23", networth);
    }

    @Test
    public void netWorthTwoAssetNoTicker() throws Exception {

        List<CryptoTypes> list = new ArrayList<>();

        PortfolioPresenter presenter = createPresenter(list);

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoTypes.BCH, "20.55", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthNoMatchingAssetTwoTickers() throws Exception {

        List<CryptoTypes> list = new ArrayList<>();

        PortfolioPresenter presenter = createPresenter(list);

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo("", "", "100.50", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo("", "", "50.55", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        presenter.createOrUpdateAsset(CryptoTypes.LTC, "50.50974687", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthNoAssetTwoTickers() throws Exception {

        List<CryptoTypes> list = new ArrayList<>();

        PortfolioPresenter presenter = createPresenter(list);

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo("", "", "100.50", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo("", "", "50.55", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthEmptyLastAmount() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfo = new CurrentTradingInfo("", "", "", "", "", "", "", "");

        presenter.addTickerData(currentTradingInfo, CryptoTypes.BTC);
        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }


    //****** Helper Functions ******

    private List<CryptoTypes> getCryptoList() {
        List<CryptoTypes> list = new ArrayList<>();
        list.add(CryptoTypes.BTC);
        list.add(CryptoTypes.BCH);
        list.add(CryptoTypes.ETH);
        list.add(CryptoTypes.LTC);
        return list;
    }

    private List<CryptoTypes> getSingleCryptoList() {
        List<CryptoTypes> list = new ArrayList<>();
        list.add(CryptoTypes.BTC);
        return list;
    }

    private PortfolioPresenter createPresenter(List<CryptoTypes> cryptoTypesArrayList) {

        return new PortfolioPresenter(RepositoryProvider.INSTANCE.provideQuadrigaRepository(),
                PortfolioFragment.Companion.newInstance(),
                cryptoTypesArrayList,
                new FakeCryptoAssetRepository());
    }
}