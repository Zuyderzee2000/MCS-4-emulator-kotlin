package intel4004.instruction

import intel4004.Register
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.SharedFlow

// TODO maak objects van instructies?

interface Instruction {
    val name: String
    val opcode: String
    val cycles: Int

    val opa: String
        get() = opcode.substring(0, 3)

    val opr: String
        get() = opcode.substring(4)

    suspend fun execute(registers: Array<Register>?, addresses: Array<Byte>?, immediate: Array<Byte>?, clock: SharedFlow<Unit>)

    fun decode(): Triple<Array<Register>?, Array<Byte>?, Array<Byte>?>

}