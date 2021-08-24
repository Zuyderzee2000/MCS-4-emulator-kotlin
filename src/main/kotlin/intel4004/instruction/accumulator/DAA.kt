package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Decimal adjust accumulator.

class DAA(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "DAA"
    override val opcode: String = "11111011"
    override val cycles: Int = 2

    // If accumulator is greater than 9 or carry bit is set add 6 to accumulator, otherwise leave as is.
    // Set the carry bit when the sum goes above 4 bits, otherwise carry bit is not reset.
    // Used for adding decimal numbers.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        if (carry) {
            // 10 + 6 > 15 so if the value is bigger than 9 it always needs carry bit.
            carry = accumulator.value > 9;
            accumulator.value = accumulator.value + 6
        }
        repeat(cycles) {
            clock.collect()
        }
    }
}