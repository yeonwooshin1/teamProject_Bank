package bankService.view;

import bankService.controller.OtpController;
import bankService.controller.UserController;
import bankService.model.dto.IdResponseDto;
import bankService.service.OtpService;
import org.jline.reader.LineReader;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ServiceConfigurationError;


// 유저와 직접적으로 소통하는 콘솔/화면 담당
public class UserView { // class start

    // 싱글톤
    private UserView(){}
    private static final UserView instance = new UserView();
    public static UserView getInstance(){
        return instance;
    }

    // 공용 리소스(라우터에서 1회 주입)
    private Scanner scan; private Object ioLock; OtpService otpService; LineReader reader;


    // 싱글톤 가져오기
    UserController userController = UserController.getInstance();
    OtpController otpController = OtpController.getInstance();

   // wire
   public void wire(Scanner scan, LineReader reader , Object ioLock , OtpService otpService){
       this.scan = scan;
       this.ioLock = ioLock;
       this.otpService = otpService;
       this.reader = reader;
   }   // wire end


    // 시작 로그인 view
    public int index() {
        synchronized (ioLock) {
            try {
                System.out.println("===================================================================\n");
                System.out.println( " /$$$$$$$  /$$$$$$$        /$$$$$$$                      /$$      \n" +
                        "| $$__  $$| $$__  $$      | $$__  $$                    | $$      \n" +
                        "| $$  \\ $$| $$  \\ $$      | $$  \\ $$  /$$$$$$  /$$$$$$$ | $$   /$$\n" +
                        "| $$$$$$$ | $$$$$$$       | $$$$$$$  |____  $$| $$__  $$| $$  /$$/\n" +
                        "| $$__  $$| $$__  $$      | $$__  $$  /$$$$$$$| $$  \\ $$| $$$$$$/ \n" +
                        "| $$  \\ $$| $$  \\ $$      | $$  \\ $$ /$$__  $$| $$  | $$| $$_  $$ \n" +
                        "| $$$$$$$/| $$$$$$$/      | $$$$$$$/|  $$$$$$$| $$  | $$| $$ \\  $$\n" +
                        "|_______/ |_______/       |_______/  \\_______/|__/  |__/|__/  \\__/\n" +
                        "                                                                 ");
                System.out.println("===================================================================");
                System.out.println("[1] 로그인");
                System.out.println("[2] 회원가입");
                System.out.println("[3] 아이디 찾기");
                System.out.println("[4] 비밀번호 찾기");
                System.out.print("선택 ➜ ");
                int choose = scan.nextInt();
                System.out.println("===================================================================");

                if (choose == 1) {
                    int result = login();
                    return result;
                } else if (choose == 2) {
                    register();
                    return 0;
                } else if (choose == 3) {
                    findId();
                    return 0;
                } else if (choose == 4) {
                    findPassword();
                    return 0;
                }
                else {
                    System.out.println("⚠\uFE0F 메뉴에 있는 숫자를 입력해주세요.");
                    return 0;
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠\uFE0F 숫자만 입력하세요.");
                scan.nextLine();
                return 0;
            }   // catch end
        }   // synchronized end
    }   // func end

    // 1. 로그인
    public int login() {
        System.out.print("아이디를 입력해주세요( 소문자 입력 ) : ");
        String u_id = scan.next();
        System.out.print("비밀번호를 입력해주세요( 소문자 입력 ) : ");
        String u_pwd = scan.next();
        int result = userController.login(u_id, u_pwd);
        if (result == -1) {
            System.out.println("⚠\uFE0F 로그인 5회 시도했습니다.");
            return result;
        }
        else if (result == 0) {
            System.out.println("⚠\uFE0F 로그인 실패했습니다.");
            return result;
        }

        if (otpRequiredPromptLogin()) {
            boolean success = handleOtpProcess(result);
            return success ? result : 0;
        } else {
            System.out.println("로그인창으로 이동합니다.");
            return 0;
        }   // if end
    }   // func end

    // 로그인시 otp 발급 여부 묻기 메소드
    private boolean otpRequiredPromptLogin() {
        while (true) {
            System.out.println(" \uD83D\uDCE9 안전한 서비스 이용을 위해 로그인 시 OTP 인증이 필요합니다. 인증 받으시겠습니까?");
            System.out.print(" Y / N 중에 선택하세요. ");
            String choose = scan.next();

            if ("Y".equalsIgnoreCase(choose)) return true;
            else if ("N".equalsIgnoreCase(choose)) return false;
            else System.out.println("Y 또는 N 중에 선택해주세요.");
        }   // while end
    }   // func end

    // otp 발급을 받겠다할 시 실행시킬 것 로그인 전용
    private boolean handleOtpProcess(int uno) {
        String email = otpController.findEmail(uno);
        boolean result = handleOtpProcess(email ,1);
        return result;

    }   // func end

    // otp 발급 (로그인 1, 비밀번호찾기 2)
    public boolean handleOtpProcess(String email , int value){
        while (true) {
            if (value == 1) otpController.getIssueLogin(email);
            else if (value == 2) otpController.getIssuePW(email);
            System.out.println(" \uD83D\uDCE9 등록된 이메일로 인증 OTP를 발송했습니다. 메일 수신함을 확인해 주세요.");

            boolean verified = handleOtpInput();
            if (verified) return true;

            // 인증 실패 후 재발급 여부
            System.out.print(" \uD83D\uDCE9 OTP를 재발급 받으시겠습니까? Y / N : ");
            String re = scan.next();
            if (!"Y".equalsIgnoreCase(re)) {
                System.out.println("로그인창으로 이동합니다.");
                return false;
            }   // if end
        }   // while end
    }   // func end

    // otp 입력 뷰
    private boolean handleOtpInput() {

        while (true) {
            System.out.print("OTP를 입력해주세요: ");
            String otpInput = scan.next();

            int otpResult = otpController.verifyOtp(otpInput);

            switch (otpResult) {
                case 1 -> {
                    System.out.println("⚠\uFE0F OTP 세션이 존재하지 않거나 만료되었습니다.");
                    return false;
                }
                case 2 -> {
                    System.out.println("⚠\uFE0F OTP 입력 유효 시간이 경과되었습니다.");
                    return false;
                }
                case 3 -> {
                    System.out.println("⚠\uFE0F OTP 입력 시도 횟수를 초과하였습니다.");
                    return false;
                }
                case 4 -> {
                    System.out.println("⚠\uFE0F OTP가 일치하지 않습니다. 다시 시도해주세요.");
                }
                case 5 -> {
                    System.out.println(" OTP 인증이 성공적으로 완료되었습니다.");
                    return true;
                }
                default -> {
                    System.out.println("⚠\uFE0F 알 수 없는 오류입니다. 다시 시도해주세요.");
                    return false;
                }   // default
            }   // switch end
        }   // while end
    }   // func end


    //----------------------------------------------------------------------------------------------------//

    // 2. 회원가입
    public void register() {
        System.out.print("아이디를 입력하세요 : ");
        String u_id = scan.next();
        System.out.print("비밀번호를 입력하세요( 영어, 숫자, 특수문자 포함 8글자 ) : ");
        String u_pwd1 = scan.next();
        System.out.print("비밀번호 확인: ");
        String u_pwd2 = scan.next();
        System.out.print("이름 : ");
        String u_name = scan.next();
        System.out.print("전화번호 ( 010-xxxx-xxxx ) : ");
        String phone = scan.next();
        System.out.print("이메일을 입력하세요 ( gmail , naver , daum , kakao 중 하나 ) : ");
        String email = scan.next();
        // 형식 맞춰서 입력 받음
        System.out.print("생년월일(yyyy-MM-dd): ");
        String u_date = scan.next();
        int result = userController.registerMember(u_id, u_pwd1, u_pwd2, u_name, phone, email, u_date);  // ← dto와 비번확인 함께 전달 // 비밀번호 확인까지 같이 전달
        // switch 문을 사용하면 result 값에 따라서 출력하는 메세지가 달라짐
        switch (result) {
            case 1  -> System.out.println("✅ 회원가입 성공했습니다. ");
            case -1 -> System.out.println("⚠\uFE0F 중복된 아이디가 존재합니다.");
            case -2 -> System.out.println("⚠\uFE0F 입력하신 두 비밀번호가 일치하지 않습니다.");
            case -3 -> System.out.println("⚠\uFE0F 형식 오류가 발생했습니다.");
            case -4 -> System.out.println("⚠\uFE0F 이메일이 잘못 입력되었습니다.");
            case -5 -> System.out.println("⚠\uFE0F 비밀번호는 영어, 숫자, 특수문자 포함 8자 이상이어야 합니다." );
        }   // switch end
    } // func end

    //----------------------------------------------------------------------------------------------------//

    // 3. 아이디 찾기
    public void findId() {
        System.out.print("이름: ");
        String u_name = scan. next();
        System.out.print("전화번호: ");
        String u_phone = scan.next();
        IdResponseDto result = userController.findId(u_name, u_phone);
        if (result != null)
            // DB 에서 확인받아서 줘야 하니까
            System.out.println("당신의 아이디는 '" + result.getU_id() + "' 입니다.");
        else
            System.out.println("⚠\uFE0F 일치하는 회원 정보가 없습니다.");
    } // func end


    //----------------------------------------------------------------------------------------------------//

    // 4. 비밀번호 찾기
    public void findPassword() {
        System.out.print("아이디: ");
        String u_id = scan.next();
        System.out.print("이메일: ");
        String u_email = scan.next();
        int check = userController.verifyAccount(u_id, u_email);

        if (check == -1) System.out.println("⚠\uFE0F 올바른 이메일 형식이 아닙니다. 다시 입력하세요.");

        if (check == 1) {
            boolean otpResult = handleOtpProcess(u_email , 2);
            if(!otpResult) return;
            System.out.print("새 비밀번호: ");
            String newPwd = scan.next();
            int result = userController.updatePassword(u_id, newPwd);
            // if 안에 if
            if (result == 1)
                System.out.println("✅ 비밀번호 변경이 완료되었습니다.");
            else
                System.out.println("⚠\uFE0F 비밀번호 변경에 실패했습니다.");
        } else {
            System.out.println("⚠\uFE0F 입력 정보에 맞는 계정을 찾을 수 없습니다.");
        } // if end
    } // func end

} // class end
