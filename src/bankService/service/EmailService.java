package bankService.service;    // package

import bankService.util.EmailSendFormat;

import javax.mail.*;
import javax.mail.internet.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

// 이메일 전송 Service
public class EmailService { // class start

    // SMTP 서버 접속 정보와 보내는 주소 상수
    private final String host;   // SMTP 호스트 ( 예: smtp.gmail.com )
    private final String port;   // SMTP 포트 (보통 587)
    private final String user;   // SMTP 로그인 아이디( 내 이메일 계정 )
    private final String pass;   // SMTP 비밀번호(2단계보안 앱 비밀번호)
    private final String from;   // 발신자 이메일 주소(표시용 내 이메일 계정)

    // 생성자
    public EmailService() {
        Properties config = new Properties();

        // config.properties 파일을 연다
        try (InputStream in = new FileInputStream("src/bankService/resources/config.properties")) {
            config.load(in);    // properties 불러온다.
        } catch (IOException e) {
            // 파일이 없거나 읽기 실패
            throw new IllegalStateException("config.properties를 읽을 수 없습니다.");
        }   // catch end

        // 필수 값 꺼내기
        this.host = config.getProperty("smtp.host");    // SMTP 호스트 ( 예: smtp.gmail.com )
        this.port = config.getProperty("smtp.port");    // SMTP 포트 (보통 587)
        this.user = config.getProperty("smtp.user");    // SMTP 로그인 아이디( 내 이메일 계정 )
        this.pass = config.getProperty("smtp.pass");    // SMTP 비밀번호(2단계보안 앱 비밀번호)
        this.from = config.getProperty("from.address"); // 발신자 이메일 주소(표시용 내 이메일 계정)

        // 하나라도 비었으면 예외 발생
        if (host == null || user == null || pass == null || from == null) {
            throw new IllegalStateException("config.properties에 필수 속성이 빠졌습니다. " +
                    "[smtp.host, smtp.port, smtp.user, smtp.pass, from.address]");
        }   // if end
    }   // 생성자 end

    // 이메일 전송
    public void sendHtml(String to , String subject, String htmlBody) throws MessagingException {


        // 1) SMTP 연결 속성 설정
        Properties props = new Properties();
        // (필수) SMTP 서버 호스트명. 예) "smtp.gmail.com"
        props.put("mail.smtp.host", host);
        // (필수) SMTP 서버 포트. 문자열로 넣는 것이 관례. 보통 587(STARTTLS) 또는 465(SSL) , 나는 587
        props.put("mail.smtp.port", port);
        // (필수) SMTP 인증 사용 여부. 대부분의 상용 SMTP는 인증을 요구함(true)
        props.put("mail.smtp.auth", "true");
        // (587/TLS) 평문 연결 후 STARTTLS 명령으로 TLS로 업그레이드
        props.put("mail.smtp.starttls.enable", "true"); // 587/TLS 사용 시


        // 2) 인증자(아이디/비밀번호) 준비
        // - 여기서 user/pass는 SMTP 로그인 계정/비밀번호
        // - Gmail은 '앱 비밀번호(2단계 인증 필수)' 또는 OAuth2 필요. 일반 비밀번호는 차단됨
        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });


        // 3) 메일 메시지 구성
        MimeMessage msg = new MimeMessage(session);
        // From(발신자) 설정.
        msg.setFrom(new InternetAddress(from));
        // To(수신자) 설정. false 는 메일 주소 strict(엄격) 모드를 끈다
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        // 제목 인코딩(UTF-8). 한국말 한국말
        msg.setSubject(subject, StandardCharsets.UTF_8.name());
        // 본문 설정(HTML, UTF-8) 한국말 한국말
        msg.setContent(htmlBody, "text/html; charset=" + StandardCharsets.UTF_8.name());
        // 4) 실제 전송
        Transport.send(msg);

    }   // func end

    // HTML만 전송 비밀번호 변경 (OTP 편의 메서드)
    public void sendOtpHtmlPW(String to, String otp) throws MessagingException {
        String subject = EmailSendFormat.subjectPassWord();
        String html    = EmailSendFormat.bodyHtmlPassWord(otp);
        sendHtml(to, subject, html);
    }   // func end

    // HTML만 전송 로그인시도 (OTP 편의 메서드)
    public void sendOtpHtmlLogin(String to, String otp) throws MessagingException {
        String subject = EmailSendFormat.subjectLogin();
        String html    = EmailSendFormat.bodyHtmlLogin(otp);
        sendHtml(to, subject, html);
    }   // func end

    public void sendOtpHtmlLockLogin(String to, String otp) throws MessagingException{
        String subject = EmailSendFormat.subjectLockLogin();
        String html    = EmailSendFormat.bodyHtmlLockLogin(otp);
        sendHtml(to, subject, html);
    }
}   // class end

