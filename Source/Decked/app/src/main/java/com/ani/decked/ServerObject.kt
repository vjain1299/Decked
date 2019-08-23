package com.ani.decked

import android.util.EventLog
import com.ani.decked.GameState.serverEventManager
import io.grpc.internal.ServerImpl
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class ServerObject(serverEventManager: ServerEventManager) {
    private val server = ServerSocket(9999)
    init {
        println("Server is running on port ${server.localPort}")

        while (true) {
            val client = server.accept()
            println("Client connected: ${client.inetAddress.hostAddress}")

            // Run client in it's own thread.
            thread { ClientHandler(client, serverEventManager).run() }
        }
    }
    fun getIP() : String {
        return(server.inetAddress.hostAddress)
    }
}
        class ClientHandler(private val client: Socket, private val serverEventManager: ServerEventManager) {
            private val reader: Scanner = Scanner(client.getInputStream())
            private val writer: OutputStream = client.getOutputStream()
            private var running: Boolean = false


            fun run() {
                running = true
                // Welcome message
                write( serverEventManager.startGameString )

                while (running) {
                    try {
                        val text = reader.nextLine()
                        val result = serverEventManager.parse(text)
                        if(result != null) {
                            write(result)
                        }
                    } catch (ex: Exception) {
                        write(serverEventManager.endGameString)
                        shutdown()
                    } finally {

                    }

                }
            }

            private fun write(message: String) {
                writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
            }

            private fun shutdown() {
                running = false
                client.close()
                println("${client.inetAddress.hostAddress} closed the connection")
            }
        }