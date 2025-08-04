package bankService;


import bankService.View.BankView;
import bankService.controller.AccountddController;
import bankService.model.dao.AccountddDao;


public class AppStart {
    public static void main(String[] args) {

  BankView.getInstance().index();

//        AccountddDao dao = AccountddDao.getInstance();  // 싱글톤이라 가정
//
//
//        // 계좌 등록 테스트
//        AccountddController controller = AccountddController.getInstance();
//        String testPwd = "test1234";
//        boolean result = controller.AccountAdd(testPwd);
//
//        if(result){
//            System.out.println("계좌 등록 성공");
//        }else {
//            System.out.println("계좌 등록 실패");
//        }

//         // 계좌 해지 테스트
//        AccountddController cn = AccountddController.getInstance();
//        String test = "111-301-234655";
//        String paw = "test1234";
//        boolean result = cn.AccountDel(test,paw);
//        if (result){
//            System.out.println("성공");
//        }else {
//            System.out.println("실패");


        }
    }
