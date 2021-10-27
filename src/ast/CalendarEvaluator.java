package ast;

import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class CalendarEvaluator implements CalendarVisitor<Integer> {

    public CalendarEvaluator() {

    }
    private static final Map<String, Integer> daysInMonth = createMonths();
    private static Map<String, Integer> createMonths() {
        Map<String, Integer> months = new HashMap<>();
        months.put("January", 31);
        months.put("February", 29);
        months.put("March", 31);
        months.put("April", 30);
        months.put("May", 31);
        months.put("June", 30);
        months.put("July", 31);
        months.put("August", 31);
        months.put("September", 30);
        months.put("October", 31);
        months.put("November", 30);
        months.put("December", 31);
        return Collections.unmodifiableMap(months);
    }
    private String month = "NOT_SET";
    private Map<Integer, List<Timeslot>> timeslots = new HashMap<>();
    public final Map<String, Map> types = new HashMap<>();
    public final List<CalendarEvent> eventsList = new ArrayList<CalendarEvent>();

    @Override
    public Integer visit(Program p) {
        for (Statement s : p.getStatements()) {
            s.accept(this);
        }
        p.setFinalEvents(eventsList);
        return null;
    }

    @Override
    public Integer visit(Color c) {
        return null;
    }

    @Override
    public Integer visit(Event e) {
        String name = e.getName();
        Date date = e.getDate();
        String start_time = e.getStart_time();
        String end_time = e.getEnd_time();
        String type = e.getType();
        if (type.equals("")) {
            type = name;
        }
        if (month.equals("NOT_SET")) {
            month = date.getMonth();
        }
        if (date.getMonth().equals(month)) {
            CalendarEvent event = new CalendarEvent(name, type, date, start_time, end_time);
            if (types.containsKey(type)) {
                Map<String, String> savedInfo = types.get(type);
                event = setCalendarValues(event, savedInfo);
            }
            List<Info> infoList = e.getAdditionalinfo();
            Map<String, String> additionalInfo = new HashMap<>();
            for (Info i : infoList) {
                String infoValue = evaluateInfo(i);
                additionalInfo.put(i.getInfoType(), infoValue);
            }
            event = setCalendarValues(event, additionalInfo);
            if (!event.isOverlap()){
                if (checkOverlapAndAdd(e, name, date)) return null;
            }
            eventsList.add(event);
            int recurrenceRate = event.getReoccurring();
            if (recurrenceRate > 0) {
                int dayLimit = daysInMonth.get(month);
                int currentDay = date.getDay() + 1;
                int counter = 1;
                while (!(currentDay > dayLimit)) {
                    if ((counter % recurrenceRate) == 0) {
                        Date newDate = new Date(month, currentDay);
                        CalendarEvent newEvent = new CalendarEvent(name, type, newDate, start_time, end_time);
                        newEvent.setPriority(event.isPriority());
                        newEvent.setColor(event.getColor());
                        newEvent.setOverlap(event.isOverlap());
                        newEvent.setPlace(event.getPlace());
                        newEvent.setReoccurring(1);
                        if (!newEvent.isOverlap()) {
                            if (checkOverlapAndAdd(e, name, newDate)) {
                                return null;
                            } else {
                                eventsList.add(newEvent);
                            }
                        } else {
                            eventsList.add(newEvent);
                        }
                    }
                    currentDay++;
                    counter++;
                }
            }
        }
        return null;
    }

    private boolean checkOverlapAndAdd(Event e, String name, Date date) {
        String[] startStrings = e.getStart_time().split(":");
        String[] endStrings = e.getEnd_time().split(":");
        double start = parseDouble(startStrings[0]) + (parseDouble(startStrings[1])) / 60.0;
        double end = parseDouble(endStrings[0]) + (parseDouble(endStrings[1])) / 60.0;
        Timeslot time = new Timeslot(start, end);
        Integer day = date.getDay();
        if (!timeslots.containsKey(day)) {
            List<Timeslot> times = new ArrayList<>();
            times.add(time);
            timeslots.put(day, times);
        } else {
            List<Timeslot> timeList = timeslots.get(day);
            for (Timeslot t : timeList) {
                if ((t.getStart() < end) && (t.getEnd() > start)) {
                    System.out.println(name + " has a conflict on " + date.getMonth() + " " + date.getDay());
                    return true;
                }
            }
            timeslots.get(day).add(time);
        }
        return false;
    }

    @Override
    public Integer visit(Info i) {
        return null;
    }

    @Override
    public Integer visit(Name n) {
        return null;
    }

    @Override
    public Integer visit(Overlap o) {
        return null;
    }

    @Override
    public Integer visit(Place p) {
        return null;
    }

    @Override
    public Integer visit(Priority p) {
        return null;
    }

    @Override
    public Integer visit(Reoccurring r) {
        return null;
    }

    @Override
    public Integer visit(Time t) {
        return null;
    }

    private CalendarEvent setCalendarValues(CalendarEvent event, Map<String, String> savedInfo) {
        for (String s : savedInfo.keySet()) {
            boolean stringToBool = false;
            switch (s) {
                case "place":
                    event.setPlace(savedInfo.get(s));
                    break;
                case "overlap":
                    if (savedInfo.get(s).equals("Yes")){
                        stringToBool = true;
                    }
                    event.setOverlap(stringToBool);
                    break;
                case "color":
                    event.setColor(savedInfo.get(s));
                    break;
                case "priority":
                    if (savedInfo.get(s).equals("Important")){
                        stringToBool = true;
                    }
                    event.setPriority(stringToBool);
                    break;
                case "reoccurring":
                    event.setReoccurring(parseInt(savedInfo.get(s)));
                    break;
            }
        }
        return event;
    }

    public Integer visit(UserDef u) {
        String name = u.getName();
        List<Info> infoList = u.getInfo();
        Map<String, String> savedInfo = new HashMap<>();
        for (Info i : infoList) {
            String infoValue = evaluateInfo(i);
            savedInfo.put(i.getInfoType(), infoValue);
        }
        types.put(name, savedInfo);
        return null;
    }

    @Override
    public Integer visit(Date d) {
        return null;
    }

    private String evaluateInfo(Info i) {
        String infoType = i.getInfoType();
        String value = "";
        switch (i.getInfoType()) {
            case "place":
                value = ((Place) i).getPlace();
                break;
            case "overlap":
                value = ((Overlap) i).getOverlap();
                break;
            case "color":
                value = ((Color) i).getColor();
                break;
            case "priority":
                value = ((Priority) i).getPriority();
                break;
            case "reoccurring":
                value = ((Reoccurring) i).getReoccurring();
                break;
        }
        return value;
    }
}
