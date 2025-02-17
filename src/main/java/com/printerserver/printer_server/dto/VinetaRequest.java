package com.printerserver.dto;

public class VinetaRequest {
    private String printerName;
    private String zplData;
    private String token;

    public String getToken(){
        return token;
    }

    public String getZPLcode() {
        return zplData;
    }

    public String getPrinterName(){
        return printerName;
    }

    public void setZplData(String zplData) {
        this.zplData = zplData;
    }

    public void setPrinterName(String printerName){
        this.printerName = printerName;
    }
}