/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
/**
 *
 * @author kms03
 */
public class MainFrame extends javax.swing.JFrame {
    
    DB_MAN DBM = new DB_MAN();
    private String currentUserId;

    public MainFrame(String userId) {
        initComponents();
        this.currentUserId = userId; // 사용자 ID 저장
        this.setTitle("시크릿 자산 관리부 - " + userId);
        this.setLocationRelativeTo(null);
        
        if (lblWelcome != null) lblWelcome.setText(userId + "님, 환영합니다!");

        setupFilter();        // 필터 초기화
        updateSummary();      // 상단 요약 정보 갱신
        loadTransactionList(); // 내역 리스트 로드
    }

    public MainFrame() { 
        this("testuser"); 
    }

    // 콤보박스 필터 설정
    private void setupFilter() {
        cboFilter.setModel(new DefaultComboBoxModel<>(new String[]{"전체", "수입", "지출"}));
        cboFilter.setSelectedIndex(0);
        
        // 필터 변경 시 리스트 즉시 갱신 이벤트
        cboFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadTransactionList(); 
            }
        });
    }

    // 상단 총 수입/지출/잔액 갱신
    public void updateSummary() {
        long totalIncome = 0, totalExpense = 0;
        String sqlIncome = "SELECT SUM(amount) AS total FROM account_book WHERE user_id = '" + currentUserId + "' AND t_type = '수입'";
        String sqlExpense = "SELECT SUM(amount) AS total FROM account_book WHERE user_id = '" + currentUserId + "' AND t_type = '지출'";

        try {
            DBM.dbOpen(); // DB 연결
            
            // 수입 합계 조회
            DBM.DB_rs = DBM.DB_stmt.executeQuery(sqlIncome);
            if (DBM.DB_rs.next()) totalIncome = DBM.DB_rs.getLong("total");
            DBM.DB_rs.close();

            // 지출 합계 조회
            DBM.DB_rs = DBM.DB_stmt.executeQuery(sqlExpense);
            if (DBM.DB_rs.next()) totalExpense = DBM.DB_rs.getLong("total");
            DBM.DB_rs.close();

            // UI 업데이트
            lblIncomeValue.setText(String.format("%,d 원", totalIncome));
            lblExpenseValue.setText(String.format("%,d 원", totalExpense));
            lblBalanceValue.setText(String.format("%,d 원", totalIncome - totalExpense));

        } catch (Exception e) { e.printStackTrace(); } 
        finally { try { DBM.dbClose(); } catch (IOException e) { } }
    }

    // 거래 내역 리스트 조회 및 테이블 렌더링
    public void loadTransactionList() {
        // 1. 검색 조건 확인 (필터, 검색어)
        String filter = cboFilter.getSelectedItem() != null ? cboFilter.getSelectedItem().toString() : "";
        String keyword = txtSearch.getText().trim();

        // 2. 기본 쿼리 작성
        String strSQL = "SELECT no, t_date, t_type, category, amount, memo FROM account_book WHERE user_id = '" + currentUserId + "'";
        
        // 3. 필터 조건 추가 (수입/지출)
        if (!filter.equals("전체") && !filter.isEmpty()) {
            strSQL += " AND t_type = '" + filter + "'";
        }
        
        // 4. 검색어 조건 추가 (카테고리 or 메모)
        if (!keyword.isEmpty()) {
            strSQL += " AND (category LIKE '%" + keyword + "%' OR memo LIKE '%" + keyword + "%')";
        }

        // 최신순 정렬
        strSQL += " ORDER BY t_date DESC, no DESC";

        try {
            DBM.dbOpen();
            DBM.DB_rs = DBM.DB_stmt.executeQuery(strSQL);

            DefaultTableModel model = (DefaultTableModel) tblTransactionList.getModel();
            model.setRowCount(0); // 테이블 초기화
            
            // 컬럼 설정 (RealID는 숨김 처리용 PK)
            model.setColumnIdentifiers(new String[]{"순번", "날짜", "구분", "카테고리", "금액", "메모", "RealID"});

            int rank = 1;
            while (DBM.DB_rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rank++); 
                row.add(DBM.DB_rs.getString("t_date"));
                row.add(DBM.DB_rs.getString("t_type"));
                row.add(DBM.DB_rs.getString("category"));
                row.add(String.format("%,d", DBM.DB_rs.getInt("amount")));
                row.add(DBM.DB_rs.getString("memo"));
                row.add(DBM.DB_rs.getInt("no")); // [6] 실제 DB PK 값
                model.addRow(row);
            }
            DBM.DB_rs.close();
            
            // RealID 컬럼 숨김 처리 (사용자에게 불필요)
            if (tblTransactionList.getColumnCount() > 6) {
                tblTransactionList.getColumnModel().getColumn(6).setMinWidth(0);
                tblTransactionList.getColumnModel().getColumn(6).setMaxWidth(0);
                tblTransactionList.getColumnModel().getColumn(6).setWidth(0);
            }

        } catch (Exception e) { e.printStackTrace(); } 
        finally { try { DBM.dbClose(); } catch (IOException e) { } }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblWelcome = new javax.swing.JLabel();
        lblIncomeValue = new javax.swing.JLabel();
        lblExpenseValue = new javax.swing.JLabel();
        lblBalanceValue = new javax.swing.JLabel();
        cboFilter = new javax.swing.JComboBox<>();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        scrollPaneList = new javax.swing.JScrollPane();
        tblTransactionList = new javax.swing.JTable();
        btnInsert = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnAnalysis = new javax.swing.JButton();
        btnCalendar = new javax.swing.JButton();
        btnWishlist = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 520));

        lblWelcome.setFont(new java.awt.Font("맑은 고딕", 1, 18)); // NOI18N
        lblWelcome.setText("jLabel1");

        lblIncomeValue.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        lblIncomeValue.setText("총 수입액");

        lblExpenseValue.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        lblExpenseValue.setText("총 지출액");

        lblBalanceValue.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        lblBalanceValue.setText("현재 잔액");

        cboFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnSearch.setText("검색");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        tblTransactionList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "순번", "날짜", "구분", "카테고리", "(단위 : 원)"
            }
        ));
        scrollPaneList.setViewportView(tblTransactionList);

        btnInsert.setText("내역 추가");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        btnUpdate.setText("내역 수정");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("내역 삭제");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAnalysis.setText("통계 및 그래프");
        btnAnalysis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalysisActionPerformed(evt);
            }
        });

        btnCalendar.setText("달력");
        btnCalendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalendarActionPerformed(evt);
            }
        });

        btnWishlist.setText("위시리스트");
        btnWishlist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWishlistActionPerformed(evt);
            }
        });

        btnLogout.setText("로그아웃");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnRefresh.setText("새로고침");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        jLabel1.setText("총 수입액: ");

        jLabel2.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        jLabel2.setText("총 지출액: ");

        jLabel3.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        jLabel3.setText("현재 잔액: ");

        jLabel4.setFont(new java.awt.Font("맑은 고딕", 1, 12)); // NOI18N
        jLabel4.setText("카테고리 검색");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(lblWelcome)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPaneList, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(126, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(6, 6, 6)
                                .addComponent(lblIncomeValue)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(btnLogout))
                                    .addComponent(btnInsert)
                                    .addComponent(btnUpdate)
                                    .addComponent(btnDelete)
                                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(cboFilter, 0, 375, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(6, 6, 6)
                                        .addComponent(lblExpenseValue))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(6, 6, 6)
                                        .addComponent(lblBalanceValue))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnAnalysis)
                                        .addGap(6, 6, 6)
                                        .addComponent(btnCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(btnWishlist)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(23, 23, 23))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblWelcome)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(lblIncomeValue))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(lblExpenseValue))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(lblBalanceValue))
                        .addGap(18, 18, 18)
                        .addComponent(cboFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLogout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearch))
                        .addGap(12, 12, 12)
                        .addComponent(scrollPaneList, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnInsert)
                        .addGap(6, 6, 6)
                        .addComponent(btnUpdate)
                        .addGap(6, 6, 6)
                        .addComponent(btnDelete)
                        .addGap(36, 36, 36)
                        .addComponent(btnRefresh)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAnalysis)
                    .addComponent(btnCalendar)
                    .addComponent(btnWishlist))
                .addGap(34, 34, 34))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        new InputDialog(this, currentUserId).setVisible(true); // 입력 창 호출
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        int selectedRow = tblTransactionList.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 내역을 선택해주세요."); return;
        }
        
        // 선택된 행 데이터 추출
        String date = (String) tblTransactionList.getValueAt(selectedRow, 1);
        String type = (String) tblTransactionList.getValueAt(selectedRow, 2);
        String category = (String) tblTransactionList.getValueAt(selectedRow, 3);
        String amountStr = (String) tblTransactionList.getValueAt(selectedRow, 4);
        int amount = Integer.parseInt(amountStr.replace(",", ""));
        String memo = (String) tblTransactionList.getValueAt(selectedRow, 5);
        int id = (int) tblTransactionList.getValueAt(selectedRow, 6); // RealID (Hidden)
        
        // 수정 창 호출 (데이터 전달)
        InputDialog dialog = new InputDialog(this, currentUserId);
        dialog.setEditData(id, date, type, category, amount, memo);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = tblTransactionList.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 내역을 선택해주세요."); return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int recordNo = (int) tblTransactionList.getValueAt(selectedRow, 6); // RealID
            try {
                DBM.dbOpen();
                DBM.DB_stmt.executeUpdate("DELETE FROM account_book WHERE no = " + recordNo);
                loadTransactionList(); // 목록 갱신
                updateSummary();       // 요약 정보 갱신
            } catch (Exception e) { e.printStackTrace(); } 
            finally { try { DBM.dbClose(); } catch(IOException e) {} }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        updateSummary();
        loadTransactionList();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        this.dispose();
        new LoginFrame().setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadTransactionList();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnAnalysisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalysisActionPerformed
        AnalysisFrame analysis = new AnalysisFrame(currentUserId);
        analysis.setVisible(true);
    }//GEN-LAST:event_btnAnalysisActionPerformed

    private void btnWishlistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWishlistActionPerformed
        new WishlistFrame(currentUserId).setVisible(true);
    }//GEN-LAST:event_btnWishlistActionPerformed

    private void btnCalendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalendarActionPerformed
        new CalendarFrame(currentUserId).setVisible(true);
    }//GEN-LAST:event_btnCalendarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // ... (Look and feel code omitted) ...
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame("testuser").setVisible(true); // 테스트용
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalysis;
    private javax.swing.JButton btnCalendar;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnWishlist;
    private javax.swing.JComboBox<String> cboFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblBalanceValue;
    private javax.swing.JLabel lblExpenseValue;
    private javax.swing.JLabel lblIncomeValue;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JScrollPane scrollPaneList;
    private javax.swing.JTable tblTransactionList;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
