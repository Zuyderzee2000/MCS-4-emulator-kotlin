package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Complement carry.

class CMC(var carry: Boolean): AccumulatorInstruction {
    override val name: String = "CMC"
    override val opcode: String = "11110011"
    override val cycles: Int = 2

    // Invert the carry bit.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        carry = !carry;
        repeat(cycles) {
            clock.collect()
        }
    }
}