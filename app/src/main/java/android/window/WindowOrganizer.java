package android.window;

public class WindowOrganizer {
    /**
     * Register an ITransitionPlayer to handle transition animations.
     * @hide
     */
//    @RequiresPermission(android.Manifest.permission.MANAGE_ACTIVITY_TASKS)
    public void registerTransitionPlayer(ITransitionPlayer player) {
        throw new AssertionError("Stub!");
    }

    /**
     * Unregister a previously-registered ITransitionPlayer.
     * @hide
     */
//    @RequiresPermission(android.Manifest.permission.MANAGE_ACTIVITY_TASKS)
    public void unregisterTransitionPlayer(ITransitionPlayer player) {
        throw new AssertionError("Stub!");
    }
}
