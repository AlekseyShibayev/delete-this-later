package com.company.app.core.aop.logging.performance;

import com.company.app.core.aop.logging.performance.component.GuidExtractor;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Аспект, логирует разными способами производительность метода.
 * Метод должен быть помечен аннотацией @PerformanceLogAnnotation
 * <p>
 * Способы логирования:
 * 1. Стандартный, пример:
 * PerformanceLogAspect] [] [] [76069a35-dab7-45bf-968e-a24281ac5a21]: запущен com.company.app.exchangeRate.ExchangeRateFacade.extract
 * PerformanceLogAspect] [] [] [76069a35-dab7-45bf-968e-a24281ac5a21]: за [1757] ms выполнен com.company.app.exchangeRate.ExchangeRateFacade.extract
 * 2. Для Collection - пишет размер, пример:
 * PerformanceLogAspect] [] [] [42db76b0-f2e2-4994-9b12-6fa603e62e72]: запущен com.company.app.wildberries.WildberriesFacade.getDesiredLots
 * PerformanceLogAspect] [] [] [42db76b0-f2e2-4994-9b12-6fa603e62e72]: за [351] ms вернул [0] шт. выполнен com.company.app.wildberries.WildberriesFacade.getDesiredLots
 * <p>
 * Способы получения GUID: (см. тест PerformanceLogAspectTest и PerformanceLogAspectExecutorTest)
 * 1. Аннотация без параметров. Пример: @PerformanceLogAnnotation
 * Попытается вытащить GUID из первого объекта в сигнатуре.
 * 2. В аннотацию можно передать параметры.
 * number - порядковый номер объекта в сигнатуре метода, начинается с 0.
 * methodName - метод объекта, возвращающего UUID или String в формате UUID. Метод должен быть без аргументов.
 * fieldName - поле объекта, содержащее UUID или String в формате UUID.
 * Передавать парами: number и methodName ИЛИ number и fieldName.
 * Пример: @PerformanceLogAnnotation(number = "0", fieldName = "guid") или @PerformanceLogAnnotation(number = "0", methodName = "getGuidMethod")
 * 3. В случае неудачи - будет сгенерирован случайный GUID.
 *
 * @author shibaev.aleksey 30.03.2023
 */
@Slf4j
@Aspect
@Component
public class PerformanceLogAspect {

	@Autowired
	GuidExtractor guidExtractor;

	@Pointcut("@annotation(PerformanceLogAnnotation)")
	public void ifPerformanceLogAnnotation() {
	}

	@SneakyThrows
	@Around("ifPerformanceLogAnnotation()")
	public Object ifPerformanceLogAnnotationAdvice(ProceedingJoinPoint proceedingJoinPoint) {
		if (log.isDebugEnabled()) {
			Stopwatch stopwatch = Stopwatch.createStarted();
			Signature signature = proceedingJoinPoint.getSignature();
			String guid = guidExtractor.extractGuid(proceedingJoinPoint);
			doLogBefore(guid, signature);

			Object proceed = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());

			stopwatch.stop();
			doLogAfter(stopwatch, guid, signature, proceed);
			return proceed;
		} else {
			return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
		}
	}

	private void doLogAfter(Stopwatch stopwatch, String guid, Signature signature, Object proceed) {
		if (proceed instanceof Collection) {
			doCollectionPerformanceLog(stopwatch, guid, signature, (Collection<?>) proceed);
		} else {
			doDefaultPerformanceLog(stopwatch, guid, signature);
		}
	}

	private void doLogBefore(String guid, Signature signature) {
		log.debug("[{}]: запущен {}.{}",
				guid,
				signature.getDeclaringType().getName(),
				signature.getName()
		);
	}

	private void doCollectionPerformanceLog(Stopwatch stopwatch, String guid, Signature signature, Collection<?> proceed) {
		log.debug("[{}]: за [{}] ms вернул [{}] шт. выполнен {}.{}",
				guid,
				stopwatch.elapsed(TimeUnit.MILLISECONDS),
				proceed.size(),
				signature.getDeclaringType().getName(),
				signature.getName()
		);
	}

	private void doDefaultPerformanceLog(Stopwatch stopwatch, String guid, Signature signature) {
		log.debug("[{}]: за [{}] ms выполнен {}.{}",
				guid,
				stopwatch.elapsed(TimeUnit.MILLISECONDS),
				signature.getDeclaringType().getName(),
				signature.getName()
		);
	}
}