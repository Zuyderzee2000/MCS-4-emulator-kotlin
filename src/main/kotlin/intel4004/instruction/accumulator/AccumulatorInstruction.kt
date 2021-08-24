package intel4004.instruction.accumulator

import intel4004.instruction.Instruction

interface AccumulatorInstruction: Instruction {
    override fun decode() = Triple(null, null, null)
}