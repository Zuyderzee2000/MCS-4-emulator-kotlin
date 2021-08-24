package lines

import exception.BitOverflowException
import kotlinx.coroutines.sync.Mutex

//A 4 bit data bus
class DataBus {
    private var data: Int = 0b0000;

    private val mutex = Mutex()
    suspend fun write(data: Int) {
        // Lock so that writing does not create race conditions
        mutex.lock()
        if (data in 0b0000..0b1111) {
            this.data = data;
        } else {
            throw BitOverflowException("Writing more than 4 bits.");
        }
        mutex.unlock()
    }

    fun read(): Int {
        return data;
    }

}