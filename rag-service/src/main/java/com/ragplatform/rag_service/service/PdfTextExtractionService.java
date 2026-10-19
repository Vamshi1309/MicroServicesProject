
package com.ragplatform.rag_service.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class PdfTextExtractionService {

    public String extractText(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return extractText(inputStream);
        }
    }

    public String extractText(InputStream inputStream) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            // Step 1: Try extracting existing PDF text
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(document);

            if (extractedText != null && !extractedText.isBlank()) {
                return extractedText;
            }

            // Step 2: Use OCR when the PDF contains scanned images
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(
                    "/usr/share/tesseract-ocr/5/tessdata");
            tesseract.setLanguage("eng");

            PDFRenderer renderer = new PDFRenderer(document);
            StringBuilder ocrText = new StringBuilder();

            for (int page = 0; page < document.getNumberOfPages(); page++) {

                BufferedImage image = renderer.renderImageWithDPI(page, 200);

                try {
                    ocrText.append(tesseract.doOCR(image));
                    ocrText.append("\n");
                } catch (TesseractException e) {
                    throw new IOException(
                            "OCR failed on PDF page " + (page + 1), e);
                }
            }

            return ocrText.toString();
        }
    }
}
