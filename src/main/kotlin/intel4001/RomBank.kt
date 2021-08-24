package intel4001

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import lines.DataBus

@ExperimentalUnsignedTypes
class RomBank(
    amount: Int, dataBus: DataBus, syncFlow: SharedFlow<Unit>,
    clockFlow: SharedFlow<Unit>
) {
    //TODO uitvogelen hoe CM ROM werkt en toevoegen
    private val romArray: Array<Intel4001?> = arrayOfNulls(16)
    init {
        repeat(amount) {
            romArray[it] = Intel4001(it, dataBus, syncFlow, clockFlow)
        }
    }

    suspend fun step() = coroutineScope {
        for (rom: Intel4001? in romArray) {
            if (rom != null) {
                launch {
                    rom.step()
                }
            } else {
                break
            }
        }
    }
}