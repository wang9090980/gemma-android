package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.qrcode.zxing.QRCodeEncoder;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 通过二维码备份私钥界面
 */
public class BackUpPriKeyQRFragment extends XFragment {

    String priKey;
    Unbinder unbinder;
    @BindView(R.id.iv_fake_QR) ImageView ivFakeQR;
    @BindView(R.id.iv_real_QR) ImageView ivRealQR;
    @BindView(R.id.bt_show_QR) Button btShowQR;

    @OnClick(R.id.bt_show_QR)
    public void showQR(){

    }

    public static BackUpPriKeyQRFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyQRFragment fragment = new BackUpPriKeyQRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyQRFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_prikey_qr;
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

    public void showRealQR(String privateKey){
        ivFakeQR.setVisibility(View.GONE);
        ivRealQR.setVisibility(View.VISIBLE);
        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ivRealQR.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(privateKey, 100));

            }
        });

    }
}