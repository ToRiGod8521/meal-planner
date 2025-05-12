package com.mealplanner.contorller;

import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealplanner.service.ShoppingListService;
import com.mealplanner.service.ShoppingListService.Item;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ShoppingListExportController {
	@Autowired
	private ShoppingListService shoppingListService;
	
	@GetMapping(value = "/api/shopping-list/export/csv" ,produces = "text/csv")
	public void exportCsv(HttpServletResponse resp, java.security.Principal principal) throws Exception{
		Long userId = Long.valueOf(principal.getName());
		List<Item> list = shoppingListService.buildWeekList(userId);
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/csv; charset=UTF-8");
		resp.setHeader("Content-Disposition", "attachment; filename=\"shopping-list.csv\"");
		
		try (PrintWriter w = resp.getWriter()) {
            w.println("食材,數量");
            for (Item it : list) {
                w.printf("\"%s\",%d%n", it.name.replace("\"","\"\""), it.count);
            }
        }
	}
	@GetMapping(value = "/api/shopping-list/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public void exportPdf(HttpServletResponse resp, java.security.Principal principal) throws Exception{
			Long userId = Long.valueOf(principal.getName());
			List<Item> list = shoppingListService.buildWeekList(userId);
			
			resp.setContentType("application/pdf");
			resp.setHeader("Content-Disposition", "attachment; filename=\"shopping-list.pdf\"");
			
			BaseFont bf = BaseFont.createFont(
					"MSung-Light",
					"UniCNS-UCS2-H",
					BaseFont.NOT_EMBEDDED
			);
			
			Font titleFont = new Font(bf, 16, Font.BOLD);
			Font headFont  = new Font(bf, 12, Font.BOLD);
			Font cellFont  = new Font(bf, 10, Font.NORMAL);
			
			Document doc = new Document(PageSize.A4);
			PdfWriter.getInstance(doc, resp.getOutputStream());
			doc.open();
			
			Paragraph title = new Paragraph("本週購物清單", titleFont);
		    title.setAlignment(Element.ALIGN_CENTER);
		    doc.add(title);
		    doc.add(Chunk.NEWLINE);
	        
	        PdfPTable table = new PdfPTable(new float[]{4f,1f});
	        table.setWidthPercentage(100);
	        
	        
	        PdfPCell h1 = new PdfPCell(new Phrase("食材", headFont));
	        PdfPCell h2 = new PdfPCell(new Phrase("數量(份)", headFont));
	        h1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        h2.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(h1);
	        table.addCell(h2);
	        
	        table.setHeaderRows(1);
	        
	        for (Item it : list) {
	        	PdfPCell c1 = new PdfPCell(new Phrase(it.name, cellFont));
	            PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(it.count), cellFont));
	            table.addCell(c1);  // <- 这里改成添加 PdfPCell
	            table.addCell(c2);
	        }
	        
	        doc.add(table);
	        doc.close();
	}
}
