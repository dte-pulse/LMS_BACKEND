package com.LMS.Pulse.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PdfService {

    public byte[] generateCertificateBytes(Long userId, String userName, String courseName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1️⃣ Add logo
            String logoUrl = "https://www.pulsepharma.net/wp-content/uploads/2022/08/Logo_1.png";
            Image logo = Image.getInstance(logoUrl);
            logo.scaleToFit(120, 120);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // 2️⃣ Certificate Title
            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, Color.BLACK);
            Paragraph title = new Paragraph("Certificate of Completion", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            // 3️⃣ Recipient
            Font recipientFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph recipient = new Paragraph("This certificate is awarded to", recipientFont);
            recipient.setAlignment(Element.ALIGN_CENTER);
            document.add(recipient);

            document.add(Chunk.NEWLINE);

            Paragraph userPara = new Paragraph(userName, new Font(Font.HELVETICA, 22, Font.BOLD, Color.BLUE));
            userPara.setAlignment(Element.ALIGN_CENTER);
            document.add(userPara);

            document.add(Chunk.NEWLINE);

            // 4️⃣ Course Details
            Paragraph coursePara = new Paragraph("For successfully completing the course:", new Font(Font.HELVETICA, 16));
            coursePara.setAlignment(Element.ALIGN_CENTER);
            document.add(coursePara);

            Paragraph courseNamePara = new Paragraph(courseName, new Font(Font.HELVETICA, 20, Font.BOLD, Color.DARK_GRAY));
            courseNamePara.setAlignment(Element.ALIGN_CENTER);
            document.add(courseNamePara);

            document.add(Chunk.NEWLINE);

            // 5️⃣ Footer - Instructor & Date
            Paragraph instructor = new Paragraph("Instructor: Tutorials Duniya", new Font(Font.HELVETICA, 14));
            instructor.setAlignment(Element.ALIGN_CENTER);
            document.add(instructor);

            Paragraph date = new Paragraph("Date: " + java.time.LocalDate.now(), new Font(Font.HELVETICA, 14));
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
