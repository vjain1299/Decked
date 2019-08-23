    package com.ani.decked

    import android.util.EventLog
    import java.io.OutputStream
    import java.net.InetAddress
    import java.net.ServerSocket
    import java.net.Socket
    import java.nio.charset.Charset
    import java.util.*
    import kotlin.concurrent.thread

    class ClientObject(ipAddress : String, clientEventManager: ClientEventManager) {
        init {
            val client = Socket(InetAddress.getByAddress(ipAddress.toByteArray()),9999)
            println("Client is running on port ${client.port}")

            while (!client.isConnected) {
                println("Client connected: ${client.inetAddress.hostAddress}")
            }
            ServerHandler(client, clientEventManager)
        }
    }
    class ServerHandler(private val client: Socket, private val clientEventManager: ClientEventManager) {
        private val reader: Scanner = Scanner(client.getInputStream())
        private val writer: OutputStream = client.getOutputStream()
        private var running: Boolean = false


        fun run() {
            running = true
            write(clientEventManager.startGameString)

            while (running) {
                try {
                    val text = reader.nextLine()
                    val result = clientEventManager.parse(text)
                    if (result != null) {
                        write(result)
                    }
                } catch (ex: Exception) {
                    write(clientEventManager.endGameString)
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