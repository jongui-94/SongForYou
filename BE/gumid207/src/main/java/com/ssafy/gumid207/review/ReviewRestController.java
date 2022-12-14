package com.ssafy.gumid207.review;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.gumid207.dto.ReviewDto;
import com.ssafy.gumid207.dto.ReviewUploadDto;
import com.ssafy.gumid207.dto.UserDto;
import com.ssafy.gumid207.entity.User;
import com.ssafy.gumid207.jwt.SecurityUtil;
import com.ssafy.gumid207.res.ResponseFrame;
import com.ssafy.gumid207.user.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "노래방 리뷰 컨트롤러")
public class ReviewRestController {
	private final UserRepository userRepo;

	public UserDto getLoginUser() {
		User user = null;
		try {
			user = userRepo.findById(SecurityUtil.getCurrentMemberId()).get();
		} catch (Exception e) {
			user = new User();
			user.setBirthday(1996);
			user.setGender("male");
			user.setId("anfidthtn");
			user.setNickName("무량소수");
			user.setPoint(1000);
			user.setPass("1234");
			user.setRegTime(LocalDateTime.now());
		}

		return UserDto.of(user);
	}

	private final ObjectMapper objectMapper;

	private final ReviewService reviewServ;

	@ApiParam(value = "노래방 리뷰 등록")
	@PostMapping(value = "/upload")
	public ResponseEntity<?> uploadReview(
			@RequestPart(name = "reviewUploadDto", required = true) String reviewUploadString,
			@RequestPart(required = true) MultipartFile imgFile) throws Exception {
		ReviewUploadDto reviewUploadDto = objectMapper.readValue(reviewUploadString, ReviewUploadDto.class);
		reviewUploadDto.validate();
		UserDto userDto = getLoginUser();
		ReviewDto reviewDto = reviewServ.uploadReview(userDto.getUserSeq(), reviewUploadDto, imgFile);
		return new ResponseEntity<>(new ResponseFrame<>(true, reviewDto, 1, "리뷰 등록에 성공했습니다."), HttpStatus.OK);
	}

	@ApiParam(value = "노래방 리뷰 조회")
	@GetMapping(value = "/list")
	public ResponseEntity<?> getReviewList(@RequestParam(required = true) String karaokeName,
			@RequestParam(required = true) String karaokeAddress) throws Exception {
		List<ReviewDto> reviewDtoList = reviewServ.getReviewList(karaokeName, karaokeAddress);
		return new ResponseEntity<>(new ResponseFrame<>(true, reviewDtoList, reviewDtoList.size(), "리뷰 목록을 반환합니다."), HttpStatus.OK);
	}

}