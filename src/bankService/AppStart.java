package bankService; // package

import bankService.app.AppRouter;
import bankService.view.UserView;

public class AppStart { // class start
    public static void main(String[] args) {    // main start

//        // view를 총괄하는 AppRouter 실행
//        AppRouter.getInstance().start();
        UserView.getInstance().index();

    }   // main end
}   // class end
