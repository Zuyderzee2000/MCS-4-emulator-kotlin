package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Set carry.

class STC(var carry: Boolean): AccumulatorInstruction {
    override val name: String = "STC"
    override val opcode: String = "11111010"
    override val cycles: Int = 2

    // Set carry bit to 1.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        carry = true;
        repeat(cycles) {
            clock.collect()
        }
    }
}