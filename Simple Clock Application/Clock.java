import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clock class responsible for storing and updating
 * the current date and time.
 */
public class Clock {

    // Shared time value used by both threads
    private volatile LocalDateTime currentTime;

    // Date and time format required by the assignment
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

    /**
     * Constructor initializes the clock.
     */
    public Clock() {
        currentTime = LocalDateTime.now();
    }

    /**
     * Continuously updates the current time.
     */
    public void updateTime() {
        while (!Thread.currentThread().isInterrupted()) {
            currentTime = LocalDateTime.now();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Update thread interrupted.");
            }
        }
    }

    /**
     * Continuously displays the current time.
     */
    public void displayTime() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Current Time: "
                    + currentTime.format(formatter));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Display thread interrupted.");
            }
        }
    }
}