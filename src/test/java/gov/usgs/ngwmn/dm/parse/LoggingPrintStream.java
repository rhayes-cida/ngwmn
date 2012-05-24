package gov.usgs.ngwmn.dm.parse;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class prints to System.out, but also keeps a record of what has been
 * printed. Useful for testing
 *
 * @author ilinkuo
 *
 */
public class LoggingPrintStream extends PrintStream {
	private StringBuilder printRecord = new StringBuilder();
	private boolean isSuppressOutput;

	// ===========
	// CONSTRUCTOR
	// ===========
	public LoggingPrintStream() {
		super(System.out);
	}

	public LoggingPrintStream(OutputStream out) {
		super(out);
	}

	// =====================
	// CONFIGURATION METHODS
	// =====================
	public LoggingPrintStream disable() {
		this.isSuppressOutput = true;
		return this;
	}

	public LoggingPrintStream enable() {
		this.isSuppressOutput = false;
		return this;
	}


	// ===============
	// SERVICE METHODS
	// ===============
	/**
	 * clears the print record
	 */
	public void clear() {
		this.printRecord = new StringBuilder();
	}

	/**
	 * @return a copy of the print record
	 */
	public String getRecord() {
		return this.printRecord.toString();
	}


	// ==================
	// OVERRIDDEN METHODS
	// ==================

	@Override
	public void print(boolean b) {
		if (!this.isSuppressOutput) {
			super.print(b);
		}
	}

	@Override
	public void print(char c) {
		this.printRecord.append(c);
		if (!this.isSuppressOutput) {
			super.print(c);
		}
	}

	@Override
	public void print(char[] s) {
		this.printRecord.append(s);
		if (!this.isSuppressOutput) {
			super.print(s);
		}
	}

	@Override
	public void print(double d) {
		this.printRecord.append(d);
		if (!this.isSuppressOutput) {
			super.print(d);
		}
	}

	@Override
	public void print(float f) {
		this.printRecord.append(f);
		if (!this.isSuppressOutput) {
			super.print(f);
		}
	}

	@Override
	public void print(int i) {
		this.printRecord.append(i);
		if (!this.isSuppressOutput) {
			super.print(i);
		}
	}

	@Override
	public void print(long l) {
		this.printRecord.append(l);
		if (!this.isSuppressOutput) {
			super.print(l);
		}
	}

	@Override
	public void print(Object obj) {
		this.printRecord.append(obj);
		if (!this.isSuppressOutput) {
			super.print(obj);
		}
	}

	@Override
	public void print(String s) {
		this.printRecord.append(s);
		if (!this.isSuppressOutput) {
			super.print(s);
		}
	}

	@Override
	public void println() {
		this.printRecord.append('\n');
		if (!this.isSuppressOutput) {
			super.println();
		}
	}

	@Override
	public void println(boolean x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(char x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(char[] x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(double x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(float x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(int x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(long x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(Object x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public void println(String x) {
		this.printRecord.append(x).append('\n');
		if (!this.isSuppressOutput) {
			super.println(x);
		}
	}

	@Override
	public PrintStream append(char c) {
		this.	printRecord.append(c);
		if (!this.isSuppressOutput) {
			super.append(c);
		}
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		throw new UnsupportedOperationException("Must override and check this before calling");
//		super.append(csq, start, end);
//		return super.append(csq, start, end);
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++) {
			this.printRecord.append((char)buf[i + off]);
		}
		if (!this.isSuppressOutput) {
			super.write(buf, off, len);
		}
	}

	@Override
	public void write(int b) {
		this.printRecord.append(b);
		if (!this.isSuppressOutput) {
			super.write(b);
		}
	}


}
