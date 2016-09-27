/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.core.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author ramms
 */
@Entity(name = "Docs")
@Table(name = "Docs")
public class Docs implements java.io.Serializable{
     /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6485105191418783803L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Column(name = "claveId", length = 20)
    private String claveId;
    @Column(name = "actualizacion", length = 5)
    private String actualizacion;
    @Column(name = "vigencia", length = 5)
    private String vigencia;
    @Column(name = "vigenciaT", length = 10)
    private String vigenciaT;
    @Column(name = "depart", length = 20)
    private String depart;
    @Column(name = "descrip", length = 50)
    private String descrip;
    @Column(name = "fecha")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;

    public String getClaveId() {
        return claveId;
    }

    public void setClaveId(String claveId) {
        this.claveId = claveId;
    }

    public String getActualizacion() {
        return actualizacion;
    }

    public void setActualizacion(String actualizacion) {
        this.actualizacion = actualizacion;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getVigenciaT() {
        return vigenciaT;
    }

    public void setVigenciaT(String vigenciaT) {
        this.vigenciaT = vigenciaT;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Docs " + "[id=" + id + ", claveId=" + claveId + ", actualizacion=" + actualizacion + ", vigencia=" + vigencia + ", vigenciaT=" + vigenciaT + ", depart=" + depart + ", descrip=" + descrip + ", fecha=" + fecha + ']';
    }
    
}
