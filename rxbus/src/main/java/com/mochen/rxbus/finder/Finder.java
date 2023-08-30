package com.mochen.rxbus.finder;


import com.mochen.rxbus.entity.EventType;
import com.mochen.rxbus.entity.SubscriberEvent;

import java.util.Map;
import java.util.Set;

/**
 * Finds producer and subscriber methods.
 */
public interface Finder {

    Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener);


    Finder ANNOTATED = AnnotatedFinder::findAllSubscribers;
}
