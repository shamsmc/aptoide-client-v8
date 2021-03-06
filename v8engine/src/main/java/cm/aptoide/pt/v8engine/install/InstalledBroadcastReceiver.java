/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by neuro on 24-05-2016.
 */
public class InstalledBroadcastReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    Intent callService = new Intent(intent);
    callService.setClassName(context, InstalledIntentService.class.getName());
    context.startService(callService);

    // from the documentation @ https://developer.android.com/reference/android/content/BroadcastReceiver.html#ProcessLifecycle
    /*
    This means that for longer-running operations you will often use a Service in conjunction with a
    BroadcastReceiver to keep the containing process active for the entire time of your operation.
    */
  }
}
