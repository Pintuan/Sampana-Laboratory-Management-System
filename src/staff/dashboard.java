/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package staff;

import PortConfigurator.PortConfig;
import java.awt.Desktop;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import main.Connection;
import main.newTest;
import main.uploadSendoutResult;
import sampanalims.LoginPage;

/**
 *
 * @author Almar Dave
 */
public class dashboard extends javax.swing.JFrame {

    Connection con = new Connection();
    String accountid = "";
    newTest nt;
    updateValue uv;
    PortConfig pc = new PortConfig();
    uploadSendoutResult usr;
    JPopupMenu popupMenu = new JPopupMenu();

    /**
     * Creates new form dashboard
     */
    public dashboard() {
        initComponents();
        setLocationRelativeTo(null);
        tblResults.setComponentPopupMenu(popupMenu);
    }

    public dashboard(String AccountNumber) {
        initComponents();
        this.accountid = AccountNumber;
        setLocationRelativeTo(null);
        ResultSet rs = con.getData("select * from users u inner join position p on u.pos = p.PosId where licenseId = " + AccountNumber);
        try {
            while (rs.next()) {
                lblUserFullname.setText(rs.getString("Fname") + " " + rs.getString("LName"));
                lblUserPosition.setText(rs.getString("position"));
            }
        } catch (SQLException ex) {
            JOptionPane.showInternalMessageDialog(null, ex);
        }

        ImageIcon icon = new ImageIcon("C:\\Sampana\\Pictures\\SCL_Logo.png");
        this.setIconImage(icon.getImage());
        clock();
        refreshTables();
        initPopup();
    }

    public final void initPopup() {
        JMenuItem view = new JMenuItem("view");
        JMenuItem edit = new JMenuItem("edit");

        view.addActionListener((ActionEvent e) -> {
            String id = String.valueOf(tblResults.getValueAt(tblResults.getSelectedRow(), 0));
            String date = String.valueOf(tblResults.getValueAt(tblResults.getSelectedRow(), 2));
            try {
                File pdf = new File("C:\\SampanaLIMS\\" + date + "\\" + id + "\\" + id + ".pdf");
                File sendout = new File("C:\\SampanaLIMS\\" + date + "\\" + id + "\\" + id + "_SendoutResult.pdf");
                if (sendout.exists()) {
                    Desktop.getDesktop().open(sendout);
                }
                Desktop.getDesktop().open(pdf);
            } catch (IOException ex) {
                System.out.println(ex);
            }
            popupMenu.setVisible(false);
        });
        edit.addActionListener((ActionEvent e) -> {
            if (uv == null || !uv.isVisible()) {
            String x = tblResults.getValueAt(tblResults.getSelectedRow(), 0).toString();
            uv = new updateValue(x);
            uv.setLocationRelativeTo(null);
            uv.setVisible(true);
        } else {
            uv.toFront();
        }
           popupMenu.setVisible(false);
        });
        popupMenu.add(view);
        popupMenu.add(edit);
    }

    private void clock() {
        Thread clock = new Thread() {
            public void run() {
                for (;;) {
                    try {
                        Calendar cal = new GregorianCalendar();
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int year = cal.get(Calendar.YEAR);
                        int ampm = cal.get(Calendar.AM_PM);
                        int hour = cal.get(Calendar.HOUR);
                        int min = cal.get(Calendar.MINUTE);
                        lblDate.setText(month + 1 + "/" + day + "/" + year);
                        lblTime.setText(hour + ":" + min + " " + ((ampm == 1) ? "PM" : "AM"));
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(dashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        clock.start();

    }

    private void refreshTables() {
        Thread refresh = new Thread() {
            public void run() {
                for (;;) {
                    try {
                        String name = txtPendingTestsSearch.getText();
                        String pname = txtpPatientSearch.getText();
                        String resname = txtResultSearch.getText();
                        String sname = txtWaitingSendoutSearch.getText();
                        
                        String[] testsHeader = {"Receipt Number", "Patient Name", "Test Taken", "Total Ammount"};
                        String[] patientHeader = {"Patient Number", "Full Name", "Date of Birth", "Sex"};
                        String[] resultHeader = {"Receipt Number", "Full Name", "Date of Visit", "Test Taken"};
                        tblPendingResults.setModel(getTable(testsHeader, "select r.receiptId, p.fullname, STRING_AGG(t.test, ',') as 'test taken', sum(t.price) as 'Total Price'  from receipt r inner join patient p on r.patientId = p.patientId inner join test_taken tk on r.receiptId = tk.receiptId inner join test t on tk.testId = t.testId where p.fullname like '%"+name+"%' and r.stat = 'pending' and price != 0 group by r.receiptId, p.fullname"));
                        tblResults.setModel(getTable(resultHeader, "select r.receiptId, p.fullname, r.receiptdate, STRING_AGG(t.test, ',') as 'test taken'  from receipt r inner join patient p on r.patientId = p.patientId inner join test_taken tk on r.receiptId = tk.receiptId inner join test t on tk.testId = t.testId where p.fullname like '%"+resname+"%' and r.stat = 'done' or r.stat = 'waiting' and price != 0 group by r.receiptId, p.fullname, r.receiptdate"));
                        tblPatients.setModel(getTable(patientHeader, "SELECT patientId, fullname,   bdate,  IIF(sex = 0 or sex = 1, 'Male', 'Female') as sex FROM patient where fullname like '%"+pname+"%'"));
                        tblPendingSendout.setModel(getTable(testsHeader, "select r.receiptId, p.fullname, STRING_AGG(t.test, ',') as 'test taken', sum(t.price) as 'Total Price'  from receipt r inner join patient p on r.patientId = p.patientId inner join test_taken tk on r.receiptId = tk.receiptId inner join test t on tk.testId = t.testId where p.fullname like '%"+sname+"%' and r.stat = 'waiting' and price != 0 group by r.receiptId, p.fullname"));
                        sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(dashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    lblPending.setText(String.valueOf(con.rowCount("SELECT * FROM receipt where stat = 'pending'")));
                    lblDone.setText(String.valueOf(con.rowCount("SELECT * FROM receipt where stat = 'done'")));
                    lblSendout.setText(String.valueOf(con.rowCount("SELECT * FROM receipt where stat = 'waiting'")));
                }
            }
        };
        refresh.start();
    }

    public DefaultTableModel getTable(String[] columns, String query) {
        DefaultTableModel model = new DefaultTableModel();
        ResultSet rs;
        try {
            for (String column : columns) {
                model.addColumn(column);
            }
            rs = con.getData(query);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getErrorCode() + "\n" + ex.getLocalizedMessage(), "Warning!", JOptionPane.WARNING_MESSAGE);
        }
        return model;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftNavigationPanel = new javax.swing.JPanel();
        lblUserPicture = new javax.swing.JLabel();
        lblUserFullname = new javax.swing.JLabel();
        lblUserPosition = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnSerial = new javax.swing.JButton();
        topNavigationPanel = new javax.swing.JPanel();
        lblDateandTime = new javax.swing.JLabel();
        btnNewTest = new javax.swing.JButton();
        Jlabel = new javax.swing.JLabel();
        Jlabel1 = new javax.swing.JLabel();
        lblDone = new javax.swing.JLabel();
        lblPending = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        lblSendout = new javax.swing.JLabel();
        Jlabel2 = new javax.swing.JLabel();
        mainActivityPane = new javax.swing.JTabbedPane();
        pnlPendingResults = new javax.swing.JPanel();
        txtPendingTestsSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPendingResults = new javax.swing.JTable();
        pnlPendingResults1 = new javax.swing.JPanel();
        txtpPatientSearch = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPatients = new javax.swing.JTable();
        pnlPendingResults2 = new javax.swing.JPanel();
        txtResultSearch = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        pnlwaiting = new javax.swing.JPanel();
        txtWaitingSendoutSearch = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblPendingSendout = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Sampana Laboratory Clinic");
        setName("mainframe"); // NOI18N
        setResizable(false);
        setSize(new java.awt.Dimension(1336, 728));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        leftNavigationPanel.setMaximumSize(new java.awt.Dimension(400, 728));

        lblUserPicture.setForeground(new java.awt.Color(255, 255, 255));
        lblUserPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUserPicture.setText("Picture");
        lblUserPicture.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 255)));

        lblUserFullname.setForeground(new java.awt.Color(255, 255, 255));
        lblUserFullname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUserFullname.setText("Full Name");

        lblUserPosition.setForeground(new java.awt.Color(255, 255, 255));
        lblUserPosition.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUserPosition.setText("Position");

        btnLogout.setText("Logout");

        btnSerial.setText("Open Serial Monitor");
        btnSerial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSerialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout leftNavigationPanelLayout = new javax.swing.GroupLayout(leftNavigationPanel);
        leftNavigationPanel.setLayout(leftNavigationPanelLayout);
        leftNavigationPanelLayout.setHorizontalGroup(
            leftNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftNavigationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserFullname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserPicture, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserPosition, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(btnSerial, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        leftNavigationPanelLayout.setVerticalGroup(
            leftNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftNavigationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUserPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblUserFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUserPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSerial, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        lblDateandTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDateandTime.setText("Date and Time");

        btnNewTest.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnNewTest.setText("New Test");
        btnNewTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewTestActionPerformed(evt);
            }
        });

        Jlabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Jlabel.setText("Pending");

        Jlabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Jlabel1.setText("Done");

        lblDone.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDone.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDone.setText("0");

        lblPending.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblPending.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPending.setText("0");

        lblDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDate.setText("month/day/year");

        lblTime.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTime.setText("Time");

        lblSendout.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSendout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSendout.setText("0");

        Jlabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Jlabel2.setText("Pending Sendout");

        javax.swing.GroupLayout topNavigationPanelLayout = new javax.swing.GroupLayout(topNavigationPanel);
        topNavigationPanel.setLayout(topNavigationPanelLayout);
        topNavigationPanelLayout.setHorizontalGroup(
            topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topNavigationPanelLayout.createSequentialGroup()
                .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topNavigationPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnNewTest, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(218, 218, 218)
                        .addComponent(Jlabel)
                        .addGap(68, 68, 68))
                    .addGroup(topNavigationPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblPending, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)))
                .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topNavigationPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(Jlabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topNavigationPanelLayout.createSequentialGroup()
                        .addComponent(lblSendout, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)))
                .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topNavigationPanelLayout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(Jlabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topNavigationPanelLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(lblDone, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(topNavigationPanelLayout.createSequentialGroup()
                                .addGap(172, 172, 172)
                                .addComponent(lblDateandTime, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topNavigationPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblDate, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                    .addComponent(lblTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(48, 48, 48)))))
                .addContainerGap())
        );
        topNavigationPanelLayout.setVerticalGroup(
            topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(topNavigationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDateandTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(topNavigationPanelLayout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(topNavigationPanelLayout.createSequentialGroup()
                        .addComponent(lblSendout, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Jlabel2))
                    .addGroup(topNavigationPanelLayout.createSequentialGroup()
                        .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDone, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPending, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(topNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNewTest, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Jlabel)
                            .addComponent(Jlabel1))))
                .addContainerGap())
        );

        tblPendingResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Receipt Number", "Patient Name", "Test Taken", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPendingResults.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblPendingResultsFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblPendingResults);
        if (tblPendingResults.getColumnModel().getColumnCount() > 0) {
            tblPendingResults.getColumnModel().getColumn(0).setResizable(false);
            tblPendingResults.getColumnModel().getColumn(1).setResizable(false);
            tblPendingResults.getColumnModel().getColumn(2).setResizable(false);
            tblPendingResults.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout pnlPendingResultsLayout = new javax.swing.GroupLayout(pnlPendingResults);
        pnlPendingResults.setLayout(pnlPendingResultsLayout);
        pnlPendingResultsLayout.setHorizontalGroup(
            pnlPendingResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPendingTestsSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlPendingResultsLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1170, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPendingResultsLayout.setVerticalGroup(
            pnlPendingResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPendingTestsSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainActivityPane.addTab("Pending Tests", pnlPendingResults);

        tblPatients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Full Name", "Age", "Date of Birth", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPatients.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblPatientsFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(tblPatients);
        if (tblPatients.getColumnModel().getColumnCount() > 0) {
            tblPatients.getColumnModel().getColumn(0).setResizable(false);
            tblPatients.getColumnModel().getColumn(1).setResizable(false);
            tblPatients.getColumnModel().getColumn(2).setResizable(false);
            tblPatients.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout pnlPendingResults1Layout = new javax.swing.GroupLayout(pnlPendingResults1);
        pnlPendingResults1.setLayout(pnlPendingResults1Layout);
        pnlPendingResults1Layout.setHorizontalGroup(
            pnlPendingResults1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingResults1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtpPatientSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlPendingResults1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1170, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPendingResults1Layout.setVerticalGroup(
            pnlPendingResults1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingResults1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtpPatientSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainActivityPane.addTab("Patients", pnlPendingResults1);

        tblResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Full Name", "Age", "Address", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblResultsMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblResults);
        if (tblResults.getColumnModel().getColumnCount() > 0) {
            tblResults.getColumnModel().getColumn(0).setResizable(false);
            tblResults.getColumnModel().getColumn(1).setResizable(false);
            tblResults.getColumnModel().getColumn(2).setResizable(false);
            tblResults.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout pnlPendingResults2Layout = new javax.swing.GroupLayout(pnlPendingResults2);
        pnlPendingResults2.setLayout(pnlPendingResults2Layout);
        pnlPendingResults2Layout.setHorizontalGroup(
            pnlPendingResults2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingResults2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtResultSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlPendingResults2Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1170, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPendingResults2Layout.setVerticalGroup(
            pnlPendingResults2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingResults2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtResultSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainActivityPane.addTab("Results", pnlPendingResults2);

        jPanel1.setLayout(new java.awt.CardLayout());

        tblPendingSendout.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Full Name", "Age", "Address", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPendingSendout.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblPendingSendoutFocusLost(evt);
            }
        });
        jScrollPane4.setViewportView(tblPendingSendout);
        if (tblPendingSendout.getColumnModel().getColumnCount() > 0) {
            tblPendingSendout.getColumnModel().getColumn(0).setResizable(false);
            tblPendingSendout.getColumnModel().getColumn(1).setResizable(false);
            tblPendingSendout.getColumnModel().getColumn(2).setResizable(false);
            tblPendingSendout.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout pnlwaitingLayout = new javax.swing.GroupLayout(pnlwaiting);
        pnlwaiting.setLayout(pnlwaitingLayout);
        pnlwaitingLayout.setHorizontalGroup(
            pnlwaitingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlwaitingLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1170, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(pnlwaitingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtWaitingSendoutSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlwaitingLayout.setVerticalGroup(
            pnlwaitingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlwaitingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtWaitingSendoutSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(pnlwaiting, "card2");

        mainActivityPane.addTab("waiting for Sendout Results", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(leftNavigationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(topNavigationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mainActivityPane)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftNavigationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topNavigationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainActivityPane, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int ans = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        switch (ans) {
            case 0 -> {
                LoginPage lp = new LoginPage();
                lp.setVisible(true);
                this.dispose();
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnNewTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTestActionPerformed
        if (nt == null || !nt.isVisible()) {
            nt = new newTest(this.accountid);
            nt.setVisible(true);
        } else {
            nt.toFront();
        }
    }//GEN-LAST:event_btnNewTestActionPerformed

    private void tblPendingResultsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPendingResultsFocusLost
        if (uv == null || !uv.isVisible()) {
            String x = tblPendingResults.getValueAt(tblPendingResults.getSelectedRow(), 0).toString();
            System.out.println(x);
            uv = new updateValue(x);
            uv.setLocationRelativeTo(null);
            uv.setVisible(true);
        } else {
            uv.toFront();
        }
    }//GEN-LAST:event_tblPendingResultsFocusLost

    private void tblPendingSendoutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPendingSendoutFocusLost
        if (usr == null || !usr.isVisible()) {
            String temp = tblPendingSendout.getValueAt(tblPendingSendout.getSelectedRow(), 0).toString();
            usr = new uploadSendoutResult(temp);
            usr.setVisible(true);
        } else {
            usr.toFront();
        }
    }//GEN-LAST:event_tblPendingSendoutFocusLost

    private void tblResultsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblResultsMouseReleased
        if (SwingUtilities.isRightMouseButton(evt) && tblResults.getSelectedRow() != 0) {
            Point loc = MouseInfo.getPointerInfo().getLocation();
            popupMenu.show(null, loc.x, loc.y);
        }
    }//GEN-LAST:event_tblResultsMouseReleased

    private void tblPatientsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPatientsFocusLost
        if (nt == null || !nt.isVisible()) {
            String patientId = String.valueOf(tblPatients.getValueAt(tblPatients.getSelectedRow(), 0));
            nt = new newTest(this.accountid, patientId);
            nt.setVisible(true);
        } else {
            nt.toFront();
        }
    }//GEN-LAST:event_tblPatientsFocusLost

    private void btnSerialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSerialActionPerformed
        if (pc == null || !pc.isVisible()) {
            pc.setVisible(true);
        } else {
            nt.toFront();
        }
    }//GEN-LAST:event_btnSerialActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jlabel;
    private javax.swing.JLabel Jlabel1;
    private javax.swing.JLabel Jlabel2;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNewTest;
    private javax.swing.JButton btnSerial;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDateandTime;
    private javax.swing.JLabel lblDone;
    private javax.swing.JLabel lblPending;
    private javax.swing.JLabel lblSendout;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblUserFullname;
    private javax.swing.JLabel lblUserPicture;
    private javax.swing.JLabel lblUserPosition;
    private javax.swing.JPanel leftNavigationPanel;
    private javax.swing.JTabbedPane mainActivityPane;
    private javax.swing.JPanel pnlPendingResults;
    private javax.swing.JPanel pnlPendingResults1;
    private javax.swing.JPanel pnlPendingResults2;
    private javax.swing.JPanel pnlwaiting;
    private javax.swing.JTable tblPatients;
    private javax.swing.JTable tblPendingResults;
    private javax.swing.JTable tblPendingSendout;
    private javax.swing.JTable tblResults;
    private javax.swing.JPanel topNavigationPanel;
    private javax.swing.JTextField txtPendingTestsSearch;
    private javax.swing.JTextField txtResultSearch;
    private javax.swing.JTextField txtWaitingSendoutSearch;
    private javax.swing.JTextField txtpPatientSearch;
    // End of variables declaration//GEN-END:variables
}
