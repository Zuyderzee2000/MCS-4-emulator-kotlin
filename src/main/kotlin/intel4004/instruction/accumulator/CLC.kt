package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Clear carry.

class CLC(var carry: Boolean): AccumulatorInstruction {
    override val name: String = "CLC"
    override val opcode: String = "11110001"
    override val cycles: Int = 2

    // Reset carry to 0.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        carry = false;
        repeat(cycles) {
            clock.collect()
        }
    }
}