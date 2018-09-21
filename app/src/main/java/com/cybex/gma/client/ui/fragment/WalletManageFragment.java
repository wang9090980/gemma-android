package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.gma.client.event.WalletNameChangedEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.WalletManageListAdapter;
import com.cybex.gma.client.ui.model.vo.WalletVO;
import com.cybex.gma.client.utils.repeatclick.NoDoubleClick;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 管理钱包一级界面
 * 钱包主页面中点击右上角icon进入的界面
 */
public class WalletManageFragment extends XFragment {

    List<WalletVO> walletVOList = new ArrayList<>();
    @BindView(R.id.superTextView_importWallet) SuperTextView superTextViewImportWallet;
    @BindView(R.id.superTextView_createWallet) SuperTextView superTextViewCreateWallet;
    @BindView(R.id.scroll_wallet_manage) ScrollView scrollViewWalletManage;
    @BindView(R.id.recycler_wallet_manage) RecyclerView recyclerViewWalletManage;
    @BindView(R.id.tv_existed_wallet) TextView tvExistedWallet;
    @BindView(R.id.tv_match_bluetooth) SuperTextView tvMathBluetooth;

    Unbinder unbinder;
    private WalletManageListAdapter adapter;
    private final int requestCode = 0;

    public static WalletManageFragment newInstance() {
        Bundle args = new Bundle();
        WalletManageFragment fragment = new WalletManageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onWalletNameChanged(WalletNameChangedEvent event){
        if (EmptyUtils.isNotEmpty(event)){
            final int walletID = event.getWalletID();
            final String walletName = event.getWalletName();
            WalletVO vo = walletVOList.get(walletID-1);
            if (EmptyUtils.isNotEmpty(vo)){
                vo.setWalletName(walletName);
                walletVOList.clear();
                setWalletListViewData();
            }
        }
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(WalletManageFragment.this, rootView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletManage);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.title_wallet), true, true);
        superTextViewCreateWallet.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchCreateWallet(getActivity());
            }
        });

        superTextViewImportWallet.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchImportWallet(getActivity());
            }
        });

        setWalletListViewData();

        List<WalletEntity> list = DBManager.getInstance().getWalletEntityDao().getBluetoothWalletList();
        if (EmptyUtils.isNotEmpty(list)) {
            tvMathBluetooth.setVisibility(View.VISIBLE);
        } else {
            tvMathBluetooth.setVisibility(View.GONE);
        }

        tvMathBluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UISkipMananger.skipBluetoothPaireActivity(getActivity(), null);
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_manage_wallet;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1){
            final int walletID = data.getInt("walletID");
            final String walletName = data.getString("walletName");
            WalletVO vo = walletVOList.get(walletID-1);
            if (EmptyUtils.isNotEmpty(vo)){
                vo.setWalletName(walletName);
                walletVOList.clear();
                setWalletListViewData();
            }
        }
    }

    /**
     * 把钱包名称数据放入RecyclerView中显示出来
     */
    public void setWalletListViewData() {
        //从数据库中读取Wallet信息转换成WalletVO列表
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        if (walletEntityList.size() == 0){
            //如果数据库中没有钱包，隐藏已有钱包提示
            tvExistedWallet.setVisibility(View.GONE);
        }else{
            tvExistedWallet.setVisibility(View.VISIBLE);
        }

        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (!EmptyUtils.isEmpty(curWallet)){
            int chosenID = curWallet.getId();//当前被选中的钱包的ID
            for (int i = 0; i < walletEntityList.size(); i++) {
                WalletVO curWalletVO = new WalletVO();
                curWalletVO.setWalletName(walletEntityList.get(i).getWalletName());
                //设置当前钱包
                if (i+1 == chosenID){
                    curWalletVO.isSelected = true;
                }
                walletVOList.add(curWalletVO);
            }
            adapter = new WalletManageListAdapter(walletVOList);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);
        recyclerViewWalletManage.setLayoutManager(layoutManager);
        recyclerViewWalletManage.setAdapter(adapter);

        recyclerViewWalletManage.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NoDoubleClick.isDoubleClick()){
                    WalletEntity thisWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID
                            (position+1);//当前卡片对应的wallet
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("curWallet", thisWallet);
                    //startForResult(WalletDetailFragment.newInstance(bundle), requestCode);
                    //UISkipMananger.launchWalletDetail(getActivity(), bundle);
                    start(WalletDetailFragment.newInstance(bundle), SINGLETASK);
                }
            }

            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NoDoubleClick.isDoubleClick()){
                    //position与数据库表中的id对应，可直接根据position值来确定ID
                    WalletEntity thisWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID
                            (position+1);//当前卡片对应的wallet

                    if (thisWallet != null && thisWallet.getIsCurrentWallet().equals(CacheConstants.IS_CURRENT_WALLET)){
                        //如果这个钱包已经是当前钱包，不做处理
                    } else {
                        walletVOList.get(position).isSelected = true;
                        //把其他的WalletVO对象设置为未被选取

                        for (int i = 0; i < walletVOList.size(); i++){
                            if (i != position){
                                walletVOList.get(i).isSelected = false;
                            }
                        }
                        //把thisWallet设置为当前Wallet

                        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                        curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        thisWallet.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
                        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
                        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(thisWallet);
                        adapter.notifyDataSetChanged();
                        TabSelectedEvent event = new TabSelectedEvent();
                        event.setPosition(0);
                        event.setRefresh(true);
                        EventBusProvider.postSticky(event);
                        UISkipMananger.launchHome(getActivity());
                    }
                }
            }
        });
    }
}
