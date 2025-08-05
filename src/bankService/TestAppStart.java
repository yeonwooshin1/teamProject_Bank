package bankService;

import bankService.app.ConsoleSession;
import bankService.app.ConsoleSessionManager;
import bankService.controller.AccountController;
import bankService.model.dao.AccountDao;
import bankService.model.dto.TransactionDto;
import bankService.model.dto.TransactionResultDto;
import bankService.model.dto.TransferDto;
import bankService.model.dto.TransferResultDto;
import bankService.view.MainView;

public class TestAppStart {
    public static void main(String[] args) {

        // MainView test
         //MainView.getInstance().mainIndex();

//        // db 연동 테스트
//        AccountDao dao = AccountDao.getInstance();
//        System.out.println("db 연결 테스트 ");
//
//        // 계좌 조회 테스트
//        boolean isVaild = dao.isAccount("111-228-586525" , "563381");
//        System.out.println("계좌 유효성 검사" + (isVaild ? "성공" : "실패"));
//
//        // 계좌 로그번호 조회 테스트
//        int acno = dao.getAcnoByAccountNo("111-228-586525");
//        System.out.println(acno);
//
//        // 계좌 잔액 조회 테스트
//        int balance = dao.isBalance(acno);
//        System.out.println("현재 잔액" + balance + "원");
//
//
//        // controller 테스트
//        System.out.println("\n컨트롤러 테스트");
//        AccountController accountController = AccountController.getInstance();
//
//        // 입금 테스트
//
//        TransactionDto depositDto = new TransactionDto("111-228-586525" , "563381" , 10000  );
//        TransactionResultDto depositResultDto = accountController.deposit(depositDto);
//        System.out.println("[입금 결과]" + depositResultDto.getMessage() + "잔액 : " + depositResultDto.getBalance() );
//
//        // 출금 테스트
//
//        TransactionDto withdrawDto = new TransactionDto("111-228-586525" , "563381" , 2000);
//        TransactionResultDto withdrawResultDto = accountController.withdraw(withdrawDto);
//        System.out.println("[출금 결과]" + withdrawResultDto.getMessage() + "잔액 : " + withdrawResultDto.getBalance());
//
//        // 이체 테스트
//        TransferDto transferDto = new TransferDto("111-228-586525" , "111-281-306773" , "563381" , 3000 , "테스트");
//        TransferResultDto transferResultDto = accountController.transfer(transferDto);
//        System.out.println("[이체 결과]" + transferResultDto.getMessage() + "잔액 : " + transferResultDto.getBalance());



//          //계좌 등록 테스트
//            AccountController controller = AccountController.getInstance();
//            String testPwd = "test1234";
//            boolean result = controller.accountAdd(testPwd);
//            if(result){ System.out.println("계좌 등록 성공");}
//            else { System.out.println("계좌 등록 실패");}
//
//        // 계좌 해지 테스트
//        int uno = 1;
//        String test = "111-301-225766";
//        String paw = "test1234";
//        boolean result1 = controller.accountDel(uno , test, paw);
//        if (result1) {
//            System.out.println("성공");
//        } else {
//            System.out.println("실패");
//
//        }

//        // 계좌 조회 테스트
//        BankView view = BankView.getInstance();
//        int testuno = 1;
//
//        System.out.println("거레 조회 테스트");
//        view.accountList(testuno);


    } // main e
} // class e
