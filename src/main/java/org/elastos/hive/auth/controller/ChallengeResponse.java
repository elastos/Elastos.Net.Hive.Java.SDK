package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;

class ChallengeResponse {
	@SerializedName("challenge_response")
	private final String challengeResponse;

	ChallengeResponse(String challengeResponse) {
		this.challengeResponse = challengeResponse;
	}
}
