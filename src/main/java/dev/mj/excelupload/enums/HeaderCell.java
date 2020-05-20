package dev.mj.excelupload.enums;

public enum HeaderCell {

    ID_HOTS("idHost"),
    CUIT("cuit"),
    DOCUMENT_TYPE("documentType"),
    DOCUMENT("document"),
    USER_NAME("username"),
    ID_ADHESION("idAdhesion");

    private String strHeaderCell;

    HeaderCell(String strHeaderCell) {
        this.strHeaderCell = strHeaderCell;
    }

    public String getStrHeaderCell(){
        return strHeaderCell;
    }
    
}
