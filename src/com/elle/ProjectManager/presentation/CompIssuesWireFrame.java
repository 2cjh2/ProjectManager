
package com.elle.ProjectManager.presentation;

import com.elle.ProjectManager.database.DBConnection;
import com.elle.ProjectManager.database.SQL_Commands;
import com.elle.ProjectManager.logic.ReadWriteFiles;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Carlos Igreja
 * @since  2016 January 2
 */
public class CompIssuesWireFrame extends javax.swing.JFrame {

    private final String DB_TABLE_NAME = "issues"; // database table name
    private final String COL_ID = "ID";
    private final String COL_APP = "app";
    private final String COL_TITLE = "title";
    private final String COL_DESCRIPTION = "description";
    private final String COL_PROGRAMMER = "programmer";
    private final String COL_DATE_OPENED = "dateOpened";
    private final String COL_RK = "rk";
    private final String COL_VERSION = "version";
    private final String COL_DATE_CLOSED = "dateClosed";
    
    private final String ALL = "All";         // used for the all option
    private final String DATES_ALL = "All";   // dates combo box selection
    private final String DATES_OPENED = "Opened Dates";   // dates combo box selection
    private final String DATES_CLOSED = "Closed Dates";   // dates combo box selection
    private final String DATES_STILL_OPENED = "Still Open";   // dates combo box selection
    
    private final String DATE_FORMAT = "yyyy-MM-dd";  // format of date
    
    private SQL_Commands sql;
    private ReadWriteFiles rwFiles;
    private JFileChooser fc;
    
    /**
     * Creates new form CompIssuesWireFrame
     */
    public CompIssuesWireFrame() {
        initComponents();
        
        // initialize class variables
        sql = new SQL_Commands(DBConnection.getConnection());
        rwFiles = new ReadWriteFiles();
        
        // initialize all combo boxes
        initComboBoxes();

        textAreaFileInformation.setLineWrap(true);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaFileInformation = new javax.swing.JTextArea();
        labelProgrammer = new javax.swing.JLabel();
        labelApp = new javax.swing.JLabel();
        labelFromDB = new javax.swing.JLabel();
        labelToDB = new javax.swing.JLabel();
        cboxOpenCloseDB = new javax.swing.JComboBox<>();
        cboxProgrammer = new javax.swing.JComboBox<>();
        cboxApp = new javax.swing.JComboBox<>();
        btnWriteToTextFile = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        labelChooseFileLocation = new javax.swing.JLabel();
        labelDisplayPath = new javax.swing.JLabel();
        datePickerFrom = new org.jdesktop.swingx.JXDatePicker();
        datePickerTo = new org.jdesktop.swingx.JXDatePicker();
        btnReadFromTextFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        textAreaFileInformation.setColumns(20);
        textAreaFileInformation.setRows(5);
        textAreaFileInformation.setText("\nID  Title   Description     \n------------------------\nnotes here\n\nSays you want dates?\nshould dates be included somewhere.\nasdf");
        jScrollPane1.setViewportView(textAreaFileInformation);

        labelProgrammer.setText("Programmer:");

        labelApp.setText("App:");

        labelFromDB.setText("From:");

        labelToDB.setText("To:");

        cboxOpenCloseDB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboxOpenCloseDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxOpenCloseDBActionPerformed(evt);
            }
        });

        cboxProgrammer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboxProgrammer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxProgrammerActionPerformed(evt);
            }
        });

        cboxApp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnWriteToTextFile.setText("Write to Text File");
        btnWriteToTextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWriteToTextFileActionPerformed(evt);
            }
        });

        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        labelChooseFileLocation.setText("Choose file location:");

        btnReadFromTextFile.setText("Read from Text File");
        btnReadFromTextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReadFromTextFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelFromDB)
                            .addComponent(labelToDB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboxOpenCloseDB, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(datePickerFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(datePickerTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(labelChooseFileLocation)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(labelProgrammer)
                                    .addGap(18, 18, 18)
                                    .addComponent(cboxProgrammer, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(labelApp)
                                .addGap(18, 18, 18)
                                .addComponent(cboxApp, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnReadFromTextFile, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(btnWriteToTextFile, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelDisplayPath, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboxOpenCloseDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelFromDB)
                            .addComponent(datePickerFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelProgrammer)
                            .addComponent(cboxProgrammer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelApp)
                            .addComponent(cboxApp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelChooseFileLocation)
                            .addComponent(btnBrowse)
                            .addComponent(labelToDB)
                            .addComponent(datePickerTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDisplayPath, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnWriteToTextFile)
                    .addComponent(btnReadFromTextFile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        // JFileChooser for choosing a file or directory
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setDialogTitle("File Chooser");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.showOpenDialog(this);
        String path = fc.getSelectedFile().getAbsolutePath();
        labelDisplayPath.setText(path);
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void cboxProgrammerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxProgrammerActionPerformed
        
    }//GEN-LAST:event_cboxProgrammerActionPerformed

    private void btnWriteToTextFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWriteToTextFileActionPerformed
        
        // file must be selected or null pointer is thrown
        try{
            // get all the information for the query
            String file = fc.getSelectedFile().getAbsolutePath(); // check first for null pointer
            String useDates = cboxOpenCloseDB.getSelectedItem().toString();
            Date date = new Date();
            if(datePickerTo.getDate()==null){
               datePickerTo.setDate(date); // set today's date
            }
            Date dateTo = datePickerTo.getDate();
            if(datePickerFrom.getDate()==null){
               date = new Date(-1900,0,1);
               datePickerFrom.setDate(date);
            }
            Date dateFrom = datePickerFrom.getDate();
            String app = cboxApp.getSelectedItem().toString();
            String programmer = cboxProgrammer.getSelectedItem().toString();
            // write the query
            String query = "SELECT * FROM " + DB_TABLE_NAME + " ";
            ArrayList<String> queries = new ArrayList<>();
            queries.add(getAppQuery(app));
            queries.add(getProgrammerQuery(programmer));
            queries.add(getDatesQuery(useDates,dateFrom,dateTo));
            queries.add("ORDER BY " + COL_RK + " ASC");
            boolean needsWhereClause = true;
            for(int i = 0; i < queries.size(); i++){
                if(!queries.get(i).equals("")){
                    if(i == queries.size() - 1){ // this is the sorting query
                        query += queries.get(i) + " ";
                    }else if(needsWhereClause){
                        query += "WHERE " + queries.get(i) + " ";
                        needsWhereClause = false;
                    }else{
                        query += "AND " + queries.get(i) + " ";
                    }
                }
            }
            // make sure connection is open
            DBConnection.close();
            DBConnection.open();
            // execute query and return data to hash map
            sql = new SQL_Commands(DBConnection.getConnection());
            HashMap<String,ArrayList<Object>> map;
            System.out.println(query); // for debugging
            map = sql.getTableData(sql.executeQuery(query));
            // write data to file
            if(writeToFile(file,map)){
                messageBox("Write finished succecssfully.");
            }
            
        }
        catch(NullPointerException e){
            messageBox("Please select a file.");
        }
    }//GEN-LAST:event_btnWriteToTextFileActionPerformed

    private void cboxOpenCloseDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOpenCloseDBActionPerformed
        if(cboxOpenCloseDB.getSelectedItem() != null
                && (cboxOpenCloseDB.getSelectedItem().toString().equals(DATES_ALL)
                || cboxOpenCloseDB.getSelectedItem().toString().equals(DATES_STILL_OPENED))){
            datePickerFrom.setEnabled(false);
            datePickerTo.setEnabled(false);
        }else{
            datePickerFrom.setEnabled(true);
            datePickerTo.setEnabled(true);
        }
    }//GEN-LAST:event_cboxOpenCloseDBActionPerformed

    private void btnReadFromTextFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReadFromTextFileActionPerformed
        
        try{
            String file = fc.getSelectedFile().getAbsolutePath(); // check first for null pointer
            BufferedReader reader = rwFiles.getReader(file);
            textAreaFileInformation.setText("");
            String line = reader.readLine();
            while(line != null){
                textAreaFileInformation.append(line + "\n");
                line = reader.readLine();
            }
        }catch(NullPointerException e){
            messageBox("Please select a file.");
        } catch (IOException ex) {
            messageBox(ex.getMessage());
        }
        
    }//GEN-LAST:event_btnReadFromTextFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CompIssuesWireFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CompIssuesWireFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CompIssuesWireFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CompIssuesWireFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CompIssuesWireFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnReadFromTextFile;
    private javax.swing.JButton btnWriteToTextFile;
    private javax.swing.JComboBox<String> cboxApp;
    private javax.swing.JComboBox<String> cboxOpenCloseDB;
    private javax.swing.JComboBox<String> cboxProgrammer;
    private org.jdesktop.swingx.JXDatePicker datePickerFrom;
    private org.jdesktop.swingx.JXDatePicker datePickerTo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelApp;
    private javax.swing.JLabel labelChooseFileLocation;
    private javax.swing.JLabel labelDisplayPath;
    private javax.swing.JLabel labelFromDB;
    private javax.swing.JLabel labelProgrammer;
    private javax.swing.JLabel labelToDB;
    private javax.swing.JTextArea textAreaFileInformation;
    // End of variables declaration//GEN-END:variables

    private void initComboBoxes() {
        
        HashMap<String,ArrayList<Object>> map;
        
        // make sure the connection is open
        DBConnection.close();     // close old connection
        DBConnection.open();      // open new connection
        sql.setConnection(DBConnection.getConnection());    // set connection
        sql.createStatement(DBConnection.getConnection());  // create statement
        
        // Opened or Closed Dates
        cboxOpenCloseDB.removeAllItems();
        cboxOpenCloseDB.addItem(DATES_STILL_OPENED);
        cboxOpenCloseDB.addItem(DATES_CLOSED);
        cboxOpenCloseDB.addItem(DATES_OPENED);
        cboxOpenCloseDB.addItem(DATES_ALL);
        datePickerFrom.setEnabled(false);
        datePickerTo.setEnabled(false);
        Date date = new Date();
        datePickerTo.setDate(date);
        
        // App combobox
        map = sql.getDistinctColumnValues(DB_TABLE_NAME, COL_APP);
        cboxApp.removeAllItems();
        cboxApp.addItem(ALL);
        for(int i = 0; i < map.get(COL_APP).size(); i++){
            cboxApp.addItem(map.get(COL_APP).get(i).toString());
        }
        
        // Programmer combobox
        map = sql.getDistinctColumnValues(DB_TABLE_NAME, COL_PROGRAMMER);
        cboxProgrammer.removeAllItems();
        cboxProgrammer.addItem(ALL);
        for(int i = 0; i < map.get(COL_PROGRAMMER).size(); i++){
            cboxProgrammer.addItem(map.get(COL_PROGRAMMER).get(i).toString());
        }
    }

    private String getAppQuery(String app) {
        if(!app.equals(ALL)){
            return COL_APP + " = '" + app + "' ";
        }
        return "";
    }

    private String getProgrammerQuery(String programmer) {
        if(!programmer.equals(ALL)){
            return COL_PROGRAMMER + " = '" + programmer + "' ";
        }
        return "";
    }

    private String getDatesQuery(String useDates, Date dateFrom, Date dateTo) {
        if(!useDates.equals(DATES_ALL)){
            if(!useDates.equals(DATES_STILL_OPENED)){
                //!Validator.isValidDate("yyyy-MM-dd", cellValue.toString())
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                String from = sdf.format(dateFrom);
                String to = sdf.format(dateTo);
                if(useDates.equals(DATES_OPENED))
                    return COL_DATE_OPENED + " BETWEEN '" + from + "' AND '" + to + "' ";
                else 
                    return COL_DATE_CLOSED + " BETWEEN '" + from + "' AND '" + to + "' ";
            }
            return COL_DATE_CLOSED + " IS NULL ";
        }
        return "";
    }

    private boolean writeToFile(String file, HashMap<String, ArrayList<Object>> map) {
        PrintWriter writer = rwFiles.getWriter(file); // writer to write to file
        // get data for each record
        int records = map.get(COL_APP).size(); // had to hard code - used app
        for(int i = 0; i < records; i++){
            String app = COL_APP + ": ";
            if(map.get(COL_APP).get(i) != null)
                app += map.get(COL_APP).get(i).toString() + " ";
            String title = COL_TITLE + ": ";
            if(map.get(COL_TITLE).get(i) != null)
                title += map.get(COL_TITLE).get(i).toString() + " ";
            String rk = COL_RK + ": ";
            if(map.get(COL_RK).get(i) != null)
                rk += map.get(COL_RK).get(i).toString() + " ";
            String programmer = COL_PROGRAMMER + ": ";
            if(map.get(COL_PROGRAMMER).get(i) != null)
                programmer += map.get(COL_PROGRAMMER).get(i).toString() + " ";
            String opened = COL_DATE_OPENED + ": ";
            if(map.get(COL_DATE_OPENED).get(i) != null)
                opened += map.get(COL_DATE_OPENED).get(i).toString() + " ";
            String closed = COL_DATE_CLOSED + ": ";
            if(map.get(COL_DATE_CLOSED).get(i) != null)
                closed += map.get(COL_DATE_CLOSED).get(i).toString() + " ";
            String version = COL_VERSION + ": ";
            if(map.get(COL_VERSION).get(i) != null)
                version += map.get(COL_VERSION).get(i).toString() + " ";
            String description = COL_DESCRIPTION + ": ";
            if(map.get(COL_DESCRIPTION).get(i) != null)
                description += map.get(COL_DESCRIPTION).get(i).toString() + " ";
            
            /**
             * print to file
             * FORMAT
             * app title rk programmer opened closed version
             * ----------------------------------------------
             * descriptions
             */
            writer.println(rk + app + title + programmer);
            writer.println("--------------------------------------------------------");
            // break up the description from one long line to ten words per line
            String[] desc = description.split(" ");
            description = "";
            for(int words = 0; words < desc.length; words++){
                description += desc[words] + " ";
                if(words !=0 && words%10 == 0){
                    writer.println(description);
                    description = "";
                }
            }
            writer.println(); // a line break between records
        }
        writer.flush();
        writer.close();
        return true;
    }

    private void messageBox(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
