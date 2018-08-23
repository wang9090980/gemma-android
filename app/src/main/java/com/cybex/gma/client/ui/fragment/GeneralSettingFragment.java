package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.event.OnChangeLanguageEvent;
import com.cybex.gma.client.manager.LanguageManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 通用设置界面
 */
public class GeneralSettingFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_change_language) SuperTextView superTextViewChangeLanguage;
    @BindView(R.id.superTextView_change_unit) SuperTextView superTextViewChangeUnit;

    public static GeneralSettingFragment newInstance() {
        Bundle args = new Bundle();
        GeneralSettingFragment fragment = new GeneralSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.general), true, true);

        superTextViewChangeLanguage.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(LanguageSettingFragment.newInstance());
            }
        });

        superTextViewChangeUnit.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(CurrencyUnitFragment.newInstance());
            }
        });

        this.showLanguage();
    }


    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
    }

    private void showLanguage() {
        int savedLanguageType = LanguageManager.getInstance().getLanguageType();
        switch (savedLanguageType) {
            case R.id.radioButton_follow_system:
                superTextViewChangeLanguage.setRightString(getString(R.string.follow_system));
                break;
            case R.id.radioButton_simC:
                superTextViewChangeLanguage.setRightString(getString(R.string.simplified_C));
                break;
            case R.id.radioButton_EN:
                superTextViewChangeLanguage.setRightString(getString(R.string.english));
                break;
            default:
                break;
        }
    }


    /**
     * 更改语言事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeLanguageEvent(OnChangeLanguageEvent event) {
        if (event != null) {
            showLanguage();
        }

    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_general_setting;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
