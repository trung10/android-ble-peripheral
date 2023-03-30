package itan.com.bluetoothle

data class PcmEvent(
    val index: Int,
    val timestamp: Int,
    val type: Int,
    val remain: Int,
    val cycle: Int,
    val InletTempF: Float,
    val OutletTempF: Float,
    val PadTempF: Float,
    val HotResvTempF: Float,
    val ColdResvTempF: Float,
    val HeaterTempF: Float,
    val HotSetpointF: Float,
    val ColdSetpointF: Float
    )
{

    fun toByteArray() = run { var b = byteArrayOf()
        b = b.plus(Utils.intTo4Bytes(this.index))
        b = b.plus(Utils.intTo4Bytes(this.timestamp))
        b = b.plus(this.type.toByte())
        b = b.plus(Utils.intTo4Bytes(this.remain))
        b = b.plus(this.cycle.toByte())
        b = b.plus(Utils.floatToBytes(this.InletTempF))
        b = b.plus(Utils.floatToBytes(this.OutletTempF))
        b = b.plus(Utils.floatToBytes(this.PadTempF))
        b = b.plus(Utils.floatToBytes(this.HotResvTempF))
        b = b.plus(Utils.floatToBytes(this.ColdResvTempF))
        b = b.plus(Utils.floatToBytes(this.HeaterTempF))
        b = b.plus(Utils.floatToBytes(this.HotSetpointF))
        b = b.plus(Utils.floatToBytes(this.ColdSetpointF))
        b
    }

}