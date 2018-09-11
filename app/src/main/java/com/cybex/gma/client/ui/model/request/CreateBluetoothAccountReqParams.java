package com.cybex.gma.client.ui.model.request;

/**
 * 创建蓝牙账户，用户注册接口
 *
 * Created by wanglin on 2018/9/11.
 */
public class CreateBluetoothAccountReqParams {

    /**
     * 访问方ID，1:Gemma 2:Wookong solo 3: Wookong
     */
    private int app_id;
    /**
     * 用户名
     */
    private String account_name;
    /**
     * 邀请码
     */
    private String invitation_code;

    /**
     * 公钥
     */
    private String public_key;

    /**
     * 公钥hex签名
     */
    private String public_key_sig;

    /**
     * 和wookong的验签
     */
    private String validation;

    /**
     * wookong的SN
     */
    private String SN;

    /**
     * SN签名
     */
    private String SN_sig;


    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getInvitation_code() {
        return invitation_code;
    }

    public void setInvitation_code(String invitation_code) {
        this.invitation_code = invitation_code;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getPublic_key_sig() {
        return public_key_sig;
    }

    public void setPublic_key_sig(String public_key_sig) {
        this.public_key_sig = public_key_sig;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getSN_sig() {
        return SN_sig;
    }

    public void setSN_sig(String SN_sig) {
        this.SN_sig = SN_sig;
    }
}
