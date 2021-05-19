package org.elastos.hive.network;

import okhttp3.ResponseBody;
import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import org.elastos.hive.vault.files.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface CallAPI {

	// files
	String API_UPLOAD = "/files/upload";

	@GET("/api/v1/files/list/folder")
	Call<FilesListResponseBody> list(@Query("path") String filename);

	@POST("/api/v1/files/copy")
	Call<HiveResponseBody> copy(@Body FilesCopyRequestBody body);

	@POST("/api/v1/files/move")
	Call<HiveResponseBody> move(@Body FilesMoveRequestBody body);

	@POST("/api/v1/files/delete")
	Call<HiveResponseBody> delete(@Body FilesDeleteRequestBody body);

	@GET("/api/v1/files/properties")
	Call<FilesPropertiesResponseBody> properties(@Query("path") String filename);

	@GET("/api/v1/files/file/hash")
	Call<FilesHashResponseBody> hash(@Query("path") String filename);

	@GET("/api/v1/files/download")
	Call<ResponseBody> download(@Query("path") String filename);

	// database

	@POST("/api/v1/db/create_collection")
	Call<HiveResponseBody> createCollection(@Body CreateCollectionRequestBody body);

	@POST("/api/v1/db/delete_collection")
	Call<HiveResponseBody> deleteCollection(@Body DeleteCollectionRequestBody body);

	@POST("/api/v1/db/insert_one")
	Call<InsertDocResponseBody> insertOne(@Body InsertDocRequestBody body);

	@POST("/api/v1/db/insert_many")
	Call<InsertDocsResponseBody> insertMany(@Body InsertDocsRequestBody body);

	@POST("/api/v1/db/update_one")
	Call<UpdateDocResponseBody> updateOne(@Body UpdateDocRequestBody body);

	@POST("/api/v1/db/update_many")
	Call<UpdateDocResponseBody> updateMany(@Body UpdateDocRequestBody body);

	@POST("/api/v1/db/delete_one")
	Call<DeleteDocResponseBody> deleteOne(@Body DeleteDocRequestBody body);

	@POST("/api/v1/db/delete_many")
	Call<DeleteDocResponseBody> deleteMany(@Body DeleteDocRequestBody body);

	@POST("/api/v1/db/count_documents")
	Call<CountDocResponseBody> countDocs(@Body CountDocRequestBody body);

	@POST("/api/v1/db/find_one")
	Call<FindDocResponseBody> findOne(@Body FindDocRequestBody body);

	@POST("/api/v1/db/find_many")
	Call<FindDocsResponseBody> findMany(@Body FindDocsRequestBody body);

	// scripting
	String API_SCRIPT_UPLOAD = "/scripting/run_script_upload";

	@POST("/api/v1/scripting/set_script")
	Call<RegisterScriptResponseBody> registerScript(@Body RegisterScriptRequestBody body);

	@POST("/api/v1/scripting/run_script")
	Call<ResponseBody> callScript(@Body CallScriptRequestBody body);

	@GET("/api/v1/scripting/run_script_url/{targetDid}@{appDid}/{scriptName}")
	Call<ResponseBody> callScriptUrl(@Path("targetDid") String targetDid,
									 @Path("appDid") String appDid,
									 @Path("scriptName") String scriptName,
									 @Query("params") String params);

	@POST("/api/v1/scripting/run_script_download/{transaction_id}")
	Call<ResponseBody> callDownload(@Path("transaction_id") String transactionId);

	// backup

	@GET("/api/v1/backup/state")
	Call<BackupStateResponseBody> getState();

	@POST("/api/v1/backup/save_to_node")
	Call<HiveResponseBody> saveToNode(@Body BackupSaveRequestBody body);

	@POST("/api/v1/backup/restore_from_node")
	Call<HiveResponseBody> restoreFromNode(@Body BackupRestoreRequestBody body);

	@POST("/api/v1/backup/activate_to_vault")
	Call<HiveResponseBody> activeToVault(@Body EmptyRequestBody body);

	// payment

	@GET("/api/v1/payment/vault_package_info")
	Call<PaymentPackageResponseBody> getPackageInfo();

	@GET("/api/v1/payment/vault_pricing_plan")
	Call<PaymentPlanResponseBody> getPricingPlan(@Query("name") String name);

	@GET("/api/v1/payment/vault_backup_plan")
	Call<PaymentPlanResponseBody> getBackupPlan(@Query("name") String name);

	@POST("/api/v1/payment/create_vault_package_order")
	Call<PaymentCreateResponseBody> createOrder(@Body PaymentCreateRequestBody body);

	@POST("/api/v1/payment/pay_vault_package_order")
	Call<HiveResponseBody> payOrder(@Body PayOrderRequestBody body);

	@GET("/api/v1/payment/vault_package_order")
	Call<OrderInfoResponseBody> getOrderInfo(@Query("order_id") String orderId);

	@GET("/api/v1/payment/vault_package_order_list")
	Call<OrderListResponseBody> getOrderList();

	@GET("/api/v1/payment/version")
	Call<PaymentVersionResponseBody> getPaymentVersion();

	// vault

	@POST("/api/v1/service/vault/create")
	Call<VaultCreateResponseBody> createVault();

	@POST("/api/v1/service/vault/freeze")
	Call<HiveResponseBody> freeze();

	@POST("/api/v1/service/vault/unfreeze")
	Call<HiveResponseBody> unfreeze();

	@POST("/api/v1/service/vault/remove")
	Call<HiveResponseBody> removeVault();

	@GET("/api/v1/service/vault")
	Call<VaultInfoResponseBody> getVaultInfo();

	@POST("/api/v1/service/vault_backup/create")
	Call<VaultCreateResponseBody> createBackupVault();

	@GET("/api/v1/service/vault_backup")
	Call<VaultInfoResponseBody> getBackupVaultInfo();

	// pubsub

	@POST("/api/v1/pubsub/publish")
	Call<HiveResponseBody> publish(@Body PubsubRequestBody body);

	@POST("/api/v1/pubsub/remove")
	Call<HiveResponseBody> remove(@Body PubsubRequestBody body);

	@GET("/api/v1/pubsub/pub/channels")
	Call<PubsubChannelsResponseBody> getPublishedChannels();

	@GET("/api/v1/pubsub/sub/channels")
	Call<PubsubChannelsResponseBody> getSubscribedChannels();

	@POST("/api/v1/pubsub/subscribe")
	Call<HiveResponseBody> subscribe(@Body PubsubSubscribeRequestBody body);

	@POST("/api/v1/pubsub/unsubscribe")
	Call<HiveResponseBody> unsubscribe(@Body PubsubSubscribeRequestBody body);

	@POST("/api/v1/pubsub/push")
	Call<HiveResponseBody> push(@Body PushMessageRequestBody body);

	@POST("/api/v1/pubsub/pop")
	Call<PopMessageResponseBody> pop(@Body PopMessageRequestBody body);

}
