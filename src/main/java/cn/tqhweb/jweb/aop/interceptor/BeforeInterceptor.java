package cn.tqhweb.jweb.aop.interceptor;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.tqhweb.jweb.aop.Interceptor;
import cn.tqhweb.jweb.aop.Invocation;
import cn.tqhweb.jweb.aop.annotation.Before;

public class BeforeInterceptor implements Interceptor {
	private static Map<Class<? extends Interceptor>, Interceptor> interCacheMap = new ConcurrentHashMap<>();

	public Object intercept(Invocation invocation) {
		Set<Interceptor> set = new HashSet<>();
		intercept(invocation.getTargetClass(), set);
		intercept(invocation.getMethod(), set);
		invocation.setInterceptors(set.iterator());
		return invocation.invoke();
	}

	private void intercept(AnnotatedElement element, Collection<Interceptor> set) {
		Before before = element.getDeclaredAnnotation(Before.class);
		if (before != null) {
			for (Class<? extends Interceptor> clazz : before.value()) {
				set.add(getInterceptor(clazz));
			}
		}
	}

	public Interceptor getInterceptor(Class<? extends Interceptor> clazz) {
		Interceptor interceptor = interCacheMap.get(clazz);
		if (interceptor == null) {
			try {
				interceptor = clazz.getConstructor().newInstance();
				interCacheMap.put(clazz, interceptor);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		return interceptor;
	}
}
