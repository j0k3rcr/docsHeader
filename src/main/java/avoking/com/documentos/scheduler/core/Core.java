/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.core;

import avoking.com.documentos.scheduler.View.pnlDocs;
import static avoking.com.documentos.scheduler.startup.Main.ctx;
import avoking.com.documentos.scheduler.core.dao.Docs;
import avoking.com.documentos.scheduler.core.dao.DocsDaoImpl;
import avoking.com.documentos.scheduler.startup.Main;
import avoking.com.documentos.scheduler.startup.SplashWindow;
import com.sun.javafx.applet.Splash;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ramms
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class Core extends SwingWorker<String, String>{
    private static final Logger LOGGER = LoggerFactory.getLogger(Core.class);
    private Session session = null;
    DocsDaoImpl ddi;
    String pathDoc;
    private String err = "";
    int cantDocs;
    int ca;
        
//    public static void main(String[] args) {
//        new Core("E:\\Avo-King\\Documentos\\Desarrollo\\PROCEDIMIENTO_Pruebas_RH");
//    }
//    
    public Core(String pathDocumentos){
        LOGGER.info("Iniciando core ->"+pathDocumentos);
        ddi = (DocsDaoImpl) ctx.getBean("docsDao");
        pathDoc = pathDocumentos;
        
//        ddi.getHibernateTemplate().getSessionFactory().getCurrentSession‌​().setFlushMode(Flus‌​hModeType.AUTO);
        
    }
    
    public FileFilter filtro = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if(f.isDirectory())
                return true;
            
            String ext = FilenameUtils.getExtension( f.getAbsolutePath() ).toUpperCase();
            switch(ext){
                case ("DOCX"):
                    return true;
                
                case ("DOC"):
                    return true;
                    
                default:
                    return false;
            }
        }

        @Override
        public String getDescription() {
            return "Documento de word 2013";
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    private String leerDoc(InputStream doc) throws IOException {
        POIFSFileSystem  fs = new POIFSFileSystem ( doc); 
        WordExtractor we = new WordExtractor(fs);
        return we.getText();
    }
	
    private String leerDocx(InputStream docx) throws IOException {
        //Se crea un documento que la POI entiende pasandole el stream
        //instanciamos el obj para extraer contenido pasando el documento
        XWPFWordExtractor xwpf_we = new XWPFWordExtractor(new XWPFDocument(docx)); 

        return xwpf_we.getText();
    }
    
//    public void explorar(String ruta){
//        LOGGER.info("Comenzando exploración para la carpeta: " + ruta);
//        
//        File directorio = new File(ruta);
//        File archivos[] = directorio.listFiles();
//        for(File f: archivos){
//            if(!f.getName().contains("~$"))
//                if(filtro.accept(f)){
//                    if(f.isDirectory())
//                        explorar( f.getAbsolutePath() );
//                    else {
//                        LOGGER.info("Procesando documento: " + f.getName() + " -> " + f.getParent());
//                        procesar(f.getAbsolutePath());
//                    }
//                }else {
//                    LOGGER.warn("Archivo denegado: " + f.getName());
//                }
//            else
//                LOGGER.warn("Archivo temporal no permitido.");
//        }
//    }
    
    public int explorarLength(String ruta, int cont){
        LOGGER.info("Comenzando exploración para la carpeta: " + ruta);
        
        File directorio = new File(ruta);
        File archivos[] = directorio.listFiles();
        for(File f: archivos){
            if(!f.getName().contains("~$"))
                if(filtro.accept(f)){
                    if(f.isDirectory()){
                        explorarLength( f.getAbsolutePath(), cont );
                    }else {
                        LOGGER.info("Procesando documento: " + f.getName() + " -> " + f.getParent());
//                        procesar(f.getAbsolutePath());
                        cont++;
                    }
                }else {
                    LOGGER.warn("Archivo denegado: " + f.getName());
                }
            else
                LOGGER.warn("Archivo temporal no permitido.");
        }
        
        return cont;
    }
        
    public void procesar(String file){
        InputStream entradaArch;
        try {
            entradaArch = new FileInputStream(file);
            String reader = "";

            if(FilenameUtils.getExtension(file).toUpperCase().equals("DOCX")){
                entradaArch = new FileInputStream(file);
                reader = leerDocx(entradaArch);
            }else{
                entradaArch = new FileInputStream(file);
                reader = leerDoc(entradaArch);
            }

            Docs doc;
            doc = readHeader.read(reader);
            if(doc.getClaveId() == null){
                err+="Error al obtener la clave del archivo: \n" + file + "\n";
//                JOptionPane.showMessageDialog(null, , "Error!!!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                session.save(doc);
                LOGGER.debug("Add: "+doc.getClaveId());
                Main.frmPortal.pnlCores.add( new pnlDocs(Color.yellow, doc));
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            }
            LOGGER.debug(doc.toString());
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected String doInBackground() throws Exception {
//        new SplashWindow("E:\\Documentos\\NetBeansProjects\\documentos.scheduler.core\\src\\main\\java\\avoking\\com\\documentos\\scheduler\\startup\\SplashScreen reducida.png", null, this);
        setProgress(0);
        LOGGER.info("Construyendo sesión hibernate.");
        try {
            session = ddi.getSessionFactory().getCurrentSession();
        } catch (HibernateException ex) {
            LOGGER.debug("Could not retrieve pre-bound Hibernate session", ex);
        }
        
        setProgress(3);

        if (session == null) {
            session = ddi.getSessionFactory().openSession();
            session.setFlushMode(FlushMode.MANUAL);
        }
        
        LOGGER.info("Iniciando exploración...");
        
        cantDocs = 0;
        Files.walk(Paths.get(pathDoc)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                File f = filePath.toFile();

                if(!f.getName().contains("~$"))
                    if(filtro.accept(f))
                        cantDocs++;
            }
        });
        
        LOGGER.debug("Cantidad de archivos a procesar: " + cantDocs);
        
        Files.walk(Paths.get(pathDoc)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                File f = filePath.toFile();

                if(!f.getName().contains("~$"))
                    if(filtro.accept(f)){
                        if(!f.isDirectory()) {
                            ca++;
//                            LOGGER.info("Procesando documento: " + f.getName() + " " + ca + " %" + (((ca*97)/cantDocs) + 3) );
                            setProgress( Math.min( ((ca*97)/cantDocs) + 3, 100) );
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException ex) {
//                                java.util.logging.Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
//                            }
                            
                            procesar(f.getAbsolutePath());
                        }
                    }
//                    else {
//                        LOGGER.warn("Archivo denegado: " + f.getName());
//                    }
            }
        });
        
        setProgress(100);
        
        LOGGER.debug("Terminado");
        
        return err;
    }
    
    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
        
    }
    
}
