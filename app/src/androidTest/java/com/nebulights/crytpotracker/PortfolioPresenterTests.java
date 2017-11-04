package com.nebulights.crytpotracker;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo;
import com.nebulights.crytpotracker.Network.RepositoryProvider;
import com.nebulights.crytpotracker.Portfolio.PortfolioFragment;
import com.nebulights.crytpotracker.Portfolio.PortfolioPresenter;
import com.nebulights.crytpotracker.Portfolio.PortfolioRecyclerAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PortfolioPresenterTests {

    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.nebulights.crytpotracker", appContext.getPackageName());
    }

    //******

    @Test
    public void stringSafeBigDecimalNotANumber() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        BigDecimal result = presenter.stringSafeBigDecimal("abc");
        assertEquals(result, new BigDecimal("0"));
    }

    @Test
    public void stringSafeBigDecimalIsANumber() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        BigDecimal result = presenter.stringSafeBigDecimal("500");
        assertEquals(result, new BigDecimal("500"));
    }

    //******

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

    @Test
    public void createAssetNew() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "10.00", "50.00");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoTypes.BTC);

        assertEquals("10.00", createdQuantity.toString());
    }

    @Test
    public void createAssetNewNotANumber() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "abc", "abc");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoTypes.BTC);

        assertEquals("0.0", createdQuantity.toString());
    }

    @Test
    public void updateAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoTypes.BTC, "97.68239875", "1756.87");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoTypes.BTC);

        assertEquals("97.68239875", createdQuantity.toString());
    }

    //******

    @Test
    public void tickerPositionNotFound() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        CryptoTypes result = presenter.getOrderedTicker(5);

        assertEquals(null, result);
    }

    @Test
    public void tickerPositionNegative() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        CryptoTypes result = presenter.getOrderedTicker(-1);

        assertEquals(null, result);
    }

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
    public void tickerIndexNotFound() throws Exception {
        PortfolioPresenter presenter = createPresenter(getSingleCryptoList());
        int result = presenter.getOrderedTicker(CryptoTypes.BCH);
        assertEquals(-1, result);
    }

    @Test
    public void tickerIndexFound() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        int result = presenter.getOrderedTicker(CryptoTypes.BCH);
        assertEquals(1, result);
    }


    //******

    @Test
    public void currentHoldingsNotFoundForPosition() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());
        BigDecimal result = presenter.getCurrentHoldings(5);

        assertEquals("0.0", result.toString());
    }

    @Test
    public void currentHoldingsForPositionZeroBTC() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        BigDecimal result = presenter.getCurrentHoldings(0);

        assertEquals("50.50974687", result.toString());
    }

    //******
    @Test
    public void netWorthOneAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfo = new CurrentTradingInfo();
        currentTradingInfo.setLast("100.50");

        presenter.addTickerData(currentTradingInfo, CryptoTypes.BTC);

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$5,076.23", networth);
    }

    @Test
    public void netWorthTwoAsset() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo();
        currentTradingInfoBTC.setLast("100.50");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo();
        currentTradingInfoBCH.setLast("50.55");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoTypes.BCH, "20.55", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$6,115.03", networth);
    }

    @Test
    public void netWorthTwoAssetOneTicker() throws Exception {
        PortfolioPresenter presenter = createPresenter(getSingleCryptoList());

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo();
        currentTradingInfoBTC.setLast("100.50");
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

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo();
        currentTradingInfoBTC.setLast("100.50");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo();
        currentTradingInfoBCH.setLast("50.55");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        presenter.createOrUpdateAsset(CryptoTypes.LTC, "50.50974687", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthNoAssetTwoTickers() throws Exception {

        List<CryptoTypes> list = new ArrayList<>();

        PortfolioPresenter presenter = createPresenter(list);

        CurrentTradingInfo currentTradingInfoBTC = new CurrentTradingInfo();
        currentTradingInfoBTC.setLast("100.50");
        presenter.addTickerData(currentTradingInfoBTC, CryptoTypes.BTC);

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo();
        currentTradingInfoBCH.setLast("50.55");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }

    @Test
    public void netWorthNullLastAmount() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfo = new CurrentTradingInfo();
        currentTradingInfo.setLast(null);

        presenter.addTickerData(currentTradingInfo, CryptoTypes.BTC);
        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");

        String networth = presenter.getNetWorth();
        assertEquals("$0.00", networth);
    }

    // ******

    @Test
    public void bindRowWithAllData() throws Exception {

        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BCH, "10.00", "1756.87");

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo();
        currentTradingInfoBCH.setLast("50.55");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        Context appContext = InstrumentationRegistry.getTargetContext();
        View rowView = LayoutInflater.from(appContext).inflate(R.layout.recycler_list_item, null);
        PortfolioRecyclerAdapter.ViewHolder viewHolder = new PortfolioRecyclerAdapter.ViewHolder(rowView);
        presenter.onBindRepositoryRowViewAtPosition(1, viewHolder);

        assertEquals(viewHolder.getTicker().getText().toString(), "BCH");
        assertEquals(viewHolder.getLastPrice().getText().toString(), "$50.55");
        assertEquals(viewHolder.getHoldings().getText().toString(), "10.00");
        assertEquals(viewHolder.getNetValue().getText().toString(), "$505.50");

    }

    @Test
    public void bindRowWithNoAsset() throws Exception {

        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo();
        currentTradingInfoBCH.setLast("50.55");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        Context appContext = InstrumentationRegistry.getTargetContext();
        View rowView = LayoutInflater.from(appContext).inflate(R.layout.recycler_list_item, null);
        PortfolioRecyclerAdapter.ViewHolder viewHolder = new PortfolioRecyclerAdapter.ViewHolder(rowView);
        presenter.onBindRepositoryRowViewAtPosition(1, viewHolder);

        assertEquals(viewHolder.getTicker().getText().toString(), "BCH");
        assertEquals(viewHolder.getLastPrice().getText().toString(), "$50.55");
        assertEquals(viewHolder.getHoldings().getText().toString(), "0.0");
        assertEquals(viewHolder.getNetValue().getText().toString(), "$0.00");

    }

    @Test
    public void bindRowWithNullLastPrice() throws Exception {

        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo();
        currentTradingInfoBCH.setLast(null);
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        Context appContext = InstrumentationRegistry.getTargetContext();
        View rowView = LayoutInflater.from(appContext).inflate(R.layout.recycler_list_item, null);
        PortfolioRecyclerAdapter.ViewHolder viewHolder = new PortfolioRecyclerAdapter.ViewHolder(rowView);

        presenter.onBindRepositoryRowViewAtPosition(1, viewHolder);

        assertEquals(viewHolder.getTicker().getText().toString(), "BCH");
        assertEquals(viewHolder.getLastPrice().getText().toString(), "---");
        assertEquals(viewHolder.getHoldings().getText().toString(), "---");
        assertEquals(viewHolder.getNetValue().getText().toString(), "---");

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

        Realm.init(InstrumentationRegistry.getTargetContext());

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("tests.realm")
                .build();

        Realm realm = Realm.getInstance(config);

        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        return new PortfolioPresenter(realm,
                RepositoryProvider.INSTANCE.provideQuadrigaRepository(),
                PortfolioFragment.Companion.newInstance(),
                cryptoTypesArrayList);
    }


}
