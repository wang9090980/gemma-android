package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eos.ui.model.response.AccountInfo;


/**
 * 链上服务器---获得账户信息
 *
 * Created by wanglin on 2018/7/11.
 */
public class GetAccountinfoRequest extends GMAHttpRequest<AccountInfo> {

    public static final String TAG = "get_account";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetAccountinfoRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_GET_ACCOUNT_INFO);
    }


    public GetAccountinfoRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    public GetAccountinfoRequest getAccountInfo(JsonCallback<AccountInfo> callback) {
        postJsonNoRxRequest(TAG, callback);

        return this;
    }


}
