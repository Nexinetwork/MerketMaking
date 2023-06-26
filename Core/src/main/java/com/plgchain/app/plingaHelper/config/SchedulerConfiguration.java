/**
 *
 */
package com.plgchain.app.plingaHelper.config;

import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;

/**
 *
 */
@Configuration
public class SchedulerConfiguration implements Serializable {

	private static final long serialVersionUID = -656684566035286207L;

	@Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

}
