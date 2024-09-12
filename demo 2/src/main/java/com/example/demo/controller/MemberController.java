package com.example.demo.controller;

import com.example.demo.dto.member.MemberDto;
import com.example.demo.dto.member.MemberResponseDto;
import com.example.demo.dto.member.MemberUpdateDto;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberService memberService;

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
}
