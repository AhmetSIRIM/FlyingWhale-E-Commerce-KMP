package com.flyingwhale.ecommerce

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform