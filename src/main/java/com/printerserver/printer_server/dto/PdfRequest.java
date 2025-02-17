package com.printerserver.dto;

public class PdfRequest {
    private String pdfUrl;
    private String printerName;
    private String token;

    public String getToken(){
        return token;
    }

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

    public void setToken(String token){
        this.token = token;
    }
}