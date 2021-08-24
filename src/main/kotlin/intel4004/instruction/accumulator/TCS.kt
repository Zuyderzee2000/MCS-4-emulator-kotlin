package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Transfer carry subtract.

class TCS(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "TCS"
    override val opcode: String = "11111001"
    override val cycles: Int = 2

    // If carry bit is 0, set accumulator to 9, if carry is 1 set accumulator to 10. Used for subtracting decimals
    // greater than 4 bits.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        if (carry) accumulator.value = 10 else accumulator.value = 9
        carry = false
        repeat(cycles) {
            clock.collect()
        }
    }
}