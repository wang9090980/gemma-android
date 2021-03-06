package com.cybex.gma.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.WookongInitialedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.CollectionUtils;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.componentservice.widget.LabelsView;
import com.cybex.gma.client.BuildConfig;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.presenter.BluetoothVerifyPresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seed39.Seed39;

/**
 * 验证助记词页面
 */

public class BluetoothVerifyMneFragment extends XFragment<BluetoothVerifyPresenter> {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar)
    TitleBar btnNavibar;
    @BindView(R.id.view_click_to_show_mne)
    LabelsView viewClickToShowMne;
    @BindView(R.id.view_show_mne)
    LabelsView viewShowMne;
    @BindView(R.id.view_root)
    LinearLayout viewRoot;

    private boolean isInit;//是否为初始状态，用来调整上方显示区域大小

    List<String> selectedLabels = new ArrayList<>();//被点选的Label
    List<String> unSelectedLabels = new ArrayList<>();

    private List<String> answerLabels = new ArrayList<>();

    BlueToothWrapper.GenSeedMnesReturnValue values = null;
    String allMnemonic = null;

//    long contextHandle = 0;

//    BlueToothWrapper getAddressThread;
//    BlueToothWrapper getCheckCodeThread;
//    MainHandler mHandler;
//    final int[] deriveEOSPaths =
//            {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};

    //    private String public_key = "";
//    private String public_key_hex = "";
//    private String public_key_sign = "";
//    private String SN = "";
//    private String SN_sign = "";
    private Bundle bd;
//    private BluetoothAccountInfoVO infoVo;

    public static BluetoothVerifyMneFragment newInstance(Bundle bd) {
        BluetoothVerifyMneFragment fragment = new BluetoothVerifyMneFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothVerifyMneFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_title_verify_mne), true, true);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        isInit = true;
        initAboveLabelView();
        initBelowLabelView();

        bd = getArguments();
        if (bd != null) {
            values = bd.getParcelable(ParamConstants.KEY_GEEN_SEED);
//            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);
//            infoVo = bd.getParcelable(ParamConstants.KEY_BLUETOOTH_ACCOUNT_INFO);

            if (values != null) {
                String[] mnes = values.getStrMneWord();
                List<String> tempLabels = new ArrayList<>();
                if (EmptyUtils.isNotEmpty(mnes)) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < mnes.length; i++) {
                        String word = mnes[i];
                        tempLabels.add(word);
                        sb.append(word);
                        if (i != ( mnes.length - 1)) {
                            sb.append(" ");
                        }
                    }
                    allMnemonic=sb.toString();

                    answerLabels.addAll(tempLabels);

                    //打乱顺序
//                    Collections.shuffle(tempLabels);

                    viewShowMne.setLabels(tempLabels);
                    unSelectedLabels.addAll(tempLabels);
                }

            }
        }


        //上方助记词显示区域
        viewClickToShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                String remove = selectedLabels.remove(position);
                unSelectedLabels.add(remove);
                if(selectedLabels.size()==0){
                    initAboveLabelView();
                    isInit=true;
                }
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
            }
        });
        //下方助记词显示区域
        viewShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if (isInit) {
                    setAboveLabelView();
                }
                selectedLabels.add(label.getText().toString());
                unSelectedLabels.remove(position);
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
                isInit = false;

                doValidateResult();
            }
        });
    }

    private void resetMnemonicsUI(){

        List<String> tempLabels = new ArrayList<>();
        tempLabels.addAll(answerLabels);
        //打乱顺序
//        Collections.shuffle(tempLabels);
        viewShowMne.setLabels(tempLabels);
        unSelectedLabels.clear();
        unSelectedLabels.addAll(tempLabels);
        selectedLabels.clear();
        isInit=true;
        initAboveLabelView();
        updateAboveLabelView(selectedLabels);
        updateBottomLabelView(unSelectedLabels);
    }


    /**
     * 执行验证助记词
     */
    private void doValidateResult() {
        if (EmptyUtils.isEmpty(unSelectedLabels)) {
            if (EmptyUtils.isNotEmpty(selectedLabels) && EmptyUtils.isNotEmpty(answerLabels)) {
                if (CollectionUtils.isEqualListWithSequence(selectedLabels, answerLabels)) {
                    showProgressDialog(getString(R.string.mnemonic_checking));
                    //app验证通过，发送给硬件验证，并且获取硬件返回的公钥
                    if (values != null) {
                        int[] checkedIndex = values.getCheckIndex();
                        int[] checkIndexCount = values.getCheckIndexCount();
                        if (EmptyUtils.isNotEmpty(checkedIndex)) {
                            StringBuffer sb = new StringBuffer();
                            for (int m = 0; m < checkIndexCount[0]; m++) {
                                String label = answerLabels.get(checkedIndex[m]);
                                sb.append(label);
                                if (m != (checkIndexCount[0] - 1)) {
                                    sb.append(" ");
                                }
                            }

                            String strDestMnes = sb.toString();
                            DeviceOperationManager.getInstance().checkMnemonic(BluetoothVerifyMneFragment.this.toString(),DeviceOperationManager.getInstance().getCurrentDeviceName(), strDestMnes, new DeviceOperationManager.CheckMnemonicCallback() {
                                @Override
                                public void onCheckSuccess() {
                                    LoggerManager.d("mne validate success...");
                                    AlertUtil.showLongSuccessAlert(context,getString(R.string.eos_mne_validate_success));
                                    //create bluetoothwallet
                                    initWookongBioWallet(strDestMnes);
                                }

                                @Override
                                public void onCheckFail(int status) {
                                    if (status == -2147483642) {
                                        LoggerManager.d("device disconnected...");
                                        dissmisProgressDialog();
                                        AlertUtil.showLongUrgeAlert(context,getString(R.string.eos_mne_validate_warn));

                                    } else {
                                        LoggerManager.d("mne validate error...");
                                        dissmisProgressDialog();
                                        AlertUtil.showLongUrgeAlert(context,getString(R.string.eos_mne_validate_failed));

                                    }
                                    resetMnemonicsUI();
                                }
                            });
                        }
                    }
                } else {
                    resetMnemonicsUI();
                    AlertUtil.showLongUrgeAlert(context,getString(R.string.eos_mne_validate_failed));
                }
            }
        }

    }

    private void initWookongBioWallet(final String mnemonics) {
        showProgressDialog(getString(R.string.initing_wookong_bio_wallet));
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {


                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                multiWalletEntity.setWalletName("WOOKONG Bio");
                multiWalletEntity.setWalletType(MultiWalletEntity.WALLET_TYPE_HARDWARE);
                multiWalletEntity.setIsBackUp(CacheConstants.ALREADY_BACKUP);
                multiWalletEntity.setPasswordTip(DeviceOperationManager.getInstance().getCurrentDeviceInitPswHint());
                multiWalletEntity.setIsCurrentWallet(1);
                multiWalletEntity.setBluetoothDeviceName(DeviceOperationManager.getInstance().getCurrentDeviceName());

                //testcode
                final String mnemonic = allMnemonic;
//                String encryptMnemonic = Seed39.keyEncrypt(password, mnemonic);
//                multiWalletEntity.setMnemonic(encryptMnemonic);

                String seed = Seed39.seedByMnemonic(mnemonic);
                String privKey = Seed39.deriveRaw(seed, BaseConst.MNEMONIC_PATH_ETH);
                String publicKey = Seed39.getEthereumPublicKeyFromPrivateKey(privKey);
                String address = Seed39.getEthereumAddressFromPrivateKey(privKey);
                String eosPriv = Seed39.deriveWIF(seed, BaseConst.MNEMONIC_PATH_EOS, false);
//                String eosPrivCompress = Seed39.deriveWIF(seed, "m/44'/194'/0'/0/", true);
                LoggerManager.d(" mnemonic=" + mnemonic);
                LoggerManager.d(" seed=" + seed);
                LoggerManager.d(" privKey=" + privKey);
                LoggerManager.d(" publicKey=" + publicKey);
                LoggerManager.d(" address=" + address);
                LoggerManager.d(" eosPriv=" + eosPriv);
//                LoggerManager.d(" eosPrivCompress="+eosPrivCompress);

                EthWalletEntity ethWalletEntity = new EthWalletEntity();
                ethWalletEntity.setAddress(address);
//                ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, privKey));
                ethWalletEntity.setPublicKey(publicKey);
                ethWalletEntity.setBackUp(true);
                ethWalletEntity.setMultiWalletEntity(multiWalletEntity);

                List<EthWalletEntity> ethWalletEntities = new ArrayList<>();
                ethWalletEntities.add(ethWalletEntity);
                multiWalletEntity.setEthWalletEntities(ethWalletEntities);

                String eosPublic = JNIUtil.get_public_key(eosPriv);
                LoggerManager.d(" eosPublic=" + eosPublic);
                EosWalletEntity eosWalletEntity = new EosWalletEntity();
//                eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, eosPriv));
                eosWalletEntity.setPublicKey(eosPublic);
                eosWalletEntity.setIsBackUp(1);
                eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                List<EosWalletEntity> eosWalletEntities = new ArrayList<>();
                eosWalletEntities.add(eosWalletEntity);
                multiWalletEntity.setEosWalletEntities(eosWalletEntities);

//                LoggerManager.d("descrypt mnemonic=" + Seed39.keyDecrypt(password, encryptMnemonic));

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
                        if (BuildConfig.DEBUG) {
                            List<MultiWalletEntity> list =
                                    SQLite.select().from(MultiWalletEntity.class)
                                            .queryList();
                            LoggerManager.d("list=" + list);
                        }
                        emitter.onNext(mnemonic);
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
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String mnemonic) {
                        dissmisProgressDialog();
//                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_ENROOL_FP_PAGE)
//                                .withInt(BaseConst.KEY_INIT_TYPE, 0)
//                                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                .navigation();

                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                .withInt(BaseConst.KEY_INIT_TYPE, BaseConst.APP_HOME_INITTYPE_TO_ENROLL_FP)
//                                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .navigation();
                        if(DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().size()==1){
                            EventBusProvider.post(new WookongInitialedEvent());
                        }else{

                        }

                        getActivity().finish();
                        //                                        doGetAddressLogic();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getString(R.string.wookong_init_fail));
                        getActivity().finish();
                    }
                });

    }


    /**
     * 设置上方LabelView初始样式
     */
    public void initAboveLabelView() {
        int horizontalViewPadding = SizeUtil.dp2px(12);
        int verticalViewPadding = SizeUtil.dp2px(31f);
        viewClickToShowMne.setPadding(horizontalViewPadding,verticalViewPadding,horizontalViewPadding,verticalViewPadding);

//        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_verify_mnemonic_above));
        viewClickToShowMne.setWordMargin(SizeUtil.dp2px(7.5f));
        viewClickToShowMne.setLineMargin(SizeUtil.dp2px(11.5f));
        viewClickToShowMne.setSelectType(LabelsView.SelectType.NONE);
        viewClickToShowMne.setLabelTextSize(getResources().getDimension(R.dimen.font_4));
        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.black_content));
        viewClickToShowMne.setLabelTextStyle(true);

        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_with_black_stroke));
//        viewClickToShowMne.setWordMargin(30);
//        viewClickToShowMne.setLineMargin(40);
//        viewClickToShowMne.setSelectType(LabelsView.SelectType.SINGLE);
//        viewClickToShowMne.setLabelTextSize(42);
//        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.black_title));
//        viewClickToShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 设置上方LabelView正常显示样式
     */
    public void setAboveLabelView() {

        int horizontalViewPadding = SizeUtil.dp2px(12);
        int verticalViewPadding = SizeUtil.dp2px(15);
        viewClickToShowMne.setPadding(horizontalViewPadding,verticalViewPadding,horizontalViewPadding,verticalViewPadding);

//        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_verify_mnemonic_above));
        viewClickToShowMne.setWordMargin(SizeUtil.dp2px(7.5f));
        viewClickToShowMne.setLineMargin(SizeUtil.dp2px(11.5f));
        viewClickToShowMne.setSelectType(LabelsView.SelectType.NONE);
        viewClickToShowMne.setLabelTextSize(getResources().getDimension(R.dimen.font_4));
        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.black_content));
        viewClickToShowMne.setLabelTextStyle(true);

        int horizontalPadding = SizeUtil.dp2px(15);
        int verticalPadding = SizeUtil.dp2px(5.5f);
        viewClickToShowMne.setLabelTextPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);



        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_text));
//        viewClickToShowMne.setWordMargin(20);
//        viewClickToShowMne.setLineMargin(20);
//        viewClickToShowMne.setSelectType(LabelsView.SelectType.SINGLE);
//        viewClickToShowMne.setLabelTextSize(42);
//        viewClickToShowMne.setLabelTextColor(Color.parseColor("#333333"));
//        viewClickToShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 设置下方LabelView样式
     */
    public void initBelowLabelView() {
        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_text_clicked));
//        viewShowMne.setWordMargin(20);
//        viewShowMne.setLineMargin(20);
//        viewShowMne.setSelectType(LabelsView.SelectType.SINGLE);
//        viewShowMne.setLabelTextSize(42);
//        viewShowMne.setLabelTextColor(Color.WHITE);
//        viewShowMne.setLabelTextPadding(40, 20, 40, 20);


//        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_verify_mnemonic_bottom));
        viewShowMne.setWordMargin(SizeUtil.dp2px(7.5f));
        viewShowMne.setLineMargin(SizeUtil.dp2px(11.5f));
        viewShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewShowMne.setLabelTextSize(getResources().getDimension(R.dimen.font_4));
        viewShowMne.setLabelTextColor(Color.WHITE);
        int horizontalPadding = SizeUtil.dp2px(15);
        int verticalPadding = SizeUtil.dp2px(5.5f);
        viewShowMne.setLabelTextPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);


    }

    /**
     * 更新上方LabelView的显示
     *
     * @param data
     */
    public void updateAboveLabelView(List<String> data) {
        viewClickToShowMne.setLabels(data);
    }

    /**
     * 更新下方LabelView的显示
     *
     * @param data
     */
    public void updateBottomLabelView(List<String> data) {
        viewShowMne.setLabels(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_bluetooth_verify_mne;
    }

    @Override
    public BluetoothVerifyPresenter newP() {
        return new BluetoothVerifyPresenter();
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
    public void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
    }

    /**
     * 获取秘钥相关逻辑
     */
//    private void doGetAddressLogic() {
//        showProgressDialog(getString(R.string.eos_do_get_init_info));
//        mHandler = new MainHandler();
//        WookongBioManager.getInstance().init(mHandler);
//
//        /*
//        if ((getAddressThread == null) || (getAddressThread.getState() == Thread.State.TERMINATED)) {
//            getAddressThread = new BlueToothWrapper(mHandler);
//            getAddressThread.setGetAddressWrapper(contextHandle, 0, MiddlewareInterface.PAEW_COIN_TYPE_EOS,
//                    deriveEOSPaths);
//            getAddressThread.start();
//        }
//        */
//
//        WookongBioManager.getInstance().getAddress(contextHandle, 0, MiddlewareInterface.PAEW_COIN_TYPE_EOS,
//                deriveEOSPaths);
//    }


    /**
     * 获取
     */
    private void doGetCheckcodeLogic() {
        /*
        if ((getCheckCodeThread == null) || (getCheckCodeThread.getState() == Thread.State.TERMINATED)) {
            getCheckCodeThread = new BlueToothWrapper(new MainHandler());
            getCheckCodeThread.setGetCheckCodeWrapper(contextHandle, 0);
            getCheckCodeThread.start();
        }
        */

//        WookongBioManager.getInstance().getCheckCode(contextHandle, 0);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onReceivePollevent(AccountRegisterEvent event) {
//        if (EmptyUtils.isNotEmpty(event)) {
//            /*
//            getP().doAccountRegisterRequest(infoVo.getAccountName(),
//                    SN, SN_sign, public_key, public_key_hex, public_key_sign, infoVo.getPassword(),
//                    infoVo.getPasswordTip(), bd);
//            */
//            UISkipMananger.launchHome(getActivity());
//        }
//    }

    /**
     * 时间戳比较验证状态判断
     */
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onCreateStatusReceieved(ValidateResultEvent event){
//        if (event != null){
//            dissmisProgressDialog();
//            if(event.isSuccess()){
//                //创建成功
//                //跳转到主页面
//                AppManager.getAppManager().finishAllActivity();
//                UISkipMananger.launchHome(getActivity());
//            }else {
//                //创建失败,删除当前钱包
//                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//                DBManager.getInstance().getWalletEntityDao().deleteEntity(curWallet);
//                List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao()
//                        .getWalletEntityList();
//                //更新列表中的第一个钱包为当前钱包并跳转
//                WalletEntity newCurWallet = walletEntityList.get(0);
//                newCurWallet.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
//                DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(newCurWallet);
//                AppManager.getAppManager().finishActivity(BluetoothPairActivity.class);
//                AppManager.getAppManager().finishActivity(BluetoothSettingFPActivity.class);
//                AppManager.getAppManager().finishActivity(BluetoothScanResultDialogActivity.class);
//                AppManager.getAppManager().finishActivity(BluetoothCreatEosAccountActivity.class);
//
//                UISkipMananger.launchHome(getActivity());
//            }
//        }
//    }
    @Override
    public boolean useEventBus() {
        return true;
    }

//    class MainHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
//
//                    BlueToothWrapper.GetAddressReturnValue returnValue = (BlueToothWrapper.GetAddressReturnValue) msg.obj;
//                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        if (returnValue.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
//                            String eosAddress = returnValue.getAddress();
//                            if (EmptyUtils.isNotEmpty(eosAddress)) {
//                                String[] addressSpilt = eosAddress.split("####");
//                                if (EmptyUtils.isNotEmpty(addressSpilt)) {
//                                    public_key = addressSpilt[0];
//                                    public_key_sign = addressSpilt[1];
//
//                                    public_key_hex = ConvertUtils.str2HexStr(public_key);
//
//                                    LoggerManager.d("public_key: " + public_key);
//                                    LoggerManager.d("public_key_hex: " + public_key_hex);
//                                    LoggerManager.d("publick_key_sign: " + public_key_sign);
//
//                                    doGetCheckcodeLogic();
//                                }
//                            }
//
//                        }
//                    } else {
//                        dissmisProgressDialog();
//                    }
//                    break;
//                case BlueToothWrapper.MSG_GET_CHECK_CODE_FINISH:
//                    dissmisProgressDialog();
//                    BlueToothWrapper.GetCheckCodeReturnValue getCheckCodeReturnValue = (BlueToothWrapper.GetCheckCodeReturnValue) msg.obj;
//                    if (getCheckCodeReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        byte[] checkedcode = getCheckCodeReturnValue.getCheckCode();
//                        byte[] snbyte = ConvertUtils.subByte(checkedcode, 0, 16);
////                         byte[] snSignByte = ConvertUtils.subByte(checkedcode,17,64);
//
//                        SN = CommonUtility.byte2hex(snbyte);
//                        SN_sign = CommonUtility.byte2hex(checkedcode);
//                        SN_sign = SN_sign.substring(32);
//                        LoggerManager.d("SN: " + SN);
//                        LoggerManager.d("SN_sign: " + SN_sign);
//
//                        if (infoVo != null) {
//                            EventBusProvider.post(new AccountRegisterEvent());
//                        }
//
//                    }
//                    break;
//                case BlueToothWrapper.MSG_DEVICE_DISCONNECTED:
//                    dissmisProgressDialog();
//                    TSnackbarUtil.showTip(viewRoot, getString(R.string.eos_mne_validate_warn),
//                            Prompt.WARNING);
//                    break;
//                default:
//                    break;
//            }
//
//        }
//    }
}
