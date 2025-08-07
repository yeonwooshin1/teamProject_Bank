package bankService.controller;     // package

import bankService.model.dao.OtpDao;
import bankService.service.EmailService;
import bankService.service.OtpService;

import javax.mail.MessagingException;


public class OtpController {    // class start

    // 싱글톤 만들기
    private OtpController(){}
    private static OtpController otpController = new OtpController();
    public static OtpController getInstance(){
        return otpController;
    }

    // 싱글톤 가져오기
    OtpDao otpDao = OtpDao.getInstance();
    EmailService emailService = new EmailService();

    // wire 멤버변수
    private OtpService otpService;
    private int uno;

    // wire 세션 연결
    public void wireUno (int uno){
        this.uno = uno;

    }
    // wire 세션 연결
    public void wireOtp (OtpService otp) {
        this.otpService = otp;
    }

    // === method ===

    // 이메일 가져오기
    public String findEmail(int uno) {
        return otpDao.findEmail(uno);
    }   // func end

    // otp 발급 (이메일)
    public void getIssue (String email, int value) {
        // otp 발급한다.
        String otp = otpService.issue();
        // email 전송한다.
        // value에 따라 다른 이메일을 보낸다.
        // 이메일 전송중 오류 예외처리
        try {
            if(value == 1) emailService.sendOtpHtmlPW(email , otp);
            else if(value == 2) emailService.sendOtpHtmlLogin(email , otp);
            else if(value == 3) emailService.sendOtpHtmlLockLogin(email , otp);


        } catch (MessagingException e) {
            System.out.println("이메일 전송 중 문제가 발생했습니다. 잠시 후 다시 시도하세요.");
        } // catch end
    }   // func end

    // otp 발급
    public String getIssue (){
        return otpService.issue();
    }

    // otp 검증
    public int verifyOtp (String inputOtp) {
        int result = otpService.verify(inputOtp);
        return result;
    }   // func end

    // mainView에 만료시 묻는 로직 controller
    // 신뢰 유효하면 true 리턴 만료 시 사용자에게 묻고, Y → OtpView.forceReauth() 실행 후 유효 여부 리턴 N → false 리턴
    public boolean trustOtp() {
        // 1) 아직 유효하면 바로 통과
        if (otpService.checkValidUntil()) {
            return true;
        }   // if end
        return false;
    }   // func end

}   // class end
