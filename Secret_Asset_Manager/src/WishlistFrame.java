/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Vector;
/**
 *
 * @author kms03
 */
public class WishlistFrame extends javax.swing.JFrame {

    DB_MAN DBM = new DB_MAN();
    private String currentUserId;
    private int selectedWishNo = -1; // 현재 선택한 위시리스트 번호 (PK)

    public WishlistFrame(String userId) {
        initComponents();
        this.currentUserId = userId; // 사용자 ID 저장
        this.setTitle("나의 위시리스트 - " + userId);
        this.setLocationRelativeTo(null); // 화면 중앙 배치
        
        // 테이블 클릭 이벤트 (게이지바 업데이트용)
        tblWishlist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateProgress();
            }
        });

        loadWishlist(); // 초기 목록 로드
    }
    
    public WishlistFrame() { this("testuser"); }

    // 선택 항목 달성률 계산 및 게이지 표시
    private void updateProgress() {
        int row = tblWishlist.getSelectedRow();
        if (row == -1) return; // 선택 없음

        // 테이블 데이터 가져오기 (콤마 및 단위 제거)
        String priceStr = (String) tblWishlist.getValueAt(row, 1);
        String savedStr = (String) tblWishlist.getValueAt(row, 2);
        int price = Integer.parseInt(priceStr.replace(",", "").replace(" 원", ""));
        int saved = Integer.parseInt(savedStr.replace(",", "").replace(" 원", ""));
        
        // 숨겨진 식별 번호(PK) 확보
        selectedWishNo = (int) tblWishlist.getValueAt(row, 3);

        // 퍼센트 계산
        int percent = (int) ((double) saved / price * 100);
        
        // 게이지바 업데이트
        progressBar.setValue(percent);
        progressBar.setString(percent + "% 달성 (" + savedStr + " / " + priceStr + ")");
    }

    // 저금하기 기능 (기존 금액에 추가)
    private void depositMoney() {
        if (selectedWishNo == -1) {
            JOptionPane.showMessageDialog(this, "먼저 목록에서 아이템을 선택해주세요.");
            return;
        }

        // 금액 입력 대화상자
        String input = JOptionPane.showInputDialog(this, "얼마를 저금하시겠습니까?");
        if (input == null || input.isEmpty()) return;

        try {
            int moneyToAdd = Integer.parseInt(input);
            
            // 금액 누적 업데이트 쿼리
            String sql = "UPDATE wishlist SET saved_money = saved_money + " + moneyToAdd + 
                         " WHERE no = " + selectedWishNo;
            
            DBM.dbOpen(); // DB 연결
            DBM.DB_stmt.executeUpdate(sql); // 실행
            
            JOptionPane.showMessageDialog(this, "저금 성공! 목표에 한 걸음 더 가까워졌습니다!");
            loadWishlist(); // 목록 갱신
            
            // 게이지바 및 선택 상태 초기화
            progressBar.setValue(0);
            progressBar.setString("0%");
            selectedWishNo = -1;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { DBM.dbClose(); } catch(IOException e) {}
        }
    }

    // 위시리스트 신규 등록
    private void saveWishlist() {
        String item = txtItem.getText().trim();
        String priceStr = txtPrice.getText().trim();

        // 필수 입력 확인
        if (item.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "물건 이름과 가격을 모두 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int price = Integer.parseInt(priceStr);
            
            // 데이터 삽입 쿼리 (이미지 경로 제외, 초기 모은 돈 0원)
            String sql = String.format("INSERT INTO wishlist (user_id, item_name, price, saved_money) VALUES ('%s', '%s', %d, 0)",
                                       currentUserId, item, price);

            DBM.dbOpen();
            DBM.DB_stmt.executeUpdate(sql);
            
            JOptionPane.showMessageDialog(this, "위시리스트에 저장되었습니다!");
            
            // 입력창 초기화 및 목록 갱신
            txtItem.setText("");
            txtPrice.setText("");
            loadWishlist();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격은 숫자만 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "저장 중 오류 발생: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { DBM.dbClose(); } catch (IOException e) {}
        }
    }

    // DB에서 목록 조회 및 테이블 렌더링
    private void loadWishlist() {
        String sql = "SELECT no, item_name, price, saved_money FROM wishlist WHERE user_id = '" + currentUserId + "'";
        
        try {
            DBM.dbOpen();
            DBM.DB_rs = DBM.DB_stmt.executeQuery(sql);
            
            DefaultTableModel model = (DefaultTableModel) tblWishlist.getModel();
            model.setRowCount(0); // 테이블 초기화
            model.setColumnIdentifiers(new String[]{"물건명", "목표 가격", "모은 돈", "RealID"});

            while (DBM.DB_rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(DBM.DB_rs.getString("item_name"));
                row.add(String.format("%,d 원", DBM.DB_rs.getInt("price")));
                row.add(String.format("%,d 원", DBM.DB_rs.getInt("saved_money")));
                row.add(DBM.DB_rs.getInt("no")); // 숨겨진 PK
                model.addRow(row);
            }
            DBM.DB_rs.close();
            
            // RealID 컬럼 숨김 처리
            if (tblWishlist.getColumnCount() > 3) {
                tblWishlist.getColumnModel().getColumn(3).setMinWidth(0);
                tblWishlist.getColumnModel().getColumn(3).setMaxWidth(0);
                tblWishlist.getColumnModel().getColumn(3).setWidth(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { DBM.dbClose(); } catch (IOException e) {}
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblWishlist = new javax.swing.JTable();
        btnClose = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        btnDeposit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("맑은 고딕", 1, 18)); // NOI18N
        jLabel1.setText("나의 위시리스트");

        jLabel2.setText("물건 이름");

        jLabel3.setText("목표 금액");

        btnSave.setText("저장");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        tblWishlist.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblWishlist);

        btnClose.setText("닫기");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        progressBar.setStringPainted(true);

        btnDeposit.setText("저금하기");
        btnDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepositActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrice, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtItem)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(91, 91, 91)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDeposit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeposit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveWishlist();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepositActionPerformed
        depositMoney();
    }//GEN-LAST:event_btnDepositActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> { new WishlistFrame("testuser").setVisible(true); });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDeposit;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTable tblWishlist;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtPrice;
    // End of variables declaration//GEN-END:variables
}
