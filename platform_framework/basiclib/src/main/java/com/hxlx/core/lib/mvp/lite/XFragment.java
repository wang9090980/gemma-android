package com.hxlx.core.lib.mvp.lite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hxlx.core.lib.R;
import com.hxlx.core.lib.common.eventbus.BaseEvent;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.framework.fragmentation.FragmentSupport;

/**
 * Created by linwang on 2018/4/7.
 */
public abstract class XFragment<P extends BasePresenter> extends FragmentSupport
    implements
      BaseView<P> {

  private VDelegate vDelegate;
  private P p;
  protected Activity context;
  private View rootView;
  protected LayoutInflater layoutInflater;
  protected KProgressHUD kProgressHUD;

  public <T extends View> T $(View layoutView, @IdRes int resId) {
    return (T) layoutView.findViewById(resId);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    layoutInflater = inflater;
    if (rootView == null && getLayoutId() > 0) {
      rootView = inflater.inflate(getLayoutId(), null);
      bindUI(rootView);
    } else {
      ViewGroup viewGroup = (ViewGroup) rootView.getParent();
      if (viewGroup != null) {
        viewGroup.removeView(rootView);
      }
    }

    return rootView;
  }


  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (useEventBus()) {
      EventBusProvider.register(this);
    }
    bindEvent();
    initData(savedInstanceState);
  }

  @Override
  public abstract void bindUI(View rootView);

  protected VDelegate getvDelegate() {
    if (vDelegate == null) {
      vDelegate = VDelegateBase.create(context);
    }
    return vDelegate;
  }

  protected P getP() {
    if (p == null) {
      p = newP();
      if (p != null) {
        p.attachV(this);
      }
    }
    return p;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Activity) {
      this.context = (Activity) context;
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public boolean useEventBus() {
    return false;
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (useEventBus()) {
      EventBusProvider.unregister(this);
    }
    if (getP() != null) {
      getP().detachV();
    }
    getvDelegate().destory();

    p = null;
    vDelegate = null;
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventBusCome(BaseEvent event) {
    if (event != null) {
      receiveEvent(event);
    }
  }


  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onStickyEventBusCome(BaseEvent event) {
    if (event != null) {
      receiveStickyEvent(event);
    }
  }


  /**
   * 接收到分发到事件
   *
   * @param event 事件
   */
  protected void receiveEvent(BaseEvent event) {

  }

  /**
   * 接受到分发的粘性事件
   *
   * @param event 粘性事件
   */
  protected void receiveStickyEvent(BaseEvent event) {

  }



  @Override
  public int getOptionsMenuId() {
    return 0;
  }

  @Override
  public void bindEvent() {

  }

  /**
   * 以无参数的模式启动Activity。
   *
   * @param activityClass
   */
  public void readyGo(Class<? extends Activity> activityClass) {
    getActivity().startActivity(getLocalIntent(activityClass, null));
    getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
  }

  /**
   * 以绑定参数的模式启动Activity。
   *
   * @param activityClass
   */
  public void readyGo(Class<? extends Activity> activityClass, Bundle bd) {
    getActivity().startActivity(getLocalIntent(activityClass, bd));
    getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
  }

  /**
   * 获取当前程序中的本地目标
   *
   * @param localIntent
   * @return
   */
  public Intent getLocalIntent(Class<? extends Context> localIntent, Bundle bd) {
    Intent intent = new Intent(getActivity(), localIntent);
    if (null != bd) {
      intent.putExtras(bd);
    }

    return intent;
  }

  public void showProgressDialog(final String prompt) {
    if (getActivity() != null) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (kProgressHUD == null) {
            kProgressHUD = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
          }
          kProgressHUD.setLabel(prompt);
          kProgressHUD.show();
        }
      });
    }
  }

  public void dissmisProgressDialog() {
    if (kProgressHUD != null && kProgressHUD.isShowing()) {
      kProgressHUD.dismiss();
    }
  }
}