package resources;

public class Clock {
    long start;

    public Clock(){
        this.start = System.currentTimeMillis();
    }

    public int getDay(){
        long now = System.currentTimeMillis();
        long secondsPassed = (now - start) / 1000;
        int day = (int) (secondsPassed / 24);
        return day;
    }
}
