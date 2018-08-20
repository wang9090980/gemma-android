package com.cybex.gma.client.api;


import com.cybex.gma.client.BuildConfig;

/**
 * 接口请求地址全部配置在这里
 * <p>
 * Created by wanglin on 2018/7/05
 */

public class ApiPath {

    //测试环境
    public static final String HOST_TEST = "";
    //开发环境
    public static final String HOST_DEV = "";
    //线上环境
    public static final String HOST_ONLINE = "https://api.xxx.com";
    public final static String[] HOST = {HOST_TEST, HOST_DEV, HOST_ONLINE};
    public static String REST_URI_HOST = HOST[BuildConfig.API_PATH];


    /**
     * ------------EOS 项目情况配置---------------
     */

    //中心化服务器host
    public static final String HOST_CENTER_SERVER = "http://139.196.73.117:3001";
    //链上服务器host
    public static final String HOST_ON_CHAIN = "http://52.77.177.200:8888";


    //主链上的一个节点
    public static final String HOST_ON_CHAIN_MAIN = "http://52.77.177.200:8888";//"http://13.251.120.68:8888";

    //eosweb.net,一个外网节点，拥有当前eos链上所有数据
    public static final String HOST_EOS_WEB = "https://eosweb.net";

    //价格地址
    public static final String URL_UNIT_PRICE = "https://app.cybex.io/price";

    /**------------EOS 项目情况配置---------------*/
}
