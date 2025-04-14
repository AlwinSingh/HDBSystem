package src.model;

import java.time.LocalDate;

public abstract class Report {
    protected int reportId;
    protected LocalDate generatedDate;
    protected String content;

    public abstract String generate();
}

