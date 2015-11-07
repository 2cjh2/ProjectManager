/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elle.ProjectManager.presentation;

import com.elle.ProjectManager.database.DBConnection;
import com.elle.ProjectManager.database.ModifiedTableData;
import com.elle.ProjectManager.logic.ColumnPopupMenu;
import com.elle.ProjectManager.logic.Tab;
import com.elle.ProjectManager.logic.TableFilter;
import com.elle.ProjectManager.logic.Validator;
import com.elle.ProjectManager.presentation.LogWindow;
import com.elle.ProjectManager.presentation.ProjectManagerWindow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

/**
 *
 * @author fuxiaoqian
 */
public class AddIssueWindow extends JFrame {

    // attributes
    private String[] columnNames;
    private float[] columnWidths;
    private int numRowsAdded;           // number of rows added counter
    private Map<String, Tab> tabs;       // used to update the records label
    private Statement statement;

    private Object[] formValues;

    // components
    private ProjectManagerWindow projectManager;
    private LogWindow logWindow;
    private DefaultTableModel model;
    private int rowInView;

    private Color defaultSelectedBG;

    private ArrayList<Integer> CellsNotEmpty; // only includes rows that have data
    private boolean notEmpty;

//    private PopupWindowInTableCell tableCellPopupWindow;
    private JTable table;

    private Map<String, JLabel> labelsInForm;
    private Map<String, JTextField> textAreasInForm;

//    private ArrayList line;
    // used to notify if the tableSelected is editing
    // the tableSelected.isEditing method has issues from the tableModelListener
    private boolean isEditClicked;

//    private String lastEditColumn = "";
    /**
     * Creates new form AddRecordsWindow
     */
    public AddIssueWindow() {

        CellsNotEmpty = new ArrayList<>();
        notEmpty = false;
        isEditClicked = false;
        formValues = null;

        projectManager = ProjectManagerWindow.getInstance();
        logWindow = projectManager.getLogWindow();
        tabs = projectManager.getTabs();
        statement = projectManager.getStatement();

        projectManager.setAddRecordsWindowShow(true);

        table = new JTable();

//        columnNames = new String[projectManager.getSelectedTable().getColumnCount()];
        // set the selected tableSelected name
        table.setName(projectManager.getSelectedTabName());

        // get default selected bg color
        defaultSelectedBG = table.getSelectionBackground();

        // initialize components
        initComponents();

        // create a new empty tableSelected
        createTable();

        createEmptyForm();

        addIssueMode(true);

        defaultSetting();

//        setKeyboardFocusManager(this);
//        setFormListener();
//        copyPasteAndCut();
//        //sets the keyboard focus manager
//        setKeyboardFocusManager(this);
        // set the label header
        this.setTitle("Add Issue");

        this.setPreferredSize(new Dimension(600, 730));

        // set this window to appear in the middle of Project Manager
        this.setLocationRelativeTo(projectManager);

        this.pack();
    }

    public AddIssueWindow(Object[] cellsValue, int id, int row, String columnName) {
        CellsNotEmpty = new ArrayList<>();
        notEmpty = false;
        isEditClicked = false;

        projectManager = ProjectManagerWindow.getInstance();
        logWindow = projectManager.getLogWindow();
        tabs = projectManager.getTabs();
        statement = projectManager.getStatement();

        projectManager.setAddRecordsWindowShow(true);

        formValues = cellsValue;
        rowInView = row;

        table = new JTable();

        // set the selected tableSelected name
        table.setName(projectManager.getSelectedTabName());

        // get default selected bg color
        defaultSelectedBG = table.getSelectionBackground();

        // initialize components
        initComponents();

        // create a new table with tableSelected values
        createTable();

        createEmptyForm();

        idText.setText(Integer.toString(id));

        this.setTitle("view issue in " + table.getName());

        this.setPreferredSize(new Dimension(600, 760));

        // set this window to appear in the middle of Project Manager
        this.setLocationRelativeTo(projectManager);

        addIssueMode(false);

        if (!columnName.equals("")) {
            buttonEdit.doClick();
            if (columnName.equals("description")) {
                descriptionText.requestFocusInWindow();
            } else {
                for (int i = 0; i < columnNames.length; i++) {
                    if (columnName.equals(columnNames[i])) {
                        textAreasInForm.get(columnNames[i]).requestFocusInWindow();
                        break;
                    }
                }
            }
        } else {
        }

        this.pack();
    }

    private void defaultSetting() {

        JTable currentTable = projectManager.getSelectedTable();

        int idNum = (int) currentTable.getValueAt(currentTable.getRowCount() - 1, 0) + 1;

        //set id to its default value
        idText.setText(Integer.toString(idNum));

        //set dateOpen to today's date
        makeContentDate(dateOpenedText);

    }

    /**
     * createEmptyTable creates an empty tableSelected with default 10 rows
     */
    private void createTable(String appDefaultValue) {
        columnNames = projectManager.getTabs().get(table.getName()).getTableColNames();
        // get tableSelected column width format
        columnWidths = tabs.get(table.getName()).getColWidthPercent();
        // we don't want the ID column 
        columnNames = Arrays.copyOfRange(columnNames, 1, columnNames.length);

        // set the tableSelected model - add 10 empty rows
        model = new DefaultTableModel(columnNames, 1);

        // add the tableSelected model to the tableSelected
        table.setModel(model);

        columnWidths = Arrays.copyOfRange(columnWidths, 1, columnWidths.length);

//        System.out.println(columnWidths.length + "widths");
//
//        System.out.println(table.getColumnCount());
        projectManager.setColumnFormat(columnWidths, table);

        if (formValues == null) {
            formValues = new Object[columnNames.length];
//            System.out.println("here");
        }
        formValues[0] = appDefaultValue;

        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                if (formValues[col] != null) {
                    table.getModel().setValueAt(formValues[col], row, col);
//                    System.out.println("print out column Names here: " + columnNames[col] + " " + formValues[col]);
                }
            }
        }
    }

    /**
     * jSubmitActionPerformed This is performed when the submit button is
     * executed. Refactored by Carlos Igreja 7-28-2015
     *
     * @param evt
     */
    private void createTable() {

        String appDefaultType = projectManager.getAppColumnCurrentType();
        if (appDefaultType.equals(" NULL ")) {
            appDefaultType = "";
        }

        createTable(appDefaultType);

    }

    /**
     * validateCell
     *
     * @param row
     * @param col
     * @return returns true if valid or false if error
     */
    public boolean validateCell(int row, int col) {
//        System.out.println("enter validatecell: " + row + " " + col);

        String colName = table.getColumnName(col);           // column name
        Object cellValue = table.getValueAt(row, col);       // store cell value
        String errorMsg = "Error with " + colName
                + " in row " + (row + 1) + ".\n";            // error message
        boolean error = false;                               // error occurred

        switch (colName) {
//            case
            case "app":
                if (cellValue == null || cellValue.toString().equals("")) {
                    errorMsg += "Symbol cannot be null";
                    error = true;
                }
                break;
            case "title":
                break;
            case "step":
                break;
            case "description":
                break;
            case "instruction":
                break;
            case "programmer":
                break;
            case "rank":
                if (cellValue != null && !cellValue.toString().equals("")) {
                    if (!cellValue.toString().matches("[1-5]{1}")) {
                        errorMsg += "Priority must be an Integer (1-5)";
                        error = true;
                    }
                }
                break;
            case "dateOpened":
                if (cellValue != null && !cellValue.toString().equals("")) {
                    if (!Validator.isValidDate("yyyy-MM-dd", cellValue.toString())) {
                        errorMsg += "Date format not correct: YYYY-MM-DD";
                        error = true;
                    }
                }
                break;
            case "dateClosed":
                if (cellValue != null && !cellValue.toString().equals("")) {
                    if (!Validator.isValidDate("yyyy-MM-dd", cellValue.toString())) {
                        errorMsg += "Date format not correct: YYYY-MM-DD";
                        error = true;
                    }
                }
                break;
            case "date_":
                if (cellValue != null && !cellValue.toString().equals("")) {
                    if (!Validator.isValidDate("yyyy-MM-dd", cellValue.toString())) {
                        errorMsg += "Date format not correct: YYYY-MM-DD";
                        error = true;
                    }
                }
                break;
            case "taskID":
                break;
            case "notes":
                break;
            case "path":
                break;
            case "submitter":
                break;
            default:
                break;

        }// end switch

        if (error) {
            JOptionPane.showMessageDialog(table, errorMsg);
            //btnSubmit.setEnabled(true); 
        }

        return !error;  // if there was an error, return false for failed
    }

    /**
     * validateData Validates all the data in the tableSelected to make sure it
     * is correct. This is used to validate the data before it is executed to
     * the server and the database so that there will not be any errors.
     *
     * @return returns true if the data is all valid and false if the is a
     * single error
     */
    public boolean validateData() {

        int col = 0;                    // column index
        boolean isCellValid = true;    // if cell is valid entry 

//        System.out.println("enter validateData");
        // if tableSelected is not empty
        if (!CellsNotEmpty.isEmpty()) {

//            System.out.println("enter validateData" + " 1 ");
            // check data
            for (int cell : CellsNotEmpty) {

                // if there was an error stop
                if (!isCellValid) {
                    break;
                }

//                for (col = 0; col < table.getColumnCount(); col++) {
                // if there was an error stop
                if (!isCellValid) {
                    break;
                }
                int row = table.getRowCount() - 1;

//                System.out.println("enter validateData " + row);
                // begin error message
                isCellValid = validateCell(row, cell);

//                }// end col for loop
            }// end row for loop

            return isCellValid;
        }

        return false; // tableSelected is empty
    }

    /**
     * checkForEmptyRows This should be used when data is removed or deleted
     */
    private void checkForEmptyRows() {

//        ArrayList<Integer> arrayCopy = new ArrayList(CellsNotEmpty);
        CellsNotEmpty.clear();

        // check List for empty rows
//        for (int row : arrayCopy) {
        int row = table.getRowCount() - 1;
        boolean isNotEmpty = false;
        for (int col = 0; col < table.getColumnCount(); col++) {
//            System.out.println("check For Empty rows (table column count) : " + table.getColumnCount());
            Object value = table.getValueAt(row, col);
//            System.out.println("checkForEmptyRows " + value);
            if (value != null && !value.equals("")) {
                isNotEmpty = true;
//                break;
            } else {
                isNotEmpty = false;
            }
//            }
            if (isNotEmpty) {
                CellsNotEmpty.add(col);
//                System.out.print("cells not empty at " + col);
            }
        }
    }

//     Variables declaration - do not modify 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        formPane = new javax.swing.JPanel();
        app = new javax.swing.JLabel();
        title = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        idText = new javax.swing.JTextField();
        programmer = new javax.swing.JLabel();
        programmerText = new javax.swing.JTextField();
        rk = new javax.swing.JLabel();
        rkText = new javax.swing.JTextField();
        dateOpened = new javax.swing.JLabel();
        dateOpenedText = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        descriptionText = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        buttonCancel = new javax.swing.JButton();
        buttonSubmit = new javax.swing.JButton();
        dateClosed = new javax.swing.JLabel();
        version = new javax.swing.JLabel();
        buttonEdit = new javax.swing.JToggleButton();
        buttonConfirm = new javax.swing.JButton();
        dateClosedText = new javax.swing.JTextField();
        versionText = new javax.swing.JTextField();
        titleText = new javax.swing.JTextField();
        appText = new javax.swing.JTextField();
        description = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        app.setText(" app");

        title.setText(" title");

        id.setText(" id");

        idText.setText("jTextField1");
        idText.setName("id"); // NOI18N
        idText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idTextActionPerformed(evt);
            }
        });

        programmer.setText(" programmer");

        programmerText.setText("jTextField1");
        programmerText.setName("programmer"); // NOI18N
        programmerText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programmerTextActionPerformed(evt);
            }
        });

        rk.setText(" rk");

        rkText.setText("jTextField1");
        rkText.setName("rk"); // NOI18N
        rkText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rkTextActionPerformed(evt);
            }
        });

        dateOpened.setText(" dateOpened");

        dateOpenedText.setText("jTextField1");
        dateOpenedText.setName("dateOpened"); // NOI18N
        dateOpenedText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateOpenedTextActionPerformed(evt);
            }
        });
        dateOpenedText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dateOpenedTextKeyReleased(evt);
            }
        });

        descriptionText.setColumns(20);
        descriptionText.setLineWrap(true);
        descriptionText.setRows(5);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setName("description"); // NOI18N
        jScrollPane7.setViewportView(descriptionText);

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonSubmit.setText("Submit");
        buttonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSubmitActionPerformed(evt);
            }
        });

        dateClosed.setText(" dateClosed");

        version.setText(" version");

        buttonEdit.setText("Edit");
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonConfirm.setText("Confirm");
        buttonConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonConfirmActionPerformed(evt);
            }
        });

        dateClosedText.setText("jTextField2");
        dateClosedText.setName("dateClosed"); // NOI18N
        dateClosedText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateClosedTextActionPerformed(evt);
            }
        });
        dateClosedText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dateClosedTextKeyReleased(evt);
            }
        });

        versionText.setText("jTextField1");
        versionText.setName("version"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonCancel)
                .addGap(4, 4, 4))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateClosedText, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateClosed))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionText, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(version)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateClosed)
                    .addComponent(version))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateClosedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSubmit)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonConfirm)
                    .addComponent(buttonEdit)))
        );

        titleText.setText("jTextField1");
        titleText.setName("title"); // NOI18N
        titleText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleTextActionPerformed(evt);
            }
        });

        appText.setText("jTextField1");
        appText.setName("app"); // NOI18N

        description.setText(" description");

        javax.swing.GroupLayout formPaneLayout = new javax.swing.GroupLayout(formPane);
        formPane.setLayout(formPaneLayout);
        formPaneLayout.setHorizontalGroup(
            formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPaneLayout.createSequentialGroup()
                        .addComponent(titleText)
                        .addContainerGap())
                    .addGroup(formPaneLayout.createSequentialGroup()
                        .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(formPaneLayout.createSequentialGroup()
                                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(id))
                                .addGap(12, 12, 12)
                                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dateOpenedText, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateOpened))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(formPaneLayout.createSequentialGroup()
                                        .addComponent(programmerText, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(rkText, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formPaneLayout.createSequentialGroup()
                                        .addComponent(programmer)
                                        .addGap(60, 60, 60)
                                        .addComponent(rk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(formPaneLayout.createSequentialGroup()
                        .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formPaneLayout.createSequentialGroup()
                                .addComponent(appText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(formPaneLayout.createSequentialGroup()
                                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(app)
                                    .addComponent(description))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(6, 6, 6))))
        );
        formPaneLayout.setVerticalGroup(
            formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dateOpened, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                        .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(programmer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rk, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateOpenedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(programmerText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rkText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(titleText, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(description, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPaneLayout.createSequentialGroup()
                        .addComponent(app)
                        .addGap(0, 0, 0)
                        .addComponent(appText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scrollPane.setViewportView(formPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(scrollPane))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonConfirmActionPerformed
        for (int col = 0; col < formValues.length; col++) {
            if (formValues[col] != null) {

                projectManager.getSelectedTable().setValueAt(formValues[col], rowInView, col + 1);
            }
        }
        this.dispose();
    }//GEN-LAST:event_buttonConfirmActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        AbstractButton abstractButton = (AbstractButton) evt.getSource();
        boolean selected = abstractButton.getModel().isSelected();
        makeCellEditable(selected);
        projectManager.makeTableEditable(selected);
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSubmitActionPerformed

        submit();
    }//GEN-LAST:event_buttonSubmitActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        projectManager.setAddRecordsWindowShow(false);
        projectManager.makeTableEditable(projectManager.getLabelEditModeState().getText().equals("OFF") ? true : false);
        this.dispose();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        projectManager.setAddRecordsWindowShow(false);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void idTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idTextActionPerformed

    private void rkTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rkTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rkTextActionPerformed

    private void programmerTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programmerTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_programmerTextActionPerformed

    private void dateOpenedTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateOpenedTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateOpenedTextActionPerformed

    private void titleTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_titleTextActionPerformed

    private void dateClosedTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateClosedTextActionPerformed
        
    }//GEN-LAST:event_dateClosedTextActionPerformed

    private void dateClosedTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateClosedTextKeyReleased
        if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_D){
            makeContentDate((JTextField)evt.getComponent());
        }
    }//GEN-LAST:event_dateClosedTextKeyReleased

    private void dateOpenedTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateOpenedTextKeyReleased
        if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_D){
            makeContentDate((JTextField)evt.getComponent());
        }
    }//GEN-LAST:event_dateOpenedTextKeyReleased

    /**
     * submit This is used when the submit button is pressed or if the enter key
     * is pressed when the tableSelected is finished editing to submit the data
     * to the database.
     */
    private void submit() {
//        storeValueInTable();
        for (int i = 0; i < table.getColumnCount(); i++) {
//            System.out.println("table cell value at " + i + " " + table.getValueAt(0, i));
        }
        checkForEmptyRows();

        Object cellValue = null;                 // store cell value
        int col = 0;                             // column index
        int row = 0;                             // row index

        // check if data is valid
        if (validateData()) {

            // once data checked, execute sql statement
            // first get the insert statement for the tableSelected
            String insertInto = "INSERT INTO " + table.getName() + " (";

            // this tableSelected should already not include the primary key
            for (col = 0; col < table.getColumnCount(); col++) {
                if (col != table.getColumnCount() - 1) {
                    insertInto += table.getColumnName(col) + ", ";
                } else {
                    insertInto += table.getColumnName(col) + ") ";
                }
            }

//            System.out.println(insertInto);
            numRowsAdded = 0; // reset numRowsAdded counter

            // Now get the values to add to the database
            String values = "";
//            for (row = 0; row < table.getRowCount(); row++) {
            values = "VALUES (";  // start the values statement
//            System.out.println(table.getColumnCount() + "table column number");
            for (col = 0; col < table.getColumnCount(); col++) {

                // get cell value
                cellValue = table.getValueAt(row, col);

                // format the cell value for sql
                if (cellValue != null) {

                    // if cell is empty it must be null
                    if (cellValue.toString().equals("")) {
                        cellValue = null;
                    } // if the cell is not empty it must have single quotes
                    else {
                        cellValue = processCellValue(cellValue);
                        cellValue = "'" + cellValue + "'";
                    }
                }
//                System.out.println("add record submit" + cellValue + "at " + row + " " + col);

//                    // skip empty rows
//                    // this must be after the format cell value so the "" => null
//                    if (col == 0 && cellValue == null) {
//                        break;
//                    }
                // add each value for each column to the values statement
                if (col != table.getColumnCount() - 1) {
                    values += cellValue + ", ";
                } else {
                    values += cellValue + ");";
                }
            }
//            System.out.println(values);

            try {
                // execute the sql statement
                if (!values.equals("VALUES (")) {      //skip if nothing was added
                    // open connection because might time out
                    DBConnection.open();
                    statement = DBConnection.getStatement();
                    statement.executeUpdate(insertInto + values);
                    numRowsAdded++;   // increment the number of rows added
                }
            } catch (SQLException sqlException) {
                try {
                    JOptionPane.showMessageDialog(null, "Upload failed!");

                    if (statement.getWarnings().getMessage() != null) {

                        String levelMessage = "2:" + statement.getWarnings().getMessage();
                        logWindow.addMessageWithDate(levelMessage);

                        projectManager.getInformationLabel().setText(levelMessage);
//                            logWindow.
//                        System.out.println(statement.getWarnings().getMessage());

//                        System.out.println(levelMessage);//delete
                        statement.clearWarnings();
                    }
                    logWindow.addMessageWithDate("2:add record submit failed!");
                } // end try-catch
                catch (SQLException ex) {
                    // this should never be called
                    ex.printStackTrace();
                }
            }
//            }

            this.dispose();

//            System.out.println("numRowsAdded" + numRowsAdded);
            if (numRowsAdded > 0) {
//                timer = new Timer();
                projectManager.getInformationLabel().setVisible(true);
                projectManager.getInformationLabel().setText("submitting to "
                        + projectManager.getSelectedTabName());
                // update tableSelected and records label
                String tabName = projectManager.getSelectedTabName();              // tab name
                Tab tab = tabs.get(tabName);                                  // selected tab

                JTable table = tab.getTable();
                projectManager.loadTable(table);                              // load tableSelected data from database

                // reload new table data for modifiedTableData
                ModifiedTableData data = tab.getTableData();
                data.reloadData();

                TableFilter filter = tab.getFilter();                         // tableSelected filter
                filter.applyFilter();                                         // apply filter
                filter.applyColorHeaders();                                   // apply color headers

                ColumnPopupMenu ColumnPopupMenu = tab.getColumnPopupMenu();   // column popup menu 
                ColumnPopupMenu.loadAllCheckBoxItems();                       // refresh the data for the column pop up

                tab.addToTotalRowCount(numRowsAdded);                         // add the number of records added to the total records count
                JLabel recordsLabel = projectManager.getRecordsLabel();
                String recordsLabelText = tab.getRecordsLabel();              // store the records label string
                recordsLabel.setText(recordsLabelText);                       // update the records label text

                projectManager.setLastUpdateTime();                                // set the last update time from database

//                JOptionPane.showMessageDialog(this,
//                        numRowsAdded + " Add successfully!");                 // show dialog box that upload was successful
                formValues = null;

                projectManager.getInformationLabel().setText("Add successfully to "
                        + projectManager.getSelectedTabName());
//                Timer time = new Timer(10000, new ActionListener() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                       projectManager.getInformationLabel().setText("");
//                    }
//                });
//                time.start();
////                createTable();                                           // create a new empty tableSelected with default 10 rows
////                resetForm();
            }
        }
    }

    private void resetForm() {
        for (int col = 0; col < columnNames.length; col++) {
            if (columnNames[col].equals("description")) {

                if (formValues[col] != null) {
                    descriptionText.setText(formValues[col].toString());
                } else {
                    descriptionText.setText("");
                }
            } else {
                if (formValues[col] != null) {
                    textAreasInForm.get(columnNames[col]).setText(formValues[col].toString());
                } else {
                    textAreasInForm.get(columnNames[col]).setText("");
                }
            }
        }
        defaultSetting();
        CellsNotEmpty.clear();
    }

    private void storeValueInTable() {

        for (int i = 0; i < columnNames[i].length(); i++) {
            String labelName = columnNames[i];
            if (labelName.equals(table.getColumnName(i))) {
                if (!labelName.equals("description")) {
                    table.setValueAt(textAreasInForm.get(labelName).getText(), 0, i);
                } else {
                    table.setValueAt(descriptionText.getText(), 0, i);
                }
            }
        }
    }

    private void makeContentDate(JTextField dateArea) {

        dateArea.requestFocusInWindow();
        dateArea.selectAll();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = dateFormat.format(date);
        dateArea.setText(today);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel app;
    private javax.swing.JTextField appText;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonConfirm;
    private javax.swing.JToggleButton buttonEdit;
    private javax.swing.JButton buttonSubmit;
    private javax.swing.JLabel dateClosed;
    private javax.swing.JTextField dateClosedText;
    private javax.swing.JLabel dateOpened;
    private javax.swing.JTextField dateOpenedText;
    private javax.swing.JLabel description;
    private javax.swing.JTextArea descriptionText;
    private javax.swing.JPanel formPane;
    private javax.swing.JLabel id;
    private javax.swing.JTextField idText;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel programmer;
    private javax.swing.JTextField programmerText;
    private javax.swing.JLabel rk;
    private javax.swing.JTextField rkText;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel title;
    private javax.swing.JTextField titleText;
    private javax.swing.JLabel version;
    private javax.swing.JTextField versionText;
    // End of variables declaration//GEN-END:variables

    private void createEmptyForm() {
//        labelsInForm = new HashMap<String, JLabel>();
        textAreasInForm = new HashMap<String, JTextField>();

        JTextField textAreaArray[] = new JTextField[8];
        textAreaArray[0] = appText;
        textAreaArray[1] = (dateOpenedText);
//        textAreaArray[2] = (descriptionText);
        textAreaArray[2] = (programmerText);
        textAreaArray[3] = (idText);
        textAreaArray[4] = (titleText);
        textAreaArray[5] = (rkText);
        textAreaArray[6] = (dateClosedText);
        textAreaArray[7] = (versionText);
        String areaName = "";
//        System.out.println(columnNames.length + " hei");
        for (int i = 0; i < columnNames.length; i++) {
            Object tableValue = formValues[i];
            System.out.println("here comes " + columnNames[i]);
            if (columnNames[i].equals("description")) {
                System.out.println("at " + columnNames[i] + " add " + areaName + " and value is " + tableValue);
                if (tableValue != null) {
                    descriptionText.setText(tableValue.toString());
                } else {
                    descriptionText.setText("");
                }
            } else {
                for (int j = 0; j < textAreaArray.length; j++) {
                    areaName = textAreaArray[j].getName();
//                Object tableValue = formValues[i];
//                System.out.println("at " + columnNames[i] + " add " + areaName + " and value is " + tableValue);
//                if (columnNames[i].equals("description")) {
//                    System.out.print(columnNames[i]);
//                    if (tableValue != null) {
//                        descriptionText.setText(tableValue.toString());
//                    } else {
//                        descriptionText.setText("");
//                    }
//                } else {
                    System.out.println("area Name now " + areaName);
                    if (areaName.equals(columnNames[i])) {

                        System.out.println("here textarea name: " + areaName);
                        System.out.println(columnNames[i] + " put in ");
                        textAreasInForm.put(columnNames[i], textAreaArray[j]);

                        if (tableValue != null) {
                            textAreasInForm.get(columnNames[i]).setText(tableValue.toString());
                        } else {
                            textAreasInForm.get(columnNames[i]).setText("");
                        }

                        formValues[i] = null;
                        break;
                    }
                }
            }
        }

        idText.setEditable(false);

        setDocumentListener();

        setTabKeyTransferFocusBtwTextArea();
    }

    private void setDocumentListener() {
        DocumentListener textDocumentLis = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {

                String columnName = (String) e.getDocument().getProperty("id");
                String value = "";
                if (!columnName.equals("description")) {
                    value = textAreasInForm.get(columnName).getText();
                } else {
                    value = descriptionText.getText();
                }
                updateValueToTableAt(value, columnName);
//                try {
////                    System.out.println("insert: " + e.getDocument().getText(0,
////                            e.getDocument().getLength()) + "in " + e.getDocument().getProperty("id"));
////                    for (int i = 0; i < columnNames.length; i++) {
////                        if (textAreasInForm.get(columnNames[i]).getText().equals("")) {
////                            notEmpty = false;
////
////                        }
////                    }
//                } catch (BadLocationException ex) {
//                    Logger.getLogger(AddIssueWindow.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }

//                if (!lastEditColumn.equals(columnName)) {
//                    if (lastEditColumn.equals("")) {
//                        lastEditColumn = columnName;
//                    } else {
//                        String value = textAreasInForm.get(lastEditColumn).getText();
//                        System.out.println(lastEditColumn + " " + value);
//                        addValueToTableAt(value, lastEditColumn);
//                        lastEditColumn = columnName;
//                    }
//                }
//                notEmpty = true;
//                if (!lastEditColumn.equals(columnName)) {
//                    if (lastEditColumn.equals("")) {
//                        lastEditColumn = columnName;
//                    } else {
//                        String value = textAreasInForm.get(lastEditColumn).getText();
//                        System.out.println(lastEditColumn + " " + value);
//                        addValueToTableAt(value, lastEditColumn);
//                        lastEditColumn = columnName;
//                    }
//                }
//                notEmpty = true;
//                if (!lastEditColumn.equals(columnName)) {
//                    if (lastEditColumn.equals("")) {
//                        lastEditColumn = columnName;
//                    } else {
//                        String value = textAreasInForm.get(lastEditColumn).getText();
//                        System.out.println(lastEditColumn + " " + value);
//                        addValueToTableAt(value, lastEditColumn);
//                        lastEditColumn = columnName;
//                    }
//                }
//                notEmpty = true;
//                if (!lastEditColumn.equals(columnName)) {
//                    if (lastEditColumn.equals("")) {
//                        lastEditColumn = columnName;
//                    } else {
//                        String value = textAreasInForm.get(lastEditColumn).getText();
//                        System.out.println(lastEditColumn + " " + value);
//                        addValueToTableAt(value, lastEditColumn);
//                        lastEditColumn = columnName;
//                    }
//                }
//                notEmpty = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String columnName = (String) e.getDocument().getProperty("id");
                String value = "";
                if (!columnName.equals("description")) {
                    value = textAreasInForm.get(columnName).getText();
                } else {
                    value = descriptionText.getText();
                }
                updateValueToTableAt(value, columnName);
//                try {
//                    System.out.println("remove: " + e.getDocument().getText(0,
//                            e.getDocument().getLength()) + "in " + e.getDocument().getProperty("id"));
////                    for (int i = 0; i < columnNames.length; i++) {
////                        if (textAreasInForm.get(columnNames[i]).getText().equals("")) {
////                            notEmpty = false;
////
////                        }
////                    }
//                } catch (BadLocationException ex) {
//                    Logger.getLogger(AddIssueWindow.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                try {
//                    System.out.println("changed: " + e.getDocument().getText(0,
//                            e.getDocument().getLength()) + "in " + e.getDocument().getProperty("id"));
//
//                } catch (BadLocationException ex) {
//                    Logger.getLogger(AddIssueWindow.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
            }

        };
        for (int i = 0; i < columnNames.length; i++) {
            Document doc;
            if (columnNames[i].equals("description")) {
                doc = descriptionText.getDocument();
            } else {
                doc = textAreasInForm.get(columnNames[i]).getDocument();
            }
            doc.addDocumentListener(textDocumentLis);
            doc.putProperty("id", columnNames[i]);
        }

    }

    private void setTabKeyTransferFocusBtwTextArea() {
        AbstractAction transferFocus = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ((Component) e.getSource()).transferFocus();
            }
        };

        for (int i = 0; i < columnNames.length; i++) {
            if (!columnNames[i].equals("description")) {
                textAreasInForm.get(columnNames[i]).getInputMap().put(KeyStroke.
                        getKeyStroke("TAB"), "transferFocus");
                textAreasInForm.get(columnNames[i]).getActionMap().put("transferFocus", transferFocus);
            }
        }
    }

    private void updateValueToTableAt(String value, String columnName) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            if (table.getColumnName(column).equals(columnName)) {
                table.setValueAt(value, 0, column);
//                System.out.println("table get Value: " + table.getValueAt(0, column) + "at " + column);
//                checkForEmptyRows();

//                System.out.println("formValue old value at " + column + " is " + formValues[column]);
                formValues[column] = value;
//                System.out.println("formValue update at " + column + " " + formValues[column]);
            }
        }
    }

    public void setEditable(boolean b) {
        makeCellEditable(b);
    }

    private void makeCellEditable(boolean b) {
        for (int col = 0; col < columnNames.length; col++) {
            if (!columnNames[col].equals("taskID") && !columnNames[col].equals("description")) {
                textAreasInForm.get(columnNames[col]).setEditable(b);
            }
        }
        descriptionText.setEditable(b);
        buttonConfirm.setEnabled(b);
        buttonCancel.setEnabled(b);
        buttonConfirm.setVisible(b);
        buttonCancel.setVisible(b);
    }

    private void addIssueMode(boolean b) {
        dateClosedText.setEnabled(!b);
        dateClosedText.setVisible(!b);
        dateClosed.setVisible(!b);

        versionText.setEnabled(!b);
        versionText.setVisible(!b);
        version.setVisible(!b);

        buttonEdit.setEnabled(!b);
        buttonEdit.setVisible(!b);

        buttonConfirm.setEnabled(!b);
        buttonConfirm.setVisible(!b);

        buttonSubmit.setEnabled(b);
        buttonSubmit.setVisible(b);

    }

    private Object processCellValue(Object cellValue) {

        return cellValue.toString().replaceAll("'", "''");
    }
//    // find the sepecfic textArea in Form
//    private JTextArea findColumnInForm(String columnName) {
//        for (int i = 0; i < columnNames.length; i++) {
//            if(columnName.equals("description")){
//                return descriptionText;
//            }
//            if (columnNames[i].contains(columnName)) {
//
//                return textAreasInForm.get(columnNames[i]);
//            }
//        }
//        return null;
//    }

//    private void setFormListener() {
//        header.addMouseListener(new MouseAdapter() {
//    }
//    private void copyPasteAndCut() {
//        InputMap imap = this.getIn
//    }
//    private void addKeyListener() {
//        KeyListener textKeyListener = new KeyListener() {
//
//            @Override
//            public void keyTyped(KeyEvent event) {
//                System.out.println("key typed");
//            }
//
//            @Override
//            public void keyReleased(KeyEvent event) {
//                System.out.println("key released");
//            }
//
//            @Override
//            public void keyPressed(KeyEvent event) {
//                System.out.println("key pressed");
//            }
//        };
//        for (int i = 0; i < columnNames.length; i++) {
//            System.out.println(columnNames[i]);
//            textAreasInForm.get(columnNames[i]).setA
//            Document doc = textAreasInForm.get(columnNames[i]).getDocument();
//            doc.addKeyListener(textKeyListener);
//            doc.putProperty("id", columnNames[i]);
//            System.out.println("add!");
//        }
//
//    }
//    private void addMouseListener() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    //
//        // add listeners
//        addTableListeners(this);
//
//        // submit button does not start enabled because the tableSelected is empty
//        btnSubmit.setEnabled(false);
//
//        // set the label header
//        this.setTitle("Add Records to " + table.getName());
//
//        Dimension scrollPanelDimension = scrollpane.getPreferredSize();
//
//        // set the size for AddRecord window
//        this.setPreferredSize(new Dimension((int) scrollPanelDimension.getWidth(),
//                (int) (scrollPanelDimension.getHeight() + 80)));
//        this.setMinimumSize(new Dimension((int) scrollPanelDimension.getWidth(), 120));
//
////        if (!tableCellPopupWindow.isPopupWindowShow(isEditing)) {
////            tableCellPopupWindow.setTableListener(table, this);
////        }
//        // set this window to appear in the middle of Project Manager
//        this.setLocationRelativeTo(projectManager);
//
//        this.addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
//                projectManager.setAddRecordsWindowShow(false);
//
//                projectManager.setDisableProjecetManagerFunction(true);
//
//            }
//        });
////        this.pack();
//        System.out.println("add record window create!");
//    }
//
//    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {
//
//        projectManager.setDisableProjecetManagerFunction(true);
//        submit();
//    }
//
//    /**
//     * submit This is used when the submit button is pressed or if the enter key
//     * is pressed when the tableSelected is finished editing to submit the data
//     * to the database.
//     */
//    private void submit() {
//
//        Object cellValue = null;                 // store cell value
//        int col = 0;                             // column index
//        int row = 0;                             // row index
//
//        // check if data is valid
//        if (validateData()) {
//
//            // once data checked, execute sql statement
//            // first get the insert statement for the tableSelected
//            String insertInto = "INSERT INTO " + table.getName() + " (";
//
//            // this tableSelected should already not include the primary key
//            for (col = 0; col < table.getColumnCount(); col++) {
//                if (col != table.getColumnCount() - 1) {
//                    insertInto += table.getColumnName(col) + ", ";
//                } else {
//                    insertInto += table.getColumnName(col) + ") ";
//                }
//            }
//
//            numRowsAdded = 0; // reset numRowsAdded counter
//
//            // Now get the values to add to the database
//            String values = "";
//            for (row = 0; row < table.getRowCount(); row++) {
//                values = "VALUES (";  // start the values statement
//                System.out.println(table.getColumnCount() + "table column number");
//                for (col = 0; col < table.getColumnCount(); col++) {
//
//                    // get cell value
//                    cellValue = table.getValueAt(row, col);
//
//                    // format the cell value for sql
//                    if (cellValue != null) {
//
//                        // if cell is empty it must be null
//                        if (cellValue.toString().equals("")) {
//                            cellValue = null;
//                        } // if the cell is not empty it must have single quotes
//                        else {
//                            cellValue = "'" + cellValue + "'";
//                        }
//                    }
//                    System.out.println("add record submit" + cellValue + "at " + row + " " + col);
//
//                    // skip empty rows
//                    // this must be after the format cell value so the "" => null
//                    if (col == 0 && cellValue == null) {
//                        break;
//                    }
//
//                    // add each value for each column to the values statement
//                    if (col != table.getColumnCount() - 1) {
//                        values += cellValue + ", ";
//                    } else {
//                        values += cellValue + ");";
//                    }
//                }
//                System.out.println(values);
//
//                try {
//                    // execute the sql statement
//                    if (!values.equals("VALUES (")) {      //skip if nothing was added
//                        // open connection because might time out
//                        DBConnection.open();
//                        statement = DBConnection.getStatement();
//                        statement.executeUpdate(insertInto + values);
//                        numRowsAdded++;   // increment the number of rows added
//                    }
//                } catch (SQLException sqlException) {
//                    try {
//                        JOptionPane.showMessageDialog(null, "Upload failed!");
//
//                        if (statement.getWarnings().getMessage() != null) {
//
//                            String levelMessage = "2:" + statement.getWarnings().getMessage();
//                            logWindow.addMessageWithDate(levelMessage);
////                            logWindow.
//                            System.out.println(statement.getWarnings().getMessage());
//
//                            System.out.println(levelMessage);//delete
//
//                            statement.clearWarnings();
//                        }
//                        logWindow.addMessageWithDate("2:add record submit failed!");
//                    } // end try-catch
//                    catch (SQLException ex) {
//                        // this should never be called
//                        ex.printStackTrace();
//                    }
//                }
//            }
//
//            System.out.println("numRowsAdded" + numRowsAdded);
//
//            if (numRowsAdded > 0) {
//                // update tableSelected and records label
//                String tabName = projectManager.getSelectedTabName();              // tab name
//                Tab tab = tabs.get(tabName);                                  // selected tab
//
//                JTable table = tab.getTable();
//                projectManager.loadTable(table);                              // load tableSelected data from database
//
//                // reload new table data for modifiedTableData
//                ModifiedTableData data = tab.getTableData();
//                data.reloadData();
//
//                TableFilter filter = tab.getFilter();                         // tableSelected filter
//                filter.applyFilter();                                         // apply filter
//                filter.applyColorHeaders();                                   // apply color headers
//
//                ColumnPopupMenu ColumnPopupMenu = tab.getColumnPopupMenu();   // column popup menu 
//                ColumnPopupMenu.loadAllCheckBoxItems();                       // refresh the data for the column pop up
//
//                tab.addToTotalRowCount(numRowsAdded);                         // add the number of records added to the total records count
//                JLabel recordsLabel = projectManager.getRecordsLabel();
//                String recordsLabelText = tab.getRecordsLabel();              // store the records label string
//                recordsLabel.setText(recordsLabelText);                       // update the records label text
//
//                projectManager.setLastUpdateTime();                                // set the last update time from database
//
//                JOptionPane.showMessageDialog(this,
//                        numRowsAdded + " Add successfully!");                 // show dialog box that upload was successful
//                createEmptyTable();                                           // create a new empty tableSelected with default 10 rows
//            }
//        }
//    }
//
//    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
//
//        projectManager.setAddRecordsWindowShow(false);
//        projectManager.setDisableProjecetManagerFunction(true);
//        tableCellPopupWindow.windowClose();
//        this.dispose();
//    }
//
//    // add an empty row to the tableSelected
////        model.addRow(new Object[]{});
    /**
     * setKeyboardFocusManager Sets the Keyboard Focus Manager
     */
//    private void setKeyboardFocusManager(JFrame frame) {
//
//        /*
//         No Tab key-pressed or key-released events are received by the key event listener. This is because the focus subsystem 
//         consumes focus traversal keys, such as Tab and Shift Tab. To solve this, apply the following to the component that is 
//         firing the key events 
//         */
//        table.setFocusTraversalKeysEnabled(false);
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {// Allow to TAB-
////
//            @Override
//            public boolean dispatchKeyEvent(KeyEvent e) {
//
//                if (e.getComponent() instanceof JTextArea) {
//                    if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {
//                        JTextArea dateArea = new JTextArea();
//                        for (int i = 0; i < columnNames.length; i++) {
//                            if (columnNames[i].contains("date")) {
//                                dateArea = textAreasInForm.get(columnNames[i]);
//                                makeContentDate(dateArea);
//                            }
//                        }
//
//                    }
//                }
//                return false;
//            }
//
//            private void makeContentDate(JTextArea dateArea) {
//
//                dateArea.requestFocusInWindow();
//                dateArea.selectAll();
//                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Date date = new Date();
//                String today = dateFormat.format(date);
//                dateArea.setText(today);
//            }
//
//        });
//    }
//                    if (e.getKeyCode() == KeyEvent.VK_TAB && e.isControlDown()) {
//                        System.out.println(activeTextArea.getText());
//                    }
//                }
//                return false;
//
//            }
//        });
//    }
//
//                if (e.getComponent() instanceof JTable) {
//
//                    // this is called to either clear data or submit data
//                    if (e.getKeyCode() == KeyEvent.VK_ENTER && !table.isEditing()) {
//
//                        // clear the row(s)
//                        if (e.getID() == KeyEvent.KEY_PRESSED) {
//                            if (table.getSelectionBackground() == Color.RED) {
//                                int[] rows = table.getSelectedRows();
//
//                                if (rows != null) {
//                                    for (int row : rows) {
//                                        for (int col = 0; col < table.getColumnCount(); col++) {
//                                            table.getModel().setValueAt("", row, col);
//                                        }
//                                    }
//                                }
//                                table.setSelectionBackground(defaultSelectedBG);
//
//                                // check for empty rows/table
//                                checkForEmptyRows();
//                                if (rowsNotEmpty.isEmpty()) {
//                                    btnSubmit.setEnabled(false);
//                                } else {
//                                    btnSubmit.setEnabled(true);
//                                }
//                            } // submit the data
//                            else if (table.getSelectionBackground() != Color.RED) {
//                                submit();
//                            }
//                        }
//                    } // this toggles the red bg for clearing row data
//                    else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
//
//                        if (e.getID() == KeyEvent.KEY_RELEASED) {
//                            if (table.isEditing()) {
//                                table.getCellEditor().stopCellEditing();
//                            }
//
//                            if (table.getSelectionBackground() == defaultSelectedBG) {
//                                table.setSelectionBackground(Color.RED);
//                            } else {
//                                table.setSelectionBackground(defaultSelectedBG);
//                            }
//                        }
//                    } // this is to tab and move to cells with arrow keys
//                    else if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_LEFT
//                            || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP
//                            || e.getKeyCode() == KeyEvent.VK_DOWN) {
//
//                        JTable tableSelected = (JTable) e.getComponent();
//
//                        if (e.getID() == KeyEvent.KEY_RELEASED) {
//                            System.out.println("add records tabs!");
//                            //if table get selected location is not the same as last selection
//                            if (tableSelected.getSelectedRow() != lastSelectedRow
//                                    || tableSelected.getSelectedColumn() != lastSelectedColumn) {
//
//                                if (lastSelectedRow == -1 || lastSelectedColumn == -1) {
//                                    lastSelectedRow = tableSelected.getSelectedRow();
//                                    lastSelectedColumn = tableSelected.getSelectedColumn();
//                                    tableCellPopupWindow = new PopupWindowInTableCell(frame, tableSelected);
//                                } else {
//                                    tableCellPopupWindow.windowClose();
//                                    tableCellPopupWindow = new PopupWindowInTableCell(frame, tableSelected);
//                                    lastSelectedRow = tableSelected.getSelectedRow();
//                                    lastSelectedColumn = tableSelected.getSelectedColumn();
//                                }// last popup window dispose and new popup window show at the selected cell
//                            }
////                        } else if (e.getID() == KeyEvent.KEY_PRESSED) {
////                            if (selectedCol == tableSelected.getColumnCount() - 1) {
////
////                                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
////
////                                tableModel.addRow(new Object[]{});
////                            }
//
//                        } else {
//
//                        }
//                    }
//
//                } // end table component condition
//                // ctrl + D fills in the current date
//                else if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {
//                    JTable table = (JTable) e.getComponent().getParent();
//                    int column = table.getSelectedColumn();
//                    if (table.getColumnName(column).toLowerCase().contains("date")) {
//                        if (e.getID() != 401) {
//                            return false;
//                        } else {
//                            JTextField selectCom = (JTextField) e.getComponent();
//                            selectCom.requestFocusInWindow();
//                            selectCom.selectAll();
//                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            Date date = new Date();
//                            String today = dateFormat.format(date);
//                            selectCom.setText(today);
//                        }// default date input with today's date}
//                    }
//                }
//
//                return false;
//            }
//        });
//    }
//
//
//    /**
//     * addTableListeners This is called to add the listeners to the
//     * tableSelected The listeners added are the TableModel listener the
//     * MouseListener and the KeyListener
//     */
//    public void addTableListeners(JFrame frame) {
//
//        // add tableModelListener
//        table.getModel().addTableModelListener(new TableModelListener() {
//
//            @Override
//            public void tableChanged(TableModelEvent e) {
//
//                // isEditing is a class boolean triggered true on double click
//                if (!isEditing) {
//                    // if clearing row then do not validate
//                    if (table.getSelectionBackground() != Color.RED) {
//
//                        // check the cell for valid entry
//                        int row = e.getLastRow();            // row index
//                        int col = e.getColumn();             // column index
//
//                        System.out.println("tableChanged at: " + row + " " + col);
//                        validateCell(row, col);
//                    }
//
//                    // get value of cell
//                    int row = e.getFirstRow();
//                    int col = e.getColumn();
//                    Object value = table.getValueAt(row, col);
//
//                    // if cell value is empty
//                    if (value == null || value.equals("")) {
//                        // check to see if it was a deletion
//                        if (!rowsNotEmpty.isEmpty() && rowsNotEmpty.contains(row)) {
//                            checkForEmptyRows();
//                        }
//                    } // else add the row to the list as not empty
//                    else {
//                        rowsNotEmpty.add(row);
//                    }
//
//                    // if list is empty then the tableSelected is empty
//                    if (!rowsNotEmpty.isEmpty() && !isEditing) {
//                        btnSubmit.setEnabled(true);
//                    }
//                }
//
//                // reset isEditing boolean
//                isEditing = false;
//            }
//
//        });
//
//        // add mouseListener
//        table.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//
//                if (e.getClickCount() == 1) {
//                    // if we click away the red delete should go away
//                    if (table.getSelectionBackground() == Color.RED && !e.isControlDown()) {
//                        table.setSelectionBackground(defaultSelectedBG);
//                    }
//                } // this enters edit mode
//                else if (e.getClickCount() == 2) {
//                    btnSubmit.setEnabled(false);
//                    isEditing = true;
//                    selectAllText(e);
//                }
//            }
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
//                // if left mouse clicks
//                if (SwingUtilities.isLeftMouseButton(e)) {
//                    if (e.getClickCount() == 1) {
//
//                        //if table get selected location is not the same as last selection
//                        if (table.getSelectedRow() != lastSelectedRow
//                                || table.getSelectedColumn() != lastSelectedColumn) {
//
//                            if (lastSelectedRow == -1 || lastSelectedColumn == -1) {
//                                lastSelectedRow = table.getSelectedRow();
//                                lastSelectedColumn = table.getSelectedColumn();
//                                tableCellPopupWindow = new PopupWindowInTableCell(frame, table);
//                            } else {
//                                tableCellPopupWindow.windowClose();
//                                tableCellPopupWindow = new PopupWindowInTableCell(frame, table);
//                                lastSelectedRow = table.getSelectedRow();
//                                lastSelectedColumn = table.getSelectedColumn();
//                            }// last popup window dispose and new popup window show at the selected cell
//                        } else {
//                            //if current selection equals last selection nothing happens
//                        }
//                    }
//                } // end if left mouse clicks
//            }
//        });
//    }
//
//    /**
//     * selectAllText Select all text inside jTextField or a cell
//     *
//     * @param e
//     */
//    private void selectAllText(MouseEvent e) {
//
//        JTable table = (JTable) e.getComponent();
//        int row = table.getSelectedRow();
//        int column = table.getSelectedColumn();
//        if (column != -1) {
//            table.getComponentAt(row, column).requestFocus();
//            table.editCellAt(row, column);
//            JTextField selectCom = (JTextField) table.getEditorComponent();
//            if (selectCom != null) {
//                selectCom.requestFocusInWindow();
//                selectCom.selectAll();
//            }
//        }
//    }
//
    //    private void createForm() {
//        createEmptyForm();
//        for (int col = 0; col < formValues.length; col++) {
//            System.out.println(col + " " + formValues[col] + " !");
//            Object tableValue = formValues[col];
//            if (tableValue != null) {
//                textAreasInForm.get(columnNames[col]).setText(tableValue.toString());
//            } else {
//                textAreasInForm.get(columnNames[col]).setText(null);
//            }
//            idText.setEditable(false);
//        }
//    }
}
