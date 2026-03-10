package org.dam.tfg

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform