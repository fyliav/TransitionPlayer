package android.window;

import android.os.IBinder;
import android.view.SurfaceControl;

public interface ITransitionPlayer {
    /**
     * Called when all participants of a transition are ready to animate. This is in response to
     * {@link IWindowOrganizerController#startTransition}.
     *
     * @param transitionToken An identifying token for the transition that is now ready to animate.
     * @param info A collection of all the changes encapsulated by this transition.
     * @param t A surface transaction containing the surface state prior to animating.
     * @param finishT A surface transaction that will reset parenting/layering and generally put
     *                surfaces into their final (post-transition) state. Apply this after playing
     *                the animation but before calling finish.
     */
    void onTransitionReady(IBinder transitionToken, TransitionInfo info,
                           SurfaceControl.Transaction t, SurfaceControl.Transaction finishT);

    /**
     * Called when something in WMCore requires a transition to play -- for example when an Activity
     * is started in a new Task.
     *
     * @param transitionToken An identifying token for the transition that needs to be started.
     *                        Pass this to {@link IWindowOrganizerController#startTransition}.
     * @param request Information about this particular request.
     */
    void requestStartTransition(IBinder transitionToken, TransitionRequestInfo request);
    abstract class Stub implements ITransitionPlayer {
    }
}
