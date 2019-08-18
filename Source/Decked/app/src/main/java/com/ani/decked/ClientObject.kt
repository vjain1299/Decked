package com.ani.decked

import com.google.common.net.HostAndPort
import java.io.InputStream
import java.io.OutputStream
import java.net.*
import javax.net.SocketFactory

class ClientObject(ipAddress: Inet4Address) {
    val PORT = 80 //TODO: Make sure this is a good port
    var socket : Socket
    init {
        socket = SocketFactory.getDefault().createSocket(ipAddress, PORT)
    }
}