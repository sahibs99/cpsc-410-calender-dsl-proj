package ast;

public class Date extends Rule{
    private String month;
    private int day;

    public Date(String month, int day){
        this.month = convertMonth(month);
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public <T> T accept(CalendarVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String getInfoType() {
        return null;
    }

    private String convertMonth(String month){
        switch(month){
            case "Jan":
                return "January";
            case "Feb":
                return "February";
            case "Mar":
                return "March";
            case "Apr":
                return "April";
            case "Jun":
                return "June";
            case "Aug":
                return "August";
            case "Sept":
                return "September";
            case "Sep":
                return "September";
            case "Oct":
                return "October";
            case "Nov":
                return "November";
            case "Dec":
                return "December";
            default:
                return month;
        }
    }
}
