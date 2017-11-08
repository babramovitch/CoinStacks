package com.nebulights.crytpotracker;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo;
import com.nebulights.crytpotracker.Network.RepositoryProvider;
import com.nebulights.crytpotracker.Portfolio.CryptoAssetRepository;
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


@RunWith(AndroidJUnit4.class)
public class PortfolioPresenterTests {

    @Test
    public void createAssetNewWithRealm() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "10.00", "50.00");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoTypes.BTC);

        assertEquals("10.00", createdQuantity.toString());
    }

    @Test
    public void createAssetNewNotANumberWithRealm() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "abc", "abc");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoTypes.BTC);

        assertEquals("0.0", createdQuantity.toString());
    }

    @Test
    public void updateAssetWithRealm() throws Exception {
        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BTC, "50.50974687", "1756.87");
        presenter.createOrUpdateAsset(CryptoTypes.BTC, "97.68239875", "1756.87");
        BigDecimal createdQuantity = presenter.tickerQuantity(CryptoTypes.BTC);

        assertEquals("97.68239875", createdQuantity.toString());
    }

    //******

    @Test
    public void bindRowWithAllData() throws Exception {

        PortfolioPresenter presenter = createPresenter(getCryptoList());

        presenter.createOrUpdateAsset(CryptoTypes.BCH, "10.00", "1756.87");

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo("", "", "50.55", "", "", "", "", "");
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

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo("", "", "50.55", "", "", "", "", "");
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
    public void bindRowWithEmptyLastPrice() throws Exception {

        PortfolioPresenter presenter = createPresenter(getCryptoList());

        CurrentTradingInfo currentTradingInfoBCH = new CurrentTradingInfo("", "", "", "", "", "", "", "");
        presenter.addTickerData(currentTradingInfoBCH, CryptoTypes.BCH);

        Context appContext = InstrumentationRegistry.getTargetContext();
        View rowView = LayoutInflater.from(appContext).inflate(R.layout.recycler_list_item, null);
        PortfolioRecyclerAdapter.ViewHolder viewHolder = new PortfolioRecyclerAdapter.ViewHolder(rowView);

        presenter.onBindRepositoryRowViewAtPosition(1, viewHolder);

        assertEquals(viewHolder.getTicker().getText().toString(), "BCH");
        assertEquals(viewHolder.getLastPrice().getText().toString(), "$0.00");
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

    private PortfolioPresenter createPresenter(List<CryptoTypes> cryptoTypesArrayList) {

        Realm.init(InstrumentationRegistry.getTargetContext());

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("tests.realm")
                .build();

        Realm realm = Realm.getInstance(config);

        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        return new PortfolioPresenter(RepositoryProvider.INSTANCE.provideQuadrigaRepository(),
                PortfolioFragment.Companion.newInstance(),
                cryptoTypesArrayList,
                new CryptoAssetRepository(realm));
    }
}
