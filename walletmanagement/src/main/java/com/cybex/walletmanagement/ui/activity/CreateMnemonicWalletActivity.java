package com.cybex.walletmanagement.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.SoftHideKeyBoardUtil;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.presenter.CreateMnemonicWalletPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.KeyboardUtils;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;


import java.util.List;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


@Route(path = RouterConst.PATH_TO_CREATE_MNEMONIC_PAGE)
public class CreateMnemonicWalletActivity extends XActivity<CreateMnemonicWalletPresenter>  {

    ImageView ivSetPassMask;
    ImageView ivRepeatPassMask;
    View viewDividerWalletName;
    View viewDividerSetPass;
    View viewDividerRepeatPass;
    View viewDividerPassHint;

    ScrollView scrollViewCreateWallet;
    TextView tvWalletName;
    ViewGroup llWalletname;
    ImageView ivWalletNameClear;
    ImageView ivSetPassClear;
    ImageView ivRepeatPassClear;
    ImageView ivPassHintClear;
    EditText edtWalletName;
    TextView tvSetPass;
    EditText edtSetPass;
    TextView tvRepeatPass;
    EditText edtRepeatPass;
    TextView tvPassHint;
    EditText edtPassHint;
    CheckBox checkboxConfig;
    TextView tvServiceAgreementConfig;
    LinearLayout layoutCheckBox;
    Button btCreateWallet;



    private boolean isMask;//true为密文显示密码
    private boolean isInitial;

    @Override
    public void bindUI(View view) {

        scrollViewCreateWallet = (ScrollView) findViewById(R.id.scroll_create_wallet);
        tvWalletName = (TextView) findViewById(R.id.tv_wallet_name);
        edtWalletName = (EditText) findViewById(R.id.edt_wallet_name);
        llWalletname = (ViewGroup) findViewById(R.id.ll_walletname);
        ivWalletNameClear = (ImageView) findViewById(R.id.iv_wallet_name_clear);
        viewDividerWalletName = findViewById(R.id.view_divider_walletName);
        tvSetPass = (TextView) findViewById(R.id.tv_set_pass);
        edtSetPass = (EditText) findViewById(R.id.edt_set_pass);
        ivSetPassClear = (ImageView) findViewById(R.id.iv_set_pass_clear);
        ivSetPassMask = (ImageView) findViewById(R.id.iv_set_pass_mask);
        viewDividerSetPass = findViewById(R.id.view_divider_setPass);
        tvRepeatPass = (TextView) findViewById(R.id.tv_repeat_pass);
        edtRepeatPass = (EditText) findViewById(R.id.et_repeat_pass);
        ivRepeatPassClear = (ImageView) findViewById(R.id.iv_repeat_pass_clear);
        ivRepeatPassMask = (ImageView) findViewById(R.id.iv_repeat_pass_mask);
        viewDividerRepeatPass = findViewById(R.id.view_divider_repeatPass);
        tvPassHint = (TextView) findViewById(R.id.tv_pass_hint);
        edtPassHint = (EditText) findViewById(R.id.edt_pass_hint);
        viewDividerPassHint = findViewById(R.id.view_divider_pass_hint);
        ivPassHintClear = (ImageView) findViewById(R.id.iv_pass_hint_clear);
        layoutCheckBox = (LinearLayout) findViewById(R.id.layout_checkBox);
        checkboxConfig = (CheckBox) findViewById(R.id.checkbox_config);
        tvServiceAgreementConfig = (TextView) findViewById(R.id.tv_service_agreement_config);
        btCreateWallet = (Button) findViewById(R.id.bt_create_wallet);
        setNavibarTitle(getResources().getString(R.string.walletmanage_tip_create_wallet), true);



        edtWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }

                if (EmptyUtils.isEmpty(getWalletName())) {
                    if (edtWalletName.hasFocus()) { setWalletNameFocusedStyle(); }
                } else  {
                    if (getP().isWalletNameValid()) {
                        if(edtWalletName.hasFocus()){
                            setWalletNameFocusedStyle();
                        }
                    } else {
                        setUnclickable(btCreateWallet);
                        setWalletNameInvalidStyle();
                    }
                }

                if (EmptyUtils.isNotEmpty(getWalletName())) {
                    ivWalletNameClear.setVisibility(View.VISIBLE);
                } else {
                    ivWalletNameClear.setVisibility(View.GONE);
                }
            }
        });

        edtSetPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }

                if (EmptyUtils.isNotEmpty(getPassword())) {
                    ivSetPassClear.setVisibility(View.VISIBLE);
                } else {
                    ivSetPassClear.setVisibility(View.GONE);
                }
            }
        });

        edtRepeatPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isEmpty(getRepeatPassword())) {
                    ivRepeatPassClear.setVisibility(View.GONE);
                    if (edtRepeatPass.hasFocus()) { setRepeatPassFocusStyle(); }
                } else {
                    ivRepeatPassClear.setVisibility(View.VISIBLE);
                }

                if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });

        edtPassHint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isNotEmpty(getPassHint())) {
                    ivPassHintClear.setVisibility(View.VISIBLE);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                }
            }
        });
    }



    @Override
    public void initData(Bundle savedInstanceState) {

        SoftHideKeyBoardUtil.assistActivity(this);
        isMask = true;
        initView();

        List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
        if(multiWalletEntityList.size()>0){
            int currentIndex = SPUtils.getInstance().getInt(BaseConst.INITIAL_WALLET_INDEX_KEY, 1);
            edtWalletName.setText(BaseConst.INITIAL_WALLET_NAME_PREFIX+currentIndex);
            edtWalletName.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            isInitial = true;
            edtWalletName.setText(BaseConst.INITIAL_WALLET_NAME);
            llWalletname.setVisibility(View.GONE);
            tvWalletName.setVisibility(View.GONE);
            viewDividerWalletName.setVisibility(View.GONE);
        }

        btCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    getP().creatWallet(getWalletName(),getPassword(),getPassHint());
                }
            }
        });
        tvServiceAgreementConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int savedLanguageType = LanguageManager.getInstance(context).getLanguageType();
                switch (savedLanguageType) {
                    case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                        CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                                .string.terms_of_service));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_EN:
                        CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_EN, getResources().getString(R
                                .string.terms_of_service));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                        Locale systemLanguageType = LanguageManager.getInstance(context).getSysLocale();
                        switch (systemLanguageType.getDisplayLanguage()) {
                            case BaseConst.CN:
                                CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                        .getString(R.string.terms_of_service));
                                break;
                            case BaseConst.EN:
                                CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_EN, getResources()
                                        .getString(R.string.terms_of_service));
                                break;
                            default:
                                CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                        .getString(R.string.terms_of_service));
                        }
                        break;
                    default:
                        CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                                .string.terms_of_service));
                }
            }
        });

        ivSetPassMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMask){
                    //如果当前为密文
                    isMask = false;
                    edtSetPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivSetPassMask.setImageResource(R.drawable.ic_invisible);
                    edtSetPass.setSelection(getPassword().length());
                }else {
                    isMask = true;
                    edtSetPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivSetPassMask.setImageResource(R.drawable.ic_visible);
                    edtSetPass.setSelection(getPassword().length());
                }
            }
        });

        ivRepeatPassMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMask){
                    //如果当前为密文
                    isMask = false;
                    edtRepeatPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivRepeatPassMask.setImageResource(R.drawable.ic_invisible);
                    edtRepeatPass.setSelection(getRepeatPassword().length());
                }else {
                    //如果当前为明文
                    isMask = true;
                    edtRepeatPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivRepeatPassMask.setImageResource(R.drawable.ic_visible);
                    edtRepeatPass.setSelection(getRepeatPassword().length());
                }
            }
        });
        ivWalletNameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtWalletName.setText("");
            }
        });

        ivSetPassClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSetPass.setText("");
            }
        });

        ivRepeatPassClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRepeatPass.setText("");
            }
        });

        ivPassHintClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPassHint.setText("");
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_create_mnemonic_wallet;
    }

    @Override
    public CreateMnemonicWalletPresenter newP() {
        return new CreateMnemonicWalletPresenter();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();


    }

    public void initView() {
        //动态设置hint样式
        setEditTextHintStyle(edtWalletName, R.string.walletmanage_tip_walletname_hint);
        setEditTextHintStyle(edtSetPass, R.string.walletmanage_tip_input_password);
        setEditTextHintStyle(edtRepeatPass, R.string.walletmanage_tip_repeat_password);
        setEditTextHintStyle(edtPassHint, R.string.walletmanage_tip_input_password_hint);

        setUnclickable(btCreateWallet);
//        edtSetPass.setOnTouchListener(new View.OnTouchListener() {
//            int flag = 0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                flag++;
//                if (flag % 2 == 0) {
////                    showBubble();
////                    scheduleDismiss();
//                }
//                return false;
//            }
//        });
//        edtRepeatPass.setOnTouchListener(new View.OnTouchListener() {
//            int flag = 0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                flag++;
//                if (flag % 2 == 0) {
//                    //hideBubble();
//                }
//                return false;
//            }
//        });
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isAllTextFilled() && getP().isWalletNameValid() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });
//        setNavibarTitle(getResources().getString(R.string.walletmanage_tip_create_wallet), true);
        /**
         * walletname输入区域样式设置
         */
        edtWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isEmpty(getWalletName())) {
                    setWalletNameValidStyle();
                    if (hasFocus) { setWalletNameFocusedStyle(); }
                } else {
                    if (getP().isWalletNameValid()) {
                        setWalletNameValidStyle();
                        if (hasFocus) { setWalletNameFocusedStyle(); }
                    } else {
                        setUnclickable(btCreateWallet);
                        setWalletNameInvalidStyle();
                    }
                }

                if (hasFocus) {
                    setDividerFocusStyle(viewDividerWalletName);
                    edtWalletName.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getWalletName())){
                        ivWalletNameClear.setVisibility(View.VISIBLE);
                    }

                    edtWalletName.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtWalletName);
                } else {
                    setDividerDefaultStyle(viewDividerWalletName);
                    ivWalletNameClear.setVisibility(View.GONE);
                    if (EmptyUtils.isEmpty(getWalletName())) {
                        edtWalletName.setTypeface(Typeface.DEFAULT);
                    }
                }

            }
        });

        /**
         * 设置密码输入区域样式设置
         */
        edtSetPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDividerFocusStyle(viewDividerSetPass);
                    tvSetPass.setTextColor(getResources().getColor(R.color.black_content));
                    edtSetPass.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getPassword())){
                        ivSetPassClear.setVisibility(View.VISIBLE);
                    }
                    edtSetPass.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtSetPass);
                } else {
                    setDividerDefaultStyle(viewDividerSetPass);
                    ivSetPassClear.setVisibility(View.GONE);
                    tvSetPass.setTextColor(getResources().getColor(R.color.black_context));
                    if (EmptyUtils.isEmpty(getPassword())) { edtSetPass.setTypeface(Typeface.DEFAULT); }
                }
            }
        });
        /**
         * 重复密码输入区域样式设置
         */
        edtRepeatPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isEmpty(getRepeatPassword())) {
                    setRepeatPassValidStyle();
                    if (hasFocus) { setRepeatPassFocusStyle(); }
                } else {
                    if (getPassword().equals(getRepeatPassword())) {
                        //两次输入的密码一致
                        setRepeatPassValidStyle();
                        if (hasFocus) { setRepeatPassFocusStyle(); }
                    } else {
                        //两次输入的密码不一致
                        setRepeatPassInvalidStyle();
                    }
                }

                if (hasFocus) {
                    edtRepeatPass.setTypeface(Typeface.DEFAULT_BOLD);
                    edtRepeatPass.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtRepeatPass);
                } else {
                    ivRepeatPassClear.setVisibility(View.GONE);
                    if (EmptyUtils.isEmpty(getRepeatPassword())) { edtRepeatPass.setTypeface(Typeface.DEFAULT); }
                }
            }
        });
        /**
         * 密码提示输入区域样式设置
         */
        edtPassHint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isNotEmpty(getPassHint())) { ivPassHintClear.setVisibility(View.VISIBLE); }
                    tvPassHint.setTextColor(getResources().getColor(R.color.black_content));
                    edtPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                    setDividerFocusStyle(viewDividerPassHint);
                    edtPassHint.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtPassHint);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                    tvPassHint.setTextColor(getResources().getColor(R.color.black_context));
                    setDividerDefaultStyle(viewDividerPassHint);
                    if (EmptyUtils.isEmpty(getPassHint())) { edtPassHint.setTypeface(Typeface.DEFAULT); }
                }
            }
        });


        OverScrollDecoratorHelper.setUpOverScroll(scrollViewCreateWallet);
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    public void setWalletNameValidStyle() {
        tvWalletName.setTextColor(getResources().getColor(R.color.black_context));
        tvWalletName.setText(getResources().getString(R.string.walletmanage_title_wallet_name));
        setDividerDefaultStyle(viewDividerWalletName);
    }

    public void setWalletNameInvalidStyle() {

        tvWalletName.setText(getResources().getString(R.string.walletmanage_tip_walletname_error));
        tvWalletName.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewDividerWalletName);
    }

    public void setWalletNameFocusedStyle() {
        tvWalletName.setTextColor(getResources().getColor(R.color.black_title));
        tvWalletName.setText(getResources().getString(R.string.walletmanage_title_wallet_name));
        setDividerFocusStyle(viewDividerWalletName);
    }

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText(getResources().getString(R.string.walletmanage_tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_context));
        setDividerDefaultStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.walletmanage_tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_title));
        setDividerFocusStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassInvalidStyle() {
        //两次输入密码不匹配
        tvRepeatPass.setText(getResources().getString(R.string.walletmanage_tip_pass_no_match));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

    public void setClickable(Button button) {
//        button.setBackgroundColor(getResources().getColor(R.color.btn_clickable));
        button.setEnabled(true);
    }

    public void setUnclickable(Button button) {
//        button.setBackgroundColor(getResources().getColor(R.color.btn_unclickable));
        button.setEnabled(false);
    }

    public String getWalletName() {
        return edtWalletName.getText().toString().trim();
    }

    public String getPassword() {
        return edtSetPass.getText().toString();
    }

    public String getRepeatPassword() {
        return edtRepeatPass.getText().toString();
    }

    public String getPassHint() {
        return edtPassHint.getText().toString();
    }


    @Override
    protected void onDestroy() {
        clearListeners();
        super.onDestroy();
    }

    /**
     * 代替监听器检查是否所有edittext输入框都不为空值
     *
     * @return
     */
    public boolean isAllTextFilled() {
        return !EmptyUtils.isEmpty(getPassword())
                && !EmptyUtils.isEmpty(getRepeatPassword())
                && !EmptyUtils.isEmpty(getWalletName());
    }

    public void setEditTextHintStyle(EditText editText, int resId) {
        String hintStr = getResources().getString(resId);
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
//        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
    }

    public void clearListeners() {
        edtWalletName.setOnFocusChangeListener(null);
        edtRepeatPass.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtPassHint.setOnFocusChangeListener(null);
        checkboxConfig.setOnCheckedChangeListener(null);
    }

    public void setDividerFocusStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                3);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.common_page_content_padding), (int) getResources()
                .getDimension(R.dimen.common_page_content_padding));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.black_title));

    }

    public void setDividerDefaultStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.common_page_content_padding), (int) getResources()
                .getDimension(R.dimen.common_page_content_padding));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.dddddd_grey_350));

    }

    public void setDividerAlertStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.common_page_content_padding), (int) getResources()
                .getDimension(R.dimen.common_page_content_padding));
        divider.setBackgroundColor(getResources().getColor(R.color.scarlet));
    }

    public void setHorizontalMargins(LinearLayout.LayoutParams params, int marginStart, int marginEnd) {
        params.setMarginStart(marginStart);
        params.setMarginEnd(marginEnd);
    }

    private boolean checkValidation() {
        String walletName = getWalletName();
        if(TextUtils.isEmpty(walletName)){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_name_not_empty));
            return false;
        }

        String password = getPassword();
        if(TextUtils.isEmpty(password)){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_pass_not_empty));
            return false;
        }
        if(!getP().isPasswordValid()){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_pass_lenth_invalid));
            return false;
        }

        String repeatPassword = getRepeatPassword();
        if(TextUtils.isEmpty(repeatPassword)){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_repeat_input_pass));
            return false;
        }
        if(!getP().isPasswordMatch()){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_password_no_match));
            return false;
        }

        if (!checkboxConfig.isChecked()) {
            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_check_agreement));
            return false;
        }

        if (!getP().isWalletNameValid()) {
//            GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_invalid_walletname));
            AlertUtil.showShortUrgeAlert(this, getString(R.string.walletmanage_name_invalid));
            return false;
        }

        return true;
    }

    public boolean isInitial() {
        return isInitial;
    }

}
