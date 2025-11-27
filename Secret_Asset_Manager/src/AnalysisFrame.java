/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnalysisFrame extends javax.swing.JFrame {

    DB_MAN DBM = new DB_MAN();
    private String currentUserId;
    private Map<String, Integer> dataMap = new HashMap<>(); // 카테고리별 금액 저장 Map
    private ChartPanel chartPanel;

    // 생성자
    public AnalysisFrame(String userId) {
        initComponents(); 
        
        this.currentUserId = userId; // 사용자 ID 저장
        this.setTitle("지출 분석 리포트 - " + userId);
        this.setSize(600, 550); 
        this.setLocationRelativeTo(null); // 화면 중앙 배치
        
        // 레이아웃 설정 (BorderLayout)
        getContentPane().setLayout(new BorderLayout());
        
        // 차트 패널 생성 및 중앙 배치
        chartPanel = new ChartPanel();
        this.add(chartPanel, BorderLayout.CENTER);
        
        // 하단 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        JButton btnClose = new JButton("닫기");
        
        btnClose.setPreferredSize(new Dimension(100, 35));
        
        // 닫기 버튼 이벤트 (창 종료)
        btnClose.addActionListener(e -> this.dispose());
        
        buttonPanel.add(btnClose);
        this.add(buttonPanel, BorderLayout.SOUTH); // 버튼 하단 배치
        
        // 데이터 로드 실행
        loadStatistics();
    }
    
    public AnalysisFrame() { 
        this("testuser"); 
    }

    // DB 데이터 로드 메서드
    private void loadStatistics() {
        // 카테고리별 지출 합계 조회 쿼리
        String sql = "SELECT category, SUM(amount) as total " +
                     "FROM account_book " +
                     "WHERE user_id = '" + currentUserId + "' AND t_type = '지출' " +
                     "GROUP BY category";
        try {
            DBM.dbOpen(); // DB 연결
            DBM.DB_rs = DBM.DB_stmt.executeQuery(sql);
            
            dataMap.clear(); // 기존 데이터 초기화
            while (DBM.DB_rs.next()) {
                // 결과셋 데이터 Map 저장 (항목명, 금액)
                dataMap.put(DBM.DB_rs.getString("category"), DBM.DB_rs.getInt("total"));
            }
            DBM.DB_rs.close();
            chartPanel.repaint(); // 차트 새로고침
        } catch (Exception e) { e.printStackTrace(); } 
        finally { try { DBM.dbClose(); } catch (IOException e) {} } // 리소스 해제
    }

    // 차트 드로잉 패널 내부 클래스
    class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // 부모 패널 배경 그리기
            
            // 데이터 부재 시 안내 문구 출력
            if (dataMap.isEmpty()) {
                g.drawString("표시할 지출 데이터가 없습니다.", 200, 200);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            // 그래픽 품질 향상 (계단 현상 제거)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            
            // 지출 총액 계산
            long totalAmount = 0;
            for (int amount : dataMap.values()) totalAmount += amount;

            int startAngle = 0; // 시작 각도
            int diameter = Math.min(width, height) - 100; // 원 지름 계산
            int x = (width - diameter) / 2 - 80; // 차트 X 좌표 (좌측 이동)
            int y = (height - diameter) / 2;     // 차트 Y 좌표
            
            int legendX = x + diameter + 30; // 범례 X 좌표
            int legendY = y + 20;            // 범례 Y 좌표
            
            // 차트 색상 배열
            Color[] colors = {new Color(255, 102, 102), new Color(102, 178, 255), new Color(255, 204, 102), 
                              new Color(153, 255, 153), new Color(204, 153, 255), new Color(150, 150, 150)};
            int colorIndex = 0;

            // 차트 및 범례 그리기 루프
            for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
                String category = entry.getKey();
                int amount = entry.getValue();
                
                // 비율에 따른 각도 계산
                int arcAngle = (int) Math.round((double) amount / totalAmount * 360);
                
                g2d.setColor(colors[colorIndex % colors.length]); // 색상 지정
                g2d.fillArc(x, y, diameter, diameter, startAngle, arcAngle); // 부채꼴 그리기
                
                // --- 범례(Legend) 표시 ---
                g2d.fillRect(legendX, legendY, 15, 15); // 색상 박스
                g2d.setColor(Color.BLACK);
                
                // 항목명 및 비율 텍스트 생성
                String label = String.format("%s (%.1f%%)", category, (double)amount/totalAmount*100);
                g2d.drawString(label, legendX + 20, legendY + 12);
                
                startAngle += arcAngle; // 다음 각도 갱신
                legendY += 30;          // 다음 범례 위치 이동
                colorIndex++;
            }
            
            // 하단 총액 표시
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            g2d.drawString("총 지출: " + String.format("%,d원", totalAmount), x, y + diameter + 20);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // "testuser" 아이디로 통계 화면을 바로 띄웁니다.
        java.awt.EventQueue.invokeLater(() -> new AnalysisFrame("testuser").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
