package com.example.rodarasa.model;

// This is a simple model to store the reported food truck data
// that will be displayed on the Profile page.
public class ReportedTruck {
    private String nameType;
    private String reportedDate; // Format: "yyyy-MM-dd"

    public ReportedTruck(String nameType, String reportedDate) {
        this.nameType = nameType;
        this.reportedDate = reportedDate;
    }

    // Getters
    public String getNameType() {
        return nameType;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    // You can add setters if needed, but for this use case, they are not strictly necessary.
    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }
}
