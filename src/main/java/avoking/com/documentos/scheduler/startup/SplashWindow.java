/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.startup;

import avoking.com.documentos.scheduler.core.Core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.scene.input.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.Timer;

/**
 *
 * @author ramms
 */
public class SplashWindow extends JWindow implements PropertyChangeListener {
    private static JProgressBar progressBar = new JProgressBar();
    private static Core task;
    
    public SplashWindow(String filename, Frame f, Core task) {
        super(f);
        
        this.task = task;
        
        JLabel l = new JLabel(new ImageIcon(filename));
        getContentPane().add(l, BorderLayout.CENTER);
        progressBar.setBounds(55, 180, 250, 15);
        getContentPane().add(progressBar, BorderLayout.SOUTH);
        pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
        
        
        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    setVisible(false);
                    dispose();
                }
            });
        
        setVisible(true);
        
        task.addPropertyChangeListener(this);
        task.execute();
    }
    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            progressBar.setString( String.format(
                    "Completed %d%% of task.\n", task.getProgress()) );
//            System.out.println(String.format(
//                    "Completed %d%% of task.\n", task.getProgress()));
        }
        
        if("state" == evt.getPropertyName()){
            dispose();
        }
    }
}