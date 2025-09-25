package bankService; // package

// import bankService.app.AppRouter;
import bankService.view.MainView;
import bankService.view.MainView2;
import bankService.view.UserView;
import bankService.view.UserView2;


public class AppStart { // class start
    public static void main(String[] args) {    // main start

       // view를 총괄하는 AppRouter 실행

       // AppRouter.getInstance().start();

        UserView2.getInstance().index();
        MainView2.getInstance().mainIndex();


    }   // main end
}   // class end