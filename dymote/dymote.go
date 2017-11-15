package dymote

import (
	"fmt"
	"io"
	"net"
	"bytes"
)

var connection net.Conn

func Connect(ip string, port int) {
	conn, err := net.Dial("tcp", fmt.Sprintf("%s:%d", ip, port))
	if err != nil {
		// handle error
		fmt.Print("Unable to connect to server.", err)
		return
	}
	//defer conn.Close()

	fmt.Println("Connection established")
	connection = conn

	//scanner := bufio.NewScanner(conn)
	//scanner.Split(bufio.ScanBytes)

	//go func() {
	//	for scanner.Scan() {
	//		bytes := scanner.Bytes()
	//
	//		fmt.Println(bytes) // Println will add back the final '\n'
	//
	//		handle(bytes)
	//	}
	//}()

	go func() {
		init := make([]byte, 3) // using small tmo buffer for demonstrating

		for {
			_, err := conn.Read(init)
			if err != nil {
				if err != io.EOF {
					fmt.Println("read error:", err)
				}
				break
			}

			packetLen := init[2]
			packet := make([]byte, packetLen + 1) // using small tmo buffer for demonstrating
			_, err = conn.Read(packet)
			if err != nil {
				if err != io.EOF {
					fmt.Println("read error:", err)
				}
				break
			}

			//fmt.Println(len(packet), packet)

			handle(packet)
		}
	}()
}

var jc JavaCallback
var volume = 0
var source = 0
var powered = false
var muted = false
var initialized = false

func setVolume(value int) {
	if volume != value {
		volume = value
		if jc != nil {
			jc.VolumeChanged(volume)
		}

		fmt.Println("Volume changed", volume)
	}
}

func setMuted(value bool) {
	if muted != value {
		muted = value
		fmt.Println("Mute changed", muted)

		if jc != nil {
			jc.MuteChanged(muted)
		}
	}
}

func setPowered(value bool) {
	if powered != value {
		powered = value
		fmt.Println("Power changed", powered)

		if jc != nil {
			jc.PowerChanged(powered)
		}
	}
}

func setSource(value int) {
	if source != value {
		source = value
		fmt.Println("Source changed", source)

		if jc != nil {
			jc.SourceChanged(source)
		}
	}
}
func setInitialized() {
	if initialized == false {
		initialized = true
		fmt.Println("Initialized")

		if jc != nil {
			jc.Initialized()
		}
	}
}

func send(command Command) {
	myList := []byte{0xFF, 0x55, 0x05, 0x2F, 0xA0, 0, 0, 0x31, 0}

	switch command {
	case POWER_ON:
		setPowered(false)
		myList[5] = 0x01

	case POWER_OFF:
		setPowered(true)
		myList[5] = 0x02

	case MUTE:
		setMuted(muted == false)
		myList[5] = 0x12

	case VOLUME_UP:
		setVolume(volume+1)

		myList[5] = 0x13
		myList[6] = byte(volume)

	case VOLUME_DOWN:
		setVolume(volume-1)

		myList[5] = 0x14
		myList[6] = byte(volume)

	case SOURCE_LINE:
		setSource(2)

		myList[5] = 0x15
		myList[6] = byte(source)

	case SOURCE_OPTICAL:
		setSource(3)

		myList[5] = 0x15
		myList[6] = byte(source)

	case SOURCE_COAX:
		setSource(4)

		myList[5] = 0x15
		myList[6] = byte(source)

	case SOURCE_USB:
		setSource(5)

		myList[5] = 0x15
		myList[6] = byte(source)
	}

	sum := 0
	for i := 3; i < 8; i++ {
		sum += int(myList[i])
	}

	q := int(float32(sum)/255.00 + 0.5) // Rounded up sum / 255
	checksum := q*255 - sum - (5 - q)

	if checksum < 0 {
		checksum = checksum & ((1 << 8) - 1)
	}

	myList[8] = byte(checksum)

	fmt.Println("Write", bytesToHex(myList))

	connection.Write(myList)
}

func handle(data []byte) {
	// I/Read: FF 55 0A     31 52 5D 01 06      03      01       00    00 01 0A FF 55 0A 31 52 39 01 05 03 01 00 00 03 2D FF 55 0A 31 52 5D 01 05 03 01 00 00 01 0B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
	//               Len               Volume  Input   Zone?    Mute
	// TODO: Rename btrar to something more meaningful
	//bytesRead := len(data)
	// Do something with the data...

	periodicBytes := []byte {0x31, 0x52}
	updatesBytes := []byte {0x2E, 0xA0}

	if bytes.HasPrefix(data, updatesBytes) {
		// Direct updates from speakers/remote
		handleUpdateBytes(data[2:])
	} else if bytes.HasPrefix(data, periodicBytes) {
		handlePeriodicUpdate(data[2:])
	}
}

func handleUpdateBytes(data []byte) {
	fmt.Println("Direct Update", data)

	// 01 00 21 00 00 D9 2F 00 00 = ON
	// 02 00 21 00 00 D9 2E 01 0C = OFF
	// 01 00 21 00 00 D9 2F 01 0A = ON
	// 04 05 21 00 00 D9 27 01 0C = VOL UP
	// 04 06 21 00 00 D9 26 01 0C = VOL UP
	// 05 03 21 00 00 D9 28 01 0C = VOL DOWN
	// 03 01 21 00 00 D9 2C 03 2E = MUTE
	// 03 00 21 00 00 D9 2D 03 2D = DE MUTE
	// 0A 00 31 00 00 D9 16 01 0D = A
	// 0B 00 11 00 00 D9 35 01 0D = B

	command := data[0]
	value := data[1]

	switch command {
	case 0x01:
		setPowered(true)
	case 0x02:
		setPowered(false)
	case 0x03:
		setMuted(value == 0x01)
	case 0x05, 0x04:
		setVolume(int(value))
	case 0x06:
		// Channel 1
		setSource(2)
	case 0x07:
		// Channel 2
		setSource(3)
	case 0x08:
		// Channel 3
		setSource(4)
	case 0x09:
		// Channel 4
		setSource(5)

	case 0x0A:
		// Zone A
		break
	case 0x0B:
		// Zone B
		break
	case 0x0C:
		// Zone C
		break
	}
}

func handlePeriodicUpdate(data []byte) {
	fmt.Println("Periodic Update", data)

	// Volume
	newVolume := int(data[2])
	setVolume(newVolume)

	newSource := int(data[3])
	setSource(newSource)

	// Power
	newPowerOn := data[4] == 0x01
	setPowered(newPowerOn)

	// Mute
	newMuted := data[5] == 0x01
	setMuted(newMuted)

	setInitialized()
}
