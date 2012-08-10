package de.xzise.jimp.util;

public final class FillingArray<E> extends Array<E> {

	private static final long serialVersionUID = -5753449045247960563L;

	private int filled = 0;

	public FillingArray(final E[] array) {
		super(array);
	}

	@Override
	public boolean add(E e) {
		if (this.isFilled()) {
			return false;
		} else {
			this.set(this.filled++, e);
			return true;
		}
	}

	public Array<E> getFilledArray() {
		if (this.filled > 0) {
			return this.subarray(0, this.filled - 1);
		} else {
			return new Array<E>();
		}
	}

	public boolean isFilled() {
		return this.filled >= this.size();
	}

	public int getFilledLength() {
		return this.filled;
	}

	public void clearFill() {
		this.filled = 0;
	}
}