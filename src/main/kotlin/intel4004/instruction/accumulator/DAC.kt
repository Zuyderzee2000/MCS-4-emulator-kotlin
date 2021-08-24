package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Decrement accumulator.

class DAC(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "DAC"
    override val opcode: String = "11111000"
    override val cycles: Int = 2

    // Subtract 1 from accumulator, reset carry bit if there is borrow at the msb, otherwise set it.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        if (accumulator.value == 0b0000) {
            accumulator.value = 0b1111
            carry = false
        } else {
            accumulator.value--
            carry = true
        }
        repeat(cycles) {
            clock.collect()
        }
    }
}