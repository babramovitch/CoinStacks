package com.nebulights.coinstacks.Network.security

import com.nebulights.coinstacks.Extensions.toHex
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * Created by babramovitch on 2018-02-26.
 */

open class HashGenerator {

    companion object {

        fun generateHmac(message: ByteArray, key: ByteArray, hashingAlgorithm: HashingAlgorithms): ByteArray {
            try {
                return hmac(hashingAlgorithm.algorthim, key, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ByteArray(0)
        }

        fun generateHmacDigest(message: ByteArray, key: ByteArray, hashingAlgorithm: HashingAlgorithms): String {
            try {
                val bytes = generateHmac(message, key, hashingAlgorithm)
                return bytes.toHex()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
        private fun hmac(algorithm: String, key: ByteArray, message: ByteArray): ByteArray {
            val mac = Mac.getInstance(algorithm)
            mac.init(SecretKeySpec(key, algorithm))
            return mac.doFinal(message)
        }

    }
}

