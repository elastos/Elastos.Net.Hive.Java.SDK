package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class OneDriveClient extends Client {
	private static Client clientInstance;

	private final AuthHelper authHelper;
	private volatile Client.Info clientInfo;

	private OneDriveClient(OneDriveParameter parameter) {
		this.authHelper = new OneDriveAuthHelper(parameter.getAuthEntry());
	}

	public static Client createInstance(OneDriveParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new OneDriveClient(parameter);
		}
		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		if (clientInfo == null)
			return null;

		return clientInfo.getUserId();
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		CompletableFuture<AuthToken> future = authHelper.loginAsync(authenticator);

		try {
			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public synchronized void logout() throws HiveException {
		CompletableFuture<Status> future = authHelper.logoutAsync();

		try {
			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public Client.Info getLastInfo() {
		return clientInfo;
	}

	@Override
	public CompletableFuture<Client.Info> getInfo() {
		return getInfo(new NullCallback<Client.Info>());
	}

	@Override
	public CompletableFuture<Client.Info> getInfo(Callback<Client.Info> callback) {
		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();

		if (callback == null)
			callback = new NullCallback<Client.Info>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetClientInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		CompletableFuture<Drive> future = new CompletableFuture<Drive>();

		if (callback == null)
			callback = new NullCallback<Drive>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDriveCallback(future, callback));

		return future;
	}

	private class GetClientInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Client.Info> future;
		private final Callback<Client.Info> callback;

		GetClientInfoCallback(CompletableFuture<Client.Info> future,
			Callback<Client.Info> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			//TODO
			JSONObject ownerObject = (JSONObject) response.getBody().getObject().get("owner");
			JSONObject userObject = (JSONObject) ownerObject.get("user");
			Client.Info info;

			info = new Client.Info(userObject.getString("id"));
			info.setDisplayName(userObject.getString("displayName"));

			clientInfo = info;

			this.callback.onSuccess(info);
			future.complete(clientInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class GetDriveCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Drive> future;
		private final Callback<Drive> callback;

		GetDriveCallback(CompletableFuture<Drive> future, Callback<Drive> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			Drive.Info info = new Drive.Info(jsonObject.getString("id"));
			OneDriveDrive drive = new OneDriveDrive(info, authHelper);
			this.callback.onSuccess(drive);
			future.complete(drive);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
