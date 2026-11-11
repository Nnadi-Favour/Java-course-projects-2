public class DVD {

    private int duration;

    public DVD(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return duration + " minutes";
    }
}