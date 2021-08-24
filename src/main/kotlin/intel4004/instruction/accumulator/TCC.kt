package intel4004.instruction.accumulator

import intel4004.Register
import util.toInt
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

// Transmit carry and clear.

class TCC(var carry: Boolean, val accumulator: Register): AccumulatorInstruction {
    override val name: String = "TCC";
    override val opcode: String = "11110111";
    override val cycles: Int = 2;

    // Set the value of the accumulator to the value of the carry bit and reset the carry bit after.
    override suspend fun execute(
        registers: Array<Register>?,
        addresses: Array<Byte>?,
        immediate: Array<Byte>?,
        clock: SharedFlow<Unit>
    ) {
        accumulator.value = carry.toInt()
        carry = false
        repeat(cycles) {
            clock.collect()
        }
    }
}