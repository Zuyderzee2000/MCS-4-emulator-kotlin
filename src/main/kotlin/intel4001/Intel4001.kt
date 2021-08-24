package intel4001

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import lines.DataBus

/***
 * The 4001 is a 2048-bit metal mask programmable ROM providing custom microprogramming capability for the MCS-4
 * micro computer set. It is organized as 256 x 8 bit words.
 */

@ExperimentalUnsignedTypes
class Intel4001(number: Int, dataBus: DataBus, syncFlow: SharedFlow<Unit>,
                clockFlow: SharedFlow<Unit>) {
    private val data: UByteArray = UByteArray(256)
    private val number: Int
    private val dataBus: DataBus
    private val syncFlow: SharedFlow<Unit>
    private val clockFlow: SharedFlow<Unit>

    init {
        this.number = number
        this.dataBus = dataBus
        this.syncFlow = syncFlow
        this.clockFlow = clockFlow
        this.data[0] = 0b1111_0010u
    }

    suspend fun step() {
        syncFlow.collect()

        // Address is sent on data bus in three cycles, starting with the lowest bytes
        clockFlow.collect()
        // Loop to ensure that values written in the same cycle are read, no need to check for next clock as
        // trycollect() already handles that.
        val addressLow = dataBus.read()

        clockFlow.collect()
        val addressHigh = dataBus.read()

        clockFlow.collect()
        // The final 4 bits determine the number of the ROM chip selected
        val chipNumber = dataBus.read()

        if (chipNumber == number) {
            val address = ((addressHigh shl 4) and addressLow)
            // Opcode will be sent split into two 4 bit values, OPR and OPA
            val opcode = data[address]
            val opr = opcode.toInt() shr 4
            val opa = opcode.toInt() and 0b00001111

            clockFlow.collect()
            // Send OPR and OPA in two cycles
            dataBus.write(opr)

            clockFlow.collect()
            dataBus.write(opa)

            //TODO meer shit voor speciale instructies
        }
    }
}