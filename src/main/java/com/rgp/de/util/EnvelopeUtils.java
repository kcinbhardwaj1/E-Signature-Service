package com.rgp.de.util;

import java.util.Random;

public class EnvelopeUtils {
	
	public static Integer getClientUserId() {
		Random rand = new Random();
		// Generate random integers in range 0 to 999999
		int clientUserId = rand.nextInt(100000);
		return Integer.valueOf(clientUserId);
	}

	public static Integer getRecipientId() {
		Random rand = new Random();
		// Generate random integers in range 0 to 99999
		int recipientId = rand.nextInt(10000);
		return Integer.valueOf(recipientId);
	}
}
