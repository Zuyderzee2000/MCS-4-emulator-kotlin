package intel4004.instruction.accumulator

import intel4004.Register
import util.toBoolean
import util.toInt
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Rotate accumulator left through carry.

class RAL(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "RAL"
    override val opcode: String = "11110101"
    override val cycles: Int = 2

    // Get most significant bit (msb), shift left, set carry as lsb, then set new carry to previous msb,
    // finally, set a potential msb bit that will exceed the 4 bits to zero.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        val msb = (accumulator.value and 0b1000) shr 3
        var shifted = accumulator.value shl 1
        shifted = shifted or carry.toInt()
        carry = msb.toBoolean()
        accumulator.value = (shifted and 0b1111)
        repeat(cycles) {
            clock.collect()
        }
    }
}