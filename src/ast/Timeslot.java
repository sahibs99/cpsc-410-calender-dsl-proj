package ast;

public class Timeslot {

    private double start;
    private double end;

    public Timeslot(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }
}
