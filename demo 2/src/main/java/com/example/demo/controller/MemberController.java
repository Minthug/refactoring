package com.example.demo.controller;

import com.example.demo.config.utils.SortUtils;
import com.example.demo.dto.follow.FollowDto;
import com.example.demo.dto.follow.FollowPageResponseDto;
import com.example.demo.dto.member.MemberDto;
import com.example.demo.dto.member.MemberResponseDto;
import com.example.demo.dto.member.MemberUpdateDto;
import com.example.demo.service.FollowService;
import com.example.demo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final FollowService followService;
    private final SortUtils sortUtils;

    @GetMapping("")
    public ResponseEntity<List<MemberDto>> findAllMember() {
        List<MemberDto> members = memberService.findAllMember();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> findMember(@PathVariable(value = "id") Long id) {
        MemberDto member = memberService.findMember(id);
        return ResponseEntity.ok(member);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editMember(@PathVariable(value = "id") Long id,
                                                      @Valid @ModelAttribute MemberUpdateDto memberUpdateDto) {
        MemberResponseDto memberResponseDto = memberService.editMember(id, memberUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(memberResponseDto);
    }

    @DeleteMapping("/id}")
    public ResponseEntity<Void> deleteMember(@PathVariable(value = "id") Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<FollowDto> followUser(@PathVariable Long targetUserId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        FollowDto response = followService.follow(targetUserId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long targetUserId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        followService.unfollow(targetUserId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<FollowPageResponseDto> getFollowers(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "20") Integer size,
                                                              @RequestParam(defaultValue = "createdDate,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortUtils.createSortOrder(sort)));
        FollowPageResponseDto response = followService.getFollowers(id, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<FollowPageResponseDto> getFollowing(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "20") Integer size,
                                                              @RequestParam(defaultValue = "createdDate,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortUtils.createSortOrder(sort)));
        FollowPageResponseDto response = followService.getFollowing(id, pageable);
        return ResponseEntity.ok(response);
    }
}
