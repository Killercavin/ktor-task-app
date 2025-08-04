package com.example.configs

import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun init() {
        val dbUrl: String = EnvConfig.getEnv("DATABASE_URL") ?: ""
        val dbUser = EnvConfig.getEnv("DATABASE_USER") ?: ""
        val dbPassword = EnvConfig.getEnv("DATABASE_PASSWORD") ?: ""

        logger.info("Initializing the database connection...")
        try {
            Database.connect(
                url = dbUrl,
                user = dbUser,
                password = dbPassword
            )
            logger.info("Database connection successful...")
        } catch (e: Exception) {
            logger.error("Database connection failed!", e)
        }
    }
}