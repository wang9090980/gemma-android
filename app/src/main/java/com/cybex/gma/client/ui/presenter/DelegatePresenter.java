package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.activity.DelegateActivity;
import com.cybex.gma.client.ui.activity.EosAssetDetailActivity;
import com.cybex.gma.client.ui.activity.ResourceDetailActivity;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.exceptions.Exceptions;

public class DelegatePresenter extends XPresenter<DelegateActivity> {

    private static final int OPERATION_DELEGATE = 3;
    private static final int OPERATION_UNDELEGATE = 4;
    private static final String VALUE_CODE = "eosio";
    private static final String VALUE_CONTRACT = "eosio";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_ACTION_DELEGATE = "delegatebw";
    private static final String VALUE_ACTION_UNDELEGATE = "undelegatebw";

    /**
     * 执行Delegate逻辑
     *
     * @param from 付EOS的账号
     * @param to 收到资源的账号
     * @param stake_net_quantity
     * @param stake_cpu_quantity
     * @param privateKey
     */
    public void executeDelegateLogic(
            String from, String to, String stake_net_quantity, String stake_cpu_quantity,
            String privateKey) {


        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_delegatebw(VALUE_CODE, VALUE_ACTION_DELEGATE, from, to,
                stake_net_quantity, stake_cpu_quantity);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        if (getV() != null)
                        super.onStart(request);
                        getV().dismissDialog();
                        getV().showProgressDialog(getV().getResources().getString(R.string.operate_deal_ing));

                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {

                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_DELEGATE, from, privateKey, binargs);

                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        }
                        //getV().dissmisProgressDialog();

                    }
                });
    }

    public void executeUndelegateLogic(
            String from, String to, String unstake_net_quantity, String unstake_cpu_quantity,
            String privateKey) {
        if (getV() != null)getV().dismissDialog();
        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_undelegatebw(VALUE_CODE, VALUE_ACTION_UNDELEGATE, from, to,
                unstake_net_quantity, unstake_cpu_quantity);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        if (getV() != null){
                            super.onStart(request);
                            getV().dismissDialog();
                            getV().showProgressDialog(getV().getString(R.string.operate_deal_ing));
                        }
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_UNDELEGATE, from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        }
                        //getV().dissmisProgressDialog();

                    }
                });

    }

    /**
     * 获取配置信息成功后，再到C++库获取交易体
     *
     * @param from
     * @param privateKey
     * @param abiStr
     */
    public void getInfo(int operation_type, String from, String privateKey, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String infostr = response.body();
                            LoggerManager.d("config info:" + infostr);
                            //C++库获取Transaction交易体
                            String transactionStr = "";
                            switch (operation_type) {
                                case OPERATION_DELEGATE:
                                    transactionStr = JNIUtil.signTransaction_delegatebw(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0, 0, 120);
                                    break;
                                case OPERATION_UNDELEGATE:
                                    transactionStr = JNIUtil.signTransaction_undelegatebw(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0, 0, 120);
                                    break;
                                default:
                                    break;
                            }

                            LoggerManager.d("transactionJson:" + transactionStr);

                            TransferTransactionVO vo = GsonUtils.jsonToBean(transactionStr,
                                    TransferTransactionVO.class);
                            if (vo != null) {
                                //构造PushTransaction 请求的json参数
                                PushTransactionReqParams reqParams = new PushTransactionReqParams();
                                reqParams.setTransaction(vo);
                                reqParams.setSignatures(vo.getSignatures());
                                reqParams.setCompression(VALUE_COMPRESSION);

                                String buildTransactionJson = GsonUtils.
                                        objectToJson(reqParams);
                                LoggerManager.d("buildTransactionJson:" + buildTransactionJson);

                                //执行Push Transaction 最后一步操作
                                pushTransaction(buildTransactionJson);
                            }

                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            getV().dissmisProgressDialog();
                        }

                    }
                });
    }

    /**
     * 执行push transaction
     */
    public void pushTransaction(String jsonParams) {
        new PushTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .pushTransaction(new StringCallback() {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())){
                            getV().dissmisProgressDialog();
                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        getV().dissmisProgressDialog();
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String jsonStr = response.body();
                            LoggerManager.d("pushTransaction json:" + jsonStr);

                            //页面跳转至资产详情页
                            AppManager.getAppManager().finishActivity();
                            AppManager.getAppManager().finishActivity(ResourceDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(ParamConstants.EOS_TOKEN_TYPE, ParamConstants.SYMBOL_EOS);
                            UISkipMananger.launchAssetDetail(getV(), bundle);

                        }
                    }
                });

    }

    private void handleEosErrorCode(String err_code){
        String code = ParamConstants.EOS_ERR_CODE_PREFIX + err_code;
        if (EmptyUtils.isNotEmpty(getV()) && EmptyUtils.isNotEmpty(getV())){
            String package_name = getV().getPackageName();
            int resId = getV().getResources().getIdentifier(code, "string", package_name);
            String err_info =  getV().getResources().getString(resId);

            Alerter.create(getV())
                    .setText(err_info)
                    .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                    .showIcon(false)
                    .setDuration(3000)
                    .setBackgroundColorRes(R.color.scarlet)
                    .show();
        }
    }

    /**
     * 获取当前账号下可赎回EOS数量
     */
    public void getRefundQuantity(){
        try {
            MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
            EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);

            String account_name = eosEntity.getCurrentEosName();

            GetAccountInfoReqParams params = new GetAccountInfoReqParams();
            params.setAccount_name(account_name);

            String jsonParams = GsonUtils.objectToJson(params);
            new GetAccountinfoRequest(AccountInfo.class)
                    .setJsonParams(jsonParams)
                    .getAccountInfo(new JsonCallback<AccountInfo>() {
                        @Override
                        public void onStart(Request<AccountInfo, ? extends Request> request) {
                            if (getV() != null){
                                super.onStart(request);
                                getV().showProgressDialog(getV().getResources().getString(R.string.eos_loading_account_info));
                            }
                        }

                        @Override
                        public void onError(Response<AccountInfo> response) {
                            if (getV() != null){
                                getV().dissmisProgressDialog();
                                GemmaToastUtils.showLongToast(getV().getString(R.string
                                        .eos_load_account_info_fail));
                            }
                        }

                        @Override
                        public void onSuccess(Response<AccountInfo> response) {
                            if (getV() != null){
                                if (response != null && response.body() != null) {
                                    AccountInfo info = response.body();
                                    AccountInfo.SelfDelegatedBandwidthBean selfDelegatedBandwidth = info
                                            .getSelf_delegated_bandwidth();

                                    if (selfDelegatedBandwidth != null){
                                        String delegatedCpu = selfDelegatedBandwidth.getCpu_weightX().split(" ")[0];
                                        String delegatedNet = selfDelegatedBandwidth.getNet_weightX().split(" ")[0];
                                        getV().updateRefundaleQuantity(delegatedCpu, delegatedNet);
                                        getV().initTextChangedListener(delegatedCpu, delegatedNet);
                                    }else {
                                        getV().updateRefundaleQuantity("0.0000", "0.0000");
                                        getV().initTextChangedListener("0.0000", "0.0000");
                                    }
                                }
                                getV().dissmisProgressDialog();
                            }
                        }
                    });
        } catch (Throwable t) {
            throw Exceptions.propagate(t);
        }
    }

}
