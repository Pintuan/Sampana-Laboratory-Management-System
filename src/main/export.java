/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;

/**
 *
 * @author Almar Dave
 */
public class export {

    Connection con = new Connection();
    Document document;

    public class footerHeader extends PdfPageEventHelper {

        protected PdfPTable footer, header;

        public footerHeader(PdfPTable footer, PdfPTable header) {
            this.footer = footer;
            this.header = header;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getPageSize();
            canvas(writer.getDirectContentUnder());
            header.setTotalWidth(rect.getWidth());
            header.writeSelectedRows(0, -1, 30, rect.getTop(120), writer.getDirectContent());
            footer.setTotalWidth(rect.getWidth());
            footer.writeSelectedRows(0, -1, -10, rect.getBottom(150), writer.getDirectContent());
        }
    }

    public void createPDF(String receiptId) {

        try {
            //declaration of file name and destination

            //filename is based on receipt Id
            String fileName = "\\" + receiptId + ".pdf";
            //destination is based on date today
            String dir = "C:\\SampanaLIMS\\" + LocalDate.now() + "\\" + receiptId;
            File directory = new File(dir);
            if (!directory.exists()) {
                // if the destination doesnt exist, will create a new folder
                directory.mkdirs();
            }
            System.out.println(dir + fileName);
            File file = new File(dir + fileName);
            file.createNewFile();
            document = new Document(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir + fileName));
            document.open();
            document.setMargins(40, 40, 200, 150);
            //header and footer declaration and assignment 
            footerHeader fh = new footerHeader(footer(receiptId), header(receiptId));
            writer.setPageEvent(fh);
            //end of header and footer event

            ResultSet rs = con.getData("Select t.category from test_taken tk join test t on tk.testId = t.testId where receiptId = " + receiptId + " and testVal != '' group by category");
            try {
                document.newPage();
                while (rs.next()) {
                    switch (rs.getString("category")) {
                        case "1000000111" -> {
                            document.add(ClinicalChembody(receiptId));
                            document.newPage();
                        }
                        case "1000000112" -> {
                            document.add(Hematology(receiptId));
                            document.newPage();
                        }
                        case "1000000113" -> {
                            document.add(ClinicalMicroscopy(receiptId));
                            document.newPage();
                        }
                        case "1000000114" -> {
                            document.add(Serology(receiptId));
                            document.newPage();
                        }
                        case "1000000115" -> {
                            document.add(ClinicalMicroscopy(receiptId));
                            document.newPage();
                        }
                        default -> {
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
            }
            document.close();
            Desktop.getDesktop().open(file);
        } catch (DocumentException | IOException e) {
            System.out.println(e);
        }
    }

    PdfPTable ClinicalChembody(String receiptId) {
        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(110);
        table.addCell(formatCell("Clinical Chemistry", Element.ALIGN_CENTER, 11, 14f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Conventional", Element.ALIGN_CENTER, 4, 11f, Font.BOLD));
        table.addCell(formatCell("SI", Element.ALIGN_CENTER, 4, 11f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));

        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Result", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell("Ref. Value", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Unit", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell("Result", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell("Ref. Value", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Unit", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell("Flag", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));

        ResultSet rs = con.getData("select tk.receiptId,"
                + "t.testId,"
                + "test,"
                + "subtest,"
                + "subsubtest,"
                + "tk.testVal,"
                + "conRefRangeLow,conRefRangeOperator, conRefRangeHigh, CONCAT(conRefRangeLow,conRefRangeOperator,conRefRangeHigh) as 'conRefRange',"
                + "t.conUnit,"
                + "siRefRangeLow,conRefRangeOperator,siRefRangeHigh, CONCAT(siRefRangeLow,conRefRangeOperator,siRefRangeHigh) as 'siRefRange',"
                + "t.siUnit,"
                + "t.siMultiplier,"
                + "tk.flag "
                + "from test t inner join test_taken tk on t.testId = tk.testId where receiptId = " + receiptId + " and isSendout = 10 and category =1000000111 order by orderNo");
        try {
            String lastTest = "";
            String test = "";
            String flag = "";
            float val = 0;
            while (rs.next()) {
                if (!rs.getString("test").equals(lastTest)) {
                    test = rs.getString("test") + "\n";
                }
                boolean isNum = this.isNumber(rs.getString("testVal"));
                if (isNum && rs.getString("siMultiplier") != null) {
                    val = Float.parseFloat(rs.getString("testVal")) * Float.parseFloat(rs.getString("siMultiplier"));
                }
                if (rs.getString("conRefRangeOperator") != null) {
                    if (isNum) {
                        float testVal = Float.parseFloat(rs.getString("testVal")),
                                conLow = Float.parseFloat(rs.getString("conRefRangeLow")),
                                conHigh = Float.parseFloat(rs.getString("conRefRangeHigh"));
                        switch (rs.getString("conRefRangeOperator")) {
                            case "-" -> {
                                if (testVal < conLow) {
                                    flag = "L";
                                } else if (testVal > conHigh) {
                                    flag = "H";
                                }
                            }
                            case "<" -> {
                                if (testVal > conHigh) {
                                    flag = "H";
                                }
                            }
                            default -> {
                                System.out.println(rs.getString("conRefRangeOperator"));
                            }
                        }
                    }
                }
                String conRefRange = "";
                String conRefRangeLow = rs.getString("conRefRangeLow");
                if (conRefRangeLow != null) {
                    float temp = Float.parseFloat(conRefRangeLow);
                    if (temp == 0 && rs.getString("conRefRangeOperator").equals("<")) {
                        conRefRangeLow = "";
                        conRefRange = conRefRangeLow + rs.getString("conRefRangeOperator") + rs.getString("conRefRangeHigh");
                    } else {
                        conRefRange = conRefRangeLow + rs.getString("conRefRangeOperator") + rs.getString("conRefRangeHigh");
                    }
                }
                String siRefRange = "";
                String siRefRangeLow = rs.getString("siRefRangeLow");
                int dec = Integer.parseInt(decimalCount(siRefRangeLow == null ? "0.00" : rs.getString("siRefRangeLow")));
                if (dec == 1) {
                    dec++;
                }
                BigDecimal res = new BigDecimal("0.0000");
                if (siRefRangeLow != null) {
                    float temp = Float.parseFloat(siRefRangeLow);
                    if (temp == 0 && rs.getString("conRefRangeOperator").equals("<")) {
                        siRefRangeLow = "";
                        siRefRange = siRefRangeLow + rs.getString("conRefRangeOperator") + rs.getString("siRefRangeHigh");
                    } else {
                        siRefRange = siRefRangeLow + rs.getString("conRefRangeOperator") + rs.getString("siRefRangeHigh");
                    }
                }
                if (val != 0) {
                    BigDecimal value = new BigDecimal(rs.getString("testVal"));
                    BigDecimal multi = new BigDecimal(rs.getString("siMultiplier"));
                    res = value.multiply(multi).setScale(dec,RoundingMode.HALF_UP);
                    System.out.println(res);
                }
                table.addCell(formatCell(test + "         " + rs.getString("subtest"), Element.ALIGN_LEFT, 2, 11f));
                table.addCell(formatCell(rs.getString("testVal"), Element.ALIGN_CENTER, 1, 11f));
                table.addCell(formatCell(conRefRange, Element.ALIGN_CENTER, 2, 11f));
                table.addCell(formatCell(rs.getString("conUnit"), Element.ALIGN_CENTER, 1, 11f));
                System.out.println(val);
                table.addCell(formatCell(String.valueOf(val == 0 ? "" : res), Element.ALIGN_CENTER, 1, 11f));
                table.addCell(formatCell(siRefRange, Element.ALIGN_CENTER, 2, 11f));
                table.addCell(formatCell(rs.getString("siUnit"), Element.ALIGN_CENTER, 1, 11f));

                table.addCell(formatCell(flag, Element.ALIGN_CENTER, 1, 11f));
                table.completeRow();
                lastTest = rs.getString("test");
                test = "";
                flag = "";
                val = 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    PdfPTable Hematology(String receiptId) {

        String lastTest = "", test = "";
        String subtest = "", lastsubTest = "";
        String flag = "";
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell(formatCell("Hematology", Element.ALIGN_CENTER, 6, 14f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Result", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell("Ref. Value", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell("Unit", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        ResultSet rs = con.getData("select tk.receiptId,"
                + "t.testId,"
                + "test,"
                + "subtest,"
                + "subsubtest,"
                + "tk.testVal,"
                + "t.withValue,"
                + "t.conRefRangeLow,"
                + "t.conRefRangeOperator,"
                + "conRefRangeHigh,"
                + "t.conUnit,"
                + "t.siMultiplier,"
                + "tk.flag "
                + "from test t inner join test_taken tk on t.testId = tk.testId where receiptId = " + receiptId + " and isSendout = 10 and category =1000000112 order by orderNo");
        try {
            while (rs.next()) {
                String value = rs.getString("testVal");
                System.out.println(value);
                boolean isNumeric = this.isNumber(value);
                if (rs.getString("conRefRangeOperator") != null && isNumeric) {
                    if (isNumeric) {
                        float testVal = Float.parseFloat(rs.getString("testVal")),
                                conLow = Float.parseFloat(rs.getString("conRefRangeLow")),
                                conHigh = Float.parseFloat(rs.getString("conRefRangeHigh"));
                        switch (rs.getString("conRefRangeOperator")) {
                            case "-" -> {
                                if (testVal < conLow) {
                                    flag = "L";
                                } else if (testVal > conHigh) {
                                    flag = "H";
                                }
                            }
                            case "<" -> {
                                if (testVal < conHigh) {
                                    flag = "L";
                                }
                            }
                            default -> {
                                System.out.println(rs.getString("conRefRangeOperator"));
                            }
                        }
                    }
                }
                if (!rs.getString("test").equals(lastTest)) {
                    test = rs.getString("test") + "\n    ";
                }
                if (!rs.getString("subtest").equals(lastsubTest)) {
                    subtest = rs.getString("subtest") + "\n";

                }
                table.addCell(formatCell(test + "     " + subtest + "     " + rs.getString("subsubtest"), Element.ALIGN_LEFT, 2, 11f));
                table.addCell(formatCell(rs.getString("testVal"), Element.ALIGN_CENTER, 1, 11f));
                if (rs.getString("conRefRangeOperator") != null) {
                    String format = "%." + decimalCount(rs.getString("testVal")) + "f";
                    Float low = Float.valueOf(rs.getString("conRefRangeLow")), high = Float.valueOf(rs.getString("conRefRangeHigh"));
                    System.out.println(format);
                    System.out.println(low);
                    System.out.println(high);
                    table.addCell(formatCell(String.format(format, low) + " " + rs.getString("conRefRangeOperator") + " " + String.format(format, high), Element.ALIGN_CENTER, 1, 11f));
                } else {
                    table.addCell(formatCell(rs.getString("conUnit"), Element.ALIGN_CENTER, 1, 11f));
                }

                table.addCell(formatCell(rs.getString("conUnit"), Element.ALIGN_CENTER, 1, 11f));

                table.addCell(formatCell(flag, Element.ALIGN_CENTER, 1, 11f));
                table.completeRow();
                lastTest = rs.getString("test");
                test = "";
                subtest = "";
                flag = "";
            }
        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    PdfPTable ClinicalMicroscopy(String receiptId) {
        String test = "", lastTest = "";
        String subtest = "", lastsubTest = "";

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell(formatCell("Clinical Microscopy", Element.ALIGN_CENTER, 5, 14f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Result", Element.ALIGN_CENTER, 2, 11f, Font.BOLD));
        table.addCell(formatCell("Unit", Element.ALIGN_CENTER, 1, 11f, Font.BOLD));
        ResultSet rs = con.getData("select tk.receiptId,"
                + "t.testId,"
                + "test,"
                + "subtest,"
                + "subsubtest,"
                + "tk.testVal,"
                + "CONCAT(conRefRangeLow,conRefRangeOperator,conRefRangeHigh) as 'conRefRange',"
                + "t.conUnit,"
                + "t.withValue,"
                + "tk.flag "
                + "from test t inner join test_taken tk on t.testId = tk.testId where receiptId = " + receiptId + " and isSendout = 10 and category =1000000113 order by orderNo");
        try {
            while (rs.next()) {
                if (!rs.getString("test").equals(lastTest)) {
                    test = rs.getString("test") + "\n";
                }
                if (!rs.getString("subtest").equals(lastsubTest)) {
                    subtest = rs.getString("subtest") + "\n       ";
                }
                if (rs.getString("testVal").isBlank() && rs.getString("withValue").equals("0")) {
                    table.addCell(formatCell(test
                            + "     " + subtest
                            + "      " + rs.getString("subsubtest"), Element.ALIGN_LEFT, 2, 11f));
                    table.addCell(formatCell(rs.getString("testVal"), Element.ALIGN_CENTER, 2, 11f));
                    table.addCell(formatCell(rs.getString("conUnit"), Element.ALIGN_CENTER, 1, 11f));
                } else if (!rs.getString("testVal").isBlank() && rs.getString("withValue").equals("1")) {
                    table.addCell(formatCell(test
                            + "     " + subtest
                            + "      " + rs.getString("subsubtest"), Element.ALIGN_LEFT, 2, 11f));
                    table.addCell(formatCell(rs.getString("testVal"), Element.ALIGN_CENTER, 2, 11f));
                    table.addCell(formatCell(rs.getString("conUnit"), Element.ALIGN_CENTER, 1, 11f));
                }
                table.completeRow();
                test = "";
                subtest = "";
                lastTest = rs.getString("test");
                lastsubTest = rs.getString("subtest");
            }
        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    PdfPTable Serology(String receiptId) {
        String test = "", lastTest = "";
        String subtest = "", lastsubTest = "";
        boolean trop = false;

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell(formatCell("Immunology / Serology", Element.ALIGN_CENTER, 5, 14f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 2, 11f));
        table.addCell(formatCell("Result", Element.ALIGN_CENTER, 1, 11f));
        table.addCell(formatCell("Lot Number", Element.ALIGN_CENTER, 1, 11f));
        table.addCell(formatCell("Expiration Date", Element.ALIGN_CENTER, 1, 11f));
        ResultSet rs = con.getData("select tk.receiptId, t.*,tk.testVal,tk.lotNo,tk.expDate from test t inner join test_taken tk on t.testId = tk.testId where receiptId = " + receiptId + " and isSendout = 10 and category =1000000114 order by test,subtest,subsubtest");
        try {
            while (rs.next()) {
                if (rs.getString("conRefRangeOperator") == null) {
                    if (!rs.getString("test").equals(lastTest)) {
                        test = rs.getString("test") + "\n";
                    }
                    if (!rs.getString("subtest").equals(lastsubTest)) {
                        subtest = rs.getString("subtest") + "\n";
                    }
                    table.addCell(formatCell(test + "     " + subtest, Element.ALIGN_LEFT, 2, 11f));
                    table.addCell(formatCell(rs.getString("testVal"), Element.ALIGN_CENTER, 1, 11f));
                    table.addCell(formatCell(rs.getString("lotNo"), Element.ALIGN_CENTER, 1, 11f));
                    table.addCell(formatCell(("1900-01-01".equals(rs.getString("expDate")) ? "" : rs.getString("expDate")), Element.ALIGN_CENTER, 1, 11f));
                    table.completeRow();
                    lastTest = rs.getString("test");
                    test = "";
                    lastsubTest = rs.getString("subtest");
                    subtest = "";
                } else {
                    trop = true;
                }
                if (trop) {
                    document.newPage();
                    try {
                        document.add(extSero(receiptId));
                        document.newPage();
                    } catch (DocumentException ex) {
                        Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    PdfPTable extSero(String receiptId) {
        String test = "", lastTest = "";
        String subtest = "", lastsubTest = "";

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell(formatCell("Immunology / Serology", Element.ALIGN_CENTER, 6, 14f, Font.BOLD));
        table.addCell(formatCell(" ", Element.ALIGN_CENTER, 2, 11f));
        table.addCell(formatCell("Result", Element.ALIGN_CENTER, 1, 11f));
        table.addCell(formatCell("Ref. Value", Element.ALIGN_CENTER, 1, 11f));
        table.addCell(formatCell("Unit", Element.ALIGN_CENTER, 1, 11f));
        table.addCell(formatCell("Flag", Element.ALIGN_CENTER, 1, 11f));
        ResultSet rs = con.getData("select tk.receiptId, t.*,tk.testVal from test t inner join test_taken tk on t.testId = tk.testId where receiptId = " + receiptId + " and isSendout = 10 and category =1000000114 and conRefRangeOperator != '' order by test,subtest,subsubtest");
        try {
            while (rs.next()) {

                String flag = null;
                boolean isNumeric = this.isNumber(rs.getString("testVal"));
                if (rs.getString("conRefRangeOperator") != null) {
                    if (isNumeric) {
                        float testVal = Float.parseFloat(rs.getString("testVal")),
                                conLow = Float.parseFloat(rs.getString("conRefRangeLow")),
                                conHigh = Float.parseFloat(rs.getString("conRefRangeHigh"));
                        switch (rs.getString("conRefRangeOperator")) {
                            case "-" -> {
                                if (testVal < conLow) {
                                    flag = "L";
                                } else if (testVal > conHigh) {
                                    flag = "H";
                                }
                            }
                            case "<" -> {
                                if (testVal > conHigh) {
                                    flag = "H";
                                }
                            }
                            default -> {
                                System.out.println(rs.getString("conRefRangeOperator"));
                            }
                        }
                    }
                }

                if (!rs.getString("test").equals(lastTest)) {
                    test = rs.getString("test") + "\n";
                }
                if (!rs.getString("subtest").equals(lastsubTest)) {
                    subtest = rs.getString("subtest") + "\n";
                }
                String refRange = rs.getString("conRefRangeLow") + " " + rs.getString("conRefRangeOperator") + " " + rs.getString("conRefRangeHigh");
                table.addCell(formatCell(test + "     " + subtest, Element.ALIGN_LEFT, 2, 11f));
                table.addCell(formatCell(rs.getString("testVal"), Element.ALIGN_CENTER, 1, 11f));
                table.addCell(formatCell(refRange, Element.ALIGN_CENTER, 1, 11f));
                table.addCell(formatCell(rs.getString("conUnit"), Element.ALIGN_CENTER, 1, 11f));
                table.addCell(formatCell(flag, Element.ALIGN_CENTER, 1, 11f));
                table.completeRow();
                lastTest = rs.getString("test");
                test = "";
                lastsubTest = rs.getString("subtest");
                subtest = "";

            }
        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    PdfPTable header(String ReceiptId) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        String[] sex = {"Male", "Female"};
        try {
            ResultSet rs = con.getData("select *, DATEDIFF(year, p.bdate,r.receiptdate) AS age from receipt r inner join patient p on r.patientId = p.patientId where receiptId = " + ReceiptId);
            PdfPCell cell = new PdfPCell();
            cell.setBorderWidth(0);
            if (rs.next()) {
                table.addCell(formatCell("Name : " + rs.getString("fullname"), Element.ALIGN_LEFT, 1, 11f));
                table.addCell(formatCell(" ", Element.ALIGN_LEFT, 1, 11f));
                table.addCell(formatCell("Date : " + rs.getString("receiptDate"), Element.ALIGN_LEFT, 1, 11f));
                table.completeRow();

                table.addCell(formatCell("DOB : " + ("1900-01-01".equals(rs.getString("bdate")) ? "" : rs.getString("bdate")), Element.ALIGN_LEFT, 1, 11f));
                table.addCell(formatCell(" ", Element.ALIGN_LEFT, 1, 11f));
                table.addCell(formatCell("Age : " + rs.getString("age"), Element.ALIGN_LEFT, 1, 11f));
                table.completeRow();

                table.addCell(formatCell("Physician : " + rs.getString("reqDoctor"), Element.ALIGN_LEFT, 1, 11f));
                table.addCell(formatCell(" ", Element.ALIGN_LEFT, 1, 11f));
                table.addCell(formatCell("Sex : " + sex[Integer.parseInt(rs.getString("sex"))], Element.ALIGN_LEFT, 1, 11f));
                table.completeRow();

            }

        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    PdfPTable footer(String ReceiptId) {
        PdfPTable table = new PdfPTable(2);
        try {
            String[] pos = {"pathologist", "medtech"};
            ResultSet rs = con.getData("select * from receipt where receiptId = " + ReceiptId);
            if (rs.next()) {
                for (int i = 0; i < 2; i++) {
                    ResultSet temp = con.getData("""
                                                 select u.LicenseId, CONCAT(u.FName, ' ', u.MName,' ', u.LName) as name, p.position
                                                 from receipt as rec
                                                 join users as u on rec.""" + pos[i] + " = u.LicenseId\n"
                            + "join position as p on u.pos = p.PosId\n"
                            + "where receiptId = " + ReceiptId);
                    if (temp.next()) {
                        table.addCell(formatCell((temp.getString("name") + "\n" + temp.getString("position") + "\nPRC ID no.:" + temp.getString("LicenseId")), Element.ALIGN_CENTER, 1, 9f));
                    }
                }
                table.completeRow();
            }

        } catch (SQLException ex) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    void canvas(PdfContentByte canvas) {

        try {
            Image image = Image.getInstance("C:\\Sampana\\Pictures\\testBG.jpg");
            image.scaleAbsolute(PageSize.LEGAL);
            image.setAbsolutePosition(0, 0);
            canvas.saveState();
            PdfGState state = new PdfGState();
            state.setFillOpacity(1f);
            canvas.setGState(state);
            canvas.addImage(image);
            canvas.restoreState();
        } catch (DocumentException | IOException e) {
            Logger.getLogger(export.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    PdfPCell formatCell(String text, int allignment, int columnSpan, float size) {

        Phrase phrase = new Phrase(text);
        Font font = FontFactory.getFont("C:\\Sampana\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, Font.NORMAL, BaseColor.BLACK);
        phrase.setFont(font);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(allignment);
        cell.setBorderWidth(0);
        cell.setColspan(columnSpan);
        cell.setMinimumHeight(25);
        return cell;
    }

    PdfPCell formatCell(String text, int allignment, int columnSpan, float size, int fontType) {

        Font font = FontFactory.getFont("C:\\Sampana\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, Font.BOLD, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(allignment);
        cell.setBorderWidth(0);
        cell.setColspan(columnSpan);
        cell.setMinimumHeight(25);
        return cell;
    }

    String decimalCount(String val) {
        int integerPlaces = val.indexOf(".");
        if (!val.contains(".")) {
            return String.valueOf(1);
        } else {
            int decimalPlaces = val.length() - integerPlaces - 1;
            return String.valueOf(decimalPlaces);
        }
    }

    boolean isNumber(String val) {
        return val.matches("^[0-9]\\d*(\\.\\d+)?$");
    }

    double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        if (places < 0) {
            bd = BigDecimal.valueOf(value);
            bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } else {
            return bd.doubleValue();
        }
    }

    public static void main(String[] args) {
        export e = new export();
        e.createPDF("994443913");
    }
}
