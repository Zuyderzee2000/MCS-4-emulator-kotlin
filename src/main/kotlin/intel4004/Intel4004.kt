package intel4004

import exception.InvalidOpcodeException
import intel4004.instruction.Instruction
import intel4004.instruction.accumulator.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lines.DataBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

//TODO bepaal of channel of class beter is voor data bus

/***
 * probleem:
 * CPU leest oude data bus waardes voordat ROM naar data bus heeft verstuurd
 * oplossing:
 * CPU moet pas lezen nadat de ROM data is gelezen
 * maar hoe?
 */

class Intel4004(dataBus: DataBus, syncFlow: MutableSharedFlow<Unit>, clockFlow: SharedFlow<Unit>) {
    private val dataBus: DataBus
    private val clockFlow: SharedFlow<Unit>
    private val syncFlow: MutableSharedFlow<Unit>

    init {
        this.dataBus = dataBus
        this.clockFlow = clockFlow
        this.syncFlow = syncFlow
    }

    // Index registers
    private val r0 = Register("r0", 4)
    private val r1 = Register("r1", 4)
    private val r2 = Register("r2", 4)
    private val r3 = Register("r3", 4)
    private val r4 = Register("r4", 4)
    private val r5 = Register("r5", 4)
    private val r6 = Register("r6", 4)
    private val r7 = Register("r7", 4)
    private val r8 = Register("r8", 4)
    private val r9 = Register("r9", 4)
    private val ra = Register("ra", 4)
    private val rb = Register("rb", 4)
    private val rc = Register("rc", 4)
    private val rd = Register("rd", 4)
    private val re = Register("re", 4)
    private val rf = Register("rf", 4)

    // Accumulator
    private val acc = Register("acc", 4)

    // Program counter
    private val pc = Register("pc", 12)

    // Stack registers
    private val s0 = Register("s0", 12)
    private val s1 = Register("s1", 12)
    private val s2 = Register("s2", 12)

    // Stack pointer - not really a register, as it cannot be manipulated manually, only when using subroutine
    // instructions. As there are only 3 entries in the stack, it is simply a number that goes from 0 to 2
    // and then loops back around to 0.
    private val sp = 0;

    // Flags
    private val carry = false

    // Accumulator instructions
    private val clb = CLB(carry, acc)
    private val clc = CLC(carry)
    private val cma = CMA(acc)
    private val cmc = CMC(carry)
    private val daa = DAA(carry, acc)
    private val dac = DAC(carry, acc)
    private val iac = IAC(carry, acc)
    private val kbp = KBP(acc)
    private val ral = RAL(carry, acc)
    private val rar = RAR(carry, acc)
    private val stc = STC(carry)
    private val tcc = TCC(carry, acc)
    private val tcs = TCS(carry, acc)

    private val instructions: List<Instruction> = listOf(clb, clc, cma, cmc, daa, dac, iac, kbp, ral, rar, stc, tcc, tcs)

    // Replace variable letters in an opcode with wildcards in a regex so that the instruction can be retrieved.
    // Sometimes there are set bits after the variable letters in an opcode, so simply checking for the first x bits
    // does not work.
    private val nonBitRegex = Regex("[^01]")
    private val opcodeMap: Map<Regex, Instruction> = instructions.associateBy {
        Regex(it.opcode.replace(nonBitRegex, "[01]"))
    }

    private fun determineInstruction(opcode: Int): Instruction {
        for (entry in opcodeMap) {
            if (entry.key.matches(opcode.toString()))
                return entry.value
        }
        val message = String.format("Opcode %x does not correspond to a valid instruction.", opcode)
        throw InvalidOpcodeException(message)
    }

    // An instruction cycle normally consists 8 clock cycles, which will consists of one iteration of the step function.
    // Some instructions take up 16 clock cycles, but those consist of simply two of these steps.
    @ExperimentalCoroutinesApi
    suspend fun step() {
        // Wait for clock
        clockFlow.collect()
        syncFlow.emit(Unit)

        clockFlow.collect()
        // Set Data Bus to PC address in three cycles, starting from low 4 bits.
        dataBus.write(pc.value and 0b0000_0000_1111)

        clockFlow.collect()
        dataBus.write((pc.value and 0b0000_1111_0000) shr 4)

        clockFlow.collect()
        dataBus.write((pc.value and 0b1111_0000_0000) shr 8)

        clockFlow.collect()
        // Loop to ensure that values written in the same cycle are read no need to check for next clock as
        // trycollect() already handles that.
        val opr = dataBus.read()

        clockFlow.collect()
        val opa = dataBus.read()

        val opcode = (opr shl 4) and opa
        val instruction = determineInstruction(opcode)
        val (registers, addresses, immediate) = instruction.decode()
        instruction.execute(registers, addresses, immediate, clockFlow)
        pc.value++
    }

}