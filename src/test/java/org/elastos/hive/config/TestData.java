package org.elastos.hive.config;

import org.elastos.did.DIDDocument;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.ApplicationContext;
import org.elastos.hive.Backup;
import org.elastos.hive.BackupAuthenticationHandler;
import org.elastos.hive.Client;
import org.elastos.hive.Management;
import org.elastos.hive.Payment;
import org.elastos.hive.Utils;
import org.elastos.hive.Vault;
import org.elastos.hive.didhelper.DApp;
import org.elastos.hive.didhelper.DIDApp;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.JwtUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class TestData {

	private DApp appInstanceDid;

	private DIDApp userDid = null;

	private Client client;

	private ClientConfig clientConfig;
	private NodeConfig nodeConfig;

	private ApplicationContext applicationContext;

	public static TestData getInstance() throws HiveException, DIDException {
		if(instance == null) {
			instance = new TestData();
		}
		return instance;
	}

	private static TestData instance;
	private TestData() throws HiveException, DIDException {
		init();
	}

	public void init() throws HiveException, DIDException {

		//TODO set environment config
		String fileName = null;
		switch (EnvironmentType.DEVELOPING) {
			case DEVELOPING:
				fileName = "Developing.conf";
				break;
			case PRODUCTION:
				fileName = "Production.conf";
				break;
			case LOCAL:
				fileName = "Local.conf";
				break;
		}

		String configJson = Utils.getConfigure(fileName);
		clientConfig = ClientConfig.deserialize(configJson);

		Client.setupResolver(clientConfig.resolverUrl(), "data/didCache");

		DummyAdapter adapter = new DummyAdapter();
		ApplicationConfig applicationConfig = clientConfig.applicationConfig();
		appInstanceDid = new DApp(applicationConfig.name(), applicationConfig.mnemonic(), adapter, applicationConfig.passPhrase(), applicationConfig.storepass());

		UserConfig userConfig = clientConfig.userConfig();
		userDid = new DIDApp(userConfig.name(), userConfig.mnemonic(), adapter, userConfig.passPhrase(), userConfig.storePass());

		nodeConfig = clientConfig.nodeConfig();

		//初始化Application Context
		applicationContext = new ApplicationContext() {
			@Override
			public String getLocalDataDir() {
				return System.getProperty("user.dir") + File.separator + "data/store" + File.separator + nodeConfig.storePath();
			}

			@Override
			public DIDDocument getAppInstanceDocument() {
				try {
					return appInstanceDid.getDocument();
				} catch (DIDException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public CompletableFuture<String> getAuthorization(String jwtToken) {
				return CompletableFuture.supplyAsync(() -> signAuthorization(jwtToken));
			}
		};

		client = Client.createInstance(applicationContext);
	}

	public String signAuthorization(String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			String iss = claims.getIssuer();
			String nonce = (String) claims.get("nonce");

			VerifiableCredential vc = userDid.issueDiplomaFor(appInstanceDid);

			VerifiablePresentation vp = appInstanceDid.createPresentation(vc, iss, nonce);

			String token = appInstanceDid.createToken(vp, iss);
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getBackupVc(String sourceDID) {
		try {
			VerifiableCredential vc = userDid.issueBackupDiplomaFor(sourceDID,
					nodeConfig.targetHost(), nodeConfig.targetDid());
			return vc.toString();
		} catch (DIDException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Client getClient() {
		return this.client;
	}

	public CompletableFuture<Management> getManagement() {
		return this.client.getManager(nodeConfig.ownerDid(), nodeConfig.provider());
	}

	public CompletableFuture<Payment> getPayment() {
		return getManagement().thenApplyAsync(management -> management.getPayment());
	}

	public CompletableFuture<Vault> getVault() {
		return this.client.getVault(nodeConfig.ownerDid(), nodeConfig.provider());
	}

	public CompletableFuture<Vault> createTargetVault() {
		return this.client.getManager(nodeConfig.targetDid(), nodeConfig.targetHost())
				.thenComposeAsync(management -> management.createVault());
	}

	public CompletableFuture<Backup> getTargetBackup() {
		return this.client.getBackup(nodeConfig.targetDid(), nodeConfig.targetHost());
	}

	public CompletableFuture<Backup> getBackup() {
		return this.client.getBackup(nodeConfig.ownerDid(), nodeConfig.provider());
	}

	public BackupAuthenticationHandler getBackupAuthenticationHandler() {
		return new BackupAuthenticationHandler() {
			@Override
			public CompletableFuture<String> getAuthorization(String serviceDid) {
				return CompletableFuture.supplyAsync(() ->
						getBackupVc(serviceDid));
			}

			@Override
			public String getTargetHost() {
				return nodeConfig.targetHost();
			}

			@Override
			public String getTargetDid() {
				return nodeConfig.targetDid();
			}
		};
	}

	private enum EnvironmentType {
		DEVELOPING,
		PRODUCTION,
		LOCAL
	}
}