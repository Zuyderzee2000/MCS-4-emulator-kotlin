package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Complement accumulator.

class CMA(val accumulator: Register): AccumulatorInstruction {
    override val name: String = "CMA"
    override val opcode: String = "11110100"
    override val cycles: Int = 2

    // Invert the accumulator bits.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        accumulator.value = accumulator.value.inv()
        repeat(cycles) {
            clock.collect()
        }
    }
}