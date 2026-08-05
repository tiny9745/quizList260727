package com.example.quizList260727.quiz.dto.response;

import java.math.BigDecimal;

/* 選項統計：得票數與百分比 */
public class OptionStatDto {
	private Long optionId;
	private String optionCode;
	private String optionText;
	private Long selectedCount; // 選擇該選項的人數
	private BigDecimal percentage; // 得票百分比 (例如: 66.67)

	public OptionStatDto() {
		super();
	}

	public OptionStatDto(Long optionId, String optionCode, String optionText, Long selectedCount,
			BigDecimal percentage) {
		super();
		this.optionId = optionId;
		this.optionCode = optionCode;
		this.optionText = optionText;
		this.selectedCount = selectedCount;
		this.percentage = percentage;
	}

	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	public String getOptionCode() {
		return optionCode;
	}

	public void setOptionCode(String optionCode) {
		this.optionCode = optionCode;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public Long getSelectedCount() {
		return selectedCount;
	}

	public void setSelectedCount(Long selectedCount) {
		this.selectedCount = selectedCount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}
}
