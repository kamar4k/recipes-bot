package io.kamae.family.bot

import java.io.InputStream
import java.nio.charset.StandardCharsets

class TestUtils {
    companion object {
        fun getTestResourcesAsString(clazz: Class<*>, fileName: String): String {
            return String(getTestResourceAsStream(clazz, fileName).readAllBytes(), StandardCharsets.UTF_8)

        }

        private fun getTestResourceAsStream(clazz: Class<*>, fileName: String): InputStream {
            return clazz.getResourceAsStream(clazz.simpleName + "/" + fileName)
                ?: error("Не удалось получить ресурс с именем $fileName для класса $clazz")
        }
    }
}