package com.nuzharukiya.authlauncher.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nuzharukiya.authlauncher.R;
import com.nuzharukiya.authlauncher.adapters.CherryAdapter;
import com.nuzharukiya.authlauncher.model.CherryModel;
import com.nuzharukiya.authlauncher.utils.HzPreferences;
import com.nuzharukiya.authlauncher.utils.UIBase;
import com.nuzharukiya.authlauncher.utils.UIComponents;

import java.util.ArrayList;

import static com.nuzharukiya.authlauncher.utils.Constants.HZ_A;
import static com.nuzharukiya.authlauncher.utils.Constants.HZ_B;

public class DashboardActivity extends AppCompatActivity implements
        UIBase {

    private Context context;

    // Cherries
    private RecyclerView rvCherries;
    private CherryAdapter cherryAdapter;
    private ArrayList<CherryModel> cherryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initApp();
        initViews();
    }

    @Override
    public void initApp() {
        context = this;

        UIComponents uiComponents = new UIComponents(context, true);
        uiComponents.setToolbarItems(R.drawable.ic_menu, R.string.activity_dashboard);
    }

    @Override
    public void initViews() {
        rvCherries = findViewById(R.id.rvCherries);

        initCherryAdapter();
        prepareCherries();
    }

    private void initCherryAdapter() {
        initCherryList();

        cherryAdapter = new CherryAdapter(context, cherryList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvCherries.setLayoutManager(mLayoutManager);
        rvCherries.setItemAnimator(new DefaultItemAnimator());
        rvCherries.setAdapter(cherryAdapter);
    }

    private void prepareCherries() {
        CherryModel cherryModel;

        cherryModel = new CherryModel();
        cherryModel.setCherryTitle("AT=" + HzPreferences.getAccessToken());
        cherryModel.setCherryId(HZ_A);
        cherryList.add(cherryModel);

        cherryModel = new CherryModel();
        cherryModel.setCherryTitle("RT=" + HzPreferences.getRefreshToken());
        cherryModel.setCherryId(HZ_B);
        cherryList.add(cherryModel);

        cherryAdapter.notifyDataSetChanged();
    }

    private void initCherryList() {
        if (cherryList == null) {
            cherryList = new ArrayList<>();
        }
    }
}