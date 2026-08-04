package com.example.quizList260727.quiz.dto.response;

/* 選項與勾選狀態 */
public class OptionDetail {
	// 選項 ID
	private Long optionId;
	// 選項代碼 (如: "A", "B", "C")
	private String optionCode;
	// 選項文字內容 (如: "滿意", "不滿意")
	private String optionText;
	// 填答者是否有勾選此選項
	private Boolean isSelected;

	public OptionDetail() {
		super();
	}

	public OptionDetail(Long optionId, String optionCode, String optionText, Boolean isSelected) {
		super();
		this.optionId = optionId;
		this.optionCode = optionCode;
		this.optionText = optionText;
		this.isSelected = isSelected;
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

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
}
