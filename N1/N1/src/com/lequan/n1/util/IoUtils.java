package com.lequan.n1.util;

import java.io.Closeable;
import java.io.IOException;

public class IoUtils {

	public static void close(Closeable io) {
		try {
			if (io != null) {
				io.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
