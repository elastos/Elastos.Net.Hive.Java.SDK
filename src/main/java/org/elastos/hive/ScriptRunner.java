package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.ResponseBody;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.network.ScriptingApi;
import org.elastos.hive.network.model.ScriptContext;
import org.elastos.hive.network.request.CallScriptRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import retrofit2.Response;

import java.util.concurrent.CompletionException;

public class ScriptRunner extends ServiceEndpoint {
	private String targetDid;
	private String targetAppDid;
	private ConnectionManager connectionManager;

	public ScriptRunner(AppContext context, String providerAddress, String userDid, String targetDid, String targetAppDid) {
		super(context, providerAddress, userDid);
		this.targetDid = targetDid;
		this.targetAppDid = targetAppDid;
		this.connectionManager = getConnectionManager();
	}

	public <T> T callScript(String name, JsonNode params, String appDid, Class<T> resultType) {
		try {
			Response<ResponseBody> response = this.connectionManager.getScriptingApi()
					.callScript(new CallScriptRequestBody()
							.setName(name)
							.setContext(new ScriptContext()
									.setTargetDid(this.targetDid)
									.setTargetAppDid(appDid)).setParams(params))
					.execute();
			return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(response), resultType);
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	public <T> T callScriptUrl(String name, String params, String appDid, Class<T> resultType) {
		try {
			Response<ResponseBody> response = this.connectionManager.getScriptingApi()
					.callScriptUrl(this.targetDid, appDid, name, params)
					.execute();
			return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(response), resultType);
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	public <T> T downloadFile(String transactionId, Class<T> resultType) {
		if (transactionId == null)
			throw new CompletionException(new HiveSdkException("Invalid parameter transactionId."));

		try {
			Response<ResponseBody> response = this.connectionManager.getScriptingApi()
					.callDownload(transactionId)
					.execute();
			return HiveResponseBody.getResponseStream(response, resultType);
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	public <T> T uploadFile(String transactionId, Class<T> resultType) {
		if (transactionId == null)
			throw new CompletionException(new HiveSdkException("Invalid parameter transactionId."));

		try {
			return HiveResponseBody.getRequestStream(this.connectionManager.openConnection(ScriptingApi.API_SCRIPT_UPLOAD + "/" + transactionId), resultType);
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}
}
