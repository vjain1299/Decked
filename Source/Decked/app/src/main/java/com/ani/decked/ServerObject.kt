package com.ani.decked

import android.util.EventLog
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class ServerObject {
    init {
        val server = ServerSocket(9999)
        println("Server is running on port ${server.localPort}")

        while (true) {
            val client = server.accept()
            println("Client connected: ${client.inetAddress.hostAddress}")

            // Run client in it's own thread.
            thread { ClientHandler(client).run() }
        }
    }
        }
        class ClientHandler(client: Socket) {
            private val client: Socket = client
            private val reader: Scanner = Scanner(client.getInputStream())
            private val writer: OutputStream = client.getOutputStream()
            private var running: Boolean = false

            fun run() {
                running = true
                // Welcome message
                write( ""
                    //Add Message Here for Client Connection
                )

                while (running) {
                    try {
                        val text = reader.nextLine()
                        if (text == "EXIT") {
                            shutdown()
                            continue
                        }

                        val values = text.split(' ')
                        val result = ""//Handle Result
                        write(result)
                    } catch (ex: Exception) {
                        // TODO: Implement exception handling
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