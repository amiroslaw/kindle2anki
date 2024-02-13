package ovh.miroslaw.kindle2anki.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Objects;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "dictionaryEntityManagerFactory",
        transactionManagerRef = "dictionaryTransactionManager",
        basePackages = {"ovh.miroslaw.kindle2anki.dictionary.repository"}
)
public class DictionaryDBConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.dictionary")
    public DataSourceProperties studentDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.dictionary.configuration")
    public DataSource dictionaryDataSource() {
        return studentDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean dictionaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dictionaryDataSource") DataSource dataSource
    ) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("spring.jpa.database-platform", "org.hibernate.community.dialect.SQLiteDialect");

        return builder.dataSource(dataSource)
                .properties(properties)
                .packages("ovh.miroslaw.kindle2anki.dictionary.model")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager dictionaryTransactionManager(@Qualifier(
            "dictionaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean dictionaryEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(dictionaryEntityManagerFactory.getObject()));
    }
}

