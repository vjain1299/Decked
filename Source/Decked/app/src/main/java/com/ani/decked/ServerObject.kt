package com.ani.decked

import android.app.IntentService
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.system.Os.inet_pton
import android.system.Os.shutdown
import android.util.EventLog
import android.util.Log
import android.widget.Toast
import com.ani.decked.GameState.gameCode
import com.ani.decked.GameState.hasCircle
import com.ani.decked.GameState.ipAddress
import com.ani.decked.GameState.nPiles
import com.ani.decked.GameState.nPlayers
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
import kotlin.NoSuchElementException
import kotlin.concurrent.thread

//Same as client except for setDoc() and creates a server socket instead of a normal socket

class ServerObject(mainActivity: MainActivity) {
    private val server = ServerSocket(9999)
    private val mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    init {
        println("Server is running on port ${server.localPort}")
        setDoc()
        while (true) {
            val client = server.accept()
            println("Client connected: ${client.inetAddress.hostAddress}")

            // Run client in it's own thread.
            thread { ClientHandler(client, mainActivity) }
        }
    }

    //Creates the Firestore Document (Collection --> Document and each doc can have collections that have documents, etc.) Can be nested up to 100 times
    private fun setDoc() {
        Log.w("ipAddress", server.inetAddress.hostAddress)
        mFirestore.collection("games").document(gameCode)
            .set(hashMapOf(Pair("nPiles", nPiles), Pair("nPlayers", nPlayers),Pair("hasCircle", hasCircle)))
            .addOnSuccessListener {
                //Toast.makeText(this, "GameCode: $gameCode", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                //Toast.makeText(this, "Failed to create game", Toast.LENGTH_LONG).show()
            }
    }
}
        class ClientHandler(private val client: Socket, mainActivity : MainActivity) {
            private val reader: Scanner = Scanner(client.getInputStream())
            private val writer: OutputStream = client.getOutputStream()
            private var running: Boolean = false
            init {
                run(mainActivity)
            }

            fun run(mainActivity: MainActivity) {
                running = true
                // Welcome message
                write( ServerEventManager.startGameString )
                while(running) {
                    try {
                        val text = reader.nextLine()
                        if (text != null && text.isNotEmpty()) {
                            write(ServerEventManager.parse(text, mainActivity))
                        }
                    }catch(ex : NoSuchElementException) {
                        //Waiting for response
                    }catch (ex: IllegalStateException) {
                        write(ServerEventManager.endGameString)
                        shutdown()
                        break
                    } finally {

                    }
                }
            }

            private fun write(message: String?) {
                if(message == null) return
                writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
                writer.flush()
            }

            private fun shutdown() {
                running = false
                client.close()
                println("${client.inetAddress.hostAddress} closed the connection")
            }
        }