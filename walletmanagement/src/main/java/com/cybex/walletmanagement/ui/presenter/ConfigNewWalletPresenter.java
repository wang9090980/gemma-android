package com.cybex.walletmanagement.ui.presenter;


import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.CloseInitialPageEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.walletmanagement.BuildConfig;
import com.cybex.walletmanagement.ui.activity.ConfigNewWalletActivity;
import com.cybex.walletmanagement.ui.model.ImportWalletConfigBean;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seed39.Seed39;

public class ConfigNewWalletPresenter extends XPresenter<ConfigNewWalletActivity> {

    @Override
    protected ConfigNewWalletActivity getV() {
        return super.getV();
    }

    /**
     * 钱包名称是否在数据库存在
     *
     * @return boolean
     */
    public boolean isWalletNameValid() {
        String walletName = getV().getWalletName();
        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntity(walletName);
        return multiWalletEntity == null;
    }

    public boolean isPasswordMatch() {
        return getV().getPassword().equals(getV().getRepeatPassword());
    }

    public void importWallet(final String walletName, final String password, final String passwordHint) {

        getV().showProgressDialog("");
        final ImportWalletConfigBean configBean = getV().getConfigBean();
        //LoggerManager.d("configBean=" + configBean);

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(final ObservableEmitter<Object> emitter) throws Exception {
                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                int walletType = configBean.getWalletType();
                multiWalletEntity.setWalletName(walletName);
                multiWalletEntity.setWalletType(walletType);
                multiWalletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
                multiWalletEntity.setPasswordTip(passwordHint);
                multiWalletEntity.setCypher(HashGenUtil.generateHashFromText(password, HashGenUtil.TYPE_SHA256));
                multiWalletEntity.setIsCurrentWallet(1);

                if(walletType==MultiWalletEntity.WALLET_TYPE_IMPORT_MNEMONIC){
                    String encryptMnemonic = Seed39.keyEncrypt(password, configBean.getMnemonic());
                    multiWalletEntity.setMnemonic(encryptMnemonic);

                    String seed = Seed39.seedByMnemonic(configBean.getMnemonic());
                    String privKey = Seed39.deriveRaw(seed, BaseConst.MNEMONIC_PATH_ETH);
                    String publicKey = Seed39.getEthereumPublicKeyFromPrivateKey(privKey);
                    String address = Seed39.getEthereumAddressFromPrivateKey(privKey);
                    String eosPriv = Seed39.deriveWIF(seed, BaseConst.MNEMONIC_PATH_EOS, false);

                    EthWalletEntity ethWalletEntity = new EthWalletEntity();
                    ethWalletEntity.setAddress(address);
                    ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, privKey));
                    ethWalletEntity.setPublicKey(publicKey);
                    ethWalletEntity.setBackUp(false);
                    ethWalletEntity.setMultiWalletEntity(multiWalletEntity);
                    List<EthWalletEntity> ethWalletEntities=new ArrayList<>();
                    ethWalletEntities.add(ethWalletEntity);
                    multiWalletEntity.setEthWalletEntities(ethWalletEntities);


                    String eosPublic = JNIUtil.get_public_key(eosPriv);
                    EosWalletEntity eosWalletEntity = new EosWalletEntity();
                    eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, eosPriv));
                    eosWalletEntity.setPublicKey(eosPublic);
                    eosWalletEntity.setIsBackUp(0);
                    eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                    List<EosWalletEntity> eosWalletEntities=new ArrayList<>();
                    eosWalletEntities.add(eosWalletEntity);
                    multiWalletEntity.setEosWalletEntities(eosWalletEntities);

                }else if(walletType==MultiWalletEntity.WALLET_TYPE_PRI_KEY){

                    if(CoinType.ETH.equals(configBean.getCoinType())){

                        String privKey=configBean.getPriKey();
                        String publicKey = Seed39.getEthereumPublicKeyFromPrivateKey(privKey);
                        String address = Seed39.getEthereumAddressFromPrivateKey(privKey);

                        EthWalletEntity ethWalletEntity = new EthWalletEntity();

                        ethWalletEntity.setAddress(address);
                        ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, privKey));
                        ethWalletEntity.setPublicKey(publicKey);
                        ethWalletEntity.setBackUp(false);
                        ethWalletEntity.setMultiWalletEntity(multiWalletEntity);
                        List<EthWalletEntity> ethWalletEntities=new ArrayList<>();
                        ethWalletEntities.add(ethWalletEntity);
                        multiWalletEntity.setEthWalletEntities(ethWalletEntities);
                        LoggerManager.d(" configBean.getPriKey()="+privKey
                                +"   publicKey="+publicKey +"   address="+address
                        );
                    }else if(CoinType.EOS.equals(configBean.getCoinType())){
                        String eosPriv=configBean.getPriKey();
                        String eosPublic = JNIUtil.get_public_key(eosPriv);
                        EosWalletEntity eosWalletEntity = new EosWalletEntity();
                        eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, eosPriv));
                        eosWalletEntity.setPublicKey(eosPublic);
                        eosWalletEntity.setIsBackUp(0);
                        eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                        List<EosWalletEntity> eosWalletEntities=new ArrayList<>();
                        eosWalletEntities.add(eosWalletEntity);
                        multiWalletEntity.setEosWalletEntities(eosWalletEntities);
                    }
                }

                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
                int walletCount = multiWalletEntityList.size();
                if (walletCount > 0) {
                    MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                    if (currentMultiWalletEntity != null) {
                        currentMultiWalletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        currentMultiWalletEntity.save();
                    }
                }

                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(multiWalletEntity, new DBCallback() {
                    @Override
                    public void onSucceed() {
                        if(BuildConfig.DEBUG) {
                            List<MultiWalletEntity> list =
                                    SQLite.select().from(MultiWalletEntity.class)
                                            .queryList();
                            LoggerManager.d("list=" + list);
                        }

                        int currentIndex = SPUtils.getInstance().getInt(BaseConst.INITIAL_WALLET_INDEX_KEY, 1);
                        if((BaseConst.INITIAL_WALLET_NAME_PREFIX+currentIndex).equals(walletName)){
                            SPUtils.getInstance().put(BaseConst.INITIAL_WALLET_INDEX_KEY,++currentIndex);
                        }

                        emitter.onNext(new Object());
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailed(Throwable error) {
                        emitter.onError(error);
                        emitter.onComplete();
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object mnemonic) {
                        getV().dissmisProgressDialog();



                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                .navigation();
                        if(getV().isInitial()){
                            EventBusProvider.post(new CloseInitialPageEvent());
                        }
                        getV().finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LoggerManager.d("throwable="+throwable.getMessage());
                        getV().dissmisProgressDialog();
                    }
                });


    }


    public boolean isPasswordValid() {
        return FormatValidateUtils.isPasswordValid(getV().getPassword());
    }

    @Override
    public void detachV() {
        super.detachV();

    }
}