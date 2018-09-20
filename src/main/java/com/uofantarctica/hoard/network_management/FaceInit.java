package com.uofantarctica.hoard.network_management;

import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.jndn.helpers.FaceSecurity;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import net.named_data.jndn.Face;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.uofantarctica.jndn.helpers.FaceSecurity.initFaceAndGetSecurityData;

public class FaceInit {
	private static final Logger log = LoggerFactory.getLogger(FaceInit.class);

	private FaceSecurity.SecurityData securityData;

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

	public static class FaceBundle {
		public final FaceSecurity.SecurityData securityData;
		public final Face face;

		public FaceBundle(FaceSecurity.SecurityData securityData, Face face) {
			this.securityData = securityData;
			this.face = face;
		}
	}

	public static FaceBundle getRawFace() throws IOException {
		Face face = TransportConfiguration.getFace();
		FaceSecurity.SecurityData securityData = initFaceAndGetSecurityData(face);
		pumpFaceAwhile(face, 2000);
		return new FaceBundle(securityData, face);
	}

	public static LocalFace getFace() throws IOException {
		FaceBundle faceBundle = getRawFace();
		//TODO sign packets? no? provenance?
		return new LocalFace(faceBundle);
	}
}
