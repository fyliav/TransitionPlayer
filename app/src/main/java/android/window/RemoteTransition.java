package android.window;

import android.app.IApplicationThread;

public class RemoteTransition {
    /**
     * The application thread that will be running the remote transition.
     * @hide
     */
    public IApplicationThread getAppThread() {
        throw new AssertionError("Stub!");
    }
}
