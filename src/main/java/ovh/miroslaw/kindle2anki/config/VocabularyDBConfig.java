package ovh.miroslaw.kindle2anki.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "vocabularyEntityManagerFactory",
        transactionManagerRef = "vocabularyTransactionManager",
        basePackages = {"ovh.miroslaw.kindle2anki.vocabulary.repository"}
)
public class VocabularyDBConfig {

    
    @Bean
    @ConfigurationProperties("spring.datasource.vocab")
    public DataSourceProperties vocabularyDataSourceProperties() {
        return new DataSourceProperties();
    }

    
    @Bean
    @ConfigurationProperties("spring.datasource.vocab.configuration")
    public DataSource vocabularyDataSource() {
        return vocabularyDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    
    @Bean
    public LocalContainerEntityManagerFactoryBean vocabularyEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("vocabularyDataSource") DataSource dataSource
    ) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.database-platform", "org.hibernate.community.dialect.SQLiteDialect");
        return builder.dataSource(dataSource)
                .properties(properties)
                .packages("ovh.miroslaw.kindle2anki.vocabulary.model")
                .build();
    }

    
    @Bean
    public PlatformTransactionManager vocabularyTransactionManager(@Qualifier(
            "vocabularyEntityManagerFactory") LocalContainerEntityManagerFactoryBean vocabularyEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(vocabularyEntityManagerFactory.getObject()));
    }
}

