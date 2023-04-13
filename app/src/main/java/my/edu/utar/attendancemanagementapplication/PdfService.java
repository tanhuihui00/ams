package my.edu.utar.attendancemanagementapplication;

import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PdfService {
    Font titlefont= new Font(Font.FontFamily.TIMES_ROMAN,16f,Font.BOLD);
    Font bodyfont=new Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL);
    private PdfWriter pdf;


    private File createFile(){
        String title="Student.pdf";
        File path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, title);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private Document createDocument() {
        //Create Document
        Document document =new Document();
        document.setMargins(24f, 24f, 32f, 32f);
        document.setPageSize(PageSize.A4)  ;
        return document;
    }


    private void setupPdfWriter(Document document, File file)  {
        try {
            pdf = PdfWriter.getInstance(document, new FileOutputStream(file));
            pdf.setFullCompression();
            //Open the document
            document.open();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private PdfPCell createCell(String content)  {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER) ;
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //setup padding
        cell.setPadding(8f);
        cell.isUseAscender();
        cell.setPaddingLeft(4f);
        cell.setPaddingRight(4f);
        cell.setPaddingTop(8f);
        cell.setPaddingBottom(8f) ;
        return cell;
    }
    private PdfPTable createTable(int column,float[]columnWidth) {
        PdfPTable table = new PdfPTable(column);
        table.setWidthPercentage(100f);
        try {
            table.setWidths(columnWidth);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setHeaderRows(1);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        return table;
    }

    private void addLineSpace(Document document, int number) {
        for (int i=0;i<number;i++) {
            try {
                document.add(new Paragraph(" "));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    private Paragraph createParagraph( String content){
        Paragraph paragraph = new Paragraph(content, bodyfont);
        paragraph.setFirstLineIndent(25f);
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        return paragraph;
    }

    File createAttendanceTable(List<Students> data ,List<String> paragraphList) throws DocumentException {
        File file=createFile();
        Document document=createDocument();
        //Setup PDF Writer
        setupPdfWriter(document, file);

        //Add Title
        //Add Empty Line as necessary
        addLineSpace(document, 1);

        //Add paragraph
        for (String content:paragraphList){

          Paragraph paragraph = createParagraph(content);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        addLineSpace(document, 1);


        //Add table title
        document.add(new Paragraph("Attendance Data", titlefont));
        addLineSpace(document, 1);


        //Define Table
        float userIdWidth = 0.8f;
        float firstNameWidth = 1f;
        float statusWidth = 1f;
        float[] columnWidth = new float[]{ firstNameWidth,userIdWidth, statusWidth};
        PdfPTable table = createTable(3, columnWidth);
        //Table header (first row)
        List<String> tableHeaderContent = Arrays.asList("Name", "ID", " Status") ;

        //write table header into table
        for (String it:tableHeaderContent){

           PdfPCell cell = createCell(it);
            //add our cell into our table
            table.addCell(cell);
        }

        //write user data into table
        for(Students it:data){
            //Write Each User Id
            PdfPCell NameCell = createCell(it.getName().toString());
            table.addCell(NameCell);
            //Write Each First Name
            PdfPCell idCell = createCell(it.getId().toString());
            table.addCell(idCell);
            //Write Each Last Name
            PdfPCell statusCell = createCell(it.getStatus().toString());
            table.addCell(statusCell);
        }

        document.add(table);
        document.close();

        try {
            pdf.close();
        } catch ( Exception ex) {
            Log.d("tag","Error:"+ex.getMessage());
        } finally {
           return file;
        }
    }
}
