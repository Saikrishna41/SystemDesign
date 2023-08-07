package lowleveldesign

import java.lang.Math.min

/**
 * In this implementation, maxTokens represents the maximum number of tokens the bucket can hold, and
 * refillRatePerSecond specifies how many tokens should be added to the bucket every second.
 * The tryConsume(tokensRequested: Int) function attempts to consume the requested number of tokens,
 * returning true if successful or false if the bucket is empty and the request is rejected.

Note that this is a basic example, and in a real-world application, you may want to use a more sophisticated rate
limiter to handle potential edge cases and fine-tune the parameters based on your specific use case
 */
class TokenBucketRateLimiter(private val maxTokens: Int, private val refillRatePerSecond: Double) {
    private var tokens: Int = maxTokens
    private var lastRefillTimeMillis: Long = System.currentTimeMillis()

    @Synchronized
    fun tryConsume(tokensRequested: Int): Boolean {
        refillTokens()
        if (tokens >= tokensRequested) {
            tokens -= tokensRequested
            return true
        }
        return false
    }

    @Synchronized
    private fun refillTokens() {
        val now = System.currentTimeMillis()
        val timeElapsedMillis = now - lastRefillTimeMillis
        val tokensToAdd = (timeElapsedMillis / 1000.0 * refillRatePerSecond).toInt()
        tokens = min(maxTokens, tokens + tokensToAdd)
        lastRefillTimeMillis = now
    }
}

fun main() {
    // Example usage:
    val rateLimiter = TokenBucketRateLimiter(
        maxTokens = 10,
        refillRatePerSecond = 1.0
    ) // 10 tokens and 1 token per second refill rate
    for (i in 1..15) {
        if (rateLimiter.tryConsume(1)) {
            println("Request $i processed successfully.")
        } else {
            println("Request $i rejected due to rate limiting.")
        }
        Thread.sleep(500) // Simulating some delay between requests
    }
}