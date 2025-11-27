/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class CalendarFrame extends javax.swing.JFrame {

    DB_MAN DBM = new DB_MAN(); 
    private String currentUserId;
    
    // 날짜 계산을 위한 Calendar 객체
    Calendar cal = Calendar.getInstance();
    int currentYear = cal.get(Calendar.YEAR);
    int currentMonth = cal.get(Calendar.MONTH) + 1; 

    // UI 컴포넌트 정의
    JPanel pnlTop = new JPanel();
    JPanel pnlContent = new JPanel(); 
    JLabel lblMonth = new JLabel();
    JButton btnPrev = new JButton("◀"); 
    JButton btnNext = new JButton("▶");

    public CalendarFrame(String userId) {
        this.currentUserId = userId; // 사용자 ID 저장
        this.setTitle("나의 지출 달력 - " + userId);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        
        initLayout();   // 레이아웃 초기화
        showCalendar(); // 달력 출력
    }
    
    public CalendarFrame() { this("testuser"); }

    private void initLayout() {
        this.setLayout(new BorderLayout());
        
        // 1. 상단 네비게이션 패널
        pnlTop.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        lblMonth.setFont(new Font("Dialog", Font.BOLD, 24)); // 년월 폰트 설정
        
        pnlTop.add(btnPrev);
        pnlTop.add(lblMonth);
        pnlTop.add(btnNext);
        
        this.add(pnlTop, BorderLayout.NORTH);
        
        // 2. 중앙 달력 그리드 (7열)
        pnlContent.setLayout(new GridLayout(0, 7, 2, 2)); 
        this.add(pnlContent, BorderLayout.CENTER);
        
        // 3. 하단 닫기 버튼 패널
        JPanel pnlBottom = new JPanel();
        JButton btnClose = new JButton("닫기");
        
        btnClose.addActionListener(e -> this.dispose()); // 창 종료
        pnlBottom.add(btnClose);
        
        this.add(pnlBottom, BorderLayout.SOUTH);
        
        // 이전 달 버튼 이벤트
        btnPrev.addActionListener(e -> {
            if (currentMonth == 1) { currentYear--; currentMonth = 12; }
            else { currentMonth--; }
            showCalendar(); // 달력 갱신
        });
        
        // 다음 달 버튼 이벤트
        btnNext.addActionListener(e -> {
            if (currentMonth == 12) { currentYear++; currentMonth = 1; }
            else { currentMonth++; }
            showCalendar(); // 달력 갱신
        });
    }

    // DB에서 월별 지출 데이터 조회 (일별 합계)
    private Map<Integer, Integer> getMonthlyExpense() {
        Map<Integer, Integer> expenseMap = new HashMap<>();
        // 해당 연/월의 일(day)별 지출 합계 쿼리
        String sql = String.format("SELECT DAY(t_date) as d, SUM(amount) as total " +
                                   "FROM account_book " +
                                   "WHERE user_id='%s' AND t_type='지출' " +
                                   "AND YEAR(t_date)=%d AND MONTH(t_date)=%d " +
                                   "GROUP BY d", 
                                   currentUserId, currentYear, currentMonth);
        try {
            DBM.dbOpen(); // DB 연결
            DBM.DB_rs = DBM.DB_stmt.executeQuery(sql);
            while (DBM.DB_rs.next()) {
                // 날짜와 금액을 Map에 매핑
                expenseMap.put(DBM.DB_rs.getInt("d"), DBM.DB_rs.getInt("total"));
            }
            DBM.DB_rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        finally { try { DBM.dbClose(); } catch(IOException e) {} }
        return expenseMap;
    }

    // 달력 UI 렌더링 메서드
    private void showCalendar() {
        pnlContent.removeAll(); // 기존 패널 초기화
        lblMonth.setText(currentYear + "년 " + currentMonth + "월");
        
        // 요일 헤더 생성 및 추가
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(Color.LIGHT_GRAY); 
            
            // 주말 색상 설정
            if (day.equals("일")) lbl.setForeground(Color.RED);
            else if (day.equals("토")) lbl.setForeground(Color.BLUE);
            else lbl.setForeground(Color.BLACK);
            
            pnlContent.add(lbl);
        }

        // 달력 시작 요일 및 마지막 날짜 계산
        cal.set(currentYear, currentMonth - 1, 1); 
        int startDay = cal.get(Calendar.DAY_OF_WEEK); 
        int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 

        // DB 데이터 조회
        Map<Integer, Integer> expenses = getMonthlyExpense();

        // 1일 전까지 빈 공백 채우기
        for (int i = 1; i < startDay; i++) {
            pnlContent.add(new JLabel("")); 
        }

        // 1일부터 말일까지 날짜 패널 생성
        for (int day = 1; day <= lastDate; day++) {
            JPanel pnlDay = new JPanel(new BorderLayout());
            pnlDay.setBackground(Color.WHITE); // 날짜 칸 흰색 배경
            pnlDay.setBorder(BorderFactory.createLineBorder(Color.GRAY)); 
            
            // 날짜 표시
            JLabel lblDay = new JLabel(" " + day);
            lblDay.setFont(new Font("Dialog", Font.BOLD, 12));
            pnlDay.add(lblDay, BorderLayout.NORTH);
            
            // 지출 내역 존재 시 금액 표시
            if (expenses.containsKey(day)) {
                int amount = expenses.get(day);
                JLabel lblMoney = new JLabel(String.format("-%,d", amount), SwingConstants.CENTER);
                lblMoney.setForeground(Color.RED);
                pnlDay.add(lblMoney, BorderLayout.CENTER);
            }
            
            pnlContent.add(pnlDay);
        }

        pnlContent.revalidate(); // 레이아웃 갱신
        pnlContent.repaint();
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
        java.awt.EventQueue.invokeLater(() -> { new CalendarFrame("testuser").setVisible(true); });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
