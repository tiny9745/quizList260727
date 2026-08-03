package com.example.quizList260727.quiz.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.quizList260727.common.exception.ResourceNotFoundException;
import com.example.quizList260727.quiz.dto.request.PublishRequest;
import com.example.quizList260727.quiz.dto.request.QuestionOptionRequest;
import com.example.quizList260727.quiz.dto.request.QuestionRequest;
import com.example.quizList260727.quiz.dto.request.QuizRequest;
import com.example.quizList260727.quiz.dto.response.QuestionOptionResponse;
import com.example.quizList260727.quiz.dto.response.QuestionResponse;
import com.example.quizList260727.quiz.dto.response.QuizResponse;
import com.example.quizList260727.quiz.entity.Question;
import com.example.quizList260727.quiz.entity.QuestionOption;
import com.example.quizList260727.quiz.entity.Quiz;
import com.example.quizList260727.quiz.enums.QuestionType;
import com.example.quizList260727.quiz.repository.QuestionOptionRepository;
import com.example.quizList260727.quiz.repository.QuestionRepository;
import com.example.quizList260727.quiz.repository.QuizRepository;
import com.example.quizList260727.reply.dto.FillQuizRequest;
import com.example.quizList260727.reply.dto.QuestionAnswerRequest;
import com.example.quizList260727.reply.repository.QuizResponseRepository;
import com.example.quizList260727.reply.repository.ResponseDetailRepository;

@Service
public class QuizService {
	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionOptionRepository questionOptionRepository;

	@Autowired
	private QuizResponseRepository quizResponseRepository;

	@Autowired
	private ResponseDetailRepository responseDetailRepository;

	/**
	 * 1. 新增問卷 (不需回傳完整問卷，成功即結束)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void createQuiz(QuizRequest request) {
		validateQuizTime(request);
		// 1. 新增 Quiz
		quizRepository.insertQuiz( //
				request.getTitle(), //
				request.getDescription(), //
				request.getStartDate(), //
				request.getEndDate(), //
				request.getIsPublished());
		Long newQuizId = quizRepository.getLastInsertedId();
		validateQuestions(request.getQuestions());
		// 2. 寫入所有問題與選項
		saveQuestionsAndOptions(newQuizId, request.getQuestions());
	}

	/**
	 * 2. 更新問卷 (Request 包含問卷以及對應的所有問題與選項)
	 */
	@Transactional
	public void updateQuiz(QuizRequest request) {
		validateQuizTime(request);
		// 更新得有quiz_id
		Long id = request.getId();
		if (id == null || id <= 0) {
			throw new RuntimeException("Quiz not found with id: " + id);
		}

		// 攔截：此問卷若已發布，禁止修改題目與選項。
		// 用 quizRepository.findQuizById(id) 查出目前的 Quiz，檢查 isPublished 欄位；
		// 同時這次查詢也順便取代了原本「id 是否存在」的檢查，找不到就直接丟 ResourceNotFoundException。
		Quiz quiz = quizRepository.findQuizById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
		if (Boolean.TRUE.equals(quiz.getIsPublished())) {
			throw new IllegalStateException("Quiz is published, edit prohibited!");
		}

		// 1. 更新 Quiz 本體
		int updatedRows = quizRepository.updateQuiz(id, request.getTitle(), request.getDescription(),
				request.getStartDate(), request.getEndDate(), request.getIsPublished());
		// 理論上不會發生（上面 findQuizById 剛查到這筆資料），
		// 保留此檢查是為了防範極端情況：兩次查詢之間，該問卷剛好被其他請求刪除（race condition），
		// 避免在沒有任何提示的情況下，讓呼叫端誤以為更新成功、但實際上什麼都沒被更新到。
		if (updatedRows == 0) {
			throw new ResourceNotFoundException("Quiz not found with id: " + id);
		}
		/* 先刪除舊的，再新增更新的 */
		// 2. 刪除舊有選項與問題
		questionOptionRepository.deleteByQuizId(id);
		questionRepository.deleteByQuizId(id);
		// 3. 重新新增問題與選項
		saveQuestionsAndOptions(id, request.getQuestions());
	}

	/**
	 * 3. 取得所有的問卷列表 (僅問卷，沒有問題內容以及選項)
	 */
	@Transactional(readOnly = true)
	public List<QuizResponse> getAllQuizzesWithoutQuestions() {
		List<Quiz> quizzes = quizRepository.findAllQuizzes();
		return quizzes.stream().map(quiz -> {
			QuizResponse dto = new QuizResponse();
			dto.setId(quiz.getId());
			dto.setTitle(quiz.getTitle());
			dto.setDescription(quiz.getDescription());
			dto.setStartDate(quiz.getStartDate());
			dto.setEndDate(quiz.getEndDate());
			dto.setIsPublished(quiz.getIsPublished());
			dto.setQuestions(null); // 不回傳問題與選項
			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * 4. 根據問卷 id 取得對應的問題內容以及選項
	 */
	@Transactional(readOnly = true)
	public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
		quizRepository.findQuizById(quizId)
				.orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
		List<Question> questions = questionRepository.findByQuizId(quizId);
		return questions.stream().map(this::convertToQuestionDto).collect(Collectors.toList());
	}

	/**
	 * 5. 更新發布狀態
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updatePublishQuiz(Long id, PublishRequest request) {
		int count = quizRepository.updatePublishStatus(id, request.getIsPublished());
		if (count == 0) {
			throw new RuntimeException("找不到問卷");
		}
	}

	/**
	 * 6. 批次刪除問卷
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteQuizzes(List<Long> quizIds) {
		// 1. 刪除這些問卷對應的所有選項
		questionOptionRepository.deleteByQuizIds(quizIds);
		// 2. 刪除這些問卷對應的所有問題
		questionRepository.deleteByQuizIds(quizIds);
		// 3. 刪除問卷本體
		int deletedCount = quizRepository.deleteByQuizIds(quizIds);
		if (deletedCount == 0) {
			throw new RuntimeException("No quizzes were found to delete!!");
		}
	}

	/**
	 * 7. 填寫並提交問卷
	 */
	@Transactional(rollbackFor = Exception.class)
	public void fillQuiz(FillQuizRequest request) {
		// 1. 檢查問卷是否存在與發布狀態

		/*
		 * orElseThrow 是 Optional 提供的方法，意思是: 如果容器裡面有資料，就把 Quiz 物件拿出來；
		 * 如果容器裡面是空的(沒找到資料)，就執行括號裡面的程式碼，拋出例外
		 */
		Quiz quiz = quizRepository.findQuizById(request.getQuizId())
				.orElseThrow(() -> new RuntimeException("Quiz not found with id: " + request.getQuizId()));
		/**
		 * 安全的布林值比較: 是種防止 NullPointerException 的寫法
		 * 
		 * 如果傳入 true --> 回傳 true； <br>
		 * 如果傳入 false --> 回傳 false；<br>
		 * 如果傳入 null --> 不會報錯，直接回傳 false
		 */
		if (!Boolean.TRUE.equals(quiz.getIsPublished())) {
			throw new IllegalArgumentException("This quiz is not published yet!!");
		}
		// 2. 檢查問卷可填寫時間(日期)
		LocalDate now = LocalDate.now();
		if (now.isBefore(quiz.getStartDate())) {
			throw new IllegalArgumentException("This quiz has not started yet!!");
		}
		if (now.isAfter(quiz.getEndDate())) {
			throw new IllegalArgumentException("This quiz has already ended!!");
		}
		// 3. 檢查必填欄位與答案合法性
		List<Question> questions = questionRepository.findByQuizId(quiz.getId());
		validateFillAnswers(questions, request.getAnswers());
		// 4. 寫入 quiz_response (主表)
		// 註：若此 Email 在此 Quiz 已填過，會觸發 uk_quiz_user 唯一約束並拋出 Database Exception
		quizResponseRepository.insertQuizResponse(request.getQuizId(), request.getUserEmail());
		Long responseId = quizResponseRepository.getLastInsertedId();
		// 5. 寫入 response_detail (明細表)
		saveResponseDetails(responseId, request.getAnswers());
	}

	// ==================== 私有輔助方法 ====================
	/**
	 * 驗證問卷題目數量
	 *
	 * 商業規則： 一份問卷至少需要兩題。
	 */
	private void validateQuestions(List<QuestionRequest> questionRequests) {
		int questionCount = questionRequests.size();
		if (questionCount < 2) {
			throw new IllegalArgumentException("Quiz requires at least two questions. Current count: " + questionCount);
		}
	}

	private void saveQuestionsAndOptions(Long quizId, List<QuestionRequest> questionRequests) {
		for (QuestionRequest qReq : questionRequests) {
			validateQuestionOptions(qReq);
			questionRepository.insertQuestion( //
					quizId, //
					qReq.getQuestionNum(), //
					qReq.getTitle(), //
					qReq.getType().name(), //
					qReq.getIsRequired());

			// 取得最新的流水號
			Long newQuestionId = questionRepository.getLastInsertedId();

			// 新增選項:前提是qReq.getOptions()不為null與空
			// 等同於排除'問答(簡答)題'
			if (!CollectionUtils.isEmpty(qReq.getOptions())) {
				for (QuestionOptionRequest oReq : qReq.getOptions()) {
					questionOptionRepository.insertOption( //
							newQuestionId, //
							oReq.getOptionCode(), //
							oReq.getOptionText());
				}
			}
		}
	}

	/**
	 * 回傳QuestionResponse格式的單一問題
	 */
	private QuestionResponse convertToQuestionDto(Question question) {
		QuestionResponse qDto = new QuestionResponse();
		qDto.setId(question.getId());
		qDto.setQuestionNum(question.getQuestionNum());
		qDto.setTitle(question.getTitle());
		qDto.setType(question.getType());
		qDto.setIsRequired(question.getIsRequired());
		List<QuestionOption> options = questionOptionRepository.findByQuestionId(question.getId());
		List<QuestionOptionResponse> oDtos = options.stream().map(o -> {
			QuestionOptionResponse oDto = new QuestionOptionResponse();
			oDto.setId(o.getId());
			oDto.setOptionCode(o.getOptionCode());
			oDto.setOptionText(o.getOptionText());
			return oDto;
		}).collect(Collectors.toList());
		qDto.setOptions(oDtos);
		return qDto;
	}

	/**
	 * 日期檢查
	 */
	private void validateQuizTime(QuizRequest request) {
		// 檢查: 1.開始日期不得大於結束時間 2.開始日期不得小於今日
		if (request.getEndDate().isBefore(request.getStartDate()) || request.getStartDate().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("End date cannot be earlier than start date!!");
		}
	}

	private void validateQuestionOptions(QuestionRequest qReq) {
		// 問答題
		if (qReq.getType() == QuestionType.TEXT) {
			return;
		}

		// 單選、多選
		if (qReq.getOptions() == null || qReq.getOptions().size() < 2) {
			throw new IllegalArgumentException(
					"Question #" + qReq.getQuestionNum() + " requires at least two options.");
		}
	}

	// 回覆
	private void validateFillAnswers(List<Question> questions, List<QuestionAnswerRequest> answers) {
		/**
		 * 轉成 Map 的原因: 
		 * 
		 * 1. 大幅提升執行效率(降低時間複雜度) <br>
		 * 2. 直接用 Key 查詢 <br>
		 * 3. 自動過濾前端傳遞的「重複答案」: (k1, k2) -> k1 表示 有重複 key 時，保留舊的 key(k1) 對應的值 
		 */
		Map<Long, QuestionAnswerRequest> answerMap = answers.stream()
				.collect(Collectors.toMap(QuestionAnswerRequest::getQuestionId, a -> a, (k1, k2) -> k1));
		// 以資料庫的標準題目為基準，逐一檢查使用者的答案是否合規
		for (Question question : questions) {
			QuestionAnswerRequest userAns = answerMap.get(question.getId());
			// 檢查必填題
			if (Boolean.TRUE.equals(question.getIsRequired())) {
				if (userAns == null || isAnswerEmpty(question.getType(), userAns)) {
					throw new IllegalArgumentException("Question #" + question.getQuestionNum() + " is required!!");
				}
			}
			// 如果使用者有傳遞答案，且為選擇題，進行選項合法性檢查
			if (userAns != null
					&& (question.getType() == QuestionType.SINGLE || question.getType() == QuestionType.MULTI)) {
				List<Long> submittedOptionIds = userAns.getOptionIds();
				if (submittedOptionIds != null && !submittedOptionIds.isEmpty()) {
					// 2. 自動去重（防止重複傳入相同的 option_id，例如 [10, 10, 11, 12, 13]）
					Set<Long> uniqueSubmittedIds = new HashSet<>(submittedOptionIds);
					// 3. 查出資料庫中該題目「真正擁有」的所有 Option IDs
					List<QuestionOption> validOptions = questionOptionRepository.findByQuestionId(question.getId());
					Set<Long> validOptionIds = validOptions.stream().map(QuestionOption::getId)
							.collect(Collectors.toSet());
					// 4. 數量與範圍驗證：
					// A. 檢查傳入的選項數量是否超過資料庫該題的選項總數 (例如選項只有 4 個，答案卻有 5 個)
					if (uniqueSubmittedIds.size() > validOptionIds.size()) {
						throw new IllegalArgumentException("Question #" + question.getQuestionNum()
								+ " has invalid/excessive option selections!!");
					}
					// B. 檢查傳入的每一個 option_id 是否真的屬於該題目 (防止偷渡其他題目的 option_id)
					for (Long optionId : uniqueSubmittedIds) {
						if (!validOptionIds.contains(optionId)) {
							throw new IllegalArgumentException("Question #" + question.getQuestionNum()
									+ " contains an invalid option ID: " + optionId);
						}
					}
					// 5. 單選題規則檢查 (不可選超過 1 個不同選項)
					if (question.getType() == QuestionType.SINGLE && uniqueSubmittedIds.size() > 1) {
						throw new IllegalArgumentException(
								"Question #" + question.getQuestionNum() + " is a single-choice question!!");
					}
				}
			}
		}
	}

	private boolean isAnswerEmpty(QuestionType type, QuestionAnswerRequest ans) {
		if (type == QuestionType.SINGLE || type == QuestionType.MULTI) {
			return ans.getOptionIds() == null || ans.getOptionIds().isEmpty();
		} else if (type == QuestionType.TEXT) {
			return ans.getAnswerText() == null || ans.getAnswerText().isBlank();
		}
		return true;
	}

	private void saveResponseDetails(Long responseId, List<QuestionAnswerRequest> answers) {
		for (QuestionAnswerRequest ans : answers) {
			if (ans.getOptionIds() != null && !ans.getOptionIds().isEmpty()) {
				for (Long optionId : ans.getOptionIds()) {
					responseDetailRepository.insertDetail(responseId, ans.getQuestionId(), optionId, null);
				}
			} else if (ans.getAnswerText() != null && !ans.getAnswerText().isBlank()) {
				responseDetailRepository.insertDetail(responseId, ans.getQuestionId(), null, ans.getAnswerText());
			}
		}
	}

}