package com.governance.repository

import com.governance.repository.entities.RequestEntity
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager

@Service
class CacheService(private val dbPath: String = "governanca.db") {
    private var connection: Connection? = null

    init {
        initDatabase()
    }

    /**
     * Inicializa o banco de dados SQLite
     */
    private fun initDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
            connection?.createStatement()?.execute(
                """
                CREATE TABLE IF NOT EXISTS requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    model TEXT NOT NULL,
                    tokens INTEGER NOT NULL,
                    cost REAL NOT NULL,
                    input_text TEXT,
                    output_text TEXT,
                    api_key_hash TEXT NOT NULL
                );
                """.trimIndent()
            )
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Verifica se um job já foi enviado
     */
    fun findByDate(date: String): List<RequestEntity> {
        val stmt = connection?.prepareStatement(
            "SELECT * FROM requests WHERE DATE(timestamp) = DATE(?)"
        )
        stmt?.setString(1, date)
        val rs = stmt?.executeQuery()
        val list = emptyList<RequestEntity>().toMutableList().apply {
            while (rs?.next() == true) {
                // Mapear resultados para RequestEntity e adicionar a uma lista
                val request = RequestEntity(
                    id = rs.getLong("id"),
                    timestamp = rs.getString("timestamp"),
                    model = rs.getString("model"),
                    tokens = rs.getInt("tokens"),
                    cost = rs.getDouble("cost"),
                    inputText = rs.getString("input_text"),
                    outputText = rs.getString("output_text"),
                    apiKeyHash = rs.getString("api_key_hash")
                )
                this.add(request)
            }
        }
        rs?.close()
        stmt?.close()
        return list
    }

    fun save(requestEntity: RequestEntity) {
        val stmt = connection?.prepareStatement(
            """
            INSERT OR IGNORE INTO requests (timestamp, model, tokens, cost, input_text, output_text, api_key_hash)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
        )
        stmt?.setString(1, requestEntity.timestamp)
        stmt?.setString(2, requestEntity.model)
        stmt?.setInt(3, requestEntity.tokens)
        stmt?.setDouble(4, requestEntity.cost)
        stmt?.setString(5, requestEntity.inputText)
        stmt?.setString(6, requestEntity.outputText)
        stmt?.setString(7, requestEntity.apiKeyHash)
        stmt?.executeUpdate()
        stmt?.close()
    }
}