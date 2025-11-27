/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
/**
 *
 * @author kms03
 */
public class CaptchaGenerator {
    private String captchaText; // 정답 텍스트를 저장할 변수
    private BufferedImage captchaImage; // 생성된 이미지를 저장할 변수
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int WIDTH = 200;
    private static final int HEIGHT = 40;
    private static final int TEXT_LENGTH = 6;

    public CaptchaGenerator() {
        this.captchaText = generateRandomText(TEXT_LENGTH);
        this.captchaImage = generateImage(this.captchaText);
    }
    
    private String generateRandomText(int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return text.toString();
    }
    
    private BufferedImage generateImage(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        Random random = new Random();
        
        // 배경 설정
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 텍스트 그리기 (랜덤 색상/위치)
        Font font = new Font("Arial", Font.BOLD | Font.ITALIC, 28);
        g2d.setFont(font);

        for (int i = 0; i < text.length(); i++) {
            char charToDraw = text.charAt(i);
            
            // 글자마다 랜덤 색상 및 위치 약간 변경
            g2d.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int x = 10 + (i * (WIDTH / TEXT_LENGTH)) + random.nextInt(5);
            int y = HEIGHT - 5 - random.nextInt(5);
            g2d.drawString(String.valueOf(charToDraw), x, y);
        }
        
        // 노이즈 라인 추가 (보안 강화)
        for (int i = 0; i < 6; i++) {
            g2d.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g2d.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                         random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        g2d.dispose();
        return image;
    }

    // 외부에서 텍스트(정답)를 가져가는 메서드
    public String getCaptchaText() {
        return captchaText;
    }

    // 외부에서 이미지를 가져가는 메서드
    public BufferedImage getCaptchaImage() {
        return captchaImage;
    }
}
