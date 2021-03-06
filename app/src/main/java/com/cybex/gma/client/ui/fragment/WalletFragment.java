//package com.cybex.gma.client.ui.fragment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.constraint.ConstraintLayout;
//import android.support.v4.widget.NestedScrollView;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.allen.library.SuperTextView;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.cybex.base.view.progress.RoundCornerProgressBar;
//import com.cybex.base.view.refresh.CommonRefreshLayout;
//import com.cybex.componentservice.api.ApiPath;
//import com.cybex.componentservice.config.CacheConstants;
//import com.cybex.componentservice.db.entity.WalletEntity;
//import com.cybex.componentservice.manager.DBManager;
//import com.cybex.componentservice.manager.LoggerManager;
//import com.cybex.componentservice.utils.ClipboardUtils;
//import com.cybex.gma.client.R;
//import com.cybex.gma.client.config.ParamConstants;
//import com.cybex.gma.client.event.ChangeAccountEvent;
//import com.cybex.gma.client.event.PollEvent;
//import com.cybex.gma.client.event.TabSelectedEvent;
//import com.cybex.gma.client.event.ValidateResultEvent;
//import com.cybex.gma.client.manager.UISkipMananger;
//import com.cybex.gma.client.ui.activity.CreateManageActivity;
//import com.cybex.gma.client.ui.adapter.ChangeAccountAdapter;
//import com.cybex.gma.client.ui.model.response.AccountInfo;
//import com.cybex.gma.client.ui.model.response.AccountRefoundRequest;
//import com.cybex.gma.client.ui.model.vo.EOSNameVO;
//import com.cybex.gma.client.ui.model.vo.HomeCombineDataVO;
//import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
//import com.cybex.gma.client.ui.presenter.WalletPresenter;
//import com.cybex.componentservice.utils.AmountUtil;
//import com.hxlx.core.lib.common.async.TaskManager;
//import com.hxlx.core.lib.common.eventbus.EventBusProvider;
//import com.hxlx.core.lib.mvp.lite.XFragment;
//import com.hxlx.core.lib.utils.EmptyUtils;
//import com.hxlx.core.lib.utils.GsonUtils;
//import com.hxlx.core.lib.utils.SPUtils;
//import com.hxlx.core.lib.utils.common.utils.AppManager;
//import com.hxlx.core.lib.utils.common.utils.DateUtil;
//import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
//import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
//import com.siberiadante.customdialoglib.CustomFullDialog;
//import com.tapadoo.alerter.Alerter;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//import io.hypertrack.smart_scheduler.SmartScheduler;
//
///**
// * 钱包Fragment
// *
// * Created by wanglin on 2018/7/9.
// */
//public class WalletFragment extends XFragment<WalletPresenter> {
//
//    @BindView(R.id.view_resource_manage) ConstraintLayout viewResourceManage;
//    @BindView(R.id.tv_number_of_tokens) TextView tvNumberOfTokens;
//    @BindView(R.id.view_eos_tokens) LinearLayout viewEosTokens;
//    @BindView(R.id.tv_currency_type) TextView tvCurrencyType;
//    @BindView(R.id.iv_eos_asset_logo) ImageView ivEosAssetLogo;
//    @BindView(R.id.tv_eos) TextView tvEos;
//    @BindView(R.id.tv_eos_amount) TextView tvEosAmount;
//    @BindView(R.id.tv_eos_value) TextView tvEosValue;
//    @BindView(R.id.view_soft_wallet) ConstraintLayout viewSoftWallet;
//    @BindView(R.id.tv_account_name) TextView tvAccountName;
//    @BindView(R.id.iv_back) ImageView ivBack;
//    @BindView(R.id.iv_copy) ImageView ivCopy;
//    @BindView(R.id.tv_current_account) TextView tvCurrentAccount;
//    @BindView(R.id.iv_change_account) ImageView ivChangeAccount;
//    @BindView(R.id.view_top_navigation) ConstraintLayout viewTopNavigation;
//    @BindView(R.id.show_cpu) LinearLayout showCpu;
//    @BindView(R.id.tv_assets) TextView tvAssets;
//    private WalletEntity curWallet;
//    private int walletID;
//    private String curEosUsername;
//    @BindView(R.id.total_EOS_amount) TextView totalEOSAmount;
//    @BindView(R.id.total_CNY_amount) TextView totalCNYAmount;
//    @BindView(R.id.balance) SuperTextView tvBalance;
//    @BindView(R.id.redeem) SuperTextView tvRedeem;
//    @BindView(R.id.layout_top_info) LinearLayout layoutTopInfo;
//    @BindView(R.id.layout_info) ConstraintLayout layoutInfo;
//    @BindView(R.id.scroll_wallet_tab) NestedScrollView mScrollView;
//    @BindView(R.id.progressbar_cpu_small) RoundCornerProgressBar progressBarCPU;
//    @BindView(R.id.progressbar_net_small) RoundCornerProgressBar progressBarNET;
//    @BindView(R.id.progressbar_ram_small) RoundCornerProgressBar progressBarRAM;
//    @BindView(R.id.view_cpu) View viewCPU;
//    @BindView(R.id.view_net) View viewNET;
//    @BindView(R.id.view_ram) View viewRAM;
//    @BindView(R.id.view_refresh_wallet) CommonRefreshLayout refreshLayout;
//    private Bundle bundle;
//    Unbinder unbinder;
//
//    private ResourceInfoVO resourceInfoVO;
//    private String curUSDTPrice;
//    private int savedCurrency;
//
//    @OnClick(R.id.view_resource_manage)
//    public void goResourceDetail() {
//        bundle.putString(ParamConstants.EOS_ASSET_VALUE, getAssetsValue());
//        bundle.putString(ParamConstants.EOS_AMOUNT, getEosAmount());
//        LoggerManager.d("assetsValue",  getAssetsValue());
//        UISkipMananger.launchAssetDetail(getActivity(), bundle);
//    }
//
//    @OnClick({R.id.view_cpu, R.id.view_net, R.id.view_ram})
//    public void clickViews(View view) {
//        switch (view.getId()) {
//            case R.id.view_cpu:
//                goResourceManagement();
//                break;
//            case R.id.view_net:
//                goResourceManagement();
//                break;
//            case R.id.view_ram:
//                goResourceManagement();
//                break;
//        }
//    }
//
//    @OnClick({R.id.iv_copy, R.id.iv_back})
//    public void onNavigationBarClicked(View v){
//        switch (v.getId()){
//            case R.id.iv_copy:
//                //复制账户名
//                if (getActivity() != null){
//                    ClipboardUtils.copyText(getActivity(), getAccountName());
//                    GemmaToastUtils.showLongToast(getString(R.string.eos_copy_success));
//                }
//                break;
//            case R.id.iv_back:
//                if (getActivity() != null){
//                    getActivity().finish();
//                }
//                break;
//        }
//    }
//
//    public String getAccountName(){
//        return tvAccountName.getText().toString().trim();
//    }
//
//    public void goResourceManagement() {
//
//        UISkipMananger.launchResourceDetail(getActivity(), bundle);
//    }
//
//    /*
//    @OnClick(R.id.textView_username)
//    public void backUpWallet(View v) {
//        switch (v.getId()) {
//            case R.id.textView_username:
//                showChangeEOSNameDialog();
//                break;
//            default:
//                break;
//        }
//    }
//    */
//
//    @Override
//    public boolean useEventBus() {
//        return true;
//    }
//
//
//    public static WalletFragment newInstance() {
//        Bundle args = new Bundle();
//        WalletFragment fragment = new WalletFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onReceivePollevent(PollEvent pollEvent) {
//        if (EmptyUtils.isNotEmpty(pollEvent) && pollEvent.isDone()) {
//            if (Alerter.isShowing()) {
//                Alerter.hide();
//                LoggerManager.d("Alert Hide");
//            }
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onTabSelctedEvent(TabSelectedEvent event) {
//        if (EmptyUtils.isNotEmpty(event) && event.getPosition() == 0) {
//            if (event.isRefresh()) {
//                getP().requestHomeCombineDataVO();
//            }
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onChangeAccountEvent(ChangeAccountEvent event) {
//        if (EmptyUtils.isNotEmpty(event)) {
//            LoggerManager.d("---changeAccount event---");
//            getP().requestHomeCombineDataVO();
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onValidateConfirmed(ValidateResultEvent event) {
//        if (event.isSuccess()) {
//            Alerter.hide();
//        }
//    }
//
//    /**
//     * 显示主界面信息
//     *
//     * @param vo 账户聚合数据信息
//     */
//    public void showMainInfo(final HomeCombineDataVO vo) {
//
//        TaskManager.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                if (vo != null) {
//
//                    //String jsonVO = GsonUtils.objectToJson(vo);
//                    //SPUtils.getInstance().put("HomeCombineData", jsonVO);
//
//                    String banlance = vo.getBanlance();
//                    String unitPriceEOS = vo.getUnitPrice();
//                    curUSDTPrice = vo.getUnitPriceUSDT();
//                    AccountInfo info = vo.getAccountInfo();
//
//
//                    //显示总资产信息
//                    showTotalPriceInfo(banlance, unitPriceEOS, info);
//                    //显示cpu，net，ram进度
//                    showResourceInfo(banlance, info);
//                    //显示赎回信息
//                    showRefoundInfo(info);
//                }
//            }
//        });
//
//    }
//
//
//    /**
//     * 显示赎回信息
//     *
//     * 上一次请求时间 需要加上72小时跟本地时间对比
//     */
//    private void showRefoundInfo(AccountInfo info) {
//        if (info != null) {
//            AccountRefoundRequest request = info.getRefund_request();
//            if (request != null) {
//                String requestTime = request.request_time;
//                if (EmptyUtils.isNotEmpty(requestTime)) {
//                    long requestOldMills = DateUtil.getMills(requestTime, DateUtil.Format.EOS_DATE_FORMAT);
//                    requestOldMills = requestOldMills + 72 * 60 * 60 * 1000;
//                    LoggerManager.d("newRequestOldMills", requestOldMills);
//                    String refoundTime = getP().dateDistance2now(requestOldMills,
//                            DateUtil.Format.EOS_DATE_FORMAT_WITH_MILLI);
//
//                    String netAmount = request.net_amount;
//                    String netNum = "0";
//                    if (EmptyUtils.isNotEmpty(netAmount)) {
//                        String[] netArr = netAmount.split(" ");
//                        if (EmptyUtils.isNotEmpty(netArr)) {
//                            netNum = netArr[0];
//                        }
//                    }
//
//                    String cpuAmount = request.cpu_amount;
//                    String cpuNum = "0";
//                    if (EmptyUtils.isNotEmpty(cpuAmount)) {
//                        String[] cpuArr = cpuAmount.split(" ");
//                        if (EmptyUtils.isNotEmpty(cpuArr)) {
//                            cpuNum = cpuArr[0];
//                        }
//                    }
//
//                    String totalNum = AmountUtil.add(netNum, cpuNum, 4);
//
//                    if (Double.parseDouble(totalNum) > 0) {
//                        String totalRefound = totalNum + " EOS";
//                        tvRedeem.setRightString(totalRefound);
//                    }
//
//                    if (EmptyUtils.isNotEmpty(refoundTime)) {
//                        tvRedeem.setRightBottomString(refoundTime);
//
//                    }
//
//                }
//            }
//
//        }
//
//    }
//
//    private void showResourceInfo(String banlance, AccountInfo info) {
//        if (info != null) {
//            //CPU使用进度
//            AccountInfo.CpuLimitBean cpuLimitBean = info.getCpu_limit();
//            int cpuUsed = 0;
//            int cpuTotal = 0;
//            if (cpuLimitBean != null) {
//                cpuUsed = cpuLimitBean.getUsed();
//                cpuTotal = cpuLimitBean.getMax();
//            }
//            double i = cpuUsed / (double) cpuTotal * 100;
//            int cpuProgress = (int) Math.ceil(i);
//            progressBarCPU.setMax(100);
//            progressBarCPU.setProgress(cpuProgress);
//            setProgressColor(progressBarCPU, cpuProgress);
//
//
//            //NET使用进度
//            AccountInfo.NetLimitBean netLimitBean = info.getNet_limit();
//            int netUsed = 0;
//            int netTotal = 0;
//            if (netLimitBean != null) {
//                netUsed = netLimitBean.getUsed();
//                netTotal = netLimitBean.getMax();
//            }
//            double j = netUsed / (double) netTotal * 100;
//            int netProgress = (int) Math.ceil(j);
//            progressBarNET.setMax(100);
//            progressBarNET.setProgress(netProgress);
//            setProgressColor(progressBarNET, netProgress);
//
//
//            //ram使用进度
//            int ramTotal = info.getRam_quota();
//            int ramUsed = info.getRam_usage();
//            double k = ramUsed / (double) ramTotal * 100;
//            int ramProgress = (int) Math.ceil(k);
//            progressBarRAM.setMax(100);
//            progressBarRAM.setProgress(ramProgress);
//            setProgressColor(progressBarRAM, ramProgress);
//
//            resourceInfoVO = new ResourceInfoVO();
//            resourceInfoVO.setBanlance(banlance);
//            resourceInfoVO.setCpuProgress(cpuProgress);
//            resourceInfoVO.setCpuUsed(cpuUsed);
//            resourceInfoVO.setCpuTotal(cpuTotal);
//            resourceInfoVO.setNetTotal(netTotal);
//            resourceInfoVO.setNetProgress(netProgress);
//            resourceInfoVO.setNetUsed(netUsed);
//            resourceInfoVO.setRamUsed(ramUsed);
//            resourceInfoVO.setRamProgress(ramProgress);
//            resourceInfoVO.setRamTotal(ramTotal);
//            resourceInfoVO.setCpuWeight(info.getCpu_weight());
//            resourceInfoVO.setNetWeight(info.getNet_weight());
//            bundle.putParcelable("resourceInfo", resourceInfoVO);
//        }
//
//    }
//
//    private void setProgressColor(RoundCornerProgressBar progressBar, int progress) {
//        if (progress < 85) {
//            progressBar.setProgressColor(getResources().getColor(R.color.black_context));
//        } else {
//            progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
//        }
//    }
//
//    private void showTotalPriceInfo(String banlance, String unitPrice, AccountInfo info) {
//        String banlanceNumber = "0";
//        String netNumber;
//        String cpuNumber;
//        String totalPrice = "0";
//        String[] banlanceNumberArr = banlance.split(" ");
//        if (EmptyUtils.isNotEmpty(banlanceNumberArr)) {
//            banlanceNumber = banlanceNumberArr[0];
//        }
//        if (info != null) {
//            AccountInfo.SelfDelegatedBandwidthBean selfDelegatedBandwidth = info.getSelf_delegated_bandwidth();
//            if (selfDelegatedBandwidth == null) {
//                //没有自己给自己抵押的资源,总资产为可用余额
//                totalPrice = banlanceNumber;
//            } else {
//                //有抵押资源，总资产为可用余额加上抵押资源
//                String[] cpuNumber_arr = selfDelegatedBandwidth.getCpu_weightX().split(" ");
//                String[] netNumber_arr = selfDelegatedBandwidth.getNet_weightX().split(" ");
//
//                cpuNumber = cpuNumber_arr[0];
//                netNumber = netNumber_arr[0];
//
//                String totalResource = AmountUtil.add(cpuNumber, netNumber, 4);
//                totalPrice = AmountUtil.add(totalResource, banlanceNumber, 4);
//            }
//
//        }
//
//
//        String totalCNY = AmountUtil.mul(unitPrice, totalPrice, 4);
//        String totalUSD = AmountUtil.div(totalCNY, curUSDTPrice, 4);
//        totalEOSAmount.setText(totalPrice);
//        tvEosAmount.setText(totalPrice);
//        savedCurrency = SPUtils.getInstance().getInt("currency_unit");
//        switch (savedCurrency) {
//            case CacheConstants.CURRENCY_CNY:
//                tvCurrencyType.setText("≈ ¥ ");
//                tvEosValue.setText(" ≈ " + totalCNY);
//                totalCNYAmount.setText(totalCNY);
//                break;
//            case CacheConstants.CURRENCY_USD:
//                tvCurrencyType.setText("≈ $ ");
//                totalCNYAmount.setText(" ≈ " + totalUSD);
//                tvEosValue.setText(totalUSD);
//                break;
//            default:
//                tvCurrencyType.setText("≈ ¥ ");
//                tvEosValue.setText(" ≈ " + totalCNY);
//                totalCNYAmount.setText(totalCNY);
//                break;
//        }
//
//    }
//
//    /**
//     * 账户余额
//     *
//     * @param banlance
//     */
//    public void showBanlance(String banlance) {
//        TaskManager.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                tvBalance.setRightString(banlance);
//            }
//        });
//
//    }
//
//    @Override
//    public void bindUI(View rootView) {
//        unbinder = ButterKnife.bind(this, rootView);
//        //OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
//    }
//
//    @Override
//    public void initData(Bundle savedInstanceState) {
//        bundle = new Bundle();
//        //初始化当前节点
//        if (SPUtils.getInstance().getString("curNode") != null) {
//            String curHost = SPUtils.getInstance().getString("curNode");
//            LoggerManager.d("curNode", curHost);
//            //ApiPath.setHOST_ON_CHAIN(curHost);
//        } else {
//            SPUtils.getInstance().put("curNode", ApiPath.EOS_CYBEX);
//        }
//
//        LoggerManager.d("host", ApiPath.getHOST_ON_CHAIN());
//        AppManager.getAppManager().finishActivity(CreateManageActivity.class);
//        //下拉刷新
//        refreshLayout.setEnableLoadmore(false);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                refreshlayout.finishRefresh(200);
//                getP().requestHomeCombineDataVO();
//            }
//        });
//
//        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//        if (EmptyUtils.isNotEmpty(curWallet)) {
//            curEosUsername = curWallet.getCurrentEosName();
//            //textViewUsername.setText(curWallet.getCurrentEosName());
//            tvAccountName.setText(curWallet.getCurrentEosName());
//            tvCurrentAccount.setText(curWallet.getCurrentEosName());
//            //生成头像
//            //generatePortrait(curWallet.getCurrentEosName());
//            //设置Alert
//            if (curWallet.getIsConfirmLib().equals(CacheConstants.NOT_CONFIRMED) && getActivity() != null) {
//                if (Alerter.isShowing()) {
//                    Alerter.create(getActivity())
//                            .setText(getResources().getString(R.string.eos_please_confirm_alert))
//                            .setBackgroundColorRes(R.color.scarlet)
//                            .enableInfiniteDuration(true)
//                            .setTextAppearance(R.style.myAlert)
//                            .show();
//                }
//            }
//            //多账户切换
//            String json = curWallet.getEosNameJson();
//            List<String> eosNamelist = GsonUtils.parseString2List(json, String.class);
//            if (EmptyUtils.isNotEmpty(eosNamelist) && eosNamelist.size() > 1) {
//                //如果当前有多个eos账户
//
//                /*
//                Drawable drawable = getResources().getDrawable(
//                        R.drawable.ic_common_drop_white);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                        drawable.getMinimumHeight());
//                textViewUsername.setCompoundDrawables(null, null, drawable, null);
//
//                textViewUsername.setClickable(true);
//                */
//            } else {
//                //只有一个eos账户
//                //textViewUsername.setCompoundDrawables(null, null, null, null);
//                //textViewUsername.setClickable(false);
//
//            }
//
//        } else {
//            //textViewUsername.setCompoundDrawables(null, null, null, null);
//
//            //textViewUsername.setClickable(false);
//        }
//
//
//        //初始化总资产法币估值显示
//        switch (SPUtils.getInstance().getInt("currency_unit")) {
//            case CacheConstants.CURRENCY_CNY:
//                tvCurrencyType.setText("≈ ¥ ");
//                totalCNYAmount.setText(" -- ");
//                break;
//            case CacheConstants.CURRENCY_USD:
//                tvCurrencyType.setText("≈ $ ");
//                totalCNYAmount.setText(" -- ");
//                break;
//            default:
//                tvCurrencyType.setText("≈ ¥ ");
//                totalCNYAmount.setText(" -- ");
//        }
//
//        //滑动监听,设置上滑文字渐显效果
//        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (getContext() != null) {
//                    float dpY = px2dp(getContext(), scrollY);
//                    if (dpY > 14) {
//                        if (curWallet != null) {
//                            curEosUsername = curWallet.getCurrentEosName();
//                            mTitleBar.setTitle(curEosUsername);
//                            TextView title = mTitleBar.getmCenterText();
//                            float alpha = (dpY * (float) 1.29 + (float) 1.94) / 100;
//                            title.setAlpha(alpha);
//                        }
//
//
//                    } else {
//                        TextView title = mTitleBar.getmCenterText();
//                        title.setAlpha(1);
//                        mTitleBar.setTitle(getString(R.string.eos_app_name));
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void setNavibarTitle(String title, boolean isShowBack) {
//        super.setNavibarTitle(title, isShowBack);
//        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add_wallet) {
//            @Override
//            public void performAction(View view) {
//                UISkipMananger.launchWalletManagement(getActivity());
//            }
//        });
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.eos_fragment_wallet;
//    }
//
//    @Override
//    public WalletPresenter newP() {
//        return new WalletPresenter();
//    }
//
//    /*
//    public void generatePortrait(String eosName) {
//        String hash = EncryptationManager.getEncrypt().encryptSHA256(eosName);
//        final String str = AvatarHelper.Companion.getInstance().getAvatarSvg(hash, 62, null);
//        Sharp.loadString(str).into(imageViewPortrait);
//    }
//    */
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//        if (EmptyUtils.isNotEmpty(curWallet)) {
//            tvAccountName.setText(curWallet.getCurrentEosName());
//            //generatePortrait(curWallet.getCurrentEosName());
//            if (curWallet.getIsConfirmLib().equals(CacheConstants.NOT_CONFIRMED) && getActivity() != null) {
//                Alerter.create(getActivity())
//                        .setText(getResources().getString(R.string.eos_please_confirm_alert))
//                        .setBackgroundColorRes(R.color.scarlet)
//                        .enableSwipeToDismiss()
//                        .enableInfiniteDuration(true)
//                        .setTextAppearance(R.style.myAlert)
//                        .show();
//            }
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//        SmartScheduler jobScheduler = SmartScheduler.getInstance(getActivity().getApplicationContext());
//        boolean reslut = jobScheduler.removeJob(2);
//        jobScheduler.removeJob(1);
//        if (reslut) {
//            LoggerManager.d("Job repeat Removed");
//        }
//
//        dissmisProgressDialog();
//
//    }
//
//    public String getAssetsValue(){
//        return totalCNYAmount.getText().toString().trim();
//    }
//
//    public String getEosAmount(){
//        return tvEosAmount.getText().toString().trim();
//    }
//
//    public static int px2dp(Context context, float pxValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
//    }
//
//    private void showChangeEOSNameDialog() {
//        int[] listenedItems = {R.id.imv_close};
//
//        CustomFullDialog dialog = new CustomFullDialog(getContext(),
//                R.layout.eos_dialog_change_account, listenedItems, false, Gravity.BOTTOM);
//
//        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.imv_close:
//                        dialog.cancel();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        dialog.show();
//
//        RecyclerView mRecyclerView = dialog.findViewById(R.id.rv_list);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(manager);
//        List<EOSNameVO> voList = getP().getEOSNameVOList();
//        ChangeAccountAdapter adapter = new ChangeAccountAdapter(voList);
//        mRecyclerView.setAdapter(adapter);
//
//        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                if (EmptyUtils.isNotEmpty(voList)) {
//                    for (int i = 0; i < voList.size(); i++) {
//                        EOSNameVO vo = voList.get(i);
//                        vo.isChecked = i == position;
//                    }
//
//                    adapter.notifyDataSetChanged();
//                    getP().saveNewEntity(voList.get(position).getEosName());
//                    tvAccountName.setText(voList.get(position).getEosName());
//                    //generatePortrait(voList.get(position).getEosName());
//
//                    EventBusProvider.postSticky(new ChangeAccountEvent());
//
//
//                    dialog.cancel();
//
//                }
//            }
//        });
//
//    }
//
//
//}
