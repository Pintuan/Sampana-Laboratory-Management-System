 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
public class HeaderFooterPageEvent extends PdfPageEventHelper {
    @Override
    public void onStartPage(PdfWriter writer,Document document) {
    	Rectangle rect = document.getPageSize();
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Center"), rect.getRight(), rect.getTop(), 0);
    }

    @Override
    public void onEndPage(PdfWriter writer,Document document) {
    	Rectangle rect = document.getPageSize();
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Center"), (rect.getWidth() / 2), rect.getBottom()+20, 0);
    }
}
