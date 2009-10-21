package se.bluebrim.crud.tutorial.server;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import se.bluebrim.crud.server.ServerFaultBarrierInterceptor;

/**
 * Applies the around advice implemented in the superclass to all methods in
 * classes with a name ending with "Service" and belonging to the package 
 * <code>"se.bluebrim.crud"</code> or any sub package<br>
 * The syntax for the point cut expression can be found at:
 * http://static.springframework.org/spring/docs/2.0.x/reference/aop.html 
 * 
 * @author GStack
 *
 */
@Aspect
@Order(1)
public class LoggingInterceptor extends ServerFaultBarrierInterceptor {

	@SuppressWarnings("unchecked")
	@Around(value="execution(* se.bluebrim.crud..*Service.*(..))")
	@Override
	public Object around(ProceedingJoinPoint pjp) throws Throwable 
	{
		return super.around(pjp);
	}

}
