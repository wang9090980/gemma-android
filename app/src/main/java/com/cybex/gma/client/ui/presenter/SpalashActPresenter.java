package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.dao.WalletEntityDao;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.SpalashActivity;
import com.cybex.gma.client.utils.SPUtils;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;

import java.util.List;

/**
 * Created by wanglin on 2018/8/5.
 */
public class SpalashActPresenter extends XPresenter<SpalashActivity> {

    public void goToNext() {

        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        List<WalletEntity> entityList = dao.getWalletEntityList();
        if (EmptyUtils.isNotEmpty(entityList)) {
            boolean isOpenGesture = SPUtils.getInstance().getBoolean(CacheConstants.KEY_OPEN_GESTURE);
            if (isOpenGesture) {
                Bundle bd = new Bundle();
                bd.putInt(ParamConstants.GESTURE_SKIP_TYPE, ParamConstants.GESTURE_SKIP_TYPE_LOGIN_VERIFY);
                UISkipMananger.launchVerifyGestureActivity(getV(), bd);
            }

            //跳转到主界面
            UISkipMananger.launchHome(getV());
        } else {
            UISkipMananger.launchLogin(getV());
        }

        getV().finish();
        getV().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }
}
