package util

public fun Boolean.toInt(): Int = if (this) 1 else 0;
public fun Boolean.toByte(): Byte = if (this) 1 else 0;

public fun Number.toBoolean() = this != 0;