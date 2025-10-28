package hyundai_4th.car_service;

import hyundai_4th.car_service.config.*;
import hyundai_4th.car_service.service.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CarServiceApplication {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context =
				new AnnotationConfigApplicationContext(CorsConfig.class, SecurityConfig.class);

		// 각 서비스 빈 꺼내기
		DoorService doorService = context.getBean(DoorService.class);
		PaymentService paymentService = context.getBean(PaymentService.class);
		QnaPostService qnaPostService = context.getBean(QnaPostService.class);
		RentalService rentalService = context.getBean(RentalService.class);
		ReservationService reservationService = context.getBean(ReservationService.class);
		SearchService searchService = context.getBean(SearchService.class);
		UserService userService = context.getBean(UserService.class);
		VehicleService vehicleService = context.getBean(VehicleService.class);

		// 테스트용 로그 (필요하면 아래에 각 서비스의 실제 비즈니스 메서드 호출 코드 작성)
		System.out.println("DoorService bean: " + doorService);
		System.out.println("PaymentService bean: " + paymentService);
		System.out.println("QnaPostService bean: " + qnaPostService);
		System.out.println("RentalService bean: " + rentalService);
		System.out.println("ReservationService bean: " + reservationService);
		System.out.println("SearchService bean: " + searchService);
		System.out.println("UserService bean: " + userService);
		System.out.println("VehicleService bean: " + vehicleService);

		// 필요 시 예시 메서드 호출
		// userService.createUser("홍길동", "hong@test.com");
		// reservationService.createReservation(...);
		// vehicleService.registerVehicle(...);

		context.close();
	}
}