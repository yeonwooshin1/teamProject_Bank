package bankService; // package

import bankService.app.AppRouter;

public class AppStart { // class start
    public static void main(String[] args) {    // main start

        // view를 총괄하는 AppRouter 실행
        AppRouter.getInstance().start();

    }   // main end
}   // class end
