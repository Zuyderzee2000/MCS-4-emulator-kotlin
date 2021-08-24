package intel4004.instruction.accumulator

import intel4004.Register
import util.toBoolean
import util.toInt
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Rotate accumulator left through carry.


class RAR(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "RAR"
    override val opcode: String = "11110110"
    override val cycles: Int = 2

    // Get least significant bit (lsb), shift right, set carry as msb, then set new carry to previous lsb.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        val lsb = accumulator.value and 0b0001
        val shifted = accumulator.value shr 1
        accumulator.value = ((carry.toInt() shl 3) or shifted)
        carry = lsb.toBoolean()
        repeat(cycles) {
            clock.collect()
        }
    }
}