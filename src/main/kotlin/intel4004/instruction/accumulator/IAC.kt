package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Increment accumulator.

class IAC(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "IAC"
    override val opcode: String = "11110010"
    override val cycles: Int = 2

    // Add 1 to accumulator, set the carry if it goes over the 4 bits.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        if (accumulator.value == 0b1111) {
            accumulator.value = 0b0000
            carry = true
        } else {
            accumulator.value++
            carry = false
        }
        repeat(cycles) {
            clock.collect()
        }
    }

}