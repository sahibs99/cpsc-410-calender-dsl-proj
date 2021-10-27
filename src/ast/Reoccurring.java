package ast;

public class Reoccurring extends Info {
    private String infoType = "reoccurring";
    private String reoccurring;

    public Reoccurring(String reoccurring){ this.reoccurring = reoccurring; }

    public <T> T accept(CalendarVisitor<T> v) {
        return v.visit(this);
    }

    public String getInfoType() {
        return infoType;
    }

    public String getReoccurring() {
        return reoccurring;
    }
}
