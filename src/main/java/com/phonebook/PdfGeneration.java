
package com.phonebook;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javafx.collections.ObservableList;

public class PdfGeneration {
    
    public void pdfGenerator(String fileName, ObservableList<Person> data) {
        Document document = new Document();
        try {
            // Logó
            PdfWriter.getInstance(document, new FileOutputStream(fileName + ".pdf"));
            document.open();
            Image image = Image.getInstance(getClass().getResource("telefonkonyv.jpg"));
            image.scaleToFit(400, 200);
            image.setAbsolutePosition(120f, 625f);
            document.add(image);
            
            // Táblázat lefelé tolása
            document.add(new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n"));
            
            // Táblázat
            float[] columnsWidth = {1, 3, 3, 4};
            PdfPTable table = new PdfPTable(columnsWidth);
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Contact List"));
            cell.setBackgroundColor(GrayColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(4);
            table.addCell(cell);
            
            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell("Id");
            table.addCell("Last Name");
            table.addCell("First Name");
            table.addCell("Email Address");
            table.setHeaderRows(1);
            
            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            
            int counter = 1;
            for (Person person : data) {
                table.addCell(String.valueOf(counter++));
                table.addCell(person.getLastName());
                table.addCell(person.getFirstName());
                table.addCell(person.getEmail());
            }
            
            document.add(table);
            
            // Aláírás
            Chunk signature = new Chunk("\n\n Generated by Phone book app!");
            
            Paragraph base = new Paragraph(signature);
            document.add(base);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        document.close();
    }
    
}
