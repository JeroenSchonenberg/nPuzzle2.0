package nl.han.ica.mad.s478416.npuzzle.utils;

/**
 * Created by jeroen on 26/04/15.
 */
public class ByteUtils {
	public static int byteArrayToInt(byte[] bytes){
		int value = 0;

		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;
		}

		return value;
	}

	public static byte[] intToByteArray(int i){
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (i & 0xFF);
		bytes[2] = (byte) ((i >> 8) & 0xFF);
		bytes[1] = (byte) ((i >> 16) & 0xFF);
		bytes[0] = (byte) ((i >> 24) & 0xFF);
		return bytes;
	}
}
