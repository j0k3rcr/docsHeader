/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.core;

import avoking.com.documentos.scheduler.core.dao.Docs;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ramms
 */
public class readHeader {
    private static final Logger LOGGER = LoggerFactory.getLogger(Core.class);
    
    public static Docs read(String header){
        StringTokenizer st = new StringTokenizer(header);
        String sAct = "";
        Docs doc = new Docs();
        //Iniciando objto a nulo
        doc.setActualizacion(null);
        doc.setClaveId(null);
        doc.setDepart(null);
        doc.setDescrip(null);
        doc.setFecha(null);
        doc.setVigencia(null);
        doc.setVigenciaT(null);
        
        while( st.hasMoreElements() ){
            /*
            Text: AVO – KING S.A. DE C.V.
	Emitido por: Recursos Humanos  	PDRH-08
	PROCEDIMIENTO
“ANALISIS CLINICO PARA EL PERSONAL DE PRODUCCION
            */
            sAct = st.nextToken().toUpperCase();
            
            if(sAct.contains("CLAVE")){
                String clave = "";
                
                if(sAct.contains(":")) {
                    if(sAct.length()>"CLAVE:".length()){
                        clave = sAct.replace("CLAVE:", "");
                    }else
                        clave = st.nextToken();
                }else{
                    if(sAct.length()>"CLAVE".length()){
                        clave = sAct.replace("CLAVE", "");
                    }else
                        clave = st.nextToken();
                }
                
                doc.setClaveId(clave);
//                String clave = doc.getClaveId();
                String tipo = clave.substring(0, 2);
                String depart = clave.substring(2, 4);
                LOGGER.debug("Tipo doc: " + getTDocumento(tipo));
                LOGGER.debug("Depart: " + getDepart(depart));
                doc.setDepart( getDepart(depart) );
                LOGGER.info("CLAVE: " + clave);
            }else if(sAct.contains("ACTUALIZACIÓN")){
                doc.setActualizacion(st.nextToken());
                //LOGGER.info("ACTUALIZACIÓN: " + st.nextToken());
            }else if(sAct.contains("VIGENCIA")){
                String temp = st.nextToken();
                if(temp.contains("años")){
                    if(temp.contains(" ")){
                        //LOGGER.info("VIGENCIA: " + temp);
                        doc.setVigencia(st.nextToken());
                    }else {
                        //LOGGER.info("VIGENCIA: " + temp.split("años")[0] + " años");
                        doc.setVigencia(temp.split("años")[0]);
                        doc.setVigenciaT("años");
                    }
                }else{
                    //LOGGER.info("VIGENCIA: " + temp + " "+ st.nextToken());
                    doc.setVigencia(temp);
                    doc.setVigenciaT(st.nextToken());
                }
            }else if(sAct.contains("FECHA:")){
                String fecha = "";
                
                if(sAct.length()>"FECHA:".length())
                    fecha = sAct.replace("FECHA:", "");
                else
                    fecha = st.nextToken();
                
                LOGGER.debug("Fecha_ORG: " + fecha);
                String f = getMesLToN(fecha);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    if(f!= null)
                        doc.setFecha(formatter2.parse(formatter2.format(formatter.parse(f))));
                    else{
                        LOGGER.debug("Error en la fecha: " + f + " del doc: " + doc.getClaveId());
                        doc.setFecha(null);
                    }
                } catch (ParseException ex) {
                    LOGGER.error("Error al convertir la fecha: " + f + " del doc: " + doc.getClaveId());
                    doc.setFecha(null);
                }
                
//                String des = "";
//                //Buscar descrip
//                while(st.hasMoreTokens()){
//                    String tmp = st.nextToken();
//                    if(tmp != null)
//                        if(!tmp.toUpperCase().contains("1."))
//                            des += " " + tmp;
//                    else
//                        break;
//                }
//                    LOGGER.debug("Descripción: \n\t" + des);
                
                break;
            }
            
        }
        
        return doc;
    }
    
    public static String getTDocumento(String abrev){
        switch(abrev){
            case "PL":	
                return "POLITICA";
                
            case "PD":	
                return "PROCEDIMIENTO";
                
            case "ES":	
                return "ESPECIFICACION";
                
            case "LT":	
                return "LISTAS Y CATALOGOS";
                
            case "PS":	
                return "PROCEDIMIENTOS OPERATIVOS ESTANDAR EN SANEAMIENTO";
                
            case "POE":	
                return "PROCEDIMIENTOS OPERATIVOS ESTANDAR";
                
            case "FO":	
                return "FORMATO";
                
            case "PG":	
                return "PROGRAMA";
        }
        
        return "Indefinido [ " + abrev + " ]";
    }
    
    public static String getDepart(String abrev){
        switch(abrev){
            case "RH":
                return "Recursos Humanos";
            
            case "MA":
                return "Mantenimiento";
            
            case "PR":
                return "Producción";
            
            case "SA":
                return "Sanidad";
            
            case "DG":
                return "Dirección General";
            
            case "DO":
                return "Director de Operaciones";
            
            case "QA":
                return "Gerencia Aseguramiento de calidad";
            
            case "AM":
                return "Gerencia Abastecimiento Lavado y Maduración";
            
            case "AP":
                return "Gerencia Almacén y Producto terminado";
            
            case "GC":
                return "Gestión de Calidad";
            
            case "LO":
                return "Logística y Tráfico";
            
            case "DP":
                return "Investigación y Desarrollo de Nuevos productos";
            
            case "SI":
                return "Gerencia Sistemas";
        }
        
        return "Indefinido [ " + abrev + " ]";
    }
    
    /**
     * Convierte mes(en letra) a número.
     * Ejemplo 01-ENE-01 a 01-01-2001
     * @param fecha Fecha
     * @return Devuelve la fecha convertida
     */
    public static String getMesLToN(String fecha){
        if(fecha.contains("/"))
            fecha = fecha.replace("/", "-");
        else if(fecha.contains(" "))
            fecha = fecha.replace(" ", "-");
        
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
        String mesParts[] = fecha.split("-");
        int nmbMes = -1;
        
        String[] capitalMonths = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};
        
        try{
            for(int i=0;i<capitalMonths.length; i++)
                if(mesParts[1].substring(0, 3).equals(capitalMonths[i]))
                    nmbMes = i+1;
            
        } catch (ArrayIndexOutOfBoundsException e){
            LOGGER.error("Error al obtener el mes: " + fecha);
            return null;
        }
        
        if(nmbMes==-1){
            try{
                formatter2.parse(fecha);
                return fecha;
            }catch (ParseException e){
                return "-1";
            }
        }
        
        if(mesParts[2].trim().length()<=2)
            mesParts[2]= "20"+mesParts[2];
       
        String recFecha;
        if(nmbMes < 10)
            recFecha = mesParts[0]+"-0"+nmbMes+"-"+mesParts[2];
        else
            recFecha = mesParts[0]+"-"+nmbMes+"-"+mesParts[2];
        
        return recFecha;
    }
    
    /**
     * Convierte mes a letra
     * Ejemplo 01-01-01 a 01-ENE-2001
     * @param fecha Fecha
     * @return Devuelve la fecha convertida
     */
    public static String getMesNToL(String fecha){
        fecha = fecha.replace("/", "-");
        String mesParts[] = fecha.split("-");
        int nmbMes = -1;
        
        String[] capitalMonths = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

        String l = capitalMonths[ Integer.parseInt(mesParts[1])-1 ];
        if(l != null)
            if(!l.isEmpty()){
                String recFecha;
                
                if(nmbMes < 10)
                    recFecha = mesParts[0]+"-"+l+"-"+mesParts[2].substring(2, 4);
                else
                    recFecha = mesParts[0]+"-"+l+"-"+mesParts[2].substring(2, 4);
                
                  return recFecha;
            }
        
        return "-1";
    }
}
