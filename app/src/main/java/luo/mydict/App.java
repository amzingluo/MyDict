package luo.mydict;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

public class App extends Application {

    public static boolean isHardPage;
    public static Context Application;

    ClipboardManager mClipboardManager;

    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    ActivityManager am ;

    public static String word;

    @Override
    public void onCreate() {
        super.onCreate();
        Application=this;
        am= (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        registerClipEvents();
    }

    private void registerClipEvents() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.d("luojianjin","收到复制通知");
                String activityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
                if(activityName.equals("luo.mydict.WebActivity")){
                        if (mClipboardManager.hasPrimaryClip()
                                && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
                            // 获取复制、剪切的文本内容
                            CharSequence content =
                                    mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                            if(!content.equals(App.word)){
                                Util.openWeb(content.toString());
                                Log.d("luojianjin","复制且打开了："+content);
                            }
                        }
                }

            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }


}