package com.karumien.cloud.sso.spi;

import java.io.IOException;

import com.karumien.cloud.sso.api.messages.DownloadMessages;

public class DownloadMessagesTest {

	public static void main(String[] args) {

		String[] args1 = new String[] {
				"key", "331203", "/tmp/csc/331203-sso", "de-at:at,bg,cs,de,da:dk,et:ee,en,es,fr,hu,it,lt,lv,nl,pl,pt,ro,ru,sl:si,sk,sr,sv,tr"
		};
		
		try {
			DownloadMessages.main(args1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
