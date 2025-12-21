package com.helpie.backend.support;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ConnectionCheckAspect {

    private final ConnectionUtils connectionUtils;

    @AfterReturning("execution(* com.helpie.backend..GroupFacade.validateCity(..))")
    public void afterLoadCity() {
        ConnectionSnapshot snapshot = connectionUtils.snapshot();
        connectionUtils.logSnapshot("after-city-findById", snapshot);
        connectionUtils.onAfterCityLookup(snapshot);
    }
}
