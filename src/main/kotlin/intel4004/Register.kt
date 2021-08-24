package intel4004

// A 4 bit register
class Register(name: String, bits: Int){
    val name: String
    private val bits: Int
    var value = 0
        set(value) {
            val max_value = (1 shl bits) - 1
            if (value in 0..max_value) {
                field = value;
            } else {
                throw Exception("Setting register value to more than $bits bits.");
            }
        };

    init {
        this.name = name
        this.bits = bits
    }
}