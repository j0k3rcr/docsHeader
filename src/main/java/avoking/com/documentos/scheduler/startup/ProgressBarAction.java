/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.startup;

import avoking.com.documentos.scheduler.core.Core;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JProgressBar;

/**
 *
 * @author ramms
 */
public class ProgressBarAction implements PropertyChangeListener {
    private JProgressBar progressBar = new JProgressBar();
    private Core task;
    private String status = "nothing";
    
    public ProgressBarAction(dlgSplash dlg, Core task) {
        this.task = task;
        
        progressBar.setStringPainted(true);
        dlg.pnlProgress.add(progressBar);
        dlg.pnlProgress.revalidate();
        dlg.revalidate();
        
//        progressBar.setBounds(55, 180, 250, 15);
        
//        dlg.addMouseListener(new MouseAdapter() {
//                public void mousePressed(MouseEvent e) {
//                    dlg.setVisible(false);
//                    //dispose();
//                }
//            });
        
        task.addPropertyChangeListener(this);
        
    }
    
    public JProgressBar getBar(){
        return progressBar;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            progressBar.setString( String.format(
                    "Completed %d%% of task.\n", task.getProgress()) );
            status = "progress";
//            System.out.println(String.format(
//                    "Completed %d%% of task.\n", task.getProgress()));
        }
        
        if("state" == evt.getPropertyName()){
            status = "Done";
        }
    }
}
