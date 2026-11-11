/**
 * Thread responsible for displaying
 * the current time and date.
 */
public class TimeDisplay extends Thread {

    private final Clock clock;

    /**
     * Constructor.
     *
     * @param clock Shared Clock object
     */
    public TimeDisplay(Clock clock) {
        this.clock = clock;
    }

    /**
     * Starts displaying the time.
     */
    @Override
    public void run() {
        clock.displayTime();
    }
}