package com.helpie.backend.support;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectionUtils {
    private final DataSource datasource;

    public ConnectionSnapshot snapshot() {
        HikariPoolMXBean mxBean = ((HikariDataSource) datasource).getHikariPoolMXBean();
        return new ConnectionSnapshot(mxBean.getActiveConnections(), mxBean.getIdleConnections());
    }

    public void logSnapshot(String tag, ConnectionSnapshot s) {
        log.info("[{}] 현재 active인 connection의 수 : {}", tag, s.active());
        log.info("[{}] 현재 idle인 connection의 수 : {}", tag, s.idle());
    }

    public void onAfterCityLookup(ConnectionSnapshot snapshot) {

    }

}
