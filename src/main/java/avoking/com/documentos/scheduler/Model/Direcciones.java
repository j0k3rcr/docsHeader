/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.Model;

import java.io.File;

/**
 *
 * @author ramms
 */
public class Direcciones {
    int tipo;
    File f;

    public Direcciones(int tipo, File f) {
        this.tipo = tipo;
        this.f = f;
    }
    
    public int getTipo() {
        return tipo;
    }

    public File getF() {
        return f;
    }
}
