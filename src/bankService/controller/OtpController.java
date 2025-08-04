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
    OtpService otpService = new OtpService();

    //!!!!!!!!!!!!!!!!!!!!!! 나중에 한 인스턴스로 합쳐야함 approuter에서 wire로 묶어야함
    EmailService emailService = new EmailService();


    // 이메일 가져오기
    public String findEmail(int uno) {
        return otpDao.findEmail(uno);
    }   // func end

    // otp 발급
    public void getIssuePW (String email) {
        // otp 발급한다.
        String otp = otpService.issue();
        // email 전송한다.
        // 이메일 전송중 오류 예외처리
        try {
            emailService.sendOtpHtmlPW(email , otp);
        } catch (MessagingException e) {
            System.out.println("이메일 전송 중 문제가 발생했습니다. 잠시 후 다시 시도하세요.");
        } // catch end
    }   // func end

    public void getIssueLogin (String email) {
        // otp 발급한다.
        String otp = otpService.issue();
        // email 전송한다.
        // 이메일 전송중 오류 예외처리
        try {
            emailService.sendOtpHtmlPW(email , otp);
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
}   // class end
