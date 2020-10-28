package org.elastos.hive.payment.pkg;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class PackageInfo extends Result<PackageInfo> {
	@JsonProperty("Trial")
	private Trial trial;
	@JsonProperty("vaultPackages")
	private List<Package> packages;
	@JsonProperty("paymentSettings")
	private PaySetting paySetting;

	public Trial trial() {
		return trial;
	}

	public List<Package> packages() {
		return packages;
	}

	public PaySetting paySetting() {
		return paySetting;
	}

	public static PackageInfo deserialize(String content) {
		return deserialize(content, PackageInfo.class);
	}
}