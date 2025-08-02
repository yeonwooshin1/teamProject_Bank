package bankService.service;    // package


import bankService.model.dto.OtpDto;
import bankService.util.HashUtil;
import bankService.util.RandomUtil;

import java.time.Duration;
import java.time.Instant;

public class OtpService {   // class start

    // 절대 변하지 않는 상수
    private static final Duration INPUT_UNTIL = Duration.ofMinutes(2); // 발급 후 입력시간 2분 제한
    private static final Duration VALID_UNTIL = Duration.ofMinutes(2); // 성공 후 유효시간 2분 신뢰
    private static final int MAX_ATTEMPTS = 3;                          // 실패 횟수 최대 3회

    // Otp 발급 상태 dto -> null 이면 발급 전/만료
    private OtpDto session;

    // 현재 활성화된 otp (없을 수도 있음)
    public OtpDto getCurrent() {
        return session;
    }   // func end




    // otp 발행 메소드
    public String issue() {
        // 난수 생성 util 에서 가져온 메소드
        String otp = RandomUtil.createRandomNum(6);       // "111111" "123456" 같은 숫자 문자열
        // 해시 생성 util 에서 가져온 메소드
        String hashcode = HashUtil.sha256Hex(otp);              // 6자리 숫자 문자열을 해시화

        // Instant.now(); -> 이 객체를 컴파일한 지금 현재 시간을 담는 것
        // ** Instant : 날짜와 시간을 초단위(정확히는 나노초)로 표현하는 클래스 **
        Instant now = Instant.now();

        // OtpDto 객체 생성
        OtpDto dto = new OtpDto();

        // 해시 문자열 setter    : 위 지역변수인 hashcode 대입
        dto.setOtpHashHex(hashcode);
        // 발급된 시각 setter    : 위 지역변수 now 대입
        dto.setIssuedAt(now);
        // 입력 마감시간 setter   : 현재 시간(now) + INPUT_UNTIL( 내가 설정한 2분 ) => 2분 동안 유효시간
        // ** Instant.plus()  지정된 시각에 ()안의 값을 더해서 시각을 나타내는 메소드 **
        dto.setSubmitUntil(now.plus(INPUT_UNTIL));
        // 실패 시도횟수 setter : 초기값 0
        dto.setAttempts(0);
        // Otp 인증 성공시 쓸 수 있는 유효기간 setter : 지금은 발급 단계니 null 초기값 설정.
        dto.setTrustUntil(null);

        // Otp 발급 상태 전 빈 dto에 해당 dto 넣어주기
        session = dto;

        // otp 값을 사용자에게 반환함.
        return otp;
    }   // func end



    // 입력한 otp 값이 맞는지 확인하는 메소드
    // int 반환값 , 사용자가 입력한 inputOtp 를 매개변수로 사용
    public int verify(String inputOtp) {
        // 혹시나 session 값이 없으면 1 반환
        if (session == null) return 1;  // println("OTP 세션이 존재하지 않거나 만료되었습니다. 새로운 OTP를 발급받아 주시기 바랍니다.")

        // Instant.now(); -> 이 객체를 컴파일한 지금 현재 시간을 담는 것
        // ** Instant : 날짜와 시간을 초단위(정확히는 나노초)로 표현하는 클래스 **
        Instant now = Instant.now();

        // 2분 유효기간 체크
        // if )) 현재 시각이 내가 setter한 입력 마감 시간보다 더 이후라면?
        // ** isAfter() : Instant 객체가 ()보다 이후인지 여부를 반환하는 함수 **
        if(now.isAfter(session.getSubmitUntil())) {
            session = null;     // Otp 값 null;     왜? 만료시각보다 더 이후니까
            return 2;           // 2 반환         // println("OTP 입력 유효 시간이 경과되었습니다. 안전을 위해 새로운 OTP를 발급받아 주시기 바랍니다.")
        }   // if end

        // 사용자가 입력한 otp 값 해시 산출
        String inputHashOtp = HashUtil.sha256Hex(inputOtp);

        // 문자열 비교
        // 입력한 otp 값이랑 setter한 해시값이랑 맞는지 확인함
        // safeEquals 헬퍼 메소드 사용 (맨 아래 참조)
        // 반환값 true , false
        boolean match = safeEquals(inputHashOtp , session.getOtpHashHex());

        // match 유효성 검사
        if (!match) {
            // 실패 시도 추가
            session.setAttempts(session.getAttempts() + 1);
            // if )) 실패 시도 횟수가 지정한 시도보다 같거나 많으면?
            if( session.getAttempts() >= MAX_ATTEMPTS ) {
                session = null;     // Otp 값 null;     왜? 시도 횟수를 넘었으니까
                return 3;           // 3 반환         // println("OTP 입력 시도 횟수를 초과하였습니다. 잠시 후 다시 시도하시거나 새로운 OTP를 발급받아 주시기 바랍니다.")
            }   // if end
            return  4;     // println("입력하신 OTP가 일치하지 않습니다. 정확히 확인하신 후 다시 시도하여 주시기 바랍니다. \n * 3회 실패시 새로운 OTP 발급필요 *")
        }   // if end

        // 모든 유효성 검사가 끝났다면 성공값 리턴과 검증 유효시간 설정
        // ** Instant.plus()  지정된 시각에 ()안의 값을 더해서 시각을 나타내는 메소드 **
        // VALID_UNTIL : 위에 지정한 유효한 시간 상수값 ====> 현재 설정 2(분)
        session.setTrustUntil(now.plus(VALID_UNTIL));
        return 5;   // println("OTP 인증이 성공적으로 완료되었습니다. * OTP 유효기간은 현재시각부터 5분입니다. *")
    }   // func end






    // 5분 otp 성공 기간인지 확인하는 메소드 : controller 에서 사용.
    public boolean checkValidUntil() {
        // if )) 만약 세션이 없거나 , 세션 검증 성공이 null(검증 전) 이면 false 반환
        if (session == null || session.getTrustUntil() == null) return false;
        // now(현재 시각)보다 dto에 저장된 검증 성공 시간 (발급 후 2분)보다 이후가 아니라면 true 아니면 false를 반환
        return !Instant.now().isAfter(session.getTrustUntil());
    }   // func end

    // 재인증을 원하면 새로 발급하는 메소드
    public String reissue() {
        return issue();
    }   // func end


    // 입력한 otp 값이랑 지정된 otp 값이 일치하는지 확인하는 *헬퍼* 메소드
    private boolean safeEquals(String a, String b) {
        // a 나 b 값이 null 이면 바로 false
        if (a == null || b == null) return false;
        // a b 길이가 다르면 바로 false
        if (a.length() != b.length()) return false;

        // a와 b의 차이를 int 변수로 줌. 0으로 초기화한 값
        int different = 0;

        // a.charAt(i) ^ b.charAt(i) : 두 문자의 코드 포인트를 비트 단위로 비교 , 같으면 결과 0, 다르면 0 이외의 값
        // different |= ... : 이전까지의 누적값(different)에 새 차이값을 OR 연산이라네요.
        // 즉 한 번이라도 달랐으면 different 에 0 이외의 비트가 남음
        for (int i = 0; i < a.length(); i++) {
            different |= a.charAt(i) ^ b.charAt(i);
        }   // for end
        // 차이가 0 이라면 true 아니라면 false 반환
        return different == 0;
    }   // func end

}   // class end
