package dymote

type JavaCallback interface {
	VolumeChanged(int)
	SourceChanged(int)
	PowerChanged(bool)
	MuteChanged(bool)
	Initialized()
}
