package cn.com.sciencsys.sciencsys.dedicated;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.com.sciencsys.sciencsys.R;
import cn.com.sciencsys.sciencsys.UImaker.LabAdapter;
import cn.com.sciencsys.sciencsys.initsystem.BaseActivity;
import cn.com.sciencsys.sciencsys.initsystem.Laboratory;

public class DedicatedMainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private Laboratory [] laboratories = {new Laboratory("1",R.drawable.timg),new Laboratory("2",R.drawable.timg),
            new Laboratory("3",R.drawable.timg),new Laboratory("4",R.drawable.timg),new Laboratory("5",R.drawable.timg),
            new Laboratory("6",R.drawable.timg),new Laboratory("7",R.drawable.timg), new Laboratory("8",R.drawable.timg),
            new Laboratory("9",R.drawable.timg)};
    private List<Laboratory> laboratoryList = new ArrayList<>();
    private LabAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dedicated_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_physics);        //默认选项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        initLaboratory();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lab_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new LabAdapter(this,laboratoryList);
        recyclerView.setAdapter(adapter);
        /**
         * 下拉刷新
         */
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.lab_recyclerView_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSwipeRefresh();
            }
        });

    }
    private void initLaboratory(){
        laboratoryList.clear();
        for (int i = 0 ;i< 9;i++){
            laboratoryList.add(laboratories[i]);
        }
    }
    /**
     * 刷新列表
     */
    private void setSwipeRefresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initLaboratory();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}
