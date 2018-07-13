package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.api.request.GMAHttpRequest;

/**
 * 中心化服务器---注册request
 *
 * Created by wanglin on 2018/7/12.
 */
public class UserRegisterRequest extends GMAHttpRequest<CustomData> {

    /**
     * @param clazz 想要请求返回的Bean
     */
    public UserRegisterRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_REGISTER);
    }

}