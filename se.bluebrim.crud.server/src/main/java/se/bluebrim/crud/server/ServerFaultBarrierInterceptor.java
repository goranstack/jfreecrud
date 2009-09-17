package se.bluebrim.crud.server;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import se.bluebrim.crud.exception.InternalServerException;

/**
 * Superclass to interceptor classes that logs calls to the certain classes. It also catches exceptions and handles them like:
 * - If the exception is declared to be thrown in the method interface, the exception is re-thrown
 * - If not, the exception is logged and a new generic InternalServerException with a reference code is thrown to the client.
 *   This hides server details for the client as well as avoiding ClassNotFoundExceptions when trying 
 *   to serialize an exception that does not exist in the client
 *   
 * The class only logs the exception if it is the first interceptor, in order to avoid multiple logging in the case of 
 * nested calls to other managers (one manger calls another manager)
 * 
 * @author OPalsson
 *
 */
public abstract class ServerFaultBarrierInterceptor {
	private static Logger logger = Logger.getLogger(ServerFaultBarrierInterceptor.class); 
	private static final ThreadLocal<Boolean> firstInterceptorFlag = new ThreadLocal<Boolean>();

	public Object around(ProceedingJoinPoint pjp) throws Throwable {

		boolean isFirstInterceptor = setFirstInterceptorFlag();
		try {
			String methodName = pjp.getSignature().getDeclaringTypeName() + ":" + pjp.getSignature().getName();
			logger.debug("Call method " + methodName);
			//logger.trace("First interceptor: " + isFirstInterceptor);
			Object retVal = pjp.proceed();
			clearFirstInterceptorFlag(isFirstInterceptor);
			if (retVal instanceof Collection)
				logger.debug("Returned from " + methodName + " result size " + ((Collection)retVal).size());
			else
				logger.debug("Returned from " + methodName);
			return retVal;
		} catch (Throwable e) {
			clearFirstInterceptorFlag(isFirstInterceptor);
			if (isFirstInterceptor) {
				// Do logging if this is the first interceptor
				MethodSignature method = (MethodSignature) pjp.getSignature();
				for (int i = 0; i < method.getExceptionTypes().length; i++) {
					Class interfaceException = method.getExceptionTypes()[i];
					if (interfaceException.isAssignableFrom(e.getClass())) {
						// Planned exception - simply re-throw it
						logger.info("Re-throw exception since it exists in interface definition: " + e.toString());
						throw e;
					}
				}
				
				// Unplanned exception - log it and throw a generic exception with error code
				String errorCode = createErrorCode(); 
				logger.warn("Unexcpected server exception: " + errorCode, e);
				throw new InternalServerException(errorCode);
			}
			else {
				// Re-throw to next interceptor
				throw e;
			}
		}
		
	}

	private boolean setFirstInterceptorFlag() {
		boolean isFirstInterceptor = firstInterceptorFlag.get() == null;
		if (isFirstInterceptor) {
			firstInterceptorFlag.set(true);
		}
		return isFirstInterceptor;
	}

	private void clearFirstInterceptorFlag(boolean isFirstInterceptor) {
		if (isFirstInterceptor) {
			//logger.debug("Clean firstInterceptorFlag");
			firstInterceptorFlag.set(null);
		}
	}

	private String createErrorCode() {
		return "ERROR_" + System.currentTimeMillis();
	}	
}
