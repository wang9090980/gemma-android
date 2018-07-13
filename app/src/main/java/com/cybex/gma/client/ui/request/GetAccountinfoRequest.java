package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.AccountInfo;

/**
 * 链上服务器---获得账户信息
 *
 * Created by wanglin on 2018/7/11.
 */
public class GetAccountinfoRequest extends GMAHttpRequest<AccountInfo> {

    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetAccountinfoRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_GET_ACCOUNT_INFO);

    }


}