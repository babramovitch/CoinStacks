package com.nebulights.coinstacks;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.nebulights.coinstacks.Network.ExchangeProvider;
import com.nebulights.coinstacks.Network.Exchanges;
import com.nebulights.coinstacks.Network.exchanges.TradingInfo;
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetRepository;
import com.nebulights.coinstacks.Portfolio.Main.PortfolioFragment;
import com.nebulights.coinstacks.Portfolio.Main.PortfolioPresenter;
import com.nebulights.coinstacks.Portfolio.Main.PortfolioRecyclerAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PortfolioPresenterTests {

    @Test
    public void createAssetNewWithRealm() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "50.00");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoPairs.QUADRIGA_BTC_CAD);

        assertEquals("10.00", createdQuantity.toString());
    }

    @Test
    public void createAssetNewNotANumberWithRealm() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "abc", "abc");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoPairs.QUADRIGA_BTC_CAD);

        assertEquals("0.0", createdQuantity.toString());
    }

    @Test
    public void updateAssetWithRealm() throws Exception {
        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "97.68239875", "1756.87");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoPairs.QUADRIGA_BTC_CAD);

        assertEquals("97.68239875", createdQuantity.toString());
    }

    //******

    @Test
    public void bindRowWithAllData() throws Exception {

        PortfolioPresenter presenter = createPresenter();
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BTC_CAD, "10.00", "1756.87");
        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BCH_CAD, "10.00", "1756.87");

        TradingInfo currentTradingInfoBCH = new TradingInfo("50.55", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoPairs.QUADRIGA_BCH_CAD);

        Context appContext = InstrumentationRegistry.getTargetContext();
        View rowView = LayoutInflater.from(appContext).inflate(R.layout.recycler_list_item, null);
        PortfolioRecyclerAdapter.ViewHolder viewHolder = new PortfolioRecyclerAdapter.ViewHolder(rowView);

        presenter.onBindRepositoryRowViewAtPosition(1, viewHolder);

        assertEquals(viewHolder.getTicker().getText().toString(), "BCH : CAD");
        assertEquals(viewHolder.getLastPrice().getText().toString(), "$50.55");
        assertEquals(viewHolder.getHoldings().getText().toString(), "10.00");
        assertEquals(viewHolder.getNetValue().getText().toString(), "$505.50");

    }

    @Test
    public void bindRowWithEmptyLastPrice() throws Exception {

        PortfolioPresenter presenter = createPresenter();

        presenter.createOrUpdateAsset(CryptoPairs.QUADRIGA_BCH_CAD, "10.00", "10.00");

        TradingInfo currentTradingInfoBCH = new TradingInfo("", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoPairs.QUADRIGA_BCH_CAD);

        Context appContext = InstrumentationRegistry.getTargetContext();
        View rowView = LayoutInflater.from(appContext).inflate(R.layout.recycler_list_item, null);
        PortfolioRecyclerAdapter.ViewHolder viewHolder = new PortfolioRecyclerAdapter.ViewHolder(rowView);

        presenter.onBindRepositoryRowViewAtPosition(0, viewHolder);

        assertEquals(viewHolder.getTicker().getText().toString(), "BCH : CAD");
        assertEquals(viewHolder.getLastPrice().getText().toString(), "$0.00");
        assertEquals(viewHolder.getHoldings().getText().toString(), "10.00");
        assertEquals(viewHolder.getNetValue().getText().toString(), "$0.00");

    }

    //****** Helper Functions ******

    private PortfolioPresenter createPresenter() {

        Realm.init(InstrumentationRegistry.getTargetContext());

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("tests.realm")
                .build();

        Realm realm = Realm.getInstance(config);

        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        Exchanges exchange = Exchanges.INSTANCE;
        exchange.loadRepositories(ExchangeProvider.INSTANCE);

        return new PortfolioPresenter(exchange,
                PortfolioFragment.Companion.newInstance(),
                new CryptoAssetRepository(realm, PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getContext())));
    }
}
