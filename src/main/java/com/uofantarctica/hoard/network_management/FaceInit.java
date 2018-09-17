package com.uofantarctica.hoard.network_management;

import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SafeBag;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static com.uofantarctica.jndn.helpers.FaceSecurity.initFaceAndGetSecurityData;

public class FaceInit {
	private static final Logger log = LoggerFactory.getLogger(FaceInit.class);

	public static void pumpFaceAwhile(Face face, long awhile) throws IOException {
		long startTime0 = System.currentTimeMillis();
		long timeNow0 = System.currentTimeMillis();
		while((timeNow0 - startTime0) <= awhile) {
			timeNow0 = System.currentTimeMillis();
			try {
				face.processEvents();
				Thread.sleep(10);
			}
			catch (IOException e) {
				log.error("failed in pumpFaceAwhile", e);
				throw e;
			}
			catch (Exception e) {
				log.error("failed in pumpFaceAwhile", e);
			}
		}

	}

	public static Face getRawFace() throws IOException {
		Face face = TransportConfiguration.getFace();
		initFaceAndGetSecurityData(face);
		pumpFaceAwhile(face, 2000);
		return face;
	}

	public static LocalFace getFace(Enqueue<NdnEvent> ndnEvents) throws IOException {
		Face face = getRawFace();
		//TODO sign packets? no? provenance?
		return new LocalFace(face, ndnEvents);
	}
}
