package com.mochen.rxbus.finder;


import com.mochen.rxbus.annotation.Subscribe;
import com.mochen.rxbus.annotation.Tag;
import com.mochen.rxbus.entity.Default;
import com.mochen.rxbus.entity.EventType;
import com.mochen.rxbus.entity.SubscriberEvent;
import com.mochen.rxbus.thread.EventThread;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper methods for finding methods annotated with {@link Subscribe}.
 */
public final class AnnotatedFinder {

    /**
     * Cache event bus producer methods for each class.
     */
    private static final ConcurrentMap<Class<?>, Map<EventType, SourceMethod>> PRODUCERS_CACHE =
            new ConcurrentHashMap<>();

    /**
     * Cache event bus subscriber methods for each class.
     */
    private static final ConcurrentMap<Class<?>, Map<EventType, Set<SourceMethod>>> SUBSCRIBERS_CACHE =
            new ConcurrentHashMap<>();

    private static void loadAnnotatedProducerMethods(Class<?> listenerClass,
                                                     Map<EventType, SourceMethod> producerMethods) {
        Map<EventType, Set<SourceMethod>> subscriberMethods = new HashMap<>();
        loadAnnotatedMethods(listenerClass, producerMethods, subscriberMethods);
    }

    private static void loadAnnotatedSubscriberMethods(Class<?> listenerClass,
                                                       Map<EventType, Set<SourceMethod>> subscriberMethods) {
        Map<EventType, SourceMethod> producerMethods = new HashMap<>();
        loadAnnotatedMethods(listenerClass, producerMethods, subscriberMethods);
    }

    /**
     * Load all methods annotated with  {@link Subscribe} into their respective caches for the
     * specified class.
     */
    private static void loadAnnotatedMethods(Class<?> listenerClass,
                                             Map<EventType, SourceMethod> producerMethods, Map<EventType, Set<SourceMethod>> subscriberMethods) {
        for (Method method : listenerClass.getDeclaredMethods()) {
            // The compiler sometimes creates synthetic bridge methods as part of the
            // type erasure process. As of JDK8 these methods now include the same
            // annotations as the original declarations. They should be ignored for
            // subscribe/produce.
            if (method.isBridge()) {
                continue;
            }
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length < 1) {
                    parameterTypes = new Class[]{Default.class};
                }
                if (parameterTypes.length > 1) {
                    throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation but requires "
                            + parameterTypes.length + " arguments.  Methods must require a single argument.");
                }

                Class<?> parameterClazz = parameterTypes[0];
                if (parameterClazz.isInterface()) {
                    throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation on " + parameterClazz
                            + " which is an interface.  Subscription must be on a concrete class type.");
                }


                if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
                    throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation on " + parameterClazz
                            + " but is not 'public'.");
                }

                Subscribe annotation = method.getAnnotation(Subscribe.class);
                EventThread thread = annotation.thread();
                Tag[] tags = annotation.tags();
                int tagLength = (tags == null ? 0 : tags.length);
                do {
                    String tag = Tag.DEFAULT;
                    if (tagLength > 0) {
                        tag = tags[tagLength - 1].value();
                    }
                    EventType type = new EventType(tag, parameterClazz);
                    Set<SourceMethod> methods = subscriberMethods.get(type);
                    if (methods == null) {
                        methods = new HashSet<>();
                        subscriberMethods.put(type, methods);
                    }
                    methods.add(new SourceMethod(thread, method));
                    tagLength--;
                } while (tagLength > 0);
            }
        }

        PRODUCERS_CACHE.put(listenerClass, producerMethods);
        SUBSCRIBERS_CACHE.put(listenerClass, subscriberMethods);
    }

    /**
     * This implementation finds all methods marked with a {@link Subscribe} annotation.
     */
    static Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener) {
        Class<?> listenerClass = listener.getClass();
        Map<EventType, Set<SubscriberEvent>> subscribersInMethod = new HashMap<>();

        Map<EventType, Set<SourceMethod>> methods = SUBSCRIBERS_CACHE.get(listenerClass);
        if (null == methods) {
            methods = new HashMap<>();
            loadAnnotatedSubscriberMethods(listenerClass, methods);
        }
        if (!methods.isEmpty()) {
            for (Map.Entry<EventType, Set<SourceMethod>> e : methods.entrySet()) {
                Set<SubscriberEvent> subscribers = new HashSet<>();
                for (SourceMethod m : e.getValue()) {
                    subscribers.add(new SubscriberEvent(listener, m.method, m.thread));
                }
                subscribersInMethod.put(e.getKey(), subscribers);
            }
        }

        return subscribersInMethod;
    }

    private AnnotatedFinder() {
        // No instances.
    }

    private static class SourceMethod {
        private EventThread thread;
        private Method method;

        private SourceMethod(EventThread thread, Method method) {
            this.thread = thread;
            this.method = method;
        }
    }

}
