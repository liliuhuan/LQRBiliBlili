package com.lqr.biliblili.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.flyco.systembar.SystemBarHelper;
import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lqr.biliblili.R;
import com.lqr.biliblili.app.base.MySupportActivity;
import com.lqr.biliblili.di.component.DaggerVideoDetailComponent;
import com.lqr.biliblili.di.module.VideoDetailModule;
import com.lqr.biliblili.mvp.contract.VideoDetailContract;
import com.lqr.biliblili.mvp.presenter.VideoDetailPresenter;
import com.lqr.biliblili.mvp.ui.adapter.VideoDetailFragmentAdapter;
import com.lqr.biliblili.mvp.ui.listener.AppBarStateChangeEvent;

import java.util.ArrayList;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class VideoDetailActivity extends MySupportActivity<VideoDetailPresenter> implements VideoDetailContract.View {

    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_av)
    TextView mTvAv;
    @BindView(R.id.tv_play_immediately)
    TextView mTvPlayImmediately;

    @BindView(R.id.tablayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerVideoDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .videoDetailModule(new VideoDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_video_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        initStatusBar();
        initAppbar();
        initToolbar();
        initViewPager();
        initFab();
    }

    private void initStatusBar() {
        //设置StatusBar透明
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setHeightAndPadding(this, mToolbar);
    }


    private void initAppbar() {
        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> showHideFab(verticalOffset));
        mAppbar.addOnOffsetChangedListener(new AppBarStateChangeEvent() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
                if (state == State.EXPANDED) {
                    mTvAv.setVisibility(View.VISIBLE);
                    mTvPlayImmediately.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    mTvAv.setVisibility(View.GONE);
                    mTvPlayImmediately.setVisibility(View.VISIBLE);
                } else {
                    mTvAv.setVisibility(View.VISIBLE);
                    mTvPlayImmediately.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initToolbar() {
        // toolbar
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // CollapsingToolbarLayout
        mCollapsingToolbarLayout.setTitleEnabled(false);// 必须关闭文字，否则Toolbar中的自定义控件位置会受影响

        mTvAv.setText("av");
    }

    private void initViewPager() {
        String summary = ArmsUtils.getString(this, R.string.v_detail_summary);
        String evaluate = getResources().getString(R.string.v_detail_evaluate, 111);
        VideoDetailFragmentAdapter videoDetailFragmentAdapter = new VideoDetailFragmentAdapter(getSupportFragmentManager(), new String[]{summary, evaluate});
        mViewPager.setAdapter(videoDetailFragmentAdapter);
        mTabLayout.setViewPager(mViewPager);
    }

    private void initFab() {

    }

    public void showHideFab(int verticalOffset) {
        if (verticalOffset == 0) {
            showFab();
        } else if (verticalOffset < 0) {
            hideFab();
        }
    }

    private void showFab() {
        mFab.animate().scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .start();
        mFab.setClickable(true);
    }

    private void hideFab() {
        mFab.animate().scaleX(0).scaleY(0)
                .setInterpolator(new AccelerateInterpolator())
                .start();
        mFab.setClickable(false);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


}
