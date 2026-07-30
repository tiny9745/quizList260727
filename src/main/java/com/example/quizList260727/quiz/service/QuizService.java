package com.example.quizList260727.quiz.service;

import java.time.LocalDate;
import java.util.List;
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

@Service
public class QuizService {
	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionOptionRepository questionOptionRepository;

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
		// 1. 更新 Quiz 本體
		int updatedRows = quizRepository.updateQuiz(id, request.getTitle(), request.getDescription(),
				request.getStartDate(), request.getEndDate(), request.getIsPublished());
		// 找到得筆數
		if (updatedRows == 0) {
			throw new RuntimeException("Quiz not found with id: " + id);
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
	public void updatePublishQuiz(PublishRequest request) {
		int count = quizRepository.updatePublishStatus(request.getId(), request.getIsPublished());
		if (count == 0) {
			throw new RuntimeException("找不到問卷");
		}
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

}