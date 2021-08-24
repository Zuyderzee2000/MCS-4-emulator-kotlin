import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class Clock(frequency: Double, flow: MutableSharedFlow<Unit>) {
    private val frequency: Double
    private var cycles: ULong = 0u
    private val flow: MutableSharedFlow<Unit>

    init {
        this.frequency = frequency;
        this.flow = flow;
    }

    @ExperimentalTime
    suspend fun run() {
        while(true) {
            flow.emit(Unit)
            cycles++
            delay((1 / frequency).toDuration(TimeUnit.SECONDS))
        }
    }

}