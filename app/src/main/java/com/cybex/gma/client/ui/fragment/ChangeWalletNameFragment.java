package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 修改钱包名页面
 */
public class ChangeWalletNameFragment extends XFragment {

    @BindView(R.id.editText_setWalletName) MaterialEditText setWalletName;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.clear_wallet_name) ImageView clearWalletName;
    Unbinder unbinder;

    @OnClick(R.id.clear_wallet_name)
    public void setClearWalletName(){
        setWalletName.setText("");
    }

    public static ChangeWalletNameFragment newInstance() {
        Bundle args = new Bundle();
        ChangeWalletNameFragment fragment = new ChangeWalletNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("管理钱包", true);
        mTitleBar.setActionTextColor(getResources().getColor(R.color.whiteTwo));
        mTitleBar.setActionTextSize(18);
        mTitleBar.addAction(new TitleBar.TextAction("保存") {
            @Override
            public void performAction(View view) {
                //todo 保存钱包名
            }
        });

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_change_walletname;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}