package intel4004.instruction.accumulator

import intel4004.Register
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Clear accumulator and carry.

class CLB(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "CLB"
    override val opcode: String = "11110000"
    override val cycles: Int = 2

    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        carry = false;
        accumulator.value = 0x00;
        repeat(cycles) {
            clock.collect()
        }
    }
}