package dymote

type Command int

const (
	POWER_ON Command = iota
	POWER_OFF
	MUTE
	VOLUME_UP
	VOLUME_DOWN
	SOURCE_LINE
	SOURCE_OPTICAL
	SOURCE_COAX
	SOURCE_USB
)
