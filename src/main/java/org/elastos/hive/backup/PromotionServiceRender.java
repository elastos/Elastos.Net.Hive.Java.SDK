package org.elastos.hive.backup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.backup.promotion.PromotionController;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.PromotionService;
import org.elastos.hive.vault.ExceptionConvertor;

class PromotionServiceRender implements PromotionService, ExceptionConvertor {
	private PromotionController controller;

	PromotionServiceRender(ServiceEndpoint serviceEndpoint) {
		controller = new PromotionController(serviceEndpoint.getConnectionManager());
	}

	@Override
	public CompletableFuture<Void> promote() {
		return CompletableFuture.runAsync(() -> {
			try {
				controller.promote();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
