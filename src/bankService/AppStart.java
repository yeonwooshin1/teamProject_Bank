package bankService;

import bankService.controller.UserController;
import bankService.model.dto.UserDto;
import bankService.view.UserView;

import java.util.ArrayList;

public class AppStart {
    public static void main(String[] args) {
        //UserView.getInstance().index();

        // (1) 테스트할 컨트롤러 싱글톤 가져오기
        UserController userController = UserController.getInstance();
//      // (2) 주석 처리 하면서 단위 기능 테스트
//          // 1) 로그인 테스트
        int result = userController.login("user001", "1234");
        System.out.println(result);

    }
}
