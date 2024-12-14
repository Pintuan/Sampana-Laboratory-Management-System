/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package staff;

import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import main.export;

/**
 *
 * @author Almar Dave
 */
public final class updateValue extends javax.swing.JFrame {

    /**
     * Creates new form updateValue
     *
     * @param ReceiptId
     */
    main.Connection con = new main.Connection();
    private String[] fieldId;
    private String[] users;
    private final String receiptId;
    private JTextField[] values;
    private JTextField[] lotNo;
    private JTextField[] exp;
    boolean withSendout = false;

    public updateValue(String receiptId) {
        initComponents();
        loadSelectedTest(receiptId);
        loadPatientInfo(receiptId);
        this.receiptId = receiptId;
        loadDropDown();
        checkSendout();

    }

    void checkSendout() {
        try {
            ResultSet rs = con.getData("select * from test_taken tk inner join test t on tk .testId = t.testId where isSendout = 11 and tk.receiptId = " + receiptId);
            if (rs.next()) {
                withSendout = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(updateValue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadDropDown() {
        String SQL = "select licenseId, CONCAT(FName,' ',LName) as name from users where pos != 11024";
        users = new String[con.rowCount(SQL)];
        ResultSet rs = con.getData(SQL);
        try {
            for (int x = 0; rs.next(); x++) {
                cbReviewee.addItem(rs.getString("name"));
                cbVerifier1.addItem(rs.getString("name"));
                cbVerifier2.addItem(rs.getString("name"));
                users[x] = rs.getString("licenseId");
            }
        } catch (SQLException ex) {
            Logger.getLogger(updateValue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadPatientInfo(String receiptId) {
        ResultSet rs = con.getData("select *, DATEDIFF(year, p.bdate,r.receiptdate) AS age from receipt r inner join patient p on r.patientId = p.patientId where r.receiptId = '"+receiptId+"'");
        try {
            String[] sex = {"Male", "Female"};
            if (rs.next()) {
                lblFullName.setText(rs.getString("fullname"));
                lblSex.setText(sex[Integer.parseInt(rs.getString("sex"))]);
                lblReqDoctor.setText(rs.getString("reqDoctor"));
                lblReceiptNum.setText(rs.getString("receiptId"));
                lblBDay.setText(rs.getString("bdate"));
                lblToday.setText(rs.getString("receiptdate"));
                lblAge.setText(rs.getString("age"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(updateValue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadSelectedTest(String receiptId) {
        try {
            String sql = "select r.receiptId, p.fullname,p.bdate, p.sex,r.reqDoctor, t.*,tk.*  from receipt r "
                    + "inner join patient p on r.patientId = p.patientId "
                    + "inner join test_taken tk on r.receiptId = tk.receiptId "
                    + "inner join test t on tk.testId = t.testId "
                    + "where r.receiptId = " + receiptId + " and isSendout = 10 order by orderNo";
            int rowCount = con.rowCount(sql);
            fieldId = new String[rowCount];
            values = new JTextField[rowCount];
            lotNo = new JTextField[rowCount];
            exp = new JTextField[rowCount];
            JLabel[][] test = new JLabel[rowCount][3];
            ResultSet rs = con.getData(sql);
            boolean header = false;
            pnlTest.setLayout(new GridLayout(rowCount, 5, 0, 1));
            pnlValues.setLayout(new GridLayout(rowCount, 1, 0, 1));
            for (int x = 0; rs.next(); x++) {
                test[x][0] = new JLabel(rs.getString("test"));
                test[x][1] = new JLabel(rs.getString("subtest"));
                test[x][2] = new JLabel(rs.getString("subsubtest"));
                if (!header) {
                    pnlTest.add(test[x][0]);
                    pnlTest.add(test[x][1]);
                    pnlTest.add(test[x][2]);
                    header = true;
                } else {
                    if (!test[x][0].getText().equals(test[x - 1][0].getText())) {
                        test[x][0] = new JLabel(rs.getString("test"));
                        pnlTest.add(test[x][0]);
                    } else {
                        test[x][0] = new JLabel(rs.getString("test"));
                        pnlTest.add(test[x][0]);
                        test[x][0].setForeground(pnlTest.getBackground());
                    }

                    if (!test[x][1].getText().equals(test[x - 1][1].getText())) {
                        test[x][1] = new JLabel(rs.getString("subtest"));
                        pnlTest.add(test[x][1]);
                    } else {
                        test[x][1] = new JLabel(rs.getString("subtest"));
                        pnlTest.add(test[x][1]);
                        test[x][1].setForeground(pnlTest.getBackground());
                    }

                    if (!test[x][2].getText().equals(test[x - 1][2].getText())) {
                        test[x][2] = new JLabel(rs.getString("subsubtest"));
                        pnlTest.add(test[x][2]);
                    } else {
                        test[x][2] = new JLabel(rs.getString("subsubtest"));
                        pnlTest.add(test[x][2]);
                        test[x][2].setForeground(pnlTest.getBackground());
                    }
                }
                if (rs.getString("withValue").equals("1")) {
                    if ("1000000114".equals(rs.getString("category"))) {
                        JPanel temp = new JPanel();
                        lotNo[x] = new JTextField();
                        exp[x] = new JTextField();
                        values[x] = new JTextField();
                        values[x].setText(rs.getString("testVal"));
                        exp[x].setText(rs.getString("expDate"));
                        lotNo[x].setText(rs.getString("lotNo"));
                        temp.setLayout(new GridLayout(1, 3));
                        temp.add(values[x]);
                        temp.add(lotNo[x]);
                        temp.add(exp[x]);
                        pnlValues.add(temp);
                        fieldId[x] = rs.getString("testId");
                    } else {
                        values[x] = new JTextField();
                        values[x].setText(rs.getString("testVal"));
                        pnlValues.add(values[x]);
                        fieldId[x] = rs.getString("testId");
                    }

                } else {
                    values[x] = new JTextField();
                    values[x].setBorder(javax.swing.BorderFactory.createEmptyBorder());
                    values[x].setEnabled(false);
                    pnlValues.add(values[x]);
                }
                if (rs.getString("conRefRangeLow") != null) {
                    String[] tmp = rs.getString("conRefRangeLow").split("-");
                    for (String tmp1 : tmp) {
                        System.out.println(tmp1);
                    }
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(updateValue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    int isComplete() {
        int isEmpty = 0;
        for (JTextField fieldText1 : values) {
            if (fieldText1.getText().equals("") && fieldText1.isEnabled()) {
                isEmpty += 1;
            }
        }
        return isEmpty;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        cbReviewee = new javax.swing.JComboBox<>();
        cbVerifier2 = new javax.swing.JComboBox<>();
        cbVerifier1 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        pnlPatientDetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblAge = new javax.swing.JLabel();
        lblFullName = new javax.swing.JLabel();
        lblReqDoctor = new javax.swing.JLabel();
        lblBDay = new javax.swing.JLabel();
        lblSex = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblReceiptNum = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblToday = new javax.swing.JLabel();
        btnSavePrint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        pnlTest = new javax.swing.JPanel();
        pnlValues = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Reviewed By");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Verified By");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbReviewee, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(95, 95, 95)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbVerifier1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(cbVerifier2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbReviewee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbVerifier2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbVerifier1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        pnlPatientDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Patient Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Full Name");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Physician");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Birthdate");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Age");
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Sex");
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 20));

        lblAge.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAge.setText("0");
        lblAge.setPreferredSize(new java.awt.Dimension(100, 20));

        lblFullName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFullName.setText("Full Name");
        lblFullName.setPreferredSize(new java.awt.Dimension(150, 20));

        lblReqDoctor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblReqDoctor.setText("Physician");
        lblReqDoctor.setPreferredSize(new java.awt.Dimension(150, 20));

        lblBDay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBDay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBDay.setText("Birthdate");
        lblBDay.setPreferredSize(new java.awt.Dimension(100, 20));

        lblSex.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSex.setText("Sex");
        lblSex.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Receipt Number");

        lblReceiptNum.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblReceiptNum.setText("Full Name");
        lblReceiptNum.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Date");
        jLabel12.setPreferredSize(new java.awt.Dimension(100, 20));

        lblToday.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblToday.setText("Birthdate");
        lblToday.setPreferredSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout pnlPatientDetailsLayout = new javax.swing.GroupLayout(pnlPatientDetails);
        pnlPatientDetails.setLayout(pnlPatientDetailsLayout);
        pnlPatientDetailsLayout.setHorizontalGroup(
            pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientDetailsLayout.createSequentialGroup()
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblReqDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(lblReceiptNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223, Short.MAX_VALUE)
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblToday, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblSex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblAge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
        pnlPatientDetailsLayout.setVerticalGroup(
            pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblToday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(lblReceiptNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(lblBDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPatientDetailsLayout.createSequentialGroup()
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPatientDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblReqDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(27, 27, 27))
        );

        btnSavePrint.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSavePrint.setText("Save and Print");
        btnSavePrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavePrintActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Test Taken", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 18))); // NOI18N

        pnlTest.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Test", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        javax.swing.GroupLayout pnlTestLayout = new javax.swing.GroupLayout(pnlTest);
        pnlTest.setLayout(pnlTestLayout);
        pnlTestLayout.setHorizontalGroup(
            pnlTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 528, Short.MAX_VALUE)
        );
        pnlTestLayout.setVerticalGroup(
            pnlTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlValues.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Value", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        javax.swing.GroupLayout pnlValuesLayout = new javax.swing.GroupLayout(pnlValues);
        pnlValues.setLayout(pnlValuesLayout);
        pnlValuesLayout.setHorizontalGroup(
            pnlValuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
        );
        pnlValuesLayout.setVerticalGroup(
            pnlValuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 332, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlValues, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlValues, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSavePrint, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addComponent(pnlPatientDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSavePrint, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSavePrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavePrintActionPerformed

        String status;
        if (!withSendout) {
            status = "stat = 'done'";
        } else {
            status = "stat = 'waiting'";
        }
        String SQL = "UPDATE receipt set " + status + ","
                + "reviewee = " + users[cbReviewee.getSelectedIndex()] + ","
                + "pathologist = " + users[cbVerifier1.getSelectedIndex()] + ","
                + "medtech = " + users[cbVerifier2.getSelectedIndex()] + ""
                + " where receiptId = " + receiptId;
        boolean stat = true;
        if (con.update(SQL)) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].isEnabled()) {
                    SQL = "UPDATE test_taken set "
                            + "testVal  = '" + values[i].getText() + "',"
                            + "lotNo    = '" + (lotNo[i] == null ? "" : lotNo[i].getText()) + "',"
                            + "expDate  = '" + (exp[i] == null ? "" : exp[i].getText()) + "' where receiptId = " + receiptId + " and testId = " + fieldId[i];
                    System.out.println(SQL);
                    if (!con.update(SQL)) {
                        JOptionPane.showMessageDialog(null, "cannot upload test values");
                        stat = false;
                        break;
                    }
                }
            }
            if (stat) {
                export e = new export();
                e.createPDF(receiptId);
                this.dispose();
            }
        }
    }//GEN-LAST:event_btnSavePrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSavePrint;
    private javax.swing.JComboBox<String> cbReviewee;
    private javax.swing.JComboBox<String> cbVerifier1;
    private javax.swing.JComboBox<String> cbVerifier2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAge;
    private javax.swing.JLabel lblBDay;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblReceiptNum;
    private javax.swing.JLabel lblReqDoctor;
    private javax.swing.JLabel lblSex;
    private javax.swing.JLabel lblToday;
    private javax.swing.JPanel pnlPatientDetails;
    private javax.swing.JPanel pnlTest;
    private javax.swing.JPanel pnlValues;
    // End of variables declaration//GEN-END:variables
}
