package com.hostel.util.file;

import com.hostel.dao.BillingDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class ReportExporter {
    public static void exportPendingDues(List<BillingDAO.PendingDue> dues) {
        File csvFile = new File("monthly_mess_dues.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Bill ID,Student Name,Roll No,Amount Due,Due Date");
            for (BillingDAO.PendingDue due : dues) {
                writer.printf("%d,%s,%s,%.2f,%s%n",
                        due.billId,
                        escapeCSV(due.studentName),
                        due.rollNo,
                        due.amount,
                        due.dueDate.toString());
            }
        } catch (IOException e) {
            System.err.println("Failed to export report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String escapeCSV(String data) {
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            data = data.replace("\"", "\"\"");
            return "\"" + data + "\"";
        }
        return data;
    }
}
