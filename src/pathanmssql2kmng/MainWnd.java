/*
 * MainWnd.java
 *
 * Created on 1 �������� 2008 �., 22:56
 */

package pathanmssql2kmng;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.plaf.metal.MetalIconFactory;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.*;

import java.sql.*;                  
import javax.sql.*;
import com.microsoft.sqlserver.jdbc.*;
//import com.inet.tds.*;
//import com.inet.pool.*;
/**
 *
 * @author  uas
 */
public class MainWnd extends javax.swing.JFrame {
    
    /** Creates new form MainWnd */
    public MainWnd() {
        initComponents();
        
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //"com.sun.java.swing.plaf.motif.MotifLookAndFeel");            
            //"javax.swing.plaf.metal.MetalLookAndFeel""
            //com.sun.java.swing.plaf.gtk.GTKLookAndFeel
            //UIManager.getCrossPlatformLookAndFeelClassName()
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception evt) {
            JOptionPane.showMessageDialog(null, "setLookAndFeel didn't work: " + evt, "UI Failure", JOptionPane.INFORMATION_MESSAGE);
        }
        
        screenSize= Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.width-=2;
        screenSize.height -=42;
        
        setSize(screenSize);
        setLocation(1,1);
        setTitle("Free MS SQL 2000 Server DataBase manager. ");
        
        mainwnddesktop= new JDesktopPane();
        mainwnddesktop.setBackground(new Color(212, 208, 200)) ;
        mainscroll= new JScrollPane(mainwnddesktop);
        
        mainwnddesktop.add(jToolBar1);
        
        setContentPane(mainscroll);

        if(connwnd==null){
            connwnd= new ConnectWnd(this);
            mainwnddesktop.add(connwnd);
        }

        if(DBwnd==null){
            DBwnd= new ShowDBFrame(this);
            mainwnddesktop.add(DBwnd);
        }
        
        if(consolewnd==null){
            consolewnd= new ConsoleFrame(this);
            consolewnd.setTitle("SQL Console...");
            mainwnddesktop.add(consolewnd);
        }
        
        if(infownd==null){
            infownd=new InfoWnd(this);
            infownd.setTitle("Info");
            mainwnddesktop.add(infownd);
        }
        
        jBtnConn.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent evt){
              connwnd.setVisible(true); 
           } 
        });
        jBtnDisconn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
              if(SQLConnect!=null){
                setTitle("Free MS SQL 2000 Server DataBase manager.");
                try{
                    SQLConnect.close();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, " " + e, "Connection Closed Failure", JOptionPane.ERROR_MESSAGE);            
                }
        
              SQLConnect = null;
              conMD = null;
              statement = null;
              DBwnd.setVisible(false);
            }    
            }
        });
        jBtnConsol.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
               if(SQLConnect!=null)
                consolewnd.setVisible(true);
            }
        });
        jBtnInfo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
               infownd.setVisible(true);
            }
        });
        jBtnExit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                 if(SQLConnect!=null){
                    setTitle("Free MS SQL 2000 Server DataBase manager.");
                    try{
                      SQLConnect.close();
                    }catch(Exception e){
                      JOptionPane.showMessageDialog(null, " " + e, "Connection Closed Failure", JOptionPane.ERROR_MESSAGE);            
                    }
        
                    SQLConnect = null;
                    conMD = null;
                    statement = null;
                    DBwnd.setVisible(false);
                 } 
                
              System.exit(0);
            }
        });
        connwnd.setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar1 = new javax.swing.JToolBar();
        jBtnConn = new javax.swing.JButton();
        jBtnDisconn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jBtnConsol = new javax.swing.JButton();
        jBtnInfo = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jBtnExit = new javax.swing.JButton();
        jMainMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItmConnect = new javax.swing.JMenuItem();
        jMenuItmDiscon = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItmExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItmConsole = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });

        jBtnConn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/png/20px_network.png")));
        jBtnConn.setToolTipText("Connect to MS SQL Server");
        jToolBar1.add(jBtnConn);

        jBtnDisconn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/png/20px_icon_delete.png")));
        jBtnDisconn.setToolTipText("Disconnect MS SQL 2K server");
        jToolBar1.add(jBtnDisconn);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setMaximumSize(new java.awt.Dimension(20, 50));
        jSeparator2.setMinimumSize(new java.awt.Dimension(20, 50));
        jToolBar1.add(jSeparator2);

        jBtnConsol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/png/20px_icon_quick_text.png")));
        jBtnConsol.setToolTipText("Open T-SQL Console");
        jToolBar1.add(jBtnConsol);

        jBtnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/png/20px_icon_info.png")));
        jBtnInfo.setToolTipText("Info");
        jToolBar1.add(jBtnInfo);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMaximumSize(new java.awt.Dimension(20, 50));
        jSeparator3.setMinimumSize(new java.awt.Dimension(20, 50));
        jToolBar1.add(jSeparator3);

        jBtnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/png/20px_poweroff.png")));
        jToolBar1.add(jBtnExit);

        jMenuFile.setText("File");
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItmConnect.setText("Connect to server...");
        jMenuItmConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItmConnectActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItmConnect);

        jMenuItmDiscon.setText("Disconnect server");
        jMenuItmDiscon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItmDisconActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItmDiscon);

        jMenuFile.add(jSeparator1);

        jMenuItmExit.setText("Exit");
        jMenuItmExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItmExitActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItmExit);

        jMainMenuBar.add(jMenuFile);

        jMenuEdit.setText("Edit");
        jMenuItmConsole.setText("SQL Console...");
        jMenuItmConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItmConsoleActionPerformed(evt);
            }
        });

        jMenuEdit.add(jMenuItmConsole);

        jMainMenuBar.add(jMenuEdit);

        setJMenuBar(jMainMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 477, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(85, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(153, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItmConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItmConsoleActionPerformed
    // TODO add your handling code here:
       if(SQLConnect!=null){
        if(consolewnd==null){
            consolewnd= new ConsoleFrame(this);
            mainwnddesktop.add(consolewnd);
        }
        consolewnd.setVisible(true);
       }
    }//GEN-LAST:event_jMenuItmConsoleActionPerformed

    private void jMenuItmDisconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItmDisconActionPerformed
    // TODO add your handling code here:
       if(SQLConnect!=null){
        setTitle("Free MS SQL 2000 Server DataBase manager.");
        try{
        SQLConnect.close();
        }catch(Exception e){
          JOptionPane.showMessageDialog(null, " " + e, "Connection Closed Failure", JOptionPane.ERROR_MESSAGE);            
        }
        
        SQLConnect = null;
        conMD = null;
        statement = null;
        DBwnd.setVisible(false);
       } 
    }//GEN-LAST:event_jMenuItmDisconActionPerformed

    private void jMenuItmConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItmConnectActionPerformed
// TODO add your handling code here:
    connwnd.setVisible(true);
    }//GEN-LAST:event_jMenuItmConnectActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
// TODO add your handling code here:
    }//GEN-LAST:event_formWindowIconified

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
// TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void jMenuItmExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItmExitActionPerformed
// TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItmExitActionPerformed

    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuFileActionPerformed
    
   
   
    public Dimension screenSize= null;
    protected JDesktopPane mainwnddesktop= null;
    protected JScrollPane mainscroll= null;
    protected ConnectWnd connwnd= null;
    protected ShowDBFrame DBwnd= null;
    protected ConsoleFrame consolewnd= null;
    protected Tableswnd tableswnd= null;
    protected InfoWnd infownd=null;
    protected BacResWnd bacupwnd=null;
    
    public String urlConnect = ""; // ��� ������� � ���� ����������� ��� ����������
    public String login= null;	   			
    public String password = null;
    public String urlConnectFull=null;
    
    public Connection SQLConnect = null;
    public DatabaseMetaData conMD = null;
    public Statement statement = null;
    
    public String ServerName=null;
    public Vector NameDatabase= null;

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnConn;
    private javax.swing.JButton jBtnConsol;
    private javax.swing.JButton jBtnDisconn;
    private javax.swing.JButton jBtnExit;
    private javax.swing.JButton jBtnInfo;
    private javax.swing.JMenuBar jMainMenuBar;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItmConnect;
    private javax.swing.JMenuItem jMenuItmConsole;
    private javax.swing.JMenuItem jMenuItmDiscon;
    private javax.swing.JMenuItem jMenuItmExit;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    
}
