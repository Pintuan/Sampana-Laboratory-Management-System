/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Almar Dave
 */
public final class newTest extends javax.swing.JFrame {

    Generator gen = new Generator();
    Connection con = new Connection();
    int[][] price;
    long[][] testId;
    String[] selected;
    String accId;
    String patientId;
    float ammount;
    boolean complete;
    int sex = 1;

    int catCount = con.rowCount("Select * FROM Category");
    int testcount;
    JCheckBox[][] ck;
    JPanel[] pnl;

    /**
     * Creates new form newTest
     */
    public newTest() {
        initComponents();
        setLocationRelativeTo(null);
        txtBDate.setForeground(txtFullName.getForeground());
        this.setVisible(true);
        loadTests();
    }

    public newTest(String AccountId) {
        initComponents();
        setLocationRelativeTo(null);
        txtBDate.setForeground(txtFullName.getForeground());
        this.setVisible(true);
        loadTests();
        accId = AccountId;
        pnlTests.setSize(this.getWidth() - 10, pnlTests.getHeight());
    }

    public newTest(String AccountId, String PatientId) {
        initComponents();
        setLocationRelativeTo(null);
        txtBDate.setForeground(txtFullName.getForeground());
        this.setVisible(true);
        loadTests();
        accId = AccountId;
        this.patientId = PatientId;
        loadPatientDetails(PatientId);
        pnlTests.setSize(this.getWidth() - 10, pnlTests.getHeight());
    }

    public void loadPatientDetails(String patientId) {
        ResultSet rs = con.getData("SELECT * FROM patient where patientId = " + patientId);
        try {
            if (rs.next()) {
                txtFullName.setText(rs.getString("fullname"));
                txtAge.setText(rs.getString("age"));
                txtBDate.setText(rs.getString("bdate"));
                cmbSex.setSelectedIndex(Integer.parseInt(rs.getString("sex")));
                if (!rs.getString("isPwd").equals("1000000000")) {
                    cbPWD.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(newTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTests() {
        pnlTests.removeAll();
        ResultSet rs = con.getData("Select * FROM Category");
        try {
            pnl = new JPanel[catCount];
            testcount = con.rowCount("select testId,test,price from test where (sex = " + sex + " or sex = 3)");
            System.out.println(sex);
            ck = new JCheckBox[testcount][pnl.length];
            testId = new long[testcount][pnl.length];
            price = new int[testcount][pnl.length];
            for (int x = 0; rs.next(); x++) {
                pnl[x] = new JPanel(new GridLayout(30, 2));
                pnlTests.setSize(350, 500);
                if (ck.length != 0) {
                    pnl[x].setPreferredSize(new Dimension(100, 500));
                    ResultSet rsTest = con.getData("select category,testId,test.test,sex,price from test where (sex = "+sex+" or sex = 3) and price != 0 and category = " + rs.getString("categoryId") + " order by test");
                    for (int i = 0; rsTest.next(); i++) {
                        System.out.println(rsTest.getString("sex"));
                        ck[i][x] = new JCheckBox(rsTest.getString("test"));
                        ck[i][x].setFont(jLabel1.getFont());
                        pnl[x].add(ck[i][x]);
                        testId[i][x] = Long.parseLong(rsTest.getString("testId"));
                        price[i][x] = Integer.parseInt(rsTest.getString("price"));
                        ck[i][x].addItemListener((ItemEvent e) -> {
                            loadPrice();
                        });

                    }
                }
                pnlTests.addTab(rs.getString("catName"), pnl[x]);
                pnlTests.setMnemonicAt(x, x);
            }
        } catch (SQLException ex) {
            Logger.getLogger(newTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadPrice() {
        float x = 0;
        float total;
        for (int j = 0; j < catCount; j++) {
            for (int i = 0; i < testcount; i++) {
                if (ck[i][j] != null && ck[i][j].isSelected()) {
                    lblAmmount.setText(String.format("%.2f", x += price[i][j]));
                }
            }
        }
        total = x - (x * (Float.parseFloat(lblDiscountVal.getText()) / 100));
        System.out.println("total price : " + x);
        System.out.println("discount : " + (Float.parseFloat(lblDiscountVal.getText()) / 100));
        System.out.println("total ammount : " + total);
        ammount = total;

        lblTotalAmmount.setText(String.format("%.2f", total));
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
        jTable1 = new javax.swing.JTable();
        pnlPatientDetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPhysician = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbSex = new javax.swing.JComboBox<>();
        cbPWD = new javax.swing.JCheckBox();
        txtAge = new javax.swing.JTextField();
        txtBDate = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlTests = new javax.swing.JTabbedPane();
        txtSearch = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblAmmount = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        sldrDiscount = new javax.swing.JSlider();
        btnCheckout = new javax.swing.JButton();
        lblDiscountVal = new javax.swing.JLabel();
        lblTotalAmmount = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        pnlPatientDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Patient Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Full Name");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Physician");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Birthdate");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Age");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Sex");

        cmbSex.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        cmbSex.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSexItemStateChanged(evt);
            }
        });

        cbPWD.setText("PWD");

        javax.swing.GroupLayout pnlPatientDetailsLayout = new javax.swing.GroupLayout(pnlPatientDetails);
        pnlPatientDetails.setLayout(pnlPatientDetailsLayout);
        pnlPatientDetailsLayout.setHorizontalGroup(
            pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPhysician, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(139, 139, 139)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbPWD))
                .addGap(18, 18, 18)
                .addComponent(txtBDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(147, 147, 147)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        pnlPatientDetailsLayout.setVerticalGroup(
            pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cmbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhysician, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtBDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbPWD)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clinical Test", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jScrollPane2.setViewportView(pnlTests);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel8.setText("Search");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Checkout", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Ammount");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Discount");

        lblAmmount.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Total Ammount");

        sldrDiscount.setMaximum(50);
        sldrDiscount.setToolTipText("");
        sldrDiscount.setValue(0);
        sldrDiscount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldrDiscountStateChanged(evt);
            }
        });

        btnCheckout.setText("Checkout");
        btnCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckoutActionPerformed(evt);
            }
        });

        lblDiscountVal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblDiscountVal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDiscountVal.setText("0");

        lblTotalAmmount.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addComponent(sldrDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(lblDiscountVal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAmmount, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalAmmount, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalAmmount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(btnCheckout, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(sldrDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDiscountVal))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblAmmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPatientDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sldrDiscountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldrDiscountStateChanged
        lblDiscountVal.setText(Integer.toString(sldrDiscount.getValue()));
        loadPrice();
    }//GEN-LAST:event_sldrDiscountStateChanged

    private void cmbSexItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSexItemStateChanged
        sex = cmbSex.getSelectedIndex() + 1;
        System.out.println(sex);
        loadTests();
    }//GEN-LAST:event_cmbSexItemStateChanged

    public boolean isCompleteDetails() {
        String[] tests = getSelectedTest();
        return !txtFullName.getText().equals("") && !txtPhysician.getText().equals("") && !txtAge.getText().equals("0") && tests.length > 0;
    }

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckoutActionPerformed
        if (isCompleteDetails()) {
            int pId = gen.getId("patient", "patientId");
            int rId = gen.getId("receipt", "receiptId");
            if (patientId == null) {
                String sql = "INSERT INTO patient values (" + pId + ",'" + txtFullName.getText() + "'," + cmbSex.getSelectedIndex() + ",'" + txtBDate.getText() + "'," + (cbPWD.isSelected() ? 1000000001 : 1000000000) + ", " + txtAge.getText() + ")";
                if (con.update(sql)) {
                    sql = "INSERT INTO receipt values(" + rId + ",'" + LocalDate.now() + "'," + this.ammount + "," + this.accId + "," + pId + ",'Pending','" + txtPhysician.getText() + "',null,null,null)";
                    if (con.update(sql)) {
                        String[] tests = getSelectedTest();
                        for (int i = 0; i != tests.length; i++) {
                            if (tests[i] != null) {
                                System.out.println(tests[i]);
                                sql = "insert into test_taken values (" + tests[i] + ",'',null," + rId + ",'','')";
                                if (con.update(sql)) {
                                    System.out.println(sql);
                                    complete = true;
                                } else {
                                    JOptionPane.showMessageDialog(null, "cannot add the taken tests", "SQL Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "cannot add receipt Details", "SQL Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "cannot add Patient Details", "SQL Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String sql = "UPDATE patient SET [fullname] = '" +txtFullName.getText()+"'"
                        + ",[sex] = " + cmbSex.getSelectedIndex()
                        + ",[bdate] = '"+txtBDate.getText()+"'"
                        + ",[isPwd] = " +(cbPWD.isSelected() == true ? 1000000001 : 1000000000)
                        + ",[age] = " + txtAge.getText()
                        + "WHERE patientId = " + this.patientId;
                if (con.update(sql)) {
                    sql = "INSERT INTO receipt values(" + rId + ",'" + LocalDate.now() + "'," + this.ammount + "," + this.accId + "," + this.patientId + ",'Pending','" + txtPhysician.getText() + "',null,null,null)";
                    if (con.update(sql)) {
                        String[] tests = getSelectedTest();
                        for (int i = 0; i != tests.length; i++) {
                            if (tests[i] != null) {
                                System.out.println(tests[i]);
                                sql = "insert into test_taken values (" + tests[i] + ",'',null," + rId + ",'','')";
                                if (con.update(sql)) {
                                    System.out.println(sql);
                                    complete = true;
                                } else {
                                    JOptionPane.showMessageDialog(null, "cannot add the taken tests", "SQL Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "cannot add receipt Details", "SQL Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please Complete all the Patient Details", "Incomplete Details", JOptionPane.ERROR_MESSAGE);
        }

        if (complete) {
            JOptionPane.showMessageDialog(null, "Upload Complete!", "Complete", JOptionPane.OK_OPTION);
            this.dispose();

        }
    }//GEN-LAST:event_btnCheckoutActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        searchTest(txtSearch.getText(), cmbSex.getSelectedIndex());
    }//GEN-LAST:event_txtSearchKeyReleased

    public String[] getSelectedTest() {
        int x = 0;
        selected = new String[con.rowCount("select * from test")];
        for (int i = 0; i < catCount; i++) {
            for (int j = 0; j < testcount; j++) {
                if (ck[j][i] != null && ck[j][i].isSelected()) {
                    String temp = String.valueOf(testId[j][i]);
                    selected[x] = temp;
                    String[] tmp = getSubTest(ck[j][i].getText());
                    if (tmp != null && tmp.length != 0) {
                        x++;
                        for (String tmp1 : tmp) {
                            selected[x] = tmp1;
                            x++;
                            String[] sstest = getSubSubTest(tmp1);
                            if (sstest != null && sstest.length != 0) {
                                x++;
                                for (String sstest1 : sstest) {
                                    selected[x] = sstest1;
                                    x++;
                                }
                            }
                        }
                    } else {
                        x++;
                    }
                }
            }
        }
        return selected;
    }

    String[] getSubTest(String test) {
        String[] temp = new String[con.rowCount("select * from test where test = '" + test + "' and subtest != '' and (sex = " + sex + " or sex = 3)")];
        ResultSet rs = con.getData("select * from test where test = '" + test + "' and subtest != ''and (sex = " + sex + " or sex = 3)");
        try {
            for (int i = 0; rs.next(); i++) {
                temp[i] = rs.getString("testId");
            }
        } catch (SQLException ex) {
            Logger.getLogger(newTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }

    String[] getSubSubTest(String subtest) {
        String[] temp = new String[con.rowCount("select * from test where subtest = '" + subtest + "' and subsubtest != ''and (sex = " + sex + " or sex = 3)")];
        ResultSet rs = con.getData("select * from test where subtest = '" + subtest + "' and subsubtest != ''and (sex = " + sex + " or sex = 3)");
        try {
            for (int i = 0; rs.next(); i++) {
                temp[i] = rs.getString("testId");
            }
        } catch (SQLException ex) {
            Logger.getLogger(newTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }

    public void searchTest(String test, int sex) {

        try {
            pnlTests.removeAll();
            ResultSet rs = con.getData("select * from category");
            for (int j = 0; rs.next(); j++) {
                pnl[j] = new JPanel(new GridLayout(30, 2));
                for (int k = 0; k != testcount; k++) {
                    if (ck[k][j] != null) {
                        if (ck[k][j].getText().toLowerCase().startsWith(test.toLowerCase())) {
                            pnl[j].add(ck[k][j]);
                        }
                    }
                }
                pnlTests.addTab(rs.getString("catName"), pnl[j]);
                pnlTests.setMnemonicAt(j, j);
            }
        } catch (SQLException ex) {
            Logger.getLogger(newTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckout;
    private javax.swing.JCheckBox cbPWD;
    private javax.swing.JComboBox<String> cmbSex;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblAmmount;
    private javax.swing.JLabel lblDiscountVal;
    private javax.swing.JLabel lblTotalAmmount;
    private javax.swing.JPanel pnlPatientDetails;
    private javax.swing.JTabbedPane pnlTests;
    private javax.swing.JSlider sldrDiscount;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtBDate;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtPhysician;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
