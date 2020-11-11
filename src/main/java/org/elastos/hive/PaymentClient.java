package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PackageInfo;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.UsingPlan;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PaymentClient implements Payment {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	public PaymentClient(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}


	@Override
	public CompletableFuture<List<PricingPlan>> getAllPricingPlans() {
		return authHelper.checkValid()
				.thenCompose(result -> getAllPricingPlansImp());
	}

	private CompletableFuture<List<PricingPlan>> getAllPricingPlansImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<PackageInfo> response = this.connectionManager.getVaultApi()
						.getPackageInfo()
						.execute();
				authHelper.checkResponseCode(response);
				return response.body().pricingPlans();
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return authHelper.checkValid()
				.thenCompose(result -> getPricingPlansImp(planName));
	}

	private CompletableFuture<PricingPlan> getPricingPlansImp(String planName) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<PricingPlan> response = this.connectionManager.getVaultApi()
						.getPricingPlan(planName)
						.execute();
				authHelper.checkResponseCode(response);
				return response.body();
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> useTrial() {
		return authHelper.checkValid()
				.thenCompose(result -> useTrialImp());
	}

	private CompletableFuture<Boolean> useTrialImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.createFreeVault()
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<String> placeOrder(String priceName) {
		return authHelper.checkValid()
				.thenCompose(result -> placeOrderImp(priceName));
	}

	private CompletableFuture<String> placeOrderImp(String priceName) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("pricing_name", priceName);
				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.createOrder(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String orderId = ret.get("order_id").toString();
				return orderId;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> payOrder(String orderId, List<String> txids) {
		return authHelper.checkValid()
				.thenCompose(result -> payOrderImp(orderId, txids));
	}

	private CompletableFuture<Boolean> payOrderImp(String orderId, List<String> txids) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("order_id", orderId);
				map.put("pay_txids", txids);
				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.pay(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return authHelper.checkValid()
				.thenCompose(result -> getOrderImp(orderId));
	}

	private CompletableFuture<Order> getOrderImp(String orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response response = this.connectionManager.getVaultApi()
						.getOrderInfo(orderId)
						.execute();
				authHelper.checkResponseCode(response);
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String orderInfo = ret.get("order_info").toString();
				return Order.deserialize(orderInfo);
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<List<Order>> getAllOrders() {
		return authHelper.checkValid()
				.thenCompose(result -> getAllOrdersImp());
	}

	private CompletableFuture<List<Order>> getAllOrdersImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response response = this.connectionManager.getVaultApi()
						.getOrderList()
						.execute();
				authHelper.checkResponseCode(response);
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String orderInfo = ret.get("order_info_list").toString();
				List<Order> orders = ResponseHelper.getValue(orderInfo, new ArrayList<Order>().getClass());
				return orders;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<UsingPlan> getUsingPricePlan() {
		return authHelper.checkValid()
				.thenCompose(result -> getUsingPricePlanImp());
	}


	private CompletableFuture<UsingPlan> getUsingPricePlanImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response response = this.connectionManager.getVaultApi()
						.getServiceInfo()
						.execute();
				authHelper.checkResponseCode(response);
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String info = ret.get("vault_service_info").toString();
				return UsingPlan.deserialize(info);
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<String> getPaymentVersion() {
		return authHelper.checkValid()
				.thenCompose(result -> getPaymentVersionImp());
	}

	private CompletableFuture<String> getPaymentVersionImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response response = this.connectionManager.getVaultApi()
						.getPaymentVersion()
						.execute();
				authHelper.checkResponseCode(response);
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String version = ret.get("version").toString();
				return version;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	private RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}
}
