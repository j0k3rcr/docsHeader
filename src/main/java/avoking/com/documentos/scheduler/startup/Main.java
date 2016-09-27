/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.startup;

import avoking.com.documentos.scheduler.View.frmPortal;
import avoking.com.documentos.scheduler.core.Core;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ramms
 */
public class Main extends SwingWorker<String, String>{
    private static final Logger LOGGER = LoggerFactory.getLogger(Core.class);
    public static  ApplicationContext ctx;
    private String departamento;
    private int rol;
    private String pathPro;
    private String pathPol;
    private String pathEsp;
    static dlgSplash splashLoading = new dlgSplash(null, true);
    public static frmPortal frmPortal = new frmPortal();
    
    public static void main(String[] args) {
//        SplashWindow s = new SplashWindow("E:\\Documentos\\NetBeansProjects\\documentos.scheduler.core\\src\\main\\java\\avoking\\com\\documentos\\scheduler\\startup\\SplashScreen reducida.png", null, 10000);
//        s.setVisible(true);
        //      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
            //</editor-fold>  
        Main m = new Main();
        m.execute();
        splashLoading.getContentPane().setBackground(Color.WHITE);
        splashLoading.pnlProgress.setBackground(Color.WHITE);
        splashLoading.setLocationRelativeTo(null);
        splashLoading.setVisible(true);
    }
    
    public void init(){
        ctx = new ClassPathXmlApplicationContext("/spring-test-dao-context.xml");
        
        LOGGER.info("Iniciando aplicación....");
        
        loadParams();
        
        LOGGER.info("Se especificaron las siguientes ruta");
        LOGGER.info("Procedimientos: \t" + pathPro);
        LOGGER.info("Especificaciones: \t" + pathEsp);
        LOGGER.info("Politicas: \t" + pathPol);
        LOGGER.info("Comenzando exploración.");
        LOGGER.debug("Creando hyper-threading");
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Core instancia1 = null;
                Core instancia2 = null;
                Core instancia3 = null;
                
                if((pathPro.equals(pathEsp)) && (pathPro.equals(pathPol)))
                    instancia1 = new Core(pathPro);
                else if(pathPol.equals(pathEsp))
                    instancia2 = new Core(pathPol);
                else {
                    if(!pathPro.isEmpty())
                        instancia1  = new Core(pathPro);

                    if(!pathEsp.isEmpty())
                        instancia2  = new Core(pathEsp);

                    if(!pathPol.isEmpty())
                        instancia3  = new Core(pathPol);
                }
                
                
                if(instancia1 != null){
                    new ProgressBarAction(splashLoading, instancia1);
                    instancia1.execute();
                }
                if(instancia2 != null){
                    new ProgressBarAction(splashLoading, instancia2);
                    instancia2.execute();
                }
                if(instancia3 != null){
                    new ProgressBarAction(splashLoading, instancia3);
                    instancia3.execute();
                }
                
                try {
                    String si1, si2, si3;
                    si1 = si2 = si3 = "";
                    
                    if(instancia1 != null)
                        si1 = instancia1.get();
                    if(instancia2 != null)
                        si2 = instancia2.get();
                    if(instancia3 != null)
                        si3 = instancia3.get();
                    
                    splashLoading.dispose();
                    
                    if(!si1.isEmpty() || !si2.isEmpty() || !si3.isEmpty()){
                        LOGGER.warn("Se encontraron los siguientes errores:\n" + si1 + "\n" + si2 + "\n" + si3);
                        JOptionPane.showMessageDialog(null, "Se encontraron los siguientes errores: \n" + si1 + "\n" + si2 + "\n" + si3, "Se encontraron errores al realizar la busqueda.", JOptionPane.ERROR_MESSAGE);
                    }
                    frmPortal.setLocationRelativeTo(null);
                    frmPortal.setVisible(true);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        Thread hilo = new Thread(run);
        LOGGER.debug("Iniciando el hilo del mal :D");
        hilo.start();
        
//        if(!path.equals("Error 404.")){
//            //Ruta de los documentos
//            LOGGER.info("Ruta de los documentos: " + path);
//            //Explorar/crear objetos(Sacar departamento)
//            LOGGER.info("Obteniendo datos...");
//            
//        }
    }
    
    public String getPathDocs(JFileChooser fc){
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        int opc = fc.showOpenDialog(null);
        if (opc == JFileChooser.APPROVE_OPTION){
            File f = fc.getSelectedFile();
            if(f != null){
                if(f.isDirectory())
                    return f.getAbsolutePath();
                else
                    return f.getParentFile().getAbsolutePath();
            }else {
//                JOptionPane.showMessageDialog(null, "Selecciona una ruta valida!!", "Directorio incorrecto", JOptionPane.WARNING_MESSAGE);
                return "";
            }
        }else
            return "";
    }
    
    public boolean loadParams() {
        Properties props = new Properties();
        InputStream is = null;

        File f = new File("config.properties");

        if(f.exists()){
            try {
                is = new FileInputStream( f );
            } catch ( Exception e ) { is = null; }
            try {
                if ( is == null ) 
                    is = getClass().getResourceAsStream("config.properties");
                
                props.load( is );
                departamento = props.getProperty("departamento", "desconocido");
                rol = new Integer(props.getProperty("rol", "30"));
                pathPro = props.getProperty("pathProcedimiento", "");
                pathEsp = props.getProperty("pathEspecificacion", "");
                pathPol = props.getProperty("pathPolitica", "");
            }catch ( Exception e ) {
                nuevoArch();
            }
        }else
            nuevoArch();
                
        return false;
    }
    
    public void pathsDocs(){
        String userDir = System.getProperty("user.home");
        
        JOptionPane.showMessageDialog(null, "Ruta para PROCEDIMIENTOS...");
        pathPro = getPathDocs(new JFileChooser(userDir +"/Documents"));
        
        JOptionPane.showMessageDialog(null, "Ruta para ESPECIFICACIONES...");
        pathEsp = getPathDocs(new JFileChooser(pathPro));
        
        JOptionPane.showMessageDialog(null, "Ruta para POLITICAS...");
        pathPol = getPathDocs(new JFileChooser(pathEsp));
    }
    
    public void nuevoArch(){
        String value = JOptionPane.showInputDialog(null, "Introduce el rol:", 30);
        if(value != null)
            if(!value.isEmpty()){
                try {
                    int testRol = Integer.parseInt(value);
                    Object[] possibilities = {"RH", "MA", "PR", "SA", "DG", "DO", "QA",
                    "AM", "AP", "GC", "LO", "DP", "SI"};
                    value = (String) JOptionPane.showInputDialog(null, "Introduce el rol:", "Configuración inicial",
                            JOptionPane.PLAIN_MESSAGE, null, possibilities, "SI");
                    
                    pathsDocs();
                    
                    saveProps(value, testRol);
                    return;
                } catch(NumberFormatException ec) {
                    JOptionPane.showMessageDialog(null, "El rol es incorrecto, cargando por defecto...", "Rol incorrecto", JOptionPane.WARNING_MESSAGE);
                }
            }
        nuevoArch();
    }
    
    public void saveProps(String depart, int rol){
        try {
            Properties props = new Properties();
            props.setProperty("departamento", depart);
            props.setProperty("rol", rol + "");
            props.setProperty("pathProcedimiento", pathPro);
            props.setProperty("pathEspecificacion", pathEsp);
            props.setProperty("pathPolitica", pathPol);
            
            File f = new File("config.properties");
            OutputStream out = new FileOutputStream( f );
            props.store(out, "Configuracion scheduler ak (CRS)");
            JOptionPane.showMessageDialog(null, "Configuración creada en: ");
        }catch (Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground() throws Exception {
        init();
        return "Done";
    }
}
