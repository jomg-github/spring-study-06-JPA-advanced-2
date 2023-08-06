package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemverV1(@RequestBody @Valid Member member) {
        Long id = memberService.signUp(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemverV2(@RequestBody @Valid CreataeMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.signUp(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMember(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> findMemberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public ApiResult<?> findMemberV2() {
        List<Member> members = memberService.findMembers();
        List<MemberDTO> memberDTOS = members.stream()
                .map(member -> new MemberDTO(member.getId(), member.getName()))
                .toList();

        return new ApiResult<>(memberDTOS.size(), memberDTOS);
    }

    @Data
    @AllArgsConstructor
    private static class CreateMemberResponse {
        private Long id;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CreataeMemberRequest {
        @NotBlank
        private String name;
    }

    @Data
    @AllArgsConstructor
    private static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    private class ApiResult<T> {
        private Integer count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    private class MemberDTO {
        private Long id;
        private String name;
    }
}
