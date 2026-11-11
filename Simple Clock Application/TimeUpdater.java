/**
 * Background thread responsible for continuously
 * updating the current time.
 */
public class TimeUpdater extends Thread {

    private final Clock clock;

    /**
     * Constructor.
     *
     * @param clock Shared Clock object
     */
    public TimeUpdater(Clock clock) {
        this.clock = clock;
    }

    /**
     * Starts updating the clock.
     */
    @Override
    public void run() {
        clock.updateTime();
    }
}