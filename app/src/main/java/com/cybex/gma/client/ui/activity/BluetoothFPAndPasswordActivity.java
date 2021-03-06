//package com.cybex.gma.client.ui.activity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
//import com.cybex.componentservice.ui.activity.BluetoothBaseActivity;
//import com.cybex.gma.client.R;
//import com.cybex.gma.client.ui.fragment.BluetoothFPAndPasswordFragment;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
//import me.framework.fragmentation.anim.FragmentAnimator;
//
//public class BluetoothFPAndPasswordActivity extends BluetoothBaseActivity {
//
//    @Override
//    public void bindUI(View view) {
//        if (findFragment(BluetoothFPAndPasswordFragment.class) == null) {
//            loadRootFragment(R.id.fl_container_bluetooth_fp_and_password, BluetoothFPAndPasswordFragment.newInstance());
//        }
//
//        //让布局向上移来显示软键盘
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//    }
//
//    @Override
//    public void initData(Bundle savedInstanceState) {
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.eos_activity_bluetooth_fp_and_password;
//    }
//
//    @Override
//    public Object newP() {
//        return null;
//    }
//
//
//    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
//    }
//
//    @Override
//    public boolean useEventBus() {
//        return true;
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receiveDeviceConnectEvent(DeviceConnectStatusUpdateEvent event){
//        fixDeviceDisconnectEvent(event);
//    }
//
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void receiveConnectEvent(DeviceConnectStatusUpdateEvent event){
////        if(event.status==DeviceConnectStatusUpdateEvent.STATUS_BLUETOOTH_DISCONNCETED&&event.manual==false){
////            //意外断开
////            if(isResume){
////                //jump to home
////                int size = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList().size();
////                if(size>0){
////                    ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME).navigation();
////                }else{
////                    ARouter.getInstance().build(RouterConst.PATH_TO_INIT).navigation();
////                }
////            }
////        }else{
////        }
////    }
//}
