import intel4001.RomBank
import intel4004.Intel4004
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import lines.DataBus
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime

private const val CLK_FREQUENCY = 10_000.0
private const val STEP_AMOUNT = 25
@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    runBlocking {
        // Clock pulses are sent through a conflated channel.
        // All emulated hardware suspends after a cycle until receiving a clock pulse on the channel.
        // If an emulated instruction cycle takes longer to execute than the actual clock cycle, the fact that the channel
        // is conflated means that a new pulse will just overwrite the old one, so the hardware will not "catch up" performing
        // delayed instructions really fast.
        val clockFlow = MutableSharedFlow<Unit>(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)

        // The sync bit which the cpu uses to synchronize the ROM and RAM
        val syncFlow = MutableSharedFlow<Unit>(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)

        val clock = Clock(CLK_FREQUENCY, clockFlow)
        val dataBus = DataBus()
        val cpu = Intel4004(dataBus, syncFlow, clockFlow.asSharedFlow())
        val romBank = RomBank(1, dataBus, syncFlow.asSharedFlow(), clockFlow.asSharedFlow())

        launch {
            clock.run()
        }

        launch {
            repeat(STEP_AMOUNT) {
                cpu.step()
            }
        }

        launch {
            repeat(STEP_AMOUNT) {
                romBank.step()
            }
        }
    }
}
