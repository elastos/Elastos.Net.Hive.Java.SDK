package org.elastos.hive.didhelper;

import org.elastos.did.DIDDocument;
import org.elastos.hive.ApplicationContext;
import org.elastos.hive.Client;
import org.elastos.hive.Vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AppInstanceFactory {
	private static final String didCachePath = "didCache";
	private Vault vault;
	private PresentationInJWT presentationInJWT;
	private static boolean resolverDidSetup = false;

	static class ClientOptions {
		private String storePath;
		private String ownerDid;
		private String resolveUrl;
		private String provider;

		public static ClientOptions create() {
			return new ClientOptions();
		}

		public ClientOptions storePath(String storePath) {
			this.storePath = storePath;
			return this;
		}

		public ClientOptions ownerDid(String ownerDid) {
			this.ownerDid = ownerDid;
			return this;
		}

		public ClientOptions resolveUrl(String resolveUrl) {
			this.resolveUrl = resolveUrl;
			return this;
		}

		public ClientOptions provider(String provider) {
			this.provider = provider;
			return this;
		}
	}

	private void setUp(PresentationInJWT.AppOptions userDidOpt, PresentationInJWT.AppOptions appInstanceDidOpt, PresentationInJWT.BackupOptions backupOptions, ClientOptions userFactoryOpt) {
		try {
			presentationInJWT = new PresentationInJWT().init(userDidOpt, appInstanceDidOpt, backupOptions);
			if (!resolverDidSetup) {
				Client.setupResolver(userFactoryOpt.resolveUrl, this.didCachePath);
				resolverDidSetup = true;
			}

			Client client = Client.createInstance(new ApplicationContext() {
				@Override
				public String getLocalDataDir() {
					return userFactoryOpt.storePath;
				}

				@Override
				public DIDDocument getAppInstanceDocument() {
					return presentationInJWT.getDoc();
				}

				@Override
				public CompletableFuture<String> getAuthorization(String jwtToken) {
					return CompletableFuture.supplyAsync(() -> presentationInJWT.getAuthToken(jwtToken));
				}
			});

			client.createVault(userFactoryOpt.ownerDid, userFactoryOpt.provider).handleAsync((ret, throwable) -> {
				if (null != throwable) {
					System.err.println("Vault already existed");
				}
				if (throwable == null) {
					vault = ret;
				} else {
					try {
						vault = client.getVault(userFactoryOpt.ownerDid, userFactoryOpt.provider).get();
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}
				return vault;
			}).thenComposeAsync(vault ->
					client.createBackupService(vault)
							.handleAsync((vault1, throwable) -> {
								if (null != throwable)
									System.err.println("Backup Vault already existed");
								return (throwable == null);
							})).get();
		} catch (Exception e) {

		}
	}

	private AppInstanceFactory(PresentationInJWT.AppOptions userDidOpt, PresentationInJWT.AppOptions appInstanceDidOpt, PresentationInJWT.BackupOptions backupOptions, ClientOptions userFactoryOpt) {
		setUp(userDidOpt, appInstanceDidOpt, backupOptions, userFactoryOpt);
	}

	private static AppInstanceFactory initOptions(Config config) {
		PresentationInJWT.AppOptions userDidOpt = PresentationInJWT.AppOptions.create()
				.setName(config.getUserName())
				.setMnemonic(config.getUserMn())
				.setPhrasepass(config.getUserPhrasepass())
				.setStorepass(config.getUserStorepass());

		PresentationInJWT.AppOptions appInstanceDidOpt = PresentationInJWT.AppOptions.create()
				.setName(config.getAppName())
				.setMnemonic(config.getAppMn())
				.setPhrasepass(config.getAppPhrasepass())
				.setStorepass(config.getAppStorePass());

		PresentationInJWT.BackupOptions backupOptions = PresentationInJWT.BackupOptions.create()
				.targetDID(config.targetDID())
				.targetHost(config.targetHost());

		ClientOptions clientOptions = ClientOptions.create();
		clientOptions.provider(config.getProvider());
		clientOptions.resolveUrl(config.getResolverUrl());
		clientOptions.ownerDid(config.getOwnerDid());
		clientOptions.storePath(config.getStorePath());

		return new AppInstanceFactory(userDidOpt, appInstanceDidOpt, backupOptions, clientOptions);
	}


	public static AppInstanceFactory configSelector() {
		//TODO You can change this value to switch the test environment
		// default value: Type.PRODUCTION
		Type select = Type.PRODUCTION;
		return initConfig(select);
	}

	public static AppInstanceFactory initCrossConfig() {
		return initConfig(Type.CROSS);
	}

	public enum Type {
		CROSS,
		DEVELOPING,
		PRODUCTION,
		TESTING
	}

	private static AppInstanceFactory initConfig(Type type) {
		String path = null;
		switch (type) {
			case CROSS:
				path = "CrossVault.conf";
				break;
			case DEVELOPING:
				path = "Developing.conf";
				break;
			case PRODUCTION:
				path = "Production.conf";
				break;
			case TESTING:
				path = "Testing.conf";
				break;
		}

		return initOptions(ConfigHelper.getConfigInfo(path));
	}

	public Vault getVault() {
		return this.vault;
	}

	public String getBackupVc(String serviceDID) {
		return this.presentationInJWT.getBackupVc(serviceDID);
	}


	public static Client getClientWithEasyAuth() {
		try {
			Config config = ConfigHelper.getConfigInfo("Production.conf");
			if (!resolverDidSetup) {
				Client.setupResolver(config.getResolverUrl(), didCachePath);
				resolverDidSetup = true;
			}
			return VaultAuthHelper.createInstance(config.getUserMn(),
					config.getAppMn(),
					config.getStorePath())
					.getClientWithAuth().join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}