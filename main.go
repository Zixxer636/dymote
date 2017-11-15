package main

import (
	"dymote/dymote"
	"time"
)

func main() {
	dymote.Connect("192.168.2.172", 1901)
	time.Sleep(time.Second * 60)

	//dymote.PowerOn()
	//time.Sleep(time.Second * 5)
	//
	//dymote.VolumeDown()
	//time.Sleep(time.Second * 2)
	//
	//dymote.VolumeUp()
	//time.Sleep(time.Second * 2)

	//dymote.PowerOff()
	//time.Sleep(time.Second * 10)
}
