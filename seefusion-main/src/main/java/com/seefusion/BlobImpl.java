/**
 * 
 */
package com.seefusion;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Daryl
 *
 */
class BlobImpl implements Blob {

	private static final Logger LOG = Logger.getLogger(BlobImpl.class.getName());
	
	Blob wrapped;
	int currentRow;
	
	BlobImpl(Blob wrapped, int currentRow) {
		this.wrapped = wrapped;
		this.currentRow = currentRow;
	}
	
	/* (non-Javadoc)
	 * @see java.sql.Blob#getBinaryStream()
	 */
	@Override
	public InputStream getBinaryStream() throws SQLException {
		log("getBinaryStream()");
		return wrapped.getBinaryStream();
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#getBytes(long, int)
	 */
	@Override
	public byte[] getBytes(long pos, int length) throws SQLException {
		log("getBytes(pos="+pos+", length="+length+")");
		return wrapped.getBytes(pos, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#length()
	 */
	@Override
	public long length() throws SQLException {
		long ret = wrapped.length();
		log("length()=="+ret);
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#position(byte[], long)
	 */
	@Override
	public long position(byte[] pattern, long start) throws SQLException {
		log("position([byte[] pattern], start="+start+")");
		return wrapped.position(pattern, start);
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#position(java.sql.Blob, long)
	 */
	@Override
	public long position(Blob pattern, long start) throws SQLException {
		log("position([Blob pattern], start="+start+")");
		return wrapped.position(pattern, start);
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#setBinaryStream(long)
	 */
	@Override
	public OutputStream setBinaryStream(long pos) throws SQLException {
		log("setBinaryStream(pos="+pos+")");
		return wrapped.setBinaryStream(pos);
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#setBytes(long, byte[])
	 */
	@Override
	public int setBytes(long pos, byte[] bytes) throws SQLException {
		log("setBytes(pos="+pos+", byte[])");
		return wrapped.setBytes(pos, bytes);
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#setBytes(long, byte[], int, int)
	 */
	@Override
	public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
		log("setBytes(pos="+pos+", byte[], offset="+offset+", len="+len+")");
		return wrapped.setBytes(pos, bytes, offset, len);
	}

	/* (non-Javadoc)
	 * @see java.sql.Blob#truncate(long)
	 */
	@Override
	public void truncate(long len) throws SQLException {
		log("truncate(len="+len+")");
		wrapped.truncate(len);
	}

	void log(String s) {
		LOG.info("Blob(row " + currentRow + "): " +s);
	}

	@Override
	public void free() throws SQLException {
		wrapped.free();
	}

	@Override
	public InputStream getBinaryStream(long pos, long length)
			throws SQLException {
		return wrapped.getBinaryStream(pos, length);
	}

}
