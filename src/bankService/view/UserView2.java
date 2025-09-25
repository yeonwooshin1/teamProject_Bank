package bankService.view;

import bankService.controller.AccountController;
import bankService.controller.UserController;
import bankService.controller.UserController2;
import bankService.model.dto.IdResponseDto;
import org.jline.reader.LineReader;


import java.util.InputMismatchException;
import java.util.Scanner;

// 유저와 직접적으로 소통하는 콘솔/화면 담당
public class UserView2 { // class start

    // 싱글톤
    private UserView2(){}
    private static final UserView2 instance = new UserView2();
    public static UserView2 getInstance(){
        return instance;
    }

    // 공용 리소스(라우터에서 1회 주입)
    private Scanner scan = new Scanner(System.in); // 기본값 초기화
    private LineReader reader;
    private Object ioLock = new Object(); // synchronized 용

    // 싱글톤 가져오기
    UserController2 userController = UserController2.getInstance();
    AccountController accountController = AccountController.getInstance();

    // wire
    public void wire(Scanner scan, LineReader reader , Object ioLock){
        if(scan != null) this.scan = scan;
        if(ioLock != null) this.ioLock = ioLock;
        if(reader != null) this.reader = reader;
    }

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
                        " Made by - Gyeore , Jihoon , Yeonwoo , Yujin -");
                System.out.println("===================================================================");
                System.out.println("[1] 로그인");
                System.out.println("[2] 회원가입");
                System.out.println("[3] 아이디 찾기");
                System.out.println("[4] 비밀번호 찾기");
                System.out.print("선택 ➜ ");
                int choose = scan.nextInt();
                System.out.println("===================================================================");

                if (choose == 1) {
                    return login();
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
            }
        }
    }

    // 1. 로그인
    public int login() {
        System.out.print("아이디를 입력해주세요( 소문자 입력 ) : ");
        String u_id = scan.next();
        System.out.print("비밀번호를 입력해주세요( 소문자 입력 ) : ");
        String u_pwd = scan.next();
        int result = userController.login(u_id, u_pwd);

        if (result < 0) {
            System.out.println("⚠\uFE0F 로그인 실패했습니다.");
            return 0;
        } else if (result == 0) {
            System.out.println("⚠\uFE0F 로그인 실패했습니다.");
            return 0;
        }

        // OTP 관련 기능 제거
        System.out.println("로그인 성공! (OTP 인증 없음)");

        // 세션 연결
        accountController.wire(result);      // AccountController에 세션 주입
        userController.wire(result);         // UserController2에 세션 주입

        return result;
    }


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
        System.out.print("생년월일(yyyy-MM-dd): ");
        String u_date = scan.next();
        int result = userController.registerMember(u_id, u_pwd1, u_pwd2, u_name, phone, email, u_date);
        switch (result) {
            case 1  -> System.out.println("✅ 회원가입 성공했습니다. ");
            case -1 -> System.out.println("⚠\uFE0F 중복된 아이디가 존재합니다.");
            case -2 -> System.out.println("⚠\uFE0F 입력하신 두 비밀번호가 일치하지 않습니다.");
            case -3 -> System.out.println("⚠\uFE0F 형식 오류가 발생했습니다.");
            case -4 -> System.out.println("⚠\uFE0F 이메일이 잘못 입력되었습니다.");
            case -5 -> System.out.println("⚠\uFE0F 비밀번호는 영어, 숫자, 특수문자 포함 8자 이상이어야 합니다." );
        }
    }

    // 3. 아이디 찾기
    public void findId() {
        System.out.print("이름: ");
        String u_name = scan.next();
        System.out.print("전화번호: ");
        String u_phone = scan.next();
        IdResponseDto result = userController.findId(u_name, u_phone);
        if (result != null)
            System.out.println("당신의 아이디는 '" + result.getU_id() + "' 입니다.");
        else
            System.out.println("⚠\uFE0F 일치하는 회원 정보가 없습니다.");
    }

    // 4. 비밀번호 찾기
    public void findPassword() {
        System.out.print("아이디: ");
        String u_id = scan.next();
        System.out.print("이메일: ");
        String u_email = scan.next();
        int check = userController.verifyAccount(u_id, u_email);

        if (check == -1) System.out.println("⚠\uFE0F 올바른 이메일 형식이 아닙니다. 다시 입력하세요.");

        if (check == 1) {
            // OTP 기능 제거
            System.out.print("새 비밀번호: ");
            String newPwd = scan.next();
            int result = userController.updatePassword(u_id, newPwd);
            if (result == 1)
                System.out.println("✅ 비밀번호 변경이 완료되었습니다.");
            else
                System.out.println("⚠\uFE0F 비밀번호 변경에 실패했습니다.");
        } else {
            System.out.println("⚠\uFE0F 입력 정보에 맞는 계정을 찾을 수 없습니다.");
        }
    }

} // class end
