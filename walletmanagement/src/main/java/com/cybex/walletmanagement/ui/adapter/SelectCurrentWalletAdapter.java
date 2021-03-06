package com.cybex.walletmanagement.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.walletmanagement.R;

import java.util.List;

import me.jessyan.autosize.AutoSize;

public class SelectCurrentWalletAdapter extends BaseQuickAdapter<MultiWalletEntity, BaseViewHolder> {

    private Activity activity;

    public SelectCurrentWalletAdapter(@Nullable List<MultiWalletEntity> data,Activity activity) {
        super(R.layout.walletmanage_item_select_current_wallet, data);
        this.activity=activity;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiWalletEntity item) {
        TextView tvWalletName = helper.getView(R.id.tv_wallet_name);
        TextView tvWalletType = helper.getView(R.id.tv_wallet_type);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        ImageView ivWookong = helper.getView(R.id.iv_wookong);
//        ImageView ivTip = helper.getView(R.id.iv_tip);

        tvWalletName.setText(item.getWalletName());

        helper.addOnClickListener(R.id.rootview_wallet);
        helper.addOnClickListener(R.id.iv_tip);

        ivWookong.setVisibility(item.getWalletType()==MultiWalletEntity.WALLET_TYPE_HARDWARE?View.VISIBLE:View.GONE);
        ivSelect.setVisibility(item.isChecked()?View.VISIBLE:View.INVISIBLE);

        if(item.getWalletType()==MultiWalletEntity.WALLET_TYPE_HARDWARE){
            tvWalletType.setText(R.string.walletmanage_wallet_type_hardware);
        }else if(item.getWalletType()==MultiWalletEntity.WALLET_TYPE_MNEMONIC||item.getWalletType()==MultiWalletEntity.WALLET_TYPE_IMPORT_MNEMONIC){
            tvWalletType.setText(R.string.walletmanage_wallet_type_multi);
        }else{
            if(item.getEosWalletEntities().size()>0&&item.getEthWalletEntities().size()>0){
                tvWalletType.setText(R.string.walletmanage_wallet_type_multi);
            }else if(item.getEosWalletEntities().size()>0){
                tvWalletType.setText(R.string.walletmanage_wallet_type_eos);
            }else{
                tvWalletType.setText(R.string.walletmanage_wallet_type_eth);
            }
        }
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(activity!=null){
            AutoSize.autoConvertDensityOfGlobal(activity);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(activity!=null){
            AutoSize.autoConvertDensityOfGlobal(activity);
        }
        super.onBindViewHolder(holder, position);
    }
}
