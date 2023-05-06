package com.github.squirrelgrip.build.extension

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.squirrelgrip.build.extension.infra.DiskDataStorage
import com.github.squirrelgrip.extension.json.toJson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.net.InetSocketAddress

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        val diskDataStorage = DiskDataStorage()
        val server = HttpServer.create(InetSocketAddress(3000), 0)
        server.createContext(
            "/"
        ) { t: HttpExchange ->
            try {
                Server::class.java.classLoader.getResourceAsStream("index.html").use { `in` ->
                    t.sendResponseHeaders(200, 0)
                    IOUtils.copy(`in`, t.responseBody)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                t.sendResponseHeaders(500, e.toString().length.toLong())
                t.responseBody.write(e.toString().toByteArray())
            } finally {
                t.close()
            }
        }
        server.createContext(
            "/api/v1/session-summaries"
        ) { t: HttpExchange ->
            val projectId = t.requestURI.path.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[4]
            val groupId = projectId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val artifactId = projectId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            try {
                val items = diskDataStorage.listSessionSummaries(groupId, artifactId)
                t.sendResponseHeaders(200, 0)
                items.toJson(t.responseBody)
            } catch (e: Throwable) {
                e.printStackTrace()
                t.sendResponseHeaders(500, e.toString().length.toLong())
                t.responseBody.write(e.toString().toByteArray())
            } finally {
                t.close()
            }
        }
        server.createContext(
            "/api/v1/session-profiles"
        ) { t: HttpExchange ->
            val id = t.requestURI.path.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[4]
            try {
                val item = diskDataStorage.getSessionProfile(id)
                t.sendResponseHeaders(200, 0)
                t.responseBody.write(item.toByteArray())
            } catch (e: Throwable) {
                e.printStackTrace()
                t.sendResponseHeaders(500, e.toString().length.toLong())
                t.responseBody.write(e.toString().toByteArray())
            } finally {
                t.close()
            }
        }
        server.createContext(
            "/api/v1/project-summaries"
        ) { t: HttpExchange ->
            try {
                val items = diskDataStorage.listProjectSummaries()
                t.sendResponseHeaders(200, 0)
                items.toJson(t.responseBody)
            } catch (e: Throwable) {
                e.printStackTrace()
                t.sendResponseHeaders(500, e.toString().length.toLong())
                t.responseBody.write(e.toString().toByteArray())
            } finally {
                t.close()
            }
        }
        println("Open http://localhost:3000 to view build scan results")
        server.start()
    }
}