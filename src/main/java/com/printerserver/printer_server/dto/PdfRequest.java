package com.printerserver.dto;

public class PdfRequest {
    private String pdfUrl;
    private String printerName;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getPrinterName(){
        return printerName;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public void setPrinterName(String printerName){
        this.printerName = printerName;
    }
}