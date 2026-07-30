package com.example.quizList260727.common.exception;

/**
 * 表示查詢的資源不存在（例如依 id 查詢問卷但查無資料）。
 *
 * 刻意獨立出這個類別，而非直接丟出 RuntimeException，
 * 是為了讓 GlobalExceptionHandler 能明確區分「預期中的查無資料」
 * 與「未預期的系統例外」，前者可安全回傳訊息給前端並回404，
 * 後者則一律回傳通用訊息，避免細節外洩。
 */
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}