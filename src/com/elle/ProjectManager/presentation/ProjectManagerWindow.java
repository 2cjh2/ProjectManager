package com.elle.ProjectManager.presentation;

import com.elle.ProjectManager.admissions.Authorization;
import com.elle.ProjectManager.controller.PMDataManager;
import com.elle.ProjectManager.dao.IssueDAO;
import com.elle.ProjectManager.database.DBConnection;
import com.elle.ProjectManager.entities.Issue;
import com.elle.ProjectManager.entities.IssueFile;
import com.elle.ProjectManager.logic.*;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * ProjectManagerWindow
 *
 * @author Xiaoqian FU
 * @since Oct 29, 2015
 * @version 0.9.3
 */
public class ProjectManagerWindow extends JFrame implements ITableConstants {

    public static String creationDate;  // set automatically from manifest
    public static String version;       // set automatically from manifest
    
    //Data center
    PMDataManager dataManager;
    
    // attributes
    private boolean online;
    
    private Map<Integer, Tab> tabs; // stores individual tabName information
    private Map<String, Map<Integer, ArrayList<Object>>> comboBoxForSearchDropDown;
    private static Statement statement;
    private String database;


    // components
    private static ProjectManagerWindow instance;
    private IssueWindow addIssueWindow;
    private LogWindow logWindow;
    private LoginWindow loginWindow;
    private BatchEditWindow batchEditWindow;
    private EditDatabaseWindow editDatabaseWindow;
    private ShortCutSetting ShortCut;
    private ConsistencyOfTableColumnName ColumnNameConsistency;
    private SqlOutputWindow sqlOutputWindow;
    private ReconcileWindow reconcileWindow;

    private Map<String, IssueWindow> openingIssuesList;

    // colors - Edit mode labels
    private Color editModeDefaultTextColor;
    private Color editModeActiveTextColor;

    private String editingTabName; // stores the name of the tab that is editing

    // Misc 
    private boolean addIssueWindowShow;
    private boolean isBatchEditWindowShow;
    private boolean comboBoxStartToSearch;

    private boolean popupWindowShowInPM;
    private boolean reconcileWindowShow;

    // create a jlabel to show the database used
    private JLabel databaseLabel;
    private String currentTabName;
    private String userName;
    private String searchValue = null;
    private String searchColName = "programmer";
//    private ArrayList<Integer> idNumOfOpenningIssues;
    private ArrayList<String> programmersActiveForSearching;
    
    
    
    //offline data manager
    public OfflineIssueManager offlineIssueMgr;
    
    private final String HYPHENS = "--------------------------------------------------------"
                                 + "--------------------------------------------------------"
                                 + "------------";

    private IdColumnRenderer idRender;
    /**
     * CONSTRUCTOR
     */
    public ProjectManagerWindow(String userName, boolean mode) {
        
        dataManager = new PMDataManager(mode);

        /**
         * Note: initComponents() executes the tabpaneChanged method. Thus, some
         * things need to be before or after the initComponents();
         */
        // the statement is used for sql statements with the database connection
        // the statement is created in LoginWindow and passed to Analyster.
        
        
        instance = this;                         // this is used to call this instance of PM

        this.userName = userName;
        online = mode;
        offlineIssueMgr = new OfflineIssueManager(userName);
        
        
        initComponents(); // generated code
        
        // initialize tabs 
        // set up table, renderer, filter, columnpopupmenu
        // initialize and save the buttons state
        tabs = new HashMap();
        for(int i = 0; i < tabbedPanel.getTabCount(); i++) {
            JScrollPane scrollPane = (JScrollPane)tabbedPanel.getComponent(i);
            JViewport viewport = scrollPane.getViewport();
            JTable table = (JTable)viewport.getView();
            Tab tab = new Tab(table);
            tabs.put(i, tab);
        }
        
        //set up orange dot
        updateTabOrangeDot();
        
        //initialize tab related components
        //including button state, recordsLabel, comboBoxSearchField, addIssue button text ,etc
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex()); 
        changeTabbedPanelState(currentTab);
        
        jPanelSQL.setVisible(false);
        comboBoxStartToSearch = false;
        
        // initialize comboBoxForSeachDropDownList
        comboBoxForSearchDropDown = new HashMap();
        programmersActiveForSearching = new ArrayList<String>();

       
        ///set the offline status
        if (online) {
            status.setText("Online");
            status.setForeground(new Color(0, 153, 0));
        }
        else {
            status.setText("Offline");
            status.setForeground(Color.RED);
            menuItemSyncLocalData.setEnabled(false);
            menuItemReconcileConflict.setEnabled(false);
        }
        
        
        //set the offline mode 
        if (!online) menuItemOfflineMode.setEnabled(false);
        menuItemOfflineMode.setSelected(!online);

        // initialize the colors for the edit mode text
        editModeActiveTextColor = new Color(44, 122, 22); //dark green
        editModeDefaultTextColor = labelEditMode.getForeground();

     
        informationLabel.setText("");

        // this sets the KeyboardFocusManger
        setKeyboardFocusManager(this);
        textComponentShortCutSetting();
  
        boolean ifDeleteRecords = false;


        //start to set renderers for id columns, it uses singleton  - by Yi
        
//        idRender = IdColumnRenderer.getInstance(offlineIssueMgr);
//        tabs.get(tableNames[0]).getTable().getColumnModel().getColumn(0).setCellRenderer(idRender);
//        tabs.get(tableNames[1]).getTable().getColumnModel().getColumn(0).setCellRenderer(idRender);
//        tabs.get(tableNames[2]).getTable().getColumnModel().getColumn(0).setCellRenderer(idRender);
//        tabs.get(tableNames[3]).getTable().getColumnModel().getColumn(0).setCellRenderer(idRender);
//        tabs.get(tableNames[4]).getTable().getColumnModel().getColumn(0).setCellRenderer(idRender);
        

        // initial openedIssuesList to manager all the openning issues
        openingIssuesList = new HashMap<String, IssueWindow>();

        this.comboBoxValue.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                comboBoxForSearchEditorMouseClicked(e);
            }
        });
        // set title of window to Project Manager
        this.setTitle("Project Manager");

        // set the size for project manager
        this.setPreferredSize(new Dimension(1014, 550));
        this.setMinimumSize(new Dimension(1000, 550));
        
        
        this.pack();

        // authorize user for this component
        Authorization.authorize(this);
 
        
        //if there are conflicted issues and is in online mode
        //open reconcile window in the dispatch thread , thus not delaying the main window
        if (online && offlineIssueMgr.getConflictIssues().size() > 0)
             SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    try {
                        reconcileWindow = new ReconcileWindow();
                    } catch (IOException ex) {
                        Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                reconcileWindow.setVisible(true);
                reconcileWindowShow = true;
                
                }
            });
        
    }

    /*
     * This is to make the scroll bar always scrolling down.
     */
    private void scrollDown(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TableFilterBtnGroup = new javax.swing.ButtonGroup();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jPanel5 = new javax.swing.JPanel();
        tabbedPanel = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        PMTable = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        ELLEGUITable = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        AnalysterTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        OtherTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        referenceTable = new javax.swing.JTable();
        jPanelEdit = new javax.swing.JPanel();
        btnBatchEdit = new javax.swing.JButton();
        btnAddIssue = new javax.swing.JButton();
        btnUploadChanges = new javax.swing.JButton();
        labelEditModeState = new javax.swing.JLabel();
        labelEditMode = new javax.swing.JLabel();
        btnRevertChanges = new javax.swing.JButton();
        informationLabel = new javax.swing.JLabel();
        jPanelSQL = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaSQL = new javax.swing.JTextArea();
        btnEnterSQL = new javax.swing.JButton();
        btnCancelSQL = new javax.swing.JButton();
        btnCloseSQL = new javax.swing.JButton();
        addPanel_control = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        status = new javax.swing.JLabel();
        labelRecords = new javax.swing.JLabel();
        labelTimeLastUpdate = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();
        btnSearch = new javax.swing.JButton();
        comboBoxField = new javax.swing.JComboBox();
        btnClearAllFilter = new javax.swing.JButton();
        searchInformationLabel = new javax.swing.JLabel();
        comboBoxValue = new javax.swing.JComboBox();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemVersion = new javax.swing.JMenuItem();
        menuSelectConn = new javax.swing.JMenu();
        menuItemAWSAssign = new javax.swing.JMenuItem();
        menuPrint = new javax.swing.JMenu();
        menuItemPrintGUI = new javax.swing.JMenuItem();
        menuItemPrintDisplay = new javax.swing.JMenuItem();
        menuItemSaveFile = new javax.swing.JMenuItem();
        menuItemLogOff = new javax.swing.JMenuItem();
        menuItemOfflineMode = new javax.swing.JCheckBoxMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemManageDBs = new javax.swing.JMenuItem();
        menuItemDeleteRecord = new javax.swing.JMenuItem();
        menuItemArchiveRecord = new javax.swing.JMenuItem();
        menuItemActivateRecord = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuItemLogChkBx = new javax.swing.JCheckBoxMenuItem();
        menuItemSQLCmdChkBx = new javax.swing.JCheckBoxMenuItem();
        menuitemViewOneIssue = new javax.swing.JMenuItem();
        menuItemViewSplashScreen = new javax.swing.JMenuItem();
        menuTools = new javax.swing.JMenu();
        menuItemReloadSelectedData = new javax.swing.JMenuItem();
        menuItemReloadData = new javax.swing.JMenuItem();
        menuItemReloadAllData = new javax.swing.JMenuItem();
        menuItemTurnEditModeOff = new javax.swing.JMenuItem();
        menuItemMoveSeletedRowsToEnd = new javax.swing.JMenuItem();
        menuItemCompIssues = new javax.swing.JMenuItem();
        menuItemBackup = new javax.swing.JMenuItem();
        menuItemSyncLocalData = new javax.swing.JMenuItem();
        menuItemReconcileConflict = new javax.swing.JMenuItem();
        menuItemLoadDataFromTXT = new javax.swing.JMenuItem();
        menuItemExportIssueToReference = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemRepBugSugg = new javax.swing.JMenuItem();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabbedPanel.setAlignmentX(0.0F);
        tabbedPanel.setAlignmentY(0.0F);
        tabbedPanel.setName("Analyster"); // NOI18N
        tabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPanelStateChanged(evt);
            }
        });

        PMTable.setAutoCreateRowSorter(true);
        PMTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "app", "title", "description", "programmer", "dateOpened", "rk", "version", "dateClosed", "issueType", "submitter", "locked", "lastmodTime"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, false, true, true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        PMTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        PMTable.setMinimumSize(new java.awt.Dimension(10, 240));
        PMTable.setName("PM"); // NOI18N
        jScrollPane1.setViewportView(PMTable);

        tabbedPanel.addTab("PM", jScrollPane1);

        ELLEGUITable.setAutoCreateRowSorter(true);
        ELLEGUITable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "app", "title", "description", "programmer", "dateOpened", "rk", "version", "dateClosed", "issueType", "submitter", "locked", "lastmodTime"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, false, true, true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ELLEGUITable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        ELLEGUITable.setMinimumSize(new java.awt.Dimension(10, 240));
        ELLEGUITable.setName("ELLEGUI"); // NOI18N
        jScrollPane6.setViewportView(ELLEGUITable);

        tabbedPanel.addTab("ELLEGUI", jScrollPane6);

        AnalysterTable.setAutoCreateRowSorter(true);
        AnalysterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "app", "title", "description", "programmer", "dateOpened", "rk", "version", "dateClosed", "issueType", "submitter", "locked", "lastmodtime"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        AnalysterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        AnalysterTable.setMinimumSize(new java.awt.Dimension(10, 240));
        AnalysterTable.setName("Analyster"); // NOI18N
        jScrollPane7.setViewportView(AnalysterTable);

        tabbedPanel.addTab("Analyster", jScrollPane7);

        OtherTable.setAutoCreateRowSorter(true);
        OtherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "app", "title", "description", "programmer", "dateOpened", "rk", "version", "dateClosed", "issueType", "submitter", "locked", "lastmodTime"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OtherTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        OtherTable.setMinimumSize(new java.awt.Dimension(10, 240));
        OtherTable.setName("Other"); // NOI18N
        jScrollPane5.setViewportView(OtherTable);

        tabbedPanel.addTab("Other", jScrollPane5);

        referenceTable.setAutoCreateRowSorter(true);
        referenceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "app", "title", "description", "programmer", "dateOpened", "rk", "version", "dateClosed", "submitter", "issueType", "locked", "lastmodTime"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        referenceTable.setName("References"); // NOI18N
        jScrollPane3.setViewportView(referenceTable);

        tabbedPanel.addTab("References", jScrollPane3);

        jPanelEdit.setPreferredSize(new java.awt.Dimension(636, 180));

        btnBatchEdit.setText("Batch Edit");
        btnBatchEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatchEditActionPerformed(evt);
            }
        });

        btnAddIssue.setText("Add Issue");
        btnAddIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddIssueActionPerformed(evt);
            }
        });

        btnUploadChanges.setText("Upload Changes");
        btnUploadChanges.setMaximumSize(new java.awt.Dimension(95, 30));
        btnUploadChanges.setMinimumSize(new java.awt.Dimension(95, 30));
        btnUploadChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadChangesActionPerformed(evt);
            }
        });

        labelEditModeState.setText("OFF");

        labelEditMode.setText("Edit Mode:");

        btnRevertChanges.setText("Revert Changes");
        btnRevertChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevertChangesActionPerformed(evt);
            }
        });

        informationLabel.setText("jLabel2");

        javax.swing.GroupLayout jPanelEditLayout = new javax.swing.GroupLayout(jPanelEdit);
        jPanelEdit.setLayout(jPanelEditLayout);
        jPanelEditLayout.setHorizontalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEditMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelEditModeState)
                .addGap(233, 233, 233)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(informationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelEditLayout.createSequentialGroup()
                        .addComponent(btnUploadChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRevertChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
                        .addComponent(btnAddIssue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatchEdit)
                        .addGap(26, 26, 26))))
        );
        jPanelEditLayout.setVerticalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelEditMode)
                    .addComponent(labelEditModeState)
                    .addComponent(btnBatchEdit)
                    .addComponent(btnAddIssue)
                    .addComponent(btnRevertChanges)
                    .addComponent(btnUploadChanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(informationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane2.setBorder(null);

        jTextAreaSQL.setBackground(new java.awt.Color(0, 153, 102));
        jTextAreaSQL.setColumns(20);
        jTextAreaSQL.setLineWrap(true);
        jTextAreaSQL.setRows(5);
        jTextAreaSQL.setText("Please input an SQL statement:\\n>>");
        ((AbstractDocument) jTextAreaSQL.getDocument())
        .setDocumentFilter(new CreateDocumentFilter(33));
        jTextAreaSQL.setWrapStyleWord(true);
        jTextAreaSQL.setMaximumSize(new java.awt.Dimension(1590, 200));
        jTextAreaSQL.setMinimumSize(new java.awt.Dimension(1590, 200));
        jScrollPane2.setViewportView(jTextAreaSQL);

        btnEnterSQL.setText("Enter");
        btnEnterSQL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnterSQLActionPerformed(evt);
            }
        });

        btnCancelSQL.setText("Cancel");
        btnCancelSQL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSQLActionPerformed(evt);
            }
        });

        btnCloseSQL.setText("Close");
        btnCloseSQL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseSQLActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSQLLayout = new javax.swing.GroupLayout(jPanelSQL);
        jPanelSQL.setLayout(jPanelSQLLayout);
        jPanelSQLLayout.setHorizontalGroup(
            jPanelSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSQLLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanelSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancelSQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEnterSQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseSQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanelSQLLayout.setVerticalGroup(
            jPanelSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSQLLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(btnEnterSQL)
                .addGap(4, 4, 4)
                .addComponent(btnCancelSQL)
                .addGap(4, 4, 4)
                .addComponent(btnCloseSQL)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelSQLLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE)
            .addComponent(jPanelSQL, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabbedPanel)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(tabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanelSQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        tabbedPanel.getAccessibleContext().setAccessibleName("Reports");
        tabbedPanel.getAccessibleContext().setAccessibleParent(tabbedPanel);

        addPanel_control.setPreferredSize(new java.awt.Dimension(1045, 120));

        status.setText("status");

        labelRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelRecords.setText("labelRecords");
        labelRecords.setPreferredSize(new java.awt.Dimension(61, 20));

        labelTimeLastUpdate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelTimeLastUpdate.setText("Last updated: ");
        labelTimeLastUpdate.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(status)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(labelTimeLastUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                        .addComponent(labelRecords, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(status)
                .addGap(0, 0, 0)
                .addComponent(labelTimeLastUpdate)
                .addGap(0, 0, 0)
                .addComponent(labelRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        searchPanel.setPreferredSize(new java.awt.Dimension(584, 76));

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        comboBoxField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "programmer", "title", "description", "dateOpened", "dateClosed", "rk", "version" }));
        comboBoxField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxFieldActionPerformed(evt);
            }
        });

        btnClearAllFilter.setText("Clear All Filters");
        btnClearAllFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllFilterActionPerformed(evt);
            }
        });

        comboBoxValue.setEditable(true);
        comboBoxValue.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        comboBoxValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxValueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnClearAllFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(comboBoxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addGap(0, 197, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchInformationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClearAllFilter)
                    .addComponent(comboBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(comboBoxValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(searchInformationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout addPanel_controlLayout = new javax.swing.GroupLayout(addPanel_control);
        addPanel_control.setLayout(addPanel_controlLayout);
        addPanel_controlLayout.setHorizontalGroup(
            addPanel_controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanel_controlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        addPanel_controlLayout.setVerticalGroup(
            addPanel_controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 63, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        menuFile.setText("File");

        menuItemVersion.setText("Version");
        menuItemVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemVersionActionPerformed(evt);
            }
        });
        menuFile.add(menuItemVersion);

        menuSelectConn.setText("Select Connection");
        menuSelectConn.setEnabled(false);

        menuItemAWSAssign.setText("AWS Assignments");
        menuItemAWSAssign.setEnabled(false);
        menuItemAWSAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAWSAssignActionPerformed(evt);
            }
        });
        menuSelectConn.add(menuItemAWSAssign);

        menuFile.add(menuSelectConn);

        menuPrint.setText("Print");

        menuItemPrintGUI.setText("Print GUI");
        menuPrint.add(menuItemPrintGUI);

        menuItemPrintDisplay.setText("Print Display Window");
        menuPrint.add(menuItemPrintDisplay);

        menuFile.add(menuPrint);

        menuItemSaveFile.setText("Save File");
        menuItemSaveFile.setEnabled(false);
        menuFile.add(menuItemSaveFile);

        menuItemLogOff.setText("Log out");
        menuItemLogOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLogOffActionPerformed(evt);
            }
        });
        menuFile.add(menuItemLogOff);

        menuItemOfflineMode.setText("Offline mode");
        menuItemOfflineMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOfflineModeActionPerformed(evt);
            }
        });
        menuFile.add(menuItemOfflineMode);

        menuBar.add(menuFile);

        menuEdit.setText("Edit");

        menuItemManageDBs.setText("Manage databases");
        menuItemManageDBs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemManageDBsActionPerformed(evt);
            }
        });
        menuEdit.add(menuItemManageDBs);

        menuItemDeleteRecord.setText("Delete Record");
        menuItemDeleteRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteRecordActionPerformed(evt);
            }
        });
        menuEdit.add(menuItemDeleteRecord);

        menuItemArchiveRecord.setText("Archive Record");
        menuItemArchiveRecord.setEnabled(false);
        menuItemArchiveRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemArchiveRecordActionPerformed(evt);
            }
        });
        menuEdit.add(menuItemArchiveRecord);

        menuItemActivateRecord.setText("Activate Record");
        menuItemActivateRecord.setEnabled(false);
        menuItemActivateRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemActivateRecordActionPerformed(evt);
            }
        });
        menuEdit.add(menuItemActivateRecord);

        menuBar.add(menuEdit);

        menuView.setText("View");

        menuItemLogChkBx.setText("Log");
        menuItemLogChkBx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLogChkBxActionPerformed(evt);
            }
        });
        menuView.add(menuItemLogChkBx);

        menuItemSQLCmdChkBx.setText("SQL Command");
        menuItemSQLCmdChkBx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSQLCmdChkBxActionPerformed(evt);
            }
        });
        menuView.add(menuItemSQLCmdChkBx);

        menuitemViewOneIssue.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_MASK));
        menuitemViewOneIssue.setText("View Selected Issue");
        menuitemViewOneIssue.setToolTipText("");
        menuitemViewOneIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuitemViewOneIssueActionPerformed(evt);
            }
        });
        menuView.add(menuitemViewOneIssue);

        menuItemViewSplashScreen.setText("View Splash Screen");
        menuItemViewSplashScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemViewSplashScreenActionPerformed(evt);
            }
        });
        menuView.add(menuItemViewSplashScreen);

        menuBar.add(menuView);

        menuTools.setText("Tools");

        menuItemReloadSelectedData.setText("Reload Selected data");
        menuItemReloadSelectedData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemReloadSelectedDataActionPerformed(evt);
            }
        });
        menuTools.add(menuItemReloadSelectedData);

        menuItemReloadData.setText("Reload Tab data");
        menuItemReloadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemReloadDataActionPerformed(evt);
            }
        });
        menuTools.add(menuItemReloadData);

        menuItemReloadAllData.setText("Reload ALL data");
        menuItemReloadAllData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemReloadAllDataActionPerformed(evt);
            }
        });
        menuTools.add(menuItemReloadAllData);

        menuItemTurnEditModeOff.setText("Turn Edit Mode OFF");
        menuItemTurnEditModeOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTurnEditModeOffActionPerformed(evt);
            }
        });
        menuTools.add(menuItemTurnEditModeOff);

        menuItemMoveSeletedRowsToEnd.setText("Move Selected Rows To End");
        menuItemMoveSeletedRowsToEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMoveSeletedRowsToEndActionPerformed(evt);
            }
        });
        menuTools.add(menuItemMoveSeletedRowsToEnd);

        menuItemCompIssues.setText("Compile Issue List");
        menuItemCompIssues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCompIssuesActionPerformed(evt);
            }
        });
        menuTools.add(menuItemCompIssues);

        menuItemBackup.setText("Backup Tables");
        menuItemBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemBackupActionPerformed(evt);
            }
        });
        menuTools.add(menuItemBackup);

        menuItemSyncLocalData.setText("Sync Local Data");
        menuItemSyncLocalData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSyncLocalDataActionPerformed(evt);
            }
        });
        menuTools.add(menuItemSyncLocalData);

        menuItemReconcileConflict.setText("Reconcile Conflict Issue");
        menuItemReconcileConflict.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemReconcileConflictActionPerformed(evt);
            }
        });
        menuTools.add(menuItemReconcileConflict);

        menuItemLoadDataFromTXT.setText("Import Data From TXT File");
        menuItemLoadDataFromTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLoadDataFromTXTActionPerformed(evt);
            }
        });
        menuTools.add(menuItemLoadDataFromTXT);

        menuItemExportIssueToReference.setText("Export Issue To Reference");
        menuItemExportIssueToReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExportIssueToReferenceActionPerformed(evt);
            }
        });
        menuTools.add(menuItemExportIssueToReference);

        menuBar.add(menuTools);

        menuHelp.setText("Help");
        menuHelp.setEnabled(false);

        menuItemRepBugSugg.setText("Report a bug/suggestion");
        menuItemRepBugSugg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRepBugSuggActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemRepBugSugg);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(addPanel_control, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(addPanel_control, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemVersionActionPerformed

        JOptionPane.showMessageDialog(this, "Creation Date: "
                + creationDate + "\n"
                + "Version: " + version);
    }//GEN-LAST:event_menuItemVersionActionPerformed


    private void updateComboList(String colName, Tab tab) {
        //create a combo box model
        DefaultComboBoxModel comboBoxSearchModel = new DefaultComboBoxModel();
        comboBoxValue.setModel(comboBoxSearchModel);
        
       
        Map comboBoxForSearchValue = tab.loadingDropdownList();

        JTable table = tab.getTable();

        for (int col = 0; col < table.getColumnCount(); col++) {

            if (table.getColumnName(col).equalsIgnoreCase(colName)) {
                ArrayList<Object> dropDownList = (ArrayList<Object>) comboBoxForSearchValue.get(col);

                if (colName.equalsIgnoreCase("dateOpened") || colName.equalsIgnoreCase("dateClosed")) {
                    Collections.sort(dropDownList, new Comparator<Object>() {
                        public int compare(Object o1, Object o2) {
                            return o2.toString().compareTo(o1.toString());
                        }

                    });

                } else if (colName.equalsIgnoreCase("rk")) {
                    if (dropDownList.get(0) == "") {
                        ArrayList<Object> list = new ArrayList<Object>();

                        for (int i = 1; i < dropDownList.size(); i++) {
                            list.add(dropDownList.get(i));
                        }
                        list.add(dropDownList.get(0));

                        dropDownList = list;
                    }
                } else if (colName.equalsIgnoreCase("programmer") || colName.equalsIgnoreCase("app")) {
                    Object nullValue = "";

                    Collections.sort(dropDownList, new Comparator<Object>() {
                        public int compare(Object o1, Object o2) {
                            if (o1 == nullValue && o2 == nullValue) {
                                return 0;
                            }

                            if (o1 == nullValue) {

                                return 1;
                            }

                            if (o2 == nullValue) {

                                return -1;
                            }

                            return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
                        }

                    });

                }
//                System.out.println(dropDownList);
                comboBoxStartToSearch = false;
                for (Object item : dropDownList) {

                    comboBoxSearchModel.addElement(item);

                }

            }
        }
//        comboBoxForSearch.setSelectedItem("Enter " + colName + " here");
//        comboBoxStartToSearch = true;
    }

    /**
     * This method is called when the search button is pressed
     *
     * @param evt
     */
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        filterBySearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    /**
     * This method is performed when the text field is used to search by either
     * clicking the search button or the Enter key in the text field. This
     * method is called by the searchActionPerformed method and the
     * textForSearchKeyPressed method
     */
    public void filterBySearch() {
        String text = "";

        int count = 0;
        for (Map.Entry<Integer, Tab> entry : tabs.entrySet()) {
            Tab tab = tabs.get(entry.getKey());
            JTable table = tab.getTable();

            String searchColName = comboBoxField.getSelectedItem().toString();
            String searchBoxValue = comboBoxValue.getSelectedItem().toString();  // store string from text box

            // this matches the combobox newValue with the column name newValue to get the column index
            for (int col = 0; col < table.getColumnCount(); col++) {
                String tableColName = table.getColumnName(col);
                if (tableColName.equalsIgnoreCase(searchColName)) {

                    // add item to filter
                    TableFilter filter = tab.getFilter();
                    filter.clearAllFilters();
                    filter.applyFilter();

                    boolean isValueInTable = false;
                    isValueInTable = checkValueInTableCell(col, searchBoxValue, table);

                    filter.addFilterItem(col, searchBoxValue);
                    filter.applyFilter();
                    if (!isValueInTable) {
                        count++;
                    }

                    // set label record information
                    String recordsLabel = tab.getRecordsLabel();
                    labelRecords.setText(recordsLabel);
                }
            }
            if (count == 4) {
                text = "There is no " + searchBoxValue
                        + " under " + searchColName + " in all tables";
            }
        }
        if (!text.equals("")) {
            LoggingAspect.afterReturn(text);

        }
    }

    private boolean checkValueInTableCell(int col, String target, JTable table) {
        int count = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            String cellValue = "";
            if (table.getValueAt(row, col) != null) {
                cellValue = table.getValueAt(row, col).toString();
            }

            if (cellValue.toLowerCase().contains(target.toLowerCase())) {

                count++;
            }
        }
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    // not sure what this is
    private void menuItemAWSAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAWSAssignActionPerformed

        Tab PMTable = tabs.get(0);
        
    }//GEN-LAST:event_menuItemAWSAssignActionPerformed
    /**
     * This method is performed when we click the upload changes button. it
     * uploads the changes and switch the edit mode off
     */
    private void btnUploadChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadChangesActionPerformed

        Tab tab = tabs.get(tabbedPanel.getSelectedIndex());
        tab.uploadChanges();
    }//GEN-LAST:event_btnUploadChangesActionPerformed


    private int[] getSelectedRowsId(JTable table) {
        int[] rows = table.getSelectedRows();

        int[] selectedRowsId = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            selectedRowsId[i] = (int) table.getValueAt(rows[i], 0);
        }
        return selectedRowsId;
    }

    private void menuItemRepBugSuggActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepBugSuggActionPerformed
//        reportWindow = new ReportWindow();
//        reportWindow.setLocationRelativeTo(this);
//        reportWindow.setVisible(true);
    }//GEN-LAST:event_menuItemRepBugSuggActionPerformed

    private void btnEnterSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnterSQLActionPerformed

        int commandStart = jTextAreaSQL.getText().lastIndexOf(">>") + 2;
        String command = jTextAreaSQL.getText().substring(commandStart);
        if(sqlOutputWindow == null){
            sqlOutputWindow = new SqlOutputWindow(command,this); 
        }
        else{
            sqlOutputWindow.setLocationRelativeTo(this);
            sqlOutputWindow.toFront();
            sqlOutputWindow.setTableModel(command);
            sqlOutputWindow.setVisible(true);
        }
    }//GEN-LAST:event_btnEnterSQLActionPerformed

    /**
     * btnCancelSQLActionPerformed
     *
     * @param evt
     */
    private void btnCancelSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelSQLActionPerformed
        ((AbstractDocument) jTextAreaSQL.getDocument())
                .setDocumentFilter(new CreateDocumentFilter(0));
        jTextAreaSQL.setText("Please input an SQL statement:\n>>");
        ((AbstractDocument) jTextAreaSQL.getDocument())
                .setDocumentFilter(new CreateDocumentFilter(33));
    }//GEN-LAST:event_btnCancelSQLActionPerformed

    private void btnCloseSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseSQLActionPerformed

        jPanelSQL.setVisible(false);
        menuItemSQLCmdChkBx.setSelected(false);
         this.setSize(this.getWidth(), 560);
    }//GEN-LAST:event_btnCloseSQLActionPerformed

   

    /**
     * @author Yi 7/11/2016
     * changeTabbedPanelState
     * getting states from panel, and set up the pwWindow components
     */
    public void changeTabbedPanelState(Tab tab) {
        
        //comboBoxField drop down reset
        String[] searchFields = tab.getSearchFields();
        if (searchFields != null) {
            comboBoxField.setModel(new DefaultComboBoxModel(searchFields));
        }
        //populate comboxValue drop down
        String searchContent = comboBoxField.getSelectedItem().toString();
        this.updateComboList(searchContent, tab);
        this.comboBoxValue.setSelectedItem("Enter search value here");
        
        
        //change addIssue button text
        btnAddIssue.setText("Add issue to " + tab.getTable().getName());
            
        //labelRecords reset for # of records andn # of records shown
        labelRecords.setText(tab.getRecordsLabel());
        
        //set buttons state
        setButtonsState(tab);
    
        Authorization.authorize(this);
    }
    
    
    /**
     * @author Yi 7/11/2016
     * set buttons visibility from tab state
     */
    private void setButtonsState(Tab tab) {
        btnAddIssue.setVisible(tab.getState().isAddBtnVisible());
        btnBatchEdit.setVisible(tab.getState().isBatchEditBtnVisible());
        btnUploadChanges.setVisible(tab.getState().isUploadChangesBtnVisible());
        btnRevertChanges.setVisible(tab.getState().isRevertChangesBtnVisible());
        if(tab.getState().isEditMode()) {
            labelEditModeState.setText("ON");
            
        }
        else labelEditModeState.setText("OFF");
        
        editModeTextColor(tab.getState().isEditMode());
    }
    
    /**
     * @author Yi 7/11/2016
     */

    private void btnBatchEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatchEditActionPerformed

        // get selected tab
        
        Tab tab = tabs.get(tabbedPanel.getSelectedIndex());
        JTable table = tab.getTable();
        int[] rows = table.getSelectedRows();

        // set the tab state to editing
        //hide add button, show changes btns, set editmode
        tab.getState().enableEdit(true);
        changeTabbedPanelState(tab);
        
        //open batch edit window
        // open a batch edit window and make visible only to this tab
        batchEditWindow = new BatchEditWindow(tab);
        this.setEnabled(false);
        batchEditWindow.setVisible(true);

    }//GEN-LAST:event_btnBatchEditActionPerformed

    private void menuItemManageDBsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemManageDBsActionPerformed

        editDatabaseWindow = new EditDatabaseWindow();
        editDatabaseWindow.setLocationRelativeTo(this);
        editDatabaseWindow.setVisible(true);

    }//GEN-LAST:event_menuItemManageDBsActionPerformed

    /**
     * btnAddRecordsActionPerformed
     *
     * @param evt
     */
    private void btnAddIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddIssueActionPerformed

        
        if (addIssueWindowShow) {
            addIssueWindow.toFront();
        }
        else {
            
            Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
            addIssueWindow = new IssueWindow(-1, currentTab);
            addIssueWindow.setVisible(true);
            addIssueWindowShow = true;
                        
        }

    }//GEN-LAST:event_btnAddIssueActionPerformed

    /**
     * jMenuItemLogOffActionPerformed Log Off menu item action performed
     *
     * @param evt
     */
    private void menuItemLogOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLogOffActionPerformed
        Object[] options = {"Reconnect", "Log Out"};  // the titles of buttons

        int n = JOptionPane.showOptionDialog(this, "Would you like to reconnect?", "Log off",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        switch (n) {
            case 0: {               // Reconnect

                // create a new Login Window
                loginWindow = new LoginWindow();
                loginWindow.setLocationRelativeTo(this);
                loginWindow.setVisible(true);

                // dispose of this Object and return resources
                this.dispose();

                break;
            }
            case 1:
                System.exit(0); // Quit
        }
    }//GEN-LAST:event_menuItemLogOffActionPerformed

    /**
     * btnClearAllFilterActionPerformed clear all filters
     *
     * @param evt
     */
    private void btnClearAllFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllFilterActionPerformed
        
        // clear all filters
        
        for (Map.Entry<Integer, Tab> entry : tabs.entrySet()) {
            Tab tab = tabs.get(entry.getKey());
            TableFilter filter = tab.getFilter();
            filter.clearAllFilters();
            filter.applyFilter();
        }
        
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        currentTab.setLabelRecords();

    }//GEN-LAST:event_btnClearAllFilterActionPerformed

    /**
     * jMenuItemOthersLoadDataActionPerformed
     *
     * @param evt
     */

    private void menuItemReloadDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReloadDataActionPerformed
        
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        currentTab.reloadTable();

    }//GEN-LAST:event_menuItemReloadDataActionPerformed

    
    

//    /**
//     * jArchiveRecordActionPerformed
//     *
//     * @param evt
//     */
    private void menuItemArchiveRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemArchiveRecordActionPerformed

//        int rowSelected = issuesTable.getSelectedRows().length;
//        int[] rowsSelected = issuesTable.getSelectedRows();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();
//        String today = dateFormat.format(date);
//
//        // Delete Selected Records from Assignments
//        if (rowSelected != -1) {
//            for (int i = 0; i < rowSelected; i++) {
//                String analyst = (String) issuesTable.getValueAt(rowsSelected[i], 2);
//                Integer selectedTask = (Integer) issuesTable.getValueAt(rowsSelected[i], 0); // Add Note to selected taskID
//                String sqlDelete = "UPDATE " + database + "." + issuesTable.getName() + " SET analyst = \"\",\n"
//                        + " priority=null,\n"
//                        + " dateAssigned= '" + today + "',"
//                        + " dateDone=null,\n"
//                        + " notes= \'Previous " + analyst + "' " + "where ID=" + selectedTask;
//                try {
//                    statement.executeUpdate(sqlDelete);
//                } catch (SQLException e) {
//                    LoggingAspect.afterThrown(ex);
//                }
//            }
//        } else {
//            JOptionPane.showMessageDialog(null, "Please, select one task!");
//        }
//        // Archive Selected Records in Assignments Archive
//        if (rowSelected != -1) {
//
//            for (int i = 0; i < rowSelected; i++) {
//                String sqlInsert = "INSERT INTO " + database + "." + issue_notesTable.getName() + " (symbol, analyst, priority, dateAssigned,dateDone,notes) VALUES (";
//                int numRow = rowsSelected[i];
//                for (int j = 1; j < issuesTable.getColumnCount() - 1; j++) {
//                    if (issuesTable.getValueAt(numRow, j) == null) {
//                        sqlInsert += null + ",";
//                    } else {
//                        sqlInsert += "'" + issuesTable.getValueAt(numRow, j) + "',";
//                    }
//                }
//                if (issuesTable.getValueAt(numRow, issuesTable.getColumnCount() - 1) == null) {
//                    sqlInsert += null + ")";
//                } else {
//                    sqlInsert += "'" + issuesTable.getValueAt(numRow, issuesTable.getColumnCount() - 1) + "')";
//                }
//                try {
//                    statement.executeUpdate(sqlInsert);
////                    logwind.addMessageWithDate(sqlInsert);
//                } catch (SQLException e) {
//                    LoggingAspect.afterThrown(ex);
//                }
//            }
//            loadTableData(issuesTable);
//            loadTableData(issue_notesTable);
//            issuesTable.setRowSelectionInterval(rowsSelected[0], rowsSelected[rowSelected - 1]);
//            JOptionPane.showMessageDialog(null, rowSelected + " Record(s) Archived!");
//
//        } else {
//            JOptionPane.showMessageDialog(null, "Please, select one task!");
//        }
    }//GEN-LAST:event_menuItemArchiveRecordActionPerformed

    /**
     * jActivateRecordActionPerformed
     *
     * @param evt
     */
    private void menuItemActivateRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemActivateRecordActionPerformed

//        int rowSelected = issue_notesTable.getSelectedRows().length;
//        int[] rowsSelected = issue_notesTable.getSelectedRows();
//        // Archive Selected Records in Assignments Archive
//        if (rowSelected != -1) {
//
//            for (int i = 0; i < rowSelected; i++) {
//                String sqlInsert = "INSERT INTO " + database + "." + issuesTable.getName() + "(symbol, analyst, priority, dateAssigned,dateDone,notes) VALUES ( ";
//                int numRow = rowsSelected[i];
//                for (int j = 1; j < issue_notesTable.getColumnCount() - 1; j++) {
//                    if (issue_notesTable.getValueAt(numRow, j) == null) {
//                        sqlInsert += null + ",";
//                    } else {
//                        sqlInsert += "'" + issue_notesTable.getValueAt(numRow, j) + "',";
//                    }
//                }
//                if (issue_notesTable.getValueAt(numRow, issue_notesTable.getColumnCount() - 1) == null) {
//                    sqlInsert += null + ")";
//                } else {
//                    sqlInsert += "'" + issue_notesTable.getValueAt(numRow, issue_notesTable.getColumnCount() - 1) + "')";
//                }
//                try {
//                    statement.executeUpdate(sqlInsert);
////                    ana.getLogWindow().addMessageWithDate(sqlInsert);
//                } catch (SQLException e) {
//                    LoggingAspect.afterThrown(ex);
//                }
//            }
//
//            issue_notesTable.setRowSelectionInterval(rowsSelected[0], rowsSelected[0]);
//            loadTableData(issue_notesTable);
//            loadTableData(issuesTable);
//
//            JOptionPane.showMessageDialog(null, rowSelected + " Record(s) Activated!");
//
//        } else {
//            JOptionPane.showMessageDialog(null, "Please, select one task!");
//        }
    }//GEN-LAST:event_menuItemActivateRecordActionPerformed

    /**
     * jCheckBoxMenuItemViewLogActionPerformed
     *
     * @param evt
     */
    private void menuItemLogChkBxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLogChkBxActionPerformed

        if (menuItemLogChkBx.isSelected()) {

            logWindow.setLocationRelativeTo(this);
            logWindow.setVisible(true); // show log window

            // remove check if window is closed from the window
            logWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    menuItemLogChkBx.setSelected(false);
                }
            });
        } else {
            // hide log window
            logWindow.setVisible(false);
        }
    }//GEN-LAST:event_menuItemLogChkBxActionPerformed

    /**
     * jCheckBoxMenuItemViewSQLActionPerformed
     *
     * @param evt
     */
    private void menuItemSQLCmdChkBxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSQLCmdChkBxActionPerformed

        /**
         * ************* Strange behavior ************************* The
         * jPanelSQL.getHeight() is the height before the
         * jCheckBoxMenuItemViewSQLActionPerformed method was called.
         *
         * The jPanelSQL.setVisible() does not change the size of the sql panel
         * after it is executed.
         *
         * The jPanel size will only change after the
         * jCheckBoxMenuItemViewSQLActionPerformed is finished.
         *
         * That is why the the actual integer is used rather than getHeight().
         *
         * Example: jPanelSQL.setVisible(true); jPanelSQL.getHeight(); // this
         * returns 0
         */
        if (menuItemSQLCmdChkBx.isSelected()) {

            // show sql panel
            jPanelSQL.setVisible(true);
            this.setSize(this.getWidth(), 560 + 112);

        } else {

            // hide sql panel
            jPanelSQL.setVisible(false);
            this.setSize(this.getWidth(), 560);
        }
    }//GEN-LAST:event_menuItemSQLCmdChkBxActionPerformed

    private void btnRevertChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevertChangesActionPerformed

        try {
            revertChanges();
        } catch (IOException ex) {
            Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnRevertChangesActionPerformed

    
    private void menuitemViewOneIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuitemViewOneIssueActionPerformed

        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());

        JTable table = currentTab.getTable();
        int row = table.getSelectedRow();
        
        openIssueWindow(row, currentTab);
        
        
    }//GEN-LAST:event_menuitemViewOneIssueActionPerformed

    /*
    **@author Yi
    ** 07/13/2016
    */
    public void openIssueWindow(int rowIndex, Tab tab) {
        
        boolean isIssueAlreadyOpened = false;
        JTable table = tab.getTable();
        //get issue id 
        int id = (int) table.getValueAt(rowIndex, 0);
        //set the issue identifier
        String identifier = table.getName() + id;
        
        //if issue already opened
        if (openingIssuesList.size() > 0) {
            for (String uniqueId : openingIssuesList.keySet()) {
            
                if (identifier.equals(uniqueId)) {
                    isIssueAlreadyOpened = true;
                    break;
                }
            }
        
        }
        
        if (isIssueAlreadyOpened) {
            openingIssuesList.get(identifier).toFront();
        } else {
            if (openingIssuesList.size() < 6) {
                
                IssueWindow viewIssue = new IssueWindow(id, tab);
                openingIssuesList.put(identifier, viewIssue);
                tab.getFilter().getCustomIdListFilter().add(id);
                viewIssue.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(null,
                        "The number of view issue window "
                        + "opened reached its maximum: 6!");

            }
        }
        
    }
    
    
    private void comboBoxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxFieldActionPerformed
        System.out.println("here" + comboBoxStartToSearch);
        searchColName = comboBoxField.getSelectedItem().toString();
        searchValue = comboBoxValue.getSelectedItem().toString();
        // update the dropdown list when we change a searchable item
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        updateComboList(searchColName, currentTab);

        comboBoxValue.setSelectedItem(searchValue);

        comboBoxStartToSearch = true;
//        System.out.println("there" + comboBoxStartToSearch);

    }//GEN-LAST:event_comboBoxFieldActionPerformed

    /* By Yi */
    private void menuItemTurnEditModeOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTurnEditModeOffActionPerformed
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        currentTab.getState().enableEdit(false);
        changeTabbedPanelState(currentTab);
    }//GEN-LAST:event_menuItemTurnEditModeOffActionPerformed

    /**
     * tabbedPanelStateChanged
     *
     * @param evt
     */
    private void tabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPanelStateChanged
        
        // tab change will change related components
        
        if (tabs != null) {
            Tab tab = tabs.get(tabbedPanel.getSelectedIndex());
            
            //set up related JComponents 
            changeTabbedPanelState(tab);
        }
    
    }//GEN-LAST:event_tabbedPanelStateChanged

    private void menuItemMoveSeletedRowsToEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMoveSeletedRowsToEndActionPerformed
        String tabName = getSelectedTabName();
        Tab tab = tabs.get(tabName);
        JTable table = tab.getTable();
        int[] rows = table.getSelectedRows();

        moveSelectedRowsToTheEnd(rows, table);
    }//GEN-LAST:event_menuItemMoveSeletedRowsToEndActionPerformed

    private void comboBoxValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxValueActionPerformed

        if (comboBoxStartToSearch) {
            String fieldToSearch = comboBoxField.getSelectedItem().toString().toLowerCase();
            switch (fieldToSearch) {
                case "programmer":
                case "dateopened":
                case "dateclosed":
                case "rk":
                    filterBySearch();
                    break;
                default:
                    break;
            }
        }


    }//GEN-LAST:event_comboBoxValueActionPerformed

    private void menuItemViewSplashScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemViewSplashScreenActionPerformed
        try {
            String fileName = FilePathFormat.supportFilePath() + "splashImage.png";
            ImageIcon img = new ImageIcon(ImageIO.read(new File(fileName)));
            JFrame splashScreenImage = new JFrame();
            JLabel image = new JLabel(img);
            splashScreenImage.add(image);
            splashScreenImage.pack();
            splashScreenImage.setLocationRelativeTo(this);
            splashScreenImage.setVisible(true);
            LoggingAspect.addLogMsgWthDate("3:" + "splash screen image show.");
        } catch (IOException ex) {
            LoggingAspect.addLogMsgWthDate("3:" + ex.getMessage());
            LoggingAspect.afterThrown(ex);
        }
    }//GEN-LAST:event_menuItemViewSplashScreenActionPerformed

    private void menuItemCompIssuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCompIssuesActionPerformed
        CompIssuesListWindow frame = new CompIssuesListWindow();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }//GEN-LAST:event_menuItemCompIssuesActionPerformed

    private void menuItemBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemBackupActionPerformed

        // open new connection
        DBConnection.close(); // connection might be timed out on server
        if (DBConnection.open()) {  // open a new connection
            BackupDBTablesDialog backupDBTables = new BackupDBTablesDialog(this);
        } else {
            JOptionPane.showMessageDialog(this, "Could not connect to Database");
        }
    }//GEN-LAST:event_menuItemBackupActionPerformed

    private void menuItemReloadAllDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReloadAllDataActionPerformed
        
       reloadAllData();
       

    }//GEN-LAST:event_menuItemReloadAllDataActionPerformed

    private void reloadAllData() {

        for (Map.Entry<Integer, Tab> entry : tabs.entrySet()) {
            
            Tab tab = tabs.get(entry.getKey());         
            tab.reloadTable();
        }

    }
    /**
     * menuItemDeleteRecordActionPerformed Delete records menu item action
     * performed
     *
     * @param evt
     */
    private void menuItemDeleteRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteRecordActionPerformed
        
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        JTable table = currentTab.getTable();
        
        // get the ids
        int[] selectedRows = table.getSelectedRows(); // array of the rows selected
        
        int rowCount = selectedRows.length; // the number of rows selected
        
        ArrayList<Integer> authorizedRows = new ArrayList(); //rows index eligible for removal
        ArrayList<Integer> ids = new ArrayList(); //issue ids eligible for removal from db
        if (rowCount > 0) {
 
            for (int i = 0, j=0; i < rowCount; i++) {
                int row = selectedRows[i];
                int selectedID = (int) table.getValueAt(row, 0); // Add Note to selected taskID
                String type = (String) table.getValueAt(row, 9);
                if (Authorization.getAccessLevel().equals("administrator") || type.equals("TEST ISSUE")) {
                    ids.add(selectedID);
                    authorizedRows.add(row);
                }
                     
                else {
                    JOptionPane.showMessageDialog(this,
                    "You are not authorized to delete Issue " + selectedID + " .",
                    "Error Message",
                    JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            }
            //delete authorized records from db
            if (table.getName().equals("References")) {
                dataManager.deleteReferences(ids);  
            }
            else
                dataManager.deleteIssues(ids);
            
            //delete particular rows from table - Yi
            for(int index = authorizedRows.size() -1; index >= 0; index --) {
                currentTab.deleteRow(index);
            }
        
            // update records label
            String recordsLabel = currentTab.getRecordsLabel();
            labelRecords.setText(recordsLabel); // update label
        }
        else{
            // no rows are selected
        }
    }//GEN-LAST:event_menuItemDeleteRecordActionPerformed

    private int[] convertToArray(ArrayList<Integer> input) {
        int[] result = new int[input.size()];
        
        for(int index = 0; index< input.size(); index++) {
            result[index] = input.get(index);
            
        }
        return result;
        
    }
    private void menuItemReloadSelectedDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReloadSelectedDataActionPerformed
       
        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        currentTab.reloadSelectedData();
        
    }//GEN-LAST:event_menuItemReloadSelectedDataActionPerformed

    private void menuItemSyncLocalDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSyncLocalDataActionPerformed
        // Sync local data
        LoggingAspect.addLogMsgWthDate("Preparing to sync local data to db server.....");
        offlineIssueMgr.syncLocalData();
        
    }//GEN-LAST:event_menuItemSyncLocalDataActionPerformed

    private void menuItemOfflineModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOfflineModeActionPerformed
       if (menuItemOfflineMode.isSelected()) {

            
            online = false;
            status.setText("Offline");
            status.setForeground(Color.RED);
            menuItemSyncLocalData.setEnabled(false);
            menuItemReconcileConflict.setEnabled(false);
            
            dataManager.setOpMode(false);

        } else {
           
            online = true;
            status.setText("Online");
            status.setForeground(new Color(0, 153, 0));
            menuItemSyncLocalData.setEnabled(true);
            menuItemReconcileConflict.setEnabled(true);
            dataManager.setOpMode(true);
        }
        
    }//GEN-LAST:event_menuItemOfflineModeActionPerformed

    private void menuItemReconcileConflictActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReconcileConflictActionPerformed
        
        if (reconcileWindowShow) {
            reconcileWindow.toFront();
        }
        
        else {
            if (offlineIssueMgr.getConflictIssues().size() > 0){
            
                try {
                    reconcileWindow = new ReconcileWindow();
                } catch (IOException ex) {
                    Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadLocationException ex) {
                    Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                reconcileWindow.setVisible(true);
                reconcileWindowShow = true;
                reconcileWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        
            }
            else {
                JOptionPane.showMessageDialog(this,
                     "There are no conflicts issues to be resolved.");
                
            }
            
        }
            
       
        
     
    }//GEN-LAST:event_menuItemReconcileConflictActionPerformed

    private void menuItemLoadDataFromTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLoadDataFromTXTActionPerformed
        IssueDAO issueDAO = new IssueDAO();
        
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        
        
        List<String> importedfile = new ArrayList<>();
        int maximumid = issueDAO.getMaxId();
        
        if (returnVal == fc.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String filename = fc.getSelectedFile().getName();
            String extension = filename.substring(filename.lastIndexOf("."), filename.length());
            //System.out.println(filename);

            if (!".txt".equals(extension)) {
                JOptionPane.showMessageDialog(fc, "Invalid filetype! Please choose a txt file!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try{
                    BufferedReader br
                            = new BufferedReader(
                                    new FileReader(file));
                    String line = br.readLine();
                    
                    while(line != null){
                        line = br.readLine();
                        importedfile.add(line);
                    }
                    
                }catch(NullPointerException e){
                    JOptionPane.showMessageDialog(fc, "Please choose a txt file!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    LoggingAspect.afterThrown(ex);
                }
            }
        }
        
        //file extracting and database updating
        int currentstartline = 0;
        int currentendline = 0;
        List<Integer> hyphenlines = new ArrayList<>();
                    
        System.out.println(importedfile.get(0));
        System.out.println(importedfile.get(1));
        if (importedfile != null) {
            
            for(int i=0; i<importedfile.size(); i++){
                //System.out.println(importedfile.get(i));
                String currentstring = importedfile.get(i);
                
                if (currentstring != null) {
                    if (currentstring.equals(HYPHENS)){
                        hyphenlines.add(i);
                    }
                }     
            }
            System.out.println(hyphenlines.size());
            for(int i=0; i<hyphenlines.size(); i++){
                currentendline = hyphenlines.get(i) - 1;
                IssueDAO dao = new IssueDAO();
                Issue issue = new Issue();
                boolean recording = false;
                int currentid = 0;
                String descriptionstring = "{\\rtf1\\ansi\n" +
                    "{\\fonttbl\\f0\\fnil Monospaced;\\f1\\fnil sansserif;}\n" +
                    "{\\colortbl\\red0\\green0\\blue0;\\red0\\green0\\blue0;}\n" +
                                                                            "\n";
                for (int j = currentstartline; j < currentendline; j++) {
                    String filestring = importedfile.get(j);
                    if (filestring.indexOf("ID:") != -1){
                        String[] splited = filestring.split(" ");
                        
                        System.out.println(splited[1]);
                        
                        if (splited[1] != null) {
                            issue.setId(Integer.parseInt(splited[1]));
                            currentid = Integer.parseInt(splited[1]);  
                        } else {
                            issue.setId(-1);
                        }

                        if (splited[3] != null) {
                            issue.setApp(splited[3]);    
                        } else {
                            issue.setApp(""); 
                        }

                        if (splited.length > 9) {
                            
                            if (splited[5] != null) {
                                issue.setProgrammer(splited[5]);
                            } else {
                                issue.setProgrammer("");
                            }

                            if (splited[7] != null) {
                                issue.setRk(splited[7]);
                            } else {
                                issue.setRk("");
                            }

                            if (splited[9] != null) {
                                issue.setDateOpened(splited[9]);
                            } else {
                                issue.setDateOpened("");
                            }


                            if (splited.length > 10) {
                                if (splited[11] != null) {
                                    issue.setDateClosed(splited[11]);
                                } else {
                                    issue.setDateClosed("");
                                } 
                            } else {
                                issue.setDateClosed("");
                            }
                            
                        } else {
                            
                            if (splited[5] != null) {
                                issue.setRk(splited[5]);
                            } else {
                                issue.setRk("");
                            }

                            if (splited[7] != null) {
                                issue.setDateOpened(splited[7]);
                            } else {
                                issue.setDateOpened("");
                            }
                        }

                    }
                    
                    if (filestring.indexOf("title:") != -1){
                        String[] splited = filestring.split(" ");
                        issue.setTitle(splited[1]);
                    }
                    
                    if (recording == true) {
                        descriptionstring += filestring + "\\par\n";
                    }
                    
                    if ( j == currentendline){
                        descriptionstring += "}";
                    }
                    
                    if (filestring.indexOf("description:") != -1){
                        recording = true;
                    }
                }
                byte[] b = descriptionstring.getBytes(Charset.forName("UTF-8"));
                
                issue.setDescription(b);
                
                if (currentid <= maximumid) {
                    dao.update(issue);
                } else {
                    dao.insert(issue);
                }
                
                currentstartline = currentendline + 2; 
            }
            System.out.println("Loading completed!");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_menuItemLoadDataFromTXTActionPerformed

    private void menuItemExportIssueToReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExportIssueToReferenceActionPerformed
        // get selected issues
        
        if (tabbedPanel.getSelectedIndex() != 4) {
            Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
            JTable table = currentTab.getTable();
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length > 0) {
                for(int row : selectedRows) {
                    int id = (int)table.getValueAt(row, 0);
                    Issue temp = dataManager.getIssueEntity(id);
                    Issue ref = copyIssueToRef(temp);
                    dataManager.insertReference(ref);
                    
                    //insert to reference table
                    tabs.get(4).insertRow(dataManager.getReference(ref));            
                }               
            }
        }
  
    }//GEN-LAST:event_menuItemExportIssueToReferenceActionPerformed

   private Issue copyIssueToRef(Issue issue) {
       Issue ref = new Issue();
       ref.setId(-1);
       ref.setTitle(issue.getTitle());
       ref.setDescription(issue.getDescription());
       ref.setProgrammer(issue.getProgrammer());
       ref.setDateOpened(issue.getDateOpened());
       ref.setLocked(issue.getLocked());
       ref.setLastmodtime(issue.getLastmodtime());
       issue.setIssueType("REFERENCE");
       return ref;
   }
 
//    private void syncLocalData() {
//        
//        File issuesDir = FilePathFormat.localDataFilePath();
//        File[] listOfFiles = issuesDir.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++) {
//            syncIssueToDbFromFile(listOfFiles[i]);
//            
//        } 
//    }
//    
//    //currently only finish the insert part
//    // update part will be added later
//    
//    private void syncIssueToDbFromFile(File file) {
//        logWindow.addMessage("Now sync file: " + file.getName());
//        try{
//            ObjectInputStream ois =
//                    new ObjectInputStream(new FileInputStream(file));
//            Issue temp = (Issue)ois.readObject();
//            ois.close();
//            boolean success = false;
//            String option = "new";
//            if (file.getName().startsWith("new")){
//                success = issueDAO.insert(temp);               
//            }
////            if (file.getName().startsWith("update")) {
////                Issue dbIssue = issueDAO.get(temp.getId());
////                if (dbIssue.getDatetimeLastMod()!= null &&
////                        dbIssue.getDatetimeLastMod().compareTo(temp.getDatetimeLastMod()) > 0) {
////                
////                    StringBuilder sb  = new StringBuilder(dbIssue.getDescription());
////                    sb.append(System.lineSeparator());
////                    sb.append("<Merged from local data>");
////                    sb.append(System.lineSeparator());
////                    sb.append(temp.getDescription());
////                    sb.append(System.lineSeparator());
////                    sb.append("</Merged from local data>");
////                    temp.setDescription(sb.toString());
////                  
////                }
//                
////                
////                success = issueDAO.update(temp);
////                option = "update";
////                
////            }
//            
//            if (success) {
//               
//                for (Tab tab : tabs.values()) {
//                    if (tab.getTable().getName().equals(temp.getApp())){
//                       if (option.equals("new"))
//                            insertTableRow(tab.getTable(),temp);
//                       if (option.equals("update"))
//                            updateTableRow(tab.getTable(),temp);
//                      makeTableEditable(false);
//                      break;
//                    }
//                }
//                logWindow.addMessage(file.getName() +" is updated to server successfully");
//                if (file.delete()) {
//                    logWindow.addMessage(file.getName() +" is deleted");
//                }
//                else {
//                    logWindow.addMessage(file.getName() +" failed to be deleted, please manually delete it");    
//                }
//            }
//            
//            else{
//                
//                logWindow.addMessage(file.getName() + " failed to update to db server");
//                     
//            }
//            
//        }  catch (IOException ex) {
//            System.out.println( "File I/O error ");
//        } catch (ClassNotFoundException ex) {
//            System.out.println("Issue class not found error");
//        }
//        
//        
//    }
//     
    
     
    
   
    
    public void comboBoxForSearchEditorMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            comboBoxValue.getEditor().selectAll();
        } else if (e.isControlDown()) {
            comboBoxValue.showPopup();
        }
    }

    public void moveSelectedRowsToTheEnd(int[] rows, JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowNum = table.getRowCount();
        int count = 0;
        for (int row : rows) {
            row = row - count;

            model.moveRow(row, row, rowNum - 1);
            count++;
        }
        table.setRowSelectionInterval(rowNum - count, rowNum - 1);
    }


    
    /**
     * getSelectedTable gets the selected tabName
     *
     * @return
     */
    public JTable getSelectedTable() {  //get JTable by  selected Tab
        String tabName = getSelectedTabName();
        Tab tab = tabs.get(tabName);
        JTable table = tab.getTable();
        return table;
    }

    /**
     * get Opening Issues list
     *
     * @return openingIssuesList
     */
    public Map getOpeningIssuesList() {
        return openingIssuesList;
    }

    public IssueWindow getViewIssueWindowOf(String id) {
        return this.openingIssuesList.get(id);
    }

    /**
     * setLastUpdateTime sets the last update time label
     */
    public void setLastUpdateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(new Date());
        labelTimeLastUpdate.setText("Last updated: " + time);
    }

    /**
     * setKeyboardFocusManager sets the keyboard focus manager
     */
    private void setKeyboardFocusManager(JFrame frame) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_TAB) {
                            if (e.isAltDown()) {
//                        if (e.getComponent() instanceof JTable) {
//                            JTable table = (JTable) e.getComponent();
                                Tab tab = tabs.get(getSelectedTabName());
                                JTable table = tab.getTable();

                                if (table.isEditing()) {
                                    table.getCellEditor().stopCellEditing();
                                }
                                setEnabledEditingButtons(true, true, true);

                            } else {
                                if (labelEditModeState.getText().equals("ON ")) {
                                    if (e.getComponent() instanceof JTable) {
                                        JTable table = (JTable) e.getComponent();
                                        int row = table.getSelectedRow();
                                        int column = table.getSelectedColumn();
                                        if (column == table.getRowCount() || column == 0) {
                                            return false;
                                        } else {
                                            table.getComponentAt(row, column).requestFocus();
                                            table.editCellAt(row, column);
                                            JTextField selectCom = (JTextField) table.getEditorComponent();
                                            selectCom.requestFocusInWindow();
                                            selectCom.selectAll();
                                        }

                                        // if table cell is editing 
                                        // then the editing buttons should not be enabled
                                        if (table.isEditing()) {
                                            setEnabledEditingButtons(false, false, false);
                                        }
                                    }
                                }
                            }

                        } else if (!isBatchEditWindowShow && !addIssueWindowShow) {
                            if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {

                                if (labelEditModeState.getText().equals("ON ")) {                       // Default Date input with today's date
//                        && !batchEditWindow.isBatchEditWindowShow()
                                    JTable table = (JTable) e.getComponent().getParent();
                                    int column = table.getSelectedColumn();
                                    if (table.getColumnName(column).toLowerCase().contains("date")) {
                                        if (e.getID() != 401) { // 401 = key down, 402 = key released
                                            return false;
                                        } else {
                                            JTextField selectCom = (JTextField) e.getComponent();
                                            selectCom.requestFocusInWindow();
                                            selectCom.selectAll();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date = new Date();
                                            String today = dateFormat.format(date);
                                            selectCom.setText(today);
                                        }
                                    }
                                }
                            }
                        } else if ((e.getKeyCode() == KeyEvent.VK_ENTER)) {
                            if (e.getComponent() instanceof JTable) {
                                JTable table = (JTable) e.getComponent();

                                // make sure in editing mode
                                if (labelEditModeState.getText().equals("ON ")
                                && !table.isEditing()
                                && e.getID() == KeyEvent.KEY_PRESSED) {

                                    // only show popup if there are changes to upload or revert
                                    if (btnUploadChanges.isEnabled() || btnRevertChanges.isEnabled()) {
                                        // if finished display dialog box
                                        // Upload Changes? Yes or No?
                                        Object[] options = {"Commit", "Revert"};  // the titles of buttons

                                        // store selected rowIndex before the table is refreshed
                                        int rowIndex = table.getSelectedRow();

                                        int selectedOption = JOptionPane.showOptionDialog(ProjectManagerWindow.getInstance(),
                                                "Would you like to upload changes?", "Upload Changes",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null, //do not use a custom Icon
                                                options, //the titles of buttons
                                                options[0]); //default button title

                                        switch (selectedOption) {
                                            case 0:
                                                // if Commit, upload changes and return to editing
                                                //uploadChanges();  // upload changes to database, commented out by YI
                                                break;
                                            case 1:
                                        {
                                            try {
                                                // if Revert, revert changes
                                                revertChanges(); // reverts the model back
                                            } catch (IOException ex) {
                                                Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (BadLocationException ex) {
                                                Logger.getLogger(ProjectManagerWindow.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                                break;
                                            default:
                                                // do nothing -> cancel
                                                break;
                                        }

                                        // highlight previously selected rowIndex
                                        if (rowIndex != -1) {
                                            table.setRowSelectionInterval(rowIndex, rowIndex);
                                        }
                                    }
                                }

                            }

                        }


                        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_V) {
                            if (e.getComponent() instanceof JTable) {
                                
                                Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
                                int rowIndex = currentTab.getTable().getSelectedRow();
                                
                                if (rowIndex != -1) 
                                    openIssueWindow(rowIndex, currentTab);

                            }
                        }
                        return false;
                    }
                });
    }

    public void setDisableProjecetManagerFunction(boolean f) {
        setEnableProjectManagerFunction(f);
    }

    private void setEnableProjectManagerFunction(boolean disable) {

        tabbedPanel.setEnabled(disable);
        btnAddIssue.setEnabled(disable);
        btnBatchEdit.setEnabled(disable);
        btnClearAllFilter.setEnabled(disable);
        btnSearch.setEnabled(disable);
        btnUploadChanges.setEnabled(disable);
        btnRevertChanges.setEnabled(disable);
        comboBoxField.setEnabled(disable);
        menuEdit.setEnabled(disable);
        menuFile.setEnabled(disable);
        menuHelp.setEnabled(disable);
        menuView.setEnabled(disable);
        menuTools.setEnabled(disable);
        searchPanel.setEnabled(disable);

        String tabName = getSelectedTabName();
        Tab tab = tabs.get(tabName);
        tab.getTable().setEnabled(disable);
        if (!disable) {
            //set sort and filter enabled
            TableFilter filter = tab.getFilter();
            for (int i = 0; i < tab.getTable().getColumnCount(); i++) {
                filter.getSorter().setSortable(i, disable);
            }
        }
    }

    public static ProjectManagerWindow getInstance() {
        return instance;
    }

    public JLabel getRecordsLabel() {
        return labelRecords;
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }

    public Map<Integer, Tab> getTabs() {
        return tabs;
    }

    public String getSelectedTabName() {
        String title = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
        if (title.startsWith("<")) {
            title = title.substring(9, title.length() - 11);
        }

        return title;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void showDatabase() {
        databaseLabel = new JLabel("                                          "
                + "                                                             "
                + "                                                             " + database);

        menuBar.add(databaseLabel);
    }

    public void setLogWindow(LogWindow logWindow) {
        this.logWindow = logWindow;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setInformationLabel(String inf, int second) {
        this.informationLabel.setText(inf);
        startCountDownFromNow(second);
    }

    public void setFiltersclean() {
        for (Map.Entry<Integer, Tab> entry : tabs.entrySet()) {
            Tab tab = tabs.get(entry.getKey());
            TableFilter filter = tab.getFilter();
            filter.clearAllFilters();
            filter.applyFilter();
            filter.applyColorHeaders();

        }

    }

    public String getUserName() {
        return this.userName;
    }

    public Statement getStatement() {
        return statement;
    }

    public JPanel getjPanelEdit() {
        return jPanelEdit;
    }

    /**
     * initTotalRowCounts called once to initialize the total rowIndex counts of
     * each tabs tableSelected
     *
     * @param tabs
     * @return
     */



    /**
     * This returns a JLabel with the title and an orange dot
     *
     * @param title
     * @return
     */
    public JLabel getTabLabelWithOrangeDot(String title) {
        ImageIcon imcon = new ImageIcon(getClass().getResource("orange-dot.png"));

//        ImageIcon icon = new ImageIcon(getClass().getResource("splashImage.png"));
        JLabel label = new JLabel(imcon);
        label.setText(title);

        label.setIconTextGap(10);

        label.setHorizontalTextPosition(JLabel.LEFT);
        label.setVerticalTextPosition(JLabel.TOP);
        return label;
    }

    
   
    /**
     * set the timer for information Label show
     *
     * @param waitSeconds
     */
    public static void startCountDownFromNow(int waitSeconds) {
        Timer timer = new Timer(waitSeconds * 1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                informationLabel.setText("");
                searchInformationLabel.setText("");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * ***** added methods ******************************
     */
    public JPanel getAddPanel_control() {
        return addPanel_control;
    }

    public JPanel getjPanel5() {
        return jPanel5;
    }

    public JPanel getjPanelSQL() {
        return jPanelSQL;
    }

    public JPanel getSearchPanel() {
        return searchPanel;
    }

    /**
     * setBatchEditButtonStates Sets the batch edit button enabled if editing
     * allowed for that tab and disabled if editing is not allowed for that tab
     *
     * @param selectedTab // this is the editing tab
     */
    

    /**
     * getBtnBatchEdit
     *
     * @return
     */
    public JButton getBtnBatchEdit() {
        return btnBatchEdit;
    }

    public BatchEditWindow getBatchEditWindow() {
        return batchEditWindow;
    }

 
    /**
     * setEnabledEditingButtons sets the editing buttons enabled
     *
     * @param switchBtnEnabled
     * @param uploadEnabled
     * @param revertEnabled
     */
    public void setEnabledEditingButtons(boolean switchBtnEnabled, boolean uploadEnabled, boolean revertEnabled) {

        // the three editing buttons (cancel, upload, revert)
//        btnSwitchEditMode.setEnabled(switchBtnEnabled);
        btnUploadChanges.setEnabled(uploadEnabled);
        btnRevertChanges.setEnabled(revertEnabled);
    }

    /**
     * revertChanges used to revert changes of modified data to original data
     */
    public void revertChanges() throws IOException, BadLocationException {

        Tab currentTab = tabs.get(tabbedPanel.getSelectedIndex());
        currentTab.revertChanges();
    }

    /**
     * editModeTextColor This method changes the color of the edit mode text If
     * edit mode is active then the text is green and if it is not active then
     * the text is the default color (black)
     */
    public void editModeTextColor(boolean editing) {

        // if editing
        if (editing) {
            labelEditMode.setForeground(editModeActiveTextColor);
            labelEditModeState.setForeground(editModeActiveTextColor);
        } // else not editing
        else {
            labelEditMode.setForeground(editModeDefaultTextColor);
            labelEditModeState.setForeground(editModeDefaultTextColor);
        }
    }

    /**
     * showWindowInFront This shows the component in front of the Main Window
     *
     * @param c Any component that needs to show on top of the Main window
     */
    public void showWindowInFront(Component c) {

        ((Window) (c)).setAlwaysOnTop(true);

    }

    public String getEditingTabName() {
        return editingTabName;
    }

    // @formatter:off
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable AnalysterTable;
    private javax.swing.JTable ELLEGUITable;
    private javax.swing.JTable OtherTable;
    private javax.swing.JTable PMTable;
    private javax.swing.ButtonGroup TableFilterBtnGroup;
    private javax.swing.JPanel addPanel_control;
    private javax.swing.JButton btnAddIssue;
    private javax.swing.JButton btnBatchEdit;
    private javax.swing.JButton btnCancelSQL;
    private javax.swing.JButton btnClearAllFilter;
    private javax.swing.JButton btnCloseSQL;
    private javax.swing.JButton btnEnterSQL;
    private javax.swing.JButton btnRevertChanges;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUploadChanges;
    private javax.swing.JComboBox comboBoxField;
    private javax.swing.JComboBox comboBoxValue;
    public static javax.swing.JLabel informationLabel;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSQL;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea jTextAreaSQL;
    private javax.swing.JLabel labelEditMode;
    private javax.swing.JLabel labelEditModeState;
    private javax.swing.JLabel labelRecords;
    private javax.swing.JLabel labelTimeLastUpdate;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAWSAssign;
    private javax.swing.JMenuItem menuItemActivateRecord;
    private javax.swing.JMenuItem menuItemArchiveRecord;
    private javax.swing.JMenuItem menuItemBackup;
    private javax.swing.JMenuItem menuItemCompIssues;
    private javax.swing.JMenuItem menuItemDeleteRecord;
    private javax.swing.JMenuItem menuItemExportIssueToReference;
    private javax.swing.JMenuItem menuItemLoadDataFromTXT;
    private javax.swing.JCheckBoxMenuItem menuItemLogChkBx;
    private javax.swing.JMenuItem menuItemLogOff;
    private javax.swing.JMenuItem menuItemManageDBs;
    private javax.swing.JMenuItem menuItemMoveSeletedRowsToEnd;
    private javax.swing.JCheckBoxMenuItem menuItemOfflineMode;
    private javax.swing.JMenuItem menuItemPrintDisplay;
    private javax.swing.JMenuItem menuItemPrintGUI;
    private javax.swing.JMenuItem menuItemReconcileConflict;
    private javax.swing.JMenuItem menuItemReloadAllData;
    private javax.swing.JMenuItem menuItemReloadData;
    private javax.swing.JMenuItem menuItemReloadSelectedData;
    private javax.swing.JMenuItem menuItemRepBugSugg;
    private javax.swing.JCheckBoxMenuItem menuItemSQLCmdChkBx;
    private javax.swing.JMenuItem menuItemSaveFile;
    private javax.swing.JMenuItem menuItemSyncLocalData;
    private javax.swing.JMenuItem menuItemTurnEditModeOff;
    private javax.swing.JMenuItem menuItemVersion;
    private javax.swing.JMenuItem menuItemViewSplashScreen;
    private javax.swing.JMenu menuPrint;
    private javax.swing.JMenu menuSelectConn;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenuItem menuitemViewOneIssue;
    private javax.swing.JTable referenceTable;
    public static javax.swing.JLabel searchInformationLabel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JLabel status;
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables
    // @formatter:on

    

    public boolean getAddRecordsWindowShow() {

        return this.addIssueWindowShow;

    }

    public JFrame getAddRecordsWindow() {
        return this.addIssueWindow;
    }

    public void setAddIssueWindowShow(boolean a) {

        this.addIssueWindowShow = a;

    }

    public void setIsBatchEditWindowShow(boolean a) {
        this.isBatchEditWindowShow = a;
    }

    public boolean getIsBatchEditWindowShow() {
        return this.isBatchEditWindowShow;
    }

    public int getNumOfAddIssueWindowOpened() {
        return this.openingIssuesList.size();
    }

//    public void deleteNumOfAddIssueWindowOpened() {
//        this.numOfAddIssueWindowOpened--;
//        if (numOfAddIssueWindowOpened == 0) {
//            addIssueWindowShow = false;
//        }
//    }
//    public void setPopupWindowShowInPM(boolean b) {
//        popupWindowShowInPM = b;
//    }
    public JLabel getInformationLabel() {
        return this.informationLabel;
    }

    public JLabel getLabelEditModeState() {
        return this.labelEditModeState;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, Map<Integer, ArrayList<Object>>> getComboBoxForSearchDropDown() {
        return comboBoxForSearchDropDown;
    }

    public String getDatabase() {
        return database;
    }

    

    public IssueWindow getAddIssueWindow() {
        return addIssueWindow;
    }

    public LoginWindow getLoginWindow() {
        return loginWindow;
    }

    public EditDatabaseWindow getEditDatabaseWindow() {
        return editDatabaseWindow;
    }

    public Color getEditModeDefaultTextColor() {
        return editModeDefaultTextColor;
    }

    public Color getEditModeActiveTextColor() {
        return editModeActiveTextColor;
    }

    public boolean isAddIssueWindowShow() {
        return addIssueWindowShow;
    }

    public boolean isIsBatchEditWindowShow() {
        return isBatchEditWindowShow;
    }

    public boolean isPopupWindowShowInPM() {
        return popupWindowShowInPM;
    }

    public JLabel getDatabaseLabel() {
        return databaseLabel;
    }

    public String getCurrentTabName() {
        return currentTabName;
    }

    public ArrayList<String> getProgrammersActiveForSearching() {
        return programmersActiveForSearching;
    }

    public JTable getAnalysterTable() {
        return AnalysterTable;
    }

    public JTable getELLEGUITable() {
        return ELLEGUITable;
    }

    public JTable getOtherTable() {
        return OtherTable;
    }

    public JTable getPMTable() {
        return PMTable;
    }

    public ButtonGroup getTableFilterBtnGroup() {
        return TableFilterBtnGroup;
    }

    public JButton getBtnAddIssue() {
        return btnAddIssue;
    }

    public JButton getBtnCancelSQL() {
        return btnCancelSQL;
    }

    public JButton getBtnClearAllFilter() {
        return btnClearAllFilter;
    }

    public JButton getBtnCloseSQL() {
        return btnCloseSQL;
    }

    public JButton getBtnEnterSQL() {
        return btnEnterSQL;
    }

    public JButton getBtnRevertChanges() {
        return btnRevertChanges;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }

    public JButton getBtnUploadChanges() {
        return btnUploadChanges;
    }

    public JComboBox getComboBoxForSearch() {
        return comboBoxValue;
    }

    public JComboBox getComboBoxSearch() {
        return comboBoxField;
    }

   

    public JCheckBoxMenuItem getjCheckBoxMenuItem1() {
        return jCheckBoxMenuItem1;
    }

    public JPanel getjPanel1() {
        return jPanel1;
    }

    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    public JScrollPane getjScrollPane2() {
        return jScrollPane2;
    }

   

    public JScrollPane getjScrollPane5() {
        return jScrollPane5;
    }

    public JScrollPane getjScrollPane6() {
        return jScrollPane6;
    }

    public JScrollPane getjScrollPane7() {
        return jScrollPane7;
    }

    public JTextArea getjTextAreaSQL() {
        return jTextAreaSQL;
    }

    public JLabel getLabelEditMode() {
        return labelEditMode;
    }

    public JLabel getLabelRecords() {
        return labelRecords;
    }

    public JLabel getLabelTimeLastUpdate() {
        return labelTimeLastUpdate;
    }

    public JMenu getMenuEdit() {
        return menuEdit;
    }

    public JMenu getMenuFile() {
        return menuFile;
    }

   
    public JMenu getMenuHelp() {
        return menuHelp;
    }

    public JMenuItem getMenuItemAWSAssign() {
        return menuItemAWSAssign;
    }

    public JMenuItem getMenuItemActivateRecord() {
        return menuItemActivateRecord;
    }

    public JMenuItem getMenuItemArchiveRecord() {
        return menuItemArchiveRecord;
    }

    public JMenuItem getMenuItemCompIssues() {
        return menuItemCompIssues;
    }

    public JMenuItem getMenuItemDeleteRecord() {
        return menuItemDeleteRecord;
    }

    public JCheckBoxMenuItem getMenuItemLogChkBx() {
        return menuItemLogChkBx;
    }

    public JMenuItem getMenuItemLogOff() {
        return menuItemLogOff;
    }

    public JMenuItem getMenuItemManageDBs() {
        return menuItemManageDBs;
    }

    public JMenuItem getMenuItemMoveSeletedRowsToEnd() {
        return menuItemMoveSeletedRowsToEnd;
    }

    public JMenuItem getMenuItemPrintDisplay() {
        return menuItemPrintDisplay;
    }

    public JMenuItem getMenuItemPrintGUI() {
        return menuItemPrintGUI;
    }

    public JMenuItem getMenuItemReloadData() {
        return menuItemReloadData;
    }

    public JMenuItem getMenuItemRepBugSugg() {
        return menuItemRepBugSugg;
    }

    public JCheckBoxMenuItem getMenuItemSQLCmdChkBx() {
        return menuItemSQLCmdChkBx;
    }

    public JMenuItem getMenuItemSaveFile() {
        return menuItemSaveFile;
    }

    public JMenuItem getMenuItemTurnEditModeOff() {
        return menuItemTurnEditModeOff;
    }

    public JMenuItem getMenuItemVersion() {
        return menuItemVersion;
    }

    public JMenuItem getMenuItemViewSplashScreen() {
        return menuItemViewSplashScreen;
    }

    public JMenu getMenuPrint() {
        return menuPrint;
    }


    public JMenu getMenuSelectConn() {
        return menuSelectConn;
    }

    public JMenu getMenuTools() {
        return menuTools;
    }

    public JMenu getMenuView() {
        return menuView;
    }

    public JMenuItem getMenuitemViewOneIssue() {
        return menuitemViewOneIssue;
    }

    public JLabel getSearchInformationLabel() {
        return searchInformationLabel;
    }

    public JTabbedPane getTabbedPanel() {
        return tabbedPanel;
    }

    public ShortCutSetting getShortCut() {
        return ShortCut;
    }

    public void setShortCut(ShortCutSetting ShortCut) {
        this.ShortCut = ShortCut;
    }

    public ConsistencyOfTableColumnName getColumnNameConsistency() {
        return ColumnNameConsistency;
    }

    public void setColumnNameConsistency(ConsistencyOfTableColumnName ColumnNameConsistency) {
        this.ColumnNameConsistency = ColumnNameConsistency;
    }

    public boolean isComboBoxStartToSearch() {
        return comboBoxStartToSearch;
    }

    public void setComboBoxStartToSearch(boolean comboBoxStartToSearch) {
        this.comboBoxStartToSearch = comboBoxStartToSearch;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public JMenuItem getMenuItemBackup() {
        return menuItemBackup;
    }

    public void setMenuItemBackup(JMenuItem menuItemBackup) {
        this.menuItemBackup = menuItemBackup;
    }

    public JMenuItem getMenuItemReloadAllData() {
        return menuItemReloadAllData;
    }

    public void setMenuItemReloadAllData(JMenuItem menuItemReloadAllData) {
        this.menuItemReloadAllData = menuItemReloadAllData;
    }

    private void textComponentShortCutSetting() {
        //changing the text field copy and paste short cut to default control key
        //(depend on system) + c/v
        InputMap ip = (InputMap) UIManager.get("TextField.focusInputMap");
        InputMap ip2 = this.jTextAreaSQL.getInputMap();
//        InputMap ip2 = (InputMap) UIManager.get("TextArea.focusInputMap");
        ShortCut.copyAndPasteShortCut(ip);
        ShortCut.copyAndPasteShortCut(ip2);

        // add redo and undo short cut to text component
    }

    public Tab getSelectedTab() {
        return tabs.get(getSelectedTabName());
    }

    /**
     * Updates a table rowIndex's data
     * @param table
     * @param issue 
     */
    public void updateTableRow(JTable table, Issue issue) throws IOException, BadLocationException {
        int row = findTableModelRow(table,issue);
        if(row != -1){
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            // remove table listeners because this listens for changes in 
            // table and changes the cell green for upload changes and revert
            // changes. So I remove them and then put them back.
            TableModelListener[] listeners = model.getTableModelListeners();
            for(int i = 0; i < listeners.length; i++){
                model.removeTableModelListener(listeners[i]);
            }

            // update -> no need for id
            model.setValueAt(issue.getApp(), row, 1);
            model.setValueAt(issue.getTitle(), row, 2);
            
            byte[] descriptiontablebytesout = issue.getDescription();
            InputStream descriptiontablestream = new ByteArrayInputStream(descriptiontablebytesout);
            RTFEditorKit rtfParser = new RTFEditorKit();
            Document document = rtfParser.createDefaultDocument();
            rtfParser.read(descriptiontablestream, document, 0);
            String text = document.getText(0, document.getLength());
            model.setValueAt(text, row, 3);
            
            
            model.setValueAt(issue.getProgrammer(), row, 4);
            model.setValueAt(issue.getDateOpened(), row, 5);
            model.setValueAt(issue.getRk(), row, 6);
            model.setValueAt(issue.getVersion(), row, 7);
            model.setValueAt(issue.getDateClosed(), row, 8);
            model.setValueAt(issue.getIssueType(), row, 9);
            model.setValueAt(issue.getSubmitter(), row, 10);
            model.setValueAt(issue.getLocked(), row, 11);
            
            // add back the table listeners
            for(int i = 0; i < listeners.length; i++){
                model.addTableModelListener(listeners[i]);
            }
            
            table.repaint();
        }
        else{
            String errMsg = "Problem updating row: Row not Found";
            LoggingAspect.afterReturn(errMsg);
        }
    }

    /**
     * Inserts a new rowIndex in the table
     * @param table
     * @param issue 
     */
    public void insertTableRow(JTable table, Issue issue) throws IOException, BadLocationException {
        
       
        Object[] rowData = new Object[13];
        rowData[0] = issue.getId();
        rowData[1] = issue.getApp();
        rowData[2] = issue.getTitle();
        byte[] descriptiontablebytesout;

        if (issue.getDescription() == null) {
            descriptiontablebytesout = new byte[0];
        } else {
            descriptiontablebytesout = issue.getDescription();
        }
        
        InputStream descriptiontablestream = new ByteArrayInputStream(descriptiontablebytesout);
        String convertedstrings = convertStreamToString(descriptiontablestream);
        
        String rtfsign = "\\par";
        boolean rtfornot = convertedstrings.contains(rtfsign);
        
        if (rtfornot) {
            RTFEditorKit rtfParser = new RTFEditorKit();
            Document document = rtfParser.createDefaultDocument();
            rtfParser.read(new ByteArrayInputStream(descriptiontablebytesout), document, 0);
            String text = document.getText(0, document.getLength());
            rowData[3] = text;
        } else {
            rowData[3] = convertedstrings;
        }

        rowData[4] = issue.getProgrammer();
        rowData[5] = issue.getDateOpened();
        rowData[6] = issue.getRk();
        rowData[7] = issue.getVersion();
        rowData[8] = issue.getDateClosed();
        rowData[9] = issue.getIssueType();
        rowData[10] = issue.getSubmitter();
        rowData[11] = issue.getLocked();
        rowData[12] = issue.getLastmodtime();
        ((DefaultTableModel)table.getModel()).addRow(rowData);
        
        tabs.get(table.getName()).addToTotalRowCount(1);
        String recordsLabel = tabs.get(table.getName()).getRecordsLabel();
        labelRecords.setText(recordsLabel);
          
    }
   
    public String convertStreamToString(InputStream is) throws IOException {
        // To convert the InputStream to String we use the
        // Reader.read(char[] buffer) method. We iterate until the
        // Reader return -1 which means there's no more data to
        // read. We use the StringWriter class to produce the string.
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader;
                reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        }
        return "";
    }
    
    
    /**
     * Inserts a new rowIndex in the table
     * @param table
     * @param issueFile
     * @param issue 
     */
    public void insertTableRow(JTable table, IssueFile issueFile) {

        Object[] rowData = new Object[9];
        rowData[0] = issueFile.getFileID();
        rowData[1] = issueFile.getTaskID();
        rowData[2] = issueFile.getApp();
        rowData[3] = issueFile.getSubmitter();
        rowData[4] = issueFile.getStep();
        rowData[5] = issueFile.getDate();
        rowData[6] = issueFile.getFiles();
        rowData[7] = issueFile.getPath();
        rowData[8] = issueFile.getNotes();
        ((DefaultTableModel)table.getModel()).addRow(rowData);
    }

    /**
     * Locates the table model rowIndex index
     * @param issue
     * @return int table model rowIndex
     */
    public int findTableModelRow(JTable table, Issue issue) {
        int rowCount = table.getModel().getRowCount();
        TableModel model = table.getModel();
        for(int rowIndex = 0; rowIndex < rowCount; rowIndex++){
            int rowId = Integer.parseInt(model.getValueAt(rowIndex, 0).toString());
            if(rowId == issue.getId()){
                return rowIndex;
            }
        }
        return -1; // rowIndex not found
    }

    /**
     * removes the selected rows from the table
     * @param table
     * @return 
     */
    private boolean removeSelectedRows(JTable table) {
        
        int[] rows = table.getSelectedRows();
	DefaultTableModel model = (DefaultTableModel)table.getModel();

        if(rows.length != -1){
            while(rows.length>0)
            {
                int row = table.convertRowIndexToModel(rows[0]);
                model.removeRow(row);
                rows = table.getSelectedRows();
            }
            table.getSelectionModel().clearSelection();
            return true;
        }
        else{
            // no rows selected
            return false;
        }
    }
    
    
    
    
    /**
     * Yi
     * for offline data, remove the offline data from table if it gets pushed to server.
     */

    public void removeTableRow(JTable table, int issueId) {

        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int recordCnt = model.getRowCount();
        
        for (int i = recordCnt - 1; i >=0; i--) {
            
            System.out.println(model.getValueAt(i,0).toString());
            System.out.println(model.getValueAt(i,1).toString());
            
            int id = Integer.parseInt(model.getValueAt(i, 0).toString());
            if (id == issueId) {
                model.removeRow(i);
                break;
            }
        }
 
    }
    /**
     * sets the table model with the custom editable table model
     * @param table 
     */
    private void addEditableTableModel(JTable table) {
        
        DefaultTableModel model = (DefaultTableModel)table.getModel();        
        EditableTableModel etm = new EditableTableModel(model);
        table.setModel(etm);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isReconcileWindowShow() {
        return reconcileWindowShow;
    }

    public void setReconcileWindowShow(boolean reconcileWindowShow) {
        this.reconcileWindowShow = reconcileWindowShow;
    }
    
    
    
    
    
    
    /**
     * CLASS
     */
    class AlignmentTableHeaderCellRenderer implements TableCellRenderer {

        private final TableCellRenderer wrappedRenderer;
        private final JLabel label;

        public AlignmentTableHeaderCellRenderer(TableCellRenderer wrappedRenderer) {
            if (!(wrappedRenderer instanceof JLabel)) {
                throw new IllegalArgumentException("The supplied renderer must inherit from JLabel");
            }
            this.wrappedRenderer = wrappedRenderer;
            this.label = (JLabel) wrappedRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            wrappedRenderer.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            

            label.setHorizontalAlignment(column == table.getColumnCount() - 1 ? JLabel.LEFT : JLabel.CENTER);
            return label;

        }

    }

    public JMenuItem getMenuItemReconcileConflict() {
        return menuItemReconcileConflict;
    }

    public void setMenuItemReconcileConflict(JMenuItem menuItemReconcileConflict) {
        this.menuItemReconcileConflict = menuItemReconcileConflict;
    }
 
    /***************************************************************************************************
     * Newly added functions
    ****************************************************************************************************/
     
    /*
    **added by Yi
    **update orange dot issues
    */
    
    private void updateTabOrangeDot(){
        for(int i: tabs.keySet()) {
            //check detect issue
            Tab tab = tabs.get(i);
            if (tab.detectOpenIssues()){
                String title = tab.getTable().getName();
                JLabel titleWithDot = getTabLabelWithOrangeDot(title);
                tabbedPanel.setTabComponentAt(i, titleWithDot);
            };
        }
    }
  
 

}
