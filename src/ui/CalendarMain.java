package ui;

import ast.CalendarEvaluator;
import ast.CalendarEvent;
import ast.CalendarParser;
import ast.Program;
import libs.CalendarTokenizer;
import libs.Tokenizer;
import gui.CalendarGUI;

import java.util.Arrays;
import java.util.List;

public class CalendarMain {
    public static void main(String[] args) {
        List<String> fixedLiterals = Arrays.asList("Event:", "{", "}", "Name:", "Type:", "Place:", "Date:", "Start time:", "End time:", "Color:", "Overlap:", "Priority:", "Reoccurring:", "\\n");
        Tokenizer tokenizer = CalendarTokenizer.createCalendarTokenizer(System.getProperty("user.dir") + "/input.tvar",fixedLiterals);
        System.out.println("Done tokenizing");

        CalendarParser p = CalendarParser.getParser(tokenizer);
        Program program = p.parseProgram();
        System.out.println("Done parsing");

        CalendarEvaluator e = new CalendarEvaluator();

        program.accept(e);
        List<CalendarEvent> tempEvents = program.getFinalEvents();
        CalendarEvent[] events = tempEvents.toArray(new CalendarEvent[0]);
        //System.out.println(events);
        CalendarGUI calendar = new CalendarGUI(2020, events[0].getDate(), events);
        calendar.makeCalendar();
    }
}
