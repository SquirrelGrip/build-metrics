package com.github.squirrelgrip.build.server

import com.github.squirrelgrip.build.common.infra.DiskDataStorage
import com.github.squirrelgrip.extension.json.toJson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

object Server {
    private val SLASH_REGEX = "/".toRegex()
    private val COLON_REGEX = ":".toRegex()

    @JvmStatic
    fun main(args: Array<String>) {
        val diskDataStorage = DiskDataStorage()
        val server = HttpServer.create(InetSocketAddress(3000), 0)
        server.createContext("/") { httpExchange ->
            httpExchange.use {
                try {
                    Server::class.java.classLoader.getResourceAsStream("index.html").use {
                        httpExchange.sendResponseHeaders(200, 0)
                        it.transferTo(httpExchange.responseBody)
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    httpExchange.sendResponseHeaders(500, e.toString().length.toLong())
                    httpExchange.responseBody.write(e.toString().toByteArray())
                }
            }
        }
        server.createContext("/api/v1/session-summaries") { httpExchange: HttpExchange ->
            val projectId = httpExchange.requestURI.path.split(SLASH_REGEX).dropLastWhile { it.isEmpty() }[4]
            val groupId = projectId.split(COLON_REGEX).dropLastWhile { it.isEmpty() }[0]
            val artifactId = projectId.split(COLON_REGEX).dropLastWhile { it.isEmpty() }[1]
            try {
                val items = diskDataStorage.listSessionSummaries(groupId, artifactId)
                httpExchange.sendResponseHeaders(200, 0)
                items.toJson(httpExchange.responseBody)
            } catch (e: Throwable) {
                e.printStackTrace()
                httpExchange.sendResponseHeaders(500, e.toString().length.toLong())
                httpExchange.responseBody.write(e.toString().toByteArray())
            } finally {
                httpExchange.close()
            }
        }
        server.createContext("/api/v1/session-profiles") { httpExchange: HttpExchange ->
            val id = httpExchange.requestURI.path.split(SLASH_REGEX).dropLastWhile { it.isEmpty() }[4]
            try {
                val item = diskDataStorage.getSessionProfile(id)
                httpExchange.sendResponseHeaders(200, 0)
                item.toJson(httpExchange.responseBody)
            } catch (e: Throwable) {
                e.printStackTrace()
                httpExchange.sendResponseHeaders(500, e.toString().length.toLong())
                httpExchange.responseBody.write(e.toString().toByteArray())
            } finally {
                httpExchange.close()
            }
        }
        server.createContext("/api/v1/project-summaries") { httpExchange: HttpExchange ->
            try {
                val items = diskDataStorage.listProjectSummaries()
                httpExchange.sendResponseHeaders(200, 0)
                items.toJson(httpExchange.responseBody)
            } catch (e: Throwable) {
                e.printStackTrace()
                httpExchange.sendResponseHeaders(500, e.toString().length.toLong())
                httpExchange.responseBody.write(e.toString().toByteArray())
            } finally {
                httpExchange.close()
            }
        }
        println("Open http://localhost:3000 to view build scan results")
        server.start()
    }
}