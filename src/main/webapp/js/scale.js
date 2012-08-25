function scale(x, i1, i2, i3, i4) {
	// scale x from interval i1..i2 to interval i3..i4
	
	if (x instanceof Date) {
		return scaleDate(x, i1, i2, i3, i4);
	}
	var p = (x - i1) / (i2 - i1);
	return i3 + p * (i4 - i3);
}

function scaleDate(d, ld, rd, i3, i4) {
	return scale(d.valueOf(), ld.valueOf(), rd.valueOf(), i3, i4);
}

