/**
 * Unit 3 - Simple Clock Application
 * Demonstrates Java threads and thread priorities.
 */
public class Unit3 {

    public static void main(String[] args) {

        System.out.println("===== SIMPLE CLOCK APPLICATION =====");
        System.out.println();

        // Create the shared Clock object
        Clock clock = new Clock();

        // Create the threads
        TimeUpdater updaterThread = new TimeUpdater(clock);
        TimeDisplay displayThread = new TimeDisplay(clock);

        // Set thread priorities
        updaterThread.setPriority(Thread.NORM_PRIORITY);
        displayThread.setPriority(Thread.MAX_PRIORITY);

        // Display thread priorities
        System.out.println("Updater Thread Priority : "
                + updaterThread.getPriority());
        System.out.println("Display Thread Priority : "
                + displayThread.getPriority());

        System.out.println();
        System.out.println("Clock started...");
        System.out.println();

        // Start both threads
        updaterThread.start();
        displayThread.start();
    }
}
