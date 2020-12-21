package org.elastos.hive;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VaultHelperTest {


	private static Database database;

	@Test
	public void createCollect() {
		CompletableFuture<Boolean> future = database.createCollection("testCollection", null)
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		try {
			Client client = AppInstanceFactory.getClient();
			String ownerDid = "did:elastos:iqcpzTBTbi27exRoP27uXMLNM1r3w3UwaL";
			String providerAddress = "https://hive1.trinity-tech.io";
			client.createVault(ownerDid, providerAddress)
					.whenComplete((vault, throwable) -> {
						if (throwable == null) {
							database = vault.getDatabase();
						} else {
							try {
								database = client.getVault(ownerDid, providerAddress).join().getDatabase();
							} catch (Exception e) {
								throw new CompletionException(e);
							}
						}
					}).join();
		} catch (Exception e) {
			System.out.println("Vault already exist");
		}
	}
}
