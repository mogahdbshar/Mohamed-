package com.dstwrtv.app.core.error

import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class AppError(open val message: String) {
    data class NetworkError(override val message: String = "لا يوجد اتصال بالإنترنت حالياً.") : AppError(message)
    data class TimeoutError(override val message: String = "عذراً، انتهت مهلة الاتصال بالسيرفر. تحقق من سرعة الإنترنت.") : AppError(message)
    data class ParsingError(override val message: String = "حدث خطأ أثناء معالجة قائمة التشغيل.") : AppError(message)
    data class DatabaseError(override val message: String = "حدث خطأ في قاعدة البيانات المحلية.") : AppError(message)
    data class UnknownError(override val message: String = "حدث خطأ غير معروف.") : AppError(message)
}

inline fun <T> safeCall(action: () -> T): Result<T> {
    return try {
        Result.success(action())
    } catch (e: SocketTimeoutException) {
        Result.failure(Exception("عذراً، انتهت مهلة الاتصال بالسيرفر. تحقق من سرعة الإنترنت."))
    } catch (e: UnknownHostException) {
        Result.failure(Exception("لا يوجد اتصال بالإنترنت حالياً."))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
