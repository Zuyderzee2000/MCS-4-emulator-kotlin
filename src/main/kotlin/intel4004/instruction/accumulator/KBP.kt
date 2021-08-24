package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Keyboard process.

class KBP(val accumulator: Register): AccumulatorInstruction {
    override val name: String = "DAA";
    override val opcode: String = "11111100";
    override val cycles: Int = 2;

    // If 1 bit in the accumulator is set, sets the accumulator to a value representing what bit was set, e.g.
    // 0100 (third bit) turns into 0011 (3).
    // If no bits are set accumulator remains 0 and if multiple bits are set it will be set to 1111 (15).
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        accumulator.value = when (accumulator.value) {
            0b0000 -> 0b0000
            0b0001 -> 0b0001
            0b0010 -> 0b0010
            0b0100 -> 0b0011
            0b1000 -> 0b0100
            else -> 0b1111
        }
        repeat(cycles) {
            clock.collect()
        }
    }
}