package bankService;

import bankService.view.MainView;
import bankService.view.UserView;

import java.util.ArrayList;

public class AppStart {
    public static void main(String[] args) {
//        UserView.getInstance().index();
        MainView.getInstance().showSecurityMenu();
//
//        // (1) 싱글톤 컨트롤러 가져오기
//        UserController userController = UserController.getInstance();
//
//        // (2) 로그인 테스트
//        int loginResult = userController.login("user03", "k2c3z4j7");
//        System.out.println("로그인 결과: " + loginResult);
//        // > 0이면 성공(uno), 0이면 실패, -1이면 5회 이상 차단
//
//        // (3) 회원가입 테스트
//        int registerResult = userController.registerMember(
//                "ddd1", "1234", "1234", "유재석", "010-2221-3978", "yoo@sample.com", "2025-07-31"
//        );
//        System.out.println("회원가입 결과: " + registerResult);
//        //  1 : 성공, -1 : 중복, -2 : 비번불일치, -3 : DB오류
//
//        // (4) 아이디 찾기 테스트
//        IdResponseDto foundId = userController.findId("유재석", "010-2221-3978");
//        if (foundId != null) {
//            System.out.println("찾은 아이디: " + foundId.getU_id());
//        } else {
//            System.out.println("일치하는 회원 정보가 없습니다.");
//        }
//
//        UserController ctrl = UserController.getInstance();
//
//        // (예시) 계정 확인 테스트
//        int result = ctrl.verifyAccount("user001", "010-2221-3978");
//        if (result == 1) {
//            System.out.println("사용자 정보가 확인되었습니다.");
//        } else {
//            System.out.println("입력 정보에 맞는 계정을 찾을 수 없습니다.");
//        }
//
//
//        UserController ctrl1 = UserController.getInstance();
//
//        int result3 = ctrl.updatePassword("user001", "newpass1234");
//        if (result == 1) {
//            System.out.println("비밀번호 변경이 완료되었습니다.");
//        } else {
//            System.out.println("비밀번호 변경에 실패했습니다.");
//        }
//
//        boolean ok = ctrl.verifyPassword("user001", "1234");
//        if (ok) {
//            System.out.println("현재 비밀번호가 확인되었습니다.");
//        } else {
//            System.out.println("비밀번호가 일치하지 않습니다.");
//        }
//
//        boolean result4 = ctrl.deleteAccount("user002", "2345");
//        if (result4) {
//            System.out.println("탈퇴 성공했습니다.");
//        } else {
//            System.out.println("탈퇴 실패했습니다.");
//        }
//    }
    }
}
