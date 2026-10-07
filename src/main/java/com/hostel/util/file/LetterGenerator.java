package com.hostel.util.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class LetterGenerator {
    private static final String DIR = "allotment_letters";

    public static void generateAllotmentLetter(String studentName, String rollNo, String roomNo, String messPlan) {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File letterFile = new File(dir, "allotment_" + rollNo + ".txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(letterFile))) {
            writer.println("=========================================");
            writer.println("         HOSTEL ALLOTMENT LETTER         ");
            writer.println("=========================================");
            writer.println("Date: " + LocalDate.now());
            writer.println();
            writer.println("Dear " + studentName + " (Roll No: " + rollNo + "),");
            writer.println();
            writer.println("We are pleased to inform you that you have been successfully");
            writer.println("allotted a room in the hostel.");
            writer.println();
            writer.println("Details:");
            writer.println(" - Room Number: " + roomNo);
            writer.println(" - Mess Plan: " + messPlan);
            writer.println();
            writer.println("Please contact the warden for your keys.");
            writer.println();
            writer.println("Best Regards,");
            writer.println("Hostel Administration");
            writer.println("=========================================");
        } catch (IOException e) {
            System.err.println("Failed to generate allotment letter: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
