    package com.ani.decked

    import android.util.EventLog
    import java.io.OutputStream
    import java.net.InetAddress
    import java.net.ServerSocket
    import java.net.Socket
    import java.nio.charset.Charset
    import java.util.*
    import kotlin.concurrent.thread

    // it opens up a socket that connects to the server and listens for incoming and potentially sends outgoing messages
    class ClientObject(ipAddress : String, mainActivity: MainActivity) {
        init {
            val client = Socket(InetAddress.getByAddress(ipAddress.toByteArray()),9999)
            println("Client is running on port ${client.port}")

            while (!client.isConnected) {
                println("Client connected: ${client.inetAddress.hostAddress}")
            }
            ServerHandler(client, mainActivity)
        }
    }
    class ServerHandler(private val client: Socket, mainActivity: MainActivity) {
        private val reader: Scanner = Scanner(client.getInputStream())
        private val writer: OutputStream = client.getOutputStream()
        private var running: Boolean = false
        init {
            run(mainActivity)
        }

        fun run(mainActivity: MainActivity) {
            running = true
            write(ClientEventManager.startGameString)

            while (running) {
                try {
                    val text = reader.nextLine()
                    val result = ClientEventManager.parse(text, mainActivity)
                    if (result != null) {
                        write(result)
                    }
                } catch (ex: Exception) {
                    write(ClientEventManager.endGameString)
                    shutdown()
                } finally {

                }

            }
        }

        private fun write(message: String) {
            writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
            writer.flush()
        }

        private fun shutdown() {
            running = false
            client.close()
            println("${client.inetAddress.hostAddress} closed the connection")
        }
    }