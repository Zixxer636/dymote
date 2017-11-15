package dymote

var hexArray = []rune("0123456789ABCDEF")

func bytesToHex(bytes []byte) string {
	hexChars := make([]rune, len(bytes)*3)
	for k, byt := range bytes {
		v := int(byt & 0xFF)
		hexChars[k*3] = hexArray[v>>4]
		hexChars[k*3+1] = hexArray[v&0x0F]
		hexChars[k*3+2] = ' '
	}

	return string(hexChars)
}
