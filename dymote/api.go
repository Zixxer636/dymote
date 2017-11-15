package dymote

// RegisterJavaCallback will register a JavaCallback instance for callbacks back to Java.
func RegisterJavaCallback(c JavaCallback) {
	jc = c
}

func PowerOn() {
	send(POWER_ON)
}

func PowerOff() {
	send(POWER_OFF)
}

func Mute() {
	send(MUTE)
}

func VolumeUp() {
	send(VOLUME_UP)
}

func VolumeDown() {
	send(VOLUME_DOWN)
}

func SourceLine() {
	send(SOURCE_LINE)
}

func SourceOptical() {
	send(SOURCE_OPTICAL)
}

func SourceCoax() {
	send(SOURCE_COAX)
}

func SourceUsb() {
	send(SOURCE_USB)
}
