package dev.mj.excelupload.model;

import java.io.Serializable;

public class OFBBase implements Serializable {

    private String idHostEmpresa;
    private String cuit;
    private String tipoDoc;
    private String nroDoc;
    private String idUsuario;
    private String nroUsuario;

    public String getIdHostEmpresa() {
        return idHostEmpresa;
    }

    public void setIdHostEmpresa(String idHostEmpresa) {
        this.idHostEmpresa = idHostEmpresa;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNroDoc() {
        return nroDoc;
    }

    public void setNroDoc(String nroDoc) {
        this.nroDoc = nroDoc;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNroUsuario() {
        return nroUsuario;
    }

    public void setNroUsuario(String nroUsuario) {
        this.nroUsuario = nroUsuario;
    }
}
