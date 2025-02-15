package com.printerserver.controllers;
import com.printerserver.dto.PdfRequest;
import com.printerserver.dto.VinetaRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.Base64;

import javax.print.*;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
/* Para PDF */
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.awt.print.PrinterJob;

@RestController
@RequestMapping("/api")
public class PrintersController {

    @GetMapping("/printers")
    public List<Map<String, String>> printers() {
        List<Map<String, String>> printerList = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        
        for (PrintService printer : printServices) {
            String name = printer.getName();
            if (!name.toLowerCase().contains("pdf")) { // Excluir impresoras virtuales
                Map<String, String> printerInfo = new HashMap<>();
                printerInfo.put("name", name); // Obtiene el nombre de la impresora
                printerInfo.put("status", isPrinterOnline(printer) ? "online" : "offline");
                printerList.add(printerInfo);
            }
        }
        return printerList;
    }

    private boolean isPrinterOnline(PrintService printer) {
        PrintServiceAttributeSet attributes = printer.getAttributes();
        PrinterState state = (PrinterState) attributes.get(PrinterState.class);
        PrinterIsAcceptingJobs acceptingJobs = (PrinterIsAcceptingJobs) attributes.get(PrinterIsAcceptingJobs.class);

        // Si la impresora no está detenida y acepta trabajos, está en línea
        return (state == null || state != PrinterState.STOPPED) &&
               (acceptingJobs != null && acceptingJobs == PrinterIsAcceptingJobs.ACCEPTING_JOBS);
    }

    @GetMapping("/printTxt")
    public String printDocument(@RequestParam String printerName, @RequestBody String documentContent) {
        try {
            // Buscar la impresora por nombre
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService selectedPrinter = findPrinter(printerName);

            if (selectedPrinter == null) {
                return "Impresora no encontrada";
            }

            // Preparar el contenido del documento para imprimir
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;  // Asegúrate de que es texto plano
            InputStream is = new ByteArrayInputStream(documentContent.getBytes(StandardCharsets.UTF_8));  // Codifica en UTF-8
            Doc doc = new SimpleDoc(is, flavor, null);

            // Obtener el trabajo de impresión y enviarlo a la impresora
            DocPrintJob printJob = selectedPrinter.createPrintJob();
            printJob.print(doc, null);

            return "Documento enviado a impresión";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al enviar el trabajo de impresión: " + e.getMessage();
        }
    }

    @GetMapping("/printImage")
    public String printImage(@RequestParam String printerName) {
        try {
            URL imageUrl = new URL("https://cdn.pixabay.com/photo/2022/12/04/06/32/programmer-7633812_1280.jpg");
            InputStream imageStream = imageUrl.openStream();  // Abrir el flujo de la imagen desde la URL

            // Buscar la impresora por nombre
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService selectedPrinter = findPrinter(printerName);

            if (selectedPrinter == null) {
                return "Impresora no encontrada";
            }

            // Preparar el flujo de la imagen y el tipo de documento
            DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;  // Para imágenes JPEG
            Doc doc = new SimpleDoc(imageStream, flavor, null);

            // Crear el trabajo de impresión y enviarlo a la impresora
            DocPrintJob printJob = selectedPrinter.createPrintJob();
            printJob.print(doc, null);

            return "Imagen enviada a impresión";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al enviar el trabajo de impresión: " + e.getMessage();
        }
    }

    @PostMapping("/printPdf")
    public String printPdf(@RequestBody PdfRequest request) {
        try {
            //Descargar el PDF desde la URL
            String pdfUrl = request.getPdfUrl();
            String printerName = request.getPrinterName();
            URL url = new URL(pdfUrl);
            InputStream inputStream = url.openStream();
            PDDocument document = PDDocument.load(inputStream);

            //Buscar la impresora por nombre
            PrintService selectedPrinter = findPrinter(printerName);

            if (selectedPrinter == null) {
                return "No se encontró la impresora " + printerName;
            }

            //Configurar el trabajo de impresión
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(selectedPrinter);
            job.setPageable(new PDFPageable(document));

            //Configurar atributos de impresión (ejemplo: 1 copia)
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1));

            //Enviar a imprimir
            job.print(attributes);

            document.close();
            return "Documento enviado a la impresora " + printerName;
        } catch (Exception e) {
            return "Error al imprimir: " + e.getMessage();
        }
    }

    // Método para buscar la impresora por nombre
    private PrintService findPrinter(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            if (printer.getName().equalsIgnoreCase(printerName)) {
                return printer;
            }
        }
        return null;
    }

    @PostMapping("/printZebra")
    public String printZebra(@RequestBody VinetaRequest request) {
        try {
            String printerName = request.getPrinterName();
            String zplData = request.getZPLcode();

            if(zplData == null || printerName == null){
                return "Datos obligatorio : printerName y zplData";
            }

            // Buscar la impresora por nombre
            PrintService selectedPrinter = findPrinter(printerName);
            if (selectedPrinter == null) {
                return "No se encontró la impresora " + printerName;
            }

            // Convertir la cadena ZPL en un flujo de bytes
            byte[] zplBytes = zplData.getBytes();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            DocPrintJob job = selectedPrinter.createPrintJob();
            Doc doc = new SimpleDoc(zplBytes, flavor, null);

            // Configurar atributos de impresión
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1)); //numero de copias
            job.print(doc, attributes); //enviar impresion
            return "Etiqueta enviada a la impresora " + printerName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al imprimir: " + e.getMessage();
        }
    }
}