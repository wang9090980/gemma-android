package com.cybex.componentservice.db.entity;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = GemmaDatabase.class, name = "t_multi_wallet")
public class MutliWalletEntity extends BaseModel {
    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    public Integer id;
    /**
     * 钱包名称
     */
    @Column
    public String walletName;

    /**
     * 助记词（当钱包类型为1的时候，保存password加密过后的助记词）
     */
    @Column
    public String mnemonic;

    /**
     * 摘要信息（保存sha256之后password，用于校验用户输入密码的正确性）
     */
    @Column
    public String cypher;

    /**
     * 是否当前钱包  （1---是  0---否）
     */
    @Column
    public Integer isCurrentWallet;

    /**
     * 密码提示
     */
    @Column
    public String passwordTip;

    /**
     * 是否已经备份
     */
    @Column
    public Integer isBackUp;

    /**
     * 钱包类型 (0--助记词软件钱包   1--硬件钱包  2-导入单个私钥类型的软钱包)
     */
    @Column(defaultValue = "0")
    public int walletType;

    /**
     * 是否设置蓝牙指纹 0--无   1--有
     */
    @Column(defaultValue = "0")
    public int isSetBluetoothFP;

    /**
     * 蓝牙卡硬件地址
     */
    @Column
    public String bluetoothDeviceName;


    @ForeignKey
    public EosWalletEntity eosWalletEntity;

    @ForeignKey
    public EthWalletEntity ethWalletEntity;


}
