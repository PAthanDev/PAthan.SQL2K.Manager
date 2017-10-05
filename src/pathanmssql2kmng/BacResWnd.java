/*
 * BacResWnd.java
 *
 * Created on 6 Сентябрь 2008 г., 23:44
 */

package pathanmssql2kmng;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.borland.jbcl.layout.*;
import java.util.*;

import java.sql.*;                  
import javax.sql.*;
import com.microsoft.sqlserver.jdbc.*;
//import com.inet.tds.*;
//import com.inet.pool.*;
/**
 *
 * @author  uas
 */
public class BacResWnd extends javax.swing.JInternalFrame {
    
    /** Creates new form BacResWnd */
    public BacResWnd(JFrame frame, boolean backup, String DBName) {
        initComponents();
        jframe= (MainWnd)frame;
        
        //если решили сделать бекап
        if(backup){
            setTitle("SQL Server BackUp....");
            JLabel jLabel1= new JLabel("Database:");
            jcboxDB= new JComboBox();
            jcboxDB.addItem(DBName);
            JLabel jLabel2= new JLabel("Name:");
            jtxtNameBackup= new JTextField();
            JLabel jLabel3= new JLabel("Option:");
            ButtonGroup bgTypeBackUp= new ButtonGroup();
            jrbFullBU= new JRadioButton("Full Database BackUp");
            jrbFullBU.setSelected(true);
            jrbDiffBU= new JRadioButton("Differential Database BackUp");
            bgTypeBackUp.add(jrbFullBU);
            bgTypeBackUp.add(jrbDiffBU);
            JLabel jLabel4= new JLabel("Path to file:");
            jtxtPathFile= new JTextField();
            JButton jbtnBackUp= new JButton("BackUp");
            
            jMainPanel.setLayout(new XYLayout());
            jMainPanel.add(jLabel1, new XYConstraints(10,10,-1,-1));
            jMainPanel.add(jcboxDB, new XYConstraints(70,10,280,-1));
            jMainPanel.add(jLabel2, new XYConstraints(10,40,-1,-1));
            jMainPanel.add(jtxtNameBackup, new XYConstraints(70,40,280,-1));
            jMainPanel.add(jLabel3, new XYConstraints(10,80,-1,-1));
            jMainPanel.add(jrbFullBU, new XYConstraints(10,110,280,-1));
            jMainPanel.add(jrbDiffBU, new XYConstraints(10,140,280,-1));
            jMainPanel.add(jLabel4, new XYConstraints(10,180,-1,-1));
            jMainPanel.add(jtxtPathFile, new XYConstraints(10,210,350,-1));
            jMainPanel.add(jbtnBackUp, new XYConstraints(300,240,-1,-1));
            
            //обработчик кнопки создания бекапа....
            jbtnBackUp.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent evt){

                 try{
                    //Class.forName("com.inet.pool.PoolDriver").newInstance();
                    //DriverManager.setLoginTimeout(1000);
            
                    TwoConnect= DriverManager.getConnection("jdbc:sqlserver://"+jframe.urlConnectFull+":1433;Databasename=master",jframe.login,jframe.password);
                    TwoconMD = TwoConnect.getMetaData();
                    TwoConnect.setCatalog((String) jcboxDB.getSelectedItem() );
                    Twostatement = TwoConnect.createStatement();                    
	            String strSQLBack="BACKUP DATABASE "+jcboxDB.getSelectedItem()+" ";
                    if(jtxtPathFile.getText().equals("")){
                      JOptionPane.showMessageDialog(null, "Не задано имя байла архива", " ", JOptionPane.ERROR_MESSAGE);
                      return;
                    }
                    strSQLBack=strSQLBack+"to disk=N'"+jtxtPathFile.getText()+"' with NOINIT";

                    if(jrbDiffBU.isSelected())
                        strSQLBack=strSQLBack+ ", Differential";

                    if(!jtxtNameBackup.getText().equals(""))
                        strSQLBack=strSQLBack+ ", NAME=N'"+jtxtNameBackup.getText()+"'";

                    Twostatement.execute(strSQLBack);
                    
                 }catch(Exception e){
                    
                    JOptionPane.showMessageDialog(null, " " + e, "Create BackUp Failure", JOptionPane.ERROR_MESSAGE);
                    return;
                 }
                  try{
                   TwoConnect.close();
                   TwoconMD=null;
                   Twostatement=null;
                  }catch(Exception e){}
                setVisible(false);

               } 
            });
            
        //если решилы востановить из бекап-а
        }else{
            
           JLabel jLabel10= new JLabel("Restore as database:"); 
           jcboxRes= new JComboBox();
           jcboxRes.setEditable(true);
           jcboxRes.addItem(DBName);
           JLabel jLabel11= new JLabel("First backup to restore:");
           jcboxFir= new JComboBox();
           jbtnRes= new JButton("Restore");
           jtxtPathRes= new JTextField();
           JLabel jLabel12= new JLabel("Path to File BackUp:");
           
           try{
                TwoConnect= DriverManager.getConnection("jdbc:sqlserver://"+jframe.urlConnectFull+":1433;Databasename=master",jframe.login,jframe.password);
                TwoconMD = TwoConnect.getMetaData();
                TwoConnect.setCatalog("msdb");
                Twostatement = TwoConnect.createStatement();                    
                String sqlGet="select backup_set_id, media_set_id, position, name, backup_finish_date from msdb.dbo.backupset order by backup_set_id desc";

                ResultSet result=Twostatement.executeQuery(sqlGet);
                if(result!=null){
                 String[] headers=new String[result.getMetaData().getColumnCount()];
                
                 for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                         headers[j-1]= result.getMetaData().getColumnName(j);
                
                 vArchive=new Vector();
                  while (result.next()){
                    String[] columns= new String[result.getMetaData().getColumnCount()];
                    for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                     columns[j-1]= result.getString(j);
                    
                   jcboxFir.addItem(columns[0]+" - "+columns[4]+" - "+columns[3]);
                   vArchive.addElement(columns);
                  }
                }
                
           }catch(Exception e){
                    JOptionPane.showMessageDialog(null, " " + e, "Restore BackUp Failure", JOptionPane.ERROR_MESSAGE);
                    return;
           }

            jcboxFir.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent evt){
                   if(evt.getStateChange()==ItemEvent.SELECTED){
                       int indexAll=jcboxFir.getItemCount(); 
                       int index=jcboxFir.getSelectedIndex();
                       String sqlGet1="select back.backup_finish_date as 'Backup Set Date', fam.physical_device_name as 'Restore From', back.name as 'Backup Set Name' from msdb.dbo.backupset as back, msdb.dbo.backupmediafamily as fam ";
                       sqlGet1=sqlGet1+" where back.backup_set_id="+(indexAll-index);
                       sqlGet1=sqlGet1+" and back.media_set_id=fam.media_set_id";
                       try{
                         ResultSet result=Twostatement.executeQuery(sqlGet1);
                         String[] headers=new String[result.getMetaData().getColumnCount()];
                
                         for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                            headers[j-1]= result.getMetaData().getColumnName(j);
                
                         Vector vArchive1=new Vector();
                         while (result.next()){
                           String[] columns= new String[result.getMetaData().getColumnCount()];
                             for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                               columns[j-1]= result.getString(j);
                    
                            vArchive1.addElement(columns);
                          }
                         
                         
                         QueryTableModel tablemodel= new QueryTableModel(headers, vArchive1);

                         jtableinfo= new JTable(tablemodel);
                         //jtableinfo.setAutoResizeMode(JTable.AUTO_RESIZE_ON);
                         jscroll.setViewportView(jtableinfo);                

                       
                       }catch(Exception e){
                           JOptionPane.showMessageDialog(null, " " + e, "Restore BackUp Failure", JOptionPane.ERROR_MESSAGE);
                           return;                           
                       }
                   } 
                }
            });
            jMainPanel.setLayout(new XYLayout());
            jMainPanel.add(jLabel10, new XYConstraints(10,10,-1,-1));
            jMainPanel.add(jcboxRes, new XYConstraints(130,10,230,-1));
            jMainPanel.add(jLabel11, new XYConstraints(10,40,-1,-1));
            jMainPanel.add(jcboxFir, new XYConstraints(130,40,230,-1));
            jMainPanel.add(jscroll, new XYConstraints(10,80,350,100));
            jMainPanel.add(jLabel12, new XYConstraints(10,210,-1,-1));
            
            jMainPanel.add(jtxtPathRes, new XYConstraints(10,240,350,-1));
            jMainPanel.add(jbtnRes, new XYConstraints(300,270,-1,-1));
            
            
            jbtnRes.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    
                 if(jtxtPathRes.getText().equals("")){  
                       
                   try{
                     StringTokenizer strToken=new StringTokenizer((String)jcboxFir.getSelectedItem());
                     String number=strToken.nextToken();
                     
                     String sqlF= "select back.backup_set_id, back.position, fam.physical_device_name from msdb.dbo.backupset as back, msdb.dbo.backupmediafamily as fam ";
                     sqlF=sqlF+" where back.media_set_id=fam.media_set_id";
                     sqlF=sqlF+" and back.backup_set_id="+number;
                     
                     ResultSet result=Twostatement.executeQuery(sqlF);
                     Vector v=new Vector();
                     String[] strnew= new String[result.getMetaData().getColumnCount()];
                     while(result.next()){
                         for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                             strnew[j-1]=result.getString(j);
                         
                         v.addElement(strnew);
                     }
                     
                     System.out.println(v.size());
                     String sqlSel= "RESTORE DATABASE "+ (String)jcboxRes.getSelectedItem();
                     sqlSel=sqlSel+ " FROM DISK=N'"+strnew[2]+"'";
                     
                     if(Integer.parseInt(strnew[1])>1)
                         sqlSel=sqlSel+ "WITH FILE="+strnew[1]+" ,RECOVERY, RESTART";
                     else
                     sqlSel=sqlSel+ " WITH FILE=1, RECOVERY, RESTART";
                     jframe.SQLConnect.close();
                     
                     TwoConnect.setCatalog("master");
                       
                     Twostatement.execute(sqlSel);  
                     
                    TwoConnect.close();   
                    jframe.SQLConnect= DriverManager.getConnection("jdbc:sqlserver://"+jframe.urlConnectFull+":1433;Databasename=master",jframe.login,jframe.password);
                    jframe.conMD = jframe.SQLConnect.getMetaData();

                    setVisible(false);
                    
                   }catch(Exception e){
                    JOptionPane.showMessageDialog(null, " " + e, "Restore BackUp Failure", JOptionPane.ERROR_MESSAGE);
                    return;   
                   }
                 }else{
                   try{
                     String sqlSel= "RESTORE DATABASE "+ (String)jcboxRes.getSelectedItem();
                     sqlSel=sqlSel+ " FROM DISK=N'"+jtxtPathRes.getText()+"'";
                     sqlSel=sqlSel+ " WITH RECOVERY, RESTART";
                     jframe.SQLConnect.close();
                     
                     TwoConnect.setCatalog("master");
                       
                     Twostatement.execute(sqlSel);  
                     
                    TwoConnect.close();   
                    jframe.SQLConnect= DriverManager.getConnection("jdbc:sqlserver://"+jframe.urlConnectFull+":1433;Databasename=master",jframe.login,jframe.password);
                    jframe.conMD = jframe.SQLConnect.getMetaData();

                    setVisible(false);

                       
                   }catch(Exception e){
                    JOptionPane.showMessageDialog(null, " " + e, "Restore BackUp Failure", JOptionPane.ERROR_MESSAGE);
                    return;   
                   }finally{
                       setVisible(false);
                   }
                   
                     
                 }
                     
                 
                }
            });
//            jMainPanel.add(jrbDiffBU, new XYConstraints(10,140,280,-1));

                       int indexAll=jcboxFir.getItemCount(); 
                       int index=jcboxFir.getSelectedIndex();
                       String sqlGet1="select back.backup_finish_date as 'Backup Set Date', fam.physical_device_name as 'Restore From', back.name as 'Backup Set Name' from msdb.dbo.backupset as back, msdb.dbo.backupmediafamily as fam ";
                       sqlGet1=sqlGet1+" where back.backup_set_id="+(indexAll-index);
                       sqlGet1=sqlGet1+" and back.media_set_id=fam.media_set_id";
                       try{
                         ResultSet result=Twostatement.executeQuery(sqlGet1);
                         String[] headers=new String[result.getMetaData().getColumnCount()];
                
                         for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                            headers[j-1]= result.getMetaData().getColumnName(j);
                
                         vArchive1=new Vector();
                         while (result.next()){
                           String[] columns= new String[result.getMetaData().getColumnCount()];
                             for(int j=1; j<=result.getMetaData().getColumnCount(); j++)
                               columns[j-1]= result.getString(j);
                    
                            vArchive1.addElement(columns);
                          }
                         
                         
                         QueryTableModel tablemodel= new QueryTableModel(headers, vArchive1);

                         jtableinfo= new JTable(tablemodel);
                         //jtableinfo.setAutoResizeMode(JTable.AUTO_RESIZE_ON);
                         jscroll.setViewportView(jtableinfo);                

                       
                       }catch(Exception e){
                           
                       }           
           
        }
        
    }
    
  public class QueryTableModel extends AbstractTableModel {
    Vector cache= null;
    String[] headers= null;

    public QueryTableModel(String[] headersName, Vector Tables) {
      cache = Tables;
      headers= new String[headersName.length];
      for(int in=0; in<headersName.length; in++)
          headers[in]= new String(headersName[in]);
    }

    public String getColumnName(int i) {
      return headers[i];
    }

    public int getColumnCount() {
      return headers.length;
    }

    public int getRowCount() {
      return cache.size();
    }

    public Object getValueAt(int row, int col) {
      return (Object)((String[]) cache.elementAt(row))[col];
//      return ((String[]) cache.elementAt(col))[row];
    }

    public void setValueAt(Object aValue, int row, int col){
    };
    
  }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jMainPanel = new javax.swing.JPanel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        org.jdesktop.layout.GroupLayout jMainPanelLayout = new org.jdesktop.layout.GroupLayout(jMainPanel);
        jMainPanel.setLayout(jMainPanelLayout);
        jMainPanelLayout.setHorizontalGroup(
            jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 394, Short.MAX_VALUE)
        );
        jMainPanelLayout.setVerticalGroup(
            jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 298, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jMainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jMainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
// TODO add your handling code here:
                  try{
                   TwoConnect.close();
                   TwoconMD=null;
                   Twostatement=null;
                  }catch(Exception e){}         
    }//GEN-LAST:event_formInternalFrameClosing

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
// TODO add your handling code here:
                  try{
                   TwoConnect.close();
                   TwoconMD=null;
                   Twostatement=null;
                  }catch(Exception e){}        
    }//GEN-LAST:event_formInternalFrameClosed
    
    protected MainWnd jframe=null;
    private Connection TwoConnect = null;
    private DatabaseMetaData TwoconMD = null;
    private Statement Twostatement = null;    
    protected JComboBox jcboxDB= null;
    protected JTextField jtxtNameBackup= null;
    protected JRadioButton jrbFullBU= null;
    protected JRadioButton jrbDiffBU= null;
    protected JTextField jtxtPathFile= null;
    protected JButton jbtnRes= null;
    protected Vector vArchive=null;
    protected Vector vArchive1=null;
    
    protected JComboBox jcboxRes=null;
    protected JComboBox jcboxFir=null;
    protected JScrollPane jscroll=new JScrollPane();
    protected JTable jtableinfo= null;
    protected JTextField jtxtPathRes= null;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jMainPanel;
    // End of variables declaration//GEN-END:variables
    
}
