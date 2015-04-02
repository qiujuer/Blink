package net.qiujuer.blink;

import java.io.File;

import net.qiujuer.blink.Entity;
import net.qiujuer.blink.ReceiveEntity;
import net.qiujuer.blink.Resource;
import net.qiujuer.blink.box.ByteReceiveEntity;
import net.qiujuer.blink.box.FileReceiveEntity;
import net.qiujuer.blink.box.StringReceiveEntity;

/**
 * Blink receive entity parse
 */
public class ReceiveParser {
	private long mId = 0;
	protected Resource mResource;

	public ReceiveParser(Resource resource) {
		mResource = resource;
	}

	public ReceiveEntity<?> parseReceive(int type, int len) {
		final long id = ++mId;
		ReceiveEntity<?> entity = null;
		switch (type) {
		case Entity.Type.STRING:
			entity = new StringReceiveEntity(id, type, len);
			break;
		case Entity.Type.BYTES:
			entity = new ByteReceiveEntity(id, type, len);
			break;
		case Entity.Type.FILE:
			File file = mResource.create(id);
			if (file != null)
				entity = new FileReceiveEntity(id, type, len, file);
			break;
		}
		return entity;
	}
}
