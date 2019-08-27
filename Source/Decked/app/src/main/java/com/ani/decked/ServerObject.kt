package com.ani.decked

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.EventLog
import android.widget.Toast
import com.ani.decked.GameState.gameCode
import com.ani.decked.GameState.ipAddress
import com.ani.decked.GameState.serverEventManager
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.Server
import io.grpc.internal.ServerImpl
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class ServerObject {
    private val server = ServerSocket(9999)
    private val mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    init {
        println("Server is running on port ${server.localPort}")
        setDoc()
        while (true) {
            val client = server.accept()
            println("Client connected: ${client.inetAddress.hostAddress}")

            // Run client in it's own thread.
            ClientHandler(client).execute()

        }
    }
    private fun setDoc() {
        mFirestore.collection("games").document(gameCode)
            .set(hashMapOf(Pair("ipAddress", server.inetAddress)))
            .addOnSuccessListener {
                //Toast.makeText(this, "GameCode: $gameCode", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                //Toast.makeText(this, "Failed to create game", Toast.LENGTH_LONG).show()
            }
    }
}
        class ClientHandler(private val client: Socket) : AsyncTask<String,String,Unit>(){
            private val reader: Scanner = Scanner(client.getInputStream())
            private val writer: OutputStream = client.getOutputStream()
            private var running: Boolean = false

            override fun doInBackground(vararg type: String?) {
                running = true
                // Welcome message
                write( ServerEventManager.startGameString )
                try {
                    val text = reader.nextLine()
                    publishProgress(text)
                } catch (ex: Exception) {
                    write(ServerEventManager.endGameString)
                    shutdown()
                } finally {

                }
            }

            override fun onProgressUpdate(vararg values: String?) {
                ServerEventManager.parse(values.fold("") { acc, s -> acc + s })
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