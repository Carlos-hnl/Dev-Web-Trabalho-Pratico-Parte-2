package com.taskmanager.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Pool de Conexões com PostgreSQL usando HikariCP.
 * Padrão Singleton — garante uma única instância do pool na aplicação.
 * Camada: DAO (Model)
 *
 * Configuração via variáveis de ambiente (recomendado) ou constantes.
 * Variáveis de ambiente:
 *   DB_URL      → jdbc:postgresql://localhost:5432/taskmanager
 *   DB_USER     → seu_usuario
 *   DB_PASSWORD → sua_senha
 */
public class ConexaoPool {

    private static HikariDataSource dataSource;

    // ── Configurações padrão (sobrescritas pelas env vars) ───────
    private static final String DEFAULT_URL  = "jdbc:postgresql://localhost:5432/taskmanager";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASS = "postgres";

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getEnv("DB_URL",      DEFAULT_URL));
        config.setUsername(getEnv("DB_USER",     DEFAULT_USER));
        config.setPassword(getEnv("DB_PASSWORD", DEFAULT_PASS));

        config.setDriverClassName("org.postgresql.Driver");

        // Pool de conexões
        config.setMaximumPoolSize(10);          // máx. 10 conexões simultâneas
        config.setMinimumIdle(2);               // mínimo 2 conexões ociosas
        config.setIdleTimeout(30_000);          // 30 s para fechar conexão ociosa
        config.setConnectionTimeout(20_000);    // 20 s de timeout ao obter conexão
        config.setMaxLifetime(1_800_000);       // 30 min de vida máxima

        // Cache de prepared statements
        config.addDataSourceProperty("cachePrepStmts",          "true");
        config.addDataSourceProperty("prepStmtCacheSize",       "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit",   "2048");

        config.setPoolName("TaskManager-Pool");

        dataSource = new HikariDataSource(config);
    }

    private ConexaoPool() {}

    /**
     * Retorna uma conexão do pool. Sempre use em try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Fecha o pool (chamado no shutdown da aplicação).
     */
    public static void fecharPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
